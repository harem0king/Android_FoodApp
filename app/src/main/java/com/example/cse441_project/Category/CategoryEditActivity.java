package com.example.cse441_project.Category;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cse441_project.Dialog.EditSuccessDialog;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class CategoryEditActivity extends AppCompatActivity {

    private EditText editCategoryName; // Tên danh mục
    private ImageView addImageView; // Hình ảnh danh mục
    private Button buttonSave; // Nút Lưu
    private Button buttonCancel; // Nút Hủy
    private Button btnBack; // Nút Trở về
    private String categoryId; // Mã danh mục
    private String updatedImage = ""; // Biến lưu hình ảnh đã chọn
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Lấy thông tin từ Intent
        categoryId = getIntent().getStringExtra("categoryId");

        // Khởi tạo các view
        editCategoryName = findViewById(R.id.editCategoryName);
        addImageView = findViewById(R.id.addImageView);
        buttonSave = findViewById(R.id.button_save);
        buttonCancel = findViewById(R.id.button_cancel);
        btnBack = findViewById(R.id.btnBack);

        // Tải thông tin danh mục từ Firestore
        loadCategoryData();

        // Thiết lập sự kiện cho nút lưu
        buttonSave.setOnClickListener(v -> saveCategory());

        // Thiết lập sự kiện cho nút hủy
        buttonCancel.setOnClickListener(v -> finish());

        // Thiết lập sự kiện cho nút trở về
        btnBack.setOnClickListener(v -> finish());

        // Thiết lập sự kiện cho ImageView để chọn ảnh
        addImageView.setOnClickListener(v -> chooseImage());
    }

    private void loadCategoryData() {
        db.collection("Category").document(categoryId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy dữ liệu từ document
                        String categoryName = documentSnapshot.getString("categoryName");
                        String categoryImage = documentSnapshot.getString("categoryImage");

                        // Cập nhật giao diện
                        editCategoryName.setText(categoryName);
                        // Sử dụng Glide để tải hình ảnh vào ImageView
                        Glide.with(this)
                                .load(categoryImage)
                                .placeholder(R.drawable.background_red) // Hình ảnh placeholder
                                .into(addImageView);
                    } else {
                        Log.d("CategoryEdit", "Danh mục không tồn tại");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting category", e);
                });
    }

    private void saveCategory() {
        String updatedName = editCategoryName.getText().toString();

        // Nếu người dùng không chọn hình ảnh mới, giữ nguyên hình ảnh cũ
        if (updatedImage.isEmpty()) {
            updatedImage = ""; // Hoặc giá trị hình ảnh cũ nếu bạn đã lưu trước đó
        }

        // Lưu lại thông tin đã chỉnh sửa vào Firestore
        db.collection("Category").document(categoryId)
                .update("categoryName", updatedName, "categoryImage", updatedImage)
                .addOnSuccessListener(aVoid -> {
                    // Hiển thị dialog thành công
                    EditSuccessDialog dialog = new EditSuccessDialog(this, "Thông báo", "Sửa thư mục thành công");
                    dialog.show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error updating category", e);
                });
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                addImageView.setImageURI(imageUri); // Hiển thị hình ảnh đã chọn
                updatedImage = imageUri.toString(); // Lưu URI hình ảnh để lưu vào Firestore
            }
        }
    }
}