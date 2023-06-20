package com.example.eeccoommaadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.example.eeccoommaadmin.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


//
//import com.example.ecomadmin.databinding.ActivityMainBinding;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private String id,title,decription,price;
    private Uri uri;

      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

          binding = ActivityMainBinding.inflate(getLayoutInflater());
          setContentView(binding.getRoot());

          //1
          binding.btnAddProduct.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  title = binding.title.getText().toString();
                  decription = binding.decription.getText().toString();
                  price = binding.price.getText().toString();
                  addProduct();
              }
          });
        //3 imageview
          binding.image.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                  intent.setType("image/*");

                  startActivityForResult(intent,100);
              }
          });

          //4 when click on upload button controll will go in uploadImage() method call
          binding.uploadPic.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  uploadImage();
              }
          });
    }

    //5 kidhar jakar image store ho storage me
    private void uploadImage()
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("products/"+id+".jpeg");
        storageReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        FirebaseFirestore.getInstance()
                                                .collection("products")
                                                .document(id)
                                                .update("image",uri.toString());
                                        Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                });

    }

    //2
    private void addProduct()
    {
        id = UUID.randomUUID().toString();//to create random id
        ProductModel productModel = new ProductModel(id,title,decription,price,null,true);
        FirebaseFirestore.getInstance()
                .collection("products")
                .document(id)
                .set(productModel);
        Toast.makeText(this, "Product Added", Toast.LENGTH_SHORT).show();
    }

    //4
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100)
        {
            uri = data.getData();
            binding.image.setImageURI(uri);
        }

    }
}