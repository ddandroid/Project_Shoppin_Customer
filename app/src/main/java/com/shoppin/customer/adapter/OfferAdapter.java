package com.shoppin.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoppin.customer.R;
import com.shoppin.customer.model.Offer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ubuntu on 24/8/16.
 */

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyViewHolder> {

    private static final String TAG = OfferAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<Offer> offerArrayList;
    private OnItemClickListener itemClickListener;

    public OfferAdapter(Context context, ArrayList<Offer> offerArrayList) {
        this.context = context;
        this.offerArrayList = offerArrayList;
    }

    @Override
    public int getItemCount() {
        return offerArrayList == null ? 0 : offerArrayList.size();
    }

    @Override
    public OfferAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_offer, parent, false);
        return new OfferAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OfferAdapter.MyViewHolder holder, final int position) {
        holder.txtOffers.setText(offerArrayList.get(position).offersDescription);
        holder.cellRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(view, position);
                }
            }
        });
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cellRoot)
        View cellRoot;

        @BindView(R.id.txtOffers)
        TextView txtOffers;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
