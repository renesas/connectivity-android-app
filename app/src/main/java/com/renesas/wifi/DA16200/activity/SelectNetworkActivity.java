package com.renesas.wifi.DA16200.activity;


import static org.bouncycastle.util.test.SimpleTestResult.failed;

import android.annotation.SuppressLint;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Insets;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.os.Message;
import android.os.SystemClock;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.afollestad.materialdialogs.Theme;
import com.renesas.wifi.DA16200.adapter.ap.APListViewAdapter;
import com.renesas.wifi.DA16200.adapter.ap.APListViewAdapter_1;
import com.renesas.wifi.DA16200.adapter.ap.APRowItem;
import com.renesas.wifi.DA16200.adapter.ap.APRowItem_1;
import com.renesas.wifi.DA16200.dialog.EnterpriseDialog;
import com.renesas.wifi.DA16200.dialog.InputPasswordDialog;

import com.renesas.wifi.DA16600.activity.DeviceControlActivity;
import com.renesas.wifi.R;
import com.renesas.wifi.activity.BaseActivity;
import com.renesas.wifi.activity.MainActivity;
import com.renesas.wifi.DA16200.dialog.AddWiFiDialog;

import com.renesas.wifi.util.CustomToast;
import com.renesas.wifi.util.FButton;

import com.renesas.wifi.util.MyLog;

