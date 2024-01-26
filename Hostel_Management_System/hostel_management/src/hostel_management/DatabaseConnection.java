

package hostel_management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String DB_USER = "System";
    private static final String DB_PASSWORD = "Oracle_1";
    private static final String SELECT_USER_QUERY = "SELECT * FROM employeesP WHERE email = ? AND password = ?";
    private static final String SELECT_DESIGNATION_QUERY = "SELECT designation FROM employeesP WHERE email = ?";
    private Connection connection;

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return connection;
    }

    public boolean authenticateUser(String email, String password) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_QUERY)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return false;
    }

    public String getDesignation(String email) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_DESIGNATION_QUERY)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("designation");
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return null;
    }

    private void handleSQLException(SQLException e) {
        // Log or handle the exception appropriately
        e.printStackTrace();
    }
}
