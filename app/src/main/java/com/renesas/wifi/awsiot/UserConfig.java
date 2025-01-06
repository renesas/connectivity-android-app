package com.renesas.wifi.awsiot;

import com.amazonaws.regions.Regions;

public class UserConfig {

    /*
     * 1. Filter for Device Provisioning - removed

     * 2. Customer specific IoT endpoint
     *    AWS Iot CLI describe-endpoint call returns: XXXXXXXXXX.iot.<region>.amazonaws.com

     * 3. Cognito pool ID. For this app, pool needs to be unauthenticated pool with AWS IoT permissions.

     * 4. Name of the AWS IoT policy to attach to a newly created certificate

     * 5. Thing of AWS IoT - get from device by provisioning

     * 6. you must first create a bucket using the S3 console before running the app (https://console.aws.amazon.com/s3/).
     *    After creating a bucket, put it's name in the field below.
     */

    //public static final String AP_NORTHEAST_2_ThingPreString = "DOORLOCK";  //1
    //public static final String US_WEST_2_ThingPreString = "THING";  //1

    public static final String AP_NORTHEAST_2_SPECIFIC_ENDPOINT = "a1kzdt4nun8bnh-ats.iot.ap-northeast-2.amazonaws.com";  //2 Seoul
    //[[livetest for user manual - 231023
    //public static final String AP_NORTHEAST_2_SPECIFIC_ENDPOINT = "a2opinf5su4jb-ats.iot.ap-northeast-2.amazonaws.com";
    //]]
    public static final String US_WEST_2_SPECIFIC_ENDPOINT = "a1kzdt4nun8bnh-ats.iot.us-west-2.amazonaws.com";  //2 Oregon
    public static final String EU_WEST_2_SPECIFIC_ENDPOINT = "a2tv87mmdfuuy6-ats.iot.eu-west-2.amazonaws.com";  //2 London  add for Keith
    public static final String Keywe_SPECIFIC_ENDPOINT = "a1wlpzfjhtxhyu-ats.iot.us-east-1.amazonaws.com";  //2  for Keywe

    public static final String AP_NORTHEAST_2_AWS_IOT_POLICY_NAME = "DOORLOCK_POLICY";  //4
    //[[livetest for user manual - 231023
    //public static final String AP_NORTHEAST_2_AWS_IOT_POLICY_NAME = "MyTestPolicy";
    //]]
    public static final String US_WEST_2_AWS_IOT_POLICY_NAME = "USA_POLICY";  //4
    public static final String EU_WEST_2_AWS_IOT_POLICY_NAME = "MyPolicy";  //4  add for Keith

    /*
     * 7. Region of AWS IoT
     */
    public static final Regions AP_NORTHEAST_2_MY_REGION = Regions.AP_NORTHEAST_2;
    public static final Regions US_WEST_2_MY_REGION = Regions.US_WEST_2;
    public static final Regions EU_WEST_2_MY_REGION = Regions.EU_WEST_2;  //add for Keith
    public static final Regions Keywe_MY_REGION = Regions.US_EAST_1;  //add for Keith


    /*
     * 8. Region of your Cognito identity pool ID.
     */
    public static final String AP_NORTHEAST_2_COGNITO_POOL_REGION = "ap-northeast-2";
    public static final String US_WEST_2_COGNITO_POOL_REGION = "us-west-2";
    public static final String EU_WEST_2_COGNITO_POOL_REGION = "eu-west-2";  //add for Keith
    public static final String Keywe_COGNITO_POOL_REGION = "us-east-1";  // for Keywe

    //[[add in v2.3.16 for single Cognito pool id (Identify-fleet-provision-demo)
    public static final String COGNITO_POOL_ID = "ap-northeast-2:b414964c-595d-4db5-aa3a-babe9dc24f96";
    //[[livetest for user manual - 231023
    //public static final String COGNITO_POOL_ID = "ap-northeast-2:2cbc2697-8f4a-486d-9aef-0c9c613c3825";
    //]]

    /*
     * 9 Region of your bucket.
     */
    public static final String AP_NORTHEAST_2_BUCKET_REGION = "ap-northeast-2";
    public static final String US_WEST_2_BUCKET_REGION = "us-west-2";
    public static final String EU_WEST_2_BUCKET_REGION = "eu-west-2";  //add for Keith
    public static final String Keywe_BUCKET_REGION = "us-east-1";  // for Keywe

    // PST-DOORLOCK
    //[[remove in v2.3.16 for single Cognito pool id
    //public static final String PST_COGNITO_POOL_ID_1 = "ap-northeast-2:9e9e5d60-29c2-46cf-9045-efd7a03e6941";  //3
    //public static final String PST_COGNITO_POOL_ID_2 = "ap-northeast-2:fbefbf61-824c-41cc-b991-5d9f1c6182e5";  //3
    //public static final String PST_COGNITO_POOL_ID_3 = "ap-northeast-2:d6ec4f97-2ee0-4a29-a01a-5ddf6735a992";  //3
    //public static final String PST_COGNITO_POOL_ID_4 = "ap-northeast-2:9aa51cbd-807e-4c67-919c-7d52ae8523c4";  //3
    //public static final String PST_COGNITO_POOL_ID_5 = "ap-northeast-2:e3ab84ad-c5d6-4f15-9758-2d0f84534bda";  //3
    //]]
    public static final String PST_BUCKET_NAME_1 = "bucket-pst-doorlock-1";  //6
    public static final String PST_BUCKET_NAME_2 = "bucket-pst-doorlock-2";  //6
    public static final String PST_BUCKET_NAME_3 = "bucket-pst-doorlock-3";  //6
    public static final String PST_BUCKET_NAME_4 = "bucket-pst-doorlock-4";  //6
    public static final String PST_BUCKET_NAME_5 = "bucket-pst-doorlock-5";  //6

    // FAE-DOORLOCK
    //[[remove in v2.3.16 for single Cognito pool id
    //public static final String FAE_COGNITO_POOL_ID_1 = "ap-northeast-2:4620130a-20ff-4fa4-bfd1-e9463a373bc5";  //3
    //public static final String FAE_COGNITO_POOL_ID_2 = "ap-northeast-2:6900218e-2f63-4977-8920-8984172b2938";  //3
    //public static final String FAE_COGNITO_POOL_ID_3 = "ap-northeast-2:910fca1a-4d6b-49e0-975b-250b11cb8789";  //3
    //public static final String FAE_COGNITO_POOL_ID_4 = "ap-northeast-2:114ed487-42a0-426d-8fb4-d120960ea7b4";  //3
    //]]
    public static final String FAE_BUCKET_NAME_1 = "bucket-fae-doorlock-1";  //6
    public static final String FAE_BUCKET_NAME_2 = "bucket-fae-doorlock-2";  //6
    public static final String FAE_BUCKET_NAME_3 = "bucket-fae-doorlock-3";  //6
    public static final String FAE_BUCKET_NAME_4 = "bucket-fae-doorlock-4";  //6

    // PAE-DOORLOCK
    //[[remove in v2.3.16 for single Cognito pool id
    //public static final String PAE_COGNITO_POOL_ID_1 = "ap-northeast-2:88d26ff3-d514-4a81-9f6d-899d68193fbe";  //3
    //]]
    public static final String PAE_BUCKET_NAME_1 = "bucket-pae-doorlock-1";  //6

