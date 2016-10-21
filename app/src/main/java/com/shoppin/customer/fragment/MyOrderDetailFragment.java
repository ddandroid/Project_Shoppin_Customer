package com.shoppin.customer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppin.customer.R;
import com.shoppin.customer.activity.NavigationDrawerActivity;
import com.shoppin.customer.adapter.ProductListRepeatAdapter;
import com.shoppin.customer.database.DBAdapter;
import com.shoppin.customer.model.Address;
import com.shoppin.customer.model.Order;
import com.shoppin.customer.model.Product;
import com.shoppin.customer.model.Store;
import com.shoppin.customer.network.DataRequest;
import com.shoppin.customer.network.IWebService;
import com.shoppin.customer.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ubuntu on 30/7/16.
 */
public class MyOrderDetailFragment extends BaseFragment {
    private static final String ORDER_NUMBER = "order_number";

    private static final String TAG = MyOrderDetailFragment.class.getSimpleName();

    @BindView(R.id.rootContainer)
    View rootContainer;

    @BindView(R.id.rlvGlobalProgressbar)
    View rlvGlobalProgressbar;

    @BindView(R.id.txtOrderNumber)
    TextView txtOrderNumber;

    @BindView(R.id.txtOrderPlacedOn)
    TextView txtOrderPlacedOn;

    @BindView(R.id.txtOrderDeliveryDateTime)
    TextView txtOrderDeliveryDateTime;

    @BindView(R.id.txtOrderCompleteDateTime)
    TextView txtOrderCompleteDateTime;

    @BindView(R.id.txtCustomerName)
    TextView txtCustomerName;

    @BindView(R.id.txtOrderAddress)
    TextView txtOrderAddress;

    @BindView(R.id.txtOrderSuburb)
    TextView txtOrderSuburb;

    @BindView(R.id.txtOrderTotal)
    TextView txtOrderTotal;

    @BindView(R.id.txtOrderItemCount)
    TextView txtOrderItemCount;

    @BindView(R.id.txtOrderShippingPhone)
    TextView txtOrderShippingPhone;

    /**
     * For preferred store
     */
    @BindView(R.id.lltPreferredStore)
    LinearLayout lltPreferredStore;

    @BindView(R.id.txtPreferredStoreName)
    TextView txtPreferredStoreName;

    @BindView(R.id.txtPreferredStoreAddress)
    TextView txtPreferredStoreAddress;

    /**
     * For order status
     */
    @BindView(R.id.lltOrderStatus)
    LinearLayout lltOrderStatus;

    @BindView(R.id.lltAccepted)
    LinearLayout lltAccepted;

    @BindView(R.id.imgAccepted)
    ImageView imgAccepted;

    @BindView(R.id.txtAccepted)
    TextView txtAccepted;

    @BindView(R.id.lltPurchasing)
    LinearLayout lltPurchasing;

    @BindView(R.id.imgPurchasing)
    ImageView imgPurchasing;

    @BindView(R.id.txtPurchasing)
    TextView txtPurchasing;

    @BindView(R.id.lltShipping)
    LinearLayout lltShipping;

    @BindView(R.id.imgShipping)
    ImageView imgShipping;

    @BindView(R.id.txtShipping)
    TextView txtShipping;

    @BindView(R.id.lltCompleted)
    LinearLayout lltCompleted;

    @BindView(R.id.imgCompleted)
    ImageView imgCompleted;

    @BindView(R.id.txtCompleted)
    TextView txtCompleted;

    @BindView(R.id.txtOrderHoldCancel)
    TextView txtOrderHoldCancel;

    @BindView(R.id.recyclerListProduct)
    RecyclerView recyclerListProduct;

    String orderNumber;

    private ArrayList<Product> productArrayList;
    private ProductListRepeatAdapter productListRepeatAdapter;

    public static MyOrderDetailFragment newInstance(String orderNumber) {
        MyOrderDetailFragment myOrderDetailFragment = new MyOrderDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ORDER_NUMBER, orderNumber);
        myOrderDetailFragment.setArguments(bundle);
        return myOrderDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_my_order_detail, container, false);
        ButterKnife.bind(this, layoutView);

        rootContainer.setVisibility(View.GONE);

        if (getArguments() != null) {
            orderNumber = getArguments().getString(ORDER_NUMBER);
        }

        productArrayList = new ArrayList<>();
        productListRepeatAdapter = new ProductListRepeatAdapter(getActivity(), productArrayList);
