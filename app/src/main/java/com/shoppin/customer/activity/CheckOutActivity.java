package com.shoppin.customer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppin.customer.R;
import com.shoppin.customer.adapter.CheckoutDateAdapter;
import com.shoppin.customer.adapter.SelectionAdapter;
import com.shoppin.customer.database.DBAdapter;
import com.shoppin.customer.database.IDatabase.IMap;
import com.shoppin.customer.model.Address;
import com.shoppin.customer.model.CheckoutDate;
import com.shoppin.customer.model.Coupon;
import com.shoppin.customer.model.Product;
import com.shoppin.customer.model.Store;
import com.shoppin.customer.network.DataRequest;
import com.shoppin.customer.network.IWebService;
import com.shoppin.customer.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckOutActivity extends AppCompatActivity {
    public static final int REQUEST_PAYMENT = 1001;
    public static final String KEY_BUNDLE_PRODUCT_LIST = "product_list";
    public static final String RESPONSE_PAYMENT_ID = "payment_id";
    public static final String KEY_ADDRESS = "address";

    private static final String TAG = CheckOutActivity.class.getSimpleName();

    @BindView(R.id.toolbarCheckOut)
    Toolbar toolbar;

    @BindView(R.id.rlvGlobalProgressbar)
    RelativeLayout rlvGlobalProgressbar;

    @BindView(R.id.rootContainer)
    View rootContainer;

    @BindView(R.id.txtCustomerName)
    TextView txtCustomerName;

    @BindView(R.id.txtPhone)
    TextView txtPhone;

    @BindView(R.id.txtStreet)
    TextView txtStreet;

    @BindView(R.id.txtSuburb)
    TextView txtSuburb;

    @BindView(R.id.txtPostCode)
    TextView txtPostCode;

    @BindView(R.id.imgEditAddress)
    ImageView imgEditAddress;

    @BindView(R.id.imgAddNewAddress)
    ImageView imgAddNewAddress;

    @BindView(R.id.etxCoupon)
    EditText etxCoupon;

    @BindView(R.id.txtStore)
    TextView txtStore;

    @BindView(R.id.cardStore)
    CardView cardStore;

    @BindView(R.id.dateRecyclerView)
    RecyclerView dateRecyclerView;

    @BindView(R.id.timeRecyclerView)
    RecyclerView timeRecyclerView;

    @BindView(R.id.txtAllProductPrice)
    TextView txtAllProductPrice;

    @BindView(R.id.txtCouponPrice)
    TextView txtCouponPrice;

    @BindView(R.id.txtTotalPrice)
    TextView txtTotalPrice;

    @BindView(R.id.txtGrandTotal)
    TextView txtGrandTotal;
    String paymentId;
    private ArrayList<CheckoutDate> checkoutDateArrayList;
    private CheckoutDateAdapter checkoutDateAdapter;
    private ArrayList<Address> addressArrayList;
    private ArrayList<Store> storeArrayList;
    private Address currentAddress;
    private Coupon currentCoupon = new Coupon();
    private double allProductPrice = 0.0;
    private double totalPrice = 0.0;

    private  ArrayList<Product> productArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        ButterKnife.bind(this);

        productArrayList = new ArrayList<>();
        if(getIntent()!=null && getIntent().hasExtra(KEY_BUNDLE_PRODUCT_LIST)) {
            productArrayList.addAll( (ArrayList<Product>) getIntent().getSerializableExtra(KEY_BUNDLE_PRODUCT_LIST));
        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imgEditAddress.setVisibility(View.GONE);
        imgAddNewAddress.setVisibility(View.VISIBLE);

        checkoutDateArrayList = new ArrayList<>();
        checkoutDateAdapter = new CheckoutDateAdapter(CheckOutActivity.this, checkoutDateArrayList, timeRecyclerView);
        LinearLayoutManager horizontalLayoutManagaerdate
                = new LinearLayoutManager(CheckOutActivity.this, LinearLayoutManager.HORIZONTAL, false);
        dateRecyclerView.setLayoutManager(horizontalLayoutManagaerdate);
        dateRecyclerView.setAdapter(checkoutDateAdapter);

        addressArrayList = new ArrayList<>();
        storeArrayList = new ArrayList<>();

        getCheckOutDetail();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                paymentId = data.getStringExtra(RESPONSE_PAYMENT_ID);
                Log.d(TAG, "paymentId = " + paymentId);
                placeOrder();
//                Utils.showToastShort(CheckOutActivity.this, "Successfully");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Utils.showToastShort(CheckOutActivity.this, "Unsuccessfully");
            }
        } else if (requestCode == AddressEditActivity.REQUEST_CODE_ADDRESS) {
            if (resultCode == Activity.RESULT_OK) {
//                String paymentId = data.getStringExtra(RESPONSE_PAYMENT_ID);
                if (data != null && data.hasExtra(KEY_ADDRESS)) {
                    Address address = data.getParcelableExtra(KEY_ADDRESS);
                    setCurrentAddress(address);
                }
            }
        }
    }

    @OnClick(R.id.imgAddNewAddress)
    void addNewAddress() {
        Intent intent = new Intent(CheckOutActivity.this, ChooseDeliveryActivity.class);
        startActivityForResult(intent, AddressEditActivity.REQUEST_CODE_ADDRESS);
    }


    @OnClick(R.id.txtApplyCoupon)
    void applyCoupon() {
//        Utils.showToastShort(CheckOutActivity.this, getString(R.string.under_development));
        getCouponVerify(etxCoupon.getText().toString(), allProductPrice);
    }

    @OnClick(R.id.txtStore)
    void selectNearByStore() {
        showAlertNearByStore();
    }

    @OnClick(R.id.txtPay)
    void makePayment() {
        if (checkoutValidation()) {
            Intent intent = new Intent(CheckOutActivity.this, PaymentActivity.class);
            startActivityForResult(intent, REQUEST_PAYMENT);
        }
    }

    private void getCheckOutDetail() {
        try {
            JSONObject loginParam = new JSONObject();
            loginParam.put(IWebService.KEY_REQ_CUSTOMER_ID,
                    DBAdapter.getMapKeyValueString(CheckOutActivity.this, IMap.CUSTOMER_ID));
            loginParam.put(IWebService.KEY_REQ_CUSTOMER_SUBURB_ID,
                    DBAdapter.getMapKeyValueString(CheckOutActivity.this, IMap.SUBURB_ID));

            DataRequest checkOutDetailDataRequest = new DataRequest(CheckOutActivity.this);
            checkOutDetailDataRequest.execute(IWebService.GET_CHECKOUT_DETAIL, loginParam.toString(), new DataRequest.CallBack() {
                public void onPreExecute() {
                    rlvGlobalProgressbar.setVisibility(View.VISIBLE);
                    rootContainer.setVisibility(View.GONE);
                }

                public void onPostExecute(String response) {
                    try {
                        rlvGlobalProgressbar.setVisibility(View.GONE);
                        if (!DataRequest.hasError(CheckOutActivity.this, response, true)) {
                            rootContainer.setVisibility(View.VISIBLE);
                            JSONObject dataJObject = DataRequest.getJObjWebdata(response);

                            Gson gson = new Gson();

                            ArrayList<CheckoutDate> tmpCheckoutDateAdapter = gson.fromJson(
                                    dataJObject.getJSONArray(IWebService.KEY_RES_DATE_LIST).toString(),
                                    new TypeToken<ArrayList<CheckoutDate>>() {
                                    }.getType());
                            if (tmpCheckoutDateAdapter != null) {
                                checkoutDateArrayList.clear();
                                checkoutDateArrayList.addAll(tmpCheckoutDateAdapter);
                                checkoutDateArrayList.get(0).isSelected = true;
                                checkoutDateArrayList.get(0).checkoutTimesArrayList.get(0).isSelected = true;
                                checkoutDateAdapter.notifyDataSetChanged();
                                Log.d(TAG, "checkoutDateArrayList.size() = " + checkoutDateArrayList.size());
                            }

                            ArrayList<Address> tmpAddressArrayList = gson.fromJson(
                                    dataJObject.getJSONArray(IWebService.KEY_RES_ADDRESS_LIST).toString(),
                                    new TypeToken<ArrayList<Address>>() {
                                    }.getType());
                            if (tmpAddressArrayList != null) {
                                addressArrayList.clear();
                                addressArrayList.addAll(tmpAddressArrayList);
                                Log.d(TAG, "addressArrayList.size() = " + addressArrayList.size());
                            }

                            ArrayList<Store> tmpStoreArrayList = gson.fromJson(
                                    dataJObject.getJSONArray(IWebService.KEY_RES_STORE_LIST).toString(),
                                    new TypeToken<ArrayList<Store>>() {
                                    }.getType());
                            if (tmpAddressArrayList != null) {
                                storeArrayList.clear();
                                storeArrayList.addAll(tmpStoreArrayList);
                                Log.d(TAG, "storeArrayList.size() = " + storeArrayList.size());
                            }

                            setCheckoutDetailOnUi();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCheckoutDetailOnUi() {
        if (addressArrayList != null && addressArrayList.size() > 0) {
            setCurrentAddress(addressArrayList.get(0));
        }

        if (storeArrayList != null && storeArrayList.size() > 0) {
            cardStore.setVisibility(View.VISIBLE);
        } else {
            cardStore.setVisibility(View.GONE);
        }

        setTotal();
    }

    private void setCurrentAddress(Address tmpCurrentAddress) {
        this.currentAddress = null;
        this.currentAddress = tmpCurrentAddress;
        txtCustomerName.setText(currentAddress.name);
        txtPhone.setText(currentAddress.phoneNumber);
        txtStreet.setText(currentAddress.street);
        txtSuburb.setText(currentAddress.suburbName);
        txtPostCode.setText(currentAddress.postCode);
    }

    private void setTotal() {
        allProductPrice = 0.0;
        double couponPrice = 0.0;
        totalPrice = 0.0;

//        allProductPrice = Cart.getCartSalePriceTotal(DBAdapter.getAllProductFromCart(CheckOutActivity.this));

        couponPrice = currentCoupon.discountAmount;

        txtAllProductPrice.setText("$ " + allProductPrice);
        txtCouponPrice.setText("$ " + couponPrice);

        totalPrice = allProductPrice - couponPrice;
        txtTotalPrice.setText("$ " + totalPrice);

        txtGrandTotal.setText("$ " + totalPrice);
    }

    private void showAlertNearByStore() {
        TextView txtSelectionDone;
        RecyclerView recyclerFilter;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                CheckOutActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_selection, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        ((TextView) dialogView.findViewById(R.id.txtSelectionType))
                .setText(getResources().getString(R.string.text_select_near_by_store));
        txtSelectionDone = (TextView) dialogView.findViewById(R.id.txtSelectionDone);
        txtSelectionDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                if (selectedEntityBoard != null) {
//                    alertDialog.dismiss();
//                    updateDropdown(UPDATE_ENTITY_BORAD);
//                } else {
//                    Utils.showToastShort(getActivity(), getResources().getString(R.string.validation_entity_board));
//                }
            }
        });

        recyclerFilter = (RecyclerView) dialogView.findViewById(R.id.recyclerFilter);
        final SelectionAdapter<Store> filterStateAdapter = new SelectionAdapter<Store>(storeArrayList);
        filterStateAdapter
                .setBindAdapterInterface(new SelectionAdapter.IBindAdapterValues<Store>() {
                    @Override
                    public void bindValues(SelectionAdapter.MyViewHolder holder, final int position) {
                        // TODO Auto-generated method stub

                        holder.txtSelectionValue.setText(storeArrayList.get(position).storeName);
                        holder.txtSelectionValue.setChecked(storeArrayList.get(position).isSelected);
                        if (storeArrayList.get(position).isSelected) {
                            holder.txtSelectionValue.setTextColor(ContextCompat.getColor(CheckOutActivity.this, R.color.app_theme_1));
                        } else {
                            holder.txtSelectionValue.setTextColor(ContextCompat.getColor(CheckOutActivity.this, R.color.text_black));
                        }
                        holder.txtSelectionValue
                                .setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        // TODO Auto-generated method stub
                                        for (int i = 0; i < storeArrayList.size(); i++) {
                                            storeArrayList.get(i).isSelected = false;
                                        }
                                        storeArrayList.get(position).isSelected = true;
                                        txtStore.setText(storeArrayList.get(position).storeName);
                                        filterStateAdapter.notifyDataSetChanged();

                                        alertDialog.dismiss();
                                    }
                                });
                    }
                });
        recyclerFilter.setLayoutManager(new LinearLayoutManager(CheckOutActivity.this));
        recyclerFilter.setAdapter(filterStateAdapter);
        alertDialog.getWindow().setBackgroundDrawableResource(
                R.color.transparent);
//        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private boolean checkoutValidation() {
        boolean isValid = true;

        if (currentAddress == null) {
            isValid = false;
            Utils.showToastShort(CheckOutActivity.this, getString(R.string.error_checkout_address));
        } else {
            boolean isDateTimeSelected = false;
            for (int indexDate = 0; indexDate < checkoutDateArrayList.size(); indexDate++) {
                if (checkoutDateArrayList.get(indexDate).isSelected) {
                    for (int indexTime = 0; indexTime < checkoutDateArrayList.get(indexDate).checkoutTimesArrayList.size(); indexTime++) {
                        if (checkoutDateArrayList.get(indexDate).checkoutTimesArrayList.get(indexTime).isSelected) {
                            isDateTimeSelected = true;
                        }
                    }
                }
            }
            if (!isDateTimeSelected) {
                isValid = false;
                Utils.showToastShort(CheckOutActivity.this, getString(R.string.error_checkout_date_time));
            }
        }
        return isValid;
    }

    private void placeOrderSuccessful() {
        Intent intent = new Intent(CheckOutActivity.this, PaymentSuccessfulActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void placeOrder() {
        try {
            DataRequest placeOrderDataRequest = new DataRequest(CheckOutActivity.this);
            placeOrderDataRequest.execute(IWebService.PLACE_ORDER, generatePlaceOrderJson(), new DataRequest.CallBack() {
                public void onPreExecute() {
                    rlvGlobalProgressbar.setVisibility(View.VISIBLE);
                }

                public void onPostExecute(String response) {
                    try {
                        rlvGlobalProgressbar.setVisibility(View.GONE);
                        if (!DataRequest.hasError(CheckOutActivity.this, response, true)) {
                            placeOrderSuccessful();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generatePlaceOrderJson() {
        String placeOrderJObject = null;
        try {
            JSONObject mainJObject = new JSONObject();

            /**
             * Order json object
             */
            JSONObject orderJObject = new JSONObject();

            orderJObject.put(IWebService.KEY_REQ_CUSTOMER_ID,
                    DBAdapter.getMapKeyValueString(CheckOutActivity.this, IMap.CUSTOMER_ID));
            orderJObject.put(IWebService.KEY_REQ_ORDER_SUBURB_ID,
                    DBAdapter.getMapKeyValueString(CheckOutActivity.this, IMap.SUBURB_ID));
            orderJObject.put(IWebService.KEY_REQ_CUSTOMER_BILLING_ID, currentAddress.addressId);
            orderJObject.put(IWebService.KEY_REQ_CUSTOMER_SHIPPING_ID, currentAddress.addressId);

            // Set slot id
            String slotId = "0";
            for (int indexDate = 0; indexDate < checkoutDateArrayList.size(); indexDate++) {
                if (checkoutDateArrayList.get(indexDate).isSelected) {
                    for (int indexTime = 0; indexTime < checkoutDateArrayList.get(indexDate).checkoutTimesArrayList.size(); indexTime++) {
                        if (checkoutDateArrayList.get(indexDate).checkoutTimesArrayList.get(indexTime).isSelected) {
                            slotId = checkoutDateArrayList.get(indexDate).checkoutTimesArrayList.get(indexTime).slotId;
                        }
                    }
                }
            }
            orderJObject.put(IWebService.KEY_REQ_ORDER_SLOT_ID, slotId);

            // Set store id
            int storeId = 0;
            if (storeArrayList != null) {
                for (int i = 0; i < storeArrayList.size(); i++) {
                    if (storeArrayList.get(i).isSelected) {
                        storeId = storeArrayList.get(i).storeId;
                    }
                }
            }
            orderJObject.put(IWebService.KEY_REQ_PREFERRED_STORE_ID, storeId);


            setTotal();
            orderJObject.put(IWebService.KEY_REQ_SUB_TOTAL, allProductPrice);
            orderJObject.put(IWebService.KEY_REQ_TOTAL, totalPrice);
            mainJObject.put(IWebService.KEY_REQ_ORDER, orderJObject);

            /**
             * Coupon json object
             */
            JSONObject couponJObject = new JSONObject();
            couponJObject.put(IWebService.KEY_REQ_COUPON_ID, currentCoupon.couponCode);
            couponJObject.put(IWebService.KEY_REQ_COUPON_DISCOUNT_AMOUNT, currentCoupon.discountAmount);
            mainJObject.put(IWebService.KEY_REQ_COUPON, couponJObject);

            /**
             * Payment json object
             */
            JSONObject paymentJObject = new JSONObject();
            paymentJObject.put(IWebService.KEY_REQ_PAYMENT_TRANSACTION_ID, paymentId);
            mainJObject.put(IWebService.KEY_REQ_PAYMENT, paymentJObject);

            /**
             * Product list json object
             */
            JSONArray productJArray = new JSONArray();

//            ArrayList<Product> productArrayList = DBAdapter.getAllProductFromCart(CheckOutActivity.this);
            for (int indexProduct = 0; indexProduct < productArrayList.size(); indexProduct++) {
                Product product = productArrayList.get(indexProduct);
                JSONObject productJObject = new JSONObject();
                productJObject.put(IWebService.KEY_REQ_PRODUCT_ID, product.productId);
                productJObject.put(IWebService.KEY_REQ_PRODUCT_NAME, product.productName);
                productJObject.put(IWebService.KEY_REQ_PRODUCT_QUANTITY, product.productQuantity);
                productJObject.put(IWebService.KEY_REQ_PRODUCT_PRICE, product.productPrice);
                productJObject.put(IWebService.KEY_REQ_PRODUCT_SALEPRICE, product.productSalePrice);

                JSONArray optionJArray = new JSONArray();
                if (product.productHasOption) {
                    for (int indexOption = 0; indexOption < product.productOptionArrayList.size(); indexOption++) {
                        JSONObject optionJObject = new JSONObject();
                        for (int indexValue = 0;
                             indexValue < product.productOptionArrayList.get(indexOption).productOptionValueArrayList.size();
                             indexValue++) {
                            if (product.productOptionArrayList.get(indexOption).productOptionValueArrayList.get(indexValue).isSelected) {
                                optionJObject.put(IWebService.KEY_REQ_PRODUCT_ID, product.productId);
                                optionJObject.put(IWebService.KEY_REQ_OPTION_ID, product.productOptionArrayList.get(indexOption).optionId);
                                optionJObject.put(IWebService.KEY_REQ_OPTION_NAME, product.productOptionArrayList.get(indexOption).optionName);
                                optionJObject.put(IWebService.KEY_REQ_OPTION_VALUE_ID,
                                        product.productOptionArrayList.get(indexOption).productOptionValueArrayList.get(indexValue).optionValueId);
                                optionJObject.put(IWebService.KEY_REQ_OPTION_VALUE_NAME,
                                        product.productOptionArrayList.get(indexOption).productOptionValueArrayList.get(indexValue).optionValueName);
                                optionJArray.put(optionJObject);
                                break;
                            }
                        }
                    }
                }
                productJObject.put(IWebService.KEY_REQ_OPTION_LIST, optionJArray);

                productJArray.put(productJObject);
            }
            mainJObject.put(IWebService.KEY_REQ_PRODUCT_LIST, productJArray);
            placeOrderJObject = mainJObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "placeOrderJObject = " + placeOrderJObject);
        return placeOrderJObject;
    }

    private void getCouponVerify(final String couponNumber, double subTotalPrice) {
        JSONObject couponParam = new JSONObject();
        try {
            couponParam.put(IWebService.KEY_REQ_CUSTOMER_ID,
                    DBAdapter.getMapKeyValueString(CheckOutActivity.this, IMap.CUSTOMER_ID));
            couponParam.put(IWebService.KEY_REQ_COUPON_CODE, couponNumber);
            couponParam.put(IWebService.KEY_REQ_ORDER_AMOUNT, subTotalPrice);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataRequest couponVerifyDataRequest = new DataRequest(CheckOutActivity.this);
        couponVerifyDataRequest.execute(IWebService.REDEEM_COUPON, couponParam.toString(), new DataRequest.CallBack() {
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
            }

            public void onPostExecute(String response) {
                try {
                    rlvGlobalProgressbar.setVisibility(View.GONE);
                    if (!DataRequest.hasError(CheckOutActivity.this, response, true)) {
                        JSONObject dataJObject = DataRequest.getJObjWebdata(response);

                        currentCoupon = new Coupon();
                        currentCoupon.couponCode = couponNumber;
                        currentCoupon.couponId = dataJObject.getString(IWebService.KEY_RES_COUPON_ID);
                        currentCoupon.discountAmount = dataJObject.getDouble(IWebService.KEY_RES_DISCOUNT_AMOUNT);

                        setTotal();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
