package project;

import java.util.*;
import java.io.*;

public class UniversitySystem {
    private static StudentDatabase db;
    private static final Scanner scanner = new Scanner(System.in); 

    public static void main(String[] args) {
        String dbName = selectDatabase();
        db = new StudentDatabase(dbName);  
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            db.saveData();
            db.close();
            System.out.println("Data saved to " + db.getDbName());
            System.out.println("You can open your DB by using this website: https://inloop.github.io/sqlite-viewer/");
        }));
                
        while (true) {
            printMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> addGrade();
                    case 3 -> removeStudent();
                    case 4 -> findStudent();
                    case 5 -> performSkill();
                    case 6 -> printStudents();
                    case 7 -> printGroupAverages();
                    case 8 -> printGroupCounts();
                    case 9 -> saveToFile();
                    case 10 -> loadFromFile();
                    case 0 -> System.exit(0);
                    default -> System.out.println("Invalid choice");
                }
            } catch (InputMismatchException e) {
                System.out.println("Numbers only");
                scanner.nextLine();
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nUniversity System Menu:");
        System.out.println("1. Add new student");
        System.out.println("2. Add grade to student");
        System.out.println("3. Remove student");
        System.out.println("4. Find student by ID");
        System.out.println("5. Perform student's skill");
        System.out.println("6. Print all students");
        System.out.println("7. Print group averages");
        System.out.println("8. Print group counts");
        System.out.println("9. Save student to file");
        System.out.println("10. Load student from file");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private static String selectDatabase() {
        while (true) {
            System.out.println("\nIs there an existing database in the root folder?");
            System.out.println("1. Yes, I want to continue editing it");
            System.out.println("2. No, I want to create a new database");
            System.out.println("3. Cancel and exit program");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter database name (format: example.db): ");
                        String dbName = scanner.nextLine();
                        
                        if (!dbName.endsWith(".db")) {
                            System.out.println("Error: Database name must end with .db");
                            continue;
                        }
                        
                        if (!new File(dbName).exists()) {
                            System.out.println("Error: Database not found. Try again or choose option 2 to create new.");
                            continue;
                        }
                        
                        return dbName;
                    }
                    case 2 -> {
                        while (true) {
                            System.out.print("Enter new database name (format: example.db): ");
                            String newDbName = scanner.nextLine();
                            
                            if (!newDbName.endsWith(".db")) {
                                System.out.println("Error: Database name must end with .db");
                                continue;
                            }
                            
                            return newDbName;
                        }
                    }
                    case 3 -> {
                        System.out.println("Goodbye!");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice. Enter 1, 2 or 3");
                }
            } catch (InputMismatchException e) {
                System.out.println("Numbers only");
                scanner.nextLine();
            }
        }
    }

    private static void addStudent() {
        try {
            int group;
            while (true) {
                System.out.print("Group (1-Telecom, 2-CyberSecurity): ");
                String input = scanner.nextLine();
                if (input.equals("1") || input.equals("2")) {
                    group = Integer.parseInt(input);
                    break;
                }
                System.out.println("Please enter only 1 or 2");
            }

            System.out.print("First name: ");
            String firstName = scanner.nextLine().trim();
            while (firstName.isEmpty()) {
                System.out.println("Name can't be empty!");
                System.out.print("First name: ");
                firstName = scanner.nextLine().trim();
            }

            System.out.print("Last name: ");
            String lastName = scanner.nextLine().trim();
            while (lastName.isEmpty()) {
                System.out.println("Name can't be empty!");
                System.out.print("Last name: ");
                lastName = scanner.nextLine().trim();
            }

            int year;
            while (true) {
                System.out.print("Birth year: ");
                String yearInput = scanner.nextLine();
                try {
                    year = Integer.parseInt(yearInput);
                    if (year >= 1900 && year <= 2025) break;
                    System.out.println("Year must be between 1900-2025");
                } catch (NumberFormatException e) {
                    System.out.println("Numbers only!");
                }
            }

            Student student = group == 1 
                ? new TelecomStudent(0, firstName, lastName, year)
                : new CyberSecurityStudent(0, firstName, lastName, year);
            
            db.addStudent(student);
            System.out.println("Student added with ID: " + String.format("%06d", student.getId()));

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    	private static void addGrade() {
    	    try {
    	        System.out.print("Enter student ID: ");
    	        int id = scanner.nextInt();
    	        scanner.nextLine(); 
    	        
    	        Student student = db.findStudent(id);
    	        if (student == null) {
    	            System.out.println("Student not found!");
    	            return;
    	        }
    	        
    	        System.out.print("Enter grades separated by commas or spaces (1-5): ");
    	        String gradesInput = scanner.nextLine();

    	        String[] gradesArray = gradesInput.split("[,\\s]+");
    	        for (String gradeStr : gradesArray) {
    	            try {
    	                int grade = Integer.parseInt(gradeStr.trim());
    	                if (grade >= 1 && grade <= 5) {
    	                    student.addGrade(grade);
    	                } else {
    	                    System.out.println("Grade " + grade + " ignored (must be 1-5)");
    	                }
    	            } catch (NumberFormatException e) {
    	                System.out.println("'" + gradeStr + "' is not a valid grade - skipped");
    	            }
    	        }
    	        
    	        System.out.println("Grades added successfully");
    	        
    	    } catch (Exception e) {
    	        System.out.println("Error: " + e.getMessage());
    	    }
    	}
    
    	private static void removeStudent() {
    	    try {
    	        System.out.print("Enter student ID (or 0 to cancel): ");
    	        int id = scanner.nextInt();
    	        scanner.nextLine();
    	        
    	        if (id == 0) {
    	            System.out.println("Operation canceled.");
    	            return;
    	        }
    	        
    	        Student student = db.findStudent(id);
    	        if (student == null) {
    	            System.out.println("Student not found!");
    	            return;
    	        }

    	        System.out.printf("Are you sure you want to delete student %s %s (ID: %06d)? (y/n): ",
    	                student.getFirstName(), student.getLastName(), student.getId());
    	        String confirmation = scanner.nextLine().trim().toLowerCase();
    	        
    	        if (confirmation.equals("y")) {
    	            db.removeStudent(id);
    	            System.out.println("Student removed successfully.");
    	        } else {
    	            System.out.println("Operation canceled.");
    	        }
    	        
    	    } catch (InputMismatchException e) {
    	        System.out.println("Invalid input. Please enter a number.");
    	        scanner.nextLine();
    	    }
    	}

    	private static void findStudent() {
    	    try {
    	        System.out.print("Enter student ID (or 0 to cancel): ");
    	        int id = scanner.nextInt();
    	        scanner.nextLine();
    	        
    	        if (id == 0) {
    	            System.out.println("Operation canceled.");
    	            return;
    	        }
    	        
    	        Student s = db.findStudent(id);
    	        if (s != null) {
    	            System.out.printf("ID: %06d, Name: %s %s, Birth: %d, Avg: %.2f, Group: %s\n",
    	                    s.getId(), s.getFirstName(), s.getLastName(),
    	                    s.getBirthYear(), s.getAverageGrade(),
    	                    (s instanceof TelecomStudent) ? "Telecom" : "CyberSecurity");

    	            System.out.println("Grades: " + s.grades.toString());
    	        } else {
    	            System.out.println("Student not found.");
    	        }
    	    } catch (InputMismatchException e) {
    	        System.out.println("Invalid input. Please enter a number.");
    	        scanner.nextLine();
    	    }
    	}

    private static void performSkill() {
        System.out.print("Student ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Student s = db.findStudent(id);
        if (s != null) {
            s.performSkill();
        } else {
            System.out.println("Not found");
        }
    }

    private static void printStudents() {
        System.out.println("\nHow do you want to filter students?");
        System.out.println("1. By alphabet (last name)");
        System.out.println("2. By ID");
        System.out.println("3. By average grade");
        System.out.print("Enter your choice: ");
        
        int filterChoice = getValidChoice(1, 3);
        
        System.out.println("\nHow do you want to sort?");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        System.out.print("Enter your choice: ");
        
        int sortOrder = getValidChoice(1, 2);
        
        List<Student> students = db.getAllStudents();

        Comparator<Student> comparator = switch (filterChoice) {
            case 1 -> Comparator.comparing(Student::getLastName);
            case 2 -> Comparator.comparing(Student::getId);
            case 3 -> Comparator.comparing(Student::getAverageGrade);
            default -> throw new IllegalStateException("Invalid filter choice");
        };

        if (sortOrder == 2) {
            comparator = comparator.reversed();
        }
        
        students.sort(comparator);

        System.out.println("\nTelecom Students:");
        students.stream()
            .filter(s -> s instanceof TelecomStudent)
            .forEach(s -> System.out.printf("%06d: %s %s (%d) - %.2f\n",
                s.getId(), s.getFirstName(), s.getLastName(),
                s.getBirthYear(), s.getAverageGrade()));

        System.out.println("\nCyberSecurity Students:");
        students.stream()
            .filter(s -> s instanceof CyberSecurityStudent)
            .forEach(s -> System.out.printf("%06d: %s %s (%d) - %.2f\n",
                s.getId(), s.getFirstName(), s.getLastName(),
                s.getBirthYear(), s.getAverageGrade()));
    }

    private static int getValidChoice(int min, int max) {
        while (true) {
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.printf("Invalid input. Please enter number between %d and %d: ", min, max);
            } catch (InputMismatchException e) {
                System.out.printf("Numbers only. Please enter number between %d and %d: ", min, max);
                scanner.nextLine();
            }
        }
    }
    
    private static void printGroupAverages() {
        System.out.printf("Telecom average: %.2f\n", db.getGroupAverage("Telecom"));
        System.out.printf("CyberSecurity average: %.2f\n", db.getGroupAverage("CyberSecurity"));
    }

    private static void printGroupCounts() {
        System.out.println("Telecom students: " + db.getGroupCount("Telecom"));
        System.out.println("CyberSecurity students: " + db.getGroupCount("CyberSecurity"));
    }

    private static void saveToFile() {
        System.out.print("Student ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Filename (without extension): ");
        String filename = scanner.nextLine();
        
        Student student = db.findStudent(id);
        if (student == null) {
            System.out.println("Student not found");
            return;
        }

        try (PrintWriter writer = new PrintWriter(filename + ".txt")) {
            writer.println("ID: " + String.format("%06d", student.getId()));
            writer.println("Type: " + (student instanceof TelecomStudent ? "Telecom" : "CyberSecurity"));
            writer.println("FirstName: " + student.getFirstName());
            writer.println("LastName: " + student.getLastName());
            writer.println("BirthYear: " + student.getBirthYear());
            writer.println("Grades: " + student.grades.toString());
            writer.println("Average: " + student.getAverageGrade());
            System.out.println("Student saved to " + filename + ".txt");
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    private static void loadFromFile() {
        File templateFile = new File("new_student.txt");
        
        try {
            if (!templateFile.exists()) {
                createTemplateFile();
            }

            System.out.println("\nInstructions:");
            System.out.println("1. Open 'new_student.txt'");
            System.out.println("2. Fill fields (except ID)");
            System.out.println("3. Save file");
            System.out.print("Press Enter when ready...");
            scanner.nextLine();

            Student student = readStudentFromFile(templateFile);

            int nextId;
            if (student instanceof TelecomStudent) {
                nextId = db.getNextTelecomId();
            } else {
                nextId = db.getNextCyberId();
            }
            student.id = nextId;
            
            db.addStudent(student);
            System.out.println("Student added with ID: " + String.format("%06d", student.getId()));
            
            try {
                resetTemplateFile(templateFile);
            } catch (IOException e) {
                System.out.println("Warning: Could not reset template file. " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            try {
                resetTemplateFile(templateFile);
            } catch (IOException ex) {
                System.out.println("Warning: Could not reset template file. " + ex.getMessage());
            }
        }
    }

    private static void createTemplateFile() throws IOException {
        try (PrintWriter writer = new PrintWriter("new_student.txt")) {
            writer.println("ID will be generated automatically.");
            writer.println("Fill-in example:");
            writer.println("Group (1-Telecom / 2-CyberSecurity): 2");
            writer.println("FirstName: John");
            writer.println("LastName: Pork");
            writer.println("BirthYear: 1992");
            writer.println("-----------------------");
            writer.println("");
            writer.println("Group (1-Telecom / 2-CyberSecurity): ");
            writer.println("FirstName: ");
            writer.println("LastName: ");
            writer.println("BirthYear: ");
        }
    }

    private static Student readStudentFromFile(File file) throws IOException, IllegalArgumentException {
        Map<String, String> data = new HashMap<>();
        boolean examplePassed = false;
        
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();

                if (line.equals("-----------------------")) {
                    examplePassed = true;
                    continue;
                }

                if (examplePassed && line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    data.put(parts[0].trim(), parts[1].trim());
                }
            }
        }

        if (!data.containsKey("Group (1-Telecom / 2-CyberSecurity)") || 
            !data.containsKey("FirstName") || 
            !data.containsKey("LastName") || 
            !data.containsKey("BirthYear")) {
            throw new IOException("All required fields must be present!");
        }

        if (data.get("Group (1-Telecom / 2-CyberSecurity)").isEmpty() ||
            data.get("FirstName").isEmpty() || 
            data.get("LastName").isEmpty() || 
            data.get("BirthYear").isEmpty()) {
            throw new IOException("All fields except ID must be filled!");
        }

        int group;
        try {
            group = Integer.parseInt(data.get("Group (1-Telecom / 2-CyberSecurity)"));
            if (group != 1 && group != 2) {
                throw new IllegalArgumentException("Group (1-Telecom / 2-CyberSecurity)");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Group (1-Telecom / 2-CyberSecurity)");
        }

        int birthYear;
        try {
            birthYear = Integer.parseInt(data.get("BirthYear"));
            if (birthYear < 1900 || birthYear > 2025) {
                throw new IllegalArgumentException("BirthYear");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("BirthYear");
        }

        if (group == 1) {
            return new TelecomStudent(0, data.get("FirstName"), data.get("LastName"), birthYear);
        } else {
            return new CyberSecurityStudent(0, data.get("FirstName"), data.get("LastName"), birthYear);
        }
    }

    private static void resetTemplateFile(File file) throws IOException {
        createTemplateFile(); 
    }
    
}