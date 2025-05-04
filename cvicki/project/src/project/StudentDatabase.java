package project;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;

class StudentDatabase {
    private List<Student> students = new ArrayList<>();
    private Connection connection;
    private int telecomNextId = 10000;
    private int cyberNextId = 20000;
    private String dbName;

    public StudentDatabase() {
        this("university.db");
    }
    
    public StudentDatabase(String dbName) {
        this.dbName = dbName;
        initializeDatabase();
    }
    
    public String getDbName() {
        return dbName;
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(
            	    "jdbc:sqlite:" + dbName + 
            	    "?busy_timeout=5000" +
            	    "&journal_mode=WAL" +
            	    "&synchronous=NORMAL"
            	);
            createTables();
            loadData();
        } catch (Exception e) {
            System.out.println("Database connection error. Using in-memory storage.");
            e.printStackTrace();
        }
    }
    
    public Connection getConnection() {
        return this.connection;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = connection.getMetaData().getTables(null, null, "students", null);
            if (!rs.next()) {
                stmt.execute("CREATE TABLE students (" +
                        "ID INT PRIMARY KEY, " +
                        "StudentGroup VARCHAR(20), " +
                        "FirstName VARCHAR(50), " +
                        "LastName VARCHAR(50), " +
                        "BirthYear INT, " +
                        "Grades TEXT, " +
                        "AverageGrade REAL)");
            } else {
                try {
                    stmt.executeQuery("SELECT AverageGrade FROM students LIMIT 1");
                } catch (SQLException e) {
                    stmt.execute("ALTER TABLE students ADD COLUMN AverageGrade REAL");
                }
            }
        }
    }

    private void loadData() {
        if (connection == null) return;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {
            while (rs.next()) {
                Student student;
                int id = rs.getInt("ID");
                String type = rs.getString("StudentGroup");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                int birthYear = rs.getInt("BirthYear");

                if ("Telecom".equals(type)) {
                    student = new TelecomStudent(id, firstName, lastName, birthYear);
                    if (id >= telecomNextId) telecomNextId = id + 1;
                } else {
                    student = new CyberSecurityStudent(id, firstName, lastName, birthYear);
                    if (id >= cyberNextId) cyberNextId = id + 1;
                }

                String grades = rs.getString("Grades");
                if (grades != null && !grades.isEmpty()) {
                    Arrays.stream(grades.split(",\\s*"))
                          .map(Integer::parseInt)
                          .forEach(student::addGrade);
                }

                students.add(student);
            }
        } catch (SQLException e) {
            System.out.println("Load error");
            e.printStackTrace();
        }
    }

    public void saveData() {
        if (connection == null) return;

        try {
            connection.setAutoCommit(false);

            try (Statement stmt = connection.createStatement();
                 PreparedStatement pstmt = connection.prepareStatement(
                     "INSERT INTO students VALUES (?, ?, ?, ?, ?, ?, ?)")) {

                stmt.execute("DELETE FROM students");

                for (Student student : students) {
                    String type = student instanceof TelecomStudent ? "Telecom" : "CyberSecurity";

                    String grades = student.grades.stream()
                        .map(String::valueOf)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");

                    double average = Math.round(student.getAverageGrade() * 10) / 10.0;

                    pstmt.setInt(1, student.getId());
                    pstmt.setString(2, type);
                    pstmt.setString(3, student.getFirstName());
                    pstmt.setString(4, student.getLastName());
                    pstmt.setInt(5, student.getBirthYear());
                    pstmt.setString(6, grades);
                    pstmt.setDouble(7, average);
                    pstmt.executeUpdate();
                }
                connection.commit();
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
                System.out.println("Save error: " + e.getMessage());
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Auto-commit reset failed");
            }
        }
    }

    public int getNextTelecomId() {
        return telecomNextId; 
    }

    public int getNextCyberId() {
        return cyberNextId; 
    }

    public void addStudent(Student student) {
        if (student instanceof TelecomStudent) {
            student.id = telecomNextId++;
        } else {
            student.id = cyberNextId++;
        }
        students.add(student);
    }

    public Student findStudent(int id) {
        return students.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    public boolean removeStudent(int id) {
        int initialSize = students.size();
        students.removeIf(s -> s.getId() == id);
        return students.size() < initialSize; 
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students); 
    }

    public double getGroupAverage(String groupType) {
        DoubleSummaryStatistics stats = students.stream()
            .filter(s -> groupType.equals("Telecom") 
                ? (s instanceof TelecomStudent) 
                : (s instanceof CyberSecurityStudent))
            .filter(s -> !s.grades.isEmpty()) 
            .mapToDouble(Student::getAverageGrade)
            .summaryStatistics();
        
        return stats.getCount() > 0 ? stats.getAverage() : 0.0;
    }

    public int getGroupCount(String groupType) {
        return (int) students.stream()
                .filter(s -> (groupType.equals("Telecom") ? (s instanceof TelecomStudent) : (s instanceof CyberSecurityStudent)))
                .count();
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
                connection = null; 
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}