//        myOrderAdapter.setOnItemClickListener(new MyOrderAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
////                NavigationDrawerActivity navigationDrawerActivity = (NavigationDrawerActivity) getActivity();
////                if (navigationDrawerActivity != null) {
////                    navigationDrawerActivity.switchContent(ProductDetailFragment.newInstance(orderArrayList.get(position).productId), false);
////                }
//            }
//        });
        recyclerListProduct.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerListProduct.setAdapter(productListRepeatAdapter);
//
        getMyOrderDetail();
        return layoutView;
    }

    @OnClick(R.id.txtRepeat)
    void repeatOrder() {
        repeatProduct();
    }

    private void getMyOrderDetail() {
        JSONObject myOrderParam = new JSONObject();
        try {
            myOrderParam.put(IWebService.KEY_REQ_ORDER_NUMBER, orderNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataRequest getSuburbsDataRequest = new DataRequest(getActivity());
        getSuburbsDataRequest.execute(IWebService.GET_ORDER_DETAIL, myOrderParam.toString(), new DataRequest.CallBack() {
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
            }

            public void onPostExecute(String response) {
                try {
                    rlvGlobalProgressbar.setVisibility(View.GONE);
                    rootContainer.setVisibility(View.VISIBLE);
                    if (!DataRequest.hasError(getActivity(), response, true)) {
                        JSONObject dataJObject = DataRequest.getJObjWebdata(response);

                        Gson gson = new Gson();

                        Order order = gson.fromJson(
                                dataJObject.getJSONObject(IWebService.KEY_RES_ORDER).toString(),
                                new TypeToken<Order>() {
                                }.getType());
                        if (order != null) {
                            setOrder(order);
                            setOrderStatus(order.status);
                        }

                        Store store = gson.fromJson(
                                dataJObject.getJSONObject(IWebService.KEY_RES_ORDER).toString(),
                                new TypeToken<Store>() {
                                }.getType());
                        if (store != null) {
                            setPreferredStore(store);
                        }

                        Address address = gson.fromJson(
                                dataJObject.getJSONObject(IWebService.KEY_RES_ADDRESS).toString(),
                                new TypeToken<Address>() {
                                }.getType());
                        if (address != null) {
                            setAddress(address);
                        }

                        ArrayList<Product> tmpProductArrayList = gson.fromJson(
                                dataJObject.getJSONArray(IWebService.KEY_RES_PRODUCT_LIST).toString(),
                                new TypeToken<ArrayList<Product>>() {
                                }.getType());

                        if (tmpProductArrayList != null) {
                            for (int i = 0; i < tmpProductArrayList.size(); i++) {

                            }


                            productArrayList.clear();
                            productArrayList.addAll(tmpProductArrayList);
                            productListRepeatAdapter.notifyDataSetChanged();
                            Log.d(TAG, "productArrayList = " + productArrayList.size());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setOrder(Order order) {
        txtOrderNumber.setText("Order : " + order.orderNumber);
        txtOrderPlacedOn.setText("Placed : " + order.orderDeliveryDate);
        txtOrderDeliveryDateTime.setText("Deliver : " + order.orderDeliveryDate
                + " " + order.orderDeliveryTime);
        if (!Utils.isNullOrEmpty(order.orderCompleteDate)) {
            txtOrderCompleteDateTime.setVisibility(View.VISIBLE);
            txtOrderCompleteDateTime.setText("Completed : " + order.orderCompleteDate
                    + " " + order.orderCompleteTime);
        } else {
            txtOrderCompleteDateTime.setVisibility(View.GONE);
        }
        txtOrderTotal.setText("$ " + order.total);
        txtOrderItemCount.setText(order.itemCount);
    }

    private void setAddress(Address address) {
        txtCustomerName.setText(address.name);
        txtOrderAddress.setText(address.street);
        txtOrderSuburb.setText(address.suburbName);
        txtOrderShippingPhone.setText(address.phoneNumber);
    }

    private void setPreferredStore(Store store) {
        if (store.storeId == IWebService.KEY_REQ_PREFERRED_STORE_ID_NONE) {
            lltPreferredStore.setVisibility(View.GONE);
        } else {
            lltPreferredStore.setVisibility(View.VISIBLE);
            txtPreferredStoreName.setText(Html.fromHtml("<b>" + getActivity().getString(R.string.preferred_store) + " </b>" + " " + store.storeName));
            txtPreferredStoreAddress.setText(store.storeName);
        }
    }

    private void setOrderStatus(int orderStatus) {
        txtOrderHoldCancel.setVisibility(View.GONE);
        lltOrderStatus.setVisibility(View.VISIBLE);
        if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_ACCEPTED) {
            lltAccepted.setBackgroundColor(getResources().getColor(R.color.order_status_accepted));
            imgAccepted.setImageResource(R.drawable.accepted);
            txtAccepted.setTextColor(getResources().getColor(R.color.white));
        } else if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_PURCHASING) {

            lltAccepted.setBackgroundColor(getResources().getColor(R.color.order_status_accepted));
            imgAccepted.setImageResource(R.drawable.accepted);
            txtAccepted.setTextColor(getResources().getColor(R.color.white));

            lltPurchasing.setBackgroundColor(getResources().getColor(R.color.order_status_purchasing));
            imgPurchasing.setImageResource(R.drawable.purchasing);
            txtPurchasing.setTextColor(getResources().getColor(R.color.white));
        } else if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_SHIPPING) {
            lltAccepted.setBackgroundColor(getResources().getColor(R.color.order_status_accepted));
            imgAccepted.setImageResource(R.drawable.accepted);
            txtAccepted.setTextColor(getResources().getColor(R.color.white));

            lltPurchasing.setBackgroundColor(getResources().getColor(R.color.order_status_purchasing));
            imgPurchasing.setImageResource(R.drawable.purchasing);
            txtPurchasing.setTextColor(getResources().getColor(R.color.white));

            lltShipping.setBackgroundColor(getResources().getColor(R.color.order_status_shipping));
            imgShipping.setImageResource(R.drawable.shipping);
            txtShipping.setTextColor(getResources().getColor(R.color.white));
        } else if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_COMPLETED) {
            lltAccepted.setBackgroundColor(getResources().getColor(R.color.order_status_accepted));
            imgAccepted.setImageResource(R.drawable.accepted);
            txtAccepted.setTextColor(getResources().getColor(R.color.white));

            lltPurchasing.setBackgroundColor(getResources().getColor(R.color.order_status_purchasing));
            imgPurchasing.setImageResource(R.drawable.purchasing);
            txtPurchasing.setTextColor(getResources().getColor(R.color.white));

            lltShipping.setBackgroundColor(getResources().getColor(R.color.order_status_shipping));
            imgShipping.setImageResource(R.drawable.shipping);
            txtShipping.setTextColor(getResources().getColor(R.color.white));

            lltCompleted.setBackgroundColor(getResources().getColor(R.color.order_status_completed));
            imgCompleted.setImageResource(R.drawable.completed_white);
            txtCompleted.setTextColor(getResources().getColor(R.color.white));
        } else if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_ON_HOLD) {
            lltOrderStatus.setVisibility(View.GONE);
            txtOrderHoldCancel.setVisibility(View.VISIBLE);
            txtOrderHoldCancel.setText(getString(R.string.error_order_onhold));
        } else if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_CANCELLED) {
            lltOrderStatus.setVisibility(View.GONE);
            txtOrderHoldCancel.setVisibility(View.VISIBLE);
            txtOrderHoldCancel.setText(getString(R.string.error_order_cancelled));
        }
    }

    private void repeatProduct() {
        for (int i = 0; i < productArrayList.size(); i++) {
            if (productArrayList.get(i).isRepeat) {
                DBAdapter.insertUpdateDeleteCart(getActivity(), productArrayList.get(i), true);
            }
        }

        NavigationDrawerActivity navigationDrawerActivity = (NavigationDrawerActivity) getActivity();
        if (navigationDrawerActivity != null) {
            navigationDrawerActivity.switchContent(new CartFragment(), false);
        }
    }

}
