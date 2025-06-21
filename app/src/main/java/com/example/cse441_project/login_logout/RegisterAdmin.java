package com.example.cse441_project.login_logout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cse441_project.Model.Employee;
import com.example.cse441_project.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class RegisterAdmin extends AppCompatActivity {

    private EditText maNVEditText, tenDNEditText, phanQuyenEditText, matKhauEditText, tenNVEditText, gioiTinhEditText, ngaySinhEditText, cmndEditText;
    private Button registerButton;
    private DatabaseReference mDatabase;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_admin_account);


        tenDNEditText = findViewById(R.id.tenDNEditText);

        matKhauEditText = findViewById(R.id.matKhauEditText);
        tenNVEditText = findViewById(R.id.tenNVEditText);
        gioiTinhEditText = findViewById(R.id.gioiTinhEditText);
        ngaySinhEditText = findViewById(R.id.ngaySinhEditText);
        cmndEditText = findViewById(R.id.cmndEditText);
        registerButton = findViewById(R.id.registerButton);

        mDatabase = FirebaseDatabase.getInstance().getReference("Employee");

        registerButton.setOnClickListener(v -> {
            String maNV = maNVEditText.getText().toString().trim();
            String tenDN = tenDNEditText.getText().toString().trim();
            String phanQuyen = phanQuyenEditText.getText().toString().trim();
            String matKhau = matKhauEditText.getText().toString().trim();
            String tenNV = tenNVEditText.getText().toString().trim();
            String gioiTinh = gioiTinhEditText.getText().toString().trim();
            String ngaySinh = ngaySinhEditText.getText().toString().trim();
            String cmnd = cmndEditText.getText().toString().trim();

            if (validateInputs(maNV, tenDN, matKhau, tenNV, gioiTinh, ngaySinh, cmnd)) {
                registerNhanVien(maNV, tenDN, phanQuyen, matKhau, tenNV, gioiTinh, ngaySinh, cmnd);
            } else {
                Toast.makeText(RegisterAdmin.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs(String maNV, String tenDN, String matKhau, String tenNV, String gioiTinh, String ngaySinh, String cmnd) {
        return !maNV.isEmpty() && !tenDN.isEmpty() && !matKhau.isEmpty() && !tenNV.isEmpty() && !gioiTinh.isEmpty() && !ngaySinh.isEmpty() && !cmnd.isEmpty();
    }

    private void registerNhanVien(String maNV, String tenDN, String phanQuyen, String matKhau, String tenNV, String gioiTinh, String ngaySinh, String cmnd) {
        Employee nhanVien = new Employee("1", tenDN, "QuanLy", matKhau, tenNV, gioiTinh, ngaySinh, cmnd);
        mDatabase.child(maNV).setValue(nhanVien).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterAdmin.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                // Chuyển sang màn hình khác hoặc đăng nhập
                startActivity(new Intent(RegisterAdmin.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(RegisterAdmin.this, "Đăng ký thất bại! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
