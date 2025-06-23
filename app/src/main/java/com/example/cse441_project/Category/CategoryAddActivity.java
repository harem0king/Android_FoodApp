package com.example.cse441_project.Category;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse441_project.Dialog.SuccessDialog;
import com.example.cse441_project.Model.Category;
import com.example.cse441_project.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class CategoryAddActivity extends AppCompatActivity {
    private EditText editCategoryName;
    private Button btnSaveCategory;
    private ImageView addImageView;
    private Button btnBack;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_add);

        // Khởi tạo các thành phần giao diện
        editCategoryName = findViewById(R.id.editcategoryName);
        btnSaveCategory = findViewById(R.id.btnSaveCategory);
        btnBack = findViewById(R.id.btnBack);
        addImageView = findViewById(R.id.addImageView);

        // Bắt sự kiện cho nút trở về
        btnBack.setOnClickListener(v -> finish());

        addImageView.setOnClickListener(v -> openGallery());

        btnSaveCategory.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageToFirebaseStorage(imageUri);
            } else {
                saveCategoryToFirestore(null);
            }
        });
    }

    // Hàm tải hình ảnh lên Firebase Storage
    private void uploadImageToFirebaseStorage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("category_images/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                    saveCategoryToFirestore(downloadUrl.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(CategoryAddActivity.this, "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show());
    }

    private void saveCategoryToFirestore(String imageUrl) {
        String categoryName = editCategoryName.getText().toString();

        if (categoryName.isEmpty()) {
            Toast.makeText(CategoryAddActivity.this, "Vui lòng nhập tên danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tìm ID lớn nhất hiện tại bắt đầu bằng "category_"
        db.collection("Category")
                .orderBy("categoryId", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int newCategoryIdNumber = 1; // Bắt đầu với 01

                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String currentMaxId = document.getString("categoryId");
                        // Lấy phần số từ ID hiện tại
                        int currentMaxIdNumber = Integer.parseInt(currentMaxId.replace("category_", ""));
                        newCategoryIdNumber = currentMaxIdNumber + 1;
                    }

                    // Tạo ID mới dạng category_01, category_02, ...
                    String newCategoryId = String.format("category_%02d", newCategoryIdNumber);

                    // Tạo đối tượng Category với ID mới
                    Category category = new Category(newCategoryId, categoryName, imageUrl);

                    // Lưu danh mục vào Firestore với ID đã xác định
                    db.collection("Category").document(newCategoryId)
                            .set(category)
                            .addOnSuccessListener(aVoid -> showSuccessDialog())
                            .addOnFailureListener(e -> Toast.makeText(CategoryAddActivity.this, "Lỗi khi lưu danh mục!", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(CategoryAddActivity.this, "Lỗi khi truy vấn CategoryID!", Toast.LENGTH_SHORT).show());
    }

    // Hiển thị SuccessDialog
    private void showSuccessDialog() {
        SuccessDialog successDialog = new SuccessDialog();
        successDialog.show(getSupportFragmentManager(), "SuccessDialog");
    }

    // Mở thư viện ảnh
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Lấy URI của hình ảnh được chọn
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            addImageView.setImageURI(imageUri);
        }
    }
}
