package com.example.cse441_project.Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Employee implements Serializable {

    private String employeeId; // maNV
    private String username;    // tenDN
    private String role;        // phanQuyen
    private String password;    // matKhau
    private String fullName;    // tenNV
    private String gender;      // gioiTinh
    private String dateOfBirth; // ngaySinh
    private String idCard;      // cmnd

    // Default constructor required for Firebase
    public Employee() {

    }

    // Full constructor
    public Employee(String employeeId, String username, String role, String password, String fullName, String gender, String dateOfBirth, String idCard) {
        this.employeeId = employeeId;
        this.username = username;
        this.role = role;
        this.password = password;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.idCard = idCard;
    }
    public Map<String, Object> toMap() {
        Map<String, Object> employeeMap = new HashMap<>();
        employeeMap.put("employeeId", employeeId);
        employeeMap.put("username", username);
        employeeMap.put("role", role);
        employeeMap.put("password", password);
        employeeMap.put("fullName", fullName);
        employeeMap.put("gender", gender);
        employeeMap.put("dateOfBirth", dateOfBirth);
        employeeMap.put("idCard", idCard);
        return employeeMap;
    }

    // Getters and setters for each property
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
}
