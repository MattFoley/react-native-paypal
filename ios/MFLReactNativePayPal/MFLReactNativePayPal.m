//
//  MFLReactNativePayPal.m
//  ReactPaypal
//
//  Created by Tj on 6/22/15.
//  Copyright (c) 2015 Facebook. All rights reserved.
//

#import "MFLReactNativePayPal.h"
#import "RCTBridge.h"
#import "PayPalMobile.h"
#import "RCTConvert.h"

NSString * const kPayPalPaymentStatusKey              = @"status";
NSString * const kPayPalPaymentConfirmationKey        = @"confirmation";

@implementation RCTConvert (PaymentCompletionStatus)

RCT_ENUM_CONVERTER(PaymentCompletionStatus, (@{ @"Canceled" : @(kPayPalPaymentCompleted),
                                                @"Completed"  : @(kPayPalPaymentCanceled)}
                                             ), kPayPalPaymentCanceled, integerValue)

@end


@implementation RCTConvert (PayPalEnvironment)

RCT_ENUM_CONVERTER(PayPalEnvironment, (@{ @"Sandbox" : @(kPayPalEnvironmentSandbox),
                                          @"Production"  : @(kPayPalEnvironmentProduction),
                                          @"NoNetwork"  : @(kPayPalEnvironmentSandboxNoNetwork)}
                                       ), kPayPalEnvironmentSandboxNoNetwork, integerValue)

@end


@interface MFLReactNativePayPal () <PayPalPaymentDelegate, RCTBridgeModule>

@property PayPalPayment *payment;
@property PayPalConfiguration *configuration;
@property (copy) RCTResponseSenderBlock flowCompletedCallback;

@end

@implementation MFLReactNativePayPal

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(initializePaypalEnvironment:(int)environment
                  forClientId:(NSString *)clientId )
{
  dispatch_async(dispatch_get_main_queue(), ^{
    NSString *envString = [self stringFromEnvironmentEnum:environment];

    [PayPalMobile initializeWithClientIdsForEnvironments:@{envString : clientId}];
    [PayPalMobile preconnectWithEnvironment:envString];
  });
}

#pragma mark React Exported Methods

RCT_EXPORT_METHOD(preparePaymentOfAmount:(NSString *)amount
                  ofCurrency:(NSString *)currencyCode
                  withDescription:(NSString *)description)
{
  self.payment = [[PayPalPayment alloc] init];
  [self.payment setAmount:[[NSDecimalNumber alloc] initWithString:amount]];
  [self.payment setCurrencyCode:currencyCode];
  [self.payment setShortDescription:description];
}


RCT_EXPORT_METHOD(prepareConfigurationForMerchant:(NSString *)merchantName
                  acceptingCreditCards:(BOOL)shouldAcceptCreditCards
                  withUserEmail:(NSString *)userEmail)
{
  self.configuration = [[PayPalConfiguration alloc] init];
  [self.configuration setMerchantName:merchantName];
  [self.configuration setAcceptCreditCards:shouldAcceptCreditCards];
  [self.configuration setDefaultUserEmail:userEmail];
}

RCT_EXPORT_METHOD(presentPaymentViewControllerForPreparedPurchase:(RCTResponseSenderBlock)flowCompletedCallback)
{
  self.flowCompletedCallback = flowCompletedCallback;

  PayPalPaymentViewController *vc = [[PayPalPaymentViewController alloc] initWithPayment:self.payment
                                                                           configuration:self.configuration
                                                                                delegate:self];

  UIViewController *visibleVC = [[[UIApplication sharedApplication] keyWindow] rootViewController];
  do {
    if ([visibleVC isKindOfClass:[UINavigationController class]]) {
      visibleVC = [(UINavigationController *)visibleVC visibleViewController];
    } else if (visibleVC.presentedViewController) {
      visibleVC = visibleVC.presentedViewController;
    }
  } while (visibleVC.presentedViewController);
  dispatch_async(dispatch_get_main_queue(), ^{
    [visibleVC presentViewController:vc animated:YES completion:nil];
  });
}

#pragma mark Paypal Delegate

- (void)payPalPaymentDidCancel:(PayPalPaymentViewController *)paymentViewController
{
  [paymentViewController.presentingViewController dismissViewControllerAnimated:YES completion:^{
    if (self.flowCompletedCallback) {
      self.flowCompletedCallback(@[[NSNull null], @{kPayPalPaymentStatusKey : @(kPayPalPaymentCanceled)}]);
    }
  }];
}

- (void)payPalPaymentViewController:(PayPalPaymentViewController *)paymentViewController
                 didCompletePayment:(PayPalPayment *)completedPayment
{
  [paymentViewController.presentingViewController dismissViewControllerAnimated:YES completion:^{
    if (self.flowCompletedCallback) {
      self.flowCompletedCallback(@[[NSNull null], @{kPayPalPaymentStatusKey : @(kPayPalPaymentCompleted),
                                                    kPayPalPaymentConfirmationKey : completedPayment.confirmation}]);
    }
  }];
}

#pragma mark Utilities

- (NSDictionary *)constantsToExport
{
  return @{ @"Environment" : @{
                @"Sandbox" : @(kPayPalEnvironmentSandbox),
                @"Production" : @(kPayPalEnvironmentProduction),
                @"NoNetwork" : @(kPayPalEnvironmentSandboxNoNetwork),
                },
            };
}

- (NSString *)stringFromEnvironmentEnum:(PayPalEnvironment)env
{
  switch (env) {
    case kPayPalEnvironmentProduction: return PayPalEnvironmentProduction;
    case kPayPalEnvironmentSandbox: return PayPalEnvironmentSandbox;
    case kPayPalEnvironmentSandboxNoNetwork: return PayPalEnvironmentNoNetwork;
  }
}

@end
