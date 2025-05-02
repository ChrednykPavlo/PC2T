package project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Student implements Serializable {
    protected int id;
    protected String firstName;
    protected String lastName;
    protected int birthYear;
    protected List<Integer> grades;

    public Student(int id, String firstName, String lastName, int birthYear) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthYear = birthYear;
        this.grades = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getBirthYear() { return birthYear; }

    public void addGrade(int grade) {
        if (grade >= 1 && grade <= 5) grades.add(grade);
    }

    public double getAverageGrade() {
        return grades.isEmpty() ? 0.0 : grades.stream().mapToInt(Integer::intValue).average().getAsDouble();
    }

    public abstract void performSkill();
}