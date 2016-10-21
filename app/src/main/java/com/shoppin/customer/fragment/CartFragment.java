package com.shoppin.customer.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppin.customer.R;
import com.shoppin.customer.activity.CheckOutActivity;
import com.shoppin.customer.activity.NavigationDrawerActivity;
import com.shoppin.customer.activity.SigninActivity;
import com.shoppin.customer.adapter.CartProductListAdapter;
import com.shoppin.customer.database.DBAdapter;
import com.shoppin.customer.database.IDatabase;
import com.shoppin.customer.model.Product;
import com.shoppin.customer.network.DataRequest;
import com.shoppin.customer.network.IWebService;
import com.shoppin.customer.utils.Cart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ubuntu on 30/7/16.
 */
public class CartFragment extends BaseFragment {
    private static final String TAG = CartFragment.class.getSimpleName();

    @BindView(R.id.rlvGlobalProgressbar)
    View rlvGlobalProgressbar;

    @BindView(R.id.recyclerListProduct)
    RecyclerView recyclerListProduct;

    @BindView(R.id.txtCartSalePriceTotal)
    TextView txtCartSalePriceTotal;

    private ArrayList<Product> productArrayList;
    private CartProductListAdapter productListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, layoutView);

        productArrayList = new ArrayList<>();
//        productArrayList.addAll(DBAdapter.getAllProductFromCart(getActivity()));
        productListAdapter = new CartProductListAdapter(getActivity(), productArrayList);
        productListAdapter.setOnItemClickListener(new CartProductListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NavigationDrawerActivity navigationDrawerActivity = (NavigationDrawerActivity) getActivity();
                if (navigationDrawerActivity != null) {
                    navigationDrawerActivity.switchContent(ProductDetailFragment.newInstance(productArrayList.get(position).productId), false);
                }
            }
        });
        productListAdapter.setOnItemLongClickListener(new CartProductListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        productListAdapter.setOnCartChangeListener(new CartProductListAdapter.OnCartChangeListener() {
            @Override
            public void onCartChange(View view, int position, boolean isProductRemove) {
                if (isProductRemove) {
                    if (productArrayList != null) {
                        productArrayList.remove(position);
                        productListAdapter.notifyDataSetChanged();
                    }
                }
                updateCartTotal();
            }
        });
        recyclerListProduct.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerListProduct.setAdapter(productListAdapter);


        getCartProductDetail();

        return layoutView;
    }

    @OnClick(R.id.txtCheckOut)
    void checkOut() {
        if (DBAdapter.getMapKeyValueBoolean(getActivity(), IDatabase.IMap.IS_LOGIN)) {
            Intent intent = new Intent(getActivity(), CheckOutActivity.class);
            intent.putExtra(CheckOutActivity.KEY_BUNDLE_PRODUCT_LIST, productArrayList);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), SigninActivity.class);
            startActivity(intent);
        }
    }

    private void updateCartTotal() {
        txtCartSalePriceTotal.setText("Total : $ " + Cart.getCartSalePriceTotal(productArrayList));
    }


    private void showAlertForDeleteProduct(final String productName, final int position) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getActivity().getString(R.string.alert_message_remove_product, productName));
        alertDialogBuilder.setPositiveButton(R.string.delete,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        productArrayList.get(position).productQuantity = 0;
                        DBAdapter.insertUpdateDeleteCart(getActivity(), productArrayList.get(position), false);
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.cancel, null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void getCartProductDetail() {
        DataRequest getSuburbsDataRequest = new DataRequest(getActivity());

        JSONObject productJObject = new JSONObject();
        JSONArray productIdJArray = new JSONArray();
        ArrayList<Integer> productIdArrayList = new ArrayList<>();
        productIdArrayList.addAll(DBAdapter.getAllProductIdFromCart(getActivity()));
        for (int i = 0; i < productIdArrayList.size(); i++) {
            productIdJArray.put(productIdArrayList.get(i));
        }
        try {
            productJObject.put(IWebService.KEY_REQ_PRODUCT_LIST, productIdJArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getSuburbsDataRequest.execute(IWebService.GET_CART_PRODUCT_DETAIL, productJObject.toString(), new DataRequest.CallBack() {
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
            }

            public void onPostExecute(String response) {
                try {
                    rlvGlobalProgressbar.setVisibility(View.GONE);
                    if (!DataRequest.hasError(getActivity(), response, true)) {
                        Gson gson = new Gson();
                        JSONObject dataJObject = DataRequest.getJObjWebdata(response);

                        ArrayList<Product> tmpProductArrayList = gson.fromJson(
                                dataJObject.getJSONArray(IWebService.KEY_RES_PRODUCT_LIST).toString(),
                                new TypeToken<ArrayList<Product>>() {
                                }.getType());
                        if (tmpProductArrayList != null) {
                            Log.e(TAG, "tmpProductArrayList.size = " + tmpProductArrayList.size());
//                            productArrayList.addAll(tmpProductArrayList);
//                            DBAdapter.setProductParams(getActivity(), productArrayList);
                            productArrayList.addAll(DBAdapter.setProductParams(getActivity(), tmpProductArrayList));
                            Log.e(TAG, "productArrayList.size = " + productArrayList.size());
                            productListAdapter.notifyDataSetChanged();
                        }

                        updateCartTotal();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
