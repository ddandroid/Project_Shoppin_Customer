package com.shoppin.customer.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppin.customer.R;
import com.shoppin.customer.activity.NavigationDrawerActivity;
import com.shoppin.customer.adapter.ImageSlideAdapter;
import com.shoppin.customer.adapter.SelectionAdapter;
import com.shoppin.customer.database.DBAdapter;
import com.shoppin.customer.model.Product;
import com.shoppin.customer.model.ProductOption;
import com.shoppin.customer.model.ProductOptionValue;
import com.shoppin.customer.network.DataRequest;
import com.shoppin.customer.network.IWebService;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class ProductDetailFragment extends BaseFragment {
    public static final String PRODUCT_ID = "product_id";

    private static final int OPTION_0 = 0;
    private static final int OPTION_1 = 1;
    private static final int OPTION_2 = 2;
    private static final int OPTION_3 = 3;
    private static final int OPTION_4 = 4;


    private static final String TAG = ProductDetailFragment.class.getSimpleName();

    @BindView(R.id.rlvGlobalProgressbar)
    View rlvGlobalProgressbar;
    @BindView(R.id.viewContainer)
    View viewContainer;
    @BindView(R.id.txtProductCartCount)
    TextView txtProductCartCount;

    @BindView(R.id.txtProductName)
    TextView txtProductName;
    @BindView(R.id.txtProductPrice)
    TextView txtProductPrice;
    @BindView(R.id.txtProductSalePrice)
    TextView txtProductSalePrice;
    @BindView(R.id.txtDescription)
    TextView txtDescription;

    @BindView(R.id.txtOption0)
    TextView txtOption0;
    @BindView(R.id.txtOption1)
    TextView txtOption1;
    @BindView(R.id.txtOption2)
    TextView txtOption2;
    @BindView(R.id.txtOption3)
    TextView txtOption3;
    @BindView(R.id.txtOption4)
    TextView txtOption4;

    @BindView(R.id.imageViewPager)
    AutoScrollViewPager productImageViewPager;
    @BindView(R.id.imageViewPagerIndicator)
    LinearLayout productImageViewPagerIndicator;
    private ImageSlideAdapter productImageAdapter;
    private ArrayList<String> productImageArrayList;

    private String productId;
    private Product productDetail;

    public static ProductDetailFragment newInstance(String productId) {
        ProductDetailFragment categoryFragment = new ProductDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PRODUCT_ID, productId);
        categoryFragment.setArguments(bundle);
        return categoryFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_product_detail, container, false);
        ButterKnife.bind(this, layoutView);

        if (getArguments() != null) {
            productId = getArguments().getString(PRODUCT_ID);
            Log.d(TAG, "productId = " + productId);
            getProductDetail(productId);
        }

        initProductImageSlider();
        return layoutView;
    }

    @Override
    public void onPause() {
        super.onPause();
        productImageViewPager.stopAutoScroll();
    }

    @Override
    public void onResume() {
        super.onResume(); // Always call the superclass method first
        productImageViewPager.startAutoScroll();
    }

    /**
     * Get product detail json and set value in UI
     *
     * @param productId
     */
    private void getProductDetail(String productId) {
        try {
            JSONObject loginParam = new JSONObject();
            loginParam.put(IWebService.KEY_REQ_PRODUCT_ID, productId);
            DataRequest getSuburbsDataRequest = new DataRequest(getActivity());
            getSuburbsDataRequest.execute(IWebService.GET_PRODUCT_DETAIL, loginParam.toString(), new DataRequest.CallBack() {
                public void onPreExecute() {
                    rlvGlobalProgressbar.setVisibility(View.VISIBLE);
                }

                public void onPostExecute(String response) {
                    try {
                        rlvGlobalProgressbar.setVisibility(View.GONE);
                        if (!DataRequest.hasError(getActivity(), response, true)) {
                            JSONObject dataJObject = DataRequest.getJObjWebdata(response);
                            Gson gson = new Gson();
                            productDetail = gson.fromJson(dataJObject.toString(),
                                    new TypeToken<Product>() {
                                    }.getType());
                            if (productDetail != null) {
                                viewContainer.setVisibility(View.VISIBLE);
                                updateProductDetail();
                            }
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

    /**
     * Set product detail value
     */
    private void updateProductDetail() {
        txtProductName.setText(productDetail.productName);

//        txtProductPrice.setText("$ " + String.valueOf(productDetail.productPrice));
//        txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//        txtProductSalePrice.setText("$ " + String.valueOf(productDetail.productSalePrice));

        txtDescription.setText(productDetail.productDescription);

        if (productDetail.productImages != null && productDetail.productImages.size() > 0) {
//            Glide.with(getActivity())
//                    .load(productDetail.productImages.get(0))
//                    .placeholder(R.drawable.placeholder)
//                    .error(R.drawable.placeholder)
//                    .into(imgProduct);
            productImageArrayList.clear();
            productImageArrayList.addAll(productDetail.productImages);
            productImageAdapter.setUiPageViewController();
            productImageAdapter.notifyDataSetChanged();
        }

        int optionSize = 0;
        if (productDetail.productOptionArrayList != null && productDetail.productOptionArrayList.size() > 0) {
            optionSize = productDetail.productOptionArrayList.size();
        }

        for (int i = 0; i < optionSize; i++) {
            productDetail.productHasOption = true;
            if (i == OPTION_0) {
                txtOption0.setVisibility(View.VISIBLE);
                productDetail.productOptionArrayList.get(i).productOptionValueArrayList.get(0).selected = true;
                txtOption0.setText(productDetail.productOptionArrayList.get(i).productOptionValueArrayList.get(0).optionValueName);
            } else if (i == OPTION_1) {
                txtOption1.setVisibility(View.VISIBLE);
                productDetail.productOptionArrayList.get(i).productOptionValueArrayList.get(0).selected = true;
                txtOption1.setText(productDetail.productOptionArrayList.get(i).productOptionValueArrayList.get(0).optionValueName);
            } else if (i == OPTION_2) {
                txtOption2.setVisibility(View.VISIBLE);
                productDetail.productOptionArrayList.get(i).productOptionValueArrayList.get(0).selected = true;
                txtOption2.setText(productDetail.productOptionArrayList.get(i).productOptionValueArrayList.get(0).optionValueName);
            } else if (i == OPTION_3) {
                txtOption3.setVisibility(View.VISIBLE);
                productDetail.productOptionArrayList.get(i).productOptionValueArrayList.get(0).selected = true;
                txtOption3.setText(productDetail.productOptionArrayList.get(i).productOptionValueArrayList.get(0).optionValueName);
            } else if (i == OPTION_4) {
                txtOption4.setVisibility(View.VISIBLE);
                productDetail.productOptionArrayList.get(i).productOptionValueArrayList.get(0).selected = true;
                txtOption4.setText(productDetail.productOptionArrayList.get(i).productOptionValueArrayList.get(0).optionValueName);
            }
        }

        updatePriceAsSelectedOption();
    }

    /**
     * Product option drop downs
     */
    private void initProductImageSlider() {
        productImageArrayList = new ArrayList<>();
        productImageAdapter = new ImageSlideAdapter(getActivity(), productImageArrayList, null);
        productImageViewPager.setAdapter(productImageAdapter);
        productImageViewPager.setCurrentItem(0);
        productImageViewPager.setInterval(3000);
        productImageViewPager.setCurrentItem(0);
        productImageViewPager.startAutoScroll(3000);
        productImageViewPager.setCycle(true);
        productImageViewPager.setStopScrollWhenTouch(true);
        productImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int arg) {
                productImageAdapter.setIndicator(arg);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.txtOption0)
    void onClickOption5() {
        showAlertOption(OPTION_0);
    }

    @OnClick(R.id.txtOption1)
    void onClickOption1() {
        showAlertOption(OPTION_1);
    }

    @OnClick(R.id.txtOption2)
    void onClickOption2() {
        showAlertOption(OPTION_2);
    }

    @OnClick(R.id.txtOption3)
    void onClickOption3() {
        showAlertOption(OPTION_3);
    }

    @OnClick(R.id.txtOption4)
    void onClickOption4() {
        showAlertOption(OPTION_4);
    }

    private void showAlertOption(int optionIndex) {
        switch (optionIndex) {
            case OPTION_0:
                showAlertOption(productDetail.productOptionArrayList.get(0), txtOption0);
                break;
            case OPTION_1:
                showAlertOption(productDetail.productOptionArrayList.get(1), txtOption1);
                break;
            case OPTION_2:
                showAlertOption(productDetail.productOptionArrayList.get(2), txtOption2);
                break;
            case OPTION_3:
                showAlertOption(productDetail.productOptionArrayList.get(3), txtOption3);
                break;
            case OPTION_4:
                showAlertOption(productDetail.productOptionArrayList.get(4), txtOption4);
                break;
        }
    }

    private void showAlertOption(final ProductOption productOption, final TextView txtOption) {
        TextView txtSelectionDone;
        ListView listCategorytFilter;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_selection, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        ((TextView) dialogView.findViewById(R.id.txtSelectionType))
                .setText(productOption.optionName);
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

//        ArrayList<ProductOptionValue> productOptionValueArrayList = productOption.productOptionValueArrayList;

        listCategorytFilter = (ListView) dialogView
                .findViewById(R.id.listFilter);
        final SelectionAdapter<ProductOptionValue> filterStateAdapter = new SelectionAdapter<ProductOptionValue>(
                getActivity(), productOption.productOptionValueArrayList);
        filterStateAdapter
                .setBindAdapterInterface(new SelectionAdapter.IBindAdapterValues<ProductOptionValue>() {
                    @Override
                    public void bindValues(SelectionAdapter.Holder holder, final int position) {
                        // TODO Auto-generated method stub

                        holder.txtSelectionValue.setText(productOption.productOptionValueArrayList
                                .get(position).optionValueName);
                        holder.txtSelectionValue.setChecked(productOption.productOptionValueArrayList
                                .get(position).selected);
                        if (productOption.productOptionValueArrayList.get(position).selected) {
                            holder.txtSelectionValue.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_theme_1));
                        } else {
                            holder.txtSelectionValue.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_black));
                        }
                        holder.txtSelectionValue
                                .setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        // TODO Auto-generated method stub
                                        for (int i = 0; i < productOption.productOptionValueArrayList
                                                .size(); i++) {
                                            productOption.productOptionValueArrayList.get(i).selected = false;
                                        }
                                        productOption.productOptionValueArrayList.get(position).selected = true;
                                        txtOption.setText(productOption.productOptionValueArrayList.get(position).optionValueName);
                                        filterStateAdapter.notifyDataSetChanged();

                                        updatePriceAsSelectedOption();

                                        alertDialog.dismiss();
                                    }
                                });
                    }
                });
        listCategorytFilter.setAdapter(filterStateAdapter);
        alertDialog.getWindow().setBackgroundDrawableResource(
                R.color.transparent);
//        alertDialog.setCancelable(false);
        alertDialog.show();
    }

