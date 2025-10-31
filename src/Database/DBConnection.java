package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mahirushop?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Mặc định trong XAMPP
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    // Constructor mặc định
    public DBConnection() {
    }

    // Kết nối đến MySQL
    public Connection getConnect() {
        try {
            // Tải driver (không bắt buộc với JDBC 4.0+, nhưng giữ để tương thích)
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Kết nối MySQL thành công!");
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Lỗi: Không tìm thấy driver MySQL JDBC: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi kết nối MySQL: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Lấy Statement để thực thi câu lệnh SQL
    public Statement getStatement() throws SQLException {
        if (statement == null || statement.isClosed()) {
            connection = getConnect();
            if (connection != null) {
                statement = connection.createStatement();
            } else {
                throw new SQLException("❌ Không thể tạo Statement vì kết nối thất bại.");
            }
        }
        return statement;
    }

    // Thực thi câu truy vấn SELECT
    public ResultSet executeQuery(String query) throws SQLException {
        try {
            resultSet = getStatement().executeQuery(query);
            return resultSet;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi truy vấn: " + e.getMessage() + " | SQL: " + query);
            throw e;
        }
    }

    // Thực thi câu lệnh INSERT, UPDATE, DELETE
    public int executeUpdate(String query) throws SQLException {
        try {
            int result = getStatement().executeUpdate(query);
            System.out.println("✅ Thực thi câu lệnh thành công: " + query);
            return result;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật: " + e.getMessage() + " | SQL: " + query);
            throw e;
        }
    }

    // Lấy tất cả dữ liệu từ bảng (ví dụ: lấy cột username từ bảng users)
    public List<String> layTatCa() {
        List<String> result = new ArrayList<>();
        try {
            resultSet = executeQuery("SELECT username FROM users"); // Thay 'users' và 'username' theo bảng/cột thực tế
            while (resultSet.next()) {
                result.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi trong layTatCa: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    // Đóng kết nối và giải phóng tài nguyên
    public void closeConnect() {
        try {
            if (resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
                resultSet = null;
            }
            if (statement != null && !statement.isClosed()) {
                statement.close();
                statement = null;
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
            System.out.println("✅ Đã đóng kết nối và tài nguyên.");
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đóng kết nối: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức main để kiểm tra
    public static void main(String[] args) {
        DBConnection db = new DBConnection();
        try {
            // Kiểm tra kết nối
            Connection conn = db.getConnect();
            if (conn != null) {
                System.out.println("✅ Kết nối hoạt động bình thường.");
            }

            // Kiểm tra layTatCa
            List<String> data = db.layTatCa();
            if (data.isEmpty()) {
                System.out.println("⚠ Không có dữ liệu hoặc có lỗi xảy ra!");
            } else {
                System.out.println("Dữ liệu từ bảng users:");
                for (String item : data) {
                    System.out.println(item);
                }
            }
        } finally {
            db.closeConnect();
        }
    }
}