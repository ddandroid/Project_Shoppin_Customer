package com.shoppin.customer.utils;

import com.shoppin.customer.model.Product;

import java.util.ArrayList;

/**
 * Created by ubuntu on 6/9/16.
 */

public class Cart {

    public static double getCartSalePriceTotal(ArrayList<Product> productArrayList) {
        double cartSalePriceTotal = 0;
        if (productArrayList != null) {
            for (int i = 0; i < productArrayList.size(); i++) {
                cartSalePriceTotal += productArrayList.get(i).getPriceAsPerSelection();
            }
        }
        return cartSalePriceTotal;
    }
}
