import java.sql.*;
import java.util.Scanner;

public class StudentRecordSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    private Scanner scanner;

    public StudentRecordSystem() {
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        StudentRecordSystem system = new StudentRecordSystem();
        system.run();
    }

    private void run() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            int choice;
            do {
                displayMenu();
                choice = getValidInput("Enter your choice: ", 1, 5);
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> viewStudent();
                    case 3 -> updateStudent();
                    case 4 -> deleteStudent();
                    case 5 -> System.out.println("Exiting...");
                }
            } while (choice != 5);
        } catch (ClassNotFoundException e) {
            System.out.println("Database driver not found: " + e.getMessage());
        }
        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n=== Student Record Management ===");
        System.out.println("1. Add Student");
        System.out.println("2. View Student");
        System.out.println("3. Update Student");
        System.out.println("4. Delete Student");
        System.out.println("5. Exit");
    }

    private void addStudent() {
        try {
            System.out.print("Enter Student ID: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            
            System.out.print("Enter GPA: ");
            double gpa = scanner.nextDouble();

            if (name.trim().isEmpty() || !email.contains("@")) {
                System.out.println("Invalid input. Please check your entries.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "INSERT INTO students (id, name, email, gpa) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                stmt.setString(2, name);
                stmt.setString(3, email);
                stmt.setDouble(4, gpa);
                stmt.executeUpdate();
                System.out.println("Student added successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void viewStudent() {
        try {
            System.out.print("Enter Student ID: ");
            int id = getValidInput("", 1, Integer.MAX_VALUE);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT * FROM students WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    System.out.println("\nID: " + rs.getInt("id"));
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("Email: " + rs.getString("email"));
                    System.out.println("GPA: " + rs.getDouble("gpa"));
                } else {
                    System.out.println("Student not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void updateStudent() {
        try {
            System.out.print("Enter Student ID to update: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            
            System.out.print("Enter new GPA: ");
            double gpa = scanner.nextDouble();

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE students SET gpa = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setDouble(1, gpa);
                stmt.setInt(2, id);
                int rows = stmt.executeUpdate();
                System.out.println(rows > 0 ? "Student updated!" : "Student not found.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            System.out.print("Enter Student ID to delete: ");
            int id = scanner.nextInt();

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "DELETE FROM students WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                int rows = stmt.executeUpdate();
                System.out.println(rows > 0 ? "Student deleted!" : "Student not found.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private int getValidInput(String prompt, int min, int max) {
        int input;
        while (true) {
            try {
                System.out.print(prompt);
                input = scanner.nextInt();
                if (input >= min && input <= max) return input;
                System.out.println("Invalid choice. Try again.");
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }
}