package com.example.cse441_project.Model;

public class Order {
    private String orderId;          // MaGM
    private String employeeId;       // MaNV
    private String orderDate;        // NgayGoi
    private String orderStatus;       // TinhTrangGM
    private String paymentStatus;    // TinhTrangTT
    private String tableId;          // MaBan
    private double totalAmount;      // TongTien

    // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    public Order() {
    }

    // Constructor with parameters
    public Order(String orderId, String employeeId, String orderDate, String orderStatus, String paymentStatus, String tableId, double totalAmount) {
        this.orderId = orderId;
        this.employeeId = employeeId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.tableId = tableId;
        this.totalAmount = totalAmount;
    }

    // Getter and setter for orderId (MaGM)
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    // Getter and setter for employeeId (MaNV)
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    // Getter and setter for orderDate (NgayGoi)
    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    // Getter and setter for orderStatus (TinhTrangGM)
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    // Getter and setter for paymentStatus (TinhTrangTT)
    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    // Getter and setter for tableId (MaBan)
    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    // Getter and setter for totalAmount (TongTien)
    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}

