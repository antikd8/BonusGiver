package by.bstu.fit.drugov.bonusgiver.Helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import by.bstu.fit.drugov.bonusgiver.Models.BonusGiver;
import by.bstu.fit.drugov.bonusgiver.Models.Timetable;


public class SQLiteHelper extends SQLiteOpenHelper {

    public Context context;
    public SQLiteDatabase db;

    public SQLiteHelper(@Nullable Context context, @Nullable String name,
                        @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
        this.db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String disciplineTable = "CREATE TABLE \"DISCIPLINE\" (\n" +
                "\t\"ID\"\tint NOT NULL,\n" +
                "\t\"DISCIPLINE_NAME\"\tnvarchar(100) COLLATE NOCASE,\n" +
                "\tPRIMARY KEY(\"ID\")\n" +
                ");";

        String teacherTable = "CREATE TABLE \"TEACHER\" (\n" +
                "\t\"ID\"\tINTEGER NOT NULL,\n" +
                "\t\"TEACHER_NAME\"\tnvarchar(150) COLLATE NOCASE,\n" +
                "\tPRIMARY KEY(\"ID\")\n" +
                ");";

        String lessonTable = "CREATE TABLE \"LESSON_INFO\" (\n" +
                "\t\"ID\"\tint NOT NULL,\n" +
                "\t\"LESSON_TIME\"\tnvarchar(20) COLLATE NOCASE,\n" +
                "\tPRIMARY KEY(\"ID\")\n" +
                ");";

        String groupsTable = "CREATE TABLE \"GROUPS\" (\n" +
                "\t\"ID\"\tint NOT NULL,\n" +
                "\t\"NUMBER\"\tint NOT NULL,\n" +
                "\tPRIMARY KEY(\"ID\")\n" +
                ");";

        String timetableTable = "CREATE TABLE \"TIMETABLE\" (\n" +
                "\t\"ID\"\tint NOT NULL,\n" +
                "\t\"LESSON_INFO\"\tint,\n" +
                "\t\"GROUP_INFO\"\tint,\n" +
                "\t\"DISCIPLINE_INFO\"\tint,\n" +
                "\t\"TEACHER_INFO\"\tint,\n" +
                "\t\"LESSON_DATE\"\tnvarchar(100) COLLATE NOCASE,\n" +
                "\tPRIMARY KEY(\"ID\"),\n" +
                "\tFOREIGN KEY(\"GROUP_INFO\") REFERENCES \"GROUPS\"(\"ID\"),\n" +
                "\tFOREIGN KEY(\"DISCIPLINE_INFO\") REFERENCES \"DISCIPLINE\"(\"ID\"),\n" +
                "\tFOREIGN KEY(\"LESSON_INFO\") REFERENCES \"LESSON_INFO\"(\"ID\")\n" +
                ");";

        String studentTable = "CREATE TABLE \"STUDENT\" (\n" +
                "\t\"ID\"\tint NOT NULL,\n" +
                "\t\"STUDENT_NAME\"\tnvarchar(100) COLLATE NOCASE,\n" +
                "\t\"GROUP_INFO\"\tint,\n" +
                "\tFOREIGN KEY(\"GROUP_INFO\") REFERENCES \"GROUPS\"(\"ID\"),\n" +
                "\tPRIMARY KEY(\"ID\")\n" +
                ");";

        String bonusesTable = "CREATE TABLE \"BONUSES\" (\n" +
                "\t\"ID\"\tint NOT NULL,\n" +
                "\t\"BONUS\"\tnvarchar(5) COLLATE NOCASE,\n" +
                "\t\"STUDENT\"\tint,\n" +
                "\t\"TIMETABLE\"\tint,\n" +
                "\t\"NOTE\"\tnvarchar(300) COLLATE NOCASE,\n" +
                "\tPRIMARY KEY(\"ID\"),\n" +
                "\tFOREIGN KEY(\"STUDENT\") REFERENCES \"STUDENT\"(\"ID\"),\n" +
                "\tFOREIGN KEY(\"TIMETABLE\") REFERENCES \"TIMETABLE\"(\"ID\")\n" +
                ");";

        db.execSQL(disciplineTable);
        db.execSQL(teacherTable);
        db.execSQL(lessonTable);
        db.execSQL(groupsTable);
        db.execSQL(timetableTable);
        db.execSQL(studentTable);
        db.execSQL(bonusesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getTimetable(String date) {
        return db.rawQuery("select t.id, l.LESSON_TIME,d.DISCIPLINE_NAME, g.NUMBER, tchr.TEACHER_NAME, t.LESSON_DATE from TIMETABLE t\n" +
                "inner join GROUPS g\n" +
                "on t.GROUP_INFO = g.ID\n" +
                "inner join TEACHER tchr\n" +
                "on tchr.ID = t.TEACHER_INFO\n" +
                "inner join LESSON_INFO l\n" +
                "on l.ID = t.LESSON_INFO\n" +
                "inner join DISCIPLINE d\n" +
                "on t.DISCIPLINE_INFO = d.ID\n" +
                "where t.LESSON_DATE = '" + date + "';", null);
    }

    public void addTeacher(String teacherName) {
        db.execSQL("insert into TEACHER(TEACHER_NAME) values ('" + teacherName + "')");
    }

    public Map<Integer, String> getTeachers() throws SQLException {
        Cursor result = db.rawQuery("SELECT * FROM TEACHER", null);
        Map<Integer, String> teachers = new HashMap<>();
        while (result.moveToNext()) {
            teachers.put(result.getInt(0), result.getString(1));
        }
        return teachers;
    }

    public void addDiscipline(String disciplineName) {
        db.execSQL("insert into DISCIPLINE(DISCIPLINE_NAME) values ('" + disciplineName + "')");
    }

    public Map<Integer, String> getDisciplines() {
        Cursor result = db.rawQuery("SELECT * FROM DISCIPLINE", null);
        Map<Integer, String> disciplines = new HashMap<>();
        while (result.moveToNext()) {
            disciplines.put(result.getInt(0), result.getString(1));
        }
        return disciplines;
    }

    public void addGroup(String groupNumber) {
        db.execSQL("insert into GROUPS(NUMBER) values ('" + Integer.parseInt(groupNumber) + "')");
    }

    public Map<Integer, String> getGroups() {
        Cursor result = db.rawQuery("SELECT * FROM GROUPS", null);
        Map<Integer, String> groups = new HashMap<>();
        while (result.moveToNext()) {
            groups.put(result.getInt(0), result.getString(1));
        }
        return groups;
    }

    public Map<Integer, String> getLessons() {
        Cursor result = db.rawQuery("SELECT * FROM LESSON_INFO", null);
        Map<Integer, String> lessons = new HashMap<>();
        while (result.moveToNext()) {
            lessons.put(result.getInt(0), result.getString(1));
        }
        return lessons;
    }


    public boolean addTimetable(Timetable timetable) {
        db.execSQL("insert into TIMETABLE(LESSON_INFO, GROUP_INFO, DISCIPLINE_INFO, TEACHER_INFO, LESSON_DATE) values \n" +
                "(" + timetable.lesson + ", " + timetable.group + ", " + timetable.discipline + ", " + timetable.teacher + ", '" + timetable.date + "')");
        return true;
    }

    public void addStudent(String student, int groupId) {
        db.execSQL("insert into STUDENT(STUDENT_NAME, GROUP_INFO) values \n" +
                "('" + student + "', " + groupId + ")");
    }

    public int getGroupIdByNumber(int parseInt) {
        Cursor result = db.rawQuery("select ID from GROUPS where NUMBER = " + parseInt, null);
        while (result.moveToNext())
            return result.getInt(0);
        return 0;
    }

    public Cursor getAllBonusesByGroup(int group) {
        return db.rawQuery("select distinct s.STUDENT_NAME, (select sum(cast(BONUS as int)) from BONUSES \n" +
                "inner join STUDENT on \n" +
                "STUDENT.ID = BONUSES.STUDENT \n" +
                "where STUDENT.ID = s.ID) from BONUSES b\n" +
                "inner join STUDENT s\n" +
                "on s.ID = b.STUDENT\n" +
                "inner join GROUPS g\n" +
                "on g.ID = s.GROUP_INFO\n" +
                "where g.NUMBER = " + group, null);
    }

    public Map<Integer, String> getStudents(int groupId, int timetableId) {
        Cursor result = db.rawQuery("select ID,STUDENT_NAME from STUDENT where GROUP_INFO = " + groupId + " \n" +
                "except \n" +
                "select STUDENT.ID,STUDENT_NAME from BONUSES\n" +
                "inner join STUDENT on\n" +
                "STUDENT.ID = BONUSES.STUDENT\n" +
                "where BONUSES.TIMETABLE = " + timetableId, null);
        Map<Integer, String> students = new HashMap<>();
        while (result.moveToNext()) {
            students.put(result.getInt(0), result.getString(1));
        }
        return students;

    }

    public Cursor getBonuses(int timetable, int group) {
        return db.rawQuery("select s.STUDENT_NAME,b.BONUS, b.NOTE from BONUSES b\n" +
                "inner join STUDENT s\n" +
                "on s.ID = b.STUDENT\n" +
                "inner join TIMETABLE t\n" +
                "on t.ID = b.TIMETABLE\n" +
                "inner join GROUPS g\n" +
                "on g.ID = t.GROUP_INFO\n" +
                "where g.ID = " + group + " and t.ID = " + timetable, null);
    }

    public void addBonuses(int timetableId, int studentId, int bonus, String note) {
        db.execSQL("insert into BONUSES(BONUS, STUDENT, TIMETABLE, NOTE) values\n" +
                "(" + bonus + ", " + studentId + ", " + timetableId + ", '" + note + "')");
    }

    public int getStudentIdByName(String student) {
        Cursor result = db.rawQuery("select ID from STUDENT where STUDENT_NAME = '" + student + "';", null);
        while (result.moveToNext())
            return result.getInt(0);
        return 0;
    }

    public void updateBonuses(BonusGiver bonusGiver, int timetable, int student) {
        db.execSQL("update BONUSES set NOTE = '" + bonusGiver.note + "'," +
                "BONUS = " + bonusGiver.bonus + " where STUDENT = " + student + " and TIMETABLE = " + timetable);
    }

    public Cursor getTimetableWithGroup(String currentDate, int studentGroupNumber) {
        return db.rawQuery("select t.id, l.LESSON_TIME,d.DISCIPLINE_NAME, g.NUMBER, tchr.TEACHER_NAME, t.LESSON_DATE from TIMETABLE t\n" +
                "inner join GROUPS g\n" +
                "on t.GROUP_INFO = g.ID\n" +
                "inner join TEACHER tchr\n" +
                "on tchr.ID = t.TEACHER_INFO\n" +
                "inner join LESSON_INFO l\n" +
                "on l.ID = t.LESSON_INFO\n" +
                "inner join DISCIPLINE d\n" +
                "on t.DISCIPLINE_INFO = d.ID\n" +
                "where t.LESSON_DATE = '"+currentDate+"'\n" +
                "and g.NUMBER = "+studentGroupNumber,null);
    }
}
