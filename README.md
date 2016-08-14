# react-native-paypal

A Cross platform React Native interface for the PayPal Payment UI. Supports both iOS and Android currently, but can be used seperately.

![Demo of a Payment using PayPal](/react-native-paypal.gif?raw=true "react-native-paypal")

## Usage
--

Initiating a payment is as simple as creating a single promise

```javascript
let PayPal = require('react-native-paypal');
PayPal.paymentRequest({
  clientId: 'AbyfNDFV53djg6w4yYgiug_JaDfBSUiYI7o6NM9HE1CQ_qk9XxbUX0nwcPXXQHaNAWYtDfphQtWB3q4R',
  environment: PayPalAndroid.SANDBOX,
  price: '42.00',
  currency: 'USD',
  description: 'PayPal Test'
}).then((confirm, payment) => console.log('Paid'); /* At this point you should verify payment independently */)
.catch((error_code) => console.error('Failed to pay through PayPal'));
```

#### Callback parameters:

If all goes OK with the payment than the paymentRequest promise is resolved with
the following arguments as JSON strings:
- A confirm:
``` json
{
  "client": {
    "environment": "mock",
    "paypal_sdk_version": "2.12.4",
    "platform": "Android",
    "product_name": "PayPal-Android-SDK"
  },
  "response": {
    "create_time": "2014-02-12T22:29:49Z",
    "id": "PAY-6RV70583SB702805EKEYSZ6Y",
    "intent": "sale",
    "state": "approved"
  },
  "response_type": "payment"
}
```

- A payment:
```json
{
  "amount": "1.00",
  "currency_code": "USD",
  "short_description": "PayPal Test",
  "intent": "sale"
}
```

Handling callbacks:
```javascript
PayPal.paymentRequest(...).then(function (payment, confirm) {
  sendPaymentToConfirmInServer(payment, confirm);
})
```

If anything fails the promise will be notify an error with a code which will be
one of:
- USER\_CANCELLED
- INVALID\_CONFIG

Handling failures:

``` javascript
PayPal.paymentRequest(...).catch(function (error_code) {
    if (error_code == PayPal.USER_CANCELLED) {
        // User didn't complete the payment
    } else if (error_code == PayPal.INVALID_CONFIG) {
        // Invalid config was sent to PayPal
    }
})
```

## Setup

Android
-------

1. Add react-navive-paypal to your project

``` bash
npm install --save react-native-paypal
```

2. Add the following to android/app/build.gradle

``` groovy
dependencies {
    // ...
    compile project(':react-native-paypal')
}
```

3. Add the following to android/settings.gradle

``` groovy
include ':react-native-paypal'
project(':react-native-paypal').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-paypal/android')
```

4. Edit android/src/.../MainActivity.java

``` java
// ...
import br.com.vizir.rn.paypal.PayPalPackage; // <--
import br.com.vizir.rn.paypal.PayPal; // <--
import android.content.Intent; // <--

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
    // ...
    private static final int PAY_PAL_REQUEST_ID = 9; // <-- Can be any unique number
    private PayPalPackage payPalPackage; // <--

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ...
        payPalPackage = new PayPalPackage(this, PAY_PAL_REQUEST_ID); // <--

        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("index.android")
                .addPackage(new MainReactPackage())
                // ...
                .addPackage(payPalPackage) // <--
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        // ...
    }

    // ...

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == PAY_PAL_REQUEST_ID) {
           payPalPackage.handleActivityResult(requestCode, resultCode, data); // <--
       } else {
           otherModulesHandlers(requestCode, resultCode, data);
       }
    }
}
```

iOS
---

### Sample App

You'll find an iOS example in `<root>/ios/Example/`. You will need to run npm install in that directory in order to download the dependencies required by the sample application. Note this sample still uses manual NativeModule usage, and you'll likely find the above Javascript simpler than the JS used in this sample.

### Installation

Currently you have to install via `npm` from GitHub (or change the version specifier in `package.json` to `MattFoley/react-native-paypal`):

```shell
npm install --save MattFoley/react-native-paypal
```

There will be an updated npm release shortly, which will allow

#### Install the PayPal-iOS-SDK

You then have to install the PayPal-iOS-SDK into `node_modules/react-native-paypal/ios/lib/Paypal`

Here's a one-liner to download and unpack version `2.13.0`:

```shell
mkdir -p node_modules/react-native-paypal/ios/lib/Paypal && curl -L --progress https://github.com/paypal/PayPal-iOS-SDK/archive/2.13.0.tar.gz | tar -xz - -C node_modules/react-native-paypal/ios/lib/Paypal --strip-components=1
```

Include PayPal as normally, following their directions. Their integration steps and iOS SDK can be found [here](https://github.com/paypal/PayPal-iOS-SDK). After doing that, also drag MFLReactNativePayPal.h and MFLReactNativePayPal.m into your project.

#### Add `MFLReactNativePayPal.xcodeproj`

Add `node_modules/react-native-paypal/ios/MFLReactNativePayPal.xcodeproj`
to the `Libraries` group in iOS and link `libMFLReactNativePayPal.a` as described in Step 2 of the
[React Native Manual Linking docs](https://facebook.github.io/react-native/docs/linking-libraries-ios.html#manual-linking).

Follow steps 4 and 5 of [the PayPal instalation instructions](https://github.com/paypal/PayPal-iOS-SDK#if-you-dont-use-cocoapods-then), as well as the [additional steps here](https://github.com/paypal/PayPal-iOS-SDK#with-or-without-cocoapods). **This has to be done for the main app, not for the library you included.**

### Manual Usage (iOS Only)

You can access the NativeModule directly on iOS using the following instructions, if you'd like to modify the functionality.

###Initialization:

     import {
       NativeModules
     } from 'react-native';
     let MFLReactNativePayPal = NativeModules.MFLReactNativePayPal;
     MFLReactNativePayPal.initializePaypalEnvironment(<environment>, "<Your client id>");

#####Environment Values that should be used here are declared in MFLReactNativePayPal.h

     • Sandbox = 0
     • Production = 1
     • No Network = 2

###Prepare a Payment:

     MFLReactNativePayPal.preparePaymentOfAmount(<Amount as a Float>, <Currency Code>, <Short description>);
     ie: MFLReactNativePayPal.preparePaymentOfAmount(100.00, "USD", "Bacon");

###Prepare Configuration:

     MFLReactNativePayPal.prepareConfigurationForMerchant(<Merchant Name>, <BOOL should accept credit cards>, <User email>);
     MFLReactNativePayPal.prepareConfigurationForMerchant("Bacon Truck", true, "bacon@bacon.com");


###Present the payment flow with a completion handler:

     MFLReactNativePayPal.presentPaymentViewControllerForPreparedPurchase((error, payload) => {
        if (error) {
          //Handle Error
          return;
        } else {

         console.log("payload: " + payload);
         if (payload.status == 1) {
            console.log(payload.confirmation);
         } else {
            console.log("User cancelled payment");
         }
        }
     });

## TODO:

     * [ ] Android Sample App
     * [ ] Refactor & cleanup
     * [ ] Automated tests
     * [ ] Future payment (subscriptions)
     * [ ] Invoice and optional fields - https://github.com/MattFoley/react-native-paypal/issues/6

## Thanks

The Android portion of this library was originally built by https://github.com/Vizir for https://github.com/Vizir/react-native-paypal. The merging of the API in order to provide cross platform support was done almost entirely by https://github.com/amiuhle.