    // NDT-DOORLOCK
    //[[remove in v2.3.16 for single Cognito pool id
    //public static final String NDT_COGNITO_POOL_ID_1 = "ap-northeast-2:fc1d6fee-c51f-4dd5-9a3c-3a92710d6cbc";  //3
    //public static final String NDT_COGNITO_POOL_ID_2 = "ap-northeast-2:0631723e-cb56-4c2a-bdc4-e7b464c1234b";  //3
    //public static final String NDT_COGNITO_POOL_ID_3 = "ap-northeast-2:3e28d8d1-7c35-466b-8bb8-064532ae8377";  //3
    //]]
    public static final String NDT_BUCKET_NAME_1 = "bucket-ndt-doorlock-1";  //6
    public static final String NDT_BUCKET_NAME_2 = "bucket-ndt-doorlock-2";  //6
    public static final String NDT_BUCKET_NAME_3 = "bucket-ndt-doorlock-3";  //6

    // USA
    //[[remove in v2.3.16 for single Cognito pool id
    //public static final String USA_COGNITO_POOL_ID_1  = "us-west-2:b0bbba85-da50-4203-892a-0982ad8fbf56";  //3
    //public static final String USA_COGNITO_POOL_ID_2  = "us-west-2:47197ed9-2d61-49cc-adc6-aac3451f60cb";  //3
    //public static final String USA_COGNITO_POOL_ID_3  = "us-west-2:d8d37c7b-1dc2-4671-a70c-b8eecc291d19";  //3
    //public static final String USA_COGNITO_POOL_ID_4  = "us-west-2:a6341ba0-9fa1-46ee-b276-a98dc3261967";  //3
    //public static final String USA_COGNITO_POOL_ID_5  = "us-west-2:71db65a5-df8a-470e-bfa5-47f622e15233";  //3
    //public static final String USA_COGNITO_POOL_ID_6  = "us-west-2:0a72af53-1ed1-452b-ac7b-1626af1f467d";  //3
    //public static final String USA_COGNITO_POOL_ID_7  = "us-west-2:7ddda163-4bd1-4415-8bbc-1e58b5e73f64";  //3
    //public static final String USA_COGNITO_POOL_ID_8  = "us-west-2:c1d6670a-4333-4add-ad39-6d276d452b4b";  //3
    //public static final String USA_COGNITO_POOL_ID_9  = "us-west-2:a63d0636-8938-4278-a58b-2178d356a582";  //3
    //public static final String USA_COGNITO_POOL_ID_10 = "us-west-2:6a2f8df1-ca3f-4eae-90b5-bb5b9f50daab";  //3
    //public static final String USA_COGNITO_POOL_ID_11 = "us-west-2:5bb6dd2a-0325-4630-a886-3794e9630362";  //3
    //public static final String USA_COGNITO_POOL_ID_12 = "us-west-2:da6ba9ea-166a-4002-abf2-af4f0c33a18e";  //3
    //public static final String USA_COGNITO_POOL_ID_13 = "us-west-2:02f010be-b793-4287-93c4-9331794bbde2";  //3
    //public static final String USA_COGNITO_POOL_ID_14 = "us-west-2:517244d2-9cfc-4a08-ba7e-84709e48db06";  //3
    //public static final String USA_COGNITO_POOL_ID_15 = "us-west-2:1b3aae81-b201-4214-94c8-c88aee18efa0";  //3
    //public static final String USA_COGNITO_POOL_ID_16 = "us-west-2:b2e033d2-777e-4b3b-a223-9c44d60da136";  //3
    //public static final String USA_COGNITO_POOL_ID_17 = "us-west-2:14c14a0c-571a-4de8-afa3-39c9d065af96";  //3
    //public static final String USA_COGNITO_POOL_ID_18 = "us-west-2:9fbcbeef-1b6a-4125-a3f2-2c32e775aa0b";  //3
    //public static final String USA_COGNITO_POOL_ID_19 = "us-west-2:e6d2a5f6-48d7-40cb-8748-0f1dac6474cc";  //3
    //public static final String USA_COGNITO_POOL_ID_20 = "us-west-2:15bfa1e8-1fa5-4bd0-8f1b-009f1c2ba743";  //3
    //public static final String USA_COGNITO_POOL_ID_21 = "us-west-2:a45c304c-a830-4a64-a769-b3beacf0ff1c";  //3
    //public static final String USA_COGNITO_POOL_ID_22 = "us-west-2:e099d9c1-82ba-4599-82e0-fcd8a38b9f52";  //3
    //public static final String USA_COGNITO_POOL_ID_23 = "us-west-2:feab3ea0-10d9-4d12-8e04-58232db6e975";  //3
    //public static final String USA_COGNITO_POOL_ID_24 = "us-west-2:ef4dd7a4-28b7-46c3-a884-31f27649895e";  //3
    //public static final String USA_COGNITO_POOL_ID_25 = "us-west-2:0849679a-838f-4733-9656-a437cfc4741d";  //3
    //public static final String USA_COGNITO_POOL_ID_26 = "us-west-2:0d77f476-1969-45c2-96d4-52c5aa3878f1";  //3
    //public static final String USA_COGNITO_POOL_ID_27 = "us-west-2:bc6677bd-eb8f-48ae-b34c-a5e0c527becb";  //3
    //public static final String USA_COGNITO_POOL_ID_28 = "us-west-2:52aca408-d5e0-4eb2-bca6-885cc5589bfd";  //3
    //public static final String USA_COGNITO_POOL_ID_29 = "us-west-2:5d13c9ae-66ad-4f3c-9542-6813a2dad85f";  //3
    //public static final String USA_COGNITO_POOL_ID_30 = "us-west-2:195e3f9d-d6a6-46ae-8fbc-f222420b515d";  //3
    //]]
    public static final String USA_BUCKET_NAME_1  = "bucket-usa-1";  //6
    public static final String USA_BUCKET_NAME_2  = "bucket-usa-2";  //6
    public static final String USA_BUCKET_NAME_3  = "bucket-usa-3";  //6
    public static final String USA_BUCKET_NAME_4  = "bucket-usa-4";  //6
    public static final String USA_BUCKET_NAME_5  = "bucket-usa-5";  //6
    public static final String USA_BUCKET_NAME_6  = "bucket-usa-6";  //6
    public static final String USA_BUCKET_NAME_7  = "bucket-usa-7";  //6
    public static final String USA_BUCKET_NAME_8  = "bucket-usa-8";  //6
    public static final String USA_BUCKET_NAME_9  = "bucket-usa-9";  //6
    public static final String USA_BUCKET_NAME_10 = "bucket-usa-10";  //6
    public static final String USA_BUCKET_NAME_11 = "bucket-usa-11";  //6
    public static final String USA_BUCKET_NAME_12 = "bucket-usa-12";  //6
    public static final String USA_BUCKET_NAME_13 = "bucket-usa-13";  //6
    public static final String USA_BUCKET_NAME_14 = "bucket-usa-14";  //6
    public static final String USA_BUCKET_NAME_15 = "bucket-usa-15";  //6
    public static final String USA_BUCKET_NAME_16 = "bucket-usa-16";  //6
    public static final String USA_BUCKET_NAME_17 = "bucket-usa-17";  //6
    public static final String USA_BUCKET_NAME_18 = "bucket-usa-18";  //6
    public static final String USA_BUCKET_NAME_19 = "bucket-usa-19";  //6
    public static final String USA_BUCKET_NAME_20 = "bucket-usa-20";  //6
    public static final String USA_BUCKET_NAME_21 = "bucket-usa-21";  //6
    public static final String USA_BUCKET_NAME_22 = "bucket-usa-22";  //6
    public static final String USA_BUCKET_NAME_23 = "bucket-usa-23";  //6
    public static final String USA_BUCKET_NAME_24 = "bucket-usa-24";  //6
    public static final String USA_BUCKET_NAME_25 = "bucket-usa-25";  //6
    public static final String USA_BUCKET_NAME_26 = "bucket-usa-26";  //6
    public static final String USA_BUCKET_NAME_27 = "bucket-usa-27";  //6
    public static final String USA_BUCKET_NAME_28 = "bucket-usa-28";  //6
    public static final String USA_BUCKET_NAME_29 = "bucket-usa-29";  //6
    public static final String USA_BUCKET_NAME_30 = "bucket-usa-30";  //6


