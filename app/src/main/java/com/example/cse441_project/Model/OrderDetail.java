package com.example.cse441_project.Model;

import java.io.Serializable;

public class OrderDetail implements Serializable {
    private String orderId;        // MaGM
    private String itemFoodID;     // MaThucDon
    private int quantity;          // SoLuong

    public OrderDetail() {
    }



    public OrderDetail(String orderId, String itemFoodID, int quantity) {
        this.orderId = orderId;
        this.itemFoodID = itemFoodID;
        this.quantity = quantity;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public String getItemFoodID() {
        return itemFoodID;
    }

    public void setItemFoodID(String itemFoodID) {
        this.itemFoodID = itemFoodID;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

