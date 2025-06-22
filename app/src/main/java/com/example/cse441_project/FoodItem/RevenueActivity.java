package com.example.cse441_project.FoodItem;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

        // Khởi tạo các view và Firestore
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
        imgBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });
    }

    // Hiển thị DatePickerDialog để chọn ngày
    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    editText.setText(date);

                    // Tự động tải dữ liệu khi đã chọn xong cả ngày bắt đầu và kết thúc
                    if (!etNgayBatDau.getText().toString().isEmpty() && !etNgayKetThuc.getText().toString().isEmpty()) {
                        loadOrdersByDate();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    // Tải dữ liệu hóa đơn theo khoảng ngày
    private void loadOrdersByDate() {
        String startDate = etNgayBatDau.getText().toString();
        String endDate = etNgayKetThuc.getText().toString();

        if (startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(RevenueActivity.this, "Vui lòng chọn khoảng thời gian", Toast.LENGTH_SHORT).show();
            return;
        }

        // Truy vấn dữ liệu từ Firestore
        db.collection("Order")
                .whereGreaterThanOrEqualTo("orderDate", startDate)
                .whereLessThanOrEqualTo("orderDate", endDate)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Xóa dữ liệu cũ và đặt lại doanh thu
                    orderList.clear();
                    totalRevenue = 0;

                    // Duyệt qua kết quả và thêm vào danh sách hóa đơn
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Order order = document.toObject(Order.class);
                        orderList.add(order);
                        totalRevenue += order.getTotalAmount();
                    }

                    // Cập nhật adapter và tổng doanh thu
                    adapter.notifyDataSetChanged();
                    tvTongDoanhThu.setText("Tổng doanh thu: " + totalRevenue + "đ");
                })
                .addOnFailureListener(e -> Log.e("RevenueActivity", "Error fetching orders", e));
    }
}
