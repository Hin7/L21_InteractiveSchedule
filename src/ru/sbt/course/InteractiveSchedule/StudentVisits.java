package ru.sbt.course.InteractiveSchedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudentVisits extends Lesson {
    private List<Student> students = new ArrayList<>();

    public StudentVisits(int id, String name, Date date) {
        super(id, name, date);
    }

    public List<Student> getStudents() {
        return students;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(super.toString() + ": ");
        for (Student s : students)
            result.append(s.getLastName() + ", ");
        return result.toString();
    }
}

