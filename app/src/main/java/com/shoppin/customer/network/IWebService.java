package com.shoppin.customer.network;

/**
 * Created by ubuntu on 10/8/16.
 */

public interface IWebService {

    //    String MAIN_URL = "http://192.168.0.1/ci/shoppin/admin/v1/";
//    String MAIN_URL = "http://192.168.0.1/shoppin/service/index.php/";
    String MAIN_URL = "http://dddemo.net/php/shoppin/service/index.php/";

    String DEVICE_TYPE = "android";

    String KEY_RES_DATA = "data";
    String KEY_RES_SUCCESS = "success";
    String KEY_RES_MESSAGE = "message";


    String APP_VERSION_VERIFY = MAIN_URL + "app/VersionCustomer";

    String FCM_TOKEN_REGISTRATION = MAIN_URL + "customer/DeviceRegistration";

    String GET_SUBURB = MAIN_URL + "customer/GetSuburb";

    String CUSTOMER_LOGIN = MAIN_URL + "customer/CustomerLogin";
    String CUSTOMER_REGISTRATION = MAIN_URL + "customer/CustomerRegistration";
    String CUSTOMER_ACCOUNT_UPDATE = MAIN_URL + "customer/CustomerProfileUpdate";
    String CUSTOMER_ADDRESS_LIST = MAIN_URL + "customer/MyAddressList";
    String CUSTOMER_PROFILE = MAIN_URL + "customer/MyProfile";
    String CUSTOMER_ADD_ADDRESS = MAIN_URL + "customer/AddMyAddress";
    String CUSTOMER_UPDATE_ADDRESS = MAIN_URL + "customer/UpdateMyAddress";
    String CUSTOMER_DELETE_ADDRESS = MAIN_URL + "customer/DeleteMyAddress";

    String GET_CATEGORY = MAIN_URL + "category/GetCategoryList";

    String GET_PRODUCT_BY_SUB_CATEGORY = MAIN_URL + "category/GetProductsBySubCatID";

    String GET_PRODUCT_DETAIL = MAIN_URL + "product/GetProductDetail";

    String GET_CHECKOUT_DETAIL = MAIN_URL + "checkout/GetCheckOutDetails";

    String PLACE_ORDER = MAIN_URL + "order/PlaceOrder";

    String REDEEM_COUPON = MAIN_URL + "coupon/RedeemCoupon";

    String GET_MY_ORDERS = MAIN_URL + "order/MyOrders";

    String GET_ORDER_DETAIL = MAIN_URL + "order/OrderDetail";

    String CUSTOMER_SIGN_OUT = MAIN_URL + "customer/Logout";

    String NEAR_BY_STORES = MAIN_URL + "store/NearByStores";

    String GET_CART_PRODUCT_DETAIL = MAIN_URL + "order/GetCartProductDetail";

    String GET_OFFER_LIST = MAIN_URL + "offer/GetOfferList";

