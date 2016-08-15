package br.com.vizir.rn.paypal;

import android.content.Intent;
import android.content.Context;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PayPalPackage implements ReactPackage {
  private Context context;
  private PayPal paypalModule;
  private int paymentIntentRequestCode;

  public PayPalPackage(Context activityContext, int paymentIntentRequestCode) {
    context = activityContext;
    this.paymentIntentRequestCode = paymentIntentRequestCode;
  }

  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
    List<NativeModule> modules = new ArrayList<>();
    paypalModule = new PayPal(reactContext, context, paymentIntentRequestCode);

    modules.add(paypalModule);
    return modules;
  }

  @Override
  public List<Class<? extends JavaScriptModule>> createJSModules() {
    return Collections.emptyList();
  }

  @Override
  public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }

  public void handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
    paypalModule.handleActivityResult(requestCode, resultCode, data);
  }
}
