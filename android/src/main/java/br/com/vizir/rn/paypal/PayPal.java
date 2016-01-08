package br.com.vizir.rn.paypal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

public class PayPal extends ReactContextBaseJavaModule {
  private final int paymentIntentRequestCode;

  private static final String ERROR_USER_CANCELLED = "USER_CANCELLED";
  private static final String ERROR_INVALID_CONFIG = "INVALID_CONFIG";

  private Callback successCallback;
  private Callback errorCallback;

  private Context activityContext;
  private Activity currentActivity;

  public PayPal(ReactApplicationContext reactContext, Context activityContext, int requestCode) {
    super(reactContext);
    this.activityContext = activityContext;
    this.currentActivity = (Activity)activityContext;
    this.paymentIntentRequestCode = requestCode;
  }

  @Override
  public String getName() {
    return "PayPal";
  }

  @Override public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();

    constants.put("NO_NETWORK", PayPalConfiguration.ENVIRONMENT_NO_NETWORK);
    constants.put("SANDBOX", PayPalConfiguration.ENVIRONMENT_SANDBOX);
    constants.put("PRODUCTION", PayPalConfiguration.ENVIRONMENT_PRODUCTION);
    constants.put(ERROR_USER_CANCELLED, ERROR_USER_CANCELLED);
    constants.put(ERROR_INVALID_CONFIG, ERROR_INVALID_CONFIG);

    return constants;
  }

  @ReactMethod
  public void paymentRequest(
    final ReadableMap payPalParameters,
    final Callback successCallback,
    final Callback errorCallback
  ) {
    this.successCallback = successCallback;
    this.errorCallback = errorCallback;

    final String environment = payPalParameters.getString("environment");
    final String clientId = payPalParameters.getString("clientId");
    final String price = payPalParameters.getString("price");
    final String currency = payPalParameters.getString("currency");
    final String description = payPalParameters.getString("description");

    PayPalConfiguration config =
      new PayPalConfiguration().environment(environment).clientId(clientId);

    startPayPalService(config);

    PayPalPayment thingToBuy =
      new PayPalPayment(new BigDecimal(price), currency, description,
                        PayPalPayment.PAYMENT_INTENT_SALE);

    Intent intent =
      new Intent(activityContext, PaymentActivity.class)
      .putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
      .putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

    currentActivity.startActivityForResult(intent, paymentIntentRequestCode);
  }

  private void startPayPalService(PayPalConfiguration config) {
    Intent intent = new Intent(currentActivity, PayPalService.class);
    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
    currentActivity.startService(intent);
  }

  public void handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
    if (requestCode != paymentIntentRequestCode) { return; }

    if (resultCode == Activity.RESULT_OK) {
      PaymentConfirmation confirm =
        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
      if (confirm != null) {
        successCallback.invoke(
          confirm.toJSONObject().toString(),
          confirm.getPayment().toJSONObject().toString()
        );
      }
    } else if (resultCode == Activity.RESULT_CANCELED) {
      errorCallback.invoke(ERROR_USER_CANCELLED);
    } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
      errorCallback.invoke(ERROR_INVALID_CONFIG);
    }

    currentActivity.stopService(new Intent(currentActivity, PayPalService.class));
  }
}
