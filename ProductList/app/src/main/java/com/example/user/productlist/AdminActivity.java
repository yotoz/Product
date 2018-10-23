package com.example.user.productlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.user.productlist.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    public void showAddProduct(View v) {
        Intent newActivity = new Intent(this, AddProductActivity.class);
        newActivity.putExtra("user", getIntent().getSerializableExtra("user"));
        startActivity(newActivity);
    }

    public void showProductList(View v) {
        Toast.makeText(this, "This is not supported yet.", Toast.LENGTH_SHORT).show();
    }
}
