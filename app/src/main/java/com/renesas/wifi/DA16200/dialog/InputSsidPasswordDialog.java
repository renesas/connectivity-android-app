package com.renesas.wifi.DA16200.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.renesas.wifi.DA16200.activity.SelectNetworkActivity;
import com.renesas.wifi.R;
import com.renesas.wifi.util.StaticDataSave;

public class InputSsidPasswordDialog extends Dialog implements TextWatcher {

    private Context context;
    private InputMethodManager imm;

    //UI resources
    private EditText ssidEditText;
    private EditText passwordEditText;
    private Button okButton;
    private View ssidContainer;
    private View passwordContainer;

    public InputSsidPasswordDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.da16200_dialog_input_ssid_password);

        imm = (InputMethodManager) SelectNetworkActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);

        ssidEditText = (EditText) findViewById(R.id.ssidEditText);
        ssidContainer = findViewById(R.id.ssid_container);
        ssidContainer.setVisibility(View.VISIBLE);
        if (ssidEditText != null) {
            ssidEditText.setText(StaticDataSave.networkSSID);
        }

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        okButton = (Button) findViewById(R.id.button_connect);
        okButton.setEnabled(false);
        passwordContainer = findViewById(R.id.password_container);

        passwordContainer.setVisibility(View.VISIBLE);

        passwordEditText.setText("");

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(ssidEditText, 0);
        inputMethodManager.showSoftInput(passwordEditText, 0);

        updateOkButtonState();

        //ssidEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);

        CheckBox cb = (CheckBox) findViewById(R.id.wifi_show_password);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    StaticDataSave.networkSecurity = true;
                } else {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    StaticDataSave.networkSecurity = false;
                }
            }
        });


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);

                dismiss();

                StaticDataSave.networkSSID = ssidEditText.getText().toString();
                StaticDataSave.networkPassword = passwordEditText.getText().toString();

                //add in v2.4.5
                if (StaticDataSave.networkPassword.length() == 0) {
                    StaticDataSave.networkSecurityNum = 0;
                } else {
                    StaticDataSave.networkSecurityNum = 3;
                }
                //]]

                //add in v2.3.5
                StaticDataSave.isHidden = 0;
                //

            }
        });

        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                StaticDataSave.networkSSID = null;
                StaticDataSave.networkSecurity = false;
                StaticDataSave.networkPassword = null;
                //[[add in v2.4.5
                StaticDataSave.networkSecurityNum = -1;
                //]]
                //initDpmSet();

            }
        });

    }


    // Text change listener methods
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //If the user chooses security as Open then make sure that the WiFi name is entered
        updateOkButtonState();
    }

    private void updateOkButtonState() {
        if (StaticDataSave.networkSecurity == true) {
            okButton.setEnabled(passwordEditText.getText().length() > 7);
        } else {
            okButton.setEnabled(true);
        }

    }


}