    // EVB-DOORLOCK
    //[[remove in v2.3.16 for single Cognito pool id
    //public static final String EVB_COGNITO_POOL_ID_1 = "ap-northeast-2:6ad0ac29-c677-443b-bd69-ccbc095999e4";  //3
    //public static final String EVB_COGNITO_POOL_ID_2 = "ap-northeast-2:15e0b87b-e400-4a10-8b85-eac58fcf71c3";  //3
    //public static final String EVB_COGNITO_POOL_ID_3 = "ap-northeast-2:c7206e15-58b2-47c6-b526-ad1493a1f951";  //3
    //public static final String EVB_COGNITO_POOL_ID_4 = "ap-northeast-2:f5e8ce82-6cc0-4413-9d46-8aa6970fb092";  //3
    //public static final String EVB_COGNITO_POOL_ID_5 = "ap-northeast-2:840463fe-214e-4962-aea6-e8b11eb5eb98";  //3
    //public static final String EVB_COGNITO_POOL_ID_6 = "ap-northeast-2:62efb2c0-1483-4e24-8b7d-23c2c00221b9";  //3
    //public static final String EVB_COGNITO_POOL_ID_7 = "ap-northeast-2:9ebb0246-64eb-4b63-ad41-7967807d2d53";  //3
    //public static final String EVB_COGNITO_POOL_ID_8 = "ap-northeast-2:cd30bff3-ebb8-4321-bf56-13bf55f59187";  //3
    //public static final String EVB_COGNITO_POOL_ID_9 = "ap-northeast-2:c35907f7-120a-4afe-8f5d-6a892901e9d2";  //3
    //public static final String EVB_COGNITO_POOL_ID_10 = "ap-northeast-2:49fdc7b7-6927-4053-937c-256ab7bdc7ac";  //3
    //public static final String EVB_COGNITO_POOL_ID_11 = "ap-northeast-2:ad293673-c17b-4953-9ff2-713d02bdd834";  //3
    //public static final String EVB_COGNITO_POOL_ID_12 = "ap-northeast-2:1e67e98e-967a-4c9c-abe6-9e56cc956b32";  //3
    //public static final String EVB_COGNITO_POOL_ID_13 = "ap-northeast-2:615d941b-63d6-4ec0-bd12-0c9500dd8a87";  //3
    //public static final String EVB_COGNITO_POOL_ID_14 = "ap-northeast-2:70d449ac-c46c-4c86-aa2c-ab8411189f42";  //3
    //public static final String EVB_COGNITO_POOL_ID_15 = "ap-northeast-2:a323cacc-6585-4caa-91b0-58e5700c2078";  //3
    //public static final String EVB_COGNITO_POOL_ID_16 = "ap-northeast-2:e639a511-c0a7-4985-9a21-4f2658fe2ca6";  //3
    //public static final String EVB_COGNITO_POOL_ID_17 = "ap-northeast-2:c62513f8-e699-4002-b04d-3876b8d70d06";  //3
    //public static final String EVB_COGNITO_POOL_ID_18 = "ap-northeast-2:ab52ae37-d50c-4181-ad06-271da6223fbc";  //3
    //public static final String EVB_COGNITO_POOL_ID_19 = "ap-northeast-2:8ddc20d6-7560-4200-83c7-dfdf84850db5";  //3
    //public static final String EVB_COGNITO_POOL_ID_20 = "ap-northeast-2:64a3f18a-86ae-4b47-b32b-582193366691";  //3
    //public static final String EVB_COGNITO_POOL_ID_21 = "ap-northeast-2:39a09736-6594-4214-a1b3-7707401fa2d2";  //3
    //public static final String EVB_COGNITO_POOL_ID_22 = "ap-northeast-2:a67ba995-be46-4c67-aa88-fa6489b0bb74";  //3
    //public static final String EVB_COGNITO_POOL_ID_23 = "ap-northeast-2:4fadb88c-9b98-4b73-9bf9-ddb12a9b45e4";  //3
    //public static final String EVB_COGNITO_POOL_ID_24 = "ap-northeast-2:d5121d96-7f06-46c5-a700-6f77d240f2a7";  //3
    //public static final String EVB_COGNITO_POOL_ID_25 = "ap-northeast-2:d74b0843-e631-4155-a756-17b056eb6297";  //3
    //public static final String EVB_COGNITO_POOL_ID_26 = "ap-northeast-2:7c4da3ad-0ffc-4071-93c0-04700c7563f4";  //3
    //public static final String EVB_COGNITO_POOL_ID_27 = "ap-northeast-2:7420c6b7-6c43-441e-bfc1-c5588789bd64";  //3
    //public static final String EVB_COGNITO_POOL_ID_28 = "ap-northeast-2:6a2b396a-0d2a-4fc2-a6e3-096807ca1fd0";  //3
    //public static final String EVB_COGNITO_POOL_ID_29 = "ap-northeast-2:23b179e6-c095-4bd9-bbc0-5d1e31e820c1";  //3
    //public static final String EVB_COGNITO_POOL_ID_30 = "ap-northeast-2:1d42c384-aa4c-45db-83c3-1a1d04aa7218";  //3
    //]]
    public static final String EVB_BUCKET_NAME_1 = "bucket-evb-doorlock-1";  //6
    public static final String EVB_BUCKET_NAME_2 = "bucket-evb-doorlock-2";  //6
    public static final String EVB_BUCKET_NAME_3 = "bucket-evb-doorlock-3";  //6
    public static final String EVB_BUCKET_NAME_4 = "bucket-evb-doorlock-4";  //6
    public static final String EVB_BUCKET_NAME_5 = "bucket-evb-doorlock-5";  //6
    public static final String EVB_BUCKET_NAME_6 = "bucket-evb-doorlock-6";  //6
    public static final String EVB_BUCKET_NAME_7 = "bucket-evb-doorlock-7";  //6
    public static final String EVB_BUCKET_NAME_8 = "bucket-evb-doorlock-8";  //6
    public static final String EVB_BUCKET_NAME_9 = "bucket-evb-doorlock-9";  //6
    public static final String EVB_BUCKET_NAME_10 = "bucket-evb-doorlock-10";  //6
    public static final String EVB_BUCKET_NAME_11 = "bucket-evb-doorlock-11";  //6
    public static final String EVB_BUCKET_NAME_12 = "bucket-evb-doorlock-12";  //6
    public static final String EVB_BUCKET_NAME_13 = "bucket-evb-doorlock-13";  //6
    public static final String EVB_BUCKET_NAME_14 = "bucket-evb-doorlock-14";  //6
    public static final String EVB_BUCKET_NAME_15 = "bucket-evb-doorlock-15";  //6
    public static final String EVB_BUCKET_NAME_16 = "bucket-evb-doorlock-16";  //6
    public static final String EVB_BUCKET_NAME_17 = "bucket-evb-doorlock-17";  //6
    public static final String EVB_BUCKET_NAME_18 = "bucket-evb-doorlock-18";  //6
    public static final String EVB_BUCKET_NAME_19 = "bucket-evb-doorlock-19";  //6
    public static final String EVB_BUCKET_NAME_20 = "bucket-evb-doorlock-20";  //6
    public static final String EVB_BUCKET_NAME_21 = "bucket-evb-doorlock-21";  //6
    public static final String EVB_BUCKET_NAME_22 = "bucket-evb-doorlock-22";  //6
    public static final String EVB_BUCKET_NAME_23 = "bucket-evb-doorlock-23";  //6
    public static final String EVB_BUCKET_NAME_24 = "bucket-evb-doorlock-24";  //6
    public static final String EVB_BUCKET_NAME_25 = "bucket-evb-doorlock-25";  //6
    public static final String EVB_BUCKET_NAME_26 = "bucket-evb-doorlock-26";  //6
    public static final String EVB_BUCKET_NAME_27 = "bucket-evb-doorlock-27";  //6
    public static final String EVB_BUCKET_NAME_28 = "bucket-evb-doorlock-28";  //6
    public static final String EVB_BUCKET_NAME_29 = "bucket-evb-doorlock-29";  //6
    public static final String EVB_BUCKET_NAME_30 = "bucket-evb-doorlock-30";  //6


