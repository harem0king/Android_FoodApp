package com.example.cse441_project.Order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.cse441_project.Adapter.TableAdapter;
import com.example.cse441_project.FoodItem.HomeActivity;
import com.example.cse441_project.Model.Table;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TableListActivity extends AppCompatActivity {
    private GridView tableGridView;
    private List<Table> tableList;
    private FirebaseFirestore db;
    private TableAdapter adapter;
    private TextView statusMessageTextView;
    private ImageView backButton;
    private Button addTableButton, orderButton, viewInvoiceButton;
    private LinearLayout actionButtonsLayout;

    private String selectedTableId;
    private String selectedTableStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);

        tableGridView = findViewById(R.id.tableGridView);
        statusMessageTextView = findViewById(R.id.statusMessageTextView);
        backButton = findViewById(R.id.backButton);
        addTableButton = findViewById(R.id.addTableButton);
        actionButtonsLayout = findViewById(R.id.actionButtonsLayout);
        orderButton = findViewById(R.id.orderButton);
        viewInvoiceButton = findViewById(R.id.viewInvoiceButton);

        tableList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        adapter = new TableAdapter(this, tableList);
        tableGridView.setAdapter(adapter);

        loadTables();

        tableGridView.setOnItemClickListener((parent, view, position, id) -> {
            Table selectedTable = tableList.get(position);
            selectedTableId = selectedTable.getTableId();
            selectedTableStatus = selectedTable.getStatus();
            updateStatusMessage(selectedTableStatus);

            // Show the action buttons based on table status
            if ("available".equals(selectedTableStatus) || "reserved".equals(selectedTableStatus)) {
                actionButtonsLayout.setVisibility(View.VISIBLE);
                viewInvoiceButton.setVisibility(View.GONE);
                orderButton.setVisibility(View.VISIBLE);
            } else if ("busy".equals(selectedTableStatus)) {
                actionButtonsLayout.setVisibility(View.VISIBLE);
                viewInvoiceButton.setVisibility(View.VISIBLE);
                orderButton.setVisibility(View.VISIBLE);
            } else {
                actionButtonsLayout.setVisibility(View.GONE);
            }
        });

        // Back button
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(TableListActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // Add Table button
        addTableButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            AddTableFragment addTableFragment = new AddTableFragment();
            addTableFragment.show(fragmentManager, "AddTableFragment");
        });

        // Order button: Cập nhật trạng thái bàn => chuyển sang OrderActivity
        orderButton.setOnClickListener(v -> {
            if (selectedTableId != null && ("available".equals(selectedTableStatus) || "reserved".equals(selectedTableStatus))) {
                db.collection("Tables")
                        .document(selectedTableId)
                        .update("status", "busy")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(TableListActivity.this, "Bàn được cập nhật sang trạng thái 'busy'", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TableListActivity.this, OrderActivity.class);
                            intent.putExtra("tableId", selectedTableId);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(TableListActivity.this, "Không thể cập nhật trạng thái bàn!", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(TableListActivity.this, "Vui lòng chọn bàn trống hoặc đã đặt.", Toast.LENGTH_SHORT).show();
            }
        });

        // View Invoice button
        viewInvoiceButton.setOnClickListener(v -> {
            if (selectedTableId != null && "busy".equals(selectedTableStatus)) {
                Intent intent = new Intent(TableListActivity.this, InvoiceActivity.class);
                intent.putExtra("tableId", selectedTableId);
                startActivity(intent);
            } else {
                Toast.makeText(TableListActivity.this, "Chọn bàn đang có khách để xem hóa đơn.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void loadTables() {
        db.collection("Tables")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tableList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String tableId = document.getString("tableId");
                            String tableName = document.getString("tableName");
                            String status = document.getString("status");
                            tableList.add(new Table(tableId, tableName, status));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(TableListActivity.this, "Không thể tải dữ liệu từ Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateStatusMessage(String status) {
        switch (status) {
            case "available":
                statusMessageTextView.setText("Bàn trống");
                break;
            case "busy":
                statusMessageTextView.setText("Bàn đang có khách");
                break;
            case "reserved":
                statusMessageTextView.setText("Bàn đã đặt trước");
                break;
            default:
                statusMessageTextView.setText("Trạng thái không xác định");
                break;
        }
    }
}

