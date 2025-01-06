/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.renesas.wifi.DA16600.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.renesas.wifi.DA16600.dialog.AddWiFiDialog;
import com.renesas.wifi.DA16600.BluetoothLeService;
import com.renesas.wifi.DA16600.InputPasswordDialog;
import com.renesas.wifi.DA16600.InputSsidPasswordDialog;
import com.renesas.wifi.DA16600.MyGattAttributes;
import com.renesas.wifi.R;
import com.renesas.wifi.DA16600.adapter.ap.APListViewAdapter;
import com.renesas.wifi.DA16600.adapter.ap.APRowItem;
import com.renesas.wifi.activity.BaseActivity;
import com.renesas.wifi.activity.MainActivity;
import com.renesas.wifi.util.CustomToast;
import com.renesas.wifi.util.FButton;
import com.renesas.wifi.util.MyLog;
import com.renesas.wifi.util.OnSingleClickListener;
import com.renesas.wifi.util.StaticDataSave;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DeviceControlActivity extends BaseActivity implements TextWatcher {

    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static Context mContext;

    //handler
    public static DeviceControlHandler mHandler;

    //service
    public static BluetoothLeService mBluetoothLeService;

    //data
    public ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private ListView listView;
    private APListViewAdapter adapter;
    private ArrayList<APRowItem> apRowItems;
    public ArrayList<String> ssidList;
    public ArrayList<Integer> securityList;
    public ArrayList<Integer> signalList;
    private String[] ssid;
    private String[] stringSecurity;
    private Integer[] signalBar;
    private boolean[] isSecurity;
    private Integer[] secMode;
    private Integer[] level;

    //UI resources
    private CustomToast customToast;
    private TextView tv_deviceName;
    private TextView mConnectionState;
    private ImageView iv_back;
    private FButton btn_bleConnect;
    //ImageButton btn_dpmSet;
    public FButton btn_apScan;
    //add in v2.3.5
    public FButton btn_hiddenWiFi;
    //
    public FButton btn_command;
    public FButton btn_reset;
    //FButton btn_disconnect;
    public FButton btn_connect;
    public RelativeLayout rl_scanAP;
    private LinearLayout ll_progressScanning;
    //[[change in v2.4.11
    //private com.wang.avi.AVLoadingIndicatorView progressScanning;
    private ProgressBar progressScanning;
    //]]
    private TextView tv_progress;
    private TextView tv_noList;
    public LinearLayout ll_selectAP;
    //EditText et_password;
    private LinearLayout ll_sendCommand;
    //LinearLayout ll_dpmSet;
    private EditText et_rawCommand1;
    private EditText et_rawCommand2;
    private EditText et_command;
    public FButton btn_send;
    private AlertDialog txApInfoFailDialog;
    private AlertDialog ApWorngPwdDialog;
    private AlertDialog apFailDialog;
    private AlertDialog dnsFailServerFailDialog;
    private AlertDialog dnsFailServerOkDialog;
    private AlertDialog noUrlPingFailDialog;
    private AlertDialog noUrlPingOkDialog;
    private AlertDialog dnsOkPingFailServerOkDialog;
    private AlertDialog dnsOkPingOkDialog;
    private AlertDialog dnsOkPingFailServerFailDialog;
    private ProgressDialog scanningDialog = null;
    private ProgressDialog checkingDialog = null;
    private AlertDialog cmdFailDialog;
    private ProgressDialog receiveAwsThingNameDialog = null;
    //[[add in v2.3.17 for Azure
    private ProgressDialog receiveAzureThingNameDialog = null;
    //]]
    private ProgressDialog connectingDialog = null;

    //[[add in v2.3.17 for AWS fleet provisioning
    private ProgressDialog registeringDialog = null;
    //]]

    //Characteristics
    public static BluetoothGattCharacteristic WIFI_SVC;
    public static BluetoothGattCharacteristic WIFI_SVC_WFCMD;
    public static BluetoothGattCharacteristic WIFI_SVC_WFACT_RES;
    public static BluetoothGattCharacteristic WIFI_SVC_APSCAN_RES;
    public static BluetoothGattCharacteristic WIFI_SVC_PROV_DATA;
    //add in v2.3.4
    public static BluetoothGattCharacteristic WIFI_SVC_AWS_DATA;
    //

    //add in v2.3.14
    public static BluetoothGattCharacteristic WIFI_SVC_AZURE_DATA;
    //

    //add in v2.3.7
    public static BluetoothGattCharacteristic GBG_SVC;
    public static BluetoothGattCharacteristic GBG_SVC_CHAR;
    //

    //flag
    private boolean mConnected = false;
    //private static boolean fromSetup = false;

    //add in v2.3.8
    private boolean isRefreshed = false;
    //
    //[[add in v2.4.12
    private boolean refresh = true;
    //]]

    //[[add in v2.4.12
    private static int retry = 1;
    //]]


    //contant
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    public final static int SCAN_TIMEOUT_TIME = 10000; //msec
    public static int dhcp = 1;

    /*private static int sleepMode = -1;
    private static int rtcDefault = -1;
    private static int rtcTimer = 1740;
    private static int useDPM = -1;
    private static int dpmDefault = -1;
    private static int dpmKeepAlive = 30000;
    private static int userWakeup = 0;
    private static int timWakeup = 10;*/

    //variables
    /*private static String countryCode = null;
    private static String sysMode = null;
    private static String ipConnectType = null;
    private static String staticIpAddress = null;
    private static String channel = null;
    private static String authentication = null;
    private static String sntpEnable = null;
    private static String sntpPeriod = null;
    private static String gmtTimezone = null;
    private static String sntpSever = null;*/


    public static DeviceControlActivity instance;
    public static DeviceControlActivity getInstance() {
        return instance;
    }

    //handler event
    public static class HandleMsg {
        public static final int E_BLE_NETWORK_SCAN_TIMEOUT = 0;
        public static final int E_BLE_CMD_TIMEOUT = 1;
        public static final int E_SHOW_REGISTERING_DIALOG = 2;
        public static final int E_DISMISS_REGISTERING_DIALOG = 3;
    }

    /**
     ****************************************************************************************
     * @brief Handler class for Device Control activity
     * @param
     * @return none
     ****************************************************************************************
     */
    private static final class DeviceControlHandler extends Handler
    {

        private final WeakReference<DeviceControlActivity> ref;

        public DeviceControlHandler(DeviceControlActivity act)
        {
            ref = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            final DeviceControlActivity act = ref.get();

            if (act != null)
            {
                switch (msg.what)
                {
                    case HandleMsg.E_BLE_NETWORK_SCAN_TIMEOUT:
                        MyLog.i("E_BLE_NETWORK_SCAN_TIMEOUT");
                        act.ll_progressScanning.setVisibility(View.INVISIBLE);
                        //[[add in v2.4.8
                        act.dismissScanningDialog();
                        //]]
                        act.dismissScanFailDialog();
                        act.listView.setVisibility(View.INVISIBLE);
                        //[[add in v2.4.8
                        act.ll_selectAP.setVisibility(View.INVISIBLE);
                        //]]
                        act.tv_noList.setVisibility(View.VISIBLE);

                        break;

                    case HandleMsg.E_BLE_CMD_TIMEOUT:
                        MyLog.i(">> retry = "+retry);
                        if (retry == 1) {
                            act.sendNetworkinfo(StaticDataSave.pingAddress, StaticDataSave.svrAddress, StaticDataSave.svrPort, StaticDataSave.svrUrl);
                            mHandler.sendEmptyMessageDelayed(HandleMsg.E_BLE_CMD_TIMEOUT, 3000);
                            retry--;
                        } else if (retry == 0) {
                            MyLog.i("E_BLE_CMD_TIMEOUT");
                            act.showCmdFailDialog();
                        }
                        break;

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

                    default:
                        break;
                }
            }
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                MyLog.e( "Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLeService.connect(StaticDataSave.mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            MyLog.i( "onServiceDisconnected");
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //[[add in v2.4.12
            MyLog.i("ACTION: " + action);
            //]]
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                MyLog.i("BluetoothLeService.ACTION_GATT_CONNECTED");
                mConnected = true;
                updateConnectionState(R.string.connected);
                btn_bleConnect.setText("DISCONNECT");

                //add in v2.3.4
                dismissConnectingDialog();
                //

                btn_bleConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mBluetoothLeService.disconnect();
                    }
                });

                mConnectionState.setTextColor(getResources().getColor(R.color.fbutton_color_green_sea));




            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                MyLog.i("BluetoothLeService.ACTION_GATT_DISCONNECTED");
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                btn_bleConnect.setText("CONNECT");
                //change to OnsingleClickListener in v2.3.6
                btn_bleConnect.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showConnectingDialog();
                                mBluetoothLeService.connect(StaticDataSave.mDeviceAddress);
                            }
                        });

                    }
                });
                mConnectionState.setTextColor(getResources().getColor(R.color.red));

                if (btn_apScan != null) {
                    btn_apScan.setEnabled(false);
                    btn_apScan.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                    /*btn_apScan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customToast = new CustomToast(mContext);
                            customToast.showToast(mContext, "Service discovery has not been completed yet", Toast.LENGTH_SHORT);
                        }
                    });*/
                }

                //add in v2.3.5
                if (btn_hiddenWiFi != null) {
                    btn_hiddenWiFi.setEnabled(false);
                    btn_hiddenWiFi.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                }
                //

                if (btn_command != null) {
                    btn_command.setEnabled(false);
                    btn_command.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                    /*btn_command.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customToast = new CustomToast(mContext);
                            customToast.showToast(mContext, "Service discovery has not been completed yet", Toast.LENGTH_SHORT);
                        }
                    });*/
                }

                if (btn_reset != null) {
                    btn_reset.setEnabled(false);
                    btn_reset.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                    /*btn_reset.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customToast = new CustomToast(mContext);
                            customToast.showToast(mContext, "Service discovery has not been completed yet", Toast.LENGTH_SHORT);
                        }
                    });*/
                }

                clearUI();
                initValue();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                MyLog.i("BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED");

                getGattServices(mBluetoothLeService.getSupportedGattServices());

                if (btn_apScan != null) {
                    //btn_apScan.setBackgroundColor(getResources().getColor(R.color.fbutton_default_color));

                    /*rl_scanAP.setVisibility(View.VISIBLE);
                    if (tv_noList.getVisibility() == View.VISIBLE) {
                        tv_noList.setVisibility(View.INVISIBLE);
                    }
                    ll_progressScanning.setVisibility(View.VISIBLE);
                    tv_progress.setText("0");
                    if (apRowItems != null && apRowItems.size() > 0) {
                        apRowItems.clear();
                        adapter.notifyDataSetChanged();
                    }
                    ll_selectAP.setVisibility(View.INVISIBLE);
                    ll_sendCommand.setVisibility(View.INVISIBLE);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //mHandler.sendEmptyMessageDelayed(HandleMsg.E_BLE_NETWORK_SCAN_TIMEOUT, 5000);
                    if (WIFI_SVC_WFACT_RES != null) {
                        mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
                    }
                    mHandler.sendEmptyMessageDelayed(HandleMsg.E_BLE_NETWORK_SCAN_TIMEOUT, 10000);

                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("dialog_cmd", "scan");
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    if (WIFI_SVC_WFCMD != null) {
                        mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
                    }

                    if (WIFI_SVC_WFACT_RES != null) {
                        mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
                    }
                    showScanningDialog();*/
                }


            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String value = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (StaticDataSave.device.equals("RRQ61400")) {
                    if(value == null) {
                        MyLog.e( "value is null");
                    } else {
                        MyLog.e("data = " + value);
                        if (value.equals("100")) {
                            mBluetoothLeService.readCharacteristic(DeviceControlActivity.WIFI_SVC_PROV_DATA);
                        } else {
                            mBluetoothLeService.readCharacteristic(DeviceControlActivity.WIFI_SVC_APSCAN_RES);
                        }
                    }
                } else {
                        if(value != null) {
                            MyLog.e("data = "+value.toString());
                        }
                        else {
                            MyLog.e( "value = null");
                        }
                }
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();

                        if (characteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_UUID))){
                            MyLog.d("== characteristic.getUuid() = WIFI_SVC_UUID ==");

                        }
                        if (characteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_WFCMD_UUID))){
                            MyLog.d("== characteristic.getUuid() = WIFI_SVC_WFCMD_UUID ==");

                        }
                        if (characteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_WFACT_RES_UUID))){
                            MyLog.d("== characteristic.getUuid() = WIFI_SVC_WFACT_RES_UUID ==");
                            /*mBluetoothLeService.readCharacteristic(characteristic);
                            if (characteristic.getValue() != null) {
                                MyLog.d("== characteristic.getValue() = "+characteristic.getValue().toString());
                            }*/
                        }

                        if (characteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_APSCAN_RES_UUID))){
                            MyLog.d("== characteristic.getUuid() = WIFI_SVC_APSCAN_RES_UUID ==");

                        }

                        if (characteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_PROV_DATA_UUID))){
                            MyLog.d("== characteristic.getUuid() = WIFI_SVC_PROV_DATA_UUID ==");
                        }

                        //add in v2.3.4
                        if (characteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_AWS_DATA_UUID))){
                            MyLog.d("== characteristic.getUuid() = WIFI_SVC_AWS_DATA_UUID ==");
                        }
                        //

                        //[[add in v2.3.17 for Azure
                        if (characteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_AZURE_DATA_UUID))){
                            MyLog.d("== characteristic.getUuid() = WIFI_SVC_AZURE_DATA_UUID ==");
                        }
                        //]]

                        //add in v2.3.7
                        if (characteristic.getUuid().equals(UUID.fromString(MyGattAttributes.GBG_SVC_UUID))){
                            MyLog.d("== characteristic.getUuid() = GBG_SVC_UUID ==");
                        }
                        if (characteristic.getUuid().equals(UUID.fromString(MyGattAttributes.GBG_CHAR_UUID))){
                            MyLog.d("== characteristic.getUuid() = GBG_CHAR_UUID ==");
                        }
                        //


                        return true;
                    }
                    return false;
                }
            };


    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        rl_scanAP.setVisibility(View.INVISIBLE);
        ll_selectAP.setVisibility(View.INVISIBLE);
        ll_sendCommand.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.da16600_activity_device_control);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        instance = this;
        mContext = this;
        mHandler = new DeviceControlHandler(this);
        customToast = new CustomToast(mContext);

        ((TextView) findViewById(R.id.device_address)).setText(StaticDataSave.mDeviceAddress);

        mConnectionState = (TextView) findViewById(R.id.connection_state);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        //change to OnsingleClickListener in v2.3.6
        iv_back.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });

        tv_deviceName = (TextView) findViewById(R.id.tv_deviceName);
        tv_deviceName.setText(StaticDataSave.mDeviceName);

        //StaticDataSave.thingName = StaticDataSave.mDeviceName;  //remove in v2.3.4

        btn_bleConnect = (FButton) findViewById(R.id.btn_bleConnect);

        //btn_dpmSet = (ImageButton) findViewById(R.id.btn_dpmSet);
        //btn_dpmSet.setOnClickListener(this);

        rl_scanAP = (RelativeLayout) findViewById(R.id.rl_scanAP);
        rl_scanAP.setVisibility(View.VISIBLE);

        ll_progressScanning = (LinearLayout) findViewById(R.id.ll_progressScanning);
        //[[change in v2.4.11
        //progressScanning = (com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.progressScanning);
        progressScanning = (ProgressBar) findViewById(R.id.progressScanning);
        //progressScanning.setIndicatorColor(getResources().getColor(R.color.blue3));
        //]]
        ll_progressScanning.setVisibility(View.INVISIBLE);

        tv_progress = (TextView) findViewById(R.id.tv_progress);

        tv_noList = (TextView) findViewById(R.id.tv_noList);
        tv_noList.setVisibility(View.INVISIBLE);

        listView = (ListView) findViewById(R.id.network_wifi_list);
        listView.setVisibility(View.INVISIBLE);

        ll_selectAP = (LinearLayout) findViewById(R.id.ll_selectAP);
        ll_selectAP.setVisibility(View.INVISIBLE);

        //ll_dpmSet = (LinearLayout) findViewById(R.id.ll_dpmSet);

        et_rawCommand1 = (EditText) findViewById(R.id.et_rawCommand1);
        et_rawCommand1.setMovementMethod(new ScrollingMovementMethod());

        et_rawCommand2 = (EditText) findViewById(R.id.et_rawCommand2);
        et_rawCommand2.setMovementMethod(new ScrollingMovementMethod());

        final SoftKeyboardDectectorView softKeyboardDecector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDecector, new LinearLayout.LayoutParams(-1, -1));
        softKeyboardDecector.setOnShownKeyboard(new SoftKeyboardDectectorView.OnShownKeyboardListener() {
            @Override
            public void onShowSoftKeyboard() {
                /*if (ll_dpmSet != null) {
                    ll_dpmSet.setVisibility(View.INVISIBLE);
                }*/
            }
        });
        softKeyboardDecector.setOnHiddenKeyboard(new SoftKeyboardDectectorView.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
                /*if (ll_dpmSet != null) {
                    ll_dpmSet.setVisibility(View.VISIBLE);
                }*/
            }
        });


        btn_connect = (FButton) findViewById(R.id.btn_connect);
        //change to OnsingleClickListener in v2.3.6
        btn_connect.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MyLog.i("btn_connect.setOnClickListener");
                JSONArray jsonArray1 = null;
                JSONArray jsonArray2 = null;
                try {

                    MyLog.i("et_rawCommand1.getText() = "+et_rawCommand1.getText());
                    MyLog.i("et_rawCommand2.getText() = "+et_rawCommand2.getText());

                    jsonArray1 = new JSONArray("["+et_rawCommand1.getText()+"]");
                    JSONObject jsonObj1 = jsonArray1.getJSONObject(0);
                    StaticDataSave.pingAddress = jsonObj1.getString("ping_addr");
                    StaticDataSave.svrAddress = jsonObj1.getString("svr_addr");
                    StaticDataSave.svrPort = jsonObj1.getInt("svr_port");
                    StaticDataSave.svrUrl = jsonObj1.getString("customer_svr_url");

                    jsonArray2 = new JSONArray("["+et_rawCommand2.getText()+"]");
                    JSONObject jsonObj2 = jsonArray2.getJSONObject(0);
                    StaticDataSave.networkSSID = jsonObj2.getString("SSID");
                    StaticDataSave.networkSecurityNum = jsonObj2.getInt("security_type");
                    StaticDataSave.networkPassword = jsonObj2.getString("password");
                    StaticDataSave.isHidden = jsonObj2.getInt("isHidden");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //[[change in v2.4.12
                //mHandler.sendEmptyMessageDelayed(HandleMsg.E_BLE_CMD_TIMEOUT, 3000);
                mHandler.sendEmptyMessageDelayed(HandleMsg.E_BLE_CMD_TIMEOUT, 3000);
                //]]
                sendNetworkinfo(StaticDataSave.pingAddress, StaticDataSave.svrAddress, StaticDataSave.svrPort, StaticDataSave.svrUrl);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (apRowItems.size() > 0) {

                    StaticDataSave.networkSSID = apRowItems.get(position).getSSID();
                    MyLog.i("ssid = " + StaticDataSave.networkSSID);

                    StaticDataSave.networkSecurityNum = apRowItems.get(position).getSecurityType();

                    MyLog.i("StaticDataSave.networkSecurityNum = " + apRowItems.get(position).getSecurityType());


                    if (btn_connect != null) {
                        btn_connect.setText("Connect to "+StaticDataSave.networkSSID);
                    }


                    if (StaticDataSave.networkSecurityNum > 0) {
                        InputPasswordDialog inputPasswordDialog = new InputPasswordDialog(mContext);
                        inputPasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        inputPasswordDialog.setCancelable(true);
                        inputPasswordDialog.getWindow().setGravity(Gravity.CENTER);
                        inputPasswordDialog.show();

                    } else {

                        rl_scanAP.setVisibility(View.INVISIBLE);
                        ll_selectAP.setVisibility(View.VISIBLE);

                        StaticDataSave.networkPassword = "";

                        displayNetworkinfo(
                                StaticDataSave.pingAddress,
                                StaticDataSave.svrAddress,
                                StaticDataSave.svrPort,
                                StaticDataSave.svrUrl
                        );

                        //add in v2.3.5
                        StaticDataSave.isHidden = 0;
                        //

                        //modify in v2.3.5
                        displayAPinfo(StaticDataSave.networkSSID,
                                StaticDataSave.networkSecurityNum,
                                StaticDataSave.networkPassword,
                                StaticDataSave.isHidden
                        );
                        //
                    }
                }
            }
        });

        btn_apScan = findViewById(R.id.btn_apScan);
        if (btn_apScan != null) {
            btn_apScan.setEnabled(false);
            btn_apScan.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
        }

        //add in v2.3.5
        btn_hiddenWiFi = findViewById(R.id.btn_hiddenWiFi);
        if (btn_hiddenWiFi != null) {
            btn_hiddenWiFi.setEnabled(false);
            btn_hiddenWiFi.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
        }
        //

        btn_reset = (FButton) findViewById(R.id.btn_reset);
        if (btn_reset != null) {
            btn_reset.setEnabled (false);
            btn_reset.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));

        }

        ll_sendCommand = (LinearLayout) findViewById(R.id.ll_sendCommand);
        ll_sendCommand.setVisibility(View.INVISIBLE);
        btn_command = (FButton) findViewById(R.id.btn_command);
        if (btn_command != null) {
            btn_command.setEnabled(false);
            btn_command.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
        }
        et_command = (EditText) findViewById(R.id.et_command);
        et_command.setText("");
        et_command.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        et_command.addTextChangedListener(this);
        btn_send = (FButton) findViewById(R.id.btn_send);
        //change to OnsingleClickListener in v2.3.6
        btn_send.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                String jsonCommand = et_command.getText().toString();

                //add in v2.3.4
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_command.getWindowToken(), 0);

                if (jsonCommand.contains("scan")) {

                    //[[add in v2.4.12
                    showScanningDialog();
                    //]]

                    btn_apScan.setEnabled(false);
                    btn_apScan.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));

                    //add in v2.3.5
                    btn_hiddenWiFi.setEnabled(false);
                    btn_hiddenWiFi.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                    //

                    btn_command.setEnabled(false);
                    btn_command.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                    btn_reset.setEnabled(false);
                    btn_reset.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                    rl_scanAP.setVisibility(View.VISIBLE);
                    if (tv_noList.getVisibility() == View.VISIBLE) {
                        tv_noList.setVisibility(View.INVISIBLE);
                    }
                    ll_progressScanning.setVisibility(View.VISIBLE);
                    tv_progress.setText("0");

                    if (ssidList != null) {
                        ssidList.clear();
                    }
                    if (securityList != null) {
                        securityList.clear();
                    }
                    if (signalList != null) {
                        signalList.clear();
                    }

                    if (apRowItems != null) { // && apRowItems.size() > 0) {
                        apRowItems.clear();
                        adapter.notifyDataSetChanged();
                    }
                    ll_selectAP.setVisibility(View.INVISIBLE);
                    ll_sendCommand.setVisibility(View.INVISIBLE);
                    mHandler.sendEmptyMessageDelayed(HandleMsg.E_BLE_NETWORK_SCAN_TIMEOUT, 20000);

                }
                //

                sendCommand(jsonCommand);

                //[[remove in v2.4.12
                //add in v2.3.4
                /*if (jsonCommand.contains("scan")) {

                    if (WIFI_SVC_WFACT_RES != null) {
                        mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
                    }
                    showScanningDialog();
                }*/
                //
                //]]



            }
        });

        //btn_disconnect = (FButton) findViewById(R.id.btn_disconnect);



    }

    @Override
    protected void onResume() {
        MyLog.i( "== onResume() ==");
        super.onResume();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //[[change in v2.4.11 for SecurityException
        //registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        mContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter(), RECEIVER_EXPORTED);
        //]]
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(StaticDataSave.mDeviceAddress);
            MyLog.d( "Connect request result=" + result);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mProgressReceiver, new IntentFilter("ProgressData")
        );

        /*if (fromSetup) {
            ll_selectAP.setVisibility(View.VISIBLE);
            displayAPinfo(StaticDataSave.networkSSID,
                    StaticDataSave.networkSecurityNum,
                    StaticDataSave.networkPassword,
                    pingAddress,
                    svrUrl,
                    sleepMode,
                    rtcTimer,
                    useDPM,
                    dpmKeepAlive,
                    userWakeup,
                    timWakeup
            );
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(et_rawCommand.getWindowToken(), 0);
            if (btn_connect != null) {
                btn_connect.setText("Connect to "+StaticDataSave.networkSSID);
            }
        }*/

    }

    @Override
    protected void onPause() {
        MyLog.i( "== onPause() ==");
        super.onPause();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        unregisterReceiver(mGattUpdateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mProgressReceiver);
    }

    @Override
    protected void onDestroy() {
        MyLog.i( "== onDestroy() ==");
        super.onDestroy();
        /*if (!fromSetup) {
            initValue();
        }*/
    }

    @Override
    public void onBackPressed() {
        initValue();
        Intent intent = new Intent(mContext, DeviceScanActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);

            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getGattServices(List<BluetoothGattService> gattServices) {
        //[[add in v2.4.12
        if (refresh) {
            refresh = false;
            mBluetoothLeService.refreshServices();
        }
        //]]
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, MyGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            MyLog.i("--------------------------------------------------------------------------");
            MyLog.i("currentServiceData = "+currentServiceData.get(LIST_NAME)+", "+currentServiceData.get(LIST_UUID));

            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();


            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);

                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, MyGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                MyLog.i("currentCharaData = "+currentCharaData.get(LIST_NAME)+", "+currentCharaData.get(LIST_UUID));
                //MyLog.i("currentCharaData = "+currentCharaData.get(LIST_UUID));
                gattCharacteristicGroupData.add(currentCharaData);

                if (gattCharacteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_UUID))) {
                    WIFI_SVC = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_WFCMD_UUID))) {
                    WIFI_SVC_WFCMD = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_WFACT_RES_UUID))) {
                    WIFI_SVC_WFACT_RES = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_APSCAN_RES_UUID))) {
                    WIFI_SVC_APSCAN_RES = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_PROV_DATA_UUID))) {
                    WIFI_SVC_PROV_DATA = gattCharacteristic;
                }
                //add in v2.3.4
                else if (gattCharacteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_AWS_DATA_UUID))) {
                    WIFI_SVC_AWS_DATA = gattCharacteristic;
                }
                //

                //add in v2.3.14
                else if (gattCharacteristic.getUuid().equals(UUID.fromString(MyGattAttributes.WIFI_SVC_AZURE_DATA_UUID))) {
                    WIFI_SVC_AZURE_DATA = gattCharacteristic;
                }
                //

                //add in v2.3.7
                else if (gattCharacteristic.getUuid().equals(UUID.fromString(MyGattAttributes.GBG_SVC_UUID))) {
                    GBG_SVC = gattCharacteristic;
                    MyLog.e("GBG_SVC exist!");

                } else if (gattCharacteristic.getUuid().equals(UUID.fromString(MyGattAttributes.GBG_CHAR_UUID))) {
                    GBG_SVC_CHAR = gattCharacteristic;
                    MyLog.e("GBG_SVC_CHAR exist!");
                }
                //
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);


        }

        //[[modify in v2.4.12
        //if (isRefreshed == false && GBG_SVC_CHAR != null) {
        if (refresh == true && isRefreshed == false && GBG_SVC_CHAR != null) {
            //]]
            MyLog.e("GBG_SVC_CHAR exist!");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GBG_SVC_CHAR = null;
            mGattCharacteristics = null;
            mBluetoothLeService.refreshServices();
            //add in v2.3.8
            isRefreshed = true;
            //
        }
        //

        //add in v2.3.4

        if (WIFI_SVC_AWS_DATA != null) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            showReceiveAwsThingNameDialog();

                            btn_apScan.setEnabled(false);
                            btn_apScan.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));

                            //add in v2.3.5
                            btn_hiddenWiFi.setEnabled(false);
                            btn_hiddenWiFi.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                            //

                            btn_command.setEnabled(false);
                            btn_command.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                            btn_reset.setEnabled(false);
                            btn_reset.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));

                            //add in v2.3.7
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //

                            sendGetThingNameCommand();
                        }
                    });
                }
            }).start();

        }

        //add in v2.3.14
        if (WIFI_SVC_AZURE_DATA != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showReceiveAzureThingNameDialog();
                            btn_apScan.setEnabled(false);
                            btn_apScan.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                            btn_hiddenWiFi.setEnabled(false);
                            btn_hiddenWiFi.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                            btn_command.setEnabled(false);
                            btn_command.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                            btn_reset.setEnabled(false);
                            btn_reset.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            sendGetThingNameCommand();
                        }
                    });
                }
            }).start();

        }/* else {
            MyLog.e("The device's SDK don't support AWS IoT or Azure IoT!");
        }*/
        //


        //add in v2.3.12
        if (WIFI_SVC_WFCMD != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (btn_apScan != null) {
                                btn_apScan.setEnabled(true);
                                btn_apScan.setButtonColor(getResources().getColor(R.color.fbutton_default_color));
                                //change to OnsingleClickListener in v2.3.6
                                btn_apScan.setOnClickListener(new OnSingleClickListener() {
                                    @Override
                                    public void onSingleClick(View v) {

                                        //add in v2.3.3
                                        btn_apScan.setEnabled(false);
                                        btn_apScan.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                                        btn_command.setEnabled(false);
                                        btn_command.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                                        btn_reset.setEnabled(false);
                                        btn_reset.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                                        //

                                        //add in v2.3.5
                                        btn_hiddenWiFi.setEnabled(false);
                                        btn_hiddenWiFi.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                                        //

                                        rl_scanAP.setVisibility(View.VISIBLE);
                                        if (tv_noList.getVisibility() == View.VISIBLE) {
                                            tv_noList.setVisibility(View.INVISIBLE);
                                        }
                                        ll_progressScanning.setVisibility(View.VISIBLE);
                                        tv_progress.setText("0");

                                        //add in v2.3.2
                                        if (ssidList != null) {
                                            ssidList.clear();
                                        }
                                        if (securityList != null) {
                                            securityList.clear();
                                        }
                                        if (signalList != null) {
                                            signalList.clear();
                                        }

                                        if (apRowItems != null) { // && apRowItems.size() > 0) {
                                            apRowItems.clear();
                                            adapter.notifyDataSetChanged();
                                        }
                                        ll_selectAP.setVisibility(View.INVISIBLE);
                                        ll_sendCommand.setVisibility(View.INVISIBLE);
                                        //[[modify in v2.4.12
                                        //mHandler.sendEmptyMessageDelayed(HandleMsg.E_BLE_NETWORK_SCAN_TIMEOUT, 20000);
                                        mHandler.sendEmptyMessageDelayed(HandleMsg.E_BLE_NETWORK_SCAN_TIMEOUT, 20000);
                                        //]]

                                        //[[add in v2.4.12
                                        if (StaticDataSave.device.equals("RRQ61400")) {
                                            if (WIFI_SVC_WFACT_RES != null) {
                                                //[[add in v2.4.12
                                                MyLog.i("SUBSCRIBE: WiFi Status");
                                                //]]
                                                mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
                                            }
                                        }
                                        //]]

                                        JSONObject obj = new JSONObject();
                                        try {
                                            obj.put("dialog_cmd", "scan");
                                        } catch(Exception e) {
                                            e.printStackTrace();
                                        }

                                        //[[modify in v2.4.12  /*setCharacteristicNotification before writeCharacteristic in case of rrq61400*/
                                        if (StaticDataSave.device.equals("RRQ61400")) {
                                            if (WIFI_SVC_WFCMD != null) {
                                                MyLog.i("WRITE: WiFi Command (scan)");
                                                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
                                            }
                                        } else {
                                            if (WIFI_SVC_WFCMD != null) {
                                                MyLog.i("WRITE: WiFi Command (scan)");
                                                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
                                            }

                                            if (WIFI_SVC_WFACT_RES != null) {
                                                MyLog.i("SUBSCRIBE: WiFi Status");
                                                mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
                                            }
                                        }
                                        //]]

                                        showScanningDialog();
                                    }
                                });
                            }

                            //add in v2.3.5
                            if (btn_hiddenWiFi != null) {
                                btn_hiddenWiFi.setEnabled(true);
                                btn_hiddenWiFi.setButtonColor(getResources().getColor(R.color.fbutton_default_color));
                                //change to OnsingleClickListener in v2.3.6
                                btn_hiddenWiFi.setOnClickListener(new OnSingleClickListener() {
                                    @Override
                                    public void onSingleClick(View v) {
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
                                });
                            }
                            //

                            if (btn_reset != null) {
                                btn_reset.setEnabled(true);
                                btn_reset.setButtonColor(getResources().getColor(R.color.fbutton_default_color));
                                //change to OnsingleClickListener in v2.3.6
                                btn_reset.setOnClickListener(new OnSingleClickListener() {
                                    @Override
                                    public void onSingleClick(View v) {
                                        JSONObject obj = new JSONObject();
                                        try {
                                            obj.put("dialog_cmd", "factory_reset");
                                        } catch(Exception e) {
                                            e.printStackTrace();
                                        }

                                        if (WIFI_SVC_WFCMD != null) {
                                            mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
                                        }

                                        if (btn_apScan != null) {
                                            btn_apScan.setEnabled(false);
                                            btn_apScan.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                    /*btn_apScan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customToast = new CustomToast(mContext);
                            customToast.showToast(mContext, "Service discovery has not been completed yet", Toast.LENGTH_SHORT);
                        }
                    });*/
                                        }

                                        //add in v2.3.5
                                        if (btn_hiddenWiFi != null) {
                                            btn_hiddenWiFi.setEnabled(false);
                                            btn_hiddenWiFi.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                                        }
                                        //

                                        if (btn_command != null) {
                                            btn_command.setEnabled(false);
                                            btn_command.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                    /*btn_command.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customToast = new CustomToast(mContext);
                            customToast.showToast(mContext, "Service discovery has not been completed yet", Toast.LENGTH_SHORT);
                        }
                    });*/
                                        }

                                        if (btn_reset != null) {
                                            btn_reset.setEnabled(false);
                                            btn_reset.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                    /*btn_reset.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customToast = new CustomToast(mContext);
                            customToast.showToast(mContext, "Service discovery has not been completed yet", Toast.LENGTH_SHORT);
                        }
                    });*/
                                        }
                                    }
                                });
                            }

                            if (btn_command != null) {
                                btn_command.setEnabled(true);
                                btn_command.setButtonColor(getResources().getColor(R.color.fbutton_default_color));
                                //change to OnsingleClickListener in v2.3.6
                                btn_command.setOnClickListener(new OnSingleClickListener() {
                                    @Override
                                    public void onSingleClick(View v) {
                                        rl_scanAP.setVisibility(View.INVISIBLE);
                                        ll_selectAP.setVisibility(View.INVISIBLE);
                                        ll_sendCommand.setVisibility(View.VISIBLE);
                                    }
                                });
                            }

                        }
                    });
                }
            }).start();

        }
        //



    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    public void updateAPList() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        ssid =  new String[ssidList.size()];
                        stringSecurity = new String[ssidList.size()];
                        signalBar = new Integer[ssidList.size()];
                        secMode = new Integer[ssidList.size()];
                        isSecurity = new boolean[ssidList.size()];
                        level = new Integer[ssidList.size()];
                        for(int i = 0; i < ssidList.size(); i++) {
                            if (securityList.get(i) == 1 || securityList.get(i) == 2 || securityList.get(i) == 3) {
                                ssid[i] = ssidList.get(i);
                                stringSecurity[i] = convertStringSecurity(securityList.get(i));
                                secMode[i] = securityList.get(i);
                                isSecurity[i] = true;
                                level[i] = signalList.get(i);
                                signalBar[i] = wifiSignalBar(isSecurity[i], level[i]);
                            } else if (securityList.get(i) == 0) {
                                ssid[i] = ssidList.get(i);
                                stringSecurity[i] = convertStringSecurity(securityList.get(i));
                                secMode[i] = securityList.get(i);
                                isSecurity[i] = false;
                                level[i] = signalList.get(i);
                                signalBar[i] = wifiSignalBar(isSecurity[i], level[i]);
                            }

                        }

                        apRowItems = new ArrayList<APRowItem>();

                        for (int i = 0; i < ssid.length; i++) {

                            if (StaticDataSave.thingName != null) {
                                if (!ssid[i].contains(StaticDataSave.thingName)) {
                                    APRowItem item = new APRowItem(signalBar[i], ssid[i], stringSecurity[i], secMode[i], level[i]);
                                    apRowItems.add(item);
                                }

                            } else {
                                APRowItem item = new APRowItem(signalBar[i], ssid[i], stringSecurity[i], secMode[i], level[i]);
                                apRowItems.add(item);
                            }
                        }

                        if (apRowItems != null && apRowItems.size() > 0) {
                            try {
                                mHandler.removeMessages(HandleMsg.E_BLE_NETWORK_SCAN_TIMEOUT);
                                adapter = new APListViewAdapter(getApplicationContext(), R.layout.da16600_ap_list_item, apRowItems);
                                listView.setAdapter(adapter);
                                listView.setVisibility(View.VISIBLE);
                                ll_progressScanning.setVisibility(View.INVISIBLE);
                                tv_noList.setVisibility(View.INVISIBLE);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        } else {
                            MyLog.e("apRowItems == null or apRowItems.size() < 0");
                        }
                    }
                });
            }
        }).start();

    }

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
        }
        return stringSecurity;
    }

    public int wifiSignalBar(boolean isSecurity, int level) {

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
    }



    public void sendNetworkinfo(String _pingAddress, String _svrAddress, int _svrPort, String _svrUrl) {

        final JSONObject obj = new JSONObject();
        try {
            obj.put("dialog_cmd", "network_info");
            obj.put("ping_addr", _pingAddress);
            obj.put("svr_addr", _svrAddress);
            obj.put("svr_port", _svrPort);
            obj.put("svr_url", _svrUrl);

            MyLog.i(">> sendNetworkinfo -> "+obj.toString());
            MyLog.i(">> sendNetworkinfo size = "+obj.toString().getBytes().length);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StaticDataSave.device.equals("RRQ61400")) {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
        } else {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
            if (WIFI_SVC_WFACT_RES != null) {
                mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
            }
        }
    }

    public void sendAPinfo(String _ssid, int _security, String _password, int _isHidden) {

        final JSONObject obj = new JSONObject();
        try {
            obj.put("dialog_cmd", "select_ap");
            obj.put("SSID", _ssid);
            obj.put("security_type", _security);
            obj.put("password", _password);
            obj.put("isHidden", _isHidden);


            MyLog.i(">> sendAPinfo -> "+obj.toString());
            MyLog.i(">> sendAPinfo size = "+obj.toString().getBytes().length);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StaticDataSave.device.equals("RRQ61400")) {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
        } else {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
            if (WIFI_SVC_WFACT_RES != null) {
                mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
            }
        }
    }

    public void sendDPMinfo(int _sleepMode, int _rtcTimer, int _useDPM, String _svrUrl, int _dpmKeepAlive, int _userWakeup, int _timWakeup) {

        final JSONObject obj = new JSONObject();
        try {
            obj.put("dialog_cmd", "set_dpm");
            obj.put("sleepMode",_sleepMode);

            if (_sleepMode == -1) {
                obj.put("rtcTimer",1740);
                obj.put("useDPM",1);
                obj.put("svr_url", _svrUrl);
                obj.put("dpmKeepAlive",30000);
                obj.put("userWakeup",0);
                obj.put("timWakeup",10);
            } else {
                obj.put("rtcTimer",_rtcTimer);
                obj.put("useDPM",_useDPM);
                obj.put("svr_url", _svrUrl);
                obj.put("dpmKeepAlive",_dpmKeepAlive);
                obj.put("userWakeup",_userWakeup);
                obj.put("timWakeup",_timWakeup);
            }

            MyLog.i(">> sendDPMinfo -> "+obj.toString());
            MyLog.i(">> sendDPMinfo size = "+obj.toString().getBytes().length);


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StaticDataSave.device.equals("RRQ61400")) {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
        } else {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
            if (WIFI_SVC_WFACT_RES != null) {
                mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
            }
        }

    }

    /*public void displayAPinfo(String _ssid, int _security, String _password, String _pingAddress, String _svrUrl,
                                int _sleepMode, int _rtcTimer, int _useDPM, int _dpmKeepAlive, int _userWakeup, int _timWakeup) {*/


    public void displayNetworkinfo(String _pingAddress, String _svrAddress, int _svrPort, String _svrUrl) {

        String result = "";
        String result_split = "";
        final JSONObject obj = new JSONObject();
        try {
            obj.put("dialog_cmd", "network_info");
            obj.put("ping_addr", _pingAddress);
            obj.put("svr_addr", _svrAddress);
            obj.put("svr_port", _svrPort);
            obj.put("customer_svr_url", _svrUrl);

            MyLog.i(">> displayNetworkinfo -> "+obj.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (obj != null) {
            result = obj.toString();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(result);
        result_split = gson.toJson(je);

        if (et_rawCommand1 != null) {
            et_rawCommand1.setText(result_split);
        }

    }

    public void displayAPinfo(String _ssid, int _security, String _password, int _isHidden) {

        String result = "";
        String result_split = "";
        final JSONObject obj = new JSONObject();
        try {
            obj.put("dialog_cmd", "select_ap");
            obj.put("SSID", _ssid);
            obj.put("security_type", _security);
            obj.put("password", _password);
            obj.put("isHidden", _isHidden);

            MyLog.i(">> displayAPinfo -> "+obj.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (obj != null) {
            result = obj.toString();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(result);
        result_split = gson.toJson(je);

        if (et_rawCommand2 != null) {
            et_rawCommand2.setText(result_split);
        }

    }

    //add in v2.3.4
    public void sendGetThingNameCommand() {
        MyLog.i(">> sendGetThingNameCommand()");
        final JSONObject obj = new JSONObject();
        try {
            obj.put("dialog_cmd", "get_thingName");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StaticDataSave.device.equals("RRQ61400")) {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
        } else {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
            if (WIFI_SVC_WFACT_RES != null) {
                mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
            }
        }


    }

    //add in v2.3.14


    public void sendGetModeCommand() {
        MyLog.i(">> sendGetModeCommand()");
        final JSONObject obj = new JSONObject();
        try {
            obj.put("dialog_cmd", "get_mode");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StaticDataSave.device.equals("RRQ61400")) {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
        } else {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
            if (WIFI_SVC_WFACT_RES != null) {
                mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
            }
        }

    }

    //

    //add in v2.3.14
    public void sendGetAzureConStringCommand() {
        MyLog.i(">> sendGetAzureConStringCommand()");
        final JSONObject obj = new JSONObject();
        try {
            obj.put("dialog_cmd", "get_azureConString");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StaticDataSave.device.equals("RRQ61400")) {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
        } else {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
            if (WIFI_SVC_WFACT_RES != null) {
                mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
            }
        }
    }
    //

    public void sendRebootCommand() {
        MyLog.i(">> sendRebootCommand()");
        final JSONObject obj = new JSONObject();
        try {
            obj.put("dialog_cmd", "reboot");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StaticDataSave.device.equals("RRQ61400")) {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
        } else {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
            if (WIFI_SVC_WFACT_RES != null) {
                mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
            }
        }
    }

    public void sendChkNetworkCommand() {

        final JSONObject obj = new JSONObject();
        try {
            obj.put("dialog_cmd", "chk_network");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StaticDataSave.device.equals("RRQ61400")) {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
        } else {
            if (WIFI_SVC_WFCMD != null) {
                mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, obj.toString());
            }
            if (WIFI_SVC_WFACT_RES != null) {
                mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
            }
        }

    }

    public void sendCommand(String command) {

        if (isJSONValid(command)) {
            JSONObject json = null;
            try {
                json = new JSONObject(command);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (StaticDataSave.device.equals("RRQ61400")) {
                if (WIFI_SVC_WFCMD != null) {
                    mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, json.toString());
                }
            } else {
                if (WIFI_SVC_WFCMD != null) {
                    mBluetoothLeService.writeCharacteristic(WIFI_SVC_WFCMD, json.toString());
                }
                if (WIFI_SVC_WFACT_RES != null) {
                    mBluetoothLeService.setCharacteristicNotification(WIFI_SVC_WFACT_RES, true);
                }
            }
        } else {
            customToast = new CustomToast(mContext);
            customToast.showToast(mContext, "The command contains invalid syntax", Toast.LENGTH_SHORT);
        }
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private BroadcastReceiver mProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String fromProgressStr = intent.getStringExtra("progress");
            MyLog.i("fromProgressStr = "+fromProgressStr);
            tv_progress.setText(fromProgressStr);
        }
    };

    private void initValue() {
        StaticDataSave.networkSSID = null;
        StaticDataSave.networkSecurity = false;
        StaticDataSave.networkSecurityNum = -1;
        StaticDataSave.networkPassword = null;

        dhcp = 1;
        StaticDataSave.pingAddress = "8.8.8.8";
        StaticDataSave.svrUrl = "www.google.com";

        /*sleepMode = -1;
        rtcDefault = 1;
        rtcTimer = 1740;
        useDPM = 1;
        dpmDefault = 1;
        dpmKeepAlive = 30000;
        userWakeup = 0;
        timWakeup = 10;*/

    }


    public AlertDialog scanFailDialog;
    public void showScanFailDialog() {

        dismissCheckingDialog();

        if (scanFailDialog != null ) {
            scanFailDialog.dismiss();
        }

        String title = "AP Scan Fail";
        String message = "AP scan failed.\nPlease try again.";
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom).create();
        dialog.setTitle(title);
        dialog.setIcon(R.mipmap.renesas_ic_launcher);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissScanFailDialog();
            }
        });
        dialog.setOnCancelListener(null);
        dialog.show();
        scanFailDialog = dialog;
    }

    public void dismissScanFailDialog() {
        if (scanFailDialog != null) {
            scanFailDialog.dismiss();
        }
    }

    public void showScanningDialog() {
        if (scanningDialog == null) {
            scanningDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            //scanningDialog.setTitle("Scanning the Wi-Fi network");
            scanningDialog.setMessage("Scanning the Wi-Fi network...");
            /*scanningDialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
            scanningDialog.getWindow().getAttributes().height = WindowManager.LayoutParams.WRAP_CONTENT;
            scanningDialog.setCanceledOnTouchOutside(false);*/

            scanningDialog.show();

           /* DisplayMetrics displayMetrics = new DisplayMetrics();
            DeviceControlActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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
            WindowManager.LayoutParams params = scanningDialog.getWindow().getAttributes();
            int dialogWindowWidth = (int) (displayWidth * 0.8f);
            params.width = dialogWindowWidth;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            scanningDialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
            scanningDialog.setCanceledOnTouchOutside(false);
            scanningDialog.setCancelable(false);  //add in v2.3.15
        }
    }

    public void dismissScanningDialog() {
        if (scanningDialog != null) {
            if (scanningDialog.isShowing()) {
                scanningDialog.dismiss();
                scanningDialog = null;
            }
        }
    }

    public void showCheckingDialog() {
        if (checkingDialog == null) {
            checkingDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            checkingDialog.setTitle("Checking the network connection");
            checkingDialog.setMessage("The device is checking the network connection.");
            /*checkingDialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
            checkingDialog.getWindow().getAttributes().height = WindowManager.LayoutParams.WRAP_CONTENT;
            checkingDialog.setCanceledOnTouchOutside(false);*/
            checkingDialog.show();

            /*DisplayMetrics displayMetrics = new DisplayMetrics();
            DeviceControlActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int displayWidth = displayMetrics.widthPixels;*/
            //modify in v2.3.15
            int displayWidth = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowMetrics windowMetrics = DeviceControlActivity.getInstance().getWindowManager().getCurrentWindowMetrics();
                Insets insets = windowMetrics.getWindowInsets()
                        .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
                displayWidth = windowMetrics.getBounds().width() - insets.left - insets.right;
            } else {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                DeviceControlActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                displayWidth = displayMetrics.widthPixels;
            }
            //
            WindowManager.LayoutParams params = checkingDialog.getWindow().getAttributes();
            int dialogWindowWidth = (int) (displayWidth * 0.8f);
            params.width = dialogWindowWidth;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            checkingDialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
            checkingDialog.setCanceledOnTouchOutside(false);
            checkingDialog.setCancelable(false);  //add in v2.3.15
        }

    }

    public void dismissCheckingDialog() {
        if (checkingDialog != null) {
            if (checkingDialog.isShowing()) {
                checkingDialog.dismiss();
                checkingDialog = null;
            }
        }
    }


    public void showTxApInfoFailDialog() {

        dismissCheckingDialog();

        if (txApInfoFailDialog != null) {
            txApInfoFailDialog.dismiss();
        }

        String title = "Failure to transmit AP information";
        String message = "Transmission of AP information failed.\nPlease check the device status.";
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom).create();
        dialog.setTitle(title);
        dialog.setIcon(R.mipmap.renesas_ic_launcher);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissTxApInfoFailDialog();

            }
        });
        dialog.setOnCancelListener(null);
        dialog.show();
        txApInfoFailDialog = dialog;
    }

    public void dismissTxApInfoFailDialog() {
        if (txApInfoFailDialog != null) {
            txApInfoFailDialog.dismiss();
        }
    }

    public void showApWrongPwdDialog() {

        dismissCheckingDialog();

        if (ApWorngPwdDialog != null) {
            ApWorngPwdDialog.dismiss();
        }

        String title = "Wrong password";
        String message = "The password of the Wi-Fi AP is incorrect.\n"+"Please try again.";
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom).create();
        dialog.setTitle(title);
        dialog.setIcon(R.mipmap.renesas_ic_launcher);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissApWorngPwdDialog();
                InputPasswordDialog inputPasswordDialog = new InputPasswordDialog(mContext);
                inputPasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                inputPasswordDialog.setCancelable(true);
                inputPasswordDialog.getWindow().setGravity(Gravity.CENTER);
                inputPasswordDialog.show();

            }
        });
        dialog.setOnCancelListener(null);
        dialog.show();
        ApWorngPwdDialog = dialog;
    }

    public void dismissApWorngPwdDialog() {
        if (ApWorngPwdDialog != null) {
            ApWorngPwdDialog.dismiss();
        }
    }

    // AP Connecttion Fail - 105
    public void showApFailDialog(String _ssid, String _password, String _security) {

        dismissCheckingDialog();

        if (apFailDialog != null) {
            apFailDialog.dismiss();
        }

        String title = "Network check result";
        String message = "\u25BA Connect to AP : Failure\n"+
                "\u25BA Check SSID or password\n\n"+
                "\u25BA SSID : "+_ssid+"\n"+"\u25BA Password : "+_password+"\n"+"\u25BA Security : "+_security;
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissApFailDialog();
                if (StaticDataSave.isHidden == 0) {
                    InputSsidPasswordDialog inputssidPasswordDialog = new InputSsidPasswordDialog(mContext);
                    inputssidPasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    inputssidPasswordDialog.setCancelable(true);
                    inputssidPasswordDialog.getWindow().setGravity(Gravity.CENTER);
                    inputssidPasswordDialog.show();
                } else if (StaticDataSave.isHidden == 1) {
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

            }
        });
        dialog.setOnCancelListener(null);
        dialog.show();
        apFailDialog = dialog;
    }

    public void dismissApFailDialog() {
        if (apFailDialog != null) {
            apFailDialog.dismiss();
        }
    }

    //DNS Fail, Server Fail - 106
    public void showDnsFailServerFailDialog(String _svrUrl, String _pingIp) {

        dismissCheckingDialog();

        if (dnsFailServerFailDialog != null) {
            dnsFailServerFailDialog.dismiss();
        }

        String title = "Network check result";
        String message = "\u25BA Get IP address from DNS : Failure\n" +
                "\u25BA Connect to Server : Failure\n"+
                "\u25BA No internet\n\n"+
                "\u25BA Customer Server URL : "+_svrUrl+"\n"+
                "\u25BA Ping IP : "+_pingIp+"\n"+
                "Are you sure you want to complete provisioning?";
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        //add RETRY in v2.3.6
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDnsFailServerFailDialog();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "COMPLETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDnsFailServerFailDialog();
                sendRebootCommand();
            }
        });
        dialog.setOnCancelListener(null);
        dialog.show();
        dnsFailServerFailDialog = dialog;
    }

    public void dismissDnsFailServerFailDialog() {
        if (dnsFailServerFailDialog != null) {
            dnsFailServerFailDialog.dismiss();
        }
    }

    //DNS Fail, Server OK - 107
    public void showDnsFailServerOkDialog(String _svrUrl, String _pingIp) {

        dismissCheckingDialog();

        if (dnsFailServerOkDialog != null) {
            dnsFailServerOkDialog.dismiss();
        }

        String title = "Network check result";
        String message = "\u25BA Get IP address from DNS : Failure\n" +
                "\u25BA Connect to Server : Success\n"+
                "\u25BA Wrong Customer Server URL\n\n"+
                "\u25BA Customer Server URL : "+_svrUrl+"\n"+
                "\u25BA Ping IP : "+_pingIp+"\n"+
                "Are you sure you want to complete provisioning?";
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        //modify in v2.3.6
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDnsFailServerOkDialog();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "COMPLETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDnsFailServerOkDialog();
                sendRebootCommand();
            }
        });
        /*dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDnsFailServerOkDialog();
            }
        });*/
        //
        dialog.setOnCancelListener(null);
        dialog.show();
        dnsFailServerOkDialog = dialog;
    }

    public void dismissDnsFailServerOkDialog() {
        if (dnsFailServerOkDialog != null) {
            dnsFailServerOkDialog.dismiss();
        }
    }

    //No URL, Ping Fail - 108
    public void showNoUrlPingFailDialog(String _pingIp) {

        dismissCheckingDialog();

        if (noUrlPingFailDialog != null) {
            noUrlPingFailDialog.dismiss();
        }

        String title = "Network check result";
        String message = "\u25BA No Customer Server URL\n" +
                "\u25BA Ping test : Failure\n\n" +
                "\u25BA Ping IP : "+_pingIp+"\n"+
                "Are you sure you want to complete provisioning?";
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        //add RETRY in v2.3.6
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissNoUrlPingFailDialog();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "COMPLETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissNoUrlPingFailDialog();
                sendRebootCommand();
            }
        });
        dialog.setOnCancelListener(null);
        dialog.show();
        noUrlPingFailDialog = dialog;
    }

    public void dismissNoUrlPingFailDialog() {
        if (noUrlPingFailDialog != null) {
            noUrlPingFailDialog.dismiss();
        }
    }


    //No URL, Ping OK - 109
    public void showNoUrlPingOkDialog(String _pingIp) {

        dismissCheckingDialog();

        if (noUrlPingOkDialog != null) {
            noUrlPingOkDialog.dismiss();
        }

        String title = "Network check result";
        String message = "\u25BA No Customer Server URL\n" +
                "\u25BA Ping test : Success\n\n" +
                "\u25BA Ping IP : "+_pingIp;
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "COMPLETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissNoUrlPingOkDialog();
                sendRebootCommand();
            }
        });
        dialog.setOnCancelListener(null);
        dialog.show();
        noUrlPingOkDialog = dialog;
    }

    public void dismissNoUrlPingOkDialog() {
        if (noUrlPingOkDialog != null) {
            noUrlPingOkDialog.dismiss();
        }
    }

    //DNS OK, Ping Fail, Server OK - 110
    public void showDnsOkPingFailServerOkDialog(String _svrUrl, String _svrIp, String _pingIp) {

        dismissCheckingDialog();

        if (dnsOkPingFailServerOkDialog != null) {
            dnsOkPingFailServerOkDialog.dismiss();
        }

        String title = "Network check result";
        String message = "\u25BA Get IP address from DNS : Success\n" +
                "\u25BA Ping test : Failure\n" +
                "\u25BA Connect to Server : Success\n"+
                "\u25BA AP gives wrong IP address\n\n"+
                "\u25BA Customer Server URL : "+_svrUrl+"\n"+
                "\u25BA Customer Server IP : "+_svrIp+"\n"+
                "\u25BA Ping IP : "+_pingIp;
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDnsOkPingFailServerOkDialog();

            }
        });
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "COMPLETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDnsOkPingFailServerOkDialog();
                sendRebootCommand();
            }
        });
        dialog.setOnCancelListener(null);
        dialog.show();
        dnsOkPingFailServerOkDialog = dialog;
    }

    public void dismissDnsOkPingFailServerOkDialog() {
        if (dnsOkPingFailServerOkDialog != null) {
            dnsOkPingFailServerOkDialog.dismiss();
        }
    }

    //DNS OK, Ping OK - 111
    public void showDnsOkPingOkDialog(String _svrUrl, String _svrIp) {

        dismissCheckingDialog();

        if (dnsOkPingOkDialog != null) {
            dnsOkPingOkDialog.dismiss();
        }

        String title = "Network check result";
        String message = "\u25BA Get IP address from DNS : Success\n" +
                "\u25BA Connect to customer server : Success\n\n"+
                "\u25BA Customer Server URL : "+_svrUrl+"\n"+
                "\u25BA Customer Server IP : "+_svrIp;
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "COMPLETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDnsOkPingOkDialog();
                sendRebootCommand();
            }
        });
        dialog.setOnCancelListener(null);
        dialog.show();
        dnsOkPingOkDialog = dialog;
    }

    public void dismissDnsOkPingOkDialog() {
        if (dnsOkPingOkDialog != null) {
            dnsOkPingOkDialog.dismiss();
        }
    }

    //DNS OK, Ping Fail, Server Fail - 113
    public void showDnsOkPingFailServerFailDialog(String _svrUrl, String _svrIp, String _pingIp) {

        dismissCheckingDialog();

        if (dnsOkPingFailServerFailDialog != null) {
            dnsOkPingFailServerFailDialog.dismiss();
        }

        String title = "Network check result";
        String message = "\u25BA Get IP address from DNS : Success\n" +
                "\u25BA Ping test : Failure\n" +
                "\u25BA Connect to Server : Failure\n\n"+
                "\u25BA Customer Server URL : "+_svrUrl+"\n"+
                "\u25BA Customer Server IP : "+_svrIp+"\n"+
                "\u25BA Ping IP : "+_pingIp+"\n"+
                "Are you sure you want to complete provisioning?";
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        //add RETRY in v2.3.6
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDnsOkPingFailServerFailDialog();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "COMPLETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDnsOkPingFailServerFailDialog();
                sendRebootCommand();
            }
        });
        dialog.setOnCancelListener(null);
        dialog.show();
        dnsOkPingFailServerFailDialog = dialog;
    }

    public void dismissDnsOkPingFailServerFailDialog() {
        if (dnsOkPingFailServerFailDialog != null) {
            dnsOkPingFailServerFailDialog.dismiss();
        }
    }

    public void showCmdFailDialog() {


        if (cmdFailDialog != null) {
            cmdFailDialog.dismiss();
        }

        String title = "Command send failure";
        //modify in v2.3.6
        String message = "No response received from device.\n"+
                "Please try again or check if the SDK version is 2.3.3.2 or higher.";
        //
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissCmdTimeoutDialog();
                Intent main = new Intent(DeviceControlActivity.this, MainActivity.class);
                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
                finishAffinity();

            }
        });
        dialog.setOnCancelListener(null);
        dialog.show();
        cmdFailDialog = dialog;
    }

    public void dismissCmdTimeoutDialog() {
        if (cmdFailDialog != null) {
            cmdFailDialog.dismiss();
        }
    }

    //add in v2.3.4
    public void showReceiveAwsThingNameDialog() {
        if (receiveAwsThingNameDialog == null) {
            receiveAwsThingNameDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            receiveAwsThingNameDialog.setMessage("Receiving Thing Name for AWS IoT...");
            /*receiveThingNameDialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
            receiveThingNameDialog.getWindow().getAttributes().height = WindowManager.LayoutParams.WRAP_CONTENT;
            receiveThingNameDialog.setCanceledOnTouchOutside(false);*/

            receiveAwsThingNameDialog.show();

            /*DisplayMetrics displayMetrics = new DisplayMetrics();
            DeviceControlActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int displayWidth = displayMetrics.widthPixels;*/
            //modify in v2.3.15
            int displayWidth = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowMetrics windowMetrics = DeviceControlActivity.getInstance().getWindowManager().getCurrentWindowMetrics();
                Insets insets = windowMetrics.getWindowInsets()
                        .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
                displayWidth = windowMetrics.getBounds().width() - insets.left - insets.right;
            } else {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                DeviceControlActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                displayWidth = displayMetrics.widthPixels;
            }
            //
            WindowManager.LayoutParams params = receiveAwsThingNameDialog.getWindow().getAttributes();
            int dialogWindowWidth = (int) (displayWidth * 0.8f);
            params.width = dialogWindowWidth;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            receiveAwsThingNameDialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
            receiveAwsThingNameDialog.setCanceledOnTouchOutside(false);
            receiveAwsThingNameDialog.setCancelable(false);
        }

    }

    public void dismissReceiveAwsThingNameDialog() {
        if (receiveAwsThingNameDialog != null) {
            if (receiveAwsThingNameDialog.isShowing()) {
                receiveAwsThingNameDialog.dismiss();
                receiveAwsThingNameDialog = null;
            }
        }
    }

    //[[add in v2.3.17 for Azure
    //add in v2.3.4
    public void showReceiveAzureThingNameDialog() {
        if (receiveAzureThingNameDialog == null) {
            receiveAzureThingNameDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            receiveAzureThingNameDialog.setMessage("Receiving Thing Name for Azure IoT...");
            /*receiveThingNameDialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
            receiveThingNameDialog.getWindow().getAttributes().height = WindowManager.LayoutParams.WRAP_CONTENT;
            receiveThingNameDialog.setCanceledOnTouchOutside(false);*/

            receiveAzureThingNameDialog.show();

            /*DisplayMetrics displayMetrics = new DisplayMetrics();
            DeviceControlActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int displayWidth = displayMetrics.widthPixels;*/
            //modify in v2.3.15
            int displayWidth = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowMetrics windowMetrics = DeviceControlActivity.getInstance().getWindowManager().getCurrentWindowMetrics();
                Insets insets = windowMetrics.getWindowInsets()
                        .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
                displayWidth = windowMetrics.getBounds().width() - insets.left - insets.right;
            } else {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                DeviceControlActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                displayWidth = displayMetrics.widthPixels;
            }
            //
            WindowManager.LayoutParams params = receiveAzureThingNameDialog.getWindow().getAttributes();
            int dialogWindowWidth = (int) (displayWidth * 0.8f);
            params.width = dialogWindowWidth;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            receiveAzureThingNameDialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
            receiveAzureThingNameDialog.setCanceledOnTouchOutside(false);
            receiveAzureThingNameDialog.setCancelable(false);
        }

    }

    public void dismissReceiveAzureThingNameDialog() {
        if (receiveAzureThingNameDialog != null) {
            if (receiveAzureThingNameDialog.isShowing()) {
                receiveAzureThingNameDialog.dismiss();
                receiveAzureThingNameDialog = null;
            }
        }
    }

    //]]

    public void showConnectingDialog() {
        if (connectingDialog == null) {
            connectingDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            connectingDialog.setMessage("Connecting Bluetooth Low Energy Device...");
            /*connectingDialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
            connectingDialog.getWindow().getAttributes().height = WindowManager.LayoutParams.WRAP_CONTENT;
            connectingDialog.setCanceledOnTouchOutside(false);*/

            connectingDialog.show();

            /*DisplayMetrics displayMetrics = new DisplayMetrics();
            DeviceControlActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int displayWidth = displayMetrics.widthPixels;*/
            //modify in v2.3.15
            int displayWidth = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowMetrics windowMetrics = DeviceControlActivity.getInstance().getWindowManager().getCurrentWindowMetrics();
                Insets insets = windowMetrics.getWindowInsets()
                        .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
                displayWidth = windowMetrics.getBounds().width() - insets.left - insets.right;
            } else {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                DeviceControlActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                displayWidth = displayMetrics.widthPixels;
            }
            //
            WindowManager.LayoutParams params = connectingDialog.getWindow().getAttributes();
            int dialogWindowWidth = (int) (displayWidth * 0.8f);
            params.width = dialogWindowWidth;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            connectingDialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
            connectingDialog.setCanceledOnTouchOutside(false);
            connectingDialog.setCancelable(false);  //add in v2.3.15
        }
    }

    public void dismissConnectingDialog() {
        if (connectingDialog != null) {
            if (connectingDialog.isShowing()) {
                connectingDialog.dismiss();
                connectingDialog = null;
            }
        }
    }
    //

}

