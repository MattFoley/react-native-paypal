# react-native-paypal

A React Native interface for the PayPal Payment UI

![Demo of a Payment using PayPal](/react-native-paypal.gif?raw=true "react-native-paypal")

# Setup

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

5. Usage example:

```javascript
var {PayPal} = require('React').NativeModules;
PayPalAndroid.paymentRequest({
  clientId: 'AbyfNDFV53djg6w4yYgiug_JaDfBSUiYI7o6NM9HE1CQ_qk9XxbUX0nwcPXXQHaNAWYtDfphQtWB3q4R',
  environment: PayPalAndroid.SANDBOX,
  price: '42.00',
  currency: 'USD',
  description: 'PayPal Test'
}).then((confirm, payment) => console.log('Paid'); /* MUST verify payment in server*/)
.catch((error_code) => console.error('Failed to pay through PayPal'));
```

6. Callback parameters:

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

iOS
---

### TODO:
- Automated tests
- iOS version
- Future payment (subscriptions)
Paypal wrapper for React Native, currently only supports the bare minimum of buying a product.

### Integration

Include PayPal as normally, following their directions. Their integration steps and iOS SDK can be found [here](https://github.com/paypal/PayPal-iOS-SDK). After doing that, also drag MFLReactNativePayPal.h and MFLReactNativePayPal.m into your project.

### Initialization:

     var MFLReactNativePayPal = require('NativeModules').MFLReactNativePayPal;
     MFLReactNativePayPal.initializePaypalEnvironment(<environment>, "<Your client id>");

##### Environment Values that should be used here are declared in MFLReactNativePayPal.h

     • Sandbox = 0
     • Production = 1
     • No Network = 2

### Prepare a Payment:

     MFLReactNativePayPal.preparePaymentOfAmount(<Amount as a Float>, <Currency Code>, <Short description>);
     ie: MFLReactNativePayPal.preparePaymentOfAmount(100.00, "USD", "Bacon");

### Prepare Configuration:

     MFLReactNativePayPal.prepareConfigurationForMerchant(<Merchant Name>, <BOOL should accept credit cards>, <User email>);
     MFLReactNativePayPal.prepareConfigurationForMerchant("Bacon Truck", true, "bacon@bacon.com");


### Present the payment flow with a completion handler:

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
