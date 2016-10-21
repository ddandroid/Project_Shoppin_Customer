package com.shoppin.customer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.shoppin.customer.model.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static com.shoppin.customer.database.IDatabase.ICart;
import static com.shoppin.customer.database.IDatabase.IMap;

/**
 * Created by ubuntu on 27/4/16.
 */
public class DBAdapter {
    private static final String TAG = DBAdapter.class.getSimpleName();

    public static void insertUpdateMap(Context context, String key, String value) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(IMap.KEY_MAP_VALUE, value);

        Cursor cursor = db.query(IMap.TABLE_MAP, new String[]{IMap.KEY_ID}, IMap.KEY_MAP_KEY + " = '" + key + "'", null, null, null, null, null);
        int index = -1;
        if (cursor != null && cursor.getCount() > 0) { //if the row exist then return the id
            cursor.moveToFirst();
            index = cursor.getInt(cursor.getColumnIndex(IMap.KEY_ID));
            cursor.close();
        }

        if (index == -1) {
            contentValues.put(IMap.KEY_MAP_KEY, key);
            db.insert(IMap.TABLE_MAP, null, contentValues);
        } else {
            db.update(IMap.TABLE_MAP, contentValues, IMap.KEY_ID + " = '" + index + "'", null);
        }
        Log.d(TAG, "insertUpdateMap key = " + key + ", value = " + value);
    }

    public static String getMapKeyValueString(Context context, String key) {
        String value = null;
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + IMap.TABLE_MAP + " where " + IMap.KEY_MAP_KEY + " = '" + key + "'", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            value = cursor.getString(cursor.getColumnIndexOrThrow(IMap.KEY_MAP_VALUE));
            cursor.close(); // that's important too, otherwise you're gonna leak cursors
        }
        Log.d(TAG, "getMapKeyValueString key = " + key + ", value = " + value);
        return value;
    }

    public static boolean getMapKeyValueBoolean(Context context, String key) {
        boolean result = false;
        if (IMap.TRUE.equalsIgnoreCase(getMapKeyValueString(context, key))) {
            result = true;
        }
        Log.d(TAG, "getMapKeyValueBoolean value [" + key + "] = " + result);
        return result;
    }

    public static void setMapKeyValueBoolean(Context context, String key,
                                             boolean value) {
        Log.d(TAG, "setMapKeyValueBoolean value[" + key + "] = " + value);
        if (value) {
            insertUpdateMap(context, key, IMap.TRUE);
        } else {
            insertUpdateMap(context, key, IMap.FALSE);
        }
    }

