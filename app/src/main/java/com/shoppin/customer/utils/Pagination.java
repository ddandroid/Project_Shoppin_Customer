package com.shoppin.customer.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.shoppin.customer.fragment.ProductListNestedFragment;

/**
 * Created by ubuntu on 12/10/16.
 */

public class Pagination {
    private static final String TAG = ProductListNestedFragment.class.getSimpleName();

    // For pagination
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    public int pageCount = 0;
    private boolean isLoading;
    private boolean canLoadMore = true;
    public int PAGINATION_LIMIT = 0;

    public void startFromFirst() {
        pageCount = 0;
        isLoading = true;
    }

    public boolean shouldLoadMore(LinearLayoutManager layoutManager) {
        visibleItemCount = layoutManager.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

        Log.d(TAG, "isLoading = " + isLoading);
        Log.d(TAG, "canLoadMore = " + canLoadMore);
        if (!isLoading && canLoadMore) {
            Log.d(TAG, "visibleItemCount = " + visibleItemCount);
            Log.d(TAG, "pastVisibleItems = " + pastVisibleItems);
            Log.d(TAG, "(visibleItemCount + pastVisibleItems) = " + (visibleItemCount + pastVisibleItems));
            Log.d(TAG, "totalItemCount = " + totalItemCount);
            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                isLoading = true;
//                getUserFeed();
            }
        }
        return false;
    }

    public void canLoadMore() {
        canLoadMore = true;
        pageCount++;
    }

}
