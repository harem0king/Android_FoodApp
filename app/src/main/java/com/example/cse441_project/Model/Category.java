package com.example.cse441_project.Model;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Category {
    private String categoryId;
    private String categoryName;
    private String categoryImage;

    // Khởi tạo đối tượng Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Constructor mặc định
    public Category() {
    }


    public Category(String categoryId, String categoryName, String categoryImage) {
        this.categoryId = categoryId; // Khởi tạo mã danh mục
        this.categoryName = categoryName; // Khởi tạo tên danh mục
        this.categoryImage = categoryImage; // Khởi tạo thuộc tính hình ảnh
    }

    // Constructor với hai tham số
    public Category(String categoryName, String categoryImage) {
        this.categoryName = categoryName; // Khởi tạo tên danh mục
        this.categoryImage = categoryImage; // Khởi tạo thuộc tính hình ảnh
    }

    // Getter và Setter cho các thuộc tính
    public String getCategoryId() {
        return categoryId; // Lấy mã danh mục
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId; // Thiết lập mã danh mục
    }

    public String getCategoryName() {
        return categoryName; // Lấy tên danh mục
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName; // Thiết lập tên danh mục
    }

    public String getCategoryImage() {
        return categoryImage; // Lấy thuộc tính hình ảnh
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    // Lấy danh sách danh mục từ Firestore
    public void getAllCategories(final FirestoreCallback callback) {
        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Category> categoryList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("categoryName");
                            String image = document.getString("categoryImage");
                            categoryList.add(new Category(name, image));
                        }

                        callback.onCallback(categoryList);
                    } else {
                        // Xử lý lỗi
                        callback.onError(task.getException());
                    }
                });
    }

    // Interface callback để xử lý dữ liệu trả về
    public interface FirestoreCallback {
        void onCallback(ArrayList<Category> categoryList); // Gọi callback với danh sách danh mục
        void onError(Exception e); // Gọi callback khi có lỗi xảy ra
    }
}
