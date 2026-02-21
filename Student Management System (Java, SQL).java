import java.sql.*;
import java.util.Scanner;

public class StudentManagementSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Student Management System ---");
            System.out.println("1. Add Student");
            System.out.println("2. View Student");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = getValidInput(scanner);
            switch (choice) {
                case 1 -> addStudent(scanner);
                case 2 -> viewStudent(scanner);
                case 3 -> updateStudent(scanner);
                case 4 -> deleteStudent(scanner);
                case 5 -> running = false;
                default -> System.out.println("Invalid option!");
            }
        }
        scanner.close();
    }

    private static void addStudent(Scanner scanner) {
        try {
            System.out.print("Enter Student ID: ");
            int id = getValidInput(scanner);
            
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
            
            System.out.print("Enter GPA: ");
            double gpa = Double.parseDouble(scanner.nextLine());
            if (gpa < 0 || gpa > 4.0) throw new IllegalArgumentException("GPA must be between 0 and 4.0");

            String query = "INSERT INTO students (id, name, gpa) VALUES (?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setString(2, name);
                stmt.setDouble(3, gpa);
                stmt.executeUpdate();
                System.out.println("Student added successfully!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void viewStudent(Scanner scanner) {
        try {
            System.out.print("Enter Student ID: ");
            int id = getValidInput(scanner);

            String query = "SELECT * FROM students WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("GPA: " + rs.getDouble("gpa"));
                } else {
                    System.out.println("Student not found!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void updateStudent(Scanner scanner) {
        try {
            System.out.print("Enter Student ID: ");
            int id = getValidInput(scanner);
            
            System.out.print("Enter new name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
            
            System.out.print("Enter new GPA: ");
            double gpa = Double.parseDouble(scanner.nextLine());
            if (gpa < 0 || gpa > 4.0) throw new IllegalArgumentException("GPA must be between 0 and 4.0");

            String query = "UPDATE students SET name = ?, gpa = ? WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setDouble(2, gpa);
                stmt.setInt(3, id);
                int rows = stmt.executeUpdate();
                System.out.println(rows > 0 ? "Student updated successfully!" : "Student not found!");
            }
        } catch (IllegalArgumentException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void deleteStudent(Scanner scanner) {
        try {
            System.out.print("Enter Student ID: ");
            int id = getValidInput(scanner);

            String query = "DELETE FROM students WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                int rows = stmt.executeUpdate();
                System.out.println(rows > 0 ? "Student deleted successfully!" : "Student not found!");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static int getValidInput(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.");
            return -1;
        }
    }
}


