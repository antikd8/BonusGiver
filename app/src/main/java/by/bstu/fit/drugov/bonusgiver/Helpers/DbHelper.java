package by.bstu.fit.drugov.bonusgiver.Helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import by.bstu.fit.drugov.bonusgiver.DbComponents;
import by.bstu.fit.drugov.bonusgiver.Models.BonusGiver;
import by.bstu.fit.drugov.bonusgiver.Models.Student;
import by.bstu.fit.drugov.bonusgiver.Models.Timetable;
import by.bstu.fit.drugov.bonusgiver.Models.TimetableView;


public class DbHelper extends SQLiteOpenHelper {

    public static String disciplineKey;

    public DbHelper(Context context) {
        super(context, DbComponents.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table if not exists " + DbComponents.TABLE_DAY + "(DAY_ID integer primary key autoincrement check(DAY_ID<=6)," +
                "NAME text not null);");

        db.execSQL("create table if not exists " + DbComponents.TABLE_GROUP + "(GROUP_ID integer primary key autoincrement," +
                "NUMBER integer not null);");

        db.execSQL("create table if not exists " + DbComponents.TABLE_STUDENT + "(STUDENT_ID integer primary key autoincrement," +
                "NAME text not null," +
                "STUDENT_GROUP integer," +
                "foreign key(STUDENT_GROUP) references " + DbComponents.TABLE_GROUP + "(GROUP_ID));");

        db.execSQL("create table if not exists " + DbComponents.TABLE_TEACHER + "(TEACHER_ID integer primary key autoincrement," +
                "NAME text not null);");

        db.execSQL("create table if not exists " + DbComponents.TABLE_LESSON + "(LESSON_ID integer primary key autoincrement," +
                "NUMBER integer not null," +
                "START_TIME text not null," +
                "END_TIME text not null);");

        db.execSQL("create table if not exists " + DbComponents.TABLE_DISCIPLINE + "(DISCIPLINE_ID integer primary key autoincrement," +
                "NAME text not null);");

        db.execSQL("create table if not exists " + DbComponents.TABLE_TIMETABLE + "(_id integer primary key autoincrement," +
                "DAY_OF_WEEK integer," +
                "LESSON_INFO integer," +
                "GROUP_INFO integer," +
                "DISCIPLINE integer," +
                "TEACHER integer," +
                "LESSON_DATE text," +
                "foreign key(DAY_OF_WEEK) references " + DbComponents.TABLE_DAY + "(DAY_ID)," +
                "foreign key(LESSON_INFO) references " + DbComponents.TABLE_LESSON + "(LESSON_ID)," +
                "foreign key(GROUP_INFO) references " + DbComponents.TABLE_GROUP + "(GROUP_ID)," +
                "foreign key(DISCIPLINE) references " + DbComponents.TABLE_DISCIPLINE + "(DISCIPLINE_ID)," +
                "foreign key(TEACHER) references " + DbComponents.TABLE_TEACHER + "(TEACHER_ID));");

        db.execSQL("create table if not exists " + DbComponents.TABLE_BONUSGIVER + "(_id integer primary key autoincrement," +
                "STUDENT integer," +
                "TIMETABLE integer," +
                "NOTE text," +
                "BONUS integer," +
                "foreign key(TIMETABLE) references " + DbComponents.TABLE_TIMETABLE + "(TIMETABLE_ID)," +
                "foreign key(STUDENT) references " + DbComponents.TABLE_STUDENT + "(STUDENT_ID));");
    }

    public void fillGroups(SQLiteDatabase db) {
        db.execSQL("insert into " + DbComponents.TABLE_GROUP + "(NUMBER) values (1),(2),(3),(4),(5),(6),(7),(8);");
    }

    public void fillStudents(SQLiteDatabase db) {
        db.execSQL("insert into " + DbComponents.TABLE_STUDENT + "(NAME,STUDENT_GROUP) values ('Другов Антон',8)," +
                "('Блинов Антон',8)," +
                "('Астровская Дарья',8)," +
                "('Валько Сергей',6)," +
                "('Ярмолик Максим',6)," +
                "('Лаппо Александра',1)," +
                "('Белявский Тихон',2)," +
                "('Лейдо Ян',3)," +
                "('Хартанович Алина',3)," +
                "('Качанова Анастасия',4)," +
                "('Квитт Кирилл',5)," +
                "('Скородумов Иван',5)," +
                "('Мотолянец Екатерина',1)," +
                "('Зизико Дарья',8);");
    }

    public void fillDays(SQLiteDatabase db) {
        db.execSQL("insert into " + DbComponents.TABLE_DAY + "(NAME) values ('Понедельник')," +
                "('Вторник')," +
                "('Среда')," +
                "('Четверг')," +
                "('Пятница')," +
                "('Суббота');");
    }

    public void fillDisciplines(SQLiteDatabase db) {
        db.execSQL("insert into " + DbComponents.TABLE_DISCIPLINE + "(NAME) values ('Программирование и безопасность баз данных мобильных систем')," +
                "('Современные технологии программирования мобильных систем')," +
                "('Тестирование программного обеспечения мобильных устройств')," +
                "('Безопасность жизнедеятельности человека')," +
                "('Математические основы обработки и анализа информации')," +
                "('Программирование сетевых приложений')," +
                "('Операционные системы и системное программирование')," +
                "('Политология');");
    }

    public void fillTeachers(SQLiteDatabase db) {
        db.execSQL("insert into " + DbComponents.TABLE_TEACHER + "(NAME) values ('Пацей Наталья Владимировна')," +
                "('Блинова Евгения Александровна')," +
                "('Шиман Дмитрий Васильевич')," +
                "('Смелов Владимир Владимирович')," +
                "('Мущук Артур Николаевич')," +
                "('Веремейчик Лариса Антоновна ')," +
                "('Шешолко Владимир Константинович')," +
                "('Крючек Петр Сергеевич');");
    }

    public void fillLessons(SQLiteDatabase db) {
        db.execSQL("insert into " + DbComponents.TABLE_LESSON + "(NUMBER, START_TIME, END_TIME) values(1,'8:00','9:20')," +
                "(2,'9:35','10:55')," +
                "(3,'11:25','12:45')," +
                "(4,'13:00','14:20')," +
                "(5,'14:40','16:00')," +
                "(6,'16:30','17:50')," +
                "(7,'18:05','19:25')," +
                "(8,'19:40','21:00');");
    }

    public void fillTimeTable(SQLiteDatabase db) {
        db.execSQL("insert into " + DbComponents.TABLE_TIMETABLE + "(DAY_OF_WEEK,LESSON_INFO, GROUP_INFO, DISCIPLINE, TEACHER, LESSON_DATE)" +
                "values (1,2,7,2,1,'27.12.2021');");
        db.execSQL("insert into " + DbComponents.TABLE_TIMETABLE + "(DAY_OF_WEEK,LESSON_INFO, GROUP_INFO, DISCIPLINE, TEACHER, LESSON_DATE)" +
                "values (4,1,7,4,6,'30.12.2021');");


    }

    public void fillBonusGiver(SQLiteDatabase db) {
        db.execSQL("insert into " + DbComponents.TABLE_BONUSGIVER + "(STUDENT, TIMETABLE, NOTE, BONUS) values " +
                "(1,1,'Хорошо отвечал на вопросы!',3)," +
                "(2,1,'Спал на лекции!',0)," +
                "(3,1,'Ответила на один вопрос!',1)");
    }

    public void addDataFirebase(FirebaseDatabase db, String item, String path) {
        DatabaseReference ref = db.getReference(path);
        ref.push().child(item).setValue(true);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                disciplineKey = snapshot.getKey();
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    public void addTimetableFirebase(FirebaseDatabase db, TimetableView item) {
        DatabaseReference ref = db.getReference("Timetable");
        ref.push().child("Date").setValue(item.date);
        DatabaseReference ref2 = db.getReference("Disciplines");
        ref2.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete( Task<DataSnapshot> task) {
                for(Object item : task.getResult().getChildren()){
                    System.out.println(item.toString());
                }
            }
        });
    }


