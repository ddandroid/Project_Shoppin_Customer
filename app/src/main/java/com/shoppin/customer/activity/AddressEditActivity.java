package com.shoppin.customer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppin.customer.R;
import com.shoppin.customer.database.DBAdapter;
import com.shoppin.customer.database.IDatabase;
import com.shoppin.customer.model.Address;
import com.shoppin.customer.model.Suburb;
import com.shoppin.customer.network.DataRequest;
import com.shoppin.customer.network.IWebService;
import com.shoppin.customer.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ubuntu on 25/8/16.
 */

public class AddressEditActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_ADDRESS = 1003;
    public static final String KEY_ADDRESS = "address";

    private static final String TAG = AddressEditActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rlvGlobalProgressbar)
    RelativeLayout rlvGlobalProgressbar;
    @BindView(R.id.etxName)
    EditText etxName;
    @BindView(R.id.etxPhone)
    EditText etxPhone;
    @BindView(R.id.etxStreet)
    EditText etxStreet;
    @BindView(R.id.atxSuburb)
    AutoCompleteTextView atxSuburb;
    @BindView(R.id.etxPostcode)
    EditText etxPostCode;


    private int addressPosition1;
    //    private ArrayList<Address> addressArrayList;
    private Address address;
    private ArrayList<Suburb> suburbArrayList;
    private ArrayAdapter<Suburb> suburbArrayAdapter;
    private Suburb selectedSuburb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        ButterKnife.bind(this);

