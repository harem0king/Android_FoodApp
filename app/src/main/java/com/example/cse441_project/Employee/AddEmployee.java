package com.example.cse441_project.Employee;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse441_project.Model.Employee;
import com.example.cse441_project.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEmployee extends AppCompatActivity {
    private EditText  etUsername, etPassword, etFullName, etDob, etCmnd;
    private Button btnAddEmployee;
    private Spinner sp_role,sp_gender;
    private FirebaseFirestore db;
    private String lastID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_employee);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        etUsername = findViewById(R.id.et_username);
        sp_role = findViewById(R.id.sp_role);
        etPassword = findViewById(R.id.et_password);
        etFullName = findViewById(R.id.et_employee_name);
        sp_gender = findViewById(R.id.et_gender);
        etDob = findViewById(R.id.et_dob);
        etCmnd = findViewById(R.id.et_cmnd);
        btnAddEmployee = findViewById(R.id.btn_add_employee);
        getLastID();
        etDob.setOnClickListener(v -> showDatePickerDialog());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_role.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_gender.setAdapter(adapter1);
        // Xử lý sự kiện khi nhấn nút thêm nhân viên
        btnAddEmployee.setOnClickListener(view -> {
            if (validateFields()) {
                addEmployeeToFirestore();
            }
        });
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(etUsername.getText().toString())) {
            etUsername.setError("Vui lòng nhập tên đăng nhập");
            return false;
        }
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }

        if (TextUtils.isEmpty(etFullName.getText().toString())) {
            etFullName.setError("Vui lòng nhập tên nhân viên");
            return false;
        }
        if (TextUtils.isEmpty(etDob.getText().toString())) {
            etDob.setError("Vui lòng nhập ngày sinh");
            return false;
        }
        if (TextUtils.isEmpty(etCmnd.getText().toString())) {
            etCmnd.setError("Vui lòng nhập CMND");
            return false;
        }

        return true;
    }

    private void addEmployeeToFirestore() {
        String username = etUsername.getText().toString();
        String role = sp_role.getSelectedItem().toString();
        String password = etPassword.getText().toString();
        String fullName = etFullName.getText().toString();
        String gender = sp_gender.getSelectedItem().toString();
        String dateOfBirth = etDob.getText().toString();
        String idCard = etCmnd.getText().toString();
        // Tạo đối tượng Employee
        Employee employee = new Employee(lastID, username, role, password, fullName, gender, dateOfBirth, idCard);

        // Thêm vào Firestore
        db.collection("Employee")
                .document(lastID)
                .set(employee)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(AddEmployee.this,ActivityFormEmployee.class);
                    startActivity(intent);
                    Toast.makeText(this, "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Thêm nhân viên thất bại", Toast.LENGTH_SHORT).show();
                    Log.e("ActivityFormEmployee", "Error adding document", e);
                });
    }

    private void clearFields() {
        etUsername.setText("");
        etPassword.setText("");
        etFullName.setText("");

        etDob.setText("");
        etCmnd.setText("");
    }
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            calendar.set(year1, month1, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etDob.setText(dateFormat.format(calendar.getTime()));
        }, year, month, day);

        datePickerDialog.show();
    }
    private void getLastID() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Truy vấn Firestore, sắp xếp theo trường "itemFoodID" giảm dần và lấy phần tử đầu tiên (lớn nhất)
        db.collection("Employee").orderBy("employeeId", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Lấy tài liệu đầu tiên
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        // Lấy giá trị của trường "itemFoodID" từ tài liệu
                        String lastItemFoodId = document.getString("employeeId");

                        try {
                            // Chuyển đổi ID thành số nguyên
                            int id = Integer.parseInt(lastItemFoodId);
                            id += 1;  // Tăng giá trị ID lên 1
                            lastID = String.format("%03d", id); // Định dạng ID với 3 chữ số (có thể điều chỉnh tùy theo nhu cầu)

                        } catch (NumberFormatException e) {
                            Log.e("Firestore", "Error parsing last itemFoodId: " + lastItemFoodId, e);
                            lastID = "001";  // Gán giá trị mặc định nếu có lỗi
                        }
                    } else {
                        lastID = "001";  // Gán giá trị mặc định nếu không có tài liệu nào
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error retrieving last itemFoodId", e));
    }
}
