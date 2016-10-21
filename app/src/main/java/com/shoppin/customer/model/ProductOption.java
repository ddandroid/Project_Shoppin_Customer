package com.shoppin.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ubuntu on 17/8/16.
 */

public class ProductOption implements Serializable {
    @SerializedName("option_id")
    public String optionId;

    @SerializedName("option_name")
    public String optionName;

    @SerializedName("option_value")
    public ArrayList<ProductOptionValue> productOptionValueArrayList;
}
