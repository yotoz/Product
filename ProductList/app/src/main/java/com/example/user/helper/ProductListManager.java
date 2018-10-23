package com.example.user.helper;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.user.helper.listener.OnAddProductListener;
import com.example.user.helper.listener.OnCheckUserListener;
import com.example.user.model.Product;
import com.example.user.model.User;


public class ProductListManager {
    // objects
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    // constants
    private final String USER_COLLECTION_NAME = "User";
    private final String PRODUCT_COLLECTION_NAME = "Product";

    public ProductListManager() {
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void addProduct(final String name, final String explanation, final Uri image, final OnAddProductListener onAddProductListener) {
        DatabaseReference ref = database.getReference()
                .child(PRODUCT_COLLECTION_NAME)
                .child(name);

        // Check to exist a product.
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    onAddProductListener.onExist();
                } else {
                    StorageReference storageRef = storage.getReference();

                    // Set path. it doesn't process that about file extension.
                    final String path = String.format("images/%s", name);

                    // Upload a image file.
                    storageRef.child(path).putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When finish to upload, insert a product.
                            DatabaseReference ref = database.getReference()
                                    .child(PRODUCT_COLLECTION_NAME)
                                    .child(name);

                            ref.child("explanation").setValue(explanation);
                            ref.child("image").setValue(path);
                            ref.child("exist").setValue(Product.EXIST_DEFAULT_VALUE);

                            onAddProductListener.onUpload();
                        }
                    });

                    // Doesn't process for fail.
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Method that check to exist a user.
     * @param id user id
     * @param pwd user password
     * @param onCheckUserListener callback method
     */
    public void checkUser(final String id, final String pwd, final OnCheckUserListener onCheckUserListener) {
        // Set the path
        Log.d("checkUser", "ID/PW:" + id + "/" + pwd);
        DatabaseReference ref = database.getReference()
                .child(USER_COLLECTION_NAME)
                .child(id);

        // Call a listener after checking
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setId(id);
                    onCheckUserListener.onSucceed(user);
                } else {
                    onCheckUserListener.onFailed();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