//    public static void insertUpdateDeleteCart(Context context, Product product, boolean increase) {
//        Log.e(TAG, "==== insertUpdateDeleteCart product.productId = " + product.productId + " ====");
//        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//        Gson gson = new Gson();
//
//        Log.d(TAG, "product.productName = " + product.productName + ", product.productHasOption= " + product.productHasOption);
//
//        String query = "select * from " + ICart.TABLE_CART + " ";
//        query += "WHERE " + ICart.KEY_PRODUCT_ID + " = '" + product.productId + "' ";
//        if (product.productHasOption && product.productOptionArrayList != null) {
//            for (int optionProduct = 0; optionProduct < product.productOptionArrayList.size(); optionProduct++) {
//                for (int valueProduct = 0; valueProduct < product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.size(); valueProduct++) {
//                    if (product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).isSelected) {
//                        switch (optionProduct) {
//                            case ICart.OPTION_0:
//                                query += "AND " + ICart.KEY_OPTION_ID_0 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_0 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                            case ICart.OPTION_1:
//                                query += "AND " + ICart.KEY_OPTION_ID_1 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_1 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                            case ICart.OPTION_2:
//                                query += "AND " + ICart.KEY_OPTION_ID_2 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_2 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                            case ICart.OPTION_3:
//                                query += "AND " + ICart.KEY_OPTION_ID_3 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_3 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                            case ICart.OPTION_4:
//                                query += "AND " + ICart.KEY_OPTION_ID_4 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_4 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                        }
//                    }
//                }
//            }
//        }
//        Log.d(TAG, "query = " + query);
//        Cursor cursor = db.rawQuery(query, null);
//        int index = -1;
//        if (cursor != null && cursor.getCount() > 0) { //if the row exist then return the id
//            cursor.moveToFirst();
//            index = cursor.getInt(cursor.getColumnIndex(IMap.KEY_ID));
////            cursor.close();
//        }
//
//        if (index == -1) {
//            // Only if product need to add
//            // else product is for delete from cart
//            if (increase) {
//                // new product
//                product.productQuantity = product.productQuantity == 0 ? 1 : product.productQuantity;
//                contentValues.put(ICart.KEY_PRODUCT_ID, product.productId);
//                contentValues.put(ICart.KEY_PRODUCT_JSON, gson.toJson(product));
//
//                if (product.productHasOption && product.productOptionArrayList != null) {
//                    for (int optionProduct = 0; optionProduct < product.productOptionArrayList.size(); optionProduct++) {
//                        for (int valueProduct = 0; valueProduct < product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.size(); valueProduct++) {
//                            if (product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).isSelected) {
//                                switch (optionProduct) {
//                                    case ICart.OPTION_0:
//                                        contentValues.put(ICart.KEY_OPTION_ID_0, product.productOptionArrayList.get(optionProduct).optionId);
//                                        contentValues.put(ICart.KEY_OPTION_VALUE_ID_0, product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId);
//                                        break;
//                                    case ICart.OPTION_1:
//                                        contentValues.put(ICart.KEY_OPTION_ID_1, product.productOptionArrayList.get(optionProduct).optionId);
//                                        contentValues.put(ICart.KEY_OPTION_VALUE_ID_1, product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId);
//                                        break;
//                                    case ICart.OPTION_2:
//                                        contentValues.put(ICart.KEY_OPTION_ID_2, product.productOptionArrayList.get(optionProduct).optionId);
//                                        contentValues.put(ICart.KEY_OPTION_VALUE_ID_2, product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId);
//                                        break;
//                                    case ICart.OPTION_3:
//                                        contentValues.put(ICart.KEY_OPTION_ID_3, product.productOptionArrayList.get(optionProduct).optionId);
//                                        contentValues.put(ICart.KEY_OPTION_VALUE_ID_3, product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId);
//                                        break;
//                                    case ICart.OPTION_4:
//                                        contentValues.put(ICart.KEY_OPTION_ID_4, product.productOptionArrayList.get(optionProduct).optionId);
//                                        contentValues.put(ICart.KEY_OPTION_VALUE_ID_4, product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId);
//                                        break;
//                                }
//                            }
//                        }
//                    }
//                }
//                db.insert(ICart.TABLE_CART, null, contentValues);
//            }
//        } else {
//            if (cursor != null && cursor.getCount() > 0) {
////                cursor.moveToFirst();
//                try {
//                    Product cartProduct = gson.fromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(ICart.KEY_PRODUCT_JSON))).toString(), Product.class);
//                    // Increase cart
//                    if (increase) {
//                        cartProduct.productQuantity++;
//                        product.productQuantity = cartProduct.productQuantity;
//                        contentValues.put(ICart.KEY_PRODUCT_JSON, gson.toJson(product));
//                        db.update(ICart.TABLE_CART, contentValues, ICart.KEY_ID + " = '" + index + "'", null);
//                    }
//                    // Decrease cart
//                    else {
//                        cartProduct.productQuantity--;
//                        // Remove product from cart
//                        product.productQuantity = cartProduct.productQuantity;
//                        if (product.productQuantity <= 0) {
//                            db.delete(ICart.TABLE_CART, ICart.KEY_ID + " = '" + index + "'", null);
//                        }
//                        // Decrease cart count
//                        else {
//                            contentValues.put(ICart.KEY_PRODUCT_JSON, gson.toJson(product));
//                            db.update(ICart.TABLE_CART, contentValues, ICart.KEY_ID + " = '" + index + "'", null);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                cursor.close(); // that's important too, otherwise you're gonna leak cursors
//            }
//        }
//        Log.e(TAG, "==== insertUpdateDeleteCart product.productId = " + product.productId + " ====");
//    }


    public static void insertUpdateDeleteCart(Context context, Product product, boolean increase) {
        Log.e(TAG, "==== insertUpdateDeleteCart product.productId = " + product.productId + " ====");
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Log.d(TAG, "product.productName = " + product.productName + ", product.productHasOption= " + product.productHasOption);

        String query = "select * from " + ICart.TABLE_CART + " ";
        query += "WHERE " + ICart.KEY_PRODUCT_ID + " = '" + product.productId + "' ";
        if (product.productHasOption && product.productOptionArrayList != null) {
            // For each option
            for (int optionProduct = 0; optionProduct < product.productOptionArrayList.size(); optionProduct++) {
                // For each value option
                for (int valueProduct = 0; valueProduct < product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.size(); valueProduct++) {
                    if (product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).isSelected) {
                        switch (optionProduct) {
                            case ICart.OPTION_0:
                                query += "AND " + ICart.KEY_OPTION_ID_0 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_0 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
                                break;
                            case ICart.OPTION_1:
                                query += "AND " + ICart.KEY_OPTION_ID_1 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_1 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
                                break;
                            case ICart.OPTION_2:
                                query += "AND " + ICart.KEY_OPTION_ID_2 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_2 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
                                break;
                            case ICart.OPTION_3:
                                query += "AND " + ICart.KEY_OPTION_ID_3 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_3 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
                                break;
                            case ICart.OPTION_4:
                                query += "AND " + ICart.KEY_OPTION_ID_4 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_4 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
                                break;
                        }
                    }
                }
            }
        }
        Log.d(TAG, "query = " + query);
        Cursor cursor = db.rawQuery(query, null);
        int index = -1;
        if (cursor != null && cursor.getCount() > 0) { //if the row exist then return the id
            cursor.moveToFirst();
            index = cursor.getInt(cursor.getColumnIndex(IMap.KEY_ID));
//            cursor.close();
        }

        if (index == -1) {
            // Only if product need to add
            // else product is for delete from cart
            if (increase) {
                // new product
                product.productQuantity = product.productQuantity == 0 ? 1 : product.productQuantity;
                contentValues.put(ICart.KEY_PRODUCT_ID, product.productId);
                contentValues.put(ICart.KEY_PRODUCT_QUANTITY, product.productQuantity);

                if (product.productHasOption && product.productOptionArrayList != null) {
                    for (int optionProduct = 0; optionProduct < product.productOptionArrayList.size(); optionProduct++) {
                        for (int valueProduct = 0; valueProduct < product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.size(); valueProduct++) {
                            if (product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).isSelected) {
                                switch (optionProduct) {
                                    case ICart.OPTION_0:
                                        contentValues.put(ICart.KEY_OPTION_ID_0, product.productOptionArrayList.get(optionProduct).optionId);
                                        contentValues.put(ICart.KEY_OPTION_VALUE_ID_0, product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId);
                                        break;
                                    case ICart.OPTION_1:
                                        contentValues.put(ICart.KEY_OPTION_ID_1, product.productOptionArrayList.get(optionProduct).optionId);
                                        contentValues.put(ICart.KEY_OPTION_VALUE_ID_1, product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId);
                                        break;
                                    case ICart.OPTION_2:
                                        contentValues.put(ICart.KEY_OPTION_ID_2, product.productOptionArrayList.get(optionProduct).optionId);
                                        contentValues.put(ICart.KEY_OPTION_VALUE_ID_2, product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId);
                                        break;
                                    case ICart.OPTION_3:
                                        contentValues.put(ICart.KEY_OPTION_ID_3, product.productOptionArrayList.get(optionProduct).optionId);
                                        contentValues.put(ICart.KEY_OPTION_VALUE_ID_3, product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId);
                                        break;
                                    case ICart.OPTION_4:
                                        contentValues.put(ICart.KEY_OPTION_ID_4, product.productOptionArrayList.get(optionProduct).optionId);
                                        contentValues.put(ICart.KEY_OPTION_VALUE_ID_4, product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId);
                                        break;
                                }
                            }
                        }
                    }
                }
                db.insert(ICart.TABLE_CART, null, contentValues);
            }
        } else {
            if (cursor != null && cursor.getCount() > 0) {
//                cursor.moveToFirst();
                int productQuantity = cursor.getInt(cursor.getColumnIndex(ICart.KEY_PRODUCT_QUANTITY));
                // Increase cart
                if (increase) {
                    productQuantity++;
                    product.productQuantity = productQuantity;
                    contentValues.put(ICart.KEY_PRODUCT_QUANTITY, product.productQuantity);
                    db.update(ICart.TABLE_CART, contentValues, ICart.KEY_ID + " = '" + index + "'", null);
                }
                // Decrease cart
                else {
                    productQuantity--;
                    // Remove product from cart
                    product.productQuantity = productQuantity;
                    if (product.productQuantity <= 0) {
                        db.delete(ICart.TABLE_CART, ICart.KEY_ID + " = '" + index + "'", null);
                    }
                    // Decrease cart count
                    else {
                        contentValues.put(ICart.KEY_PRODUCT_QUANTITY, product.productQuantity);
                        db.update(ICart.TABLE_CART, contentValues, ICart.KEY_ID + " = '" + index + "'", null);
                    }
                }
                cursor.close(); // that's important too, otherwise you're gonna leak cursors
            }
        }
        Log.e(TAG, "==== insertUpdateDeleteCart product.productId = " + product.productId + " ====");
    }

