'use strict';

var {PayPal} = require('react-native').NativeModules;

var constants = {};
var constantNames = Object.keys(PayPal).filter(p => p == p.toUpperCase());
constantNames.forEach(c => constants[c] = PayPal[c]);

var functions = {
  paymentRequest(payPalParameters) {
    return new Promise(function(resolve, reject) {
      PayPal.paymentRequest(payPalParameters, resolve, reject);
    });
  }
};

var exported = {};
Object.assign(exported, constants, functions);

module.exports = exported;
