# Android Open-Source Telematics App

![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/telematicsapp.jpeg)

## Description

This Telematics App is developed by Damoov and is distributed free of charge. This app can be used
to create your own telematics app for Android in few steps.

## Ready Features

Telematics:

- Telematics SDK - mobile telematics engine.

Screens:

- Dashboard
- Feed
- Trip Details
- Leaderboard
- My Rewards

## Basic concepts & credentials

1. Create an account https://app.damoov.com and get `X-API-KEY` auth key to work
   with the telematics SDK & APIs.
   How to obtain X-API-KEY => https://docs.damoov.com/docs/devportal-web-console#api-key

2. Additionally, to use Firebase© Crashlytics, you need to create a Firebase account: https://firebase.google.com


# Setting Up

## Setup Firebase© Project

Step 1: After creating your Firebase© account, open your
console: https://console.firebase.google.com

Click "Create a project" button.

![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/1.png)

Step 2: Enter the name of your future Project. Click "Continue" button.

![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/2.png)

Step 3: For ease of integration, at the next step, we recommend deactivating the "Enable Google
Analytics" checkbox.

![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/3.png)

Click "Create project".

Step 4: Now you need to create a configuration for your Android app. Click on the "Android" as it us
shown on the picture below:
![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/4.png)

Step 5: Enter your Android Package Name. Enter the SHA-1 key, this identifier must be used in your
application in Android Studio. Click "Register app" then.
![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/5.png)

Step 6: To connect your Firebase you need to add the `google-services.json` file
to `project_directory\app`. Final file path: `project_directory\app\google-services.json`
![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/6.png)

Step 7: You can skip the "Add Firebase SDK" & "Add initialization code" steps below, because we
already did it for you in our Telematics App:) Finish the setup and click on "Continue to console".
![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/7.png)

Step 8: Open our TelematicsApp in Android Studio, make sure to transfer the `google-services.json`
file to project_directory\app (See Step 5 above) and Enjoy!

Build & Run!

## Setup TelematicsApp Configuration

In file AppConfig.kt you can specify the basic settings for your app.
To use your unique applicationId for your application, change APP_ID:<br/>
const val APP_ID = "your_application_id"<br/>

To work with our API use X_API_KEY
from https://docs.damoov.com/docs/devportal-web-console#api-key:<br/>
const val X_API_KEY = "YOUR_X-API-KEY"<br/>

To set application label change app_name in strings.xml in content module:<br/>
<string name="app_name">YOUR_LABEL</string>

To set Privacy Policy change PRIVACY_POLICY in AppConfig.kt file:<br/>
const val PRIVACY_POLICY = "YOUR_PRIVACY_POLICY_LINK" //for
example "https://www.telematicssdk.com/privacy-policy/"

To set Terms Of Use change PRIVACY_POLICY in AppConfig.kt file: <br/>
const val TERMS_OF_USE = "YOUR_TERMS_OF_USE_LINK" //for
example"https://www.telematicssdk.com/privacy-policy/"

To set application icon, find the content module icon in resource folders (res/mipmap,
res/mipmap-hdpi, etc.) and replace it. And for change background icon color set
ic_launcher_background in color.xml:
<color name="ic_launcher_background">#your_color</color>

To usu Intercom Chat set Intercom app Id and api Key in AppConfig.kt file: <br/>
const val INTERCOM_APP_ID = "YOUR_INTERCOM_APP_ID"<br/>
const val INTERCOM_API_KEY = "YOUR_INTERCOM_API_KEY"

## Telematics SDK Setup

We are using the Gradle auto build system.
The Telematics SDK is installed automatically in the Telematics app. After downloading this
application for the first time, you need to run the `Sync Project with Gradle Files` command. This
will install the required dependency libraries for the application to function properly. To upgrade
the Telematics SDK version, go to the `libs.versions.toml` file (.\gradle) and change
the `trackingApi` parameter. This repository will always use the current version of the
Telematics SDK.

## Telematics SDK | Permission Wizard

An important part to record user's trips is to properly request permissions to use the user's
Location and Motion & Fitness activity. Telematics SDK includes a specially designed `Wizard` that
helps the user explain why the application needs it and make the right choice.
Note: this wizard is fully customizable, you can find the documentation
here: https://docs.telematicssdk.com/docs/android-sdk-integration

For use your own icon to the notification, place your own icons to res/drawable (module: content)
folders with the following
names: `ic_tracking_sdk_status_bar.png`, `ic_tracking_sdk_notification.png`.
For change Wizard next button background color you need edit `layout_telematics_wizard_page.xml` (
module: data).

## Get Google Map API key

In the next few simple steps, we'll show you how easy it is to create access keys in the Google
Cloud Console.

Step 1: After sign in your Google account open https://console.cloud.google.com/ </br>

Step 2: Create new project in Google Cloud
Console: https://console.cloud.google.com/projectcreate </br>

