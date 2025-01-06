package com.renesas.wifi.awsiot;


import android.content.Context;
import android.content.SharedPreferences;

import com.amazonaws.regions.Regions;
import com.renesas.wifi.activity.MainActivity;
import com.renesas.wifi.buildOption;
import com.renesas.wifi.util.MyLog;
import com.renesas.wifi.util.StaticDataSave;


public class AWSConfig {

    private static Context appContext;
    private static AWSConfig awsConfigInstance;
    static UserConfig userConfig;

    public static String ThingPreString;
    public static String CUSTOMER_SPECIFIC_ENDPOINT;
    public static String AWS_IOT_POLICY_NAME;
    public static Regions MY_REGION;
    public static String COGNITO_POOL_REGION;
    public static String BUCKET_REGION;
    //[[remove in v2.3.16
    /*public static String CertificateKey;
    public static String PrivateKey;
    public static String CertificateArn;*/
    //]]


    public static AWSConfig getInstance() {
        if (awsConfigInstance == null)
            awsConfigInstance = new AWSConfig();
        return awsConfigInstance;
    }

    public static void setConfig() {
        appContext = MainActivity.mContext;
        if (appContext != null) {
            userConfig = new UserConfig();
            StaticDataSave.saveData = appContext.getSharedPreferences(StaticDataSave.mSharedPreferencesName, Context.MODE_PRIVATE);
            StaticDataSave.thingName = StaticDataSave.saveData.getString(StaticDataSave.thingNameKey, null);

            //modify in v2.3.9
            if (buildOption.region.equals("ap-northeast-2")) {
                StaticDataSave.region = StaticDataSave.saveData.getString(StaticDataSave.regionKey, "ap-northeast-2");
            } else if (buildOption.region.equals("eu-west-2")) {
                StaticDataSave.region = StaticDataSave.saveData.getString(StaticDataSave.regionKey, "eu-west-2");  //modify for Keith
            } else if (buildOption.region.equals("us-west-2")) {
                StaticDataSave.region = StaticDataSave.saveData.getString(StaticDataSave.regionKey, "us-west-2");
            }
            //

            SharedPreferences.Editor editor = StaticDataSave.saveData.edit();

            if (StaticDataSave.thingName != null) {

                if (StaticDataSave.region != null) {
                    if (StaticDataSave.region.contains("ap-northeast-2")) {
                        //ThingPreString = userConfig.AP_NORTHEAST_2_ThingPreString;
                        CUSTOMER_SPECIFIC_ENDPOINT = userConfig.AP_NORTHEAST_2_SPECIFIC_ENDPOINT;
                        AWS_IOT_POLICY_NAME = userConfig.AP_NORTHEAST_2_AWS_IOT_POLICY_NAME;
                        MY_REGION = userConfig.AP_NORTHEAST_2_MY_REGION;
                        COGNITO_POOL_REGION = userConfig.AP_NORTHEAST_2_COGNITO_POOL_REGION;
                        BUCKET_REGION = userConfig.AP_NORTHEAST_2_BUCKET_REGION;
                        //[[remove in v2.3.16
                        /*CertificateKey = userConfig.AP_NORTHEAST_2_CertificateKey;
                        PrivateKey = userConfig.AP_NORTHEAST_2_PrivateKey;
                        CertificateArn = userConfig.AP_NORTHEAST_2_CertificateArn;*/
                        //]]
                    } else if (StaticDataSave.region.contains("us-west-2")) {
                        //ThingPreString = userConfig.US_WEST_2_ThingPreString;
                        CUSTOMER_SPECIFIC_ENDPOINT = userConfig.US_WEST_2_SPECIFIC_ENDPOINT;
                        AWS_IOT_POLICY_NAME = userConfig.US_WEST_2_AWS_IOT_POLICY_NAME;
                        MY_REGION = userConfig.US_WEST_2_MY_REGION;
                        COGNITO_POOL_REGION = userConfig.US_WEST_2_COGNITO_POOL_REGION;
                        BUCKET_REGION = userConfig.US_WEST_2_BUCKET_REGION;
                        //[[remove in v2.3.16
                        /*CertificateKey = userConfig.US_WEST_2_CertificateKey;
                        PrivateKey = userConfig.US_WEST_2_PrivateKey;
                        CertificateArn = userConfig.US_WEST_2_CertificateArn;*/
                        //]]
                    }
                    //add for Keith
                    else if (StaticDataSave.region.contains("eu-west-2")) {
                        //ThingPreString = userConfig.US_WEST_2_ThingPreString;
                        CUSTOMER_SPECIFIC_ENDPOINT = userConfig.EU_WEST_2_SPECIFIC_ENDPOINT;
                        AWS_IOT_POLICY_NAME = userConfig.EU_WEST_2_AWS_IOT_POLICY_NAME;
                        MY_REGION = userConfig.EU_WEST_2_MY_REGION;
                        COGNITO_POOL_REGION = userConfig.EU_WEST_2_COGNITO_POOL_REGION;
                        BUCKET_REGION = userConfig.EU_WEST_2_BUCKET_REGION;
                        //[[remove in v2.3.16
                        /*CertificateKey = userConfig.EU_WEST_2_CertificateKey;
                        PrivateKey = userConfig.EU_WEST_2_PrivateKey;
                        CertificateArn = userConfig.EU_WEST_2_CertificateArn;*/
                        //]]
                    }
                    //
                }

                //[[add in v2.3.16 for single cognito pool id
                StaticDataSave.cognitoPoolId = userConfig.COGNITO_POOL_ID;
                //]]

                if (StaticDataSave.thingName.equals("PST-DOORLOCK-1")) {
                    //StaticDataSave.cognitoPoolId = userConfig.PST_COGNITO_POOL_ID_1;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_1;
                } else if (StaticDataSave.thingName.equals("PST-DOORLOCK-2")) {
                    //StaticDataSave.cognitoPoolId = userConfig.PST_COGNITO_POOL_ID_2;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_2;
                } else if (StaticDataSave.thingName.equals("PST-DOORLOCK-3")) {
                    //StaticDataSave.cognitoPoolId = userConfig.PST_COGNITO_POOL_ID_3;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_3;
                } else if (StaticDataSave.thingName.equals("PST-DOORLOCK-4")) {
                    //StaticDataSave.cognitoPoolId = userConfig.PST_COGNITO_POOL_ID_4;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_4;
                } else if (StaticDataSave.thingName.equals("PST-DOORLOCK-5")) {
                    //StaticDataSave.cognitoPoolId = userConfig.PST_COGNITO_POOL_ID_5;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;

                } else if (StaticDataSave.thingName.equals("FAE-DOORLOCK-1")) {
                    //StaticDataSave.cognitoPoolId = userConfig.FAE_COGNITO_POOL_ID_1;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.FAE_BUCKET_NAME_1;
                } else if (StaticDataSave.thingName.equals("FAE-DOORLOCK-2")) {
                    //StaticDataSave.cognitoPoolId = userConfig.FAE_COGNITO_POOL_ID_2;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.FAE_BUCKET_NAME_2;
                } else if (StaticDataSave.thingName.equals("FAE-DOORLOCK-3")) {
                    //StaticDataSave.cognitoPoolId = userConfig.FAE_COGNITO_POOL_ID_3;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.FAE_BUCKET_NAME_3;
                } else if (StaticDataSave.thingName.equals("FAE-DOORLOCK-4")) {
                    //StaticDataSave.cognitoPoolId = userConfig.FAE_COGNITO_POOL_ID_4;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.FAE_BUCKET_NAME_4;

                } else if (StaticDataSave.thingName.equals("PAE-DOORLOCK-1")) {
                    //StaticDataSave.cognitoPoolId = userConfig.PAE_COGNITO_POOL_ID_1;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PAE_BUCKET_NAME_1;

                } else if (StaticDataSave.thingName.equals("NDT-DOORLOCK-1")) {
                    //StaticDataSave.cognitoPoolId = userConfig.NDT_COGNITO_POOL_ID_1;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.NDT_BUCKET_NAME_1;
                } else if (StaticDataSave.thingName.equals("NDT-DOORLOCK-2")) {
                    //StaticDataSave.cognitoPoolId = userConfig.NDT_COGNITO_POOL_ID_2;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.NDT_BUCKET_NAME_2;
                } else if (StaticDataSave.thingName.equals("NDT-DOORLOCK-3")) {
                    //StaticDataSave.cognitoPoolId = userConfig.NDT_COGNITO_POOL_ID_3;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.NDT_BUCKET_NAME_3;

                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-1")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_1;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_1;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-2")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_2;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_2;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-3")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_3;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_3;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-4")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_4;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_4;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-5")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_5;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-6")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_6;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_6;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-7")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_7;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_7;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-8")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_8;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_8;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-9")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_9;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_9;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-10")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_10;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_10;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-11")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_11;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_11;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-12")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_12;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_12;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-13")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_13;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_13;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-14")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_14;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_14;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-15")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_15;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_15;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-16")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_16;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_16;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-17")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_17;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_17;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-18")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_18;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_18;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-19")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_19;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_19;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-20")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_20;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_20;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-21")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_21;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_21;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-22")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_22;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_22;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-23")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_23;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_23;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-24")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_24;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_24;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-25")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_25;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_25;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-26")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_26;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_26;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-27")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_27;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_27;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-28")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_28;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_28;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-29")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_29;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_29;
                } else if (StaticDataSave.thingName.equals("EVB-DOORLOCK-30")) {
                    //StaticDataSave.cognitoPoolId = userConfig.EVB_COGNITO_POOL_ID_30;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.EVB_BUCKET_NAME_30;

                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-1")) {
                     //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_1;  //remove in v2.3.16 for single cognito pool id
                     StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_1;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-2")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_2;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_2;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-3")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_3;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_3;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-4")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_4;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_4;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-5")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_5;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-6")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_6;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_6;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-7")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_7;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_7;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-8")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_8;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_8;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-9")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_9;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_9;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-10")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_10;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_10;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-11")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_11;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_11;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-12")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_12;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_12;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-13")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_13;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_13;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-14")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_14;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_14;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-15")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_15;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_15;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-16")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_16;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_16;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-17")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_17;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_17;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-18")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_18;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_18;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-19")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_19;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_19;
                } else if (StaticDataSave.thingName.equals("DIALOG-DOORLOCK-20")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_COGNITO_POOL_ID_20;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_BUCKET_NAME_20;

                } else if (StaticDataSave.thingName.equals("THING-USA-1")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_1;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_1;
                } else if (StaticDataSave.thingName.equals("THING-USA-2")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_2;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_2;
                } else if (StaticDataSave.thingName.equals("THING-USA-3")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_3;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_3;
                } else if (StaticDataSave.thingName.equals("THING-USA-4")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_4;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_4;
                } else if (StaticDataSave.thingName.equals("THING-USA-5")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_5;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("THING-USA-6")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_6;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_6;
                } else if (StaticDataSave.thingName.equals("THING-USA-7")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_7;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_7;
                } else if (StaticDataSave.thingName.equals("THING-USA-8")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_8;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_8;
                } else if (StaticDataSave.thingName.equals("THING-USA-9")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_9;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_9;
                } else if (StaticDataSave.thingName.equals("THING-USA-10")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_10;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_10;
                } else if (StaticDataSave.thingName.equals("THING-USA-11")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_11;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_11;
                } else if (StaticDataSave.thingName.equals("THING-USA-12")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_12;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_12;
                } else if (StaticDataSave.thingName.equals("THING-USA-13")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_13;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_13;
                } else if (StaticDataSave.thingName.equals("THING-USA-14")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_14;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_14;
                } else if (StaticDataSave.thingName.equals("THING-USA-15")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_15;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_15;
                } else if (StaticDataSave.thingName.equals("THING-USA-16")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_16;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_16;
                } else if (StaticDataSave.thingName.equals("THING-USA-17")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_17;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_17;
                } else if (StaticDataSave.thingName.equals("THING-USA-18")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_18;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_18;
                } else if (StaticDataSave.thingName.equals("THING-USA-19")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_19;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_19;
                } else if (StaticDataSave.thingName.equals("THING-USA-20")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_20;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_20;
                } else if (StaticDataSave.thingName.equals("THING-USA-21")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_21;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_21;
                } else if (StaticDataSave.thingName.equals("THING-USA-22")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_22;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_22;
                } else if (StaticDataSave.thingName.equals("THING-USA-23")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_23;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_23;
                } else if (StaticDataSave.thingName.equals("THING-USA-24")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_24;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_24;
                } else if (StaticDataSave.thingName.equals("THING-USA-25")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_25;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_25;
                } else if (StaticDataSave.thingName.equals("THING-USA-26")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_26;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_26;
                } else if (StaticDataSave.thingName.equals("THING-USA-27")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_27;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_27;
                } else if (StaticDataSave.thingName.equals("THING-USA-28")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_28;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_28;
                } else if (StaticDataSave.thingName.equals("THING-USA-29")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_29;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_29;
                } else if (StaticDataSave.thingName.equals("THING-USA-30")) {
                    //StaticDataSave.cognitoPoolId = userConfig.USA_COGNITO_POOL_ID_30;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.USA_BUCKET_NAME_30;
                }

                else if (StaticDataSave.thingName.equals("IOT-SENSOR-1")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_1;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-2")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_2;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-3")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_3;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-4")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_4;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-5")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_5;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-6")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_6;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-7")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_7;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-8")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_8;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-9")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_9;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-10")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_10;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-11")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_11;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-12")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_12;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-13")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_13;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-14")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_14;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-15")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_15;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-16")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_16;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-17")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_17;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-18")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_18;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-19")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_19;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-20")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_20;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-21")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_21;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-22")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_22;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-23")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_23;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-24")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_24;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-25")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_25;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-26")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_26;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-27")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_27;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-28")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_28;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-29")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_29;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-30")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_30;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-31")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_31;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-32")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_32;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-33")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_33;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-34")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_34;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-35")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_35;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-36")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_36;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-37")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_37;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-38")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_38;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-39")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_39;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-40")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_40;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-41")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_41;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-42")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_42;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-43")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_43;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-44")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_44;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-45")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_45;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-46")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_46;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-47")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_47;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-48")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_48;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-49")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_49;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("IOT-SENSOR-50")) {
                    //StaticDataSave.cognitoPoolId = userConfig.SENSOR_COGNITO_POOL_ID_50;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                }

                else if (StaticDataSave.thingName.equals("DIALOG-IOT-1")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_1;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-2")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_2;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-3")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_3;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-4")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_4;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-5")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_5;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-6")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_6;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-7")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_7;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-8")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_8;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-9")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_9;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-10")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_10;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-11")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_11;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-12")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_12;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-13")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_13;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-14")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_14;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-15")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_15;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-16")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_16;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-17")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_17;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-18")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_18;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-19")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_19;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-20")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_20;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-21")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_21;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-22")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_22;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-23")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_23;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-24")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_24;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-25")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_25;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-26")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_26;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-27")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_27;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-28")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_28;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-29")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_29;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-30")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_30;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-31")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_31;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-32")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_32;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-33")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_33;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-34")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_34;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-35")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_35;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-36")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_36;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-37")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_37;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-38")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_38;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-39")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_39;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                } else if (StaticDataSave.thingName.equals("DIALOG-IOT-40")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_IOT_COGNITO_POOL_ID_40;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.PST_BUCKET_NAME_5;
                }

                //add for Keith
                else if (StaticDataSave.thingName.equals("DialogDoorLock")) {
                    //StaticDataSave.cognitoPoolId = userConfig.DIALOG_DOORLOCK_COGNITO_POOL_ID;  //remove in v2.3.16 for single cognito pool id
                    StaticDataSave.bucketName = userConfig.DIALOG_DOORLOCK_BUCKET_NAME;
                }
                //

                MyLog.i("StaticDataSave.cognitoPoolId = "+StaticDataSave.cognitoPoolId);
                MyLog.i("StaticDataSave.bucketName = "+StaticDataSave.bucketName);
                editor.putString(StaticDataSave.cognitoPoolIdKey, StaticDataSave.cognitoPoolId);
                editor.putString(StaticDataSave.bucketNameKey, StaticDataSave.bucketName);
                editor.commit();
            }
        }

    }


}
