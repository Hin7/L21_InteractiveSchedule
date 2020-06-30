package ru.sbt.course.InteractiveSchedule;

public class Student {
    static int student_count = 0;

    private int id = 0;
    private String firstName;
    private String lastName;

    public Student(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Student(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = ++student_count;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public static void setStudent_count(int student_count) {
        Student.student_count = student_count;
    }

    @Override
    public String toString() {
        return "id=" + id + ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'';
    }
}
