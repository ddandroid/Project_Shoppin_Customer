package com.shoppin.customer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.shoppin.customer.R;
import com.shoppin.customer.activity.NavigationDrawerActivity;
import com.shoppin.customer.adapter.ProductListAdapter;
import com.shoppin.customer.model.Product;
import com.shoppin.customer.model.SubCategory;
import com.shoppin.customer.network.DataRequest;
import com.shoppin.customer.network.IWebService;
import com.shoppin.customer.utils.Pagination;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shoppin.customer.fragment.ProductListFragment.SUB_CATEGORY_ARRAYLIST;
import static com.shoppin.customer.fragment.ProductListFragment.SUB_CATEGORY_POSITION;

public class ProductListNestedFragment extends BaseFragment {
    private static final String TAG = ProductListNestedFragment.class.getSimpleName();

    @BindView(R.id.rlvGlobalProgressbar)
    View rlvGlobalProgressbar;

    @BindView(R.id.recyclerListProduct)
    RecyclerView recyclerListProduct;

    @BindView(R.id.swipeRefreshProduct)
    SwipeRefreshLayout swipeRefreshProduct;

    private Pagination pagination;
    private ArrayList<SubCategory> subCategoryArrayList;
    private int subCategoryPosition = -1;
    private int position;

    private ArrayList<Product> productArrayList;
    private ProductListAdapter productListAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_nested_product_list, container, false);
        ButterKnife.bind(this, layoutView);

        if (getArguments() != null) {
            subCategoryArrayList = (ArrayList<SubCategory>) getArguments().getSerializable(SUB_CATEGORY_ARRAYLIST);
            subCategoryPosition = getArguments().getInt(SUB_CATEGORY_POSITION, -1);
        }

        pagination = new Pagination();
        productArrayList = new ArrayList<>();
        productListAdapter = new ProductListAdapter(getActivity(), productArrayList);
        productListAdapter.setOnItemClickListener(new ProductListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NavigationDrawerActivity navigationDrawerActivity = (NavigationDrawerActivity) getActivity();
                if (navigationDrawerActivity != null) {
                    navigationDrawerActivity.switchContent(ProductDetailFragment.newInstance(productArrayList.get(position).productId), false);
                }
            }
        });
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerListProduct.setLayoutManager(linearLayoutManager);
        recyclerListProduct.setAdapter(productListAdapter);

        /**
         * Logic for pagination
         */
        recyclerListProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                Log.d(TAG, "onScrolled");
                if (dy > 0) //check for scroll down
                {
                    if (pagination.shouldLoadMore(linearLayoutManager)) {
                        getProductBySubCategory();
                    }
                }
            }
        });

        /**
         * Start pagination from begining
         */
        swipeRefreshProduct.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pagination.startFromFirst();
                swipeRefreshProduct.setRefreshing(true);

                productArrayList.clear();
                productListAdapter.notifyDataSetChanged();

                getProductBySubCategory();
            }
        });

        getProductBySubCategory();
        return layoutView;
    }

    private void getProductBySubCategory() {
        try {
            position = FragmentPagerItem.getPosition(getArguments());
            JSONObject loginParam = new JSONObject();
            loginParam.put(IWebService.KEY_REQ_SUB_CATEGORY_ID, subCategoryArrayList.get(position).subcategoryId);
            loginParam.put(IWebService.KEY_REQ_PAGE_NUMBER, pagination.pageCount);
            DataRequest getSuburbsDataRequest = new DataRequest(getActivity());
            getSuburbsDataRequest.execute(IWebService.GET_PRODUCT_BY_SUB_CATEGORY, loginParam.toString(), new DataRequest.CallBack() {
                public void onPreExecute() {
                    if (pagination.pageCount > 0) {
                        productArrayList.add(null);
                        productListAdapter.notifyItemInserted(productArrayList.size() - 1);
                    } else {
                        rlvGlobalProgressbar.setVisibility(View.VISIBLE);
                    }

                }

                public void onPostExecute(String response) {
                    try {
                        rlvGlobalProgressbar.setVisibility(View.GONE);
                        if (pagination.pageCount > 0) {
                            productArrayList.remove(productArrayList.size() - 1);
                            productListAdapter.notifyItemRemoved(productArrayList.size());
                        }
                        if (!DataRequest.hasError(getActivity(), response, true)) {
                            JSONObject dataJObject = DataRequest.getJObjWebdata(response);
                            Gson gson = new Gson();
                            ArrayList<Product> tmpProductArrayList = gson.fromJson(
                                    dataJObject.getJSONArray(IWebService.KEY_RES_PRODUCT_LIST).toString(),
                                    new TypeToken<ArrayList<Product>>() {
                                    }.getType());

                            if (tmpProductArrayList != null) {
                                if (tmpProductArrayList.size() >= pagination.PAGINATION_LIMIT) {
                                    pagination.canLoadMore();
                                }
                                productArrayList.addAll(tmpProductArrayList);
                                productListAdapter.notifyDataSetChanged();
                                Log.d(TAG, "productArrayList = " + productArrayList.size());
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
}