//        Intent intent = getIntent();
//        if (intent != null) {
//            addressArrayList = intent.getParcelableArrayListExtra(KEY_ADDRESS_LIST);
//            Log.d(TAG, "addressArrayList.size() = " + addressArrayList.size());
//            addressPosition = intent.getIntExtra(KEY_ADDRESS_POSITION, -1);
//            Log.d(TAG, "postion = " + addressPosition);
//
//            if (addressArrayList != null && addressPosition >= 0) {
//                loadAddress(addressArrayList.get(addressPosition));
//            }
//        }

        if (getIntent() != null && getIntent().hasExtra(KEY_ADDRESS)) {
            address = getIntent().getParcelableExtra(KEY_ADDRESS);
            if (address != null) {
                loadAddress(address);
            }
        }

        if (toolbar != null) {
//            toolbar.setNavigationIcon(R.drawable.menu_icon);
//            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.menu_icon));
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getResources().getString(R.string.activity_address));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        suburbArrayList = new ArrayList<>();
        suburbArrayAdapter = new ArrayAdapter<>(AddressEditActivity.this, android.R.layout.simple_dropdown_item_1line, suburbArrayList);
        atxSuburb.setAdapter(suburbArrayAdapter);
        getSuburbs();
    }

    private void loadAddress(Address currentAddress) {
        etxName.setText(currentAddress.name);
        etxPhone.setText(currentAddress.phoneNumber);
        etxStreet.setText(currentAddress.street);
        atxSuburb.setText(currentAddress.suburbName);
        etxPostCode.setText(currentAddress.postCode);
    }

    private void getSuburbs() {
        DataRequest getSuburbsDataRequest = new DataRequest(AddressEditActivity.this);
        getSuburbsDataRequest.execute(IWebService.GET_SUBURB, null, new DataRequest.CallBack() {
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
            }

            public void onPostExecute(String response) {
                try {
                    rlvGlobalProgressbar.setVisibility(View.GONE);
                    if (!DataRequest.hasError(AddressEditActivity.this, response, true)) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_address_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                if (validData()) {
                    if (address != null) {
                        updateAddress();
                    } else {
                        addAddress();
                    }
                }
                return true;
            case R.id.action_delete:
                if (address != null) {
                    deleteAddress();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validData() {
        boolean isValid = true;
        etxName.setError(null);
        etxPhone.setError(null);
        etxStreet.setError(null);
        atxSuburb.setError(null);
        etxPostCode.setError(null);

        selectedSuburb = Utils.getSelectedSuburb(suburbArrayList, atxSuburb.getText().toString());

        if (Utils.isNullOrEmpty(etxName.getText().toString())) {
            etxName.setError(getString(R.string.error_required));
            etxName.requestFocus();
            isValid = false;
        } else if (Utils.isNullOrEmpty(etxPhone.getText().toString())) {
            etxPhone.setError(getString(R.string.error_required));
            etxPhone.requestFocus();
            isValid = false;
        } else if (Utils.isNullOrEmpty(etxStreet.getText().toString())) {
            etxStreet.setError(getString(R.string.error_required));
            etxStreet.requestFocus();
            isValid = false;
        } else if (selectedSuburb == null) {
            atxSuburb.setError(getString(R.string.error_valid_suburb));
            atxSuburb.requestFocus();
            isValid = false;
        } else if (Utils.isNullOrEmpty(etxPostCode.getText().toString())) {
            etxPostCode.setError(getString(R.string.error_required));
            etxPostCode.requestFocus();
            isValid = false;
        } else if (Utils.isNullOrEmpty(etxPostCode.getText().toString())) {
            etxPostCode.setError(getString(R.string.error_required));
            etxPostCode.requestFocus();
            isValid = false;
        }
        return isValid;
    }

    private void deleteAddress() {
        DataRequest deleteDataRequest = new DataRequest(AddressEditActivity.this);
        JSONObject deleteParams = new JSONObject();
        try {
            deleteParams.put(IWebService.KEY_REQ_ADDRESS_ID, address.addressId);
            deleteParams.put(IWebService.KEY_REQ_CUSTOMER_ID, address.addressId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        deleteDataRequest.execute(IWebService.CUSTOMER_DELETE_ADDRESS, deleteParams.toString(), new DataRequest.CallBack() {
            @Override
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPostExecute(String response) {
                rlvGlobalProgressbar.setVisibility(View.GONE);
                if (!DataRequest.hasError(AddressEditActivity.this, response, true)) {
                    try {
                        JSONObject responseJObject = new JSONObject(response);
                        Utils.showToastShort(AddressEditActivity.this, responseJObject.getString(IWebService.KEY_RES_MESSAGE));
                        backSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void addAddress() {
        DataRequest addNewAddressDataRequest = new DataRequest(AddressEditActivity.this);
        JSONObject addressParams = new JSONObject();

        try {
            addressParams.put(IWebService.KEY_RES_CUSTOMER_ID,
                    DBAdapter.getMapKeyValueString(AddressEditActivity.this, IDatabase.IMap.CUSTOMER_ID));
            addressParams.put(IWebService.KEY_REQ_CUSTOMER_NAME, etxName.getText().toString().trim());
            addressParams.put(IWebService.KEY_REQ_CUSTOMER_MOBILE, etxPhone.getText().toString().trim());
            addressParams.put(IWebService.KEY_REQ_CUSTOMER_STREET, etxStreet.getText().toString().trim());
            addressParams.put(IWebService.KEY_REQ_CUSTOMER_SUBURB_ID, selectedSuburb.suburb_id);
            addressParams.put(IWebService.KEY_REQ_CUSTOMER_POSTCODE, etxPostCode.getText().toString().trim());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        addNewAddressDataRequest.execute(IWebService.CUSTOMER_ADD_ADDRESS, addressParams.toString(), new DataRequest.CallBack() {
            @Override
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPostExecute(String response) {
                rlvGlobalProgressbar.setVisibility(View.GONE);
                if (!DataRequest.hasError(AddressEditActivity.this, response, true)) {
                    JSONObject responseJObject = null;
                    try {
                        responseJObject = new JSONObject(response);
                        Utils.showToastShort(AddressEditActivity.this, responseJObject.getString(IWebService.KEY_RES_MESSAGE));
                        backSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateAddress() {
        DataRequest updateAddressDataRequest = new DataRequest(AddressEditActivity.this);
        JSONObject updateParams = new JSONObject();
        try {
            updateParams.put(IWebService.KEY_REQ_ADDRESS_ID, address.addressId);
            updateParams.put(IWebService.KEY_REQ_CUSTOMER_NAME, etxName.getText().toString().trim());
            updateParams.put(IWebService.KEY_REQ_CUSTOMER_MOBILE, etxPhone.getText().toString().trim());
            updateParams.put(IWebService.KEY_REQ_CUSTOMER_STREET, etxStreet.getText().toString().trim());
            updateParams.put(IWebService.KEY_REQ_CUSTOMER_SUBURB_ID, selectedSuburb.suburb_id);
            updateParams.put(IWebService.KEY_REQ_CUSTOMER_POSTCODE, etxPostCode.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateAddressDataRequest.execute(IWebService.CUSTOMER_UPDATE_ADDRESS, updateParams.toString(), new DataRequest.CallBack() {
            @Override
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPostExecute(String response) {
                rlvGlobalProgressbar.setVisibility(View.GONE);
                if (!DataRequest.hasError(AddressEditActivity.this, response, true)) {
                    try {
                        JSONObject responseJObject = new JSONObject(response);
                        Utils.showToastShort(AddressEditActivity.this, responseJObject.getString(IWebService.KEY_RES_MESSAGE));
                        backSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void backSuccess() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

}

