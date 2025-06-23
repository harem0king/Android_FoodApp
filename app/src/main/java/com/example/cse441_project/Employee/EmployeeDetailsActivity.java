package com.example.cse441_project.Employee;

import android.app.AlertDialog;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EmployeeDetailsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    EditText etEmployeeId;
    EditText etUsername ;
    EditText etPassword;
    Spinner spRole ;
    EditText etFullName;
    Spinner spGender;
    EditText etDob ;
    EditText etCmnd ;
    Employee employee;
    private Button updateBtn,deleteBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail);
        Intent intent = getIntent();
        employee = (Employee) intent.getSerializableExtra("employee");
        db = FirebaseFirestore.getInstance();

         etEmployeeId = findViewById(R.id.employee_id);
         etUsername = findViewById(R.id.et_username);
         etPassword = findViewById(R.id.et_password);
         spRole = findViewById(R.id.sp_role);
         etFullName = findViewById(R.id.et_employee_name);
         spGender = findViewById(R.id.et_gender);
         etDob = findViewById(R.id.et_dob);
         etCmnd = findViewById(R.id.et_cmnd);
         updateBtn = findViewById(R.id.update_btn);
         deleteBtn = findViewById(R.id.finish_btn);


        etDob.setOnClickListener(v -> showDatePickerDialog());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(adapter1);
        getInfo();

        deleteBtn.setOnClickListener(v->{
            new AlertDialog.Builder(this)
                    .setTitle("Xóa món ăn")
                    .setMessage("Bạn có chắc chắn muốn xóa món ăn này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {

                        deleteEmployee(etEmployeeId.getText().toString());
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        });
        updateBtn.setOnClickListener(v->{
            if(validateFields()) {
                updateEmployeeToFirestore();
            }
        });

    }
    private void deleteEmployee(String employeeID) {
        db.collection("Employee").document(employeeID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Xoá tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EmployeeDetailsActivity.this, ActivityFormEmployee.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi xóa tài khoản!", Toast.LENGTH_SHORT).show();
                });
    }
    private void getInfo()
    {
        if (employee != null) {
            etEmployeeId.setText(employee.getEmployeeId());
            etUsername.setText(employee.getUsername());
            etPassword.setText(employee.getPassword());
            etFullName.setText(employee.getFullName());
            etDob.setText(employee.getDateOfBirth());
            etCmnd.setText(employee.getIdCard());

            setSpinnerSelection(spRole, employee.getRole());
            setSpinnerSelection(spGender, employee.getGender());
        }
    }
    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

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
    private void updateEmployeeToFirestore() {

        String employeeID = etEmployeeId.getText().toString();
        String username = etUsername.getText().toString();
        String role = spRole.getSelectedItem().toString();
        String password = etPassword.getText().toString();
        String fullName = etFullName.getText().toString();
        String gender = spGender.getSelectedItem().toString();
        String dateOfBirth = etDob.getText().toString();
        String idCard = etCmnd.getText().toString();

        Employee employee = new Employee(employeeID, username, role, password, fullName, gender, dateOfBirth, idCard);

        db.collection("Employee")
                .document(employeeID)
                .set(employee)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(EmployeeDetailsActivity.this,ActivityFormEmployee.class);
                    startActivity(intent);
                    Toast.makeText(this, "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Thêm nhân viên thất bại", Toast.LENGTH_SHORT).show();
                    Log.e("ActivityFormEmployee", "Error adding document", e);
                });
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
}