    /**
     * Request Params
     */
    String KEY_REQ_DEVICE_TYPE = "device_type";
    String KEY_REQ_CURRENT_VERSION = "current_version";
    String KEY_REQ_CUSTOMER_MOBILE = "customer_mobile";
    String KEY_REQ_CUSTOMER_PASSWORD = "customer_password";
    String KEY_REQ_CUSTOMER_DEVICE_TYPE = "customer_device_type";
    String KEY_REQ_CUSTOMER_DEVICE_TOKEN = "customer_device_token";
    String KEY_REQ_CUSTOMER_DEVICE_ID = "customer_device_id";
    String KEY_REQ_CUSTOMER_NAME = "customer_name";
    String KEY_REQ_CUSTOMER_STREET = "customer_address_line1";
    String KEY_REQ_CUSTOMER_SUBURB_ID = "customer_suburb_id";
    String KEY_REQ_CUSTOMER_POSTCODE = "customer_zip";
    String KEY_REQ_IS_HOME = "is_home";
    String KEY_REQ_SUB_CATEGORY_ID = "sub_cat_id";
    String KEY_REQ_ADDRESS_ID = "customer_address_id";
    String KEY_REQ_CUSTOMER_SUBURB_NAME = "customer_suburb_name";
    String KEY_REQ_CUSTOMER_ID = "customer_id";
    String KEY_REQ_ORDER = "order";
    String KEY_REQ_ORDER_SUBURB_ID = "order_suburb_id";
    String KEY_REQ_ORDER_SLOT_ID = "order_slot_id";
    String KEY_REQ_PREFERRED_STORE_ID = "preferred_store_id";
    String KEY_REQ_CUSTOMER_BILLING_ID = "customer_billing_address_id";
    String KEY_REQ_CUSTOMER_SHIPPING_ID = "customer_shipping_address_id";
    String KEY_REQ_SUB_TOTAL = "subtotal";
    String KEY_REQ_TOTAL = "total";
    String KEY_REQ_PAYMENT = "payment";
    String KEY_REQ_PAYMENT_TRANSACTION_ID = "transaction_id";
    String KEY_REQ_COUPON = "coupon";
    String KEY_REQ_COUPON_DISCOUNT_AMOUNT = "discount_amount";
    String KEY_REQ_COUPON_ID = "coupon_id";
    String KEY_REQ_PRODUCT_LIST = "product_list";
    String KEY_REQ_PRODUCT_ID = "product_id";
    String KEY_REQ_PRODUCT_NAME = "product_name";
    String KEY_REQ_PRODUCT_QUANTITY = "product_quantity";
    String KEY_REQ_PRODUCT_PRICE = "product_price";
    String KEY_REQ_PRODUCT_SALEPRICE = "product_saleprice";
    String KEY_REQ_OPTION_LIST = "option_list";
    String KEY_REQ_OPTION_ID = "option_id";
    String KEY_REQ_OPTION_NAME = "option_name";
    String KEY_REQ_OPTION_VALUE_ID = "option_value_id";
    String KEY_REQ_OPTION_VALUE_NAME = "option_value_name";
    String KEY_REQ_COUPON_CODE = "coupon_code";
    String KEY_REQ_ORDER_AMOUNT = "order_amount";
    String KEY_REQ_ORDER_NUMBER = "order_number";
    String KEY_REQ_SUBURB_ID = "customer_suburb_id";
    String KEY_REQ_PAGE_NUMBER = "page_no";

    /**
     * Response Params
     */
    String KEY_RES_CUSTOMER_ID = "customer_id";
    String KEY_RES_SUBURB_LIST = "suburb_list";
    String KEY_RES_SUBURB_ID = "customer_suburb_id";
    String KEY_RES_SUBURB_NAME = "customer_suburb_name";
    String KEY_RES_CATEGORY_LIST = "category_list";
    String KEY_RES_OFFER_LIST = "offer_list";
    String KEY_RES_ADDRESS_LIST = "address_list";
    String KEY_RES_DATE_LIST = "date_list";
    String KEY_RES_STORE_LIST = "store_list";
    String KEY_RES_PRODUCT_LIST = "product_list";
    String KEY_RES_ORDER_LIST = "order_list";
    String KEY_RES_DISCOUNT_AMOUNT = "discount_amount";
    String KEY_RES_COUPON_ID = "coupon_id";
    String KEY_RES_UPDATE_REQUIRE = "update_require";
    String KEY_RES_ADDRESS = "address";
    String KEY_RES_ORDER = "order";

    /**
     * Order status response
     */
    int KEY_RES_ORDER_STATUS_PLACED = 1;
    int KEY_RES_ORDER_STATUS_REJECTED = 2;
    int KEY_RES_ORDER_STATUS_ACCEPTED = 3;
    int KEY_RES_ORDER_STATUS_PURCHASING = 4;
    int KEY_RES_ORDER_STATUS_SHIPPING = 5;
    int KEY_RES_ORDER_STATUS_COMPLETED = 6;
    int KEY_RES_ORDER_STATUS_ON_HOLD = 7;
    int KEY_RES_ORDER_STATUS_CANCELLED = 8;

    /**
     * Product status
     */
    int KEY_RES_PRODUCT_STATUS_AVAILABLE = 1;
    int KEY_RES_PRODUCT_STATUS_NOT_AVAILABLE = -1;
    int KEY_RES_PRODUCT_STATUS_NORMAL = 0;

    int KEY_REQ_PREFERRED_STORE_ID_NONE = 0;

}
