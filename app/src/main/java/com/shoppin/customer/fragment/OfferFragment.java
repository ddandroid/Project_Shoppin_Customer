package com.shoppin.customer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppin.customer.R;
import com.shoppin.customer.adapter.OfferAdapter;
import com.shoppin.customer.model.Offer;
import com.shoppin.customer.network.DataRequest;
import com.shoppin.customer.network.IWebService;
import com.shoppin.customer.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ubuntu on 24/8/16.
 */

public class OfferFragment extends BaseFragment {

    private static final String TAG = OfferFragment.class.getSimpleName();

    @BindView(R.id.rlvGlobalProgressbar)
    View rlvGlobalProgressbar;

    @BindView(R.id.recyclerListOffer)
    RecyclerView recyclerListOffer;

    private ArrayList<Offer> offerArrayList;
    private OfferAdapter offerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_offer, container, false);
        ButterKnife.bind(this, layoutView);

        offerArrayList = new ArrayList<>();
        offerAdapter = new OfferAdapter(getActivity(), offerArrayList);
        offerAdapter.setOnItemClickListener(new OfferAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Utils.copyTextToClipBoard(getActivity(), offerArrayList.get(position).offersDescription);
            }
        });
        recyclerListOffer.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerListOffer.setAdapter(offerAdapter);

        getOfferList();

        return layoutView;
    }

    private void getOfferList() {
        DataRequest getOfferListDataRequest = new DataRequest(getActivity());
        getOfferListDataRequest.execute(IWebService.GET_OFFER_LIST, null, new DataRequest.CallBack() {
            public void onPreExecute() {
                rlvGlobalProgressbar.setVisibility(View.VISIBLE);
            }

            public void onPostExecute(String response) {
                try {
                    rlvGlobalProgressbar.setVisibility(View.GONE);
                    if (!DataRequest.hasError(getActivity(), response, true)) {
                        JSONObject dataJObject = DataRequest.getJObjWebdata(response);
                        Gson gson = new Gson();
                        ArrayList<Offer> tmpOfferArrayList = gson.fromJson(
                                dataJObject.getJSONArray(IWebService.KEY_RES_OFFER_LIST).toString(),
                                new TypeToken<ArrayList<Offer>>() {
                                }.getType());

                        if (tmpOfferArrayList != null) {
                            offerArrayList.clear();
                            offerArrayList.addAll(tmpOfferArrayList);
                            offerAdapter.notifyDataSetChanged();
                            Log.d(TAG, "offerArrayList = " + offerArrayList.size());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