![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/instruction_map_1.png)

![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/instruction_map_2.png)

![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/instruction_map_3.png)

Step 3: Set billing for this project: https://console.cloud.google.com/projectselector/billing
Note: use of maps is free </br>

Step 4: Enable MapsSDK for
Android: https://console.cloud.google.com/apis/library/maps-android-backend.googleapis.com </br>

![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/instruction_map_4.png)

Step 5: Go to the Google Maps Platform → Credentials page → Create credentials → API key. </br>

![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/instruction_map_5.png)

![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/instruction_map_6.png)

![](https://github.com/Mobile-Telematics/telematics-open-source-app-android/blob/main/img_readme/instruction_map_7.png)

Step 6: After API key is created, copy it to GOOGLE_MAP_API parameter in AppConfig.kt file. </br>

More info here: https://developers.google.com/maps/documentation/android-sdk/overview

## Dashboard

Our goal is to provide your users with a user-friendly interface to get the best user experience.
To get the first data, user usually needs to drive a short distance. We set this parameter in the
configuration file `AppConfig.kt` in parameter
const val DASHBOARD_DISTANCE_LIMIT = "10" //measured in km

## Feed

The Trips screen displays the trips users have made.

## Feed | Type of transport

The Telematics SDK allows users to change their role for any trip.

Use the following string values below!

- OriginalDriver
- Passenger
- Bus
- Motorcycle
- Train
- Taxi
- Bicycle
- Other

## Feed | Tags

Depending on your product use cases, you can also use our Tags feature. You can learn more about it
here: https://docs.telematicssdk.com/docs/tags
We also offer you a convenient interface for switching certain tags for each trip.

The Telematics SDK allows users to add specific unique`tags` to any ride for ease of use.
For example, by adding tag options to any trip, you will be able to mark specific trips for
Business/Personal or other options.

NOTE: you can use `DEL` tag and hide the trips marked by it in the app. These trips will be shown in
DataHub on List of Trips page with a special mark that these trips were hidden in the app.

## Leaderboard

You can learn more about these services by following to our docs:
https://docs.telematicssdk.com/docs/leaderboards

All 9 types of Leaderboard are presented in the Telematics App and you can figure out which of these
options you actually need.

> Note! Only users who have trips during latest 14 days participate in Leaderboard. Use placeholders
> for new and lost users.

## My Rewards

Our telematics app allows you to work with DriveCoins and Streaks for each user:

You can learn more about these services by following to our docs:
DriveCoins - https://docs.telematicssdk.com/docs/drivecoins
Streaks - https://docs.telematicssdk.com/docs/streaks-1

In detail, you can see the work with methods for rewards in the Telematics App source code in the
DriveCoins section.

## On-Demand Tracking Mode

In the new version of the app, we have provided the ability to select Tracking Mode in Settings.
There may be 3 options - `Automatic Tracking`, `On-Demand Tracking`, `Tracking disabled`.

The`On-Demand Tracking` provides an updated `Dashboard` by applying and programmatically increasing
Constraints in InterfaceBuilder and a special method for increasing the vertical dimensions of the
DashboardController.m file. In this Mode, the user can create a Job for himself.

`JobName` is a specific tag identifier that will be added for 1 or any number of trips made by the
user. The user must necessarily start a certain job or order, and complete it accordingly. In the
future, when the trip is enriched on our backend-side, the app will receive statistics for
this `JobName` tag. The user will see the number of trips made for this task, the rating of
maneuverability, risk score, etc. All this is available in a new section on our `Dashboard`.

`On-Demand Tracking` is great for any business like delivery service, taxi and many others.
Currently, this Mode will be an integral part of the Telematics App and provide you with a new
experience of integrations and work options.

## User Log Out

In the Telematics App source code, we show you an option to clear user data after logging out. Do
not forget - to stop tracking and record user trips, you need to explicitly
delete `VIRTUAL_DEVICE_TOKEN`.
This can be done using Telematics SDK method:

    trackingApi.logout()

You can also disable Telematics SDK with the trips uploading to upload already recorded and stored
on the device trips to Damoov platform.
Learn more about available SDK methods
here: https://docs.telematicssdk.com/docs/methods-for-android-app

## Links

[Official product Web-page](https://app.damoov.com/)

[Official API services web-page](https://www.damoov.com/telematics-api/)

[Official API references](https://docs.telematicssdk.com/reference)

[Official ZenRoad web-page](https://www.damoov.com/telematics-app/)

[Official ZenRoad app for iOS](https://apps.apple.com/jo/app/zenroad/id1563218393)

[Official ZenRoad app for Android](https://play.google.com/store/apps/details?id=com.telematicssdk.zenroad&hl=en&gl=US)

###### Copyright © 2020-2025 DAMOOV PTE.LTD. All rights reserved.
