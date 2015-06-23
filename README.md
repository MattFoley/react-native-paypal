# react-native-paypal

Paypal wrapper for React Native, currently only supports the bare minimum of buying a product.

###Integration

Include PayPal as normally, following their directions. After doing that, also drag MFLReactNativePayPal.h and MFLReactNativePayPal.m into your project.

###Initialization:

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
