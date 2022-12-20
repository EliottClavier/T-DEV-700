import 'package:flutter/material.dart';
import 'package:flutter_redux/flutter_redux.dart';
import 'package:redux/redux.dart';

@immutable
class TransactionState {
  String amount;
  TransactionState({this.amount = '0.00 €'});
}

class updateAmountStateAction {
  final amount;
  updateAmountStateAction(this.amount);
}

TransactionState updateAmountReducer(TransactionState state, dynamic action) {
  if (action is updateAmountStateAction) {
    print(action.amount);
    return TransactionState(amount: action.amount);
  }
  return state;
}
