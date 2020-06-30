package ru.sbt.course.InteractiveSchedule;

import java.util.Date;

public class Lesson {
    private static int lesson_count=0;

    private int id = 0;
    private String name;
    private java.sql.Date date;

    public Lesson(int id, String name, Date date) {
        this.id = id;
        this.name = name;
        this.date = new java.sql.Date(date.getTime());
    }

    public Lesson(String name, Date date) {
        this.name = name;
        this.date = new java.sql.Date(date.getTime());
        this.id = ++lesson_count;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public static void setLesson_count(int lesson_count) {
        Lesson.lesson_count = lesson_count;
    }

    @Override
    public String toString() {
        return "Lesson:" +
                "id=" + id +
                ", name=" + name +
                ", date=" + date;
    }
}
