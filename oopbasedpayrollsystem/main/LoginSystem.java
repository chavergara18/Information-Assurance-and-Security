/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oopbasedpayrollsystem.main;

/**
 *
 * @author Cha
 */
import oopbasedpayrollsystem.gui.LoginGUI;
import oopbasedpayrollsystem.model.User;
import oopbasedpayrollsystem.model.AdminUser;
import oopbasedpayrollsystem.model.EmployeeUser;
import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class LoginSystem {

    public static User login(String username, String password) {
        DatabaseConnection.checkConfiguration();
        // Validate input.
        if (username == null || username.isBlank()
                || password == null || password.isEmpty()) {
            return null;
        }

        String cleanUsername = username.trim();

        // Try administrator authentication first.
        if (validateAdminLogin(cleanUsername, password)) {
            return new AdminUser(
                0,
                "Admin",
                "User",
                LocalDate.of(1990, 1, 1),
                "Admin Address",
                "09123456789",
                "SSS001",
                "PH001",
                "TIN001",
                "PAGIBIG001",
                "Active",
                "Admin",
                cleanUsername,
                ""
            );
        }

        // Try employee authentication.
        String query =
            "SELECT * FROM employees WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, cleanUsername);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                String status = rs.getString("status");

                // Reject inactive accounts.
                if (status == null
                        || !"Active".equalsIgnoreCase(status.trim())) {
                    return null;
                }

                java.sql.Date sqlBirthday = rs.getDate("birthday");

                LocalDate birthday = sqlBirthday == null
                    ? null
                    : sqlBirthday.toLocalDate();

                return new EmployeeUser(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    birthday,
                    rs.getString("address"),
                    rs.getString("phone_number"),
                    rs.getString("sss_number"),
                    rs.getString("philhealth_number"),
                    rs.getString("tin_number"),
                    rs.getString("pagibig_number"),
                    status,
                    rs.getString("position"),
                    rs.getString("email"),
                    ""
                );
            }

        } catch (SQLException e) {
            System.err.println(
                "=== EMPLOYEE LOGIN DATABASE ERROR ==="
            );
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean validateAdminLogin(
            String username, String password) {

        if (username == null || username.isBlank()
                || password == null || password.isEmpty()) {
            return false;
        }

        String query =
            "SELECT 1 FROM admin_credentials "
            + "WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username.trim());
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println(
                "=== ADMIN LOGIN DATABASE ERROR ==="
            );
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            LoginGUI loginScreen = new LoginGUI();
            loginScreen.setVisible(true);
        });
    }
}











