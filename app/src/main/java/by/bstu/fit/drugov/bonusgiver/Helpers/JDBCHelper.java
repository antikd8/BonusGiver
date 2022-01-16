package by.bstu.fit.drugov.bonusgiver.Helpers;

import android.os.AsyncTask;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import by.bstu.fit.drugov.bonusgiver.Models.BonusGiver;
import by.bstu.fit.drugov.bonusgiver.Models.Timetable;

public class JDBCHelper extends AsyncTask<String, String, String> {

    Connection connection;

    @Override
    protected String doInBackground(String... strings) {
        String address = "80.94.168.145";
        String port = "1433";
        String dbName = "BonusGiver";
        String user = "student";
        String password = "Pa$$w0rd";
        connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            System.out.println("Driver is registered!");

            ConnectionURL = "jdbc:jtds:sqlserver://" + address + ":" + port + "/" + dbName + ";";

            connection = DriverManager.getConnection(ConnectionURL, "student", "Pa$$w0rd");
            System.out.println("You are connected");

        } catch (ClassNotFoundException e) {
            System.out.println(e.getStackTrace());
        } catch (SQLException throwables) {
            System.out.println(throwables.getStackTrace());
        }

        return null;
    }

    public ResultSet getTimetable(String date) {
        try {
            CallableStatement statement = connection.prepareCall("dbo.getTimetable @date = '" + date + "'");
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<Integer, String> getTeachers() throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.getTeachers");
        ResultSet result = statement.executeQuery();
        Map<Integer, String> teachers = new HashMap<>();
        while (result.next()) {
            teachers.put(result.getInt(1), result.getString(2));
        }
        return teachers;
    }

    public Map<Integer, String> getGroups() throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.getGroups");
        ResultSet result = statement.executeQuery();
        Map<Integer, String> teachers = new HashMap<>();
        while (result.next()) {
            teachers.put(result.getInt(1), result.getString(2));
        }
        return teachers;
    }

    public Map<Integer, String> getLessons() throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.getLessons");
        ResultSet result = statement.executeQuery();
        Map<Integer, String> teachers = new HashMap<>();
        while (result.next()) {
            teachers.put(result.getInt(1), result.getString(2));
        }
        return teachers;
    }

    public Map<Integer, String> getDisciplines() throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.getDisciplines");
        ResultSet result = statement.executeQuery();
        Map<Integer, String> teachers = new HashMap<>();
        while (result.next()) {
            teachers.put(result.getInt(1), result.getString(2));
        }
        return teachers;
    }

    public Map<Integer, String> getStudents(int groupID, int timetableId) throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.getStudentsByGroup @number = " + groupID +
                ", @timetableId = " + timetableId);
        ResultSet resultSet = statement.executeQuery();
        Map<Integer, String> students = new HashMap<>();
        while (resultSet.next()) {
            students.put(resultSet.getInt(1), resultSet.getString(2));
        }
        return students;
    }

    public void addDiscipline(String discipline) throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.addDisciplines @discipline = '" + discipline + "'");
        statement.execute();
    }

    public void addTeacher(String teacherName) throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.addTeacher @teacher = '" + teacherName + "'");
        statement.execute();
    }

    public void addGroup(String number) throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.addGroup @number = '" + Integer.parseInt(number) + "'");
        statement.execute();
    }

    public boolean addTimetable(Timetable item) throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.AddTimetable " +
                "@lessonInfo = " + item.lesson +
                ", @groupInfo= " + item.group +
                ", @disciplineInfo=" + item.discipline +
                ", @teacherInfo=" + item.teacher +
                ", @lessonDate= '" + item.date + "'");
        statement.execute();
        return true;
    }

    public void addStudent(String name, int group) throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.addStudent " +
                "@name = '" + name + "'" +
                ",@group = " + group);
        statement.execute();
    }

    public void addBonuses(int timetableId, int studentId, int bonus, String note) throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.addBonuses " +
                "@bonus = " + bonus +
                ", @student=" + studentId +
                ", @timetable=" + timetableId +
                ", @note='" + note + "'");
        statement.execute();
    }

    public int getGroupIdByNumber(int number) throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.getGroupIdByNumber" +
                " @number = " + number);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next())
            return resultSet.getInt(1);
        return 0;
    }

    public ResultSet getBonuses(int timetableId, int groupId) throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.getBOnuses @group = " + groupId + " ,@timetable = " + timetableId);
        return statement.executeQuery();
    }

    public void updateBonuses(BonusGiver bonusGiver, int timetable, int student) throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.updateBonuses @bonus = '" + bonusGiver.bonus +
                "', @note = '" + bonusGiver.note +
                "', @student = " + student +
                ", @timetable = " + timetable);
        statement.execute();
    }

    public int getStudentIdByName(String student) throws SQLException {
        CallableStatement statement = connection.prepareCall("dbo.getStudentIdByName @name = '"+student+"'");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next())
            return resultSet.getInt(1);
        return 0;
    }
}
