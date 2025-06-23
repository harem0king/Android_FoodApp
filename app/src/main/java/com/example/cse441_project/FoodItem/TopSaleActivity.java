package com.example.cse441_project.FoodItem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.Adapter.HomeAdapter;
import com.example.cse441_project.Model.FoodItem;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TopSaleActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private List<FoodItem> foodItemList = new ArrayList<>();
    private Map<String, Integer> foodSalesCount = new HashMap<>();
    private ImageView imgBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_sale_activity);

        imgBack = findViewById(R.id.img_back);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new HomeAdapter(foodItemList);
        fetchTopSellingItems();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        imgBack.setOnClickListener(v->{
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
        });

    }

    public void fetchTopSellingItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Order")
                .get()
                .addOnSuccessListener(orderSnapshot -> {
                    for (QueryDocumentSnapshot orderDoc : orderSnapshot) {
                        String orderId = orderDoc.getId();
                        // Access the OrderDetail sub-collection for each Order
                        db.collection("Order")
                                .document(orderId)
                                .collection("OrderDetails")
                                .get()
                                .addOnSuccessListener(orderDetailSnapshot -> {
                                    for (QueryDocumentSnapshot detailDoc : orderDetailSnapshot) {
                                        String itemFoodID = detailDoc.getString("itemFoodID");
                                        int quantity = detailDoc.getLong("quantity").intValue();
                                        foodSalesCount.put(itemFoodID, foodSalesCount.getOrDefault(itemFoodID, 0) + quantity);
                                    }
                                    // Once all OrderDetails are processed, calculate top items
                                    List<String> topSellingFoodIds = foodSalesCount.entrySet().stream()
                                            .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                                            .limit(6)
                                            .map(Map.Entry::getKey)
                                            .collect(Collectors.toList());
                                    fetchFoodDetails(topSellingFoodIds);
                                })
                                .addOnFailureListener(e -> Log.e("TopSaleActivity", "Error fetching order detail for order " + orderId, e));
                    }
                })
                .addOnFailureListener(e -> Log.e("TopSaleActivity", "Error fetching orders", e));
    }

    private void fetchFoodDetails(List<String> topSellingFoodIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("FoodItem")
                .whereIn("itemFoodID", topSellingFoodIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        FoodItem foodItem = document.toObject(FoodItem.class);
                        foodItemList.add(foodItem);
                        Log.d("TopSaleActivity", "Food Item: " + foodItem.getFoodName() + ", Sold Quantity: " +
                                foodSalesCount.get(foodItem.getItemFoodID()));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("TopSaleActivity", "Error fetching food item details", e));
    }

}
