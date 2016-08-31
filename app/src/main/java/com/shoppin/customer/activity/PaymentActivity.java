package com.shoppin.customer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.shoppin.customer.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.clickToPay)
    void clickToPay() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(CheckOutActivity.RESPONSE_PAYMENT_ID,"payment123");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}