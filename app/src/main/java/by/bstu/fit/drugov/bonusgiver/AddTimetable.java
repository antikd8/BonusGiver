package by.bstu.fit.drugov.bonusgiver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Array;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import by.bstu.fit.drugov.bonusgiver.Helpers.DbHelper;
import by.bstu.fit.drugov.bonusgiver.Models.Timetable;
import by.bstu.fit.drugov.bonusgiver.Models.TimetableView;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddTimetable extends AppCompatActivity {

    private String currentDate = LocalDateTime.now().getDayOfMonth()
            + "." + LocalDateTime.now().getMonthValue()
            + "." + LocalDateTime.now().getYear();
    Spinner LessonNumber;
    Spinner Teacher;
    Spinner Discipline;
    Spinner Group;

    Map<Integer, String> mapLesson;
    Map<Integer, String> mapTeacher;
    Map<Integer, String> mapDiscipline;
    Map<Integer, String> mapGroup;

    ArrayAdapter adapterLessonNumber;
    ArrayAdapter adapterTeacher;
    ArrayAdapter adapterDiscipline;
    ArrayAdapter adapterGroup;

    Button addTimetable;
    EditText dateTV;


    public DbHelper dbHelper;
    public SQLiteDatabase db;
    public FirebaseDatabase fireDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timetable);

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);

        fireDB = FirebaseDatabase.getInstance();


        LessonNumber = findViewById(R.id.spinnerLessonNumber);
        Teacher = findViewById(R.id.spinnerTeacher);
        Group = findViewById(R.id.spinnerGroup);
        Discipline = findViewById(R.id.spinnerDiscipline);
        addTimetable = findViewById(R.id.buttonAddTimetable);
        dateTV = findViewById(R.id.editTextLessonDate);

        try {
            setLessonNumber();
            setDiscipline();
            setGroup();
            setTeacher();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        setListeners();
    }

    private void setGroup() throws SQLException {
        mapGroup = MainActivity.jdbcHelper.getGroups();
        adapterGroup = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mapGroup.values().toArray());
        Group.setAdapter(adapterGroup);
    }

    private void setDiscipline() throws SQLException {
        mapDiscipline = MainActivity.jdbcHelper.getDisciplines();
        adapterDiscipline = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mapDiscipline.values().toArray());
        Discipline.setAdapter(adapterDiscipline);
    }

    private void setTeacher() throws SQLException {
        mapTeacher = MainActivity.jdbcHelper.getTeachers();
        adapterTeacher = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mapTeacher.values().toArray());
        Teacher.setAdapter(adapterTeacher);
    }

    private void setLessonNumber() throws SQLException {
        mapLesson = MainActivity.jdbcHelper.getLessons();
        adapterLessonNumber = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mapLesson.values().toArray());
        LessonNumber.setAdapter(adapterLessonNumber);

    }


    private void setListeners() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTimetable.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month++;
                        String date = dayOfMonth + "." + month + "." + year;
                        currentDate = date;
                        dateTV.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
                dateTV.setError(null);
            }
        });

        addTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!currentDate.equals(dateTV.getText().toString())){
                    System.out.println(currentDate+"---"+dateTV.getText().toString());
                    dateTV.setError("Пожалуйста, выберите дату!");
                    return;
                }

                Timetable timetable = new Timetable();
                TimetableView tv = new TimetableView();
                for (Map.Entry<Integer, String> entry : mapTeacher.entrySet()) {
                    if (entry.getValue().equals(Teacher.getSelectedItem().toString())) {
                        timetable.teacher = entry.getKey();
                        tv.teacher = entry.getValue();
                    }
                }

                for (Map.Entry<Integer, String> entry : mapDiscipline.entrySet()) {
                    if (entry.getValue().equals(Discipline.getSelectedItem().toString()))
                        timetable.discipline = entry.getKey();
                    tv.discipline = entry.getValue();

                }

                for (Map.Entry<Integer, String> entry : mapGroup.entrySet()) {
                    if (entry.getValue().equals(Group.getSelectedItem().toString()))
                        timetable.group = entry.getKey();
                    tv.group = entry.getValue();

                }

                for (Map.Entry<Integer, String> entry : mapLesson.entrySet()) {
                    if (entry.getValue().equals(LessonNumber.getSelectedItem().toString()))
                        timetable.lesson = entry.getKey();
                    tv.lesson = entry.getValue();

                }

                timetable.date = currentDate;
                tv.date = currentDate;
                try {
                    if (MainActivity.jdbcHelper.addTimetable(timetable)) {
                        Snackbar.make(findViewById(R.id.coordLayout), "Данные добавлены!", Snackbar.LENGTH_SHORT).show();
                        InputMethodManager imm = (InputMethodManager) AddTimetable.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        View view = AddTimetable.this.getCurrentFocus();
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }


            }
        });
    }
}