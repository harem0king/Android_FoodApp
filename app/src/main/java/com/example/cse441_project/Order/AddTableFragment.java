package com.example.cse441_project.Order;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cse441_project.Model.Table;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddTableFragment extends DialogFragment {
    private EditText edtTableId, edtTableName;
    private Button btnAddTable;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_table, container, false);

        edtTableId = view.findViewById(R.id.edtTableId);
        edtTableName = view.findViewById(R.id.edtTableName);
        btnAddTable = view.findViewById(R.id.btnAddTable);
        db = FirebaseFirestore.getInstance();

        btnAddTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableId = edtTableId.getText().toString().trim();
                String tableName = edtTableName.getText().toString().trim();

                if (TextUtils.isEmpty(tableId) || TextUtils.isEmpty(tableName)) {
                    Toast.makeText(getActivity(), "ID và tên bàn không được để trống!", Toast.LENGTH_SHORT).show();
                } else {
                    addNewTable(tableId, tableName);
                }
            }
        });

        return view;
    }

    // Thêm bàn mới vào Firestore
    private void addNewTable(String tableId, String tableName) {
        Table newTable = new Table(tableId, tableName, "available");
        db.collection("Tables").document(tableId).set(newTable)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Đã thêm bàn mới!", Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof TableListActivity) {
                        TableListActivity tableListActivity = (TableListActivity) getActivity();
                        tableListActivity.loadTables(); // Tải lại danh sách bàn
                    }

                    dismiss(); // Đóng fragment sau khi thêm thành công
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Lỗi khi thêm bàn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
