package com.example.cse441_project.Order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.Model.Category;
import com.example.cse441_project.Model.FoodItem;
import com.example.cse441_project.Model.OrderDetail;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class OrderActivity extends AppCompatActivity {
    private RecyclerView recyclerView, rcvCategory;
    private OrderFoodAdapter adapter;
    private List<FoodItem> foodItemList;
    private DrawerLayout drawerLayout;
    private List<Category> categoryList = new ArrayList<>();
    private Button datmon;
    private ImageView searchImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.order_food);

        // Khởi tạo các thành phần giao diện
        recyclerView = findViewById(R.id.recyclerView);
        drawerLayout = findViewById(R.id.drawer_layout);
        rcvCategory = findViewById(R.id.rcv_category);
        searchImageView = findViewById(R.id.imageView3);
        datmon = findViewById(R.id.btnGoiMon);

        // Thiết lập GridLayout cho danh sách món ăn
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        foodItemList = new ArrayList<>();
        adapter = new OrderFoodAdapter(foodItemList);
        recyclerView.setAdapter(adapter);

        // Tải dữ liệu từ Firestore
        loadFoodItemsFromFirestore();

        // Sự kiện click cho nút datmon
        datmon.setOnClickListener(v -> {
            // Lấy danh sách OrderDetail từ adapter
            List<OrderDetail> orderDetails = adapter.getOrderDetails("001");

            // Tạo Intent và truyền dữ liệu foodItems và orderDetails
            Intent intent = new Intent(OrderActivity.this, OrderRequestActivity.class);
            intent.putExtra("orderDetails", new ArrayList<>(orderDetails)); // Truyền danh sách OrderDetail
            intent.putExtra("foodItems", new ArrayList<>(foodItemList)); // Truyền danh sách FoodItem
            startActivity(intent);
        });
    }

    private void loadFoodItemsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("FoodItem")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Chuyển đổi tài liệu thành đối tượng FoodItem
                            FoodItem foodItem = document.toObject(FoodItem.class);
                            foodItemList.add(foodItem); // Thêm vào danh sách
                        }
                        adapter.notifyDataSetChanged(); // Cập nhật adapter
                    } else {
                        Toast.makeText(OrderActivity.this, "Lỗi khi lấy dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(OrderActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}