class SoftKeyboardDectectorView extends View {

    private boolean mShownKeyboard;
    private OnShownKeyboardListener mOnShownSoftKeyboard;
    private OnHiddenKeyboardListener onHiddenSoftKeyboard;

    public SoftKeyboardDectectorView(Context context) {
        this(context, null);
    }

    public SoftKeyboardDectectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Activity activity = (Activity)getContext();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
        int diffHeight = (screenHeight - statusBarHeight) - h;
        if (diffHeight > 100 && !mShownKeyboard) {
            mShownKeyboard = true;
            onShownSoftKeyboard();
        } else if (diffHeight < 100 && mShownKeyboard) {
            mShownKeyboard = false;
            onHiddenSoftKeyboard();
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void onHiddenSoftKeyboard() {
        if (onHiddenSoftKeyboard != null)
            onHiddenSoftKeyboard.onHiddenSoftKeyboard();
    }

    public void onShownSoftKeyboard() {
        if (mOnShownSoftKeyboard != null)
            mOnShownSoftKeyboard.onShowSoftKeyboard();
    }

    public void setOnShownKeyboard(OnShownKeyboardListener listener) {
        mOnShownSoftKeyboard = listener;
    }

    public void setOnHiddenKeyboard(OnHiddenKeyboardListener listener) {
        onHiddenSoftKeyboard = listener;
    }

    public interface OnShownKeyboardListener {
        public void onShowSoftKeyboard();
    }

    public interface OnHiddenKeyboardListener {
        public void onHiddenSoftKeyboard();
    }
}
