package com.example.user.productlist;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.example.user.helper.ProductListManager;
import com.example.user.helper.listener.OnAddProductListener;
import com.example.user.productlist.R;

public class AddProductActivity extends AppCompatActivity {

    // constants
    private static final int REQ_OPEN_IMAGE = 1000;

    // objects
    private ProductListManager productListManager = null;
    private Uri selectedImageUri = null;

    // UI Components
    private EditText etProductName;
    private EditText etProductExplanation;
    private ImageView ivProduct;
    private Button btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Connect variables to UI resources
        etProductName = findViewById(R.id.etProductName);
        etProductExplanation = findViewById(R.id.etProductExplanation);
        ivProduct = findViewById(R.id.ivProduct);
        btnUpload = findViewById(R.id.btnUpload);

        // Initialize objects
        FirebaseApp.initializeApp(this);
        productListManager = new ProductListManager();

        // Initialize events
        ivProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openBrowser = new Intent();
                openBrowser.setType("image/*");
                openBrowser.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(openBrowser, REQ_OPEN_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // If user choose a image.
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_OPEN_IMAGE) {
            selectedImageUri = data.getData();
            ivProduct.setImageURI(selectedImageUri);
        }
    }

    public void uploadProduct(View v) {

        btnUpload.setEnabled(false);

        productListManager.addProduct(
                etProductName.getText().toString(),
                etProductExplanation.getText().toString(),
                this.selectedImageUri,
                new OnAddProductListener() {
                    @Override
                    public void onUpload() {
                        Toast.makeText(AddProductActivity.this, "Added a Product.", Toast.LENGTH_SHORT).show();
                        btnUpload.setEnabled(true);
                    }

                    @Override
                    public void onExist() {
                        Toast.makeText(AddProductActivity.this, "This product is already exist.", Toast.LENGTH_SHORT).show();
                        btnUpload.setEnabled(true);
                    }
                }
        );
    }
}
