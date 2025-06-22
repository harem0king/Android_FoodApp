package com.example.cse441_project.Employee;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse441_project.Model.Employee;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyInfoActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView tvEmployeeId, tvUsername, tvFullName, tvRole, tvGender, tvDob, tvCmnd,tvPass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info_activity);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Reference the TextView elements
        tvEmployeeId = findViewById(R.id.employee_id);
        tvUsername = findViewById(R.id.et_username);
        tvFullName = findViewById(R.id.et_employee_name);
        tvPass = findViewById(R.id.et_password);
        tvRole = findViewById(R.id.sp_role); // Assuming you're still using it as TextView
        tvGender = findViewById(R.id.et_gender);
        tvDob = findViewById(R.id.et_dob);
        tvCmnd = findViewById(R.id.et_cmnd);

        // Get employee ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String employeeId = sharedPreferences.getString("employeeId", null);

       if (employeeId != null) {
            getEmployeeInfo(employeeId);
      } else {
          Toast.makeText(this, "Không có mã nhân viên.", Toast.LENGTH_SHORT).show();
       }
    }

    private void getEmployeeInfo(String employeeId) {
        db.collection("Employee").document(employeeId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Employee employee = documentSnapshot.toObject(Employee.class);
                        if (employee != null) {
                            displayEmployeeInfo(employee);
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy nhân viên.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi lấy thông tin nhân viên!", Toast.LENGTH_SHORT).show();
                    Log.e("MyInfoActivity", "Error getting document", e);
                });
    }

    private void displayEmployeeInfo(Employee employee) {
        tvEmployeeId.setText("Mã Nhân Viên: " + employee.getEmployeeId());
        tvUsername.setText("Tên đăng nhập: " + employee.getUsername());
        tvFullName.setText("Tên nhân viên: " + employee.getFullName());
        tvPass.setText("PassWord: " + employee.getPassword());
        tvRole.setText("Vai trò: " + employee.getRole());
        tvGender.setText("Giới tính: " + employee.getGender());
        tvDob.setText("Ngày sinh: " + employee.getDateOfBirth());
        tvCmnd.setText("CMND: " + employee.getIdCard());
    }
}