    // DIALOG-DOORLOCK
    //[[remove in v2.3.16 for single Cognito pool id
    //public static final String DIALOG_COGNITO_POOL_ID_1 = "ap-northeast-2:264c271c-edc2-4589-b169-544350904df1";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_2 = "ap-northeast-2:6a11e777-897e-4769-8173-0c5c3d144a4e";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_3 = "ap-northeast-2:9ac4b0e9-b92e-4954-be40-4f8fdfc5bd9c";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_4 = "ap-northeast-2:21b76553-a058-4dca-ab8d-e7851921eebf";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_5 = "ap-northeast-2:bf87913e-9c5a-43a1-968e-9bf0d150fe97";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_6 = "ap-northeast-2:ed72cb90-ea3e-406f-8a08-ce3cb703c19d";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_7 = "ap-northeast-2:3175b279-3d46-4bfa-be3a-a28e056a7fcc";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_8 = "ap-northeast-2:adfddcfa-bd3a-4e24-9d6b-32e502361a26";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_9 = "ap-northeast-2:ed57b152-b0c4-40b9-a455-2feb01a73854";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_10 = "ap-northeast-2:51ded748-207b-4c85-bfee-2d68fffd2a58";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_11 = "ap-northeast-2:cac7275e-1ac3-49a8-a8c9-8d8a70217889";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_12 = "ap-northeast-2:9bfcb18c-7d5d-476d-bc4e-96019558276e";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_13 = "ap-northeast-2:baa60288-1484-4bff-864b-4be59cb3a749";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_14 = "ap-northeast-2:cb5161d7-f169-4f50-a99a-f84a8342e92f";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_15 = "ap-northeast-2:4b51232d-d218-4016-a451-9d5e3659c193";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_16 = "ap-northeast-2:24199593-0ce7-4d42-8e35-6c2ed25d36d9";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_17 = "ap-northeast-2:97a1d522-b0a1-40e9-8c1d-426e4ea2b136";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_18 = "ap-northeast-2:e30810e9-e2f1-4f9a-95f7-33c920f5eb10";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_19 = "ap-northeast-2:5fe7f5d6-07e6-47dd-a33b-e86320ee13e5";  //3
    //public static final String DIALOG_COGNITO_POOL_ID_20 = "ap-northeast-2:5a41de03-8560-4054-92e2-e2874e4255ce";  //3
    //]]
    public static final String DIALOG_BUCKET_NAME_1 = "";  //6
    public static final String DIALOG_BUCKET_NAME_2 = "";  //6
    public static final String DIALOG_BUCKET_NAME_3 = "";  //6
    public static final String DIALOG_BUCKET_NAME_4 = "";  //6
    public static final String DIALOG_BUCKET_NAME_5 = "";  //6
    public static final String DIALOG_BUCKET_NAME_6 = "";  //6
    public static final String DIALOG_BUCKET_NAME_7 = "";  //6
    public static final String DIALOG_BUCKET_NAME_8 = "";  //6
    public static final String DIALOG_BUCKET_NAME_9 = "";  //6
    public static final String DIALOG_BUCKET_NAME_10 = "";  //6
    public static final String DIALOG_BUCKET_NAME_11 = "";  //6
    public static final String DIALOG_BUCKET_NAME_12 = "";  //6
    public static final String DIALOG_BUCKET_NAME_13 = "";  //6
    public static final String DIALOG_BUCKET_NAME_14 = "";  //6
    public static final String DIALOG_BUCKET_NAME_15 = "";  //6
    public static final String DIALOG_BUCKET_NAME_16 = "";  //6
    public static final String DIALOG_BUCKET_NAME_17 = "";  //6
    public static final String DIALOG_BUCKET_NAME_18 = "";  //6
    public static final String DIALOG_BUCKET_NAME_19 = "";  //6
    public static final String DIALOG_BUCKET_NAME_20 = "";  //6

    // SENSOR
    //[[remove in v2.3.16 for single Cognito pool id
    //public static final String SENSOR_COGNITO_POOL_ID_1  = "ap-northeast-2:d1022182-e4f7-452c-9388-e094ab53dec3";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_2  = "ap-northeast-2:14a82a78-993a-4223-81f6-7066c22c4cc7";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_3  = "ap-northeast-2:d18a8f8c-80e5-4dbb-87d1-8aa6552bd01d";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_4  = "ap-northeast-2:5c3656de-8947-4a6a-b258-ad26fc17e5d2";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_5  = "ap-northeast-2:d5bc04a2-9d34-44c8-b2fc-7c4908d85052";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_6  = "ap-northeast-2:4a476109-2090-4eda-83ee-3bfb0ea76b15";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_7  = "ap-northeast-2:702eb712-2b5c-4bdc-9441-42ad4eb7ab4b";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_8  = "ap-northeast-2:598e83f0-4aaa-408f-b9bb-f96f322e3997";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_9  = "ap-northeast-2:cb0af620-b500-4873-95d3-0f0324c43e44";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_10 = "ap-northeast-2:200b3811-df42-4903-afd3-f9a67c7212a0";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_11 = "ap-northeast-2:7383b690-3d86-4ad8-9800-d43bd1330c50";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_12 = "ap-northeast-2:602b93a2-fe31-4153-a767-bfa391002687";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_13 = "ap-northeast-2:2b9bf1f2-ddbc-45f9-b365-071fc26e573c";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_14 = "ap-northeast-2:3b6acff6-4412-4c8c-bf85-fc6211a55fb8";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_15 = "ap-northeast-2:d6d747e2-50e8-4558-8234-ee8bd06fa539";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_16 = "ap-northeast-2:c600d4ac-773e-4039-871c-c89fdc09e748";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_17 = "ap-northeast-2:e547f562-c673-42e4-8971-c570534a45fb";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_18 = "ap-northeast-2:efdde07c-e612-48aa-aa21-cf92f8109f8a";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_19 = "ap-northeast-2:2d528d07-b4e6-4f41-af9c-edc125210665";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_20 = "ap-northeast-2:602d0b6b-6bbb-4cd6-9543-d20d499d0c2a";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_21 = "ap-northeast-2:eec1bf8d-7dd5-49ad-8402-da7229ff095c";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_22 = "ap-northeast-2:5e80361e-08e7-485b-9850-41c42883088e";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_23 = "ap-northeast-2:c41b6e89-1784-415c-bfe3-87172dc3b66f";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_24 = "ap-northeast-2:eb179dd6-efbf-4dea-a7b1-8482bcb7aaa3";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_25 = "ap-northeast-2:523de284-1a93-4fb7-9f53-357cb5991657";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_26 = "ap-northeast-2:57d1f8a6-1325-46fd-a43a-a6f83f35f95d";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_27 = "ap-northeast-2:fca32cba-2c82-4450-90d8-77194eeefbcb";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_28 = "ap-northeast-2:57a48f72-c550-4bd8-a55b-a7503da3a3fa";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_29 = "ap-northeast-2:8a220c92-ed76-483c-bb2d-1ad1c19e1be9";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_30 = "ap-northeast-2:64e5e356-9042-4935-aed7-68feed26de90";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_31 = "ap-northeast-2:ef35f6d3-0e7f-4e09-8cf2-d59a3e4e988d";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_32 = "ap-northeast-2:fe589526-e2ed-4f83-8037-2202e912d436";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_33 = "ap-northeast-2:7123524b-5768-4e5c-8637-9af262cf5f04";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_34 = "ap-northeast-2:53f65604-8a92-48a1-98a8-3ad0d976a8c3";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_35 = "ap-northeast-2:e2da4d37-196d-4573-82b2-2e4b820a437d";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_36 = "ap-northeast-2:2f600bb6-36f2-4d67-9207-93ed80dc47b6";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_37 = "ap-northeast-2:5aa5bbe3-21f3-442e-b34f-84eff2931e7a";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_38 = "ap-northeast-2:58895a87-2b25-4633-9d0c-89f84072567f";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_39 = "ap-northeast-2:f897e671-d969-4277-84ee-62ad46c81be0";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_40 = "ap-northeast-2:c494f39c-4e56-4827-b42f-b24a53f218a3";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_41 = "ap-northeast-2:47dbc41c-8d6f-46a6-a720-84bb239201a3";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_42 = "ap-northeast-2:0046d3e7-d254-49ce-aff6-08697d7d02b4";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_43 = "ap-northeast-2:2d031589-44e7-454b-8fb8-718ae6ff6708";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_44 = "ap-northeast-2:8fea486f-dbc3-42f6-b4be-bebd59dfd890";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_45 = "ap-northeast-2:20bb4f3c-06ad-4bde-a29b-3793e65ead16";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_46 = "ap-northeast-2:c1f9f4bc-2cf4-4a98-9828-2999d51469a3";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_47 = "ap-northeast-2:9f95dd30-056c-4be3-a48e-9ac5eeeb41bb";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_48 = "ap-northeast-2:98ac218a-5c6a-4aa7-a827-4bc6cbe4a717";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_49 = "ap-northeast-2:6e1b9c88-6026-493c-82af-dc1816e7f864";  //3
    //public static final String SENSOR_COGNITO_POOL_ID_50 = "ap-northeast-2:23c59dd0-fba4-4b5d-9bb6-69484f951348";  //3
    //]]