    public boolean AddTimetable(SQLiteDatabase db, Timetable item) {
        try {
            db.execSQL("insert into " + DbComponents.TABLE_TIMETABLE + " (DAY_OF_WEEK,LESSON_INFO, GROUP_INFO, DISCIPLINE, TEACHER, LESSON_DATE) values " +
                    "("  +
                    item.lesson + "," +
                    item.group + "," +
                    item.discipline + "," +
                    item.teacher + "," + "'" +
                    item.date + "');");
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void AddStudents(SQLiteDatabase db, Student student) { //TODO испарвить
        db.execSQL("insert into " + DbComponents.TABLE_BONUSGIVER + "(STUDENT, TIMETABLE, NOTE, BONUS) values( 4) ");
    }

    public Map<Integer, String> getStudentsByGroup(SQLiteDatabase db, int group) {
        Cursor cursor = db.rawQuery("select GROUP_ID,NAME from " + DbComponents.TABLE_GROUP + " inner join " + DbComponents.TABLE_STUDENT + " on STUDENT.STUDENT_GROUP = GROUPS.GROUP_ID" +
                " where GROUPS.GROUP_ID = ?", new String[]{String.valueOf(group)});
        System.out.println(cursor.getCount());
        Map<Integer, String> students = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                students.put(cursor.getInt(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }
        System.out.println(students.size());
        //TODO почему в мапе получается 1 элемент
        return students;
    }

    public Cursor getTimetable(SQLiteDatabase db, String date) {
        return db.rawQuery("select _id, TEACHER.NAME[Преподаватель], DISCIPLINE.NAME[Дисциплина], LESSON_DATE[Дата]," +
                "DAY_OF_WEEK.NAME[День недели], " +
                "LESSON.START_TIME[Началo], " +
                "LESSON.END_TIME[Конец], " +
                "GROUPS.NUMBER[Группа] from " + DbComponents.TABLE_TIMETABLE +
                " inner join " + DbComponents.TABLE_TEACHER + " on  TEACHER.TEACHER_ID = TIMETABLE.TEACHER " +
                "inner join " + DbComponents.TABLE_DAY + " on DAY_OF_WEEK.DAY_ID = TIMETABLE.DAY_OF_WEEK " +
                "inner join " + DbComponents.TABLE_DISCIPLINE + " on DISCIPLINE.DISCIPLINE_ID = TIMETABLE.DISCIPLINE " +
                "inner join " + DbComponents.TABLE_LESSON + " on LESSON.LESSON_ID = TIMETABLE.LESSON_INFO " +
                "inner join " + DbComponents.TABLE_GROUP + " on GROUPS.GROUP_ID = TIMETABLE.GROUP_INFO" +
                " where LESSON_DATE = ?;", new String[]{date});
    }

    public Map<Integer, String> getDayOfWeek(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from " + DbComponents.TABLE_DAY + " ;", null);
        Map<Integer, String> result = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                result.put(cursor.getInt(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public Map<Integer, String> getLessonInfo(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from " + DbComponents.TABLE_LESSON + " ;", null);
        Map<Integer, String> result = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                result.put(cursor.getInt(0),
                        cursor.getString(cursor.getColumnIndexOrThrow("START_TIME"))
                                + "-"
                                + cursor.getString(cursor.getColumnIndexOrThrow("END_TIME")));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public Map<Integer, String> getTeacher(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from " + DbComponents.TABLE_TEACHER + " ;", null);
        Map<Integer, String> result = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                result.put(cursor.getInt(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public Map<Integer, String> getDiscipline(SQLiteDatabase db) {
        FirebaseDatabase fireDB = FirebaseDatabase.getInstance();
        Cursor cursor = db.rawQuery("select * from " + DbComponents.TABLE_DISCIPLINE + " ;", null);
        Map<Integer, String> result = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                result.put(cursor.getInt(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public Map<Integer, String> getGroup(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from " + DbComponents.TABLE_GROUP + " ;", null);
        Map<Integer, String> result = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                result.put(cursor.getInt(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public void updateBonuses(SQLiteDatabase db, BonusGiver item) {
        db.execSQL("update " + DbComponents.TABLE_BONUSGIVER + " set BONUS = " + item.bonus + ", NOTE = '" + item.note +
                "' where BONUS_GIVER.STUDENT = (SELECT STUDENT.STUDENT_ID from " + DbComponents.TABLE_STUDENT + " where STUDENT.NAME = '" + item.student + "') ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public Cursor getBonuses(SQLiteDatabase db, int timetable) {
        return db.rawQuery("select _id, STUDENT.NAME[Имя], BONUS[Бонус], NOTE from " + DbComponents.TABLE_BONUSGIVER + "" +
                " inner join " + DbComponents.TABLE_STUDENT +
                " on STUDENT.STUDENT_ID = BONUS_GIVER.STUDENT where TIMETABLE=?", new String[]{String.valueOf(timetable)});
    }


}
