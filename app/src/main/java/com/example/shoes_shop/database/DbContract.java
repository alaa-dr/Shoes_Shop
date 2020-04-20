package com.example.shoes_shop.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class DbContract implements BaseColumns {
    public static final int SYNC_STATUS_SUCCESS=0;
    public static final int SYNC_STATUS_FAILED=1;
    public static final String SERVER_URL_GET_ALL_ORDERS="http://192.168.56.1/Shoes_shop/syncOrder.php";
    public static final String SERVER_URL_INSERT_ORDER = "http://192.168.56.1/Shoes_shop/insertOrder.php";

    public static final String PATH_ORDERS="orders";
    public static final String AUTHORITY = "com.example.android.shoes_shop_adapter";
    public static final Uri BASE_URI=Uri.parse("content://"+AUTHORITY);
    public static final Uri CONTENT_ORDER_URI=Uri.withAppendedPath(BASE_URI,PATH_ORDERS);


    public static final String DATABASE_NAME="shoes_shop";
    //order table
    public static final String ORDER_ID=BaseColumns._ID;
    public static final String TABLE_ORDER_NAME = "orders";
    public static final String USER_ID = "user_id";
    public static final String ORDER_TYPE = "order_type";
    public static final String SUPPLIER_CLIENT_NAME="supplier_client_name";
    public static final String SYNC_STATUS = "sync_status";
    public static final String STATUS ="status";


}
