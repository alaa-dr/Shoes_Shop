package com.example.shoes_shop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shoes_shop.ui.home.*;

import com.google.android.material.snackbar.Snackbar;

public class AddOrderActivity extends AppCompatActivity {
    EditText msupplier_client_name, mOrder_type, mStatus, mDiscount1, mCountity1, mDiscount2, mCountity2;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        //to show Tip
        View view = findViewById(android.R.id.content);
        Snackbar.make(view, "Here you add order", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();

        //Get views
        msupplier_client_name = findViewById(R.id.editText);
        mOrder_type = findViewById(R.id.editText2);
        mStatus = findViewById(R.id.editText3);
        mDiscount1 = findViewById(R.id.editText4);
        mCountity1 = findViewById(R.id.editText5);

        mCountity2 = findViewById(R.id.editText7);
        b1 = (Button) findViewById(R.id.button);

        mDiscount2 = findViewById(R.id.editText6);
        mDiscount2.setVisibility(View.VISIBLE);

        mCountity2 = findViewById(R.id.editText7);
        mCountity2.setVisibility(View.VISIBLE);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



//                Intent insertIntent =new Intent(AddOrderActivity.this,HomeFragment.class);
//                insertIntent.putExtra("order_type",mOrder_type.getText().toString());
//                insertIntent.putExtra("supplier_client_name",msupplier_client_name.getText().toString());
//                insertIntent.putExtra("status",mStatus.getText().toString());
//                startActivity(insertIntent);
//                finish();

                //set argument is method used instead intents to reach to fragment
                Bundle bundle =new Bundle();
                bundle.putString("order_type",mOrder_type.getText().toString());
                bundle.putString("supplier_client_name",msupplier_client_name.getText().toString());
                bundle.putString("status",mStatus.getText().toString());
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(bundle);
                //to solve problem that we can't reach to home fragment
                getSupportFragmentManager().beginTransaction().add(homeFragment,"ww").commit();
                finish();

            }
        });


    }


}
