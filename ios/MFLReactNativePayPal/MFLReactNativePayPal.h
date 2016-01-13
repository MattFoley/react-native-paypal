//
//  MFLReactNativePayPal.h
//  ReactPaypal
//
//  Created by Tj on 6/22/15.
//  Copyright (c) 2015 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger, PayPalEnvironment)
{
  kPayPalEnvironmentSandbox,
  kPayPalEnvironmentProduction,
  kPayPalEnvironmentSandboxNoNetwork
};

typedef NS_ENUM(NSInteger, PaymentCompletionStatus)
{
  kPayPalPaymentCanceled,
  kPayPalPaymentCompleted
};


@interface MFLReactNativePayPal : NSObject

@end
