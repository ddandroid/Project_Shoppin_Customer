package com.shoppin.customer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppin.customer.R;
import com.shoppin.customer.activity.SignupActivity;
import com.shoppin.customer.adapter.CategoryHomeAdapter;
import com.shoppin.customer.adapter.ImageSlideAdapter;
import com.shoppin.customer.model.Category;
import com.shoppin.customer.network.DataRequest;
import com.shoppin.customer.network.IWebService;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

import static cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE;

/**
 * Created by ubuntu on 15/8/16.
 */

public class HomeFragment extends BaseFragment {

    private static final String TAG = SignupActivity.class.getSimpleName();


    @BindView(R.id.rlvGlobalProgressbar)
    View rlvGlobalProgressbar;

    @BindView(R.id.recyclerListCategory)
    RecyclerView recyclerListCategory;

    @BindView(R.id.imageViewPager)
    AutoScrollViewPager offerViewPager;

    @BindView(R.id.imageViewPagerIndicator)
    LinearLayout offerViewPagerIndicator;

    private ArrayList<Category> categoryArrayList;
    private CategoryHomeAdapter categoryHomeAdapter;
    private ArrayList<String> offerArrayList;
    private ImageSlideAdapter offerHomeAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        layoutView = inflater.inflate(R.layout.fragment_home, null);
        layoutView = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, layoutView);

        initOffers();

        categoryArrayList = new ArrayList<>();
        categoryHomeAdapter = new CategoryHomeAdapter(getActivity(), categoryArrayList);
        recyclerListCategory.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerListCategory.setAdapter(categoryHomeAdapter);

        getCategory();

        return layoutView;
    }

    @Override
    public void onPause() {
        super.onPause();
        offerViewPager.stopAutoScroll();
    }

    @Override
    public void onResume() {
        super.onResume(); // Always call the superclass method first
        offerViewPager.startAutoScroll();
    }

    private void getCategory() {
        try {
            JSONObject loginParam = new JSONObject();
            loginParam.put(IWebService.KEY_REQ_IS_HOME, true);
            DataRequest getSuburbsDataRequest = new DataRequest(getActivity());
            getSuburbsDataRequest.execute(IWebService.GET_CATEGORY, loginParam.toString(), new DataRequest.CallBack() {
                public void onPreExecute() {
                    rlvGlobalProgressbar.setVisibility(View.VISIBLE);
                }

                public void onPostExecute(String response) {
                    try {
                        rlvGlobalProgressbar.setVisibility(View.GONE);
                        if (!DataRequest.hasError(getActivity(), response, true)) {
                            JSONObject dataJObject = DataRequest.getJObjWebdata(response);
                            Gson gson = new Gson();
                            ArrayList<Category> tmpCategoryArrayList = gson.fromJson(
                                    dataJObject.getJSONArray(IWebService.KEY_RES_CATEGORY_LIST).toString(),
                                    new TypeToken<ArrayList<Category>>() {
                                    }.getType());

                            if (tmpCategoryArrayList != null) {
                                categoryArrayList.addAll(tmpCategoryArrayList);
                                categoryHomeAdapter.notifyDataSetChanged();
                                Log.d(TAG, "tmpCategoryArrayList = " + tmpCategoryArrayList.size());

                                if (categoryArrayList != null && categoryArrayList.size() >= 2) {
                                    categoryArrayList.get(0).setCategoryExpand(true);
                                    categoryArrayList.get(1).setCategoryExpand(true);
                                } else if (categoryArrayList != null && categoryArrayList.size() >= 1) {
                                    categoryArrayList.get(0).setCategoryExpand(true);
                                }
                            }

                            ArrayList<String> tmpOfferArrayList = gson.fromJson
                                    (dataJObject.getJSONArray(IWebService.KEY_RES_OFFER_LIST).toString(),
                                            new TypeToken<ArrayList<String>>() {
                                            }.getType());
//                            Log.d(TAG, "tmpOfferArrayList = " + tmpOfferArrayList.size());
                            if (tmpOfferArrayList != null) {
                                offerArrayList.clear();
                                offerArrayList.addAll(tmpOfferArrayList);
                                offerHomeAdapter.setUiPageViewController();
                                offerHomeAdapter.notifyDataSetChanged();
                                Log.d(TAG, "tmpOfferArrayList = " + tmpOfferArrayList.size());
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

    private void initOffers() {
        offerArrayList = new ArrayList<>();
        offerHomeAdapter = new ImageSlideAdapter(getActivity(), offerArrayList, offerViewPagerIndicator);
        offerViewPager.setAdapter(offerHomeAdapter);
        offerViewPager.setInterval(5000);
        offerViewPager.setCurrentItem(0);
        offerViewPager.startAutoScroll(5000);
        offerViewPager.setCycle(true);
        offerViewPager.setStopScrollWhenTouch(true);
        offerViewPager.setSlideBorderMode(SLIDE_BORDER_MODE_CYCLE);
        offerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int arg) {
                offerHomeAdapter.setIndicator(arg);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
