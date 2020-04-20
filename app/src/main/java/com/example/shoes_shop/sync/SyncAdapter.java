package com.example.shoes_shop.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.shoes_shop.R;
import com.example.shoes_shop.database.DbContract;
import com.example.shoes_shop.database.dao.Order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "Alaa";
    //for dummy account
    public static final String ACCOUNT = "dummyaccount";
    //Refresh every 5 seconds
    private static final int SYNC_INTERVAL = 5;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private AccountManager mAccountManager;
    private Context mContext;
    public ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
        mContentResolver = context.getContentResolver();
        mContext = context;
    }
    /**
     * Constructor. Obtains handle to content resolver for later use.
     * on old version
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mAccountManager = AccountManager.get(context);
        mContext = context;
        mContentResolver =context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {


        // Building a print of the extras we got
        StringBuilder sb = new StringBuilder();
        if (extras != null) {
            for (String key : extras.keySet()) {
                sb.append(key + "[" + extras.get(key) + "] ");
            }
        }

        Log.e("alaa", TAG + "> onPerformSync for account[" + account.name + "]. Extras: " + sb.toString());

        try {
            // Get the auth token for the current account and
            // the userObjectId, needed for creating items on Parse.com account
//            String authToken = mAccountManager.blockingGetAuthToken(account,
//                    "Full access", true);
            String userObjectId = mAccountManager.getUserData(account,
                    "userObjectId");

            ParseComServerAccessor parseComService = new ParseComServerAccessor();

            Log.e("alaa", TAG + "> Get remote Orders");
            // Get orders from remote
            List<Order> remoteOrders = parseComService.getOrders( mContext);

            Log.e("alaa", TAG + "> Get local Oredrs");
            // Get orders from local
            ArrayList<Order> localOrders = new ArrayList<Order>();
            Cursor curOrder = provider.query(DbContract.CONTENT_ORDER_URI, null, null, null, null);
            if (curOrder != null) {
                while (curOrder.moveToNext()) {
                    localOrders.add(Order.fromCursor(curOrder));
                }
                curOrder.close();
            }

            // See what Local order are missing on Remote
            ArrayList<Order> ordersToRemote = new ArrayList<Order>();
            for (Order localOrder : localOrders) {
                if (!remoteOrders.contains(localOrder))
                    ordersToRemote.add(localOrder);
            }

            // See what Remote orders are missing on Local
            ArrayList<Order> ordersToLocal = new ArrayList<Order>();
            for (Order remoteOrder : remoteOrders) {
                if (!localOrders.contains(remoteOrder))
                    ordersToLocal.add(remoteOrder);
            }

            if (ordersToRemote.size() == 0) {
                Log.e("alaa", TAG + "> No local changes to update server");
            } else {
                Log.e("alaa", TAG + "> Updating remote server with local changes");

                // Updating remote orders
                for (Order remoteOrder : ordersToRemote) {
                    parseComService.putOrder(userObjectId, remoteOrder, mContext);
                }
            }

            if (ordersToLocal.size() == 0) {
                Log.e("alaa", TAG + "> No server changes to update local database");
            } else {
                Log.e("alaa", TAG + "> Updating local database with remote changes");

                // Updating local orders
                int i = 0;
                ContentValues[] showsToLocalValues = new ContentValues[ordersToLocal.size()];
                for (Order localOrder : ordersToLocal) {
                    showsToLocalValues[i++] = localOrder.getContentValuesOrders();
                }
                provider.bulkInsert(DbContract.CONTENT_ORDER_URI, showsToLocalValues);
            }

            Log.e("alaa", TAG + "> Finished.");


        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (IOException e) {
            syncResult.stats.numIoExceptions++;
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            syncResult.stats.numAuthExceptions++;
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
