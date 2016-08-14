/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */
'use strict';

import React from 'react';

import {
  Component
} from 'react';

import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableHighlight,
  NativeModules
} from 'react-native';

var MFLReactNativePayPal = NativeModules.MFLReactNativePayPal;

class ReactPaypal extends Component {
  componentDidMount() {
    console.log("Component did mount");
    MFLReactNativePayPal.initializePaypalEnvironment(0,
      "AWyPKtVl7ac7SOlIscEe-dONGB-oBYav2TwimOgB1FysVGcKneAkj_1O1LV-Vnr0PEeMk5NmweJfIiLm");
  }

  _onPressButton() {
    MFLReactNativePayPal.preparePaymentOfAmount("100.00", "USD", "Bacon");
    MFLReactNativePayPal.prepareConfigurationForMerchant("Bacon Truck", true, "bacon@bacon.com");
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
  }

  render() {
    return (
      <View style={styles.container}>
        <TouchableHighlight onPress={this._onPressButton}>
          <View style={styles.container}>
            <Text style={styles.welcome}>
              Tap To Initiate Paypal Test
            </Text>
            <Text style={styles.instructions}>
              Use test user "bacon@react.com" password "Password!".
            </Text>
          </View>
        </TouchableHighlight>
      </View>
    );
  }
}

var styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('ReactPaypal', () => ReactPaypal);