import com.renesas.wifi.util.StaticDataSave;
import com.renesas.wifi.util.WIFIUtil;
import com.koushikdutta.async.AsyncSSLSocket;
import com.koushikdutta.async.AsyncSSLSocketWrapper;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.ConnectCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.WritableCallback;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiRemove.RemoveErrorCode;
import com.thanosfisherman.wifiutils.wifiRemove.RemoveSuccessListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SelectNetworkActivity extends BaseActivity implements View.OnClickListener {

    public Context mContext;

    //handler
    private NetworkHandler mHandler;

    private static WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;
    private ConnectivityManager.NetworkCallback mNetworkCallback;

    private WifiReceiver mReceiver;
    //[[add in v2.3.19
    private WifiProvisioningReceiver wifiProvisioningReceiver;
    SSLParameters sslParameters;


    //UI resources
    private Switch sw_tls;
    private TextView tv_tls;
    private ImageView iv_tls;
    private ImageView iv_back;
    private Button btn_network_scan;
    private FButton iv_rescan;
    private FButton btn_hiddenWiFi;
    private TextView tv_mode;
    private LinearLayout ll_socketConnecting;
    //[[change in v2.4.11
    //private com.wang.avi.AVLoadingIndicatorView socketConnecting;
    private ProgressBar socketConnecting;
    //]]
    private LinearLayout ll_progressScanning;
    //[[change in v2.4.11
    //private com.wang.avi.AVLoadingIndicatorView progressScanning;
    private ProgressBar progressScanning;
    //]]
    private LinearLayout ll_switchingSocket;
    //[[change in v2.4.11
    //private com.wang.avi.AVLoadingIndicatorView progressSwitching;
    private ProgressBar progressSwitching;
    //]]
    private TextView tv_noList;
    private MaterialDialog inputPasswordDialog = null;
    private MaterialDialog successDialog = null;
    private MaterialDialog socketConnectTimeoutDialog = null;
    private MaterialDialog receiveAplistTimeoutDialog = null;
    //[[add in v2.3.17 for AWS fleet provisioning
    private ProgressDialog registeringDialog = null;
    //]]

    //[[add in v2.3.19 for concurrent mode
    private StringBuffer sb = new StringBuffer();
    private MaterialDialog reconnectDialog = null;
    private ProgressDialog preparingDialog = null;
    public MaterialDialog connectingNetworkDialog = null;
    public MaterialDialog connectNetworkFailDialog = null;
    public MaterialDialog authFailDialog = null;
    public MaterialDialog noInternetDialog = null;
    public MaterialDialog unknownErrorDialog = null;
    public MaterialDialog completeDialog = null;
    private boolean isReconnected = false;
    //]]

    //socket
    private AsyncSocket mSocket = null;
    private AsyncSSLSocket mSocketForTLS = null;

    //flags
    private boolean isSecure = true;
    //private boolean isSecure = false;
    private boolean isSocketChanged = false;
    private boolean isCompleted = false;
    private boolean mIsSocketConnected = false;

    //[[add in v2.3.19 for concurrent
    private static boolean isReceiveApResult = false;

    private boolean mIsDA16200ApConnected = false;
    private boolean mIsStartConnectDA16200Ap = false;

    private CustomToast customToast;
    private static boolean isRetryPassword = false;
    //]]

    //data
    private ArrayList<Integer> indexList;
    private ArrayList<String> ssidList;
    private ArrayList<Boolean> boolSecModeList;
    public ArrayList<Integer> intSecModeList;  //add in v2.3.13
    private ArrayList<Integer> signalList;
    private ListView listView;
    private APListViewAdapter adapter;
    private ArrayList<APRowItem> apRowItems;
    private APListViewAdapter_1 adapter_1;  //add in v2.3.13
    private ArrayList<APRowItem_1> apRowItems_1;  //add in v2.3.13
    private String[] ssid;
    private String[] stringSecurity;  //add in v2.3.13
    private Integer[] signalBar;
    private Boolean[] boolSecMode;
    private Integer[] intSecMode;  //add in v2.3.13
    private boolean[] isSecurity;  //add in v2.3.13
    private Integer[] level;
    private boolean isUnder2_3_13 = false;

    //constant
    private final static String MSG_TYPE = "msgType";
    private static final long MIN_CLICK_INTERVAL = 600;
    private long mLastClickTime = 0;

    private static SelectNetworkActivity instance;

    public static SelectNetworkActivity getInstance() {
        return instance;
    }

    //handler event
    private static class HandleMsg {
        public static final int CLOSE_SOCKET = 0;
        public static final int E_SHOW_INPUT_PASSWORD_DIALOG = 1;
        public static final int E_SHOW_SUCCESS_DIALOG = 2;
        //add in v2.3.4
        public static final int SOCKET_CONNECT_TIMEOUT = 3;
        public static final int RECEIVE_APLIST_TIMEOUT = 4;
        //
        //[[add in v2.3.17 for AWS fleet provisioning
        public static final int E_SHOW_REGISTERING_DIALOG = 5;
        public static final int E_DISMISS_REGISTERING_DIALOG = 6;
        //]]

        //[[add in v2.3.19 for concurrent mode
        public static final int E_REQ_HOMEAP_RESULT = 7;
        public static final int E_SHOW_NETWORK_CONNECTING_DIALOG = 8;
        public static final int E_SHOW_NETWORK_CONNECT_FAIL_DIALOG = 9;
        public static final int E_SHOW_NO_INTERNET_DIALOG = 10;
        public static final int E_SHOW_AUTH_FAIL_DIALOG = 11;
        public static final int E_SHOW_UNKNOWN_ERROR_DIALOG = 12;

        public static final int RENESAS_AP_CONNECTED = 13;

        public static final int DEVICE_SCAN_TIMEOUT = 14;
        public static final int E_SHOW_RECONNECT_DIALOG = 15;
        //]]

        //[[add in v2.4.14
        public static final int E_SHOW_ENTERPRISE_DIALOG = 16;
        //]]
    }

    /**
     ****************************************************************************************
     * @brief Handler class for splash activity
     * @param
     * @return none
     ****************************************************************************************
     */
    private static final class NetworkHandler extends Handler {

        private final WeakReference<SelectNetworkActivity> ref;

        public NetworkHandler(SelectNetworkActivity act) {
            ref = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            final SelectNetworkActivity act = ref.get();

            if (act != null) {
                switch (msg.what) {

                    case HandleMsg.CLOSE_SOCKET:
                        MyLog.i("Close socket");

                        //[[add in v2.3.15
                        act.sendDisconnected();

                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //]]

                        if (act.mSocketForTLS != null && act.mSocketForTLS.isOpen()) {
                            act.mSocketForTLS.close();
                        }

                        if (act.mSocket != null && act.mSocket.isOpen()) {
                            act.mSocket.close();
                        }

                        if (act.apRowItems != null) {
                            act.apRowItems.clear();
                        }
                        //add in v2.3.13
                        if (act.apRowItems_1 != null) {
                            act.apRowItems_1.clear();
                        }
                        //
                        if (act.adapter != null) {
                            act.adapter.notifyDataSetChanged();
                        }

                        act.mIsSocketConnected = false;
                        act.mSocketForTLS = null;
                        act.mSocket = null;

                        if (act.isSocketChanged) {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            act.onInitSocket();

                            MyLog.i("isSecure = " + act.isSecure);
                        }

                        break;

                    case HandleMsg.E_SHOW_INPUT_PASSWORD_DIALOG:
                        MyLog.i("E_SHOW_INPUT_PASSWORD_DIALOG");
                        if (act.mContext != null) {
                            InputPasswordDialog inputPasswordDialog = new InputPasswordDialog(act.mContext);
                            inputPasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            inputPasswordDialog.setCancelable(true);
                            inputPasswordDialog.getWindow().setGravity(Gravity.CENTER);
                            inputPasswordDialog.show();
                        }
                        break;

                        //[[add in v2.4.14
                    case HandleMsg.E_SHOW_ENTERPRISE_DIALOG:
                        MyLog.i("E_SHOW_ENTERPRISE_DIALOG");
                        if (act.mContext != null) {
                            EnterpriseDialog enterpriseDialog = new EnterpriseDialog(act.mContext);
                            enterpriseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            enterpriseDialog.setCancelable(true);
                            enterpriseDialog.getWindow().setGravity(Gravity.CENTER);
                            enterpriseDialog.show();
                        }
                        break;
                    //]]

                    //[[add in v2.3.17 for AWS fleet provisioning
                    case HandleMsg.E_SHOW_REGISTERING_DIALOG:
                        MyLog.i("E_SHOW_REGISTERING_DIALOG");
                        if (act.registeringDialog == null && act.mContext != null) {
                            act.registeringDialog = new ProgressDialog(act.mContext, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                            act.registeringDialog.setTitle("Registering device");
                            act.registeringDialog.setMessage("The device is being registered with the AWS server.\n" +
                                    "It takes about 60 seconds.");
                            act.registeringDialog.show();

                            int displayWidth = 0;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                WindowMetrics windowMetrics = act.getWindowManager().getCurrentWindowMetrics();
                                Insets insets = windowMetrics.getWindowInsets()
                                        .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
                                displayWidth = windowMetrics.getBounds().width() - insets.left - insets.right;
                            } else {
                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                act.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                displayWidth = displayMetrics.widthPixels;
                            }
                            //
                            WindowManager.LayoutParams params = act.registeringDialog.getWindow().getAttributes();
                            int dialogWindowWidth = (int) (displayWidth * 0.9f);
                            params.width = dialogWindowWidth;
                            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            act.registeringDialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
                            act.registeringDialog.setCanceledOnTouchOutside(false);
                            act.registeringDialog.setCancelable(false);

                        }
                        break;

                    case HandleMsg.E_DISMISS_REGISTERING_DIALOG:
                        MyLog.i("E_DISMISS_REGISTERING_DIALOG");
                        if (act.registeringDialog != null) {
                            act.registeringDialog.dismiss();
                            act.registeringDialog = null;
                        }
                        break;
                    //]]

                    //[[add in v2.3.19 for concurrent

                    case HandleMsg.E_SHOW_RECONNECT_DIALOG:
                        MyLog.i("E_SHOW_RECONNECT_DIALOG");

                        if (act.reconnectDialog == null && act.mContext != null) {
                            //        && ((Activity) NetworkActivity.getInstance().mContext).hasWindowFocus()) {
                            act.reconnectDialog = new MaterialDialog.Builder(act.mContext)
                                    .theme(Theme.LIGHT)
                                    .title("Lost Wi-Fi connection to device")
                                    .titleGravity(GravityEnum.CENTER)
                                    .titleColor(act.getResources().getColor(R.color.blue3))
                                    .content("The connection was lost because the device's Wi-Fi channel changed.\n" +
                                            "You will need to reconnect to your device.")
                                    .contentColor(act.getResources().getColor(R.color.black))
                                    .contentGravity(GravityEnum.START)
                                    .positiveText("OK")
                                    .positiveColor(act.getResources().getColor(R.color.blue3))
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                            if (act.reconnectDialog != null) {
                                                act.reconnectDialog.dismiss();
                                                act.reconnectDialog = null;
                                            }

                                            act.isSocketChanged = false;

                                            act.mIsDA16200ApConnected = false;
                                            act.mIsStartConnectDA16200Ap = true;
                                            act.mIsSocketConnected = false;
                                            act.isReconnected = true;

                                            act.onRegisterWifiProvisioningReceiver();

                                            if (act.isDA16200APConnected(act.mContext) == false) {
                                                //[[modify in v2.4.7
                                                //act.onWifiConnect(StaticDataSave.deviceSSID, StaticDataSave.devicePassword);
                                                WifiUtils.withContext(act.getApplicationContext())
                                                        .connectWith(StaticDataSave.deviceSSID, StaticDataSave.devicePassword)
                                                        .setTimeout(40000)
                                                        .onConnectionResult(new ConnectionSuccessListener() {
                                                            @Override
                                                            public void success() {
                                                                MyLog.d("Connect Success!");
                                                                act.mHandler.sendEmptyMessageDelayed(HandleMsg.RENESAS_AP_CONNECTED, 1000);
                                                            }

                                                            @Override
                                                            public void failed(@NonNull ConnectionErrorCode errorCode) {
                                                                //Toast.makeText(SelectDeviceActivity.this, "EPIC FAIL!" + errorCode.toString(), Toast.LENGTH_SHORT).show();
                                                                MyLog.d("EPIC FAIL!" + errorCode.toString());
                                                            }
                                                        })
                                                        .start();
                                                //]]
                                            }


                                        }
                                    })
                                    .showListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
                                            final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                                        }

                                    }).build();
                            act.reconnectDialog.getWindow().setGravity(Gravity.CENTER);
                            act.reconnectDialog.show();
                            act.reconnectDialog.setCanceledOnTouchOutside(false);
                        }
                        break;
                    //]]

                    case HandleMsg.E_SHOW_SUCCESS_DIALOG:
                        MyLog.i("E_SHOW_SUCCESS_DIALOG");

                        if (act.successDialog == null &&
                                act.mContext != null) {
                            act.successDialog = new MaterialDialog.Builder(act.mContext)
                                    .theme(Theme.LIGHT)
                                    .title("Success")
                                    .titleGravity(GravityEnum.CENTER)
                                    .content("If you entered the correct passphrase,\n"
                                            + "your device should now connected.\n"
                                            + "Please check.\n"
                                            + "If it is not connected,\n"
                                            + "please run this application again\n"
                                            + "and re-enter the correct passphrase.")
                                    .contentColor(act.getResources().getColor(R.color.black))
                                    .contentGravity(GravityEnum.START)
                                    .positiveText("OK")
                                    .positiveColor(act.getResources().getColor(R.color.blue3))
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            act.isCompleted = true;
                                            act.successDialog.dismiss();
                                            act.successDialog = null;

                                            MyLog.i("networkSecurity = " + StaticDataSave.networkSecurity + ", networkSSID= " + StaticDataSave.networkSSID + ", networkPassword = " + StaticDataSave.networkPassword);

                                            //[[remove in v2.4.6
                                            /*if (WIFIUtil.isConnectWIFI(act.mContext)) {
                                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                                    //[[modify in v2.4.5
                                                *//*SelectDeviceActivity.mConnectivityManager.bindProcessToNetwork(null);
                                                SelectDeviceActivity.mConnectivityManager.unregisterNetworkCallback(SelectDeviceActivity.mNetworkCallback);*//*
                                                    act.mConnectivityManager.bindProcessToNetwork(null);
                                                    act.mConnectivityManager.unregisterNetworkCallback(act.mNetworkCallback);
                                                    //]]
                                                } else {
                                                    onWifiConnectUnderP(StaticDataSave.networkSSID, StaticDataSave.networkPassword);
                                                    WIFIUtil.setDefaultNetworkForInternet(SelectNetworkActivity.getInstance().mContext);
                                                }
                                            }*/
                                            //]]

                                            //[[remove in v2.4.7
                                            //[[add in v2.4.6
                                            /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                                SelectDeviceActivity.mConnectivityManager.bindProcessToNetwork(null);
                                                SelectDeviceActivity.mConnectivityManager.unregisterNetworkCallback(SelectDeviceActivity.mNetworkCallback);
                                            } else {
                                                onWifiConnectUnderP(StaticDataSave.networkSSID, StaticDataSave.networkPassword);
                                                WIFIUtil.setDefaultNetworkForInternet(SelectNetworkActivity.getInstance().mContext);
                                            }*/
                                            //]]
                                            //]]

                                            //[[add in v2.4.7
                                            WifiUtils.withContext(act.mContext)
                                                    .remove(StaticDataSave.deviceSSID, new RemoveSuccessListener() {
                                                                @Override
                                                                public void success() {
                                                                    MyLog.d("Remove success!");
                                                                }

                                                                @Override
                                                                public void failed(@NonNull RemoveErrorCode errorCode) {
                                                                    MyLog.d("Failed to disconnect and remove: $errorCode");
                                                                }
                                                            });
                                            //]]


                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            MyLog.i("StaticDataSave.mode = " + String.valueOf(StaticDataSave.mode));
                                            MyLog.i("StaticDataSave.thingName = " + StaticDataSave.thingName);

                                            MyLog.i("go to MainActivity.class");
                                            Intent main = new Intent(act.mContext, MainActivity.class);
                                            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            act.startActivity(main);
                                            act.finishAffinity();

                                            /*if (StaticDataSave.mode < 10) {  //Generic SDK
                                                MyLog.i("go to MainActivity.class");
                                                Intent main = new Intent(act.mContext, MainActivity.class);
                                                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                act.startActivity(main);
                                                act.finishAffinity();

                                            } else if (StaticDataSave.mode == 10) {  //General AWS IoT

                                                if (StaticDataSave.thingName.contains("DOORLOCK") || StaticDataSave.thingName.contains("DoorLock")) {  //modify for Keith
                                                    MyLog.i("go to AWSIoTDoorActivity.class");
                                                    Intent main = new Intent(act.mContext, AWSIoTDoorActivity.class);
                                                    main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    act.startActivity(main);
                                                    act.finishAffinity();
                                                } else if (StaticDataSave.thingName.contains("SENSOR")) {
                                                    MyLog.i("go to AWSIoTSensorActivity.class");
                                                    Intent main = new Intent(act.mContext, AWSIoTSensorActivity.class);
                                                    main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    act.startActivity(main);
                                                    act.finishAffinity();
                                                }

                                            } else if (StaticDataSave.mode == 11) {  //AT-CMD AWS IoT
                                                MyLog.i("go to AWSIoTDeviceActivity.class");
                                                Intent main = new Intent(act.mContext, AWSIoTDeviceActivity.class);
                                                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                act.startActivity(main);
                                                act.finishAffinity();
                                            }*/

                                            //change in v2.3.13
                                            /*MyLog.i("go to MainActivity.class");
                                            Intent main = new Intent(act.mContext, MainActivity.class);
                                            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            act.startActivity(main);
                                            act.finishAffinity();*/


                                        }
                                    })
                                    .showListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
                                            final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                                        }

                                    }).build();
                            act.successDialog.getWindow().setGravity(Gravity.CENTER);
                            act.successDialog.show();
                            act.successDialog.setCanceledOnTouchOutside(false);
                            act.successDialog.setCancelable(false);  //add in v2.3.15
                        }
                        break;

                    //add in v2.3.4
                    case HandleMsg.SOCKET_CONNECT_TIMEOUT:
                        MyLog.i("SOCKET_CONNECT_TIMEOUT");
                        act.showSocketConnectTimeoutDialog();
                        break;

                    case HandleMsg.RECEIVE_APLIST_TIMEOUT:
                        MyLog.i("RECEIVE_APLIST_TIMEOUT");
                        act.showReceiveAplistTimeoutDialog();
                        break;
                    //

                    //[[add in v2.3.19 for concurrent

                    case HandleMsg.E_REQ_HOMEAP_RESULT:
                        MyLog.i("== E_REQ_HOMEAP_RESULT ==");
                        MyLog.i("isReceiveApResult = " + act.isReceiveApResult);

                        MyLog.i("getSSID = " + WIFIUtil.getInstance().getSSID(act.mContext));

                        //act.setDialogContent("Setting SSID and password ... done"+"\n"+"Connecting to the AP ...");

                        /*while (act.isReceiveApResult == false) {
                            act.sendReqApResult();
                        }*/
                        if (act.isReceiveApResult == false) {
                            act.sendReqApResult();
                        }

                        //act.setDialogContent("Setting SSID and password ... done"+"\n"+"Connecting to the AP ... done");

                        break;

                    case HandleMsg.E_SHOW_NETWORK_CONNECTING_DIALOG:
                        MyLog.i("E_SHOW_NETWORK_CONNECTING_DIALOG");
                        if (act.connectingNetworkDialog == null && act.mContext != null) {
                            //&& ((Activity) NetworkActivity.getInstance().mContext).hasWindowFocus()) {
                            act.connectingNetworkDialog = new MaterialDialog.Builder(act.mContext)
                                    .theme(Theme.LIGHT)
                                    //.title("Setting up device")
                                    //.titleColor(act.mContext.getResources().getColor(R.color.blue3))
                                    //.titleGravity(GravityEnum.CENTER)
                                    .content("Checking the network status...")
                                    .contentColor(act.getResources().getColor(R.color.black))
                                    .contentGravity(GravityEnum.START)
                                    .progress(true, 50)
                                    .progressIndeterminateStyle(true)
                                    .widgetColorRes(R.color.blue3)
                                    .showListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
                                            final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                                        }

                                    }).build();
                            act.connectingNetworkDialog.getWindow().setGravity(Gravity.CENTER);
                            act.connectingNetworkDialog.show();
                            act.connectingNetworkDialog.setCanceledOnTouchOutside(false);
                        }
                        break;

                    case HandleMsg.E_SHOW_AUTH_FAIL_DIALOG:
                        MyLog.i("E_SHOW_AUTH_FAIL_DIALOG");
                        act.mHandler.removeCallbacksAndMessages(null);

                        if (act.connectingNetworkDialog != null) {
                            act.connectingNetworkDialog.dismiss();
                            act.connectingNetworkDialog = null;
                        }

                        if (act.authFailDialog == null && act.mContext != null) {
                            //        && ((Activity) NetworkActivity.getInstance().mContext).hasWindowFocus()) {
                            act.authFailDialog = new MaterialDialog.Builder(act.mContext)
                                    .theme(Theme.LIGHT)
                                    .title("Authentication problem")
                                    .titleGravity(GravityEnum.CENTER)
                                    .titleColor(act.getResources().getColor(R.color.blue3))
                                    .content("Authentication failed to connect to the AP." + "\n"
                                            + "Check your password entered correctly and try again.")
                                    .contentColor(act.getResources().getColor(R.color.black))
                                    .contentGravity(GravityEnum.START)
                                    .positiveText("OK")
                                    .positiveColor(act.getResources().getColor(R.color.blue3))
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                            act.authFailDialog.dismiss();
                                            act.authFailDialog = null;

                                            //TCPConnection.getWIFIConnection().removeEvent(TCPConnection.WIFICommmad.CMD_WIFI_TCP_REQ_HOMEAP_RESULT);
                                            act.mHandler.removeCallbacksAndMessages(null);

                                            StaticDataSave.networkPassword = null;
                                            act.isCompleted = false;
                                            act.isRetryPassword = true;

                                            StaticDataSave.setSSIDPWResult = -1;
                                            //StaticDataSave.apConnectionResult = -1;
                                            StaticDataSave.rebootResult = -1;

                                            act.isReceiveApResult = false;

                                            act.mHandler.sendEmptyMessage(HandleMsg.E_SHOW_INPUT_PASSWORD_DIALOG);

                                        }
                                    })
                                    .showListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
                                            final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                                        }

                                    }).build();
                            act.authFailDialog.getWindow().setGravity(Gravity.CENTER);
                            act.authFailDialog.show();
                            act.authFailDialog.setCanceledOnTouchOutside(false);
                        }
                        break;

                    case HandleMsg.E_SHOW_NO_INTERNET_DIALOG:
                        MyLog.i("E_NO_INTERNET_DIALOG");
                        //removeEvent(MYEVENT.E_SHOW_NETWORK_CONNECT_FAIL_DIALOG);
                        act.mHandler.removeCallbacksAndMessages(null);

                        if (act.connectingNetworkDialog != null) {
                            act.connectingNetworkDialog.dismiss();
                            act.connectingNetworkDialog = null;
                        }

                        if (act.noInternetDialog == null && act.mContext != null) {
                            //        && ((Activity) NetworkActivity.getInstance().mContext).hasWindowFocus()) {
                            act.noInternetDialog = new MaterialDialog.Builder(act.mContext)
                                    .theme(Theme.LIGHT)
                                    .title("No internet")
                                    .titleGravity(GravityEnum.CENTER)
                                    .titleColor(SelectNetworkActivity.getInstance().getResources().getColor(R.color.blue3))
                                    .content("The WiFi AP is not sure of the internet connection." + "\n"
                                            + "Please set the device to Provisioning mode and try again.")
                                    .contentColor(SelectNetworkActivity.getInstance().getResources().getColor(R.color.black))
                                    .contentGravity(GravityEnum.START)
                                    .positiveText("OK")
                                    .positiveColor(SelectNetworkActivity.getInstance().getResources().getColor(R.color.blue3))
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                            act.noInternetDialog.dismiss();
                                            act.noInternetDialog = null;

                                            //TCPConnection.getWIFIConnection().sendEvent(TCPConnection.WIFICommmad.CMD_REQ_REBOOT);
                                            //TCPConnection.getWIFIConnection().removeEvent(TCPConnection.WIFICommmad.CMD_WIFI_TCP_REQ_HOMEAP_RESULT);
                                            act.mHandler.removeCallbacksAndMessages(null);

                                            act.isCompleted = true;

                                            StaticDataSave.networkSSID = null;
                                            StaticDataSave.networkPassword = null;
                                            StaticDataSave.networkSecurity = false;

                                            //[[add in v2.3.19 for concurrent mode
                                            act.mHandler.sendEmptyMessage(HandleMsg.CLOSE_SOCKET);

                                            act.initFlag();

                                            Intent main = new Intent(act.mContext, DA16200MainActivity.class);
                                            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            act.startActivity(main);
                                            act.finishAffinity();
                                            //]]


                                        }
                                    })
                                    .showListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
                                            final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                                        }

                                    }).build();
                            act.noInternetDialog.getWindow().setGravity(Gravity.CENTER);
                            act.noInternetDialog.show();
                            act.noInternetDialog.setCanceledOnTouchOutside(false);
                        }
                        break;

                    case HandleMsg.E_SHOW_UNKNOWN_ERROR_DIALOG:
                        MyLog.i("E_SHOW_UNKNOWN_ERROR_DIALOG");
                        //removeEvent(MYEVENT.E_SHOW_NETWORK_CONNECT_FAIL_DIALOG);
                        act.mHandler.removeCallbacksAndMessages(null);

                        if (act.connectingNetworkDialog != null) {
                            act.connectingNetworkDialog.dismiss();
                            act.connectingNetworkDialog = null;
                        }

                        if (act.unknownErrorDialog == null && act.mContext != null) {
                            //        && ((Activity) NetworkActivity.getInstance().mContext).hasWindowFocus()) {
                            act.unknownErrorDialog = new MaterialDialog.Builder(act.mContext)
                                    .theme(Theme.LIGHT)
                                    .title("Unknown Error")
                                    .titleGravity(GravityEnum.CENTER)
                                    .titleColor(SelectNetworkActivity.getInstance().getResources().getColor(R.color.blue3))
                                    .content("An unknown error has occurred." + "\n"
                                            + "Please set the device to Provisioning mode and try again.")
                                    .contentColor(SelectNetworkActivity.getInstance().getResources().getColor(R.color.black))
                                    .contentGravity(GravityEnum.START)
                                    .positiveText("YES")
                                    .positiveColor(SelectNetworkActivity.getInstance().getResources().getColor(R.color.blue3))
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                            act.unknownErrorDialog.dismiss();
                                            act.unknownErrorDialog = null;

                                            //TCPConnection.getWIFIConnection().sendEvent(TCPConnection.WIFICommmad.CMD_WIFI_TCP_REQ_REBOOT);

                                            /*if (StaticDataSave.networkSecurity == true) {
                                                Controller.getInstance().sendEvent(MYEVENT.E_NETWORK_CONNECT_WIFI_WPA2);
                                            } else {
                                                Controller.getInstance().sendEvent(MYEVENT.E_NETWORK_CONNECT_WIFI_OPEN);
                                            }*/

                                            act.isCompleted = true;

                                            if (act.apRowItems != null) {
                                                act.apRowItems.clear();
                                            }

                                            if (act.apRowItems_1 != null) {
                                                act.apRowItems_1.clear();
                                            }

                                            act.mHandler.sendEmptyMessage(HandleMsg.CLOSE_SOCKET);  //add in v2.3.15

                                            act.initFlag();

                                            Intent main = new Intent(act.mContext, DA16200MainActivity.class);
                                            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            act.startActivity(main);
                                            act.finishAffinity();

                                        }
                                    })
                                    .negativeText("NO")
                                    .negativeColor(SelectNetworkActivity.getInstance().getResources().getColor(R.color.blue3))
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                            act.unknownErrorDialog.dismiss();
                                            act.unknownErrorDialog = null;

                                            //TCPConnection.getWIFIConnection().sendEvent(TCPConnection.WIFICommmad.CMD_WIFI_TCP_REQ_REBOOT);

                                            /*if (StaticDataSave.networkSecurity == true) {
                                                WIFIUtil.setDefaultNetworkForNoInternet(act.mContext);
                                                act.onWifiConnect(StaticDataSave.networkSSID, StaticDataSave.networkPassword);
                                            } else {
                                                Controller.getInstance().sendEvent(MYEVENT.E_NETWORK_CONNECT_WIFI_OPEN);
                                            }*/

                                            act.finish();
                                        }
                                    })
                                    .showListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
                                            final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                                        }

                                    }).build();
                            act.unknownErrorDialog.getWindow().setGravity(Gravity.CENTER);
                            act.unknownErrorDialog.show();
                            act.unknownErrorDialog.setCanceledOnTouchOutside(false);
                        }
                        break;

                    //]]

                    //[[add in v2.3.19 for concurrent

                    case HandleMsg.RENESAS_AP_CONNECTED:
                        MyLog.i("RENESAS AP Connected");
                        MyLog.i("act.mIsDA16200ApConnected = " + act.mIsDA16200ApConnected);
                        MyLog.i("act.mIsStartConnectDA16200Ap = " + act.mIsStartConnectDA16200Ap);

                        if (act.mIsDA16200ApConnected || act.mIsStartConnectDA16200Ap == false) {
                            break;
                        }

                        act.mIsDA16200ApConnected = true;
                        act.mIsStartConnectDA16200Ap = false;

                        act.customToast.showToast(act.mContext, R.string.dialog_ap_connect_complete, Toast.LENGTH_LONG);

                        act.onUnRegisterWifiProvisioningReceiver();  //add 221215

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        act.onConnectSocket();
                        break;

                    //]]
                    default:
                        break;
                }
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        MyLog.i("onCreate");
        setContentView(R.layout.da16200_activity_select_network);

        mContext = SelectNetworkActivity.this;
        instance = this;

        //[[remove in v2.4.7
        //onRegisterReceiver();  //add in v2.3.15
        //]]

        //[[add in v2.3.19
        customToast = new CustomToast(mContext);
        //]]
        mHandler = new NetworkHandler(this);

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        sw_tls = findViewById(R.id.sw_tls);
        sw_tls.setChecked(true);
        tv_tls = findViewById(R.id.tv_tls);
        iv_tls = findViewById(R.id.iv_tls);

        sw_tls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                long currentClickTime = SystemClock.uptimeMillis();
                long elapsedTime = currentClickTime - mLastClickTime;
                mLastClickTime = currentClickTime;

                if (elapsedTime > MIN_CLICK_INTERVAL) {
                    MyLog.i("sw_tls.setOnCheckedChangeListener");

                    if (apRowItems != null) {
                        apRowItems.clear();
                    }
                    //add in v2.3.13
                    if (apRowItems_1 != null) {
                        apRowItems_1.clear();
                    }
                    //

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }

                    btn_network_scan.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                    btn_network_scan.setEnabled(false);
                    iv_rescan.setVisibility(View.INVISIBLE);
                    //add in v2.3.5
                    btn_hiddenWiFi.setVisibility(View.INVISIBLE);
                    //

                    isSocketChanged = true;
                    ll_switchingSocket.setVisibility(View.VISIBLE);

                    if (!isSecure) {
                        //isSecure = true;
                        tv_tls.setText("TLS Secured");
                        iv_tls.setImageResource(R.drawable.baseline_lock_green);
                        if (mSocket != null && mSocket.isOpen()) {
                            sendSocketType(1);
                        } else {

                        }

                    } else {
                        //Off
                        //isSecure = false;
                        tv_tls.setText("TLS Not Secured");
                        iv_tls.setImageResource(R.drawable.baseline_lock_red);
                        if (mSocket != null && mSocket.isOpen()) {
                            sendSocketType(0);
                        } else {

                        }
                    }
                    isSecure = !isSecure;
                }
            }
        });

        btn_network_scan = findViewById(R.id.btn_network_scan);
        btn_network_scan.setBackgroundColor(getResources().getColor(R.color.dark_gray));
        btn_network_scan.setEnabled(false);
        btn_network_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long currentClickTime = SystemClock.uptimeMillis();
                long elapsedTime = currentClickTime - mLastClickTime;
                mLastClickTime = currentClickTime;

                /*if (elapsedTime > MIN_CLICK_INTERVAL) {
                    if (btn_network_scan.isEnabled()) {
                        ll_progressScanning.setVisibility(View.VISIBLE);
                        sendConnected();
                    }
                }*/
            }
        });


        iv_rescan = findViewById(R.id.iv_rescan);
        iv_rescan.setVisibility(View.INVISIBLE);
        iv_rescan.setOnClickListener(this);

        //add in v2.3.5
        btn_hiddenWiFi = findViewById(R.id.btn_hiddenWiFi);
        btn_hiddenWiFi.setVisibility(View.INVISIBLE);
        btn_hiddenWiFi.setOnClickListener(this);
        //

        tv_mode = (TextView) findViewById(R.id.tv_mode);

        ll_socketConnecting = (LinearLayout) findViewById(R.id.ll_socketConnecting);
        //[[change in v2.4.11
        //socketConnecting = (com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.socketConnecting);
        socketConnecting = (ProgressBar) findViewById(R.id.socketConnecting);
        //socketConnecting.setIndicatorColor(getResources().getColor(R.color.blue3));
        //]]
        ll_socketConnecting.setVisibility(View.INVISIBLE);

        ll_progressScanning = (LinearLayout) findViewById(R.id.ll_progressScanning);
        //[[change in v2.4.11
        //progressScanning = (com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.progressScanning);
        progressScanning = (ProgressBar) findViewById(R.id.progressScanning);
        //progressScanning.setIndicatorColor(getResources().getColor(R.color.blue3));
        //]]
        ll_progressScanning.setVisibility(View.INVISIBLE);

        ll_switchingSocket = (LinearLayout) findViewById(R.id.ll_switchingSocket);
        //[[change in v2.4.11
        //progressSwitching = (com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.progressSwitching);
        progressSwitching = (ProgressBar) findViewById(R.id.progressSwitching);
        //progressSwitching.setIndicatorColor(getResources().getColor(R.color.blue3));
        //]]
        ll_switchingSocket.setVisibility(View.INVISIBLE);

        tv_noList = (TextView) findViewById(R.id.tv_noList);
        tv_noList.setVisibility(View.INVISIBLE);

        listView = (ListView) findViewById(R.id.network_wifi_list);
        listView.setVisibility(View.INVISIBLE);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // listening to single list item on click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //modify in v2.3.13
                if (isUnder2_3_13 == true) {
                    if (apRowItems.size() > 0) {

                        StaticDataSave.networkSSID = apRowItems.get(position).getSSID();
                        MyLog.i("ssid = " + StaticDataSave.networkSSID);

                        StaticDataSave.networkSecurity = apRowItems.get(position).getSecreteMode();
                        MyLog.i("StaticDataSave.networkSecurity = " + apRowItems.get(position).getSecreteMode());
                        if (StaticDataSave.networkSecurity == true) {
                            mHandler.sendEmptyMessage(HandleMsg.E_SHOW_INPUT_PASSWORD_DIALOG);

                        } else {
                            sendDPMSet();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //add in v2.3.5
                            StaticDataSave.isHidden = 0;
                            //
                            sendSSIDPW(StaticDataSave.networkSSID, null, StaticDataSave.isHidden, StaticDataSave.serverURL);

                        }
                    }
                } else {
                    if (apRowItems_1.size() > 0) {

                        StaticDataSave.networkSSID = apRowItems_1.get(position).getSSID();
                        MyLog.i("ssid = " + StaticDataSave.networkSSID);

                        StaticDataSave.networkSecurityNum = apRowItems_1.get(position).getSecurityType();
                        MyLog.i("StaticDataSave.networkSecurityNum = " + apRowItems_1.get(position).getSecurityType());

                        /*
                        typedef enum
                        {
                            eWiFiSecurityOpen = 0,             	///< Open - No Security
                                    eWiFiSecurityWEP,                  	///< WEP
                                    eWiFiSecurityWPA,                  	///< WPA
                                    eWiFiSecurityWPA2,                 	///< WPA2 (RSN)
                                    eWiFiSecurityWPA_AUTO,              	///< WPA & WPA2 (RSN)
                                    eWiFiSecurityOWE,      			///< WPA3 OWE
                                    eWiFiSecuritySAE,      			///< WPA3 SAE
                                    eWiFiSecurityRSN_SAE,        		///< WPA2 (RSN) & WPA3 SAE
                                    eWiFiSecurityWPA_EAP,               	///< WPA Enterprise
                                    eWiFiSecurityWPA2_EAP,              	///< WPA2 Enterprise
                                    eWiFiSecurityWPA_AUTO_EAP,          	///< WPA & WPA2 Enterprise
                                    eWiFiSecurityWPA3_EAP,              	///< WPA3 Enterprise
                                    eWiFiSecurityWPA2_AUTO_EAP,        	 ///< WPA2 & WPA3 Enterprise
                                    eWiFiSecurityWPA3_EAP_192B,         	///< WPA3 192B Enterprise DA16200 not support
                                    eWiFiSecurityNotSupported          	///< Unknown Security
                        } WIFISecurity_t;
                        */

                        if (StaticDataSave.networkSecurityNum == 0
                                || StaticDataSave.networkSecurityNum == 5) {
                            sendDPMSet();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            StaticDataSave.isHidden = 0;
                            sendSSIDPW(StaticDataSave.networkSSID, null, StaticDataSave.networkSecurityNum, StaticDataSave.isHidden, StaticDataSave.serverURL);
                        } else if (StaticDataSave.networkSecurityNum == 1
                                || StaticDataSave.networkSecurityNum == 2
                                || StaticDataSave.networkSecurityNum == 3
                                || StaticDataSave.networkSecurityNum == 4
                                || StaticDataSave.networkSecurityNum == 6
                                || StaticDataSave.networkSecurityNum == 7) {
                            mHandler.sendEmptyMessage(HandleMsg.E_SHOW_INPUT_PASSWORD_DIALOG);
                        } else if (StaticDataSave.networkSecurityNum == 8
                                ||StaticDataSave.networkSecurityNum == 9
                                ||StaticDataSave.networkSecurityNum == 10
                                ||StaticDataSave.networkSecurityNum == 11
                                ||StaticDataSave.networkSecurityNum == 12
                                ||StaticDataSave.networkSecurityNum == 13) {
                            mHandler.sendEmptyMessage(HandleMsg.E_SHOW_ENTERPRISE_DIALOG);
                        } else {

                        }

                    }
                }
                //
            }
        });

        onConnectSocket();

    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        MyLog.i("=== onStop() ===");
        super.onStop();
        if (inputPasswordDialog != null) {
            inputPasswordDialog.dismiss();
        }

        if (successDialog != null) {
            successDialog.dismiss();
        }
    }


    @Override
    public void onDestroy() {

        MyLog.i("=== onDestroy() ===");
        isReceiveApResult = false;
        isSocketChanged = false;
        mIsDA16200ApConnected = false;
        mIsStartConnectDA16200Ap = false;
        mHandler.sendEmptyMessage(HandleMsg.CLOSE_SOCKET);
        //[[add in v2.3.17
        dismissSocketConnectTimeoutDialog();
        //]]

        //[[remove in v2.4.7
        //onUnRegisterReceiver();
        //]]

        //[[add in v2.3.19 for concurrent
        onUnRegisterWifiProvisioningReceiver();

        //[[remove in v2.4.7
        /*final ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            connManager.setProcessDefaultNetwork(null);
        } else {
            connManager.bindProcessToNetwork(null);
        }*/
        //]]

        //[[add in v2.4.7
        WifiUtils.withContext(mContext)
                .remove(StaticDataSave.deviceSSID, new RemoveSuccessListener() {
                    @Override
                    public void success() {
                        MyLog.d("Remove success!");
                    }

                    @Override
                    public void failed(@NonNull RemoveErrorCode errorCode) {
                        MyLog.d("Failed to disconnect and remove: $errorCode");
                    }
                });
        //]]

        //]]

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (apRowItems != null) {
            apRowItems.clear();
        }
        //[[add in v2.3.13
        if (apRowItems_1 != null) {
            apRowItems_1.clear();
        }
        //]]

        mHandler.sendEmptyMessage(HandleMsg.CLOSE_SOCKET);  //add in v2.3.15

        initFlag();

        Intent main = new Intent(SelectNetworkActivity.this, DA16200MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
        finishAffinity();
    }

    protected void onResume() {

        super.onResume();
    }

    @Override
    public void onClick(View view) {

        if (view == iv_back) {

            if (apRowItems != null) {
                apRowItems.clear();
            }
            //add in v2.3.13
            if (apRowItems_1 != null) {
                apRowItems_1.clear();
            }
            //

            mHandler.sendEmptyMessage(HandleMsg.CLOSE_SOCKET);  //add in v2.3.15

            initFlag();

            Intent main = new Intent(SelectNetworkActivity.this, DA16200MainActivity.class);
            //Intent main = new Intent(SelectNetworkActivity.this, SelectDeviceActivity.class);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
            finishAffinity();

        } else if (view == iv_rescan) {

            //add in v2.3.2

            if (indexList != null) {
                indexList.clear();
            }
            if (ssidList != null) {
                ssidList.clear();
            }
            if (boolSecModeList != null) {
                boolSecModeList.clear();
            }
            //add in v2.3.13
            if (intSecModeList != null) {
                intSecModeList.clear();
            }
            //
            if (signalList != null) {
                signalList.clear();
            }

            if (apRowItems != null) {
                apRowItems.clear();
            }
            //add in v2.3.13
            if (apRowItems_1 != null) {
                apRowItems_1.clear();
            }
            //

            //[[add in v2.4.5
            iv_rescan.setEnabled(false);
            btn_hiddenWiFi.setEnabled(false);
            sw_tls.setEnabled(false);
            if (isSecure) {
                sw_tls.setChecked(true);
            } else {
                sw_tls.setChecked(false);
            }
            //]]

            tv_noList.setVisibility(View.INVISIBLE);
            ll_progressScanning.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);

            sendReqRescan();

        }

        //add in v2.3.5
        else if (view == btn_hiddenWiFi) {
            AddWiFiDialog addWiFiDialog = new AddWiFiDialog(mContext);
            addWiFiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            addWiFiDialog.setCancelable(false);
            addWiFiDialog.show();

            /*DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int displayWidth = displayMetrics.widthPixels;*/

            //modify in v2.3.15
            int displayWidth = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
                Insets insets = windowMetrics.getWindowInsets()
                        .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
                displayWidth = windowMetrics.getBounds().width() - insets.left - insets.right;
            } else {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                displayWidth = displayMetrics.widthPixels;
            }
            //
            WindowManager.LayoutParams params = addWiFiDialog.getWindow().getAttributes();
            int dialogWindowWidth = (int) (displayWidth * 0.8f);
            params.width = dialogWindowWidth;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            addWiFiDialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
        }
        //
    }

    public void updateAPList() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ssid = new String[ssidList.size()];
                        stringSecurity = new String[ssidList.size()];
                        signalBar = new Integer[ssidList.size()];
                        boolSecMode = new Boolean[ssidList.size()];
                        level = new Integer[ssidList.size()];
                        for (int i = 0; i < ssidList.size(); i++) {
                            if (boolSecModeList.get(i) == true) {
                                ssid[i] = ssidList.get(i);
                                boolSecMode[i] = true;
                                level[i] = signalList.get(i);
                                signalBar[i] = wifiSignalBar(boolSecMode[i], level[i]);
                            } else if (boolSecModeList.get(i) == false) {
                                ssid[i] = ssidList.get(i);
                                boolSecMode[i] = false;
                                level[i] = signalList.get(i);
                                signalBar[i] = wifiSignalBar(boolSecMode[i], level[i]);
                            }
                        }

                        MyLog.i("StaticDataSave.thingName = " + StaticDataSave.thingName);


                        apRowItems = new ArrayList<APRowItem>();
                        for (int i = 0; i < ssid.length; i++) {
                            if (!ssid[i].contains(StaticDataSave.thingName)) {
                                APRowItem item = new APRowItem(signalBar[i], ssid[i], boolSecMode[i], level[i]);
                                apRowItems.add(item);
                            }
                        }

                        if (apRowItems.size() > 0) {
                            adapter = new APListViewAdapter(getApplicationContext(), R.layout.da16200_wifi_list_item, apRowItems);
                            listView.setAdapter(adapter);
                            listView.setVisibility(View.VISIBLE);
                            ll_progressScanning.setVisibility(View.INVISIBLE);
                            tv_noList.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }).start();

        //[[add in v2.4.5
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_rescan.setEnabled(true);
                btn_hiddenWiFi.setEnabled(true);
                sw_tls.setEnabled(true);
                if (isSecure) {
                    sw_tls.setChecked(true);
                } else {
                    sw_tls.setChecked(false);
                }
            }
        });
        //]]

    }

    //add in v2.3.13
    public void updateAPList_1() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ssid = new String[ssidList.size()];
                        stringSecurity = new String[ssidList.size()];
                        signalBar = new Integer[ssidList.size()];
                        intSecMode = new Integer[ssidList.size()];
                        isSecurity = new boolean[ssidList.size()];
                        level = new Integer[ssidList.size()];
                        for (int i = 0; i < ssidList.size(); i++) {
                            /*if (intSecModeList.get(i) == 1 || intSecModeList.get(i) == 2 || intSecModeList.get(i) == 3) {
                                ssid[i] = ssidList.get(i);
                                stringSecurity[i] = convertStringSecurity(intSecModeList.get(i));
                                intSecMode[i] = intSecModeList.get(i);
                                isSecurity[i] = true;
                                level[i] = signalList.get(i);
                                signalBar[i] = wifiSignalBar(isSecurity[i], level[i]);
                            } else */
                            if (intSecModeList.get(i) == 0 || intSecModeList.get(i) == 5) {
                                ssid[i] = ssidList.get(i);
                                stringSecurity[i] = convertStringSecurity(intSecModeList.get(i));
                                intSecMode[i] = intSecModeList.get(i);
                                isSecurity[i] = false;
                                level[i] = signalList.get(i);
                                signalBar[i] = wifiSignalBar(isSecurity[i], level[i]);
                            } else {
                                ssid[i] = ssidList.get(i);
                                stringSecurity[i] = convertStringSecurity(intSecModeList.get(i));
                                intSecMode[i] = intSecModeList.get(i);
                                isSecurity[i] = true;
                                level[i] = signalList.get(i);
                                signalBar[i] = wifiSignalBar(isSecurity[i], level[i]);
                            }
                        }

                        apRowItems_1 = new ArrayList<APRowItem_1>();


                            for (int i = 0; i < ssid.length; i++) {

                                if (StaticDataSave.thingName != null) {
                                    if (!ssid[i].contains(StaticDataSave.thingName)) {
                                        APRowItem_1 item = new APRowItem_1(signalBar[i], ssid[i], stringSecurity[i], intSecMode[i], level[i]);
                                        apRowItems_1.add(item);
                                    }
                                } else {
                                    APRowItem_1 item = new APRowItem_1(signalBar[i], ssid[i], stringSecurity[i], intSecMode[i], level[i]);
                                    apRowItems_1.add(item);
                                }
                            }


                        if (apRowItems_1 != null && apRowItems_1.size() > 0) {
                            try {
                                //[[remove in v2.3.19
                                //mHandler.removeMessages(DeviceControlActivity.HandleMsg.E_BLE_NETWORK_SCAN_TIMEOUT);
                                //]]
                                adapter_1 = new APListViewAdapter_1(getApplicationContext(), R.layout.da16200_wifi_list_item, apRowItems_1);
                                listView.setAdapter(adapter_1);
                                listView.setVisibility(View.VISIBLE);
                                ll_progressScanning.setVisibility(View.INVISIBLE);
                                tv_noList.setVisibility(View.INVISIBLE);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        } else {
                            MyLog.e("apRowItems_1 == null or apRowItems_1.size() < 0");
                        }
                    }
                });
            }
        }).start();

        //[[add in v2.4.5
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_rescan.setEnabled(true);
                btn_hiddenWiFi.setEnabled(true);
                sw_tls.setEnabled(true);
                if (isSecure) {
                    sw_tls.setChecked(true);
                } else {
                    sw_tls.setChecked(false);
                }
            }
        });
        //]]

    }
    //

    public void updateMode() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (StaticDataSave.mode == 2) {
                            tv_mode.setText("Concurrent Mode");
                        } else if (StaticDataSave.mode == 1) {
                            tv_mode.setText("Soft-AP Mode");
                        } else {
                            tv_mode.setText("N/A");
                        }
                    }
                });
            }
        }).start();

    }


    private void initFlag() {

        StaticDataSave.mode = -1;

        StaticDataSave.thingName = null;

        StaticDataSave.networkSSID = null;
        StaticDataSave.networkSecurity = false;
        StaticDataSave.networkPassword = null;

        StaticDataSave.apConnectionResult = -1;
        StaticDataSave.rebootResult = -1;

    }

    public static NetworkInterface getActiveWifiInterface(Context context) throws SocketException, UnknownHostException {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //Return dynamic information about the current Wi-Fi connection, if any is active.
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) return null;
        InetAddress address = intToInet(wifiInfo.getIpAddress());
        int currentNetworkId = wifiInfo.getNetworkId();
        return NetworkInterface.getByInetAddress(address);
    }

    public static byte byteOfInt(int value, int which) {
        int shift = which * 8;
        return (byte) (value >> shift);
    }

    public static InetAddress intToInet(int value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = byteOfInt(value, i);
        }
        try {
            return InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            // This only happens if the byte array has a bad length
            return null;
        }
    }


    /**
     ****************************************************************************************
     * @brief Socket Connect to AP Server after Device Wifi AP Connection Established.
     * @param
     * @return none
     ****************************************************************************************
     */
    private void onConnectSocket() {
        MyLog.i("onConnectSocket()");

        if (isDA16200APConnected(mContext)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ll_socketConnecting.setVisibility(View.VISIBLE);
            //add in v2.3.4
            MyLog.i("mHandler.sendEmptyMessageDelayed(HandleMsg.SOCKET_CONNECT_TIMEOUT, 20000)");
            //mHandler.sendEmptyMessageDelayed(HandleMsg.SOCKET_CONNECT_TIMEOUT, 20000);  //remove 221215
            //WIFIUtil.setDefaultNetworkForNoInternet(mContext);  //remove 221202
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //
            onInitSocket();
        } else {
            Toast.makeText(mContext, "Wifi connection Failed.", Toast.LENGTH_SHORT).show();

            //WIFIUtil.setDefaultNetworkForNoInternet(mContext);
            //onWifiConnect(StaticDataSave.deviceSSID, StaticDataSave.devicePassword);
        }
    }

    String mHost;
    String mInterface;

    /**
     ****************************************************************************************
     * @brief Socket connection initialize.
     * @param
     * @return none
     ****************************************************************************************
     */
    private void onInitSocket() {

        MyLog.i("onInitSocket()");

        //AlertDialog.showLoadingDialog(mContext, mCurrentActivity,true, false, null, false);

        if (mIsSocketConnected || mSocket != null) {
            MyLog.e("Socket already connected.");
            return;
        }

        mIsSocketConnected = true;


        //[[livetest
        /*try {
            for(Enumeration<NetworkInterface> list = NetworkInterface.getNetworkInterfaces(); list.hasMoreElements();)
            {
                NetworkInterface i = list.nextElement();
                MyLog.d("network_interfaces : "+ "display name = " + i.getDisplayName());
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }*/
        //]]


        try {
            mHost = getGWAddress(mContext).toString().replace("/", "");

            MyLog.i("Socket GateWay IP Address:" + mHost);

            //If mobile app try to connect before Device AP's Socket server is not ready, it should try reconnect.
            if ("0.0.0.0".equals(mHost)) {
                MyLog.e("AP Server is Not Ready");
                mIsSocketConnected = false;
                return;
            }
        } catch (Exception e) {
            MyLog.e("get gateway addr error");

        }

        if (mSocket == null || mSocket.isOpen() == false) {

            MyLog.i("Try to connect socket, isSecure = " + isSecure);

            if (isSecure) {
                new Runnable() {
                    @Override
                    public void run() {

                        MyLog.i("StaticDataSave.SOCKET_PORT_FOR_TLS : " + StaticDataSave.SOCKET_PORT_FOR_TLS);
                        AsyncServer.getDefault().connectSocket(new InetSocketAddress(mHost, StaticDataSave.SOCKET_PORT_FOR_TLS), new ConnectCallback() {

                            @Override
                            public void onConnectCompleted(Exception ex, final AsyncSocket socket) {

                                MyLog.i("AP Server Connection Completed");

                                if (socket != null) {
                                    mSocket = socket;
                                    handleConnectCompletedWithTLS(ex, socket);
                                    //[[add in v2.3.19 for concurrent mode
                                    MyLog.i("mHandler.removeMessages(HandleMsg.SOCKET_CONNECT_TIMEOUT)");
                                    mHandler.removeMessages(HandleMsg.SOCKET_CONNECT_TIMEOUT);
                                    //]]

                                } else {
                                    MyLog.e("Socket is not connected.");
                                    if (ex != null) {
                                        MyLog.e("onConnectCompleted Error");
                                        ex.printStackTrace();
                                    }
                                    mIsSocketConnected = false;
                                    //[[add in v2.4.4
                                    mSocket = null;
                                    onInitSocket_portChange();
                                    //]]
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //[[remove in v2.4.4
                                            //Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                                            //]]
                                            btn_network_scan.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                                            btn_network_scan.setEnabled(false);
                                            iv_rescan.setVisibility(View.INVISIBLE);
                                            //add in v2.3.5
                                            btn_hiddenWiFi.setVisibility(View.INVISIBLE);
                                            //
                                        }
                                    });
                                }
                            }

                        });
                    }
                }.run();
            } else {
                new Runnable() {
                    @Override
                    public void run() {
                        MyLog.i("StaticDataSave.SOCKET_PORT_FOR_TCP : " + StaticDataSave.SOCKET_PORT_FOR_TCP);
                        AsyncServer.getDefault().connectSocket(new InetSocketAddress(mHost, StaticDataSave.SOCKET_PORT_FOR_TCP), new ConnectCallback() {
                            @Override
                            public void onConnectCompleted(Exception ex, final AsyncSocket socket) {

                                MyLog.i("AP Server Connection Completed");

                                if (socket != null) {
                                    mSocket = socket;
                                    handleConnectCompleted(ex, socket);

                                } else {
                                    MyLog.e("Socket is not connected.");
                                    mIsSocketConnected = false;
                                    //[[add in v2.4.4
                                    mSocket = null;
                                    onInitSocket_portChange();
                                    //]]
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //[[remove in v2.4.4
                                            //Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                                            //]]
                                            btn_network_scan.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                                            btn_network_scan.setEnabled(false);
                                            iv_rescan.setVisibility(View.INVISIBLE);
                                            //add in v2.3.5
                                            btn_hiddenWiFi.setVisibility(View.INVISIBLE);
                                            //
                                        }
                                    });
                                }
                            }

                        });
                    }
                }.run();
            }


        } else {
            MyLog.i("Socket is already Opened.");
        }
    }

    //[[add in v2.4.4
    private void onInitSocket_portChange() {

        MyLog.i("onInitSocket_portChange()");

        //AlertDialog.showLoadingDialog(mContext, mCurrentActivity,true, false, null, false);

        if (mIsSocketConnected || mSocket != null) {
            MyLog.e("Socket already connected.");
            return;
        }

        mIsSocketConnected = true;

        try {
            mHost = getGWAddress(mContext).toString().replace("/", "");

            MyLog.i("Socket GateWay IP Address:" + mHost);

            //If mobile app try to connect before Device AP's Socket server is not ready, it should try reconnect.
            if ("0.0.0.0".equals(mHost)) {
                MyLog.e("AP Server is Not Ready");
                mIsSocketConnected = false;
                return;
            }
        } catch (Exception e) {
            MyLog.e("get gateway addr error");

        }

        if (mSocket == null || mSocket.isOpen() == false) {

            MyLog.i("Try to connect socket, isSecure = " + isSecure);

            if (isSecure) {
                new Runnable() {
                    @Override
                    public void run() {
                        MyLog.i("StaticDataSave.SOCKET_PORT_FOR_TLS1 : " + StaticDataSave.SOCKET_PORT_FOR_TLS1);
                        AsyncServer.getDefault().connectSocket(new InetSocketAddress(mHost, StaticDataSave.SOCKET_PORT_FOR_TLS1), new ConnectCallback() {

                            @Override
                            public void onConnectCompleted(Exception ex, final AsyncSocket socket) {

                                MyLog.i("AP Server Connection Completed");

                                if (socket != null) {
                                    mSocket = socket;
                                    handleConnectCompletedWithTLS(ex, socket);
                                    //[[add in v2.3.19 for concurrent mode
                                    MyLog.i("mHandler.removeMessages(HandleMsg.SOCKET_CONNECT_TIMEOUT)");
                                    mHandler.removeMessages(HandleMsg.SOCKET_CONNECT_TIMEOUT);
                                    //]]

                                } else {
                                    MyLog.e("Socket is not connected.");
                                    if (ex != null) {
                                        MyLog.e("onConnectCompleted Error");
                                        ex.printStackTrace();
                                    }
                                    mIsSocketConnected = false;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                                            btn_network_scan.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                                            btn_network_scan.setEnabled(false);
                                            iv_rescan.setVisibility(View.INVISIBLE);
                                            //add in v2.3.5
                                            btn_hiddenWiFi.setVisibility(View.INVISIBLE);
                                            //
                                        }
                                    });
                                }
                            }

                        });
                    }
                }.run();
            } else {
                new Runnable() {
                    @Override
                    public void run() {
                        MyLog.i("StaticDataSave.SOCKET_PORT_FOR_TCP1 : " + StaticDataSave.SOCKET_PORT_FOR_TCP1);
                        AsyncServer.getDefault().connectSocket(new InetSocketAddress(mHost, StaticDataSave.SOCKET_PORT_FOR_TCP1), new ConnectCallback() {
                            @Override
                            public void onConnectCompleted(Exception ex, final AsyncSocket socket) {

                                MyLog.i("AP Server Connection Completed");

                                if (socket != null) {
                                    mSocket = socket;
                                    handleConnectCompleted(ex, socket);

                                } else {
                                    MyLog.e("Socket is not connected.");
                                    mIsSocketConnected = false;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                                            btn_network_scan.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                                            btn_network_scan.setEnabled(false);
                                            iv_rescan.setVisibility(View.INVISIBLE);
                                            //add in v2.3.5
                                            btn_hiddenWiFi.setVisibility(View.INVISIBLE);
                                            //
                                        }
                                    });
                                }
                            }

                        });
                    }
                }.run();
            }


        } else {
            MyLog.i("Socket is already Opened.");
        }
    }

    //]]

    /**
     ****************************************************************************************
     * @brief Socket Connect Handler Registration
     * @param ex Exception
     * @param socket Socket
     * @return none
     ****************************************************************************************
     */
    private void handleConnectCompleted(Exception ex, final AsyncSocket socket) {

        MyLog.i("handleConnectCompleted");

        if (ex != null) {
            MyLog.e("handleConnectCompleted Error");
            ex.printStackTrace();
            mIsSocketConnected = false;
            mSocket = null;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                    btn_network_scan.setEnabled(false);
                    iv_rescan.setVisibility(View.INVISIBLE);
                    //add in v2.3.5
                    btn_hiddenWiFi.setVisibility(View.INVISIBLE);
                    //
                }
            });

            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //
                //[[modify in v2.3.19 for concurrent mode
                if (isReconnected == false) {
                    tv_tls.setText(R.string.tls_not_secured);
                    iv_tls.setImageResource(R.drawable.baseline_lock_red);
                    tv_tls.setVisibility(View.VISIBLE);
                    iv_tls.setVisibility(View.VISIBLE);
                    ll_switchingSocket.setVisibility(View.INVISIBLE);
                    btn_network_scan.setBackgroundColor(getResources().getColor(R.color.blue3));
                    btn_network_scan.setEnabled(true);
                    iv_rescan.setVisibility(View.VISIBLE);
                    //add in v2.3.5
                    btn_hiddenWiFi.setVisibility(View.VISIBLE);
                    //
                    ll_socketConnecting.setVisibility(View.INVISIBLE);  //add in v2.3.15
                    ll_progressScanning.setVisibility(View.VISIBLE);
                    //add in v2.3.4
                    MyLog.i(">> mHandler.removeMessages(HandleMsg.SOCKET_CONNECT_TIMEOUT)");
                    mHandler.removeMessages(HandleMsg.SOCKET_CONNECT_TIMEOUT);
                    mHandler.sendEmptyMessageDelayed(HandleMsg.RECEIVE_APLIST_TIMEOUT, 20000);  //change from 10000 to 20000 for Mirador
                    sendConnected();
                } else {
                    mHandler.removeMessages(HandleMsg.SOCKET_CONNECT_TIMEOUT);
                    MyLog.i(">> mHandler.removeMessages(HandleMsg.SOCKET_CONNECT_TIMEOUT)");

                    //setDialogContent("Setting SSID and password ... done");
                    if (isReceiveApResult == false) {
                        mHandler.sendEmptyMessage(HandleMsg.E_REQ_HOMEAP_RESULT);
                    }

                }
                //]]


            }
        });

        socket.setWriteableCallback(new WritableCallback() {
            @Override
            public void onWriteable() {
                MyLog.i("Writeable CallBack");
            }
        });


        //Data Callback from Device AP
        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                MyLog.i("DataCallback");
                if (bb != null) {
                    //MyLog.i("[TCP] received : " + bb);
                    MyLog.i("bb.remaining() = " + bb.remaining());
                    String input = new String(bb.getAllByteArray());

                    if (bb.remaining() == 1460) {
                        sb.append(input);
                        socket.getDataCallback();
                    } else if (bb.remaining() < 1460) {
                        sb.append(input);
                    }

                    try {
                        JSONObject jsonObject = null;
                        //jsonObject = new JSONObject(bb.readString());
                        jsonObject = new JSONObject(sb.toString());
                        MyLog.i("[TCP] received : " + jsonObject);

                        if (jsonObject != null) {

                            if (jsonObject.has("SOCKET_TYPE")) {
                                receiveSocketType(jsonObject);
                            }

                            if (jsonObject.has("thingName")) {
                                receiveThingName(jsonObject);
                            }

                            if (jsonObject.has("mode")) {
                                receiveMode(jsonObject);
                            }

                            //add in v2.3.14
                            if (jsonObject.has("azureConString")) {
                                receiveAzureConString(jsonObject);
                            }
                            //

                            if (jsonObject.has("APList")) {
                                //add in v2.3.4
                                mHandler.removeMessages(HandleMsg.RECEIVE_APLIST_TIMEOUT);
                                //
                                receiveAPList(jsonObject);
                            }

                            if (jsonObject.has("SET_AP_SSID_PW")) {
                                receiveSetApSSIDPW(jsonObject);
                            }

                            //[[add in v2.3.19 for concurrent mode
                            if (StaticDataSave.mode == 2) {
                                if (jsonObject.has("RESULT_REBOOT")) {
                                    receiveRebootResult(jsonObject);
                                }

                                if (jsonObject.has("RESULT_HOMEAP")) {
                                    receiveApResult(jsonObject);
                                }
                            } else {
                                if (jsonObject.has("RESULT_REBOOT")) {
                                    receiveRebootResult(jsonObject);
                                }
                            }
                            //]]


                        } else {
                            MyLog.i("json object invalid");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    MyLog.i("input is null~~");
                }
            }
        });

        //Socket close Callback
        socket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {

                MyLog.i("Socket Closed");

                if (ex != null) {
                    MyLog.e("ClosedCallback Error");
                    ex.printStackTrace();
                }
            }
        });


        socket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                MyLog.i("TCP Socket End");
                //[[add in v2.3.19 for concurrent mode
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyLog.i("isSocketChanged = " + isSocketChanged);
                        if (!isSocketChanged && !isCompleted) {  //add !isCompleted condition in v2.4.1
                            if (StaticDataSave.mode == 2) {

                                mSocket = null;
                                mSocketForTLS = null;
                                mIsSocketConnected = false;

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (WIFIUtil.getSSID(mContext).equals("Dialog_DA16200")
                                                || WIFIUtil.getSSID(mContext).equals("Renesas_DA16200")
                                                //[[add in v2.4.8
                                                || WIFIUtil.getSSID(mContext).equals("Renesas_IoT_WiFi")
                                                //|| WIFIUtil.getSSID(mContext).equals("RZ_RRQ66620")  //remove in v2.4.9
                                                //]]
                                        ) {
                                            onConnectSocket();
                                        } else {
                                            mHandler.sendEmptyMessage(HandleMsg.E_SHOW_RECONNECT_DIALOG);
                                        }
                                    }
                                }, 10000);


                            }
                        }
                    }
                });
                //]]
                if (ex != null) {
                    MyLog.e("EndCallback Error");
                    ex.printStackTrace();
                }

            }
        });

    }

    public int getTotal(byte bytes[]) {
        return ((((int) bytes[3] & 0xff) << 8) |
                (((int) bytes[2] & 0xff)));
    }

    public int getRemain(byte bytes[]) {
        return ((((int) bytes[1] & 0xff) << 8) |
                (((int) bytes[0] & 0xff)));
    }


    /**
     ****************************************************************************************
     * @brief Socket Connect Handler Registration With TLS
     * @param ex Exception
     * @param socket Socket
     * @return none
     ****************************************************************************************
     */
    private void handleConnectCompletedWithTLS(Exception ex, final AsyncSocket socket) {

        MyLog.i("handleConnectCompletedWithTLS");

        SSLContext sslCtx = null;
        SSLEngine sslEng = null;
        TrustManager[] tm = null;

        if (ex != null) {
            MyLog.e("handleConnectCompleted Error");
            ex.printStackTrace();
            mIsSocketConnected = false;
            mSocket = null;
            mSocketForTLS = null;
            return;
        }

        try {
            tm = new TrustManager[]{createTrustMangerForAll()};

            //modify in v2.3.4
            //sslCtx = SSLContext.getInstance("SSL");
            try {
                sslParameters = SSLContext.getDefault().getDefaultSSLParameters();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            MyLog.d("sslParameters.getProtocols() = " + Arrays.toString(sslParameters.getProtocols()));
            sslCtx = SSLContext.getInstance("TLSv1.2");
            //
            sslCtx.init(null, tm, new SecureRandom());
            sslEng = sslCtx.createSSLEngine();
        } catch (NoSuchAlgorithmException e) {
            MyLog.e("NoSuchAlgorithmException Error");
            ex.printStackTrace();
        } catch (KeyManagementException e) {
            MyLog.e("KeyManagementException Error");
            ex.printStackTrace();
        } catch (Exception e) {
            MyLog.e("SSLContext Init Unknown Exception Error");
            ex.printStackTrace();
        }

        AsyncSSLSocketWrapper.handshake(socket, mHost, StaticDataSave.SOCKET_PORT_FOR_TLS, sslEng, tm, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER, true,
                new AsyncSSLSocketWrapper.HandshakeCallback() {
                    @Override
                    public void onHandshakeCompleted(Exception e, AsyncSSLSocket socket) {
                        MyLog.i("onHandshakeComplete");

                        if (e != null) {
                            MyLog.e("onHandshakeCompleted Error");
                            e.printStackTrace();
                            mIsSocketConnected = false;
                            mSocket = null;
                            mSocketForTLS = null;

                            return;
                        }

                        if (socket != null) {
                            mSocketForTLS = socket;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //
                                    //[[modify in v2.3.19 for concurrent mode
                                    MyLog.i("isReconnected = " + isReconnected);
                                    if (isReconnected == false) {
                                        tv_tls.setText(R.string.tls_secured);
                                        iv_tls.setImageResource(R.drawable.baseline_lock_green);
                                        tv_tls.setVisibility(View.VISIBLE);
                                        iv_tls.setVisibility(View.VISIBLE);
                                        ll_switchingSocket.setVisibility(View.INVISIBLE);
                                        btn_network_scan.setBackgroundColor(getResources().getColor(R.color.blue3));
                                        btn_network_scan.setEnabled(true);
                                        iv_rescan.setVisibility(View.VISIBLE);
                                        //add in v2.3.5
                                        btn_hiddenWiFi.setVisibility(View.VISIBLE);
                                        //

                                        ll_socketConnecting.setVisibility(View.INVISIBLE);
                                        ll_progressScanning.setVisibility(View.VISIBLE);
                                        //add in v2.3.4
                                        MyLog.i(">> mHandler.removeMessages(HandleMsg.SOCKET_CONNECT_TIMEOUT)");
                                        mHandler.removeMessages(HandleMsg.SOCKET_CONNECT_TIMEOUT);
                                        mHandler.sendEmptyMessageDelayed(HandleMsg.RECEIVE_APLIST_TIMEOUT, 20000);  //change from 10000 to 20000 for Mirador
                                        sendConnected();
                                    } else {
                                        ll_socketConnecting.setVisibility(View.INVISIBLE);
                                        MyLog.i(">> mHandler.removeMessages(HandleMsg.SOCKET_CONNECT_TIMEOUT)");
                                        mHandler.removeMessages(HandleMsg.SOCKET_CONNECT_TIMEOUT);
                                        mHandler.sendEmptyMessage(HandleMsg.E_SHOW_NETWORK_CONNECTING_DIALOG);

                                        if (isReceiveApResult == false) {
                                            mHandler.sendEmptyMessage(HandleMsg.E_REQ_HOMEAP_RESULT);
                                        }
                                    }
                                    //]]

                                }
                            });
                        } else {
                            mIsSocketConnected = false;
                            mSocket = null;
                            mSocketForTLS = null;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                                    btn_network_scan.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                                    btn_network_scan.setEnabled(false);
                                    iv_rescan.setVisibility(View.INVISIBLE);
                                    //add in v2.3.5
                                    btn_hiddenWiFi.setVisibility(View.INVISIBLE);
                                    //
                                }
                            });
                            return;
                        }

                        socket.setWriteableCallback(new WritableCallback() {
                            @Override
                            public void onWriteable() {
                                MyLog.i("Writeable CallBack");
                            }
                        });

                        //Data Callback from Device AP
                        socket.setDataCallback(new DataCallback() {
                            @Override
                            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                                MyLog.i("DataCallback");
                                if (bb != null) {

                                    //MyLog.i("[TLS] received : " + bb);

                                    try {

                                        JSONObject jsonObject = null;
                                        jsonObject = new JSONObject(bb.readString());

                                        MyLog.i("[TLS] received : " + jsonObject);

                                        if (jsonObject != null) {

                                            if (jsonObject.has("SOCKET_TYPE")) {
                                                receiveSocketType(jsonObject);
                                            }

                                            if (jsonObject.has("thingName")) {
                                                receiveThingName(jsonObject);
                                            }

                                            if (jsonObject.has("mode")) {
                                                receiveMode(jsonObject);
                                            }

                                            //add in v2.3.14
                                            if (jsonObject.has("azureConString")) {
                                                receiveAzureConString(jsonObject);
                                            }
                                            //

                                            if (jsonObject.has("APList")) {
                                                //add in v2.3.4
                                                mHandler.removeMessages(HandleMsg.RECEIVE_APLIST_TIMEOUT);
                                                //
                                                receiveAPList(jsonObject);
                                            }

                                            if (jsonObject.has("SET_AP_SSID_PW")) {
                                                receiveSetApSSIDPW(jsonObject);
                                            }

                                            //[[add in v2.3.19 for concurrent mode
                                            if (StaticDataSave.mode == 2) {  //concurent mode

                                                if (jsonObject.has("RESULT_REBOOT")) {
                                                    receiveRebootResult(jsonObject);
                                                    isSocketChanged = false;
                                                }

                                                if (jsonObject.has("RESULT_HOMEAP")) {
                                                    receiveApResult(jsonObject);
                                                }
                                            } else {
                                                if (jsonObject.has("RESULT_REBOOT")) {
                                                    receiveRebootResult(jsonObject);
                                                }
                                            }
                                            //]]


                                        } else {
                                            MyLog.i("json object invalid");
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    MyLog.i("input is null~~");
                                }
                            }
                        });

                        //Socket close Callback
                        socket.setClosedCallback(new CompletedCallback() {
                            @Override
                            public void onCompleted(Exception ex) {
                                MyLog.i("Socket Closed");

                                if (ex != null) {
                                    MyLog.e("ClosedCallback Error");
                                    ex.printStackTrace();
                                }
                            }
                        });


                        socket.setEndCallback(new CompletedCallback() {
                            @Override
                            public void onCompleted(Exception ex) {
                                MyLog.i("TLS Socket End");
                                //[[add in v2.3.19 for concurrent mode
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isSocketChanged && !isCompleted) {  //add !isCompleted condition in v2.4.1
                                            if (StaticDataSave.mode == 2) {

                                                mSocket = null;
                                                mSocketForTLS = null;
                                                mIsSocketConnected = false;

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (WIFIUtil.getSSID(mContext).equals("Dialog_DA16200")
                                                                || WIFIUtil.getSSID(mContext).equals("Renesas_DA16200")
                                                                //[[add in v2.4.8
                                                                || WIFIUtil.getSSID(mContext).equals("Renesas_IoT_WiFi")
                                                                //|| WIFIUtil.getSSID(mContext).equals("RZ_RRQ66620")  //remove in v2.4.9
                                                                //]]
                                                        ) {
                                                            onConnectSocket();
                                                        } else {
                                                            mHandler.sendEmptyMessage(HandleMsg.E_SHOW_RECONNECT_DIALOG);
                                                        }
                                                    }
                                                }, 10000);
                                            }
                                        }
                                    }
                                });
                                //]]
                                if (ex != null) {
                                    MyLog.e("EndCallback Error");
                                    ex.printStackTrace();
                                }

                            }
                        });

                    }

                });
    }

    /**
     ****************************************************************************************
     * @brief Create TrustManager for All Cert
     * @return X509TrustManager
     ****************************************************************************************
     */
    private TrustManager createTrustMangerForAll() {
        return new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {

            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        };
    }


    public void receiveSetApSSIDPW(JSONObject obj) {

        if (obj != null) {
            MyLog.i("== receiveSetApSSIDPW ==");
            try {
                if (obj.getInt("SET_AP_SSID_PW") != -1) {
                    MyLog.i("SET_AP_SSID_PW = " + obj.getInt("SET_AP_SSID_PW"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void receiveSocketType(JSONObject obj) {

        if (obj != null) {

            MyLog.i("== receiveSocketType() ==");

            try {
                MyLog.i("socketType = " + obj.getInt("SOCKET_TYPE"));
                StaticDataSave.socketType = obj.getInt("SOCKET_TYPE");
                if (StaticDataSave.socketType == 0 || StaticDataSave.socketType == 1) {
                    mHandler.sendEmptyMessage(HandleMsg.CLOSE_SOCKET);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void receiveThingName(JSONObject obj) {

        if (obj != null) {

            MyLog.i("== receiveThingName() ==");

            try {
                MyLog.i("thingName = " + obj.getString("thingName"));
                StaticDataSave.thingName = obj.getString("thingName");
                StaticDataSave.saveData = mContext.getSharedPreferences(StaticDataSave.mSharedPreferencesName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = StaticDataSave.saveData.edit();
                editor.putString(StaticDataSave.thingNameKey, StaticDataSave.thingName);
                editor.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void receiveMode(JSONObject obj) {

        if (obj != null) {

            MyLog.i("== receiveMode() ==");

            try {
                MyLog.i("mode = " + obj.getInt("mode"));
                StaticDataSave.mode = obj.getInt("mode");

                //updateMode();

                StaticDataSave.saveData = mContext.getSharedPreferences(StaticDataSave.mSharedPreferencesName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = StaticDataSave.saveData.edit();
                editor.putInt(StaticDataSave.modeKey, StaticDataSave.mode);
                editor.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //add in v2.3.14
    public void receiveAzureConString(JSONObject obj) {

        if (obj != null) {

            MyLog.i("== receiveAzureConString() ==");

            try {
                MyLog.i("azureConString = " + obj.getString("azureConString"));
                StaticDataSave.azureConString = obj.getString("azureConString");

                StaticDataSave.saveData = mContext.getSharedPreferences(StaticDataSave.mSharedPreferencesName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = StaticDataSave.saveData.edit();
                editor.putString(StaticDataSave.azureConStringKey, StaticDataSave.azureConString);
                editor.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    //


    public void receiveAPList(JSONObject jsonObject) {

        MyLog.i("== receiveAPList() ==");

        JSONArray jsonArray = null;
        indexList = new ArrayList<>();
        ssidList = new ArrayList<>();
        boolSecModeList = new ArrayList<>();
        intSecModeList = new ArrayList<>();  //add in v2.3.13
        signalList = new ArrayList<>();

        try {
            jsonArray = jsonObject.getJSONArray("APList");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                int index = obj.getInt("index");
                String ssid = obj.getString("SSID");
                if (obj.has("secMode")) {
                    boolean boolSecMode = obj.getBoolean("secMode");
                    boolSecModeList.add(boolSecMode);
                    isUnder2_3_13 = true;
                }
                if (obj.has("securityType")) {
                    int intSecMode = obj.getInt("securityType");  //add in v2.3.13
                    intSecModeList.add(intSecMode);  //add in v2.3.13
                    isUnder2_3_13 = false;
                }

                int signal = obj.getInt("signal");

                indexList.add(index);
                ssidList.add(ssid);
                signalList.add(signal);
            }
            MyLog.i("indexList = " + indexList.toString());
            MyLog.i("ssidList = " + ssidList.toString());
            MyLog.i("isUner2_3_13 = " + isUnder2_3_13);
            if (isUnder2_3_13 == true) {
                if (boolSecModeList != null) {
                    MyLog.i("boolSecModeList = " + boolSecModeList.toString());
                }
            } else if (isUnder2_3_13 == false) {
                if (intSecModeList != null) {
                    MyLog.i("intSecModeList = " + intSecModeList.toString());  //add in v2.3.13
                }
            }

            MyLog.i("signalList = " + signalList.toString());

            //modify in v2.3.13
            if (isUnder2_3_13 == true) {
                updateAPList();
            } else {
                updateAPList_1();
            }
            sb.setLength(0);  //add 221202
            //

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //[[add in v2.3.19 concurrent mode
    public void receiveApResult(JSONObject obj) {

        if (obj != null) {
            MyLog.i("== receiveApResult ==");
            try {
                if (obj.getInt("RESULT_HOMEAP") != -1) {
                    MyLog.i("RESULT_HOMEAP = " + obj.getInt("RESULT_HOMEAP"));
                    StaticDataSave.apConnectionResult = obj.getInt("RESULT_HOMEAP");

                    if (StaticDataSave.apConnectionResult == 0) {  //checking
                        isReceiveApResult = false;

                    } else if (StaticDataSave.apConnectionResult == 1) {  //success
                        mHandler.removeCallbacksAndMessages(null);

                        if (connectingNetworkDialog != null) {
                            connectingNetworkDialog.dismiss();
                        }

                        isReceiveApResult = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ll_socketConnecting.setVisibility(View.INVISIBLE);
                            }
                        });

                        //TCPConnection.getWIFIConnection().sendEvent(TCPConnection.WIFICommmad.CMD_WIFI_TCP_REQ_REBOOT);

                        //[[remove 230424
                        mHandler.sendEmptyMessageDelayed(HandleMsg.E_SHOW_SUCCESS_DIALOG, 1000);
                        //]]


                    } else if (StaticDataSave.apConnectionResult == 2) {  //authentication fail
                        mHandler.removeCallbacksAndMessages(null);

                        if (connectingNetworkDialog != null) {
                            connectingNetworkDialog.dismiss();
                        }

                        isReceiveApResult = true;
                        mHandler.sendEmptyMessage(HandleMsg.E_SHOW_AUTH_FAIL_DIALOG);

                    } else if (StaticDataSave.apConnectionResult == 3) {  //no internet error
                        mHandler.removeCallbacksAndMessages(null);

                        if (connectingNetworkDialog != null) {
                            connectingNetworkDialog.dismiss();
                        }

                        isReceiveApResult = true;
                        mHandler.sendEmptyMessage(HandleMsg.E_SHOW_NO_INTERNET_DIALOG);

                    } else if (StaticDataSave.apConnectionResult == 4) {  //unknown error
                        mHandler.removeCallbacksAndMessages(null);

                        if (connectingNetworkDialog != null) {
                            connectingNetworkDialog.dismiss();
                        }

                        isReceiveApResult = true;
                        mHandler.sendEmptyMessage(HandleMsg.E_SHOW_UNKNOWN_ERROR_DIALOG);
                    } else if (StaticDataSave.apConnectionResult == 10) {
                        isReceiveApResult = false;
                        /*try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/

                        mHandler.sendEmptyMessage(HandleMsg.E_SHOW_NETWORK_CONNECTING_DIALOG);
                        mHandler.sendEmptyMessageDelayed(HandleMsg.E_REQ_HOMEAP_RESULT, 1000);
                    }
                    sb.setLength(0);  //add 221202
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    //]]

    public void receiveRebootResult(JSONObject obj) {

        if (obj != null) {
            MyLog.i("== receiveRebootResult ==");
            try {
                if (obj.getInt("RESULT_REBOOT") != -1) {
                    MyLog.i("RESULT_REBOOT = " + obj.getInt("RESULT_REBOOT"));
                    StaticDataSave.rebootResult = obj.getInt("RESULT_REBOOT");

                    if (StaticDataSave.rebootResult == 0) {  //receive reboot result


                        //[[add in v2.3.17 for AWS fleet provisioning
                        //StaticDataSave.mode = 12 : General AWS IoT
                        //StaticDataSave.mode = 13 : AT-CMD AWS IoT
                        MyLog.i("StaticDataSave.mode = " + StaticDataSave.mode);
                        if (StaticDataSave.mode == 2) {  //add for concurrent

                            //WIFIUtil.setDefaultNetworkForNoInternet(mContext);
                            //onWifiConnect(StaticDataSave.deviceSSID, StaticDataSave.devicePassword);

                            MyLog.i("isCompleted = " + isCompleted);
                            MyLog.i("isSecure = " + isSecure);


                            if (isCompleted == false) {
                                if (isSecure) {
                                    if (mSocketForTLS != null && mSocketForTLS.isOpen()) {
                                        MyLog.i("mSocketForTLS.isOpen() = " + mSocketForTLS.isOpen());
                                        //sendReqApResult();
                                        //mHandler.sendEmptyMessage(HandleMsg.E_REQ_HOMEAP_RESULT);


                                        //mSocketForTLS.end();

                                        /*mIsSocketConnected = false;
                                        mSocketForTLS = null;
                                        mSocket = null;*/
                                        isSocketChanged = false;
                                        MyLog.i("StaticDataSave.apConnectionResult = " + String.valueOf(StaticDataSave.apConnectionResult));
                                        //if (StaticDataSave.apConnectionResult == 2 && isReceiveApResult == false) {
                                        if (isReceiveApResult == false) {

                                            mHandler.sendEmptyMessage(HandleMsg.E_SHOW_NETWORK_CONNECTING_DIALOG);
                                            mHandler.sendEmptyMessageDelayed(HandleMsg.E_REQ_HOMEAP_RESULT, 4000);
                                        }

                                    }
                                } else {
                                    if (mSocket != null && mSocket.isOpen()) {
                                        MyLog.i("mSocket.isOpen() = " + mSocket.isOpen());
                                        //sendReqApResult();
                                        //mHandler.sendEmptyMessage(HandleMsg.E_REQ_HOMEAP_RESULT);

                                        isSocketChanged = false;
                                        MyLog.i("StaticDataSave.apConnectionResult = " + String.valueOf(StaticDataSave.apConnectionResult));
                                        //if (StaticDataSave.apConnectionResult == 2 && isReceiveApResult == false) {
                                        if (isReceiveApResult == false) {

                                            mHandler.sendEmptyMessage(HandleMsg.E_SHOW_NETWORK_CONNECTING_DIALOG);
                                            mHandler.sendEmptyMessageDelayed(HandleMsg.E_REQ_HOMEAP_RESULT, 4000);
                                        }
                                    }
                                }
                            }

                            //onInitSocket();


                        } else if (StaticDataSave.mode == 12 || StaticDataSave.mode == 13) {

                            //[[remove in v2.4.4
                            /*mHandler.sendEmptyMessage(HandleMsg.CLOSE_SOCKET);  //add in v2.3.6
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                mConnectivityManager.bindProcessToNetwork(null);
                                mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);

                            } else {
                                onWifiConnectUnderP(StaticDataSave.networkSSID, StaticDataSave.networkPassword);
                                WIFIUtil.setDefaultNetworkForInternet(SelectNetworkActivity.getInstance().mContext);
                            }*/
                            //]]

                            MyLog.i(">> mHandler.sendEmptyMessage(HandleMsg.E_SHOW_REGISTERING_DIALOG)");
                            mHandler.sendEmptyMessage(HandleMsg.E_SHOW_REGISTERING_DIALOG);
                            mHandler.sendEmptyMessageDelayed(HandleMsg.E_DISMISS_REGISTERING_DIALOG, 60000);
                            mHandler.sendEmptyMessageDelayed(HandleMsg.E_SHOW_SUCCESS_DIALOG, 61000);

                        } else {
                            mHandler.sendEmptyMessageDelayed(HandleMsg.E_SHOW_SUCCESS_DIALOG, 1000);
                        }
                        //]]
                        sb.setLength(0);  //add 221202

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //]]


    /**
     ****************************************************************************************
     * @brief Send a message to Dialog Ap Server
     * @param
     * @return none
     ****************************************************************************************
     */

    private void sendSocketType(Integer type) {
        MyLog.i("tcpSendSocketType, isSecure = " + isSecure);
        if (isSecure) {
            if (mSocketForTLS != null && mSocketForTLS.isOpen()) {
                MyLog.i("TLS Socket is opened");
            } else {
                MyLog.e("TLS Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();

            try {
                obj.put(MSG_TYPE, 6);
                obj.put("SOCKET_TYPE", type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }


            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocketForTLS, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        } else {
            if (mSocket != null && mSocket.isOpen()) {
                MyLog.i("Socket is opened");
            } else {
                MyLog.e("Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();
            try {
                obj.put(MSG_TYPE, 6);
                obj.put("SOCKET_TYPE", type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }

            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocket, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });

                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        }
    }

    private void sendConnected() {
        MyLog.i("sendConnected, isSecure = " + isSecure);

        if (isSecure) {
            if (mSocketForTLS != null && mSocketForTLS.isOpen()) {
                MyLog.i("TLS Socket is opened");
            } else {
                MyLog.e("TLS Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();

            try {
                obj.put(MSG_TYPE, 0);
                obj.put("CONNECTED", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }


            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocketForTLS, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        } else {
            if (mSocket != null && mSocket.isOpen()) {
                MyLog.i("Socket is opened");
            } else {
                MyLog.e("Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();
            try {
                obj.put(MSG_TYPE, 0);
                obj.put("CONNECTED", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }

            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocket, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });

                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        }
    }

    private void sendReqRescan() {
        MyLog.i("sendReqRescan, isSecure = " + isSecure);

        if (isSecure) {
            if (mSocketForTLS != null && mSocketForTLS.isOpen()) {
                MyLog.i("TLS Socket is opened");
            } else {
                MyLog.e("TLS Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();

            try {
                obj.put(MSG_TYPE, 3);
                obj.put("REQ_RESCAN", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }


            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocketForTLS, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        } else {
            if (mSocket != null && mSocket.isOpen()) {
                MyLog.i("Socket is opened");
            } else {
                MyLog.e("Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();
            try {
                obj.put(MSG_TYPE, 3);
                obj.put("REQ_RESCAN", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }

            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocket, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });

                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        }
    }

    public void sendDPMSet() {
        MyLog.i("sendDPMSet, isSecure = " + isSecure);

        if (isSecure) {
            if (mSocketForTLS != null && mSocketForTLS.isOpen()) {
                MyLog.i("TLS Socket is opened");
            } else {
                MyLog.e("TLS Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();

            try {
                obj.put(MSG_TYPE, 5);
                obj.put("REQ_SET_DPM", 0);
                obj.put("sleepMode", 0);
                obj.put("rtcTimer", 1740);
                obj.put("useDPM", 0);
                obj.put("dpmKeepAlive", 30000);
                obj.put("userWakeUp", 0);
                obj.put("timWakeup", 10);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }


            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocketForTLS, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        } else {
            if (mSocket != null && mSocket.isOpen()) {
                MyLog.i("Socket is opened");
            } else {
                MyLog.e("Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();
            try {
                obj.put(MSG_TYPE, 5);
                obj.put("REQ_SET_DPM", 0);
                obj.put("sleepMode", 0);
                obj.put("rtcTimer", 1740);
                obj.put("useDPM", 0);
                obj.put("dpmKeepAlive", 30000);
                obj.put("userWakeUp", 0);
                obj.put("timWakeup", 10);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }

            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocket, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });

                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        }
    }

    public void sendSSIDPW(String _ssid, String _pwd, int _isHidden, String _url) {
        MyLog.i("sendSSIDPW, isSecure = " + isSecure);

        if (isSecure) {
            if (mSocketForTLS != null && mSocketForTLS.isOpen()) {
                MyLog.i("TLS Socket is opened");
            } else {
                MyLog.e("TLS Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();

            try {
                obj.put(MSG_TYPE, 1);
                obj.put("SET_AP_SSID_PW", 0);
                obj.put("ssid", _ssid);
                obj.put("pw", _pwd);
                //add in v2.3.5
                obj.put("isHidden", _isHidden);
                //
                obj.put("url", _url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }


            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocketForTLS, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        } else {
            if (mSocket != null && mSocket.isOpen()) {
                MyLog.i("Socket is opened");
            } else {
                MyLog.e("Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();
            try {
                obj.put(MSG_TYPE, 1);
                obj.put("SET_AP_SSID_PW", 0);
                obj.put("ssid", _ssid);
                obj.put("pw", _pwd);
                //add in v2.3.5
                obj.put("isHidden", _isHidden);
                //
                obj.put("url", _url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }

            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocket, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });

                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        }
    }

    //add in v2.3.13
    public void sendSSIDPW(String _ssid, String _pwd, int _securityType, int _isHidden, String _url) {
        MyLog.i("sendSSIDPW, isSecure = " + isSecure);

        if (isSecure) {
            if (mSocketForTLS != null && mSocketForTLS.isOpen()) {
                MyLog.i("TLS Socket is opened");
            } else {
                MyLog.e("TLS Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();

            try {
                obj.put(MSG_TYPE, 1);
                obj.put("SET_AP_SSID_PW", 0);
                obj.put("ssid", _ssid);
                obj.put("pw", _pwd);
                obj.put("securityType", _securityType);
                //add in v2.3.5
                obj.put("isHidden", _isHidden);
                //
                obj.put("url", _url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }


            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocketForTLS, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        } else {
            if (mSocket != null && mSocket.isOpen()) {
                MyLog.i("Socket is opened");
            } else {
                MyLog.e("Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();
            try {
                obj.put(MSG_TYPE, 1);
                obj.put("SET_AP_SSID_PW", 0);
                obj.put("ssid", _ssid);
                obj.put("pw", _pwd);
                obj.put("securityType", _securityType);
                //add in v2.3.5
                obj.put("isHidden", _isHidden);
                //
                obj.put("url", _url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }

            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocket, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });

                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        }
    }
    //

    public void sendEnterpriseConfig(String _ssid, int _securityType, int _isHidden, String _url, int _authType, int _authProtocol, String _userName, String _password) {
        MyLog.i("sendEnterpriseConfig, isSecure = " + isSecure);

        if (isSecure) {
            if (mSocketForTLS != null && mSocketForTLS.isOpen()) {
                MyLog.i("TLS Socket is opened");
            } else {
                MyLog.e("TLS Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();

            try {
                obj.put(MSG_TYPE, 1);
                obj.put("SET_AP_SSID_PW", 0);
                obj.put("ssid", _ssid);
                obj.put("securityType", _securityType);
                obj.put("isHidden", _isHidden);
                obj.put("url", _url);
                obj.put("authType", _authType);
                obj.put("authProtocol", _authProtocol);
                obj.put("authID", _userName);
                obj.put("authPW", _password);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }


            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocketForTLS, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        } else {
            if (mSocket != null && mSocket.isOpen()) {
                MyLog.i("Socket is opened");
            } else {
                MyLog.e("Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();
            try {
                obj.put(MSG_TYPE, 1);
                obj.put("SET_AP_SSID_PW", 0);
                obj.put("ssid", _ssid);
                obj.put("securityType", _securityType);
                obj.put("isHidden", _isHidden);
                obj.put("url", _url);
                obj.put("authType", _authType);
                obj.put("authProtocol", _authProtocol);
                obj.put("authID", _userName);
                obj.put("authPW", _password);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }

            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocket, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });

                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        }
    }
    //]]

    //[[add in v2.3.15
    private void sendDisconnected() {
        MyLog.i("sendDisconnected, isSecure = " + isSecure);
        if (isSecure) {
            if (mSocketForTLS != null && mSocketForTLS.isOpen()) {
                MyLog.i("TLS Socket is opened");
            } else {
                MyLog.e("TLS Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();

            try {
                obj.put(MSG_TYPE, 8);
                obj.put("DISCONNECTED", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }


            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocketForTLS, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        } else {
            if (mSocket != null && mSocket.isOpen()) {
                MyLog.i("Socket is opened");
            } else {
                MyLog.e("Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();
            try {
                obj.put(MSG_TYPE, 8);
                obj.put("DISCONNECTED", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }

            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocket, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });

                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        }
    }
    //]]

    //[[add in v2.3.19 for concurrent mode
    private void sendReqApResult() {
        MyLog.i("sendDisconnected, isSecure = " + isSecure);
        if (isSecure) {
            if (mSocketForTLS != null && mSocketForTLS.isOpen()) {
                MyLog.i("TLS Socket is opened");
            } else {
                MyLog.e("TLS Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();

            try {
                obj.put(MSG_TYPE, 2);
                obj.put("REQ_HOMEAP_RESULT", 0);
                MyLog.i("REQ_HOMEAP_RESULT -> ");
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }


            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocketForTLS, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        } else {
            if (mSocket != null && mSocket.isOpen()) {
                MyLog.i("Socket is opened");
            } else {
                MyLog.e("Socket is closed");
                return;
            }

            final JSONObject obj = new JSONObject();
            try {
                obj.put(MSG_TYPE, 2);
                obj.put("REQ_HOMEAP_RESULT", 0);
                MyLog.i("REQ_HOMEAP_RESULT -> ");
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] byteMsg = obj.toString().getBytes(StandardCharsets.UTF_8);

            if (byteMsg == null) {
                return;
            }

            //send a message to ap
            com.koushikdutta.async.Util.writeAll(mSocket, byteMsg, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {

                    if (ex != null) {
                        MyLog.e("Sending message error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            }
                        });

                        ex.printStackTrace();
                    } else {
                        MyLog.i("Sending message Completed");
                    }
                }
            });
        }
    }


    //]]


    private int wifiSignalBar(boolean secMode, int level) {

        int absValue = Math.abs(level);
        int signalBarID = R.drawable.baseline_signal_wifi_0_bar_black_48dp;

        if (absValue < 88) {
            if (secMode == true) {
                signalBarID = R.drawable.baseline_signal_wifi_1_bar_lock_black_48dp;
            } else {
                signalBarID = R.drawable.baseline_signal_wifi_1_bar_black_48dp;
            }
            if (absValue < 78) {
                if (secMode == true) {
                    signalBarID = R.drawable.baseline_signal_wifi_2_bar_lock_black_48dp;
                } else {
                    signalBarID = R.drawable.baseline_signal_wifi_2_bar_black_48dp;
                }
                if (absValue < 67) {
                    if (secMode == true) {
                        signalBarID = R.drawable.baseline_signal_wifi_3_bar_lock_black_48dp;
                    } else {
                        signalBarID = R.drawable.baseline_signal_wifi_3_bar_black_48dp;
                    }

                    if (absValue < 56) {
                        if (secMode == true) {
                            signalBarID = R.drawable.outline_signal_wifi_4_bar_lock_black_24;
                        } else {
                            signalBarID = R.drawable.outline_signal_wifi_4_bar_black_24;
                        }
                    }
                }
            }
        } else {
            signalBarID = R.drawable.baseline_signal_wifi_0_bar_black_48dp;
        }

        return signalBarID;
    }

    //add in v2.3.13
/*    private int wifiSignalBar_1(boolean isSecurity, int level) {

        int signalBarID = R.drawable.outline_signal_wifi_4_bar_lock_black_24;

        if(level < 20) {
            if (isSecurity == true) {
                signalBarID = R.drawable.baseline_signal_wifi_1_bar_lock_black_48dp;
            } else {
                signalBarID = R.drawable.baseline_signal_wifi_0_bar_black_48dp;
            }
        }
        else if(level < 40) {
            if (isSecurity == true) {
                signalBarID = R.drawable.baseline_signal_wifi_1_bar_lock_black_48dp;
            } else {
                signalBarID = R.drawable.baseline_signal_wifi_1_bar_black_48dp;
            }
        }
        else if(level < 60) {
            if (isSecurity == true) {
                signalBarID = R.drawable.baseline_signal_wifi_2_bar_lock_black_48dp;
            } else {
                signalBarID = R.drawable.baseline_signal_wifi_2_bar_black_48dp;
            }
        }
        else if(level < 80) {
            if (isSecurity == true) {
                signalBarID = R.drawable.baseline_signal_wifi_3_bar_lock_black_48dp;
            } else {
                signalBarID = R.drawable.baseline_signal_wifi_3_bar_black_48dp;
            }
        }
        else {
            if (isSecurity == true) {
                signalBarID = R.drawable.outline_signal_wifi_4_bar_lock_black_24;
            } else {
                signalBarID = R.drawable.outline_signal_wifi_4_bar_black_24;
            }
        }

        return signalBarID;
    }*/
    //

    /**
     ****************************************************************************************
     * @brief Broadcast Receiver
     * @param
     * @return none
     ****************************************************************************************
     */

    //[[add in v2.3.15
    private class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mContext = context;
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    //WiFi is connected
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();
                    MyLog.i("-- Wifi connected -- " + " SSID : " + ssid);
                    if (ssid.equals("\"Dialog_DA16200\"")) {

                    } else {
                        if (apRowItems != null) {
                            apRowItems.clear();
                        }

                        if (apRowItems_1 != null) {
                            apRowItems_1.clear();
                        }

                        mHandler.sendEmptyMessage(HandleMsg.CLOSE_SOCKET);

                        initFlag();

                        Intent main = new Intent(SelectNetworkActivity.this, DA16200MainActivity.class);
                        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(main);
                        finishAffinity();
                    }
                }
            } else if (intent.getAction().equalsIgnoreCase(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                    MyLog.e("-- Wifi disconnected --");

                    if (apRowItems != null) {
                        apRowItems.clear();
                    }

                    if (apRowItems_1 != null) {
                        apRowItems_1.clear();
                    }

                    mHandler.sendEmptyMessage(HandleMsg.CLOSE_SOCKET);

                    initFlag();

                    Intent main = new Intent(SelectNetworkActivity.this, DA16200MainActivity.class);
                    main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(main);
                    finishAffinity();

                }
            }
        }
    }
    //]]

    //remove in v2.3.15
    /*private class WifiProvisioningReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(WifiManager.EXTRA_WIFI_STATE))

            if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                MyLog.i("Network State Changed");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                boolean isConnected = info.isConnected();

                String ssid = mWifiManager.getConnectionInfo() != null ?
                        mWifiManager.getConnectionInfo().getSSID() : null;

                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

                    if (isConnected) {
                        final String connectedSsid = ssid.replace("\"", "");

                        MyLog.i("Connected SSID:"+connectedSsid);

                        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkRequest req = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();

                        mConnectivityManager.requestNetwork(req, new ConnectivityManager.NetworkCallback()
                        {
                            @Override
                            public void onAvailable(Network network)
                            {
                                MyLog.i("Request Network is onAvailable");

                                super.onAvailable(network);
                                NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(network);

                                String networkSsid = networkInfo.getExtraInfo();

                                if(network == null)
                                {
                                    MyLog.e("Network is null");
                                }

                                MyLog.i("Connected SSID:"+networkSsid);


                                mConnectivityManager.unregisterNetworkCallback(this);

                                boolean result = false;

                                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                                {
                                    result = ConnectivityManager.setProcessDefaultNetwork(network);
                                }
                                else
                                {
                                    result = mConnectivityManager.bindProcessToNetwork(network);
                                }

                                MyLog.i("DefaultNetwork:"+result);
                            }
                        });
                    }
                }
            }
        }
    }*/

    /**
     ****************************************************************************************
     * @brief Register Broadcast Receiver
     * @param
     * @return none
     ****************************************************************************************
     */
    private void onRegisterReceiver() {
        MyLog.i(">> onRegisterReceiver");

        IntentFilter filter;
        filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        //mReceiver = new WifiProvisioningReceiver();
        mReceiver = new WifiReceiver();  //modify in v2.3.15
        mContext.registerReceiver(mReceiver, filter);
    }

    /**
     ****************************************************************************************
     * @brief Unregister Broadcast Receiver
     * @param
     * @return none
     ****************************************************************************************
     */
    private void onUnRegisterReceiver() {
        MyLog.i(">> onUnRegisterReceiver");

        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    //[[add in v2.3.19

    /**
     ****************************************************************************************
     * @brief Register Broadcast Receiver
     * @param
     * @return none
     ****************************************************************************************
     */
    private void onRegisterWifiProvisioningReceiver() {
        MyLog.i("onRegisterWifiProvisioningReceiver");

        IntentFilter filter;
        filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiProvisioningReceiver = new WifiProvisioningReceiver();
        mContext.registerReceiver(wifiProvisioningReceiver, filter);
    }

    /**
     ****************************************************************************************
     * @brief Unregister Broadcast Receiver
     * @param
     * @return none
     ****************************************************************************************
     */
    private void onUnRegisterWifiProvisioningReceiver() {
        MyLog.i("onUnRegisterWifiProvisioningReceiver");

        if (wifiProvisioningReceiver != null) {
            mContext.unregisterReceiver(wifiProvisioningReceiver);
            wifiProvisioningReceiver = null;
        }
    }
    //]]

    /**
     ****************************************************************************************
     * @brief Check Dialog AP connection.
     * @param context
     * @return boolean
     ****************************************************************************************
     */
    private static boolean isDA16200APConnected(Context context) {
        MyLog.i("isDA16200APConnected()");

        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo != null && wifiInfo.getSSID() != null &&
                (wifiInfo.getSSID().contains(StaticDataSave.deviceSSID))) {
            MyLog.i("RENESAS AP Connected.");
            return true;
        } else {
            MyLog.i("RENESAS AP Not Connected.");
            return false;
        }
    }

    /**
     ****************************************************************************************
     * @brief Get gateway address to connect Dialog AP server.
     * @param context
     * @return InetAddress
     ****************************************************************************************
     */
    private static InetAddress getGWAddress(Context context) throws IOException {
        MyLog.i("getGWAddress()");

        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        //[[livetest
        /*WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if(wifiInfo == null) return null;
        int currentNetworkId = wifiInfo.getNetworkId();
        MyLog.d("currentNetworkId = "+String.valueOf(currentNetworkId));*/
        //]]

        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

        if (dhcpInfo == null) {
            return null;
        }

        int gwAddrInt = dhcpInfo.gateway;
        byte[] gwAddrByte = new byte[4];

        for (int i = 0; i < 4; i++) {
            gwAddrByte[i] = (byte) ((gwAddrInt >> i * 8) & 0xFF);
        }

        return InetAddress.getByAddress(gwAddrByte);
    }

    private static void onWifiConnectUnderP(String _ssid, String _pwd) {

        WifiConfiguration wificonfig = new WifiConfiguration();
        wificonfig.SSID = String.format("\"%s\"", _ssid);
        // open network
        if (_pwd == null) {
            wificonfig.status = WifiConfiguration.Status.DISABLED;
            wificonfig.priority = 40;

            wificonfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wificonfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wificonfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wificonfig.allowedAuthAlgorithms.clear();
            wificonfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            wificonfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wificonfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wificonfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wificonfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        } else {
            MyLog.i("wifi_pwd = " + _pwd);
            wificonfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wificonfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wificonfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wificonfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wificonfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wificonfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wificonfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wificonfig.preSharedKey = String.format("\"%s\"", _pwd);
        }

        int netId = mWifiManager.addNetwork(wificonfig);

        if (netId == -1) {
            MyLog.e("add network fail!");

            @SuppressLint("MissingPermission") List<WifiConfiguration> wifiConfig = mWifiManager.getConfiguredNetworks();

            int tmpNetId = -1;

            if (wifiConfig != null) {
                for (int i = 0; i < wifiConfig.size(); i++) {
                    if (wifiConfig.get(i) != null &&
                            StringUtils.isNotBlank(wifiConfig.get(i).SSID)) {
                        MyLog.i("SSID:" + wifiConfig.get(i).SSID + "  netID:" + wifiConfig.get(i).networkId);

                        if (wifiConfig.get(i).SSID.contains(_ssid)) {
                            MyLog.i("Wifi Ap found.");
                            tmpNetId = wifiConfig.get(i).networkId;
                            MyLog.i("Net ID:" + tmpNetId);
                            break;
                        }
                    }
                }
            }

            if (tmpNetId != -1) {
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(tmpNetId, true);
                mWifiManager.reconnect();
            } else {
                MyLog.e("Wifi connection failed");
            }
        } else {
            mWifiManager.disconnect();
            mWifiManager.enableNetwork(netId, true);
            mWifiManager.reconnect();
        }

    }

    //add in v2.3.4
    private void showSocketConnectTimeoutDialog() {
        if (socketConnectTimeoutDialog != null) {
            socketConnectTimeoutDialog.dismiss();
        }
        if (mContext != null) {
            socketConnectTimeoutDialog = new MaterialDialog.Builder(mContext)
                    .theme(Theme.LIGHT)
                    .title("Socket connection timeout")
                    .titleGravity(GravityEnum.CENTER)
                    .titleColor(mContext.getResources().getColor(R.color.black))
                    .content("Socket connection failed.\n" +
                            "Please run provisioning again.")
                    .contentColor(mContext.getResources().getColor(R.color.black))
                    .positiveText("OK")
                    .positiveColor(mContext.getResources().getColor(R.color.blue3))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dismissSocketConnectTimeoutDialog();
                            Intent main = new Intent(SelectNetworkActivity.this, MainActivity.class);
                            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(main);
                            finishAffinity();
                        }
                    })
                    .build();
            socketConnectTimeoutDialog.getWindow().setGravity(Gravity.CENTER);
            socketConnectTimeoutDialog.show();
        }

    }

    private void dismissSocketConnectTimeoutDialog() {
        if (socketConnectTimeoutDialog != null) {
            socketConnectTimeoutDialog.dismiss();
        }
    }

    private void showReceiveAplistTimeoutDialog() {
        if (receiveAplistTimeoutDialog != null) {
            receiveAplistTimeoutDialog.dismiss();
        }
        if (mContext != null) {
            receiveAplistTimeoutDialog = new MaterialDialog.Builder(mContext)
                    .theme(Theme.LIGHT)
                    .title("Receive Wi-Fi network list timeout")
                    .titleGravity(GravityEnum.CENTER)
                    .titleColor(mContext.getResources().getColor(R.color.black))
                    .content("Could not receive Wi-Fi network list due to unknown error.\n" +
                            "Please run provisioning again.")
                    .contentColor(mContext.getResources().getColor(R.color.black))
                    .positiveText("OK")
                    .positiveColor(mContext.getResources().getColor(R.color.blue3))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dismissReceiveAplistTimeoutDialog();
                            Intent main = new Intent(SelectNetworkActivity.this, MainActivity.class);
                            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(main);
                            finishAffinity();
                        }
                    })
                    .build();
            receiveAplistTimeoutDialog.getWindow().setGravity(Gravity.CENTER);
            receiveAplistTimeoutDialog.show();
        }

    }

    private void dismissReceiveAplistTimeoutDialog() {
        if (socketConnectTimeoutDialog != null) {
            socketConnectTimeoutDialog.dismiss();
        }
    }
    //


    //add in v2.3.13
    public String convertStringSecurity(int securityNumber) {
        String stringSecurity = "";
        if (securityNumber == 0) {
            stringSecurity = "none";
        } else if (securityNumber == 1) {
            stringSecurity = "WEP";
        } else if (securityNumber == 2) {
            stringSecurity = "WPA";
        } else if (securityNumber == 3) {
            stringSecurity = "WPA2";
        } else if (securityNumber == 4) {
            stringSecurity = "WPA/WPA2";
        } else if (securityNumber == 5) {
            stringSecurity = "WPA3OWE";
        } else if (securityNumber == 6) {
            stringSecurity = "WPA3SAE";
        } else if (securityNumber == 7) {
            stringSecurity = "WPA2/WPA3SAE";
        } else if (securityNumber == 8) {
            stringSecurity = "WPA-EAP";
        } else if (securityNumber == 9) {
        stringSecurity = "WPA2-EAP";
        } else if (securityNumber == 10) {
            stringSecurity = "WPA/WPA2-EAP";
        } else if (securityNumber == 11) {
            stringSecurity = "WPA3-EAP";
        } else if (securityNumber == 12) {
            stringSecurity = "WPA2/WPA3-EAP";
        } else if (securityNumber == 13) {
            stringSecurity = "WPA3-EAP-192B";
        }
        return stringSecurity;
    }
    //

    //[[add in v2.3.19 for concurrent mode

    /*private void setDialogContent(final String content) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        if (connectingNetworkDialog != null) {
                            connectingNetworkDialog.setContent(content);
                        }
                    }
                });
            }
        }).start();

    }*/
    //]]

    private void initApp() {

        StaticDataSave.mode = -1;

        StaticDataSave.deviceSSID = null;
        StaticDataSave.deviceSecurity = null;
        StaticDataSave.devicePassword = null;
        StaticDataSave.networkSSID = null;
        StaticDataSave.networkSecurity = false;
        StaticDataSave.networkPassword = null;
        StaticDataSave.thingName = null;

        StaticDataSave.apConnectionResult = -1;
        StaticDataSave.rebootResult = -1;

        isReceiveApResult = false;

    }


    //]]


    /**
     ****************************************************************************************
     * @brief connect Wi-Fi AP.
     * @param
     * @return none
     ****************************************************************************************
     */
    private void onWifiConnect(String wifi_ssid, String wifi_pwd) {

        //ll_progressConnecting.setVisibility(View.VISIBLE);

        MyLog.i("onWifiConnect():" + wifi_ssid);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            MyLog.i("Start Wifi Connect for Version Code upper P");
            NetworkSpecifier specifier;

            if (StringUtils.isBlank(wifi_pwd)) {
                specifier = new WifiNetworkSpecifier.Builder()
                        .setSsid(wifi_ssid)
                        .build();
            } else {
                specifier = new WifiNetworkSpecifier.Builder()
                        .setSsid(wifi_ssid)
                        .setWpa2Passphrase(wifi_pwd)
                        .build();
            }

            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    //[[add in v2.4.5
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
                    //]]
                    .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .setNetworkSpecifier(specifier)
                    .build();

            mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            mNetworkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    MyLog.i("Request Network is onAvailable");
                    super.onAvailable(network);
                    if (mIsDA16200ApConnected == false) {
                        //add runOnUiThread in v2.3.17
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //currentSSID = getSSID(mContext);
                                //tv_currentSSID.setText (currentSSID);
                                //mHandler.removeMessages(HandleMsg.DIALOG_AP_CONNECT_TIMEOUT);
                                //[[add in v2.3.4
                                if (WIFIUtil.getSSID(mContext).equals("Dialog_DA16200")
                                        || WIFIUtil.getSSID(mContext).equals("Renesas_DA16200")
                                        //[[add in v2.4.8
                                        || WIFIUtil.getSSID(mContext).equals("Renesas_IoT_WiFi")
                                        //|| WIFIUtil.getSSID(mContext).equals("RZ_RRQ66620")  //remove in v2.4.9
                                    //]]
                                ) {
                                    WIFIUtil.setDefaultNetworkForNoInternet(mContext);  //add 221124
                                    mHandler.sendEmptyMessageDelayed(HandleMsg.RENESAS_AP_CONNECTED, 5000);
                                }
                                //]]
                            }
                        });

                    }
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    MyLog.i("Request Network onUnavailable");
                }

            };

            mConnectivityManager.requestNetwork(networkRequest, mNetworkCallback);
        } else {
            MyLog.i("Start Wifi Connect for Version Code under P");
            WifiConfiguration wificonfig = new WifiConfiguration();
            wificonfig.SSID = String.format("\"%s\"", wifi_ssid);

            // open network
            if (wifi_pwd == null) {
                wificonfig.status = WifiConfiguration.Status.DISABLED;
                wificonfig.priority = 40;

                wificonfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wificonfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wificonfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wificonfig.allowedAuthAlgorithms.clear();
                wificonfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

                wificonfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wificonfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wificonfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wificonfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            } else {
                wificonfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wificonfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wificonfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                wificonfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wificonfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wificonfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wificonfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                wificonfig.preSharedKey = String.format("\"%s\"", wifi_pwd);
            }

            int netId = mWifiManager.addNetwork(wificonfig);

            if (netId == -1) {
                MyLog.e("add network fail!");

                @SuppressLint("MissingPermission") List<WifiConfiguration> wifiConfig = mWifiManager.getConfiguredNetworks();

                int tmpNetId = -1;

                if (wifiConfig != null) {
                    for (int i = 0; i < wifiConfig.size(); i++) {
                        if (wifiConfig.get(i) != null &&
                                StringUtils.isNotBlank(wifiConfig.get(i).SSID)) {
                            MyLog.i("SSID:" + wifiConfig.get(i).SSID + "  netID:" + wifiConfig.get(i).networkId);

                            if (wifiConfig.get(i).SSID.contains(wifi_ssid)) {
                                MyLog.i("Wifi Ap found.");
                                tmpNetId = wifiConfig.get(i).networkId;
                                MyLog.i("Net ID:" + tmpNetId);
                                break;
                            }
                        }
                    }
                }

                if (tmpNetId != -1) {
                    mWifiManager.disconnect();
                    mWifiManager.enableNetwork(tmpNetId, true);
                    mWifiManager.reconnect();
                } else {
                    MyLog.e("Wifi connection failed");
                }
            } else {
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(netId, true);
                mWifiManager.reconnect();
            }
        }
    }

    /**
     ****************************************************************************************
     * @brief Broadcast Receiver
     * @param
     * @return none
     ****************************************************************************************
     */
    private class WifiProvisioningReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                MyLog.i("SCAN_RESULTS_AVAILABLE_ACTION");

            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                MyLog.i("Network State Changed");
                NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                if (ConnectivityManager.TYPE_WIFI == netInfo.getType()) {  //add condition in v2.3.15

                    boolean isConnected = netInfo.isConnected();

                    String ssid = mWifiManager.getConnectionInfo() != null ?
                            mWifiManager.getConnectionInfo().getSSID() : null;

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

                        if (isConnected && mIsDA16200ApConnected == false) {

                            if (ssid != null) {
                                final String connectedSsid = ssid.replace("\"", "");

                                MyLog.i("Connected SSID:" + connectedSsid);

                                if (connectedSsid != null) {
                                    MyLog.d(">> connectedSsid = " + connectedSsid);
                                    //tv_currentSSID.setText(connectedSsid);

                                    if (StringUtils.isNotBlank(connectedSsid) && connectedSsid.contains(StaticDataSave.deviceSSID)) {
                                        //mHandler.removeMessages(HandleMsg.DIALOG_AP_CONNECT_TIMEOUT);
                                        //onUnRegisterReceiver();  //add 221125
                                        if (StaticDataSave.deviceSSID.equals("Dialog_DA16200")
                                                || StaticDataSave.deviceSSID.equals("Renesas_DA16200")
                                                //[[add in v2.4.8
                                                || StaticDataSave.deviceSSID.equals("Renesas_IoT_WiFi")
                                                //|| StaticDataSave.deviceSSID.equals("RZ_RRQ66620")  //remove in v2.4.9
                                                //]]
                                        ) {
                                            mHandler.sendEmptyMessageDelayed(HandleMsg.RENESAS_AP_CONNECTED, 5000);
                                        }

                                    }

                                    //add in v2.3.14
                                    //[[remove in v2.3.17
                                    /*else {
                                        WIFIUtil.setDefaultNetworkForNoInternet(mContext);
                                        onWifiConnect(StaticDataSave.deviceSSID, StaticDataSave.devicePassword);
                                    }*/
                                    //]]
                                    //
                                }

                                //[[remove in v2.3.19

                                /*mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkRequest req = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
                                *//*NetworkRequest req = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                        .build();*//*

                                mConnectivityManager.requestNetwork(req, new ConnectivityManager.NetworkCallback()
                                {
                                    @Override
                                    public void onAvailable(Network network)
                                    {
                                        MyLog.i("Request Network is onAvailable");

                                        super.onAvailable(network);
                                        NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(network);

                                        if (networkInfo != null) {  //add condition 220119
                                            String networkSsid = networkInfo.getExtraInfo();

                                            if(network == null)
                                            {
                                                MyLog.e("Network is null");
                                            }



                                            if (networkSsid != null) {
                                                MyLog.i("Connected SSID:"+networkSsid);
                                                if (networkSsid.equals(StaticDataSave.deviceSSID)) {

                                                    mConnectivityManager.unregisterNetworkCallback(this);

                                                    boolean result = false;

                                                    if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M)
                                                    {
                                                        result = ConnectivityManager.setProcessDefaultNetwork(network);
                                                    }
                                                    else
                                                    {
                                                        result = mConnectivityManager.bindProcessToNetwork(network);
                                                    }

                                                    MyLog.i("DefaultNetwork:"+result);
                                                }
                                            }
                                        }

                                    }
                                });*/
                                //]]
                            }
                        }
                    }
                }
            }
        }
    }

    //]]
}