//        public static Product getProductFromCart(Context context, Product product) {
//        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
//        String query = "select * from " + ICart.TABLE_CART + " ";
//        query += "WHERE " + ICart.KEY_PRODUCT_ID + " = '" + product.productId + "' ";
//        if (product.productHasOption && product.productOptionArrayList != null) {
//            for (int optionProduct = 0; optionProduct < product.productOptionArrayList.size(); optionProduct++) {
//                for (int valueProduct = 0; valueProduct < product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.size(); valueProduct++) {
//                    if (product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).isSelected) {
//                        switch (optionProduct) {
//                            case ICart.OPTION_0:
//                                query += "AND " + ICart.KEY_OPTION_ID_0 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_0 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                            case ICart.OPTION_1:
//                                query += "AND " + ICart.KEY_OPTION_ID_1 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_1 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                            case ICart.OPTION_2:
//                                query += "AND " + ICart.KEY_OPTION_ID_2 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_2 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                            case ICart.OPTION_3:
//                                query += "AND " + ICart.KEY_OPTION_ID_3 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_3 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                            case ICart.OPTION_4:
//                                query += "AND " + ICart.KEY_OPTION_ID_4 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_4 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                        }
//                    }
//                }
//            }
//        }
//        Log.d(TAG, "query = " + query);
//        Cursor cursor = db.rawQuery(query, null);
//        Product cartProduct = null;
//        Gson gson = new Gson();
//        try {
//            if (cursor != null && cursor.getCount() > 0) {
//                cartProduct = gson.fromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(ICart.KEY_PRODUCT_JSON))).toString(), Product.class);
//                cursor.close(); // that's important too, otherwise you're gonna leak cursors
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return cartProduct;
//    }


