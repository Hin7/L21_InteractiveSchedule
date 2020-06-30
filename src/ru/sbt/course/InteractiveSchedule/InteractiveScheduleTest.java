package ru.sbt.course.InteractiveSchedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Задание по 21 уроку SBT. JDBC.
 * Доступ к БД со студенческим расписанием.
 *
 * @author Hin7
 * @version 1.0 17/06/2020
 */

public class InteractiveScheduleTest {

    public static void main(String[] args) {

        InteractiveScheduleTest app = new InteractiveScheduleTest();
        app.initDatabase();

        app.userActionCicle();

    }

    private Scanner keybIn = new Scanner(System.in);
    scheduleDB scheduleDB = null;


    void initDatabase() {
        scheduleDB = new scheduleDB();
    }

    void userActionCicle() {

        String userAction = "";

        while (!"9".equals(userAction)) {

            showMenu();
            userAction = keybIn.nextLine();
            switch (userAction) {
                case "1":
                    showStudentsList();
                    break;
                case "2":
                    showSchedule();
                    break;
                case "3":
                    showVisits();
                    break;
                case "4":
                    appendStudent();
                    break;
                case "5":
                    deleteStudent();
                    break;
                case "6":
                    appendLesson();
                    break;
                case "7":
                    deleteLesson();
                    break;
                case "8":
                    appendVisit();
                    break;
                case "9":
                    break;
            }
        }
    }

    void showMenu() {
        System.out.println();
        System.out.println("Выберите действие");
        System.out.println("1 - просмотр студентов  2 - просмотр расписания  3 - просмотр посещаемости");
        System.out.println("4 - добавить студента   5 - удалить студента     6 - добавить лекцию");
        System.out.println("7 - удалить лекцию      8 - добавить посещение   9 - выход");
    }

    void showStudentsList() {
        System.out.println("Список студентов");
        for (Student s : scheduleDB.getStudentList())
            System.out.println(s);
    }

    void appendStudent() {
        System.out.println("Добавление студента в список");
        System.out.println("Введите имя студетна:");
        String firstName = keybIn.nextLine();
        System.out.println("Введите фамилю студетна:");
        String lastName = keybIn.nextLine();
        Student student = new Student(firstName, lastName);
        boolean result = scheduleDB.appendStudent(student);
        System.out.println("Студент " + (result ? student.toString() + " добавлен в список" :
                " не добавлен"));
    }

    void showSchedule() {
        System.out.println("Расписание");
        for (Lesson lesson : scheduleDB.getLessonList())
            System.out.println(lesson);
    }

    void appendLesson() {
        try {
            System.out.println("Добавление лекции");
            System.out.println("Введите название предмета:");
            String lessonName = keybIn.nextLine();
            System.out.println("Введите дату лекции");
            String dateStr = keybIn.nextLine();
            Lesson lesson = new Lesson(lessonName, DateFormat.getDateInstance().parse(dateStr));

            boolean result = scheduleDB.appendLesson(lesson);
            System.out.println("Лекция " + (result ? lesson.toString() + " добавлена в расписание" : " не добавлена"));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    int getStudentIdFromUserString() {
        System.out.println("Введите фамилию или id студента");
        String userString = keybIn.nextLine();
        if (Character.isDigit(userString.charAt(0)))
            return Integer.parseInt(userString);
        return scheduleDB.getStudentId(userString);
    }

    int getLessonIdFromUserString() {
        System.out.println("Введите id или название лекции");
        String lessonName = keybIn.nextLine();
        if (Character.isDigit(lessonName.charAt(0)))
            return Integer.parseInt(lessonName);
        System.out.println("Введите дату лекции");
        String dateStr = keybIn.nextLine();
        java.sql.Date lessonDate;
        try {
            lessonDate = new java.sql.Date(DateFormat.getDateInstance().parse(dateStr).getTime());
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return -1;
        }
        return scheduleDB.getLessonId(lessonName, lessonDate);
    }

    void appendVisit() {
        System.out.println("Добавление посещения лекции");
        int studentId = getStudentIdFromUserString();
        if (studentId < 0) {
            System.out.println("Студента с такой фамилией нет в базе");
            return;
        }
        int lessonId = getLessonIdFromUserString();
        if (lessonId < 0) {
            System.out.println("Такая лекция в указанную дату не найдена");
            return;
        }
        //добавляем посещение
        if (scheduleDB.appendVisit(studentId, lessonId)) System.out.println("Посещение добавлено");
        else System.out.println("Посещение не добавлено");
    }

    void showVisits() {
        System.out.println("Посещения лекций");
        List<StudentVisits> studentsVisitsList = scheduleDB.getStudentsVisitsList();
        for (StudentVisits sv : studentsVisitsList)
            System.out.println(sv);
    }

    void deleteLesson() {
        System.out.println("Удаление лекции");
        int lessonId = getLessonIdFromUserString();
        if (lessonId < 0) {
            System.out.println("Такая лекция в указанную дату не найдена");
            return;
        }
        if (scheduleDB.deleteLesson(lessonId))
            System.out.println("Лекция удалена");
        else
            System.out.println("Сбой при удалении лекции");
    }

    void deleteStudent() {
        System.out.println("Удаление студента");
        int studentId = getStudentIdFromUserString();
        if (studentId < 0) {
            System.out.println("Студента с такой фамилией нет в базе");
            return;
        }
        scheduleDB.deleteStudent(studentId);
    }
}
