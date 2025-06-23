package com.example.cse441_project.FoodItem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.Adapter.RevenueAdapter;
import com.example.cse441_project.Model.Order;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RevenueActivity extends AppCompatActivity {

    private EditText etNgayBatDau, etNgayKetThuc;
    private RecyclerView rvDoanhThu;
    private TextView tvTongDoanhThu;
    private FirebaseFirestore db;
    private RevenueAdapter adapter;
    private List<Order> orderList;
    private double totalRevenue = 0;
    private ImageView imgBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.revenue_statistics_activity);

        etNgayBatDau = findViewById(R.id.etNgayBatDau);
        etNgayKetThuc = findViewById(R.id.etNgayKetThuc);
        rvDoanhThu = findViewById(R.id.rvDoanhThu);
        tvTongDoanhThu = findViewById(R.id.tvTongDoanhThu);
        db = FirebaseFirestore.getInstance();
        imgBack = findViewById(R.id.img_back);

        orderList = new ArrayList<>();
        adapter = new RevenueAdapter(orderList);
        rvDoanhThu.setLayoutManager(new LinearLayoutManager(this));
        rvDoanhThu.setAdapter(adapter);

        etNgayBatDau.setOnClickListener(v -> showDatePickerDialog(etNgayBatDau));
        etNgayKetThuc.setOnClickListener(v -> showDatePickerDialog(etNgayKetThuc));
        imgBack.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // *** ĐÂY LÀ DÒNG QUAN TRỌNG NHẤT CẦN SỬA ***
                    // Luôn định dạng ngày thành "yyyy-MM-dd" (ví dụ: "2025-06-23")
                    String date = String.format(Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    editText.setText(date);

                    if (!etNgayBatDau.getText().toString().isEmpty() && !etNgayKetThuc.getText().toString().isEmpty()) {
                        loadOrdersByDate();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadOrdersByDate() {
        String startDate = etNgayBatDau.getText().toString();
        String endDate = etNgayKetThuc.getText().toString();

        if (startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn khoảng thời gian", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startDate.compareTo(endDate) > 0) {
            Toast.makeText(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Order")
                .whereGreaterThanOrEqualTo("orderDate", startDate)
                .whereLessThanOrEqualTo("orderDate", endDate)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    orderList.clear();
                    totalRevenue = 0;

                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(RevenueActivity.this, "Không tìm thấy đơn hàng nào.", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Order order = document.toObject(Order.class);
                            orderList.add(order);
                            totalRevenue += order.getTotalAmount();
                        }
                    }

                    adapter.notifyDataSetChanged();
                    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    tvTongDoanhThu.setText("Tổng doanh thu: " + currencyFormatter.format(totalRevenue));
                })
                .addOnFailureListener(e -> {
                    Log.e("RevenueActivity", "Error fetching orders", e);
                    Toast.makeText(RevenueActivity.this, "Lỗi khi tải dữ liệu.", Toast.LENGTH_SHORT).show();
                });
    }
}
