package com.example.cse441_project.Employee;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.Model.Employee;
import com.example.cse441_project.R;

import java.util.List;

public class EmployeeFormAdapter extends RecyclerView.Adapter<EmployeeFormAdapter.EmployeeViewHolder> {
    private List<Employee> employeeList;

    // Constructor nhận danh sách nhân viên
    public EmployeeFormAdapter(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.tvFullName.setText(employee.getFullName());
        holder.tvUsername.setText(employee.getUsername());
        holder.tvRole.setText(employee.getRole());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EmployeeDetailsActivity.class);

            intent.putExtra("employee", employee);

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return employeeList != null ? employeeList.size() : 0;
    }

    // ViewHolder cho Employee
    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvUsername, tvRole;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvRole = itemView.findViewById(R.id.tvRole);
        }
    }
}
