package by.bstu.fit.drugov.bonusgiver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
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
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import by.bstu.fit.drugov.bonusgiver.Helpers.JDBCHelper;
import by.bstu.fit.drugov.bonusgiver.Helpers.TimetableAdapter;
import by.bstu.fit.drugov.bonusgiver.Models.TimetableView;

public class MainActivity extends AppCompatActivity {

    public static String currentDate = "19.1.2022";
    public static CalendarView calendar;

    TimetableAdapter adapter;
    FloatingActionButton fab;
    FloatingActionButton getAllStatistics;
    ArrayList<TimetableView> timetables;
    ListView items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);

        setBindings();
        setRoleOpportunities();

        try {
            getTimetable();
        } catch (SQLException | InterruptedException throwables) {
            System.out.println("Возникла ошибка соединения с сетью");
            throwables.printStackTrace();
        }
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

        setListeners();

    }

    private void setRoleOpportunities(){
        if(!Login.user){
            fab.setVisibility(View.INVISIBLE);

        }
    }

    private void setBindings() {
        fab = findViewById(R.id.floatingActionButton);
        calendar = findViewById(R.id.calendarView);
        items = findViewById(R.id.timetable_listview);
        getAllStatistics = findViewById(R.id.floatingActionButtonStatistics);
    }

    private void getTimetable() throws SQLException, InterruptedException {
        timetables = new ArrayList<>();
        Thread.sleep(1000);
        ResultSet result = Login.user? Login.jdbcHelper.getTimetable(currentDate):
                Login.jdbcHelper.getTimetableWithGroup(currentDate, Login.studentGroupNumber);
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


    public void setListeners() {
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                currentDate = new StringBuilder().append(dayOfMonth)
                        .append(".")
                        .append(month + 1)
                        .append(".")
                        .append(year).toString();
                try {
                    getTimetable();
                } catch (SQLException | InterruptedException throwables) {
                    throwables.printStackTrace();
                }
                adapter = new TimetableAdapter(MainActivity.this, timetables);
                items.setAdapter(adapter);
            }
        });

        getAllStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllStatistics.class);
                startActivity(intent);
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
        if(Login.user) getMenuInflater().inflate(R.menu.adding_data, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
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
                inputData.setInputType(InputType.TYPE_CLASS_PHONE);
                createDialogBuilder(builder, tv, inputData);
                return true;
            }
            case R.id.add_student: {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_student, null);
                AlertDialog.Builder builder = dialogBuilderHandler(view);
                Spinner studentSpinner = view.findViewById(R.id.spinnerGroups);
                Map<Integer, String> mapGroup = null;
                try {
                    mapGroup = Login.jdbcHelper.getGroups();
                } catch (SQLException throwables) {
                    System.out.println("Возникла ошибка соединения с сетью");
                    throwables.printStackTrace();
                }
                List<Integer> sorted = new ArrayList<>();
                
                for (Map.Entry<Integer, String> entry:
                        mapGroup.entrySet()) {
                    sorted.add(Integer.parseInt(entry.getValue()));
                }
                Collections.sort(sorted);
                ArrayAdapter adapterGroup = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, sorted);
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
                            Login.jdbcHelper.addStudent(inputData.getText().toString(), Login.jdbcHelper.getGroupIdByNumber(Integer.parseInt(studentSpinner.getSelectedItem().toString())));
                        } catch (SQLException throwables) {
                            System.out.println("Возникла ошибка соединения с сетью");
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
                        Login.jdbcHelper.addTeacher(data.getText().toString());
                        alertDialog.dismiss();
                    }
                    if ("Дисциплина:".equals(text.getText().toString())) {
                        Login.jdbcHelper.addDiscipline(data.getText().toString());
                        alertDialog.dismiss();
                    }
                    if ("Группа:".equals(text.getText().toString())) {
                        Login.jdbcHelper.addGroup(data.getText().toString());
                        alertDialog.dismiss();
                    }
                } catch (SQLException throwables) {
                    System.out.println("Возникла ошибка соединения с сетью");
                    throwables.printStackTrace();
                }
            }
        });
    }
}