/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */
'use strict';

var MFLReactNativePayPal = require('NativeModules').MFLReactNativePayPal;
var React = require('react-native');

var {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableHighlight
} = React;

var ReactPaypal = React.createClass({
  componentDidMount: function() {
    console.log("Component did mount");
    MFLReactNativePayPal.initializePaypalEnvironment(0,
      "AWyPKtVl7ac7SOlIscEe-dONGB-oBYav2TwimOgB1FysVGcKneAkj_1O1LV-Vnr0PEeMk5NmweJfIiLm");
  },

  _onPressButton: function() {
    MFLReactNativePayPal.preparePaymentOfAmount(100.00, "USD", "Bacon");
    MFLReactNativePayPal.prepareConfigurationForMerchant("Bacon Truck", true, "bacon@bacon.com");
    MFLReactNativePayPal.presentPaymentViewControllerForPreparedPurchase((error, payload) => {
      if (error) {
        // I should think about returning some errors
        return;
      } else {
        console.log("payload: " + payload);
      }
    });
  },

  render: function() {
    return (
      <View style={styles.container}>
        <TouchableHighlight onPress={this._onPressButton}>
          <View style={styles.container}>
            <Text style={styles.welcome}>
              Tap To Initiate Paypal Test
            </Text>
            <Text style={styles.instructions}>
              Use test user "bacon@bacon.com" password "password".
            </Text>
          </View>
        </TouchableHighlight>
      </View>
    );
  }
});

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
