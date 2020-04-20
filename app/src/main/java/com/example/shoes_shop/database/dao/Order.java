package com.example.shoes_shop.database.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.shoes_shop.database.DbContract;

public class Order {
    private String supplier_client_name, status;
    private int user_id, order_type, sync_status;

    public Order(int user_id, int order_type, String supplier_client_name
            , int sync_status, String status) {
        this.supplier_client_name = supplier_client_name;
        this.status = status;
        this.user_id = user_id;
        this.order_type = order_type;
        this.sync_status = sync_status;
    }

    public String getSupplier_client_name() {
        return supplier_client_name;
    }

    public void setSupplier_client_name(String supplier_client_name) {
        this.supplier_client_name = supplier_client_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public int getSync_status() {
        return sync_status;
    }

    public void setSync_status(int sync_status) {
        this.sync_status = sync_status;
    }

    /**
     * Convenient method to get the objects data members in ContentValues object.
     * This will be useful for Content Provider operations,
     * which use ContentValues object to represent the data.
     *
     * @return Content Values od Orders
     */
    public ContentValues getContentValuesOrders(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.USER_ID,user_id);
        contentValues.put(DbContract.ORDER_TYPE,order_type);
        contentValues.put(DbContract.SUPPLIER_CLIENT_NAME,supplier_client_name);
        contentValues.put(DbContract.SYNC_STATUS,sync_status);
        contentValues.put(DbContract.STATUS,status);
        return contentValues;

    }
    public static ContentValues getContentValuesOrders(int user_id, int order_type, String supplier_client_name
            , int sync_status, String status){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.USER_ID,user_id);
        contentValues.put(DbContract.ORDER_TYPE,order_type);
        contentValues.put(DbContract.SUPPLIER_CLIENT_NAME,supplier_client_name);
        contentValues.put(DbContract.SYNC_STATUS,sync_status);
        contentValues.put(DbContract.STATUS,status);
        return contentValues;

    }

    // Create a TvShow object from a cursor
    public static Order fromCursor(Cursor curTvShows) {

        String supplier_client_name = curTvShows.getString(curTvShows.getColumnIndex(DbContract.SUPPLIER_CLIENT_NAME));
        String status = curTvShows.getString(curTvShows.getColumnIndex(DbContract.STATUS));
        int user_id = curTvShows.getInt(curTvShows.getColumnIndex(DbContract.USER_ID));
        int order_type = curTvShows.getInt(curTvShows.getColumnIndex(DbContract.ORDER_TYPE));
        int sync_status = curTvShows.getInt(curTvShows.getColumnIndex(DbContract.SYNC_STATUS));

        return new Order(user_id,order_type,supplier_client_name, sync_status,status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (user_id != order.user_id) return false;
        if (order_type != order.order_type) return false;
        if (sync_status != order.sync_status) return false;
        if (!supplier_client_name.equals(order.supplier_client_name)) return false;
        if (!status.equals(order.status)) return false;

        return true;
    }

//    @Override
//    public int hashCode() {
//        int result = name.hashCode();
//        result = 31 * result + year;
//        return result;
//    }

//    @Override
//    public String toString() {
//        return name + " (" + year + ")";
//    }

}