//    public static Product getProductFromCart(Context context, Product product) {
//        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
//        String query = "select * from " + ICart.TABLE_CART + " ";
//        query += "WHERE " + ICart.KEY_PRODUCT_ID + " = '" + product.productId + "' ";
//        if (product.productHasOption && product.productOptionArrayList != null) {
//            for (int optionProduct = 0; optionProduct < product.productOptionArrayList.size(); optionProduct++) {
//                for (int valueProduct = 0; valueProduct < product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.size(); valueProduct++) {
//                    if (product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).isSelected) {
//                        switch (optionProduct) {
//                            case ICart.OPTION_0:
//                                query += "AND " + ICart.KEY_OPTION_ID_0 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_0 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                            case ICart.OPTION_1:
//                                query += "AND " + ICart.KEY_OPTION_ID_1 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_1 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                            case ICart.OPTION_2:
//                                query += "AND " + ICart.KEY_OPTION_ID_2 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_2 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                            case ICart.OPTION_3:
//                                query += "AND " + ICart.KEY_OPTION_ID_3 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_3 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                            case ICart.OPTION_4:
//                                query += "AND " + ICart.KEY_OPTION_ID_4 + " = '" + product.productOptionArrayList.get(optionProduct).optionId + "' ";
//                                query += "AND " + ICart.KEY_OPTION_VALUE_ID_4 + " = '" + product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId + "' ";
//                                break;
//                        }
//                    }
//                }
//            }
//        }
//        Log.d(TAG, "query = " + query);
//        Cursor cursor = db.rawQuery(query, null);
//        Product cartProduct = null;
//        Gson gson = new Gson();
//        try {
//            if (cursor != null && cursor.getCount() > 0) {
//                cartProduct = gson.fromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(ICart.KEY_PRODUCT_JSON))).toString(), Product.class);
//                cursor.close(); // that's important too, otherwise you're gonna leak cursors
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return cartProduct;
//    }

//    public static ArrayList<Product> getAllProductFromCart(Context context) {
//        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
//        Cursor cursor = db.rawQuery("select * from " + ICart.TABLE_CART, null);
//
//        Gson gson = new Gson();
//        ArrayList<Product> productArrayList = new ArrayList<>();
//
//        if (cursor != null && cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            do {
//                try {
//                    Product product = gson.fromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(ICart.KEY_PRODUCT_JSON))).toString(), Product.class);
//                    productArrayList.add(product);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } while (cursor.moveToNext());
//            cursor.close(); // that's important too, otherwise you're gonna leak cursors
//        }
//        return productArrayList;
//    }

    public static ArrayList<Integer> getAllProductIdFromCart(Context context) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + ICart.TABLE_CART, null);

        ArrayList<Integer> productIdArrayList = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                int productQuantity = cursor.getInt(cursor.getColumnIndex(ICart.KEY_PRODUCT_ID));
                productIdArrayList.add(productQuantity);
            } while (cursor.moveToNext());
            cursor.close(); // that's important too, otherwise you're gonna leak cursors
        }
        return productIdArrayList;
    }

