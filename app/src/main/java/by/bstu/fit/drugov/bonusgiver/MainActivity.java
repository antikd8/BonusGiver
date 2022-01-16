package by.bstu.fit.drugov.bonusgiver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import by.bstu.fit.drugov.bonusgiver.Helpers.DbHelper;
import by.bstu.fit.drugov.bonusgiver.Helpers.JDBCHelper;
import by.bstu.fit.drugov.bonusgiver.Helpers.TimetableAdapter;
import by.bstu.fit.drugov.bonusgiver.Models.TimetableView;

public class MainActivity extends AppCompatActivity {

    public static String currentDate = "30.12.2021";
    public static CalendarView calendar;
    public static DbHelper dbHelper;
    public static SQLiteDatabase db;
    public static FirebaseDatabase fireDB;
    public static JDBCHelper jdbcHelper;

    TimetableAdapter adapter;
    FloatingActionButton fab;
    ArrayList<TimetableView> timetables;
    ListView items;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        jdbcHelper = new JDBCHelper();
        jdbcHelper.execute("");

        setContentView(R.layout.activity_main);

        setBindings();

        dbHelper = new DbHelper(MainActivity.this);
        db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);
        fireDB = FirebaseDatabase.getInstance();
        extractValuesResult();
        adapter = new TimetableAdapter(MainActivity.this, timetables);
        items.setAdapter(adapter);


        items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, FullTimetable.class);
                intent.putExtra("timetable", (Serializable) timetables.get(position));
                startActivity(intent);
            }
        });


        //addDataDatabase(db);

        setListeners();

    }

    private void setBindings() {
        fab = findViewById(R.id.floatingActionButton);
        calendar = findViewById(R.id.calendarView);
        items = findViewById(R.id.timetable_listview);
    }

    private void getTimetable() throws SQLException {

        timetables = new ArrayList<>();
        ResultSet result = jdbcHelper.getTimetable(currentDate);

        while (result.next()) {
            TimetableView timetable = new TimetableView();
            timetable.date = result.getString(6);
            timetable.teacher = result.getString(5);
            timetable.group = result.getString(4);
            timetable.discipline = result.getString(3);
            timetable.lesson = result.getString(2);
            timetable.id = result.getInt(1);
            timetables.add(timetable);
        }

    }

    private void extractValuesResult() {
        timetables = new ArrayList<>();
        Cursor result = dbHelper.getTimetable(db, currentDate);
        if (result.moveToFirst() && result.getCount() >= 1) {
            do {
                TimetableView timetable = new TimetableView();
                timetable.date = result.getString(result.getColumnIndexOrThrow("Дата"));
                timetable.teacher = result.getString(result.getColumnIndexOrThrow("Преподаватель"));
                timetable.group = result.getString(result.getColumnIndexOrThrow("Группа"));
                timetable.discipline = result.getString(result.getColumnIndexOrThrow("Дисциплина"));
                timetable.lesson = result.getString(result.getColumnIndexOrThrow("Началo")) + "-" + result.getString(result.getColumnIndexOrThrow("Конец"));
                timetable.id = result.getInt(result.getColumnIndexOrThrow("_id"));
                timetables.add(timetable);
            } while (result.moveToNext());
        }
    }

    public void setListeners() {
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                int mYear = year;
                int mMonth = month;
                int mDay = dayOfMonth;
                currentDate = new StringBuilder().append(mDay)
                        .append(".")
                        .append(mMonth + 1)
                        .append(".")
                        .append(mYear).toString();
                Cursor result = dbHelper.getTimetable(db, currentDate);
                //extractValuesResult();
                try {
                    getTimetable();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                adapter = new TimetableAdapter(MainActivity.this, timetables);
                items.setAdapter(adapter);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTimetable.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adding_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add_teacher: {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_data, null);
                AlertDialog.Builder builder = dialogBuilderHandler(view);
                TextView tv = view.findViewById(R.id.textViewText);
                tv.setText("Преподаватель:");
                EditText inputData = view.findViewById(R.id.editTextInputField);
                createDialogBuilder(builder, tv, inputData);
                return true;
            }
            case R.id.add_discipline: {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_data, null);
                AlertDialog.Builder builder = dialogBuilderHandler(view);
                TextView tv = view.findViewById(R.id.textViewText);
                tv.setText("Дисциплина:");
                EditText inputData = view.findViewById(R.id.editTextInputField);
                createDialogBuilder(builder, tv, inputData);
                return true;
            }
            case R.id.add_group: {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_data, null);
                AlertDialog.Builder builder = dialogBuilderHandler(view);
                TextView tv = view.findViewById(R.id.textViewText);
                tv.setText("Группа:");
                EditText inputData = view.findViewById(R.id.editTextInputField);
                createDialogBuilder(builder, tv, inputData);
                return true;
            }
            case R.id.add_student: {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_student, null);
                AlertDialog.Builder builder = dialogBuilderHandler(view);
                Spinner studentSpinner = view.findViewById(R.id.spinnerGroups);
                Map<Integer, String> mapGroup = null;
                try {
                    mapGroup = MainActivity.jdbcHelper.getGroups();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                ArrayAdapter adapterGroup = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mapGroup.values().toArray());
                studentSpinner.setAdapter(adapterGroup);
                EditText inputData = view.findViewById(R.id.editTextStudentName);
                builder.setCancelable(true)
                        .setPositiveButton("Доабвить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("Выход", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(inputData.getText().length()<1){
                            inputData.setError("Пожалуйста, введите данные о студенте!");
                            return;
                        }
                        try {
                            jdbcHelper.addStudent(inputData.getText().toString(), Integer.parseInt(studentSpinner.getSelectedItem().toString()));
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        alertDialog.dismiss();
                    }
                });
                return true;
            }
        }
        return true;
    }

    public AlertDialog.Builder dialogBuilderHandler(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        return builder;
    }

    public void createDialogBuilder(AlertDialog.Builder builder, TextView text, EditText data) {
        builder.setCancelable(false)
                .setNegativeButton("Выход", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (data.getText().length() < 1) {
                        data.setError("Пожалуйста, добавьте нужные данные!");
                        return;
                    }
                    if ("Преподаватель:".equals(text.getText().toString())) {
                        jdbcHelper.addTeacher(data.getText().toString());
                        alertDialog.dismiss();
                    }
                    if ("Дисциплина:".equals(text.getText().toString())) {
                        jdbcHelper.addDiscipline(data.getText().toString());
                        alertDialog.dismiss();
                    }
                    if ("Группа:".equals(text.getText().toString())) {
                        jdbcHelper.addGroup(data.getText().toString());
                        alertDialog.dismiss();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        });
    }


    public void addDataDatabase(SQLiteDatabase db) {
        dbHelper.fillGroups(db);
        dbHelper.fillStudents(db);
        dbHelper.fillDays(db);
        dbHelper.fillDisciplines(db);
        dbHelper.fillLessons(db);
        dbHelper.fillTeachers(db);
        dbHelper.fillTimeTable(db);
        dbHelper.fillBonusGiver(db);
    }
}