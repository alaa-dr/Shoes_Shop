package com.example.shoes_shop.ui.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    private EditText mUserName;
    private EditText mPassword;
    private TextView mRegisterButton;
    private ProgressBar mloadingProgressBarRegister;
    private Context mContext;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Register.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View root = inflater.inflate(R.layout.fragment_register, container, false);
        Log.e("fuck",String.valueOf(root));
        mUserName = root.findViewById(R.id.user_name_register);
        mPassword = root.findViewById(R.id.password_register);
        mRegisterButton = root.findViewById(R.id.register);
        mloadingProgressBarRegister = root.findViewById(R.id.loading_register);


        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mUserName.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();

                if (!name.isEmpty() || !pass.isEmpty()) {
                    Register(name, pass);
                } else {
                    mUserName.setError("please insert user name ");
                    mPassword.setError("please insert passworld ");
                }

            }
        });

        return root;
    }

    private void Register(final String name, final String pass) {
        mloadingProgressBarRegister.setVisibility(View.VISIBLE);
        mRegisterButton.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.URL_REGISTER),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                String name = jsonObject.getString("name").trim();
                                Toast.makeText(mContext, "Register , success  " + name, Toast.LENGTH_SHORT).show();
                                mloadingProgressBarRegister.setVisibility(View.GONE);
                                mRegisterButton.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(mContext, AdministratorActivity.class);
                                startActivity(intent);
                                if (getActivity()!=null){getActivity().finish();}
                            } else {
                                Toast.makeText(mContext, "fail ,Register again please " + name, Toast.LENGTH_SHORT).show();
                                mloadingProgressBarRegister.setVisibility(View.GONE);
                                mRegisterButton.setVisibility(View.VISIBLE);

                            }
                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "Error2" + e.toString(), Toast.LENGTH_SHORT).show();
                            mloadingProgressBarRegister.setVisibility(View.GONE);
                            mRegisterButton.setVisibility(View.VISIBLE);

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Error3" + error.toString(), Toast.LENGTH_SHORT).show();
                mloadingProgressBarRegister.setVisibility(View.GONE);
                mRegisterButton.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("name",name);
                params.put("password", pass);
                //temporary variable to replace it with spinner
                params.put("branchId","1");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
