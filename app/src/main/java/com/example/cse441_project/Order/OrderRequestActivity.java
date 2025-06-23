package com.example.cse441_project.Order;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cse441_project.Model.FoodItem;
import com.example.cse441_project.Model.Order;
import com.example.cse441_project.Model.OrderDetail;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderRequestActivity extends AppCompatActivity {
    private TextView tvTableNumber, txtTTien;
    private Button btnCancel, btnConfirm;
    private ImageView imgBack;
    private ArrayList<FoodItem> foodItems; // Danh sách chứa FoodItem
    private FirebaseFirestore db; // Khai báo Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.processing_order_request);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Khởi tạo các thành phần giao diện
        tvTableNumber = findViewById(R.id.tvTableNumber);
        txtTTien = findViewById(R.id.txtTTien);
        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);
        imgBack = findViewById(R.id.imgBack);
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        foodItems = (ArrayList<FoodItem>) intent.getSerializableExtra("foodItems");
        ArrayList<OrderDetail> orderDetails = (ArrayList<OrderDetail>) intent.getSerializableExtra("orderDetails");
        double totalAmount = 0;

        if (orderDetails != null) {
            for (OrderDetail detail : orderDetails) {
                TableRow tableRow = new TableRow(this);

                // STT
                TextView tvIndex = new TextView(this);
                tvIndex.setText(String.valueOf(orderDetails.indexOf(detail) + 1));
                tvIndex.setGravity(Gravity.CENTER); // Canh giữa
                tvIndex.setPadding(8, 8, 8, 8);
                tableRow.addView(tvIndex);

                // Tên món
                TextView tvItemName = new TextView(this);
                FoodItem foodItem = findFoodItemById(detail.getItemFoodID());
                if (foodItem != null) {
                    tvItemName.setText(foodItem.getFoodName()); // Lấy tên từ FoodItem
                } else {
                    tvItemName.setText("N/A"); // Nếu không tìm thấy món
                }
                tvItemName.setGravity(Gravity.CENTER); // Canh giữa
                tvItemName.setPadding(8, 8, 8, 8);
                tableRow.addView(tvItemName);

                // Số lượng
                TextView tvQuantity = new TextView(this);
                tvQuantity.setText(String.valueOf(detail.getQuantity()));
                tvQuantity.setGravity(Gravity.CENTER); // Canh giữa
                tvQuantity.setPadding(8, 8, 8, 8);
                tableRow.addView(tvQuantity);

                // Giá tiền
                if (foodItem != null) {
                    double price = foodItem.getPrice();
                    totalAmount += price * detail.getQuantity();

                    TextView tvPrice = new TextView(this);
                    tvPrice.setText(String.format("%,.0f", price));
                    tvPrice.setGravity(Gravity.CENTER); // Canh giữa
                    tvPrice.setPadding(8, 8, 8, 8);
                    tableRow.addView(tvPrice);
                }

                // Thêm hàng vào bảng
                tableLayout.addView(tableRow);
            }
        }

        // Hiển thị tổng tiền
        txtTTien.setText(String.format("Tổng tiền: %,.0f", totalAmount));

        // Các sự kiện nút
        imgBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        double finalTotalAmount = totalAmount;
        btnConfirm.setOnClickListener(v -> {
            // Logic xác nhận đơn và đẩy dữ liệu lên Firestore
            confirmOrder(orderDetails, finalTotalAmount);
        });
    }

    // Phương thức để tìm FoodItem dựa trên itemFoodID
    private FoodItem findFoodItemById(String itemFoodID) {
        for (FoodItem item : foodItems) {
            if (item.getItemFoodID().equals(itemFoodID)) {
                return item;
            }
        }
        return null;
    }

    private void confirmOrder(ArrayList<OrderDetail> orderDetails, double totalAmount) {
        // Tạo mã đơn hàng duy nhất
        String orderId = "order_" + System.currentTimeMillis();
        String employeeId = "employee1"; // Thay thế bằng ID của nhân viên thực hiện đơn hàng
        String orderDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String orderStatus = "Chưa hoàn thành"; // Hoặc trạng thái mặc định mà bạn muốn
        String paymentStatus = "Chưa thanh toán"; // Hoặc trạng thái mặc định mà bạn muốn
        String tableId = tvTableNumber.getText().toString(); // Số bàn từ TextView

        // Tạo Order object
        Order order = new Order(orderId, employeeId, orderDate, orderStatus, paymentStatus, tableId, totalAmount);

        // Đẩy Order object lên Firestore
        db.collection("Order") // Tên collection
                .document(orderId) // Sử dụng orderId làm ID tài liệu
                .set(order) // Ghi dữ liệu cho đơn hàng
                .addOnSuccessListener(aVoid -> {
                    // Sau khi lưu đơn hàng, lưu chi tiết đơn hàng
                    saveOrderDetails(orderId, orderDetails);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi xác nhận đơn hàng: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveOrderDetails(String orderId, ArrayList<OrderDetail> orderDetails) {
        if (orderDetails != null) {
            for (OrderDetail detail : orderDetails) {
                // Tạo đối tượng OrderDetail với orderId đã có
                OrderDetail orderDetail = new OrderDetail(orderId, detail.getItemFoodID(), detail.getQuantity());

                // Đẩy OrderDetail object lên Firestore
                db.collection("order").document(orderId).collection("orderDetails")
                        .add(orderDetail) // Thêm vào collection con orderDetails
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Chi tiết đơn hàng đã được lưu!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi khi lưu chi tiết đơn hàng: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            Toast.makeText(this, "Không có chi tiết đơn hàng để lưu.", Toast.LENGTH_SHORT).show();
        }
    }
}