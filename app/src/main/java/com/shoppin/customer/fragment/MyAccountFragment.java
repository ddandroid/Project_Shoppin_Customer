package com.shoppin.customer.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppin.customer.R;
import com.shoppin.customer.activity.AddressEditActivity;
import com.shoppin.customer.activity.NavigationDrawerActivity;
import com.shoppin.customer.activity.SigninActivity;
import com.shoppin.customer.adapter.AddressAdapter;
import com.shoppin.customer.database.DBAdapter;
import com.shoppin.customer.database.IDatabase;
import com.shoppin.customer.model.Address;
import com.shoppin.customer.model.Suburb;
import com.shoppin.customer.network.DataRequest;
import com.shoppin.customer.network.IWebService;
import com.shoppin.customer.utils.Customer;
import com.shoppin.customer.utils.UniqueId;
import com.shoppin.customer.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.shoppin.customer.R.id.txtUpdate;
import static com.shoppin.customer.database.IDatabase.IMap;

/**
 * Created by ubuntu on 15/8/16.
 */

public class MyAccountFragment extends BaseFragment {

    private static final String TAG = MyAccountFragment.class.getSimpleName();

    @BindView(R.id.rootContainer)
    View rootContainer;

    @BindView(R.id.rlvGlobalProgressbar)
    RelativeLayout rlvGlobalProgressbar;

    @BindView(R.id.recyclerListAddress)
    RecyclerView rclAddress;

    @BindView(R.id.etxName)
    EditText etxName;

    @BindView(R.id.etxPhoneNumber)
    EditText etxPhoneNumber;

    @BindView(R.id.etxStreet)
    EditText etxStreet;

    @BindView(R.id.atxSuburb)
    AutoCompleteTextView atxSuburb;

    @BindView(R.id.etxPostcode)
    EditText etxPostcode;

    @BindView(R.id.etxPassword)
    EditText etxPassword;

    private ArrayList<Address> addressArrayList;
    private AddressAdapter addressAdapter;

    private ArrayList<Suburb> suburbArrayList;
    private ArrayAdapter<Suburb> suburbArrayAdapter;
    private Suburb selectedSuburb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        layoutView = inflater.inflate(R.layout.fragment_home, null);
        layoutView = inflater.inflate(R.layout.fragment_my_account, container, false);
        ButterKnife.bind(this, layoutView);

        rootContainer.setVisibility(View.GONE);

        addressArrayList = new ArrayList<>();
        addressAdapter = new AddressAdapter(getActivity(), addressArrayList, MyAccountFragment.this);
        rclAddress.setLayoutManager(new LinearLayoutManager(getActivity()));
        rclAddress.setAdapter(addressAdapter);

