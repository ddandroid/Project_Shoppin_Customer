package com.shoppin.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppin.customer.R;
import com.shoppin.customer.database.DBAdapter;
import com.shoppin.customer.database.IDatabase;
import com.shoppin.customer.model.Suburb;
import com.shoppin.customer.network.DataRequest;
import com.shoppin.customer.network.IWebService;
import com.shoppin.customer.utils.UniqueId;
import com.shoppin.customer.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.shoppin.customer.database.IDatabase.IMap;

public class SigninActivity extends AppCompatActivity {

    private static final String TAG = SigninActivity.class.getSimpleName();

    @BindView(R.id.rlvGlobalProgressbar)
    RelativeLayout rlvGlobalProgressbar;

    @BindView(R.id.etxSigninId)
    EditText etxSigninId;
    @BindView(R.id.etxPassword)
    EditText etxPassword;

    @BindView(R.id.txtSignin)
    TextView txtSignin;

    @BindView(R.id.txtSignup)
    TextView txtSignup;

    @BindView(R.id.txtGuest)
    TextView txtGuest;

    private ArrayList<Suburb> suburbArrayList;
    private ArrayAdapter<Suburb> suburbArrayAdapter;
    private Suburb selectedSuburb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

//        rlvGlobalProgressbar.setVisibility(View.VISIBLE);
        suburbArrayList = new ArrayList<>();
        suburbArrayAdapter = new ArrayAdapter<>(SigninActivity.this, android.R.layout.simple_dropdown_item_1line, suburbArrayList);
        getSuburbs();
    }

    @OnClick(R.id.txtSignin)
    void singIn() {
        try {
            if (loginValidation()) {
                JSONObject loginParam = new JSONObject();
                loginParam.put(IWebService.KEY_REQ_CUSTOMER_MOBILE, etxSigninId.getText().toString());
                loginParam.put(IWebService.KEY_REQ_CUSTOMER_PASSWORD, etxPassword.getText().toString());
                loginParam.put(IWebService.KEY_REQ_CUSTOMER_DEVICE_TYPE, IWebService.DEVICE_TYPE);
                loginParam.put(IWebService.KEY_REQ_CUSTOMER_DEVICE_TOKEN, FirebaseInstanceId.getInstance().getToken());
                loginParam.put(IWebService.KEY_REQ_CUSTOMER_DEVICE_ID, UniqueId.getUniqueId(SigninActivity.this));

                DataRequest signinDataRequest = new DataRequest(SigninActivity.this);
                signinDataRequest.execute(IWebService.CUSTOMER_LOGIN, loginParam.toString(), new DataRequest.CallBack() {
                    public void onPreExecute() {
                        rlvGlobalProgressbar.setVisibility(View.VISIBLE);
                    }

                    public void onPostExecute(String response) {
                        try {
                            rlvGlobalProgressbar.setVisibility(View.GONE);
                            if (!DataRequest.hasError(SigninActivity.this, response, true)) {

                                JSONObject dataJObject = DataRequest.getJObjWebdata(response);

                                DBAdapter.insertUpdateMap(SigninActivity.this, IMap.CUSTOMER_ID,
                                        dataJObject.getString(IWebService.KEY_RES_CUSTOMER_ID));
                                DBAdapter.insertUpdateMap(SigninActivity.this, IMap.SUBURB_ID,
                                        dataJObject.getString(IWebService.KEY_RES_SUBURB_ID));
                                DBAdapter.insertUpdateMap(SigninActivity.this, IMap.SUBURB_NAME,
                                        dataJObject.getString(IWebService.KEY_RES_SUBURB_NAME));
                                DBAdapter.insertUpdateMap(SigninActivity.this, IMap.CUSTOMER_ADDRESS_ID,
                                        dataJObject.getString(IWebService.KEY_REQ_ADDRESS_ID));
                                DBAdapter.setMapKeyValueBoolean(SigninActivity.this, IMap.IS_LOGIN, true);

                                Intent intent = new Intent(SigninActivity.this, NavigationDrawerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean loginValidation() {
        boolean isValid = true;
        etxSigninId.setError(null);
        etxPassword.setError(null);
        if (Utils.isNullOrEmpty(etxSigninId.getText().toString())) {
            etxSigninId.setError(getString(R.string.error_required));
            etxSigninId.requestFocus();
            isValid = false;
        } else if (Utils.isNullOrEmpty(etxPassword.getText().toString())) {
            etxPassword.setError(getString(R.string.error_required));
            etxPassword.requestFocus();
            isValid = false;
        }
        return isValid;
    }

    @OnClick(R.id.txtSignup)
    void singUp() {
        Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void getSuburbs() {
        DataRequest getSuburbsDataRequest = new DataRequest(SigninActivity.this);
        getSuburbsDataRequest.execute(IWebService.GET_SUBURB, null, new DataRequest.CallBack() {
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
            }

            public void onPostExecute(String response) {
                try {
                    rlvGlobalProgressbar.setVisibility(View.GONE);
                    if (!DataRequest.hasError(SigninActivity.this, response, true)) {
                        Gson gson = new Gson();
                        JSONObject dataJObject = DataRequest.getJObjWebdata(response);

                        ArrayList<Suburb> tmpSuburbArrayList = gson.fromJson(
                                dataJObject.getJSONArray(IWebService.KEY_RES_SUBURB_LIST).toString(),
                                new TypeToken<ArrayList<Suburb>>() {
                                }.getType());
                        if (tmpSuburbArrayList != null) {
                            Log.e(TAG, "size = " + tmpSuburbArrayList.size());
                            suburbArrayList.addAll(tmpSuburbArrayList);
                            suburbArrayAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.txtGuest)
    void guestLogin() {
        showAlertGuestLogin();
    }

    private void showAlertGuestLogin() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SigninActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_guest_suburb, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();

        final AutoCompleteTextView atxSuburbDialog = (AutoCompleteTextView) dialogView.findViewById(R.id.atxSuburbDialog);
        atxSuburbDialog.setAdapter(suburbArrayAdapter);

        dialogView.findViewById(R.id.txtCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.txtGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atxSuburbDialog.setError(null);
                selectedSuburb = Utils.getSelectedSuburb(suburbArrayList, atxSuburbDialog.getText().toString());
                if (selectedSuburb == null) {
                    atxSuburbDialog.setError(getString(R.string.error_valid_suburb));
                    atxSuburbDialog.requestFocus();
                } else {
                    DBAdapter.insertUpdateMap(SigninActivity.this, IDatabase.IMap.SUBURB_ID,
                            selectedSuburb.suburb_id);
                    DBAdapter.insertUpdateMap(SigninActivity.this, IDatabase.IMap.SUBURB_NAME,
                            selectedSuburb.suburb_name);
                    DBAdapter.setMapKeyValueBoolean(SigninActivity.this, IDatabase.IMap.IS_LOGIN, false);

                    alertDialog.dismiss();

                    Intent intent = new Intent(SigninActivity.this, NavigationDrawerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }
}