//    public static void setProductParams(Context context, ArrayList<Product> productArrayList) {
////        Product product = null;
//        for (int i = 0; i < productArrayList.size(); i++) {
//            Product product = productArrayList.get(i);
//            Cursor cursor = getProductCursor(context, product);
//            if (cursor != null) {
//                product.productQuantity = cursor.getInt(cursor.getColumnIndex(ICart.KEY_PRODUCT_QUANTITY));
//                if (product.productHasOption && product.productOptionArrayList != null) {
//                    // For each option
//                    for (int optionProduct = 0; optionProduct < product.productOptionArrayList.size(); optionProduct++) {
//                        // For each value option
//                        for (int valueProduct = 0; valueProduct < product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.size(); valueProduct++) {
//                            String valueId = product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId;
//                            if (valueId.equals(cursor.getColumnIndex(ICart.KEY_OPTION_VALUE_ID_0)) ||
//                                    valueId.equals(cursor.getColumnIndex(ICart.KEY_OPTION_VALUE_ID_1)) ||
//                                    valueId.equals(cursor.getColumnIndex(ICart.KEY_OPTION_VALUE_ID_2)) ||
//                                    valueId.equals(cursor.getColumnIndex(ICart.KEY_OPTION_VALUE_ID_3)) ||
//                                    valueId.equals(cursor.getColumnIndex(ICart.KEY_OPTION_VALUE_ID_4))) {
//                                product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).isSelected = true;
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//            cursor.close();
//        }
//    }


    public static ArrayList<Product> setProductParams(Context context, ArrayList<Product> tmpProductArrayList) {
        ArrayList<Product> productArrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        String query = "select * from " + ICart.TABLE_CART;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String productId = cursor.getString(cursor.getColumnIndex(ICart.KEY_PRODUCT_ID));
                Log.e(TAG, "productId = " + productId);

                for (int i = 0; i < tmpProductArrayList.size(); i++) {
                    Log.e(TAG, "tmpProductArrayList.get(i).productId = " + tmpProductArrayList.get(i).productId);

                    if (tmpProductArrayList.get(i).productId.equals(productId)) {

                        Product product = tmpProductArrayList.get(i);

                        product.productQuantity = cursor.getInt(cursor.getColumnIndex(ICart.KEY_PRODUCT_QUANTITY));

                        Log.e(TAG, "product.productHasOption = " + product.productHasOption);
                        if (product.productHasOption && product.productOptionArrayList != null) {
                            // For each option
                            for (int optionProduct = 0; optionProduct < product.productOptionArrayList.size(); optionProduct++) {
                                // For each value option
                                for (int valueProduct = 0; valueProduct < product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.size(); valueProduct++) {
                                    String valueId = product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).optionValueId;
                                    Log.d(TAG, "1 valueId = " + valueId);
                                    if (valueId != null && valueId.equals(cursor.getString(cursor.getColumnIndex(ICart.KEY_OPTION_VALUE_ID_0))) ||
                                            valueId.equals(cursor.getString(cursor.getColumnIndex(ICart.KEY_OPTION_VALUE_ID_1))) ||
                                            valueId.equals(cursor.getString(cursor.getColumnIndex(ICart.KEY_OPTION_VALUE_ID_2))) ||
                                            valueId.equals(cursor.getString(cursor.getColumnIndex(ICart.KEY_OPTION_VALUE_ID_3))) ||
                                            valueId.equals(cursor.getString(cursor.getColumnIndex(ICart.KEY_OPTION_VALUE_ID_4)))) {
                                        product.productOptionArrayList.get(optionProduct).productOptionValueArrayList.get(valueProduct).isSelected = true;
                                        Log.d(TAG, "2 valueId = " + valueId);
                                        break;
                                    }
                                }
                            }
                        }
                        productArrayList.add(product);
                        tmpProductArrayList.remove(i);
                        break;
                    }
                }
            } while (cursor.moveToNext());
            cursor.close(); // that's important too, otherwise you're gonna leak cursors
        }

        return productArrayList;
    }

    public static void clearCart(Context context) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        db.delete(ICart.TABLE_CART, null, null);
    }

    public static void exportDB(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            FileChannel source;
            FileChannel destination;
            String currentDBPath = "/data/" + "com.shoppin.customer" + "/databases/" + IDatabase.DATABASE_NAME;
            String backupDBPath = IDatabase.DATABASE_NAME;
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(context, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
