package com.shoppin.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ubuntu on 12/9/16.
 */

public class Order {
    @SerializedName("order_number")
    public String orderNumber;

    @SerializedName("ordered_on")
    public String orderPlacedOn;

    @SerializedName("delivery_date")
    public String orderDeliveryDate;

    @SerializedName("delivery_time")
    public String orderDeliveryTime;

    @SerializedName("item_count")
    public String itemCount;

    public String total;

    public int status = -1;

    @SerializedName("status_label")
    public String statusLabel;

    @SerializedName("completed_date")
    public String orderCompleteDate;

    @SerializedName("completed_time")
    public String orderCompleteTime;

}