        suburbArrayList = new ArrayList<>();
        suburbArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, suburbArrayList);
        atxSuburb.setAdapter(suburbArrayAdapter);

        getSuburbs();

        return layoutView;
    }

    @Override
    public void updateFragment() {
        super.updateFragment();
        if (getActivity() != null && getActivity() instanceof NavigationDrawerActivity) {
            ((NavigationDrawerActivity) getActivity()).setToolbarTitle(
                    getActivity().getResources().getString(R.string.fragment_myaccount));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "resultCode = " + requestCode);
        if (requestCode == AddressEditActivity.REQUEST_CODE_ADDRESS) {
            Log.d(TAG, "resultCode = " + resultCode);
            if (resultCode == Activity.RESULT_OK) {
                getAddressList();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(txtUpdate)
    void updateAddress() {
        if (signupValidation()) {
            updateMyAccountDetails();
        }
    }

    @OnClick(R.id.txtAddNewAddress)
    void addNewAddress() {
        Intent intent = new Intent(getActivity(), AddressEditActivity.class);
        startActivityForResult(intent, AddressEditActivity.REQUEST_CODE_ADDRESS);
    }

    @OnClick(R.id.txtLogout)
    void logOut() {
        signOut();
    }

    @OnClick(R.id.btnExport)
    void btnExport() {
        DBAdapter.exportDB(getActivity());
    }

    private void getSuburbs() {
        DataRequest getSuburbsDataRequest = new DataRequest(getActivity());
        getSuburbsDataRequest.execute(IWebService.GET_SUBURB, null, new DataRequest.CallBack() {
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
            }

            public void onPostExecute(String response) {
                try {
                    rlvGlobalProgressbar.setVisibility(View.GONE);
                    if (!DataRequest.hasError(getActivity(), response, true)) {
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
                        //webservice call to fetch the customer details.
                        getMyProfileDetails();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getMyProfileDetails() {
        DataRequest profileDataRequest = new DataRequest(getActivity());
        JSONObject profileParams = new JSONObject();

        try {
            profileParams.put(IWebService.KEY_RES_CUSTOMER_ID, DBAdapter.getMapKeyValueString(getActivity(), IMap.CUSTOMER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        profileDataRequest.execute(IWebService.CUSTOMER_PROFILE, profileParams.toString(), new DataRequest.CallBack() {
            @Override
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPostExecute(String response) {
                rlvGlobalProgressbar.setVisibility(View.GONE);
                if (!DataRequest.hasError(getActivity(), response, true)) {
                    rootContainer.setVisibility(View.VISIBLE);
                    JSONObject dataJObject = DataRequest.getJObjWebdata(response);
                    try {
                        etxName.setText(dataJObject.getString(IWebService.KEY_REQ_CUSTOMER_NAME));
                        etxPhoneNumber.setText(dataJObject.getString(IWebService.KEY_REQ_CUSTOMER_MOBILE));
                        etxStreet.setText(dataJObject.getString(IWebService.KEY_REQ_CUSTOMER_STREET));
                        atxSuburb.setText(dataJObject.getString(IWebService.KEY_REQ_CUSTOMER_SUBURB_NAME));
                        etxPostcode.setText(dataJObject.getString(IWebService.KEY_REQ_CUSTOMER_POSTCODE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //webservice call for fetching address
                    getAddressList();
                }
            }
        });
    }

    private void getAddressList() {
        DataRequest addressDataRequest = new DataRequest(getActivity());
        JSONObject addressParams = new JSONObject();

        try {
            addressParams.put(IWebService.KEY_RES_CUSTOMER_ID, DBAdapter.getMapKeyValueString(getActivity(), IMap.CUSTOMER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addressDataRequest.execute(IWebService.CUSTOMER_ADDRESS_LIST, addressParams.toString(), new DataRequest.CallBack() {
            @Override
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
                addressArrayList.clear();
                addressAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPostExecute(String response) {
                rlvGlobalProgressbar.setVisibility(View.GONE);
                if (!DataRequest.hasError(getActivity(), response, true)) {
                    addressArrayList.clear();
                    Gson gson = new Gson();
                    JSONObject dataJObject = DataRequest.getJObjWebdata(response);
                    try {
                        ArrayList<Address> tmpAddressArrayList = gson.fromJson(
                                dataJObject.getJSONArray(IWebService.KEY_RES_ADDRESS_LIST).toString(),
                                new TypeToken<ArrayList<Address>>() {
                                }.getType());
                        if (tmpAddressArrayList != null) {
                            Log.e(TAG, "size = " + tmpAddressArrayList.size());
                            addressArrayList.addAll(tmpAddressArrayList);
                            addressAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public boolean signupValidation() {
        boolean isValid = true;
        etxName.setError(null);
        etxPhoneNumber.setError(null);
        etxStreet.setError(null);
        atxSuburb.setError(null);
        etxPostcode.setError(null);
        etxPassword.setError(null);

        selectedSuburb = Utils.getSelectedSuburb(suburbArrayList, atxSuburb.getText().toString());

        if (Utils.isNullOrEmpty(etxName.getText().toString())) {
            etxName.setError(getString(R.string.error_required));
            etxName.requestFocus();
            isValid = false;
        }
//        else if (Utils.isNullOrEmpty(etxPhoneNumber.getText().toString())) {
//            etxPhoneNumber.setError(getString(R.string.error_required));
//            etxPhoneNumber.requestFocus();
//            isValid = false;
//        }
        else if (Utils.isNullOrEmpty(etxStreet.getText().toString())) {
            etxStreet.setError(getString(R.string.error_required));
            etxStreet.requestFocus();
            isValid = false;
        } else if (selectedSuburb == null) {
            atxSuburb.setError(getString(R.string.error_valid_suburb));
            atxSuburb.requestFocus();
            isValid = false;
        } else if (Utils.isNullOrEmpty(etxPostcode.getText().toString())) {
            etxPostcode.setError(getString(R.string.error_required));
            etxPostcode.requestFocus();
            isValid = false;
        } else if (Utils.isNullOrEmpty(etxPassword.getText().toString())) {
            etxPassword.setError(getString(R.string.error_required));
            etxPassword.requestFocus();
            isValid = false;
        }
        return isValid;
    }

    private void updateMyAccountDetails() {
        DataRequest updateProfileDataRequest = new DataRequest(getActivity());
        JSONObject updateParams = new JSONObject();
        try {
            updateParams.put(IWebService.KEY_RES_CUSTOMER_ID,
                    DBAdapter.getMapKeyValueString(getActivity(), IMap.CUSTOMER_ID));
            updateParams.put(IWebService.KEY_REQ_ADDRESS_ID,
                    DBAdapter.getMapKeyValueString(getActivity(), IMap.CUSTOMER_ADDRESS_ID));
            updateParams.put(IWebService.KEY_REQ_CUSTOMER_NAME, etxName.getText().toString());
//            updateParms.put(IWebService.KEY_REQ_CUSTOMER_MOBILE, etxPhoneNumber.getText().toString());
            updateParams.put(IWebService.KEY_REQ_CUSTOMER_STREET, etxStreet.getText().toString());
            updateParams.put(IWebService.KEY_REQ_CUSTOMER_SUBURB_ID, selectedSuburb.suburb_id);
            updateParams.put(IWebService.KEY_REQ_CUSTOMER_POSTCODE, etxPostcode.getText().toString());
            updateParams.put(IWebService.KEY_REQ_CUSTOMER_PASSWORD, etxPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateProfileDataRequest.execute(IWebService.CUSTOMER_ACCOUNT_UPDATE, updateParams.toString(), new DataRequest.CallBack() {
            @Override
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPostExecute(String response) {
                rlvGlobalProgressbar.setVisibility(View.GONE);
                Log.d(TAG, "response = " + response);
                if (!DataRequest.hasError(getActivity(), response, true)) {
                    try {
                        DBAdapter.insertUpdateMap(getActivity(), IDatabase.IMap.SUBURB_ID,
                                selectedSuburb.suburb_id);
                        DBAdapter.insertUpdateMap(getActivity(), IDatabase.IMap.SUBURB_NAME,
                                selectedSuburb.suburb_name);
                        JSONObject responseJObject = new JSONObject(response);
                        Utils.showToastShort(getActivity(), responseJObject.getString(IWebService.KEY_RES_MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void signOut() {
        JSONObject myOrderParam = new JSONObject();
        try {
            myOrderParam.put(IWebService.KEY_REQ_CUSTOMER_ID,
                    DBAdapter.getMapKeyValueString(getActivity(), IMap.CUSTOMER_ID));
            myOrderParam.put(IWebService.KEY_REQ_CUSTOMER_DEVICE_ID, UniqueId.getUniqueId(getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataRequest getSuburbsDataRequest = new DataRequest(getActivity());
        getSuburbsDataRequest.execute(IWebService.CUSTOMER_SIGN_OUT, myOrderParam.toString(), new DataRequest.CallBack() {
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
            }

            public void onPostExecute(String response) {
                try {
                    rlvGlobalProgressbar.setVisibility(View.GONE);
                    if (!DataRequest.hasError(getActivity(), response, true)) {
                        Customer.singOut(getActivity());
                        Intent intent = new Intent(getActivity(), SigninActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
