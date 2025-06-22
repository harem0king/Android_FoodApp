package com.example.cse441_project.Category;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cse441_project.Dialog.ConfirmationDialog;
import com.example.cse441_project.Model.Category;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private GridLayout grCategory;
    private Button btnAddCategory;
    private List<Category> categoryList = new ArrayList<>(); // Danh sách các danh mục
    private FirebaseFirestore db; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);

        grCategory = findViewById(R.id.grCategory);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        db = FirebaseFirestore.getInstance(); // Khởi tạo Firestore instance

        // Thiết lập sự kiện cho nút thêm danh mục
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryActivity.this, CategoryAddActivity.class);
                startActivity(intent);
            }
        });

        // Thêm logic để hiển thị danh mục trong grCategory
        loadCategories();
    }

    private void loadCategories() {
        db.collection("Category").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryList.clear(); // Xóa danh sách trước khi thêm mới
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Category category = document.toObject(Category.class);
                        category.setCategoryId(document.getId()); // Gán auto-ID từ Firestore
                        categoryList.add(category); // Thêm danh mục vào danh sách
                    }
                    Log.d("CategoryList", "Total categories: " + categoryList.size()); // In ra số danh mục
                    displayCategories(); // Hiển thị danh mục trong GridLayout
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error retrieving categories", e);
                });
    }


    private void displayCategories() {
        grCategory.removeAllViews(); // Xóa tất cả các view hiện có trong GridLayout

        for (Category category : categoryList) {
            // Tạo một LinearLayout cho mỗi danh mục
            LinearLayout categoryLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.category_card, null);

            // Thiết lập tên danh mục
            TextView txtCategoryName = categoryLayout.findViewById(R.id.txtcategoryName);
            txtCategoryName.setText(category.getCategoryName());

            // Thiết lập hình ảnh bằng Glide
            ImageView imgCategory = categoryLayout.findViewById(R.id.txtImageView);
            Glide.with(this)
                    .load(category.getCategoryImage())  // URL hình ảnh
                    .placeholder(R.drawable.background_red)
                    .error(R.drawable.background_red)
                    .into(imgCategory);

            // Thiết lập sự kiện cho nút chỉnh sửa
            Button btnEdit = categoryLayout.findViewById(R.id.btnEdit);
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(CategoryActivity.this, CategoryEditActivity.class);
                intent.putExtra("categoryId", category.getCategoryId()); // Truyền ID danh mục sang Activity chỉnh sửa
                startActivity(intent);
            });

            // Thiết lập sự kiện cho nút xóa
            Button btnDelete = categoryLayout.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(v -> {
                // Khởi tạo dialog xác nhận
                ConfirmationDialog confirmationDialog = new ConfirmationDialog(CategoryActivity.this);
                confirmationDialog.setTitle("Xác nhận xóa");
                confirmationDialog.setMessage("Bạn có chắc chắn muốn xóa danh mục này không?");

                // Thiết lập hành động cho nút OK
                confirmationDialog.setOkButtonClickListener(view -> {
                    // Xóa danh mục từ Firestore
                    db.collection("Category").document(category.getCategoryId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                confirmationDialog.dismiss(); // Đóng dialog
                                updateCategories(); // Cập nhật danh sách danh mục
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error deleting category", e);
                                confirmationDialog.dismiss(); // Đóng dialog nếu có lỗi
                            });
                });

                // Thiết lập hành động cho nút Hủy
                confirmationDialog.setCancelButtonClickListener(view -> {
                    confirmationDialog.dismiss(); // Đóng dialog khi hủy
                });

                // Hiển thị dialog xác nhận
                confirmationDialog.show();
            });

            // Thêm LinearLayout vào GridLayout cho mỗi lần lặp
            grCategory.addView(categoryLayout);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCategories(); // Gọi lại phương thức để lấy dữ liệu mới
    }

    public void updateCategories() {
        loadCategories(); // Gọi lại để tải danh mục từ Firestore
    }
}
