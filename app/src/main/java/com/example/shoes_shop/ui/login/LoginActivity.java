package com.example.shoes_shop.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shoes_shop.AdministratorActivity;
import com.example.shoes_shop.R;
import com.example.shoes_shop.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText mUserName;
    private EditText mPassword;
    private TextView mloginButton;
    private ProgressBar mloadingProgressBarLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /** @Get views */
        mUserName = findViewById(R.id.username_login);
        mPassword = findViewById(R.id.password_login);
        mloginButton = findViewById(R.id.login);
        mloadingProgressBarLogin = findViewById(R.id.loading_login);
        /**execut checking and login or login if the use already signup*/
        checkingAndLogin(LoginActivity.this);

    }


    /**
     * Checking if user already login if yes go to AdministratorActivity else LOGIN
     */
    private void checkingAndLogin(Context context) {
        if (PreferenceUtils.getName(context) != null) {
            int branchId = PreferenceUtils.getBranchId(context);
            intentSwitch(branchId);

        } else {
            mloginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = mUserName.getText().toString().trim();
                    String pass = mPassword.getText().toString().trim();

                    if (name.isEmpty()) {
                        mUserName.setError("please insert user name ");
                    } else if (pass.isEmpty()) {
                        mPassword.setError("please insert passworld ");
                    } else {
                        Login(name, pass);
                    }

                }
            });
        }
    }


    /**
     * send stringRequest to server to checking authentication
     */

    private void Login(final String name, final String pass) {
        mloadingProgressBarLogin.setVisibility(View.VISIBLE);
        mloginButton.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST
                , getString(R.string.URL_LOGIN)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //handling response from server like that :
                    //{"login":[{"name":"alaa"}],"branchId":"0","status":"success"}
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    JSONArray jsonArray = jsonObject.getJSONArray("login");
                    String branchId = jsonObject.getString("branchId");
                    if (status.equals("success")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String name = object.getString("name").trim();

                            Toast.makeText(LoginActivity.this, "success ,Hello " + name, Toast.LENGTH_SHORT).show();
                            mloadingProgressBarLogin.setVisibility(View.GONE);
                            mloginButton.setVisibility(View.VISIBLE);
                            intentSwitch(Integer.parseInt(branchId));
                            PreferenceUtils.saveName(name, LoginActivity.this);
                            PreferenceUtils.savePassword(pass, LoginActivity.this);
                            PreferenceUtils.saveBranchId(Integer.parseInt(branchId), LoginActivity.this);
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "fail ,Register please " + name, Toast.LENGTH_SHORT).show();
                        mloadingProgressBarLogin.setVisibility(View.GONE);
                        mloginButton.setVisibility(View.VISIBLE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error2" + e.toString(), Toast.LENGTH_SHORT).show();
                    mloadingProgressBarLogin.setVisibility(View.GONE);
                    mloginButton.setVisibility(View.VISIBLE);

                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Error3" + error.toString(), Toast.LENGTH_SHORT).show();
                        mloadingProgressBarLogin.setVisibility(View.GONE);
                        mloginButton.setVisibility(View.VISIBLE);


                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("password", pass);
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }


    //TODO need to create path to clientActivity not for Adminstreator only

    /**
     * @param branchId is number of each branch on user table MYSQL
     *                 handling the explicit intent to each activity depending on @param branch ID
     */
    private void intentSwitch(int branchId) {

        switch (branchId) {
            //id 0 is path to admin activity
            case 0: {
                Intent intent = new Intent(this, AdministratorActivity.class);
                startActivity(intent);
            }
            //id : 1 and 2 and 3 is path to new activity for client
            case 1:
            case 2:
            case 3:


        }

    }
}
