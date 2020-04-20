package com.example.shoes_shop.ui.home;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shoes_shop.AddOrderActivity;
import com.example.shoes_shop.R;
import com.example.shoes_shop.database.DbContract;
import com.example.shoes_shop.database.DbHelper;
import com.example.shoes_shop.database.dao.Order;
import com.example.shoes_shop.database.dao.OrderHome;
import com.example.shoes_shop.sync.GenericAccountService;
import com.example.shoes_shop.sync.SyncAdapter;
import com.example.shoes_shop.sync.SyncUtils;
import com.example.shoes_shop.ui.home.Recycle.RecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerAdapter adapter;
    private ArrayList<OrderHome> arrayList = new ArrayList<>();
    private Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static HomeFragment instance;


    /**
     * Handle to a SyncObserver. The ProgressBar element is visible until the SyncObserver reports
     * that the sync is complete.
     *
     * <p>This allows us to delete our SyncObserver once the application is no longer in the
     * foreground.
     */
    private Object mSyncObserverHandle;

    /**
     * Options menu used to populate ActionBar.
     */
    private Menu mOptionsMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    //TODO we need to creat handler for database to refresh when stringrequest exception exceut best solution for now is content provider
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = root.findViewById(R.id.HomeRecycleView);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(adapter);


        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AddOrderActivity.class);
                startActivity(intent);
            }
        });


        if (getArguments() != null) {
            saveToLocalStorage(1, Integer.parseInt(getArguments().getString("order_type"))
                    , getArguments().getString("supplier_client_name")
                    ,0, getArguments().getString("status"));
        }


        getLoaderManager().initLoader(0, null, this);

        return root;


    }


    //to call fragment method from separate class
    public static HomeFragment getInstance(){
        return instance;
    }


    public void readFromLocalStorage() {
        arrayList.clear();

        String projection[] = {DbContract.SUPPLIER_CLIENT_NAME, DbContract.SYNC_STATUS};
        Cursor cursor = mContext.getContentResolver().query(DbContract.CONTENT_ORDER_URI, projection, null, null, null);
        while (cursor.moveToNext()) {
            String supplier_client_name = cursor.getString(cursor.getColumnIndex(DbContract.SUPPLIER_CLIENT_NAME));
            int sync_status = cursor.getInt(cursor.getColumnIndex(DbContract.SYNC_STATUS));
            arrayList.add(new OrderHome(supplier_client_name, sync_status));
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }

//    public void saveToAppServer(final int user_id, final int order_type, final String supplier_client_name
//            , final String status) {
//
//        if (checkNetworkConnection()) {
//            StringRequest stringRequest = new StringRequest(Request.Method.POST
//                    , DbContract.SERVER_URL_INSERT_ORDER
//                    , new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    try {
//                        //handling response from server like that :
//                        //{"status":"success"} or {"status":"fail"}
//                        Log.e("alaa", response);
//                        JSONObject jsonObject = new JSONObject(response);
//                        String status = jsonObject.getString("status");
//                        if (status.equals("success")) {
//                            saveToLocalStorage(user_id, order_type, supplier_client_name, DbContract.SYNC_STATUS_SUCCESS, status);
//                        } else {
//                            saveToLocalStorage(user_id, order_type, supplier_client_name, DbContract.SYNC_STATUS_FAILED, status);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            saveToLocalStorage(user_id, order_type, supplier_client_name, DbContract.SYNC_STATUS_FAILED, status);
//                        }
//                    }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("user_id", String.valueOf(user_id));
//                    params.put("order_type", String.valueOf(order_type));
//                    params.put("supplier_client_name", supplier_client_name);
//                    params.put("status", status);
//                    return params;
//                }
//            };
//
//
//            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
//            requestQueue.add(stringRequest);
//        } else {
//            saveToLocalStorage(user_id, order_type, supplier_client_name, DbContract.SYNC_STATUS_FAILED, status);
//
//        }
//
//
//    }

    public void saveToLocalStorage(int user_id, int order_type, String supplier_client_name
            , int sync_status, String status) {

        ContentValues contentValues = Order.getContentValuesOrders(user_id, order_type, supplier_client_name, sync_status, status);
        mContext.getContentResolver().insert(DbContract.CONTENT_ORDER_URI, contentValues);
        readFromLocalStorage();
    }

    //checking network connection
    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    //handling vanishing of getActivity (Context) when do some thing in back ground such as volly
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mContext = context;
        }
        //if there no account
        SyncUtils.CreateSyncAccount(context);
    }


    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    /**
     * Query the content provider for data.
     *
     * <p>Loaders do queries in a background thread. They also provide a ContentObserver that is
     * triggered when data in the content provider changes. When the sync adapter updates the
     * content provider, the ContentObserver responds by resetting the loader and then reloading
     * it.
     */

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),            // Context
                DbContract.CONTENT_ORDER_URI,             // URI
                null,                           // Projection
                null,                            // Selection
                null,                        // Selection args
                null);                          // Sort
    }

    /**
     * Move the Cursor returned by the query into the ListView adapter. This refreshes the existing
     * UI with the data in the Cursor.
     */

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        readFromLocalStorage();
    }

    /**
     * Called when the ContentObserver defined for the content provider detects that data has
     * changed. The ContentObserver resets the loader, and then re-runs the loader. In the adapter,
     * set the Cursor value to null. This removes the reference to the Cursor, allowing it to be
     * garbage-collected.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        readFromLocalStorage();
    }

    /**
     * Create the ActionBar.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mOptionsMenu = menu;
        inflater.inflate(R.menu.syncbtn, menu);
    }

    /**
     * Respond to user gestures on the ActionBar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // If the user clicks the "Refresh" button.
            case R.id.menu_refresh:
                SyncUtils.TriggerRefresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Set the state of the Refresh button. If a sync is active, turn on the ProgressBar widget.
     * Otherwise, turn it off.
     *
     * @param refreshing True if an active sync is occuring, false otherwise
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setRefreshActionButtonState(boolean refreshing) {
        if (mOptionsMenu == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }

        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }

    /**
     * Crfate a new anonymous SyncStatusObserver. It's attached to the app's ContentResolver in
     * onResume(), and removed in onPause(). If status changes, it sets the state of the Refresh
     * button. If a sync is active or pending, the Refresh button is replaced by an indeterminate
     * ProgressBar; otherwise, the button itself is displayed.
     */

    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /** Callback invoked with the sync adapter status changes. */
        @Override
        public void onStatusChanged(int which) {
            getActivity().runOnUiThread(new Runnable() {
                /**
                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                 * runs on the UI thread.
                 */
                @Override
                public void run() {
                    // Create a handle to the account that was created by
                    // SyncService.CreateSyncAccount(). This will be used to query the system to
                    // see how the sync status has changed.
                    Account account = GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE);
                    if (account == null) {
                        // GetAccount() returned an invalid value. This shouldn't happen, but
                        // we'll set the status to "not refreshing".
                        setRefreshActionButtonState(false);
                        return;
                    }

                    // Test the ContentResolver to see if the sync adapter is active or pending.
                    // Set the state of the refresh button accordingly.
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, DbContract.AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, DbContract.AUTHORITY);
                    setRefreshActionButtonState(syncActive || syncPending);
                }
            });
        }
    };


}



