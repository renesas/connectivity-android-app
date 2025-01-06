package com.renesas.wifi.DA16200.dialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;

import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.renesas.wifi.DA16200.activity.SelectNetworkActivity;
import com.renesas.wifi.R;
import com.renesas.wifi.util.MyLog;
import com.renesas.wifi.util.StaticDataSave;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class EnterpriseDialog extends Dialog implements TextWatcher {


    private Context context;
    private InputMethodManager imm;
    private TextView wifiName;
    //UI resources
    private EditText idEditText;
    private EditText passwordEditText;
    private Button okButton;
    private View idContainer;
    private View passwordContainer;

    //String[] auth_type_array = { "PEAP or TTLS or FAST (Recommend)", "PEAP", "TTLS", "FAST", "TLS" };
    String[] auth_type_array = { "PEAP or TTLS or FAST (Recommend)", "PEAP", "TTLS", "FAST" };
    String[] auth_protocol_array = { "MSCHAPv2 or GTC (Recommend)", "MSCHAPv2", "GTC" };
    String[] ca_array = { "Do not validate", "Find CA file" };
    private Spinner authTypeSpinner;
    private Spinner authProtocolSpinner;
    //private Spinner caSpinner;

    public EnterpriseDialog(Context context) {
        super(context, R.style.Dialog);
        //super(context);
        this.context = context;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.da16200_dialog_enterprise);
        wifiName = (TextView) findViewById(R.id.wifiName);
        wifiName.setText(StaticDataSave.networkSSID);

        authTypeSpinner = findViewById(R.id.auth_type_spinner);
        ArrayAdapter<String> authTypeAdapter = new ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_item, auth_type_array);
        authTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        authTypeSpinner.setSelection(0);
        authTypeSpinner.setAdapter(authTypeAdapter);
        authTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //StaticDataSave.enterpriseAuthType = auth_type_array[position];
                StaticDataSave.enterpriseAuthType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        authProtocolSpinner = findViewById(R.id.auth_protocol_spinner);
        ArrayAdapter<String> authProtocolAdapter = new ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_item, auth_protocol_array);
        authProtocolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        authProtocolSpinner.setSelection(0);
        authProtocolSpinner.setAdapter(authProtocolAdapter);
        authProtocolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //StaticDataSave.enterpriseAuthProtocol = auth_protocol_array[position];
                StaticDataSave.enterpriseAuthProtocol = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*caSpinner = findViewById(R.id.ca_spinner);
        ArrayAdapter<String> caAdapter = new ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_item, ca_array);
        caAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caSpinner.setSelection(0);
        caSpinner.setAdapter(caAdapter);
        caSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position ==1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(SelectNetworkActivity.getInstance().mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SelectNetworkActivity.getInstance(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_PERMISSION_READ_STORAGE);
                        } else {
                            readFileFromDownloads();
                        }
                    } else {
                        readFileFromDownloads();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        imm = (InputMethodManager) SelectNetworkActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);

        idEditText = (EditText) findViewById(R.id.idEditText);
        idEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        idContainer = findViewById(R.id.id_container);
        idContainer.setVisibility(View.VISIBLE);
        /*if (idEditText != null) {
            idEditText.setText(StaticDataSave.enterpriseID);
        }*/

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        okButton = (Button) findViewById(R.id.button_connect);
        okButton.setEnabled(false);
        passwordContainer = findViewById(R.id.password_container);

        passwordContainer.setVisibility(View.VISIBLE);

        passwordEditText.setText("");

        //
        imm.hideSoftInputFromWindow(idEditText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(idEditText, 0);
        inputMethodManager.showSoftInput(passwordEditText, 0);

        //updateOkButtonState();

        //ssidEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);

                dismiss();

                StaticDataSave.enterpriseID = idEditText.getText().toString();
                StaticDataSave.enterprisePassword = passwordEditText.getText().toString();

                //add in v2.3.5
                StaticDataSave.isHidden = 0;
                //

                SelectNetworkActivity.getInstance().sendDPMSet();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                StaticDataSave.isHidden = 0;


                SelectNetworkActivity.getInstance().sendEnterpriseConfig(
                        StaticDataSave.networkSSID,
                        StaticDataSave.networkSecurityNum,
                        StaticDataSave.isHidden,
                        StaticDataSave.serverURL,
                        StaticDataSave.enterpriseAuthType,
                        StaticDataSave.enterpriseAuthProtocol,
                        StaticDataSave.enterpriseID,
                        StaticDataSave.enterprisePassword);
            }
        });

        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                StaticDataSave.networkSSID = null;
                StaticDataSave.networkSecurity = false;
                StaticDataSave.networkSecurityNum = -1;
                StaticDataSave.isHidden = -1;

                StaticDataSave.enterpriseAuthType = -1;
                StaticDataSave.enterpriseAuthProtocol = -1;
                StaticDataSave.enterpriseID = null;
                StaticDataSave.enterprisePassword = null;
            }
        });



    }


    // Text change listener methods
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateOkButtonState();
    }

    @Override
    public void afterTextChanged(Editable s) {
        //If the user chooses security as Open then make sure that the WiFi name is entered

    }

    private void updateOkButtonState() {
        MyLog.i("updateOkButtonState()");
        okButton.setEnabled(true);
    }

    /*public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File file = new File(filePath);
        FileInputStream fin = new FileInputStream(file);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }*/

}
