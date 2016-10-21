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
import com.shoppin.customer.activity.NavigationDrawerActivity;
import com.shoppin.customer.adapter.MyOrderAdapter;
import com.shoppin.customer.database.DBAdapter;
import com.shoppin.customer.database.IDatabase;
import com.shoppin.customer.model.Order;
import com.shoppin.customer.network.DataRequest;
import com.shoppin.customer.network.IWebService;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ubuntu on 30/7/16.
 */
public class MyOrderListFragment extends BaseFragment {
    private static final String TAG = MyOrderListFragment.class.getSimpleName();

    @BindView(R.id.rlvGlobalProgressbar)
    View rlvGlobalProgressbar;

    @BindView(R.id.recyclerListOrder)
    RecyclerView recyclerListOrder;

    private ArrayList<Order> orderArrayList;
    private MyOrderAdapter myOrderAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_my_order_list, container, false);
        ButterKnife.bind(this, layoutView);

        orderArrayList = new ArrayList<>();
        myOrderAdapter = new MyOrderAdapter(getActivity(), orderArrayList);
        myOrderAdapter.setOnItemClickListener(new MyOrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NavigationDrawerActivity navigationDrawerActivity = (NavigationDrawerActivity) getActivity();
                if (navigationDrawerActivity != null) {
                    navigationDrawerActivity.switchContent(MyOrderDetailFragment.newInstance(orderArrayList.get(position).orderNumber), false);
                }
            }
        });
        recyclerListOrder.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerListOrder.setAdapter(myOrderAdapter);

        getMyOrder();
        return layoutView;
    }

    private void getMyOrder() {
        try {
            JSONObject myOrderParam = new JSONObject();
            myOrderParam.put(IWebService.KEY_REQ_CUSTOMER_ID,
                    DBAdapter.getMapKeyValueString(getActivity(), IDatabase.IMap.CUSTOMER_ID));
            myOrderParam.put(IWebService.KEY_REQ_PAGE_NUMBER, 0);
            DataRequest getSuburbsDataRequest = new DataRequest(getActivity());
            getSuburbsDataRequest.execute(IWebService.GET_MY_ORDERS, myOrderParam.toString(), new DataRequest.CallBack() {
                public void onPreExecute() {
                    rlvGlobalProgressbar.setVisibility(View.VISIBLE);
                }

                public void onPostExecute(String response) {
                    try {
                        rlvGlobalProgressbar.setVisibility(View.GONE);
                        if (!DataRequest.hasError(getActivity(), response, true)) {
                            JSONObject dataJObject = DataRequest.getJObjWebdata(response);
                            Gson gson = new Gson();
                            ArrayList<Order> tmpOrderArrayList = gson.fromJson(
                                    dataJObject.getJSONArray(IWebService.KEY_RES_ORDER_LIST).toString(),
                                    new TypeToken<ArrayList<Order>>() {
                                    }.getType());

                            if (tmpOrderArrayList != null) {
                                orderArrayList.clear();
                                orderArrayList.addAll(tmpOrderArrayList);
                                myOrderAdapter.notifyDataSetChanged();
                                Log.d(TAG, "orderArrayList = " + orderArrayList.size());
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
