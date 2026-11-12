
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            System.getenv("PAYROLL_DB_URL");

    private static final String USER =
            System.getenv("PAYROLL_DB_USER");

    private static final String PASSWORD =
            System.getenv("PAYROLL_DB_PASSWORD");

    // Temporary diagnostic method
    public static void checkConfiguration() {
        System.out.println("=== DATABASE CONFIGURATION CHECK ===");
        System.out.println("URL set: "
                + (URL != null && !URL.isBlank()));
        System.out.println("Username set: "
                + (USER != null && !USER.isBlank()));
        System.out.println("Password set: "
                + (PASSWORD != null && !PASSWORD.isBlank()));
    }

    public static Connection getConnection() throws SQLException {

        if (URL == null || URL.isBlank()
                || USER == null || USER.isBlank()
                || PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "Database configuration is missing. "
                    + "Set PAYROLL_DB_URL, PAYROLL_DB_USER, "
                    + "and PAYROLL_DB_PASSWORD."
            );
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL JDBC Driver not found.", e
            );
        }
    }
}