    // DIALOG-IOT
    //[[remove in v2.3.16 for single Cognito pool id
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_1 =  "ap-northeast-2:ee615245-ae0e-4617-803d-cb7ec45e667f";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_2 =  "ap-northeast-2:90273ec7-1899-45e2-bf1e-286611c39555";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_3 =  "ap-northeast-2:36d85186-a243-4751-9a68-5e80d62ff511";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_4 =  "ap-northeast-2:2ea5986a-025b-4775-8fb2-979f626a692d";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_5 =  "ap-northeast-2:08d14130-7a69-4188-a9cb-cce018db6176";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_6 =  "ap-northeast-2:86e2dd78-5433-4161-8ad2-88366e663477";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_7 =  "ap-northeast-2:888fc15c-dce1-4951-bc10-41519585cbca";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_8 =  "ap-northeast-2:ea205941-ea7f-456a-b50e-08bb7368024a";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_9 =  "ap-northeast-2:5ff30e49-6ed9-444c-b97c-e31ede9d8f22";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_10 = "ap-northeast-2:ba216e10-bf42-434f-8f31-3ed4f3184e43";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_11 = "ap-northeast-2:eea14e24-6a1c-406a-8ca0-607486c36ec1";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_12 = "ap-northeast-2:f7142ce0-f928-4de9-9408-0a6cc95cee11";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_13 = "ap-northeast-2:9ca7eaa8-9be5-47a4-bea4-8188e8778bc7";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_14 = "ap-northeast-2:eefe7187-f04a-432f-90a0-850fbad73fdf";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_15 = "ap-northeast-2:7553aaf2-5cc3-4b0f-82f4-ed92c8cc0605";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_16 = "ap-northeast-2:1b0f867a-d582-4545-9f02-8606bcaf446d";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_17 = "ap-northeast-2:ee8d4fcb-2f5c-4cf8-aa9a-48eb04dfb700";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_18 = "ap-northeast-2:e9a21a27-dce9-4b4d-9c10-6a02c9b7c330";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_19 = "ap-northeast-2:8de405f8-d35c-457a-a8fa-c1102ca01d6a";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_20 = "ap-northeast-2:f090316c-f836-4aad-bd69-c890afe1682f";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_21 = "ap-northeast-2:9a3992be-8db6-475f-8a78-ecd688a65dd2";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_22 = "ap-northeast-2:910db47e-da12-4eaf-8a8d-fc4107bfd044";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_23 = "ap-northeast-2:3f55ef1d-e757-4037-b801-76f80eba44e9";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_24 = "ap-northeast-2:85730a31-ba50-4f0e-a0ef-7ae8fac5e6e8";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_25 = "ap-northeast-2:6b426738-d88f-406d-b1ac-34a4b73e3475";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_26 = "ap-northeast-2:dc83a91e-6c81-41a0-b8f1-6dc0bd2847c5";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_27 = "ap-northeast-2:e774df30-d334-4c97-acc7-30fdb1b03433";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_28 = "ap-northeast-2:1154e325-5ec4-484a-a9d8-3c628da90241";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_29 = "ap-northeast-2:cbc27fa2-406a-4f78-9339-da59c7ab25c9";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_30 = "ap-northeast-2:3d8fc147-f5cf-430b-963f-fd4a5deaa98d";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_31 = "ap-northeast-2:5465998c-842c-49a9-8c87-25d4d0911d01";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_32 = "ap-northeast-2:7c442cd5-1816-49b9-bf76-b68841f58bee";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_33 = "ap-northeast-2:457c53ad-7b17-4383-b268-1dd9bdaf80a5";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_34 = "ap-northeast-2:542a679e-946a-450a-980e-25446737bcc6";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_35 = "ap-northeast-2:0a89f01b-930f-440c-a56d-65af9d32bcf5";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_36 = "ap-northeast-2:34cd2e00-cc53-4886-8b6c-3e2f9d4a21da";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_37 = "ap-northeast-2:51453a6c-0c3d-4399-a903-54657cfa8137";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_38 = "ap-northeast-2:ac4c40b7-03c0-43f7-85be-4b9b88293648";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_39 = "ap-northeast-2:1290831a-b232-4094-8df6-6a3247f54457";  //3
    //public static final String DIALOG_IOT_COGNITO_POOL_ID_40 = "ap-northeast-2:37a5faab-02ba-488c-9b01-316cd80f159c";  //3
    //]]

    //EU
    //[[remove in v2.3.16 for single Cognito pool id
    //public static final String DIALOG_DOORLOCK_COGNITO_POOL_ID = "eu-west-2:60ad206a-3137-4734-b51e-0141a430a556";  //3
    //]]
    public static final String DIALOG_DOORLOCK_BUCKET_NAME = "dialogdoorlock-log";


