package com.example.cse441_project.FoodItem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cse441_project.Dialog.FoodConfirmUpdate;
import com.example.cse441_project.Dialog.FoodUpdateSuccess;
import com.example.cse441_project.Model.Category;
import com.example.cse441_project.Model.FoodItem;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditFood extends AppCompatActivity {
    private AutoCompleteTextView listCategoryName;
    private EditText price, foodName;
    private Button updateFood;
    private Button cancel;
    private ImageView image;
    private static final int PICK_IMAGE_REQUEST = 1;
    List<Category> category = new ArrayList<>();
    List<String> categoryNames = new ArrayList<>();
    private Uri imageUri;
    private String lastID;
    private String categoryName;
    private FoodItem foodItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_food_activity);
        listCategoryName = findViewById(R.id.listCategoryname);
        price = findViewById(R.id.price);
        foodName = findViewById(R.id.foodName);
        updateFood = findViewById(R.id.editFood);
        cancel = findViewById(R.id.cancel);
        image = findViewById(R.id.imageFood);


        foodItem = getIntent().getParcelableExtra("foodItem");
        if (foodItem != null) {
            populateFoodDetails(foodItem);
        } else {
            Toast.makeText(this, "Lỗi", Toast.LENGTH_SHORT).show();
        }

        image.setOnClickListener(v -> openGallery());

        GetCategoriesFromFirestore();
        cancel.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchEditFood.class);
            startActivity(intent);
        });


        updateFood.setOnClickListener(v -> {

            FoodConfirmUpdate dialog = new FoodConfirmUpdate(EditFood.this, () -> {
                uploadImageToFirebaseStorage(imageUri);
            });
            dialog.show();
        });


    }

    private void populateFoodDetails(FoodItem foodItem) {
        getCategoryById(foodItem.getCategoryId());
        foodName.setText(foodItem.getFoodName());
        imageUri = Uri.parse(foodItem.getImageUrl());

        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = numberFormat.format(foodItem.getPrice());
        price.setText(formattedPrice);

        Glide.with(this)
                .load(foodItem.getImageUrl())
                .into(image);
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(EditFood.this, "Vui lòng chọn ảnh trước khi thêm món ăn!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {

                    saveFoodToFirestore(downloadUrl.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(EditFood.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }


    private void saveFoodToFirestore(String imageUrl) {

        String selectedCategory = listCategoryName.getText().toString();
        String foodItemName = foodName.getText().toString();
        String priceString = price.getText().toString();

        if (selectedCategory.isEmpty() || foodItemName.isEmpty() || priceString.isEmpty()) {
            Toast.makeText(EditFood.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xem danh mục có tồn tại hay không

        try {
            double foodPrice = Double.parseDouble(priceString);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String id = foodItem.getItemFoodID();
            String idCategory = foodItem.getCategoryId();
            FoodItem foodItem = new FoodItem(id, foodItemName, foodPrice, idCategory, imageUrl);

            // Lưu món ăn vào Firestore
            db.collection("FoodItem").document(id).set(foodItem)
                    .addOnSuccessListener(aVoid -> {
                        FoodUpdateSuccess dialog = new FoodUpdateSuccess(this);
                        dialog.showSuccessDialog();
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditFood.this, "Lỗi khi lưu món ăn!", Toast.LENGTH_SHORT).show());
        } catch (NumberFormatException e) {
            Toast.makeText(EditFood.this, "Giá tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }


    private void getCategoryById(String categoryId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Category").document(categoryId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Category category = documentSnapshot.toObject(Category.class);
                        if (category != null) {
                            categoryName = category.getCategoryName();
                            Toast.makeText(EditFood.this, "Lấy thanành công", Toast.LENGTH_SHORT).show();
                            runOnUiThread(() -> {
                                listCategoryName.setText(categoryName);
                            });
                        } else {
                            Log.e("Firestore", "Category object is null!");
                        }
                    } else {
                        Log.e("Firestore", "No such category exists with ID: " + categoryId);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error retrieving data: " + e.getMessage()));
    }


    private void GetCategoriesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Category").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    category.clear();
                    categoryNames.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Category menuCategory = document.toObject(Category.class);
                        if (menuCategory != null) {
                            category.add(menuCategory);
                            categoryNames.add(menuCategory.getCategoryName());
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
                    listCategoryName.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error retrieving data", e));
    }

    // Mở thư viện ảnh
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                imageUri = data.getData();
                image.setImageURI(imageUri);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bạn đã hủy chọn hình ảnh", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Có lỗi xảy ra khi chọn hình ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
