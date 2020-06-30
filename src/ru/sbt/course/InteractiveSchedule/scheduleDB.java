package ru.sbt.course.InteractiveSchedule;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class scheduleDB {

    final String url = "jdbc:postgresql://localhost:5432/mydb";
    final String user = "postgres";
    final String password = "masterkey";

    public scheduleDB() {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS Students" +
                            "(id int PRIMARY KEY," +
                            " firstname varchar(80), " +
                            "lastName varchar(80));");

            statement.execute(
                    "CREATE TABLE IF NOT EXISTS Lessons " +
                            "(id int PRIMARY KEY," +
                            "Lesson varchar(80)," +
                            "Data date);");

            statement.execute("CREATE TABLE IF NOT EXISTS Students_visits" +
                    "(student_id int," +
                    "lesson_id int);");

            syncStudentCounter(statement);
            syncLessonCounter(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    void syncStudentCounter(Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT MAX(id) FROM Students");
        if (rs.next())
            Student.setStudent_count(rs.getInt(1));
    }

    void syncLessonCounter(Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT  MAX(id) FROM Lessons");
        if (rs.next())
            Lesson.setLesson_count(rs.getInt(1));
    }

    public List<Student> getStudentList() {
        List<Student> studentList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Students;");
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                studentList.add(new Student(id, firstName, lastName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentList;
    }

    public boolean appendStudent(Student student) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Students VALUES (?, ?, ?)")) {
            statement.setInt(1, student.getId());
            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getLastName());
            statement.addBatch();
            int[] executeBatch = statement.executeBatch();
            return executeBatch[0] == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Lesson> getLessonList() {
        List<Lesson> lessonList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Lessons ORDER BY data");
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                Date date = resultSet.getDate(3);
                lessonList.add(new Lesson(id, name, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lessonList;
    }

    public boolean appendLesson(Lesson lesson) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Lessons VALUES (?, ?, ?)")) {
            statement.setInt(1, lesson.getId());
            statement.setString(2, lesson.getName());
            java.sql.Date date = new java.sql.Date(lesson.getDate().getTime());
            statement.setDate(3, lesson.getDate());
            statement.addBatch();

            int[] executeBatch = statement.executeBatch();
            return executeBatch[0] == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getStudentId(String lastName) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM Students WHERE lastName = ?")) {
            statement.setString(1, lastName);
            statement.addBatch();

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getLessonId(String lessonName, java.sql.Date lessonDate) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM Lessons " +
                     "WHERE lesson = ? AND Data = ?")) {
            statement.setString(1, lessonName);
            statement.setDate(2, lessonDate);
            statement.addBatch();

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean appendVisit(int studentId, int lessonId) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Students_visits VALUES (?, ?)")) {
            statement.setInt(1, studentId);
            statement.setInt(2, lessonId);
            statement.addBatch();

            int[] executeBatch = statement.executeBatch();
            return executeBatch[0] == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<StudentVisits> getStudentsVisitsList() {
        List<StudentVisits> studentVisitsList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT sv.lesson_id, sv.student_id, " +
                    "l.lesson, l.Data, s.lastname, s.firstname " +
                    "FROM students_visits sv, lessons l, students s " +
                    "WHERE sv.lesson_id = l.id AND sv.student_id = s.id ORDER BY l.Data, l.Id;");
            StudentVisits studentVisits = null;
            while (rs.next()) {
                int lessonId = rs.getInt(1);
                int studentId = rs.getInt(2);
                String lesson = rs.getString(3);
                java.sql.Date date = rs.getDate(4);
                String lastName = rs.getString(5);
                String firstName = rs.getString(6);
                if(studentVisits == null || studentVisits.getId() != lessonId){
                    studentVisits = new StudentVisits(lessonId, lesson, date);
                    studentVisitsList.add(studentVisits);
                }
                studentVisits.getStudents().add(new Student(studentId, firstName, lastName));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return studentVisitsList;
    }

    boolean deleteLesson(int id){
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement1 = connection.prepareStatement("DELETE FROM lessons WHERE id = ?");
             PreparedStatement statement2 = connection.prepareStatement("DELETE FROM students_visits WHERE lesson_id = ?");
             Statement statement = connection.createStatement()) {
            statement1.setInt(1, id);
            statement1.addBatch();

            statement2.setInt(1, id);
            statement2.addBatch();

            int[] executeBatch1 = statement1.executeBatch();
            int[] executeBatch2 = statement2.executeBatch();
            syncLessonCounter(statement);
            return (executeBatch1[0] == 1 & executeBatch2[0] == 1);

        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    boolean deleteStudent(int id){
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement1 = connection.prepareStatement("DELETE FROM students WHERE id = ?");
             PreparedStatement statement2 = connection.prepareStatement("DELETE FROM students_visits WHERE student_id = ?");
             Statement statement = connection.createStatement()) {

            statement1.setInt(1, id);
            statement1.addBatch();

            statement2.setInt(1, id);
            statement2.addBatch();

            int[] executeBatch1 = statement1.executeBatch();
            int[] executeBatch2 = statement2.executeBatch();
            syncStudentCounter(statement);
            return (executeBatch1[0] == 1 & executeBatch2[0] == 1);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
