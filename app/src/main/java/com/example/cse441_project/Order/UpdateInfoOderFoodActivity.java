package com.example.cse441_project.Order;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cse441_project.Model.FoodItem;
import com.example.cse441_project.Model.Order;
import com.example.cse441_project.Model.OrderDetail;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UpdateInfoOderFoodActivity extends AppCompatActivity {
    private TextView tvOrderStatus, tvPaymentStatus;
    private RadioGroup rgOrderStatus, rgPaymentStatus;
    private FirebaseFirestore db;
    private String orderId; // Nhận orderId từ Intent hoặc trong Activity
    private TextView tvTableNumber, tvEmployeeCode, tvCallDate, tvOrderCode, tvTotalAmount;
    private List<OrderDetail> orderDetails;
    private TableLayout tableLayout;
    private Button btnUpdate;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_infor_oderfood);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Nhận orderId từ Intent
        orderId = getIntent().getStringExtra("ORDER_ID");
        btnUpdate = findViewById(R.id.btnUpdate);
        tableLayout = findViewById(R.id.tableLayout);
        tvTableNumber = findViewById(R.id.tvTableNumber);
        tvEmployeeCode = findViewById(R.id.tvemployeecode);
        tvCallDate = findViewById(R.id.tvcalldate);
        tvOrderCode = findViewById(R.id.tvordercode);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        rgOrderStatus = findViewById(R.id.rgOrderStatus);
        rgPaymentStatus = findViewById(R.id.rgPaymentStatus);

        tableLayout.removeAllViews();

        // Lấy thông tin order và order details
        getOrderDetails(orderId);
        btnUpdate.setOnClickListener(v -> {
            String orderStatus = getOrderStatus();
            String paymentStatus = getPaymentStatus();

            // Cập nhật thông tin vào Firestore
            db.collection("Order").document(orderId)
                    .update("orderStatus", orderStatus, "paymentStatus", paymentStatus)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("UpdateInfo", "Cập nhật thành công");
                        // Thực hiện hành động sau khi cập nhật thành công
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UpdateInfo", "Lỗi khi cập nhật thông tin đơn hàng: ", e);
                    });
        });
    }

    private void getOrderDetails(String orderId) {
        // Lấy Order từ Firestore
        db.collection("Order").whereEqualTo("orderId", orderId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            order = document.toObject(Order.class);
                            displayOrderInfo(order);
                            // Sau khi có thông tin order, lấy order details
                            getOrderDetail(orderId);
                            updateRadioGroups(order);
                        }
                    } else {
                        Log.e("OrderDetails", "Lỗi khi lấy thông tin đơn hàng: ", task.getException());
                    }
                });
    }

    private void getOrderDetail(String orderId) {
        db.collection("OrderDetail").whereEqualTo("orderId", orderId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderDetails = new ArrayList<>();

                        TableRow headerRow = new TableRow(this);
                        headerRow.setBackgroundColor(Color.parseColor("#CCCCCC"));

                        // Tạo tiêu đề cho các cột
                        String[] headers = {"STT", "Tên món", "Số lượng", "Giá tiền"};
                        for (String header : headers) {
                            TextView textView = new TextView(this);
                            textView.setText(header);
                            textView.setPadding(8, 8, 8, 8);
                            textView.setGravity(Gravity.CENTER);
                            headerRow.addView(textView);
                        }
                        tableLayout.addView(headerRow);

                        // Duyệt qua các chi tiết đơn hàng và thêm vào bảng
                        int index = 1; // Chỉ số bắt đầu từ 1
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OrderDetail orderDetail = document.toObject(OrderDetail.class);
                            orderDetails.add(orderDetail);

                            TableRow tableRow = new TableRow(this);

                            TextView tvIndex = new TextView(this);
                            tvIndex.setText(String.valueOf(index++));
                            tvIndex.setPadding(8, 8, 8, 8);
                            tvIndex.setGravity(Gravity.CENTER);
                            tableRow.addView(tvIndex);

                            // Lấy thông tin món ăn bằng callback
                            getFoodById(orderDetail.getItemFoodID(), foodItem -> {
                                TextView tvItemName = new TextView(this);
                                tvItemName.setText(foodItem != null ? foodItem.getFoodName() : "Không tìm thấy");
                                tvItemName.setPadding(8, 8, 8, 8);
                                tvItemName.setGravity(Gravity.CENTER);
                                tableRow.addView(tvItemName);

                                // Số lượng
                                TextView tvQuantity = new TextView(this);
                                tvQuantity.setText(String.valueOf(orderDetail.getQuantity()));
                                tvQuantity.setPadding(8, 8, 8, 8);
                                tvQuantity.setGravity(Gravity.CENTER);
                                tableRow.addView(tvQuantity);

                                // Giá tiền
                                double price = foodItem != null ? foodItem.getPrice() * orderDetail.getQuantity() : 0; // Lấy giá từ foodItem
                                TextView tvPrice = new TextView(this);
                                tvPrice.setText(String.valueOf(price));
                                tvPrice.setPadding(8, 8, 8, 8);
                                tvPrice.setGravity(Gravity.CENTER);
                                tableRow.addView(tvPrice);

                                // Thêm hàng vào bảng sau khi lấy xong thông tin món
                                tableLayout.addView(tableRow);
                            });
                        }
                    } else {
                        Log.e("OrderDetail", "Lỗi khi lấy chi tiết đơn hàng: ", task.getException());
                    }
                });
    }

    private void getFoodById(String itemFoodID, FoodItemCallback callback) {
        db.collection("FoodItem")
                .whereEqualTo("itemFoodID", itemFoodID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        FoodItem foodItem = document.toObject(FoodItem.class);
                        callback.onCallback(foodItem); // Trả về toàn bộ FoodItem
                    } else {
                        Log.d("FoodItem", "Không tìm thấy món ăn với ID: " + itemFoodID);
                        callback.onCallback(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FoodItem", "Lỗi khi truy vấn Firestore: ", e);
                    callback.onCallback(null);
                });
    }

    // Định nghĩa lại interface callback
    public interface FoodItemCallback {
        void onCallback(FoodItem foodItem);
    }

    private String getOrderStatus() {
        int selectedOrderStatusId = rgOrderStatus.getCheckedRadioButtonId();
        if (selectedOrderStatusId != -1) {
            RadioButton selectedOrderStatus = findViewById(selectedOrderStatusId);
            return selectedOrderStatus.getText().toString();
        }
        return "Chưa hoàn thành"; // Giá trị mặc định
    }

    private String getPaymentStatus() {
        int selectedPaymentStatusId = rgPaymentStatus.getCheckedRadioButtonId();
        if (selectedPaymentStatusId != -1) {
            RadioButton selectedPaymentStatus = findViewById(selectedPaymentStatusId);
            return selectedPaymentStatus.getText().toString();
        }
        return "Chưa thanh toán"; // Giá trị mặc định
    }

    private void displayOrderInfo(Order order) {
        tvTableNumber.setText("Bàn số: " + order.getTableId());
        tvEmployeeCode.setText("Mã nhân viên: " + order.getEmployeeId());
        tvCallDate.setText("Ngày gọi: " + order.getOrderDate());
        tvOrderCode.setText("Mã gọi món: " + order.getOrderId());
        tvTotalAmount.setText("Tổng tiền: " + order.getTotalAmount());
        tvOrderStatus.setText("Tình trạng gọi món: " + order.getOrderStatus());
        tvPaymentStatus.setText("Tình trạng thanh toán: " + order.getPaymentStatus());
    }

    private void updateRadioGroups(Order order) {
        // Cập nhật trạng thái đơn hàng
        String orderStatus = order.getOrderStatus();
        String paymentStatus = order.getPaymentStatus();

        // Cập nhật cho rgOrderStatus
        if ("Chưa hoàn thành".equals(orderStatus)) {
            rgOrderStatus.check(R.id.rbIncomplete);
        } else if ("Đã hoàn thành".equals(orderStatus)) {
            rgOrderStatus.check(R.id.rbComplete);
        }

        // Cập nhật cho rgPaymentStatus
        if ("Chưa thanh toán".equals(paymentStatus)) {
            rgPaymentStatus.check(R.id.rbUnpaid);
        } else if ("Đã thanh toán".equals(paymentStatus)) {
            rgPaymentStatus.check(R.id.rbPaid);
        }
    }
}