//    private void updatePriceAsSelectedOption() {
//        double salePrice = 0;
//        salePrice += productDetail.productSalePrice;
//        Log.d(TAG,"price productDetail.productSalePrice = " + productDetail.productSalePrice);
//
//        int optionSize = 0;
//        if (productDetail.productHasOption && productDetail.productOptionArrayList != null && productDetail.productOptionArrayList.size() > 0) {
//            optionSize = productDetail.productOptionArrayList.size();
//        }
//
//        for (int iOption = 0; iOption < optionSize; iOption++) {
//            for (int jOptionValue = 0;
//                 jOptionValue < productDetail.productOptionArrayList.get(iOption).productOptionValueArrayList.size();
//                 jOptionValue++) {
//                if (productDetail.productOptionArrayList.get(iOption).productOptionValueArrayList.get(jOptionValue).selected) {
//                    salePrice += productDetail.productOptionArrayList.get(iOption).productOptionValueArrayList.get(jOptionValue).optionValuePrice;
//                    Log.d(TAG,"price optionValueName = " + productDetail.productOptionArrayList.get(iOption).productOptionValueArrayList.get(jOptionValue).optionValueName);
//                    Log.d(TAG,"price optionValuePrice = " + productDetail.productOptionArrayList.get(iOption).productOptionValueArrayList.get(jOptionValue).optionValuePrice);
//                    break;
//                }
//            }
//        }
//
//        txtProductPrice.setText("$ " + String.valueOf(productDetail.productPrice));
//        txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//        // multiply sale price and quantity
//        Log.d(TAG,"price  productDetail.productQuantity = " +  productDetail.productQuantity);
//        salePrice = salePrice * (productDetail.productQuantity == 0 ? 1 : productDetail.productQuantity);
//        txtProductSalePrice.setText("$ " + String.valueOf(salePrice));
//    }

    private void updatePriceAsSelectedOption() {
        txtProductPrice.setText("$ " + String.valueOf(productDetail.productPrice));
        txtProductPrice.setPaintFlags(txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        txtProductSalePrice.setText("$ " + String.valueOf(productDetail.getPriceAsPerSelection()));
    }

    /**
     * Logic for cart in local database
     */

    @OnClick(R.id.imgIncrementProductCart)
    void increaseCart() {
        NavigationDrawerActivity navigationDrawerActivity = (NavigationDrawerActivity) getActivity();
        if (navigationDrawerActivity != null) {
            DBAdapter.insertUpdateDeleteCart(getActivity(), productDetail, true);
            navigationDrawerActivity.updateCartCount();
            txtProductCartCount.setText("" + productDetail.productQuantity);
            updatePriceAsSelectedOption();
        }
    }

    @OnClick(R.id.imgDecrementProductCart)
    void decreaseCart() {
        NavigationDrawerActivity navigationDrawerActivity = (NavigationDrawerActivity) getActivity();
        if (navigationDrawerActivity != null) {
            DBAdapter.insertUpdateDeleteCart(getActivity(), productDetail, false);
            navigationDrawerActivity.updateCartCount();
            txtProductCartCount.setText("" + productDetail.productQuantity);
            updatePriceAsSelectedOption();
        }
    }
}
