package com.shoppin.customer.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shoppin.customer.R;
import com.shoppin.customer.activity.NavigationDrawerActivity;
import com.shoppin.customer.database.DBAdapter;
import com.shoppin.customer.model.Product;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductListAdapter extends RecyclerView.Adapter {

    private static final String TAG = ProductListAdapter.class.getSimpleName();

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROGRESS = 0;

    private Context context;
    private ArrayList<Product> productArrayList;
    private OnItemClickListener itemClickListener;

    public ProductListAdapter(Context context, ArrayList<Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
    }

    @Override
    public int getItemCount() {
        return productArrayList == null ? 0 : productArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return productArrayList.get(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View itemView;
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.cell_product, parent, false);

            viewHolder = new MyViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.cell_progressbar_bottom, parent, false);

            viewHolder = new ProgressViewHolder(itemView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            Log.e(TAG, "productId = " + productArrayList.get(position).productId);

            ((MyViewHolder) holder).txtProductName.setText(productArrayList.get(position).productName);

            ((MyViewHolder) holder).txtProductPrice.setText("$ " + String.valueOf(productArrayList.get(position).productPrice));
            ((MyViewHolder) holder).txtProductPrice.setPaintFlags(((MyViewHolder) holder).txtProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            ((MyViewHolder) holder).txtProductSalePrice.setText("$ " + String.valueOf(productArrayList.get(position).productSalePrice));

            if (productArrayList.get(position).productImages != null && productArrayList.get(position).productImages.size() > 0) {
                Glide.with(context)
                        .load(productArrayList.get(position).productImages.get(0))
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(((MyViewHolder) holder).imgProduct);
            }
            if (productArrayList.get(position).productHasOption) {
                ((MyViewHolder) holder).imgAddToCart.setVisibility(View.INVISIBLE);
            } else {
                ((MyViewHolder) holder).imgAddToCart.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).imgAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                    Utils.showToastShort(context, "Under Development : " + productArrayList.get(position).productName);
                        NavigationDrawerActivity navigationDrawerActivity = (NavigationDrawerActivity) context;
                        if (navigationDrawerActivity != null) {
                            DBAdapter.insertUpdateDeleteCart(context, productArrayList.get(position), true);
                            navigationDrawerActivity.updateCartCount();
                        }
                    }
                });
            }

            ((MyViewHolder) holder).cellRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(view, position);
                    }
                }
            });
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cellRoot)
        View cellRoot;

        @BindView(R.id.txtProductName)
        TextView txtProductName;

        @BindView(R.id.txtProductPrice)
        TextView txtProductPrice;

        @BindView(R.id.txtProductSalePrice)
        TextView txtProductSalePrice;

        @BindView(R.id.imgProduct)
        ImageView imgProduct;

        @BindView(R.id.imgAddToCart)
        ImageView imgAddToCart;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, itemView);
        }
    }


}