    //[[remove in v2.3.16
/*    public static final String AP_NORTHEAST_2_CertificateKey =
            "-----BEGIN CERTIFICATE-----\n" +
                    "MIIDWjCCAkKgAwIBAgIVAIqSKvd/Qq2E9ZleQWN2Gk/iPw2GMA0GCSqGSIb3DQEB\n" +
                    "CwUAME0xSzBJBgNVBAsMQkFtYXpvbiBXZWIgU2VydmljZXMgTz1BbWF6b24uY29t\n" +
                    "IEluYy4gTD1TZWF0dGxlIFNUPVdhc2hpbmd0b24gQz1VUzAeFw0xODEyMDYwNjQw\n" +
                    "MjZaFw00OTEyMzEyMzU5NTlaMB4xHDAaBgNVBAMME0FXUyBJb1QgQ2VydGlmaWNh\n" +
                    "dGUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDZ/AbN7xxXgAslyB14\n" +
                    "ZHV/MPUjrpgPSnrbHcLwhOpKILoHiLO6CTqfXv/pxcXyh0UCHpp1PF63m0vmYYuA\n" +
                    "ueRgW23sjKPXRPyGnFPVjGntNhlFuXAWX1+m09GLkqdxWGz2wgKokSa8pMO/otTA\n" +
                    "iV5+uh8y/7q5fuASGywZR5WeutH8yjw4ui5Il+66S2yUifsCDrNgsmfTIZdta4cV\n" +
                    "umRKG6ZMT7XHEVuFMIrE5N2nfXu62CUWn4GOmmoF4iH0w6FINmV0f0sQcLS73+tv\n" +
                    "CR/dFnNzRT3DLm8FJH7RD60jHiYoZQFNHWuSH98cAg3RSyuRnHx9mmD+O8jKyZ6D\n" +
                    "r/7NAgMBAAGjYDBeMB8GA1UdIwQYMBaAFL6HYMtZyM54cz3RAAzyR1zF7+1TMB0G\n" +
                    "A1UdDgQWBBQeh5c1lEyK80j/TBBMP6Cz/qU3ljAMBgNVHRMBAf8EAjAAMA4GA1Ud\n" +
                    "DwEB/wQEAwIHgDANBgkqhkiG9w0BAQsFAAOCAQEAp9tbJ4GOFoX11trWKc/HTfdb\n" +
                    "TMIVu8KeEnIdFadgGhVcafH6cIrVBcocR5iAQNhV28P5dSFSrsdDOaiYQQ6XyaS9\n" +
                    "oOfLJCHosFd0CCAkV+2ZEmxDA0bN+WDdCppQHocYoNt8h6X+Mh0h2hnfB2hPQwDX\n" +
                    "TcaCwbJQy2XprqPpBo3ZuWqmSi55uslXj+2B4XgPZutim++8J7DHQbfHAGZwiAFN\n" +
                    "90TNlhZBdI87Ga07p0db03KcBQs8dBMaABC0RK39LqJ5ZdQMT/Owx0+iO2Be7w30\n" +
                    "7o06zCQB2A0nmfvAR8gSuImIBfKz2I1xQX5+CO4wes8RH5pNIOK2QrKgr9NJkA==\n" +
                    "-----END CERTIFICATE-----";

    public static final String AP_NORTHEAST_2_PrivateKey =
            "-----BEGIN RSA PRIVATE KEY-----\n" +
                    "MIIEpAIBAAKCAQEA2fwGze8cV4ALJcgdeGR1fzD1I66YD0p62x3C8ITqSiC6B4iz\n" +
                    "ugk6n17/6cXF8odFAh6adTxet5tL5mGLgLnkYFtt7Iyj10T8hpxT1Yxp7TYZRblw\n" +
                    "Fl9fptPRi5KncVhs9sICqJEmvKTDv6LUwIlefrofMv+6uX7gEhssGUeVnrrR/Mo8\n" +
                    "OLouSJfuuktslIn7Ag6zYLJn0yGXbWuHFbpkShumTE+1xxFbhTCKxOTdp317utgl\n" +
                    "Fp+BjppqBeIh9MOhSDZldH9LEHC0u9/rbwkf3RZzc0U9wy5vBSR+0Q+tIx4mKGUB\n" +
                    "TR1rkh/fHAIN0UsrkZx8fZpg/jvIysmeg6/+zQIDAQABAoIBADfE6fy/4xFj2fZF\n" +
                    "l3yYvxLWdLE3VwH6fSoYGCqu5r4mV1HcIJdFCzGA/ZpSlg0xnG8pYz0BP/5bhfSg\n" +
                    "Gi/J32rjmWD+rmBB7xWFY1FsRiGBSL/07H9c0Tz+TksWLy6pf981zbZQxIdY5Bfg\n" +
                    "UewceQeVGKxUjvIsSql3ODYTgW0FR7h+YGtmtXJ+8SQi3FSRwDdbpyoLokUf1YaH\n" +
                    "ksG1RPOxxah7Jr0YFN4waSixMMSb/fAxF5F1/mD0tgUSkUptRXu879mpA5+uYD+Q\n" +
                    "YrPzEDhvd8mXPaH1f1e/29Kq+tUNtmBdzY8gcmWr2h859x3R6wpybbYJt5KWK4NT\n" +
                    "7auoPKECgYEA/7yOGf8y2QJLfjW08qBAATnYZuZySrV7rNK5kxenGueZl2t52NCa\n" +
                    "vRQC8nNqouu33RjiYHSR8NQk9cLdpjnQOVxtSEWTZIctOPhtw53EAdRfw4v2e/7n\n" +
                    "oe9kR3VH1OUKfMDhduMUI09UGGyxsyRcKs1uLvvC5DX2XRAGafN1aDkCgYEA2jWD\n" +
                    "5SAPPJU+cbkQbkSBmcJph8x0949c/HJ2U6xcMmwR8G4Jnlwe+w3expwKnNlpNXaZ\n" +
                    "I+mm2BeXvyPJCgN/BMkDhU1xyDQBscCYrD9q41IU318CTmH6iExoUwv6NNuyOsFd\n" +
                    "IJeYnG6ckgI7yGkY0wxQvsI8alleI2mLehHuwzUCgYEA3Ye1xPlXT7r4MH1PoPmG\n" +
                    "WEmGlyS7DtKFLuFf1fagT+MeHpgAdfvGf1HNd77ZOgZdQI6k0w9HuMnctnO2U58z\n" +
                    "K+1P0VJL6sJaP0actt58g2U4C4m73A+lEZbxVCFZNyetXQIsjTMKJ8g5PesyR8+Q\n" +
                    "c5d/Af4fBldkcZtHIxK9uqkCgYAaqpmQwac7Bx4Xdb9NSm/wI3MUFmdg7ZM2gqJ1\n" +
                    "PUYTH2Pd1wSz5pweoCZObTlay7LwxqqWWfJ6y/9Oa4ghAiZeplYYz0sNZVWjrF68\n" +
                    "BhAA8cH9PjYg8BZW28eQBpGwLf0M8x53Yi9TRq05pq45oqZW/FVNypzpfjxj5X0X\n" +
                    "EOP11QKBgQCDnAVbfrXC+4S5UNwxGHw4cZJwAvOkkeApV3WlBSZFbbGzIxrVy79O\n" +
                    "7ETTGfSAbksUljV+2HZZVSXtgsCS/fzsFjMWYpeNRX3+9wtFfGCfxoygGW0JvOyY\n" +
                    "kg61geirHUDYgog9XzGKATXc3K/m7JdyOcWdbf54nhzcEqjRv1DhCA==\n" +
                    "-----END RSA PRIVATE KEY-----";

    public static final String AP_NORTHEAST_2_CertificateArn =
            "arn:aws:iot:ap-northeast-2:432073875051:cert/991648ec0bdb39032543d3d224270f990a7f9fe4e1418742842a4dcb57fa8fc5";


    public static final String US_WEST_2_CertificateKey =
            "-----BEGIN CERTIFICATE-----\n" +
                    "MIIDWTCCAkGgAwIBAgIUA6SF8A52YvU/a1L8qnX37ch3xfgwDQYJKoZIhvcNAQEL\n" +
                    "BQAwTTFLMEkGA1UECwxCQW1hem9uIFdlYiBTZXJ2aWNlcyBPPUFtYXpvbi5jb20g\n" +
                    "SW5jLiBMPVNlYXR0bGUgU1Q9V2FzaGluZ3RvbiBDPVVTMB4XDTIwMDExNjAxNDc1\n" +
                    "NloXDTQ5MTIzMTIzNTk1OVowHjEcMBoGA1UEAwwTQVdTIElvVCBDZXJ0aWZpY2F0\n" +
                    "ZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAOVEr5xESM5FqvLcDbgY\n" +
                    "PWVIQklemLSdqkw8nN7uoPg1PUzCu4IUkYdTTgsa4jA3eHjJ3BMDk9vv8uQftgBI\n" +
                    "yhJiozxq8BWBxd0t+6SWUQpYYaGEA5w+f2Qx2b//GTFIenrvNQ4tq6tkQt4k2vL0\n" +
                    "zhPxfDoJN3/SBkWZZU1NcSW89Mj6Ta4a7jnKisi4TLYTrJCs3Q2xH1RIJXhQfxUB\n" +
                    "CGp9AJxf1NglkylYGXfcFOBGkAtubBQEtkn790wqYflk543FvZyb27pHijyp0noe\n" +
                    "sOv/O+pXjtnOSavGWfnMOGdxmUPvIkfapYoCsOavelVkrZ4flcg9mwRxXCX3xFdz\n" +
                    "1A0CAwEAAaNgMF4wHwYDVR0jBBgwFoAU3196nQIZ5Ri6aAWpH9j+HcPD3zMwHQYD\n" +
                    "VR0OBBYEFENHpu3bYGIy4yecDTQOkbgDCYsKMAwGA1UdEwEB/wQCMAAwDgYDVR0P\n" +
                    "AQH/BAQDAgeAMA0GCSqGSIb3DQEBCwUAA4IBAQAFTs1wBF3PvXUrbSfZ8BD4ljzM\n" +
                    "X4dYP8FhkeaNkqBDvLKrd8qx1bkWYVPjidrHPHd2hOs3BgfGuyKR0iBZMie2Fzmu\n" +
                    "M7Mo5dCWWPpX9LXUt4I81V7ysQ0mKLGNqzHcv5AZGRMMpA8LOBSQ1Uy5UR667xv8\n" +
                    "sgNwM2DIC6xzFD9mcaV8iVoZyZJP4siEVJPsGHmi+Ch/IyUyypo/BE/rvF7w9QWv\n" +
                    "2VZqwq8k7/0BaJuBujP9iVjgne72qtDropsWDkeoVQKnTrgTOhHX9M2ue5us1WxZ\n" +
                    "BTvV6W2PLT+rIak/kMmRmvAcEjtNlG7+t5O9u3ccqwuXfddg4A4Txe+JbA+C\n" +
                    "-----END CERTIFICATE-----";

    public static final String US_WEST_2_PrivateKey =
            "-----BEGIN RSA PRIVATE KEY-----\n" +
                    "MIIEowIBAAKCAQEA5USvnERIzkWq8twNuBg9ZUhCSV6YtJ2qTDyc3u6g+DU9TMK7\n" +
                    "ghSRh1NOCxriMDd4eMncEwOT2+/y5B+2AEjKEmKjPGrwFYHF3S37pJZRClhhoYQD\n" +
                    "nD5/ZDHZv/8ZMUh6eu81Di2rq2RC3iTa8vTOE/F8Ogk3f9IGRZllTU1xJbz0yPpN\n" +
                    "rhruOcqKyLhMthOskKzdDbEfVEgleFB/FQEIan0AnF/U2CWTKVgZd9wU4EaQC25s\n" +
                    "FAS2Sfv3TCph+WTnjcW9nJvbukeKPKnSeh6w6/876leO2c5Jq8ZZ+cw4Z3GZQ+8i\n" +
                    "R9qligKw5q96VWStnh+VyD2bBHFcJffEV3PUDQIDAQABAoIBAAySqba+H+f/9hH/\n" +
                    "c1knKybEwa3YXDN7ef1YTZoEKBu0f7oX68SEcD6lmOAPB1xxjk/BciowxgkHraBJ\n" +
                    "BOgbFcnQUvdbPfNP/JPWk7hA7gzNL/11ieUloELUYQagbLdBLEZVm+k9I+7wPqKE\n" +
                    "DuXH+w0o7xEXtE10LRfgA6grmQriqU6JxzmL9F/h+IIsBmAK4mBagbxgKRfNfkn6\n" +
                    "MbGTRU+kP6s5ioPhWde2PYPYkmltnvuSPJiycbOmrjF64DeIVjIZXV+dUBBgIDy4\n" +
                    "GW8U2Ax2WQWipApZV7abQTJ7N/NQM3aSg+kQq7Kfdm0ywUJfWU/+aSSogPbI0jlC\n" +
                    "xTcoqBkCgYEA9mzoov8Dkrg62K0WazC2KXPcB/+zr2Nyqn+lkuLkmYxkgA6lHOQD\n" +
                    "eDjGZo+EQKeZaQMfQFfcYPFni0sao7/w9mx9kQv+RNdAu4obMnvD1yA2h+9d0yXF\n" +
                    "udl18TIkWtTGVAGXqe3TmikpLb6XEeOnQHe9HI6Tz7rAq19x+XCv2VMCgYEA7i0f\n" +
                    "XBgkeCbusD7soh8hyq59M7o7KP+bopzjpR/zMvgyepuarwUYcA4NaK4SJdnLwITf\n" +
                    "5dz92hyTuH5ZjwqLUxx4OHY2L8SrTf8Is7YbqR/OlKP06aWwX/VuxGZ39+PcOKro\n" +
                    "miT9uvBdyFhOFSrk6P6EA1XAwcszigVGyp8eER8CgYBOsuVw+MRpXRDeFhkszne+\n" +
                    "sVO58j2ua7I5JV75lCCxsRZ/ly3AFknNYURriLYR9/SUhvy6DdVB2Jba83dkiG4q\n" +
                    "JOfNJOlljuELg8W/z6rxz0XbD+UFA/Ers3lpODxIDd/xADkxedS0TSTriQyyF+RY\n" +
                    "QGVS823dT6UsleTOZuCiJQKBgGvJmeaYunBx1hWG9JpOEoHiQ6EUR8+/FdUawV8q\n" +
                    "PWq0rkuUfgV0/eFASssz89yTW0VuHNGvGGcMBcI51BgLApCDUtjijpVBoPD0WSDT\n" +
                    "amM4lMnqBV1z8YUL0qwpX2OdCD3RAuE3mmhufVK8u+GSfvP+swm7Vjed2V4+ExKB\n" +
                    "vh6rAoGBAK6uotPvDgOOcQzqJ19qYwNEWwR+gbvT7MpVChdO211/jThd9xUQhp+f\n" +
                    "CZ5ltmfaWOIc0MP7nNkZt1gYSSWd/bAFxOV+5sSnF+iHnQUJFAxPKbUjS/fK0lGZ\n" +
                    "L2nUGVlX05w/k8uBwp1DB1dU9JyxZaMrSdOrrYmT0iKseADw5G7e\n" +
                    "-----END RSA PRIVATE KEY-----";

    public static final String US_WEST_2_CertificateArn =
            "arn:aws:iot:us-west-2:432073875051:cert/d38a9bba135f9a673cbc47dd95f9f93a35ebf34c8704d1e8ef76df853ed7288f";


    //add for Keith
    public static final String EU_WEST_2_CertificateKey =
            "-----BEGIN CERTIFICATE-----\n" +
                    "MIIDWjCCAkKgAwIBAgIVALCV8W8mkiOYOBhLD+JsBwEE9Y9HMA0GCSqGSIb3DQEB\n" +
                    "CwUAME0xSzBJBgNVBAsMQkFtYXpvbiBXZWIgU2VydmljZXMgTz1BbWF6b24uY29t\n" +
                    "IEluYy4gTD1TZWF0dGxlIFNUPVdhc2hpbmd0b24gQz1VUzAeFw0yMTA3MTMxMTQ0\n" +
                    "MDlaFw00OTEyMzEyMzU5NTlaMB4xHDAaBgNVBAMME0FXUyBJb1QgQ2VydGlmaWNh\n" +
                    "dGUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQChI3r9np5fqLO1ue7k\n" +
                    "NKJvLqi7V9xSJqyDROtHvAonbyHGP+MPGmWKeyHGorbB3jJP9X0GewI/9kNe+Kue\n" +
                    "REQE2hrrULqj4OmePScBvbOJENaPxFl6J4iESTKBm0duOt+HhW8oE1sKitEets6q\n" +
                    "xHC4RYeXK4Fqna64MasKShnJ7LNn9+hwaELdfsljhkcJjvbysjujjZ7/iJpJAhpW\n" +
                    "ed+u+mqtyTs+sg9eMhcjGUNRn4keKkEk0K9BTiOFRZNm694gJIJBSoxDM9z6iDo/\n" +
                    "hb7csWrYx9A5c8QUGVjJjzNG8aNP+j6HGBtmS1B+j7UyXPsSsutVKV3+OD/i7wbs\n" +
                    "3hsJAgMBAAGjYDBeMB8GA1UdIwQYMBaAFPtAUnVlpB6WvdIsul0CrXNrMpUkMB0G\n" +
                    "A1UdDgQWBBS1YelQIPlN5CrwkxvcXjkC3I77xjAMBgNVHRMBAf8EAjAAMA4GA1Ud\n" +
                    "DwEB/wQEAwIHgDANBgkqhkiG9w0BAQsFAAOCAQEAbhYOZUkCTavMU8Bl6nhaU+dF\n" +
                    "d1qj7KJpWa2v0gG/WTZ7870ROHNf4rqBy7lMWLC2OdeHn0VAu/oQe2JIROGszeda\n" +
                    "V+G/N7AnVi2zUvS1c+22UlD8vxAQQqbJ7IQC5qIvG5jHsW37rKH8vv0Sk3aXGyPv\n" +
                    "qxbZLQz0K6pT/3anvDOGpP2TYX8Ot3vtZgwfFju71XcEW+0/sCANexlF3Wu6oiDR\n" +
                    "ElVxibfusrbTpiVRdRfVt5dcOtW8iJlf4W1+tBNAHpuK+xEyRul9k6gce10cbeTt\n" +
                    "bLEe1sgmxHJ5kCIK3ZLdzQyhmuP8jeEI+FoQUX3ArEC+ce+uRLh28oxm0scVzQ==\n" +
                    "-----END CERTIFICATE-----";

    public static final String EU_WEST_2_PrivateKey =
            "-----BEGIN RSA PRIVATE KEY-----\n" +
                    "MIIEowIBAAKCAQEAoSN6/Z6eX6iztbnu5DSiby6ou1fcUiasg0TrR7wKJ28hxj/j\n" +
                    "DxplinshxqK2wd4yT/V9BnsCP/ZDXvirnkREBNoa61C6o+Dpnj0nAb2ziRDWj8RZ\n" +
                    "eieIhEkygZtHbjrfh4VvKBNbCorRHrbOqsRwuEWHlyuBap2uuDGrCkoZyeyzZ/fo\n" +
                    "cGhC3X7JY4ZHCY728rI7o42e/4iaSQIaVnnfrvpqrck7PrIPXjIXIxlDUZ+JHipB\n" +
                    "JNCvQU4jhUWTZuveICSCQUqMQzPc+og6P4W+3LFq2MfQOXPEFBlYyY8zRvGjT/o+\n" +
                    "hxgbZktQfo+1Mlz7ErLrVSld/jg/4u8G7N4bCQIDAQABAoIBABkoaPNf6GoW9LfS\n" +
                    "p7GJZjLlngJh3UwWLajxAarZeNxfRW6mWtTo0iGkx8kzbkUmY8SIF1UDr348/Je/\n" +
                    "S0wSbGw1gS5sFDexzcee1JA5BebIcjdP+IhR08aThwclLynOl0eZVZl0LCqhMbUR\n" +
                    "5YJT2SVr9Zdse3Ah3j1zw7h/iTc1Nfv5NlYNeq1bdE4XxunHfloMzYB00YfETPWL\n" +
                    "89kry5l1m4tjlZZ9620s8o8OdNKPFZJHv2Xb0mPKeWGR5n8KNAhZy43LnV4hfZTL\n" +
                    "f/laY8qNL0Q2PBO0ayJYt2IxKhi31SLQkEwB15FbgDsXiRt00eiRBjTqGg4Crx/2\n" +
                    "atRwlAECgYEA0JjPHNK0qOepWDaY5FT4OvwrHnRdw98jtN1d4a1dM0djaRAVzDYQ\n" +
                    "KG4pI/xZ5NTp4EekWIaMWUW3NzxrZGF9S1AL0jsvczwTlShmw5ngg5o3DgqNSdUY\n" +
                    "WMJobJFP+JrWaRY+B8Cysw0H4PCg5AAWG6Nl/9mRJBUaRywVybZdK9ECgYEAxcHE\n" +
                    "3AwXqU0mEELXpLfUx4iQIdtDa+zO3F4CD0zZTTLrJZ6F+UoyKraUqmT4hEqSoP9s\n" +
                    "C+fYC4jHOz7DKf5osSw5A+ZNQgQm/jTH387RiZjrzkJhv5aVfl1nbAv/rB0lfTYw\n" +
                    "oIsYqITSG4Yh87+jnHKhN2TE4wo5dvJbRVkLobkCgYBSBEMq6g3ue2eyL4VeAaq4\n" +
                    "Rn4Ns2NKqGm5q3BGU4vZpbAIlzJ64KbpvqaeZ5JQzwZGEOUliYGVeDvZlAAFr+87\n" +
                    "tXVwa6T0giKQ+xmsvsvv8qrC8pMq/IZeH9I/HcN71CWJnB03nySkEfOPFg2sl/wZ\n" +
                    "Ud3BAyJ2NXXnlgXbEcEAgQKBgQCAEjR2udIdD9ylzVQw1ekUbCnD82dfntZ9Otik\n" +
                    "pgVZDK/jBh4Hy3hf1yzCiW+05UK+x4xmzXhKSAOXJ9dm+987VsN59g10wpGqMe57\n" +
                    "0Eo9jW6xfqYlWNVqJ/2RqNek1J9xzsWBQ3Ptt08HCkA0xpij+kzUPguQag+7K8vW\n" +
                    "RovU6QKBgCIv4WJ/6i5CC1cpYfayX+9qKWtsAJq1oen6ADkUZx15KJFuphzz7tT/\n" +
                    "OTA7XdCvS0ePS0LRK8PVTKTXXKOWn2+gKKjYHLpI4T5Hvc7Sc2IRs9nMRVu5dKnt\n" +
                    "blIbR5RmtLpdvo+wcA/FhF8wWVj7Fan5cT1rM9hDSPSFk9lfsILc\n" +
                    "-----END RSA PRIVATE KEY-----";

    public static final String EU_WEST_2_CertificateArn =
            "arn:aws:iot:eu-west-2:954825700866:cert/523f9f3ae22b44b83dc8be89206f3b5663be08a8880df494372163b5f90d4b62";

    //*/
    //]]
}
