package com.example.cse441_project.FoodItem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.Adapter.CategoryHomeAdapter;
import com.example.cse441_project.Adapter.HomeAdapter;
import com.example.cse441_project.Category.CategoryActivity;
import com.example.cse441_project.Employee.ActivityFormEmployee;
import com.example.cse441_project.Model.Category;
import com.example.cse441_project.Model.FoodItem;
import com.example.cse441_project.Order.OrderActivity;
import com.example.cse441_project.Order.TableListActivity;
import com.example.cse441_project.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView, rcvCategory;
    private HomeAdapter adapter;
    private List<FoodItem> foodItemList;
    private DrawerLayout drawerLayout;
    private CategoryHomeAdapter categoryAdapter;
    private NavigationView navigationView;
    private ImageView imgMenu;
    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        rcvCategory = findViewById(R.id.rcv_category);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        rcvCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        imgMenu = findViewById(R.id.menu_img);

        loadFoodItemsFromFirestore();

        foodItemList = new ArrayList<>();
        adapter = new HomeAdapter(foodItemList);
        recyclerView.setAdapter(adapter);
        fetchCategoriesFromFirestore();

        categoryAdapter = new CategoryHomeAdapter(categoryList);
        rcvCategory.setAdapter(categoryAdapter);

        if (savedInstanceState == null) {
            addFragment(new HomeFragment());
        }

        imgMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Kiểm tra vai trò và điều chỉnh menu
        checkRoleAndSetMenuVisibility();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_manage_food) {
                    View view = findViewById(R.id.nav_manage_food);
                    PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
                    popupMenu.getMenuInflater().inflate(R.menu.sub_menu, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem submenuItem) {
                            if (submenuItem.getItemId() == R.id.add) {
                                startActivity(new Intent(HomeActivity.this, AddFoodActivity.class));
                                return true;
                            } else if (submenuItem.getItemId() == R.id.edit) {
                                startActivity(new Intent(HomeActivity.this, SearchEditFood.class));
                                return true;
                            } else if (submenuItem.getItemId() == R.id.delete) {
                                startActivity(new Intent(HomeActivity.this, DeleteFood.class));
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });

                    popupMenu.show();

                } else if (item.getItemId() == R.id.nav_manage_food_type) {
                    startActivity(new Intent(HomeActivity.this, CategoryActivity.class));
                } else if (item.getItemId() == R.id.nav_manage_table) {
                    startActivity(new Intent(HomeActivity.this, TableListActivity.class));
                } else if (item.getItemId() == R.id.nav_top_selling_items) {
                    startActivity(new Intent(HomeActivity.this, TopSaleActivity.class));
                } else if (item.getItemId() == R.id.nav_revenue_statistics) {
                    startActivity(new Intent(HomeActivity.this, RevenueActivity.class));
                }
                else if (item.getItemId() == R.id.nav_manage_employee) {
                    startActivity(new Intent(HomeActivity.this, ActivityFormEmployee.class));
                }
                else if (item.getItemId() == R.id.nav_order_food) {
                    startActivity(new Intent(HomeActivity.this, OrderActivity.class));
                }
                else if (item.getItemId() == R.id.nav_revenue_statistics) {
                    startActivity(new Intent(HomeActivity.this, RevenueActivity.class));
                }else {
                    Toast.makeText(HomeActivity.this, "Item không xác định", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.home_fragment, fragment);
        fragmentTransaction.commit();
    }

    private void loadFoodItemsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("FoodItem")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FoodItem foodItem = document.toObject(FoodItem.class);
                            foodItemList.add(foodItem);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(HomeActivity.this, "Lỗi khi lấy dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchCategoriesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Category")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Category category = document.toObject(Category.class);
                        categoryList.add(category);
                    }
                    categoryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("CategoryActivity", "Error fetching categories", e));
    }

    // Hàm kiểm tra vai trò người dùng và điều chỉnh hiển thị menu tương ứng
    private void checkRoleAndSetMenuVisibility() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String employeeId = sharedPreferences.getString("employeeId", "");
        getRoleById(employeeId);
    }

    // Hàm lấy vai trò từ Firestore dựa trên employeeId và điều chỉnh menu
    private void getRoleById(String employeeId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Employee").document(employeeId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if ("nhân viên".equals(role)) {
                            navigationView.getMenu().findItem(R.id.nav_manage_food).setVisible(false);
                            navigationView.getMenu().findItem(R.id.nav_manage_food_type).setVisible(false);
                            navigationView.getMenu().findItem(R.id.nav_top_selling_items).setVisible(false);
                            navigationView.getMenu().findItem(R.id.nav_revenue_statistics).setVisible(false);
                            navigationView.getMenu().findItem(R.id.nav_manage_employee).setVisible(false);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("HomeActivity", "Error fetching role", e));
    }
}
