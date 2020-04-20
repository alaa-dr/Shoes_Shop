package com.example.shoes_shop.sync;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;


import com.example.shoes_shop.ui.home.HomeFragment;
import com.google.gson.Gson;

import com.example.shoes_shop.database.DbContract;
import com.example.shoes_shop.database.dao.Order;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Scanner;

public class ParseComServerAccessor {
    //this list is the return value of @getOrders
    private List<Order> orders;

    public List<Order> getOrders(final Context context) throws Exception {

        Log.e("alaa", "getOrders auth[" + "  this suppose be authentication String  "+ "]");

        //make http get method to get all orders and return list of them
        StringBuffer stringBuffer = null;
        Scanner in = null;
        String json = null;
        URL url = null;
        HttpURLConnection httpConection = null;
        String ss = null;
        try {
            url = new URL(DbContract.SERVER_URL_GET_ALL_ORDERS);
            httpConection = (HttpURLConnection) url.openConnection();
            httpConection.setRequestMethod("GET");
            int response = httpConection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = new Scanner(httpConection.getInputStream());
                in.useDelimiter("\\A");
                boolean hasInput = in.hasNext();
                if (hasInput) {

                    json = in.next();
                }
                if (json != null) {
                    JSONArray jsonArray = new JSONArray(json);
                    Log.e("fuck", String.valueOf(jsonArray));
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Order>>() {
                    }.getType();
                    orders = gson.fromJson(String.valueOf(jsonArray), listType);
                } else {
                    return null;
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (httpConection != null && in != null) {
                httpConection.disconnect();
                in.close();
            }
        }


        return orders;
    }

    public void putOrder(String userId, final Order orderToAdd, final Context context) throws Exception {

        Log.e("alaa", "putOrder [" + orderToAdd.getSupplier_client_name() + "]");

        JSONObject jsonObject = null;
        Scanner in = null;
        String json = null;
        URL url = null;
        HttpURLConnection httpConection = null;
        try {

            url = new URL(DbContract.SERVER_URL_INSERT_ORDER);
            httpConection = (HttpURLConnection) url.openConnection();
            httpConection.setRequestMethod("POST");
            httpConection.setDoInput(true);
            httpConection.setDoOutput(true);
            String a1 = URLEncoder.encode(String.valueOf(orderToAdd.getUser_id()), "UTF-8");
            String a2 = URLEncoder.encode(String.valueOf(orderToAdd.getOrder_type()), "UTF-8");
            String a3 = URLEncoder.encode(orderToAdd.getSupplier_client_name(), "UTF-8");
            String a4 = URLEncoder.encode(orderToAdd.getStatus(), "UTF-8");
            String param = "user_id=" + a1 + "&" + "order_type=" + a2 + "&" + "supplier_client_name=" + a3
                    + "&" + "status=" + a4;
            OutputStream os = httpConection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(param);
            writer.flush();
            writer.close();
            os.close();

            int response = httpConection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = new Scanner(httpConection.getInputStream());
                in.useDelimiter("\\A");
                boolean hasInput = in.hasNext();
                if (hasInput) {
                    json = in.next();
                }
                jsonObject = new JSONObject(json);
            }
            if (jsonObject!=null) {
                String status = jsonObject.getString("status");
                if (status.equals("success")) {
                    ContentValues contentValues = orderToAdd.getContentValuesOrders();
                    context.getContentResolver().insert(DbContract.CONTENT_ORDER_URI, contentValues);

                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (httpConection != null && in != null) {
                httpConection.disconnect();
                in.close();
            }
        }


    }


}
