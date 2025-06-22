package com.example.cse441_project.Employee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.Model.Employee;
import com.example.cse441_project.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ActivityFormEmployee extends AppCompatActivity {
    private Button addEmployee;
    private RecyclerView recyclerView;
    private EmployeeFormAdapter employeeAdapter;
    private List<Employee> employeeList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_employee);
        addEmployee = findViewById(R.id.add_btn);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        employeeList = new ArrayList<>();
        employeeAdapter = new EmployeeFormAdapter(employeeList);
        recyclerView.setAdapter(employeeAdapter);
        addEmployee.setOnClickListener(v ->{
           Intent intent = new  Intent(ActivityFormEmployee.this,AddEmployee.class);
            startActivity(intent);
        });
        loadEmployeesFromFirestore();
    }

    private void loadEmployeesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Employee")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        employeeList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Employee employee = document.toObject(Employee.class);
                            employeeList.add(employee);
                        }
                        employeeAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("MainActivity", "Error getting documents.", task.getException());
                    }
                });
    }
}
