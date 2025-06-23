package com.example.cse441_project.Login_Logout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse441_project.FoodItem.HomeActivity;
import com.example.cse441_project.Model.Employee;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUser,edtPass;
    private  String UserName,PassWorld;
    private  String userDb,passDb;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        // Khởi tạo nút đăng nhập và thiết lập sự kiện click
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy username và password từ EditText
                UserName = edtUser.getText().toString().trim();
                PassWorld = edtPass.getText().toString().trim();
                if (UserName.isEmpty() || PassWorld.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Username và Password không được để trống!", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkLoginCredentials();
            }
        });

    }
    private void checkLoginCredentials() {
        // Tạo tham chiếu tới Cloud Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy tất cả người dùng từ collection "employees"
        db.collection("Employee")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isValidUser = false;
                        // Duyệt qua các tài liệu trong collection
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Employee employee = document.toObject(Employee.class);
                            if (employee != null) {
                                String usernameFromDb = employee.getUsername(); // Lấy username từ đối tượng Employee
                                String passwordFromDb = employee.getPassword(); // Lấy password từ đối tượng Employee
                                String idFromDb = employee.getEmployeeId();
                                if (UserName.equals(usernameFromDb) && PassWorld.equals(passwordFromDb)) {
                                    isValidUser = true;
                                    // Lưu thông tin đăng nhập vào SharedPreferences
                                    saveLoginCredentials(UserName, PassWorld,idFromDb);
                                    break;
                                }
                            }
                        }
                        if (isValidUser) {
                            // Chuyển đến activity tiếp theo hoặc thực hiện hành động đăng nhập thành công
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            // Thông báo đăng nhập thất bại
                            Toast.makeText(LoginActivity.this, "Thông tin đăng nhập không đúng!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Xử lý lỗi khi truy vấn không thành công
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi khi lấy dữ liệu từ Firestore thất bại
                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Phương thức để lưu thông tin đăng nhập
    private void saveLoginCredentials(String username, String password,String id) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("employeeId", id);
        editor.apply();
    }




}
