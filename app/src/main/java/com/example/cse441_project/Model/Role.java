package com.example.cse441_project.Model;

public class Role {
    private String roleId;   // MaQuyen
    private String roleName; // TenQuyen

    // Default constructor required for calls to DataSnapshot.getValue(Role.class)
    public Role() {
    }

    // Constructor with parameters
    public Role(String roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    // Getter and setter for roleId (MaQuyen)
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    // Getter and setter for roleName (TenQuyen)
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
