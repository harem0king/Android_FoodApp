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
import androidx.fragment.app.FragmentManager;

import com.example.cse441_project.Model.OrderDetail;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InvoiceActivity extends AppCompatActivity {
    private Button printInvoiceButton;
    private ImageView backButton;
    private TextView tvTableName, invoiceDateTextView, totalAmountTextView;
    private TableLayout invoiceTableLayout;
    private FirebaseFirestore db;
    private String tableId;
    private String orderId;
    private double totalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        printInvoiceButton = findViewById(R.id.printInvoiceButton);
        backButton = findViewById(R.id.backButton);
        tvTableName = findViewById(R.id.tableNameTextView);
        invoiceDateTextView = findViewById(R.id.invoiceDateTextView);
        invoiceTableLayout = findViewById(R.id.invoiceTableLayout);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);

        // Retrieve table ID from Intent
        Intent intent = getIntent();
        tableId = intent.getStringExtra("tableId");

        // Display table name and set invoice date
        setTableName();
        setInvoiceDate();

        // Fetch order ID based on table ID and load order items
        fetchOrderIdAndLoadOrderItems();

        // Print invoice button click listener
        printInvoiceButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            SuccessDialogFragment successDialog = new SuccessDialogFragment();
            successDialog.show(fragmentManager, "SuccessDialog");

            // Update order status to "paid"
            updateOrderStatusToPaid();

            // Set table status to "available"
            updateTableStatusToAvailable();
        });

        // Back button click listener
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(InvoiceActivity.this, TableListActivity.class);
            startActivity(backIntent);
            finish();
        });
    }

    private void setTableName() {
        if (tableId != null) {
            db.collection("Tables")
                    .document(tableId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String tableName = documentSnapshot.getString("tableName");
                            tvTableName.setText("Hóa đơn bàn: " + tableName);
                        } else {
                            Toast.makeText(this, "Không tìm thấy bàn", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi tải dữ liệu bàn: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void setInvoiceDate() {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        invoiceDateTextView.setText(currentDate);
    }

    private void fetchOrderIdAndLoadOrderItems() {
        if (tableId != null) {
            db.collection("Order")
                    .whereEqualTo("tableId", tableId)
                    .whereEqualTo("orderStatus", "Chưa hoàn thành") // Lọc theo trạng thái đơn hàng nếu cần
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                orderId = document.getId(); // Lấy orderId từ document
                                loadOrderItems(); // Gọi loadOrderItems sau khi lấy được orderId
                                break; // Lấy order đầu tiên nếu có nhiều đơn hàng trùng tableId
                            }
                        } else {
                            Toast.makeText(this, "Không tìm thấy đơn hàng cho bàn này", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi tải đơn hàng: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadOrderItems() {
        if (orderId != null) {
            db.collection("Order").document(orderId).collection("OrderDetails")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        int index = 1;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            OrderDetail detail = document.toObject(OrderDetail.class);
                            fetchAndAddOrderDetailRow(detail, index++);
                        }

                        // Display the total amount after loading all items
                        totalAmountTextView.setText(String.format("Tổng tiền: %,.0f VND", totalAmount));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi tải dữ liệu đơn hàng: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void fetchAndAddOrderDetailRow(OrderDetail detail, int index) {
        String itemFoodID = detail.getItemFoodID();

        if (itemFoodID == null) {
            Toast.makeText(this, "ID món ăn không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("FoodItem").document(itemFoodID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String itemName = documentSnapshot.getString("foodName");
                        Double price = documentSnapshot.getDouble("price");

                        // Kiểm tra nếu giá là null
                        if (price == null) {
                            Toast.makeText(this, "Giá không tồn tại cho món ăn: " + itemName, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Add row to the table
                        addOrderDetailRow(detail, index, itemName, price);
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin món ăn", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi lấy giá món ăn: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addOrderDetailRow(OrderDetail detail, int index, String itemName, double price) {
        TableRow tableRow = new TableRow(this);

        // STT
        TextView tvIndex = new TextView(this);
        tvIndex.setText(String.valueOf(index));
        tvIndex.setGravity(Gravity.CENTER);
        tvIndex.setPadding(8, 8, 8, 8);
        tableRow.addView(tvIndex);

        // Tên món
        TextView tvItemName = new TextView(this);
        tvItemName.setText(itemName);
        tvItemName.setGravity(Gravity.CENTER);
        tvItemName.setPadding(8, 8, 8, 8);
        tableRow.addView(tvItemName);

        // Số lượng
        TextView tvQuantity = new TextView(this);
        tvQuantity.setText(String.valueOf(detail.getQuantity()));
        tvQuantity.setGravity(Gravity.CENTER);
        tvQuantity.setPadding(8, 8, 8, 8);
        tableRow.addView(tvQuantity);

        // Giá tiền
        TextView tvPrice = new TextView(this);
        tvPrice.setText(String.format("%,.0f VND", price));
        tvPrice.setGravity(Gravity.CENTER);
        tvPrice.setPadding(8, 8, 8, 8);
        tableRow.addView(tvPrice);

        // Thành tiền
        double itemTotal = price * detail.getQuantity();
        totalAmount += itemTotal;
        TextView tvItemTotal = new TextView(this);
        tvItemTotal.setText(String.format("%,.0f VND", itemTotal));
        tvItemTotal.setGravity(Gravity.CENTER);
        tvItemTotal.setPadding(8, 8, 8, 8);
        tableRow.addView(tvItemTotal);

        // Add row to table layout
        invoiceTableLayout.addView(tableRow);

        // Update total amount display
        totalAmountTextView.setText(String.format("Tổng tiền: %,.0f VND", totalAmount));
    }

    private void updateOrderStatusToPaid() {
        if (orderId != null) {
            db.collection("Order")
                    .document(orderId)
                    .update("status", "paid")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Đơn hàng đã được cập nhật thành 'đã thanh toán'", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi cập nhật trạng thái đơn hàng: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateTableStatusToAvailable() {
        if (tableId != null) {
            db.collection("Tables")
                    .document(tableId)
                    .update("status", "available")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Trạng thái bàn đã được cập nhật thành 'available'", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi cập nhật trạng thái bàn: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
