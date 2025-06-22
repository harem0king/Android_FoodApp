package com.example.cse441_project.FoodItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.Adapter.EditFoodAdapter;
import com.example.cse441_project.Category.CategoryActivity;
import com.example.cse441_project.Employee.ActivityFormEmployee;
import com.example.cse441_project.Model.FoodItem;
import com.example.cse441_project.Order.OrderActivity;
import com.example.cse441_project.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchEditFood extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditFoodAdapter adapter;
    private List<FoodItem> foodItemList;
    EditText searchEditText ;
    private NavigationView navigationView;
    private ImageView imgMenu;
    private Button searchButton;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.search_edit_food);
        recyclerView = findViewById(R.id.recyclerView);
        searchEditText = findViewById(R.id.search_edt);
        searchButton = findViewById(R.id.search_btn);
        drawerLayout = findViewById(R.id.drawer_layout);
        imgMenu = findViewById(R.id.menu_img);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        navigationView = findViewById(R.id.nav_view);

        searchButton.setOnClickListener(v -> {

            String keyword = searchEditText.getText().toString().trim();
            if (!keyword.isEmpty()) {
                searchFoodItems(keyword);
            } else {
                Toast.makeText(SearchEditFood.this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });
        loadFoodItemsFromFirestore();
        if (savedInstanceState == null) {
            addFragment(new HomeFragment());
        }
        foodItemList = new ArrayList<>();
        adapter = new EditFoodAdapter(SearchEditFood.this,foodItemList);
        recyclerView.setAdapter(adapter);

        imgMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_manage_food) {
                    View view = findViewById(R.id.nav_manage_food);
                    PopupMenu popupMenu = new PopupMenu(SearchEditFood.this, view);
                    popupMenu.getMenuInflater().inflate(R.menu.sub_menu, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem submenuItem) {
                            if (submenuItem.getItemId() == R.id.add) {
                                startActivity(new Intent(SearchEditFood.this, AddFoodActivity.class));
                                return true;
                            } else if (submenuItem.getItemId() == R.id.edit) {
                                startActivity(new Intent(SearchEditFood.this, SearchEditFood.class));

                                return true;
                            } else if (submenuItem.getItemId() == R.id.delete) {
                                startActivity(new Intent(SearchEditFood.this, DeleteFood.class));
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });

                    popupMenu.show();

                } else if (item.getItemId() == R.id.nav_manage_food_type) {
                    startActivity(new Intent(SearchEditFood.this, CategoryActivity.class));
                } else if (item.getItemId() == R.id.nav_manage_table) {
                    startActivity(new Intent(SearchEditFood.this, HomeActivity.class));
                } else if (item.getItemId() == R.id.nav_top_selling_items) {
                    startActivity(new Intent(SearchEditFood.this, TopSaleActivity.class));
                } else if (item.getItemId() == R.id.nav_revenue_statistics) {
                    startActivity(new Intent(SearchEditFood.this, RevenueActivity.class));
                } else if (item.getItemId() == R.id.nav_manage_employee) {
                    startActivity(new Intent(SearchEditFood.this, ActivityFormEmployee.class));
                } else if (item.getItemId() == R.id.nav_order_food) {
                    startActivity(new Intent(SearchEditFood.this, OrderActivity.class));
                } else {
                    Toast.makeText(SearchEditFood.this, "Item không xác định", Toast.LENGTH_SHORT).show();
                }

//                drawerLayout.closeDrawer(GravityCompat.START); // Đóng ngăn kéo sau khi chọn
                return true; // Trả về true để xác nhận sự kiện đã được xử lý
            }
        });



    }
    private void loadFoodItemsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu từ collection "FoodItem"
        db.collection("FoodItem")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Chuyển đổi tài liệu thành đối tượng FoodItem
                            FoodItem foodItem = document.toObject(FoodItem.class);
                            foodItemList.add(foodItem);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchEditFood.this, "Lỗi khi lấy dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SearchEditFood.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void searchFoodItems(String keyword) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Truy vấn với tên trường bạn muốn tìm kiếm, ví dụ "name"
        db.collection("FoodItem")
                .whereGreaterThanOrEqualTo("foodName", keyword)
                .whereLessThanOrEqualTo("foodName", keyword + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        foodItemList.clear(); // Xóa danh sách cũ để cập nhật dữ liệu mới
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FoodItem foodItem = document.toObject(FoodItem.class);
                            foodItemList.add(foodItem);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchEditFood.this, "Lỗi khi lấy dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SearchEditFood.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.home_fragment, fragment);
        fragmentTransaction.commit();
    }
}
