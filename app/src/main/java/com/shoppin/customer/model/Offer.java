package com.shoppin.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ubuntu on 16/8/16.
 */

public class Offer implements Serializable {

    public String[] offer_list;

    @SerializedName("offers_desc")
    public String offersDescription;

    @SerializedName("offers_type")
    public String offersType;
}
