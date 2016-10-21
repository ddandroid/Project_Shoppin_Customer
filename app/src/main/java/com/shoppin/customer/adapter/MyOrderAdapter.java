package com.shoppin.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoppin.customer.R;
import com.shoppin.customer.model.Order;
import com.shoppin.customer.network.IWebService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {

    private static final String TAG = MyOrderAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<Order> orderArrayList;
    private OnItemClickListener itemClickListener;

    public MyOrderAdapter(Context context, ArrayList<Order> orderArrayList) {
        this.context = context;
        this.orderArrayList = orderArrayList;
    }

    @Override
    public int getItemCount() {
        return orderArrayList == null ? 0 : orderArrayList.size();
    }

    @Override
    public MyOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_order, parent, false);
        return new MyOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyOrderAdapter.MyViewHolder holder, final int position) {
        Log.e(TAG, "productId = " + orderArrayList.get(position).orderDeliveryDate);

        holder.txtOrderNumber.setText("Order :" + orderArrayList.get(position).orderNumber);
        holder.txtOrderDeliveryDate.setText(orderArrayList.get(position).orderDeliveryDate);
        holder.txtOrderDeliveryTime.setText(orderArrayList.get(position).orderDeliveryTime);
        holder.txtOrderTotal.setText("$ " + orderArrayList.get(position).total);
        holder.txtOrderItemCount.setText(orderArrayList.get(position).itemCount);
        holder.txtOrderStatus.setText(orderArrayList.get(position).statusLabel);
        setOrderStatus(orderArrayList.get(position).status, holder.txtOrderStatus);

        holder.cellRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(view, position);
                }
            }
        });

    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }

    private void setOrderStatus(int orderStatus, TextView txtOrderStatus) {
        if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_PLACED) {
            txtOrderStatus.setBackgroundResource(R.drawable.order_status_list_placed);
        } else if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_ACCEPTED) {
            txtOrderStatus.setBackgroundResource(R.drawable.order_status_list_accepted);
        } else if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_REJECTED) {
            txtOrderStatus.setBackgroundResource(R.drawable.order_status_list_rejected);
        } else if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_PURCHASING) {
            txtOrderStatus.setBackgroundResource(R.drawable.order_status_list_purchasing);
        } else if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_SHIPPING) {
            txtOrderStatus.setBackgroundResource(R.drawable.order_status_list_shipping);
        } else if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_COMPLETED) {
            txtOrderStatus.setBackgroundResource(R.drawable.order_status_list_completed);
        } else if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_ON_HOLD) {
            txtOrderStatus.setBackgroundResource(R.drawable.order_status_list_onhold);
        } else if (orderStatus == IWebService.KEY_RES_ORDER_STATUS_CANCELLED) {
            txtOrderStatus.setBackgroundResource(R.drawable.order_status_list_cancelled);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cellRoot)
        View cellRoot;

        @BindView(R.id.txtOrderDeliveryDate)
        TextView txtOrderDeliveryDate;

        @BindView(R.id.txtOrderDeliveryTime)
        TextView txtOrderDeliveryTime;

        @BindView(R.id.txtOrderNumber)
        TextView txtOrderNumber;

        @BindView(R.id.txtOrderTotal)
        TextView txtOrderTotal;

        @BindView(R.id.txtOrderItemCount)
        TextView txtOrderItemCount;

        @BindView(R.id.txtOrderStatus)
        TextView txtOrderStatus;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
