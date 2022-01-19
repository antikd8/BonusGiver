package by.bstu.fit.drugov.bonusgiver;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import by.bstu.fit.drugov.bonusgiver.Helpers.BonusesAdapter;
import by.bstu.fit.drugov.bonusgiver.Helpers.JSONHelper;
import by.bstu.fit.drugov.bonusgiver.Models.BonusGiver;
import by.bstu.fit.drugov.bonusgiver.Models.TimetableView;

public class FullTimetable extends AppCompatActivity {

    TextView tvDate;
    TextView tvTeacher;
    TextView tvLessonInfo;
    TextView tvGroup;
    TextView tvDiscipline;
    Button addBonus;
    Spinner studentSpinner;
    EditText bonus_adding;
    EditText note_adding;
    ArrayAdapter studentAdapter;
    Map<Integer, String> mapStudents;

    public static SQLiteDatabase db;
    public static Object context;

    public static TimetableView timetableView;

    ListView students;
    ArrayList<BonusGiver> bonuses;
    public static BonusesAdapter adapter;
    boolean isSortedSize = false;
    boolean isSortedAlphabet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_timetable);
        setBindings();
        setListeners();
        context = FullTimetable.this;

        Intent prevActivity = getIntent();
        extractDataFromActivity(prevActivity);
        setBonusesList();

    }

    private void setBonusesList(){
        try {
            extractBonusesList(Login.jdbcHelper.getBonuses(timetableView.id, Login.jdbcHelper.getGroupIdByNumber(Integer.parseInt(timetableView.group))));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (bonuses != null) {
            adapter = new BonusesAdapter(this, bonuses);
            students.setAdapter(adapter);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!Login.user) menu.findItem(R.id.add_student_group).setVisible(false);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        LayoutInflater inflater = LayoutInflater.from(FullTimetable.this);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(FullTimetable.this);
        switch (id) {
            case (R.id.add_student_group): {
                View view = inflater.inflate(R.layout.add_data, null);
                builder.setView(view);
                TextView tv = view.findViewById(R.id.textViewText);
                tv.setText("Добавить студента в группу " + timetableView.group + " : ");
                EditText inputData = view.findViewById(R.id.editTextInputField);
                builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (inputData.getText().toString().equals("")) {
                            inputData.setError("Пожалуйста, введите информацию о студенте!");
                            return;
                        }
                        try {
                            Login.jdbcHelper.addStudent(inputData.getText().toString(),
                                    Login.jdbcHelper.getGroupIdByNumber(Integer.parseInt(timetableView.group)));
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        Toast.makeText(FullTimetable.this, "Студент успешно добавлен в группу!", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });
                break;
            }
            case R.id.download_bonuses: {
                JSONHelper helper = new JSONHelper();
                helper.exportToJSONPublic(FullTimetable.this, bonuses);
                Toast.makeText(FullTimetable.this, "Список бонусов успешно сохранен!", Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.sort_alphabet: {
                bonuses = isSortedAlphabet ?
                        (ArrayList<BonusGiver>) bonuses.stream()
                                .sorted(Comparator.comparing(BonusGiver::getStudent)
                                        .reversed())
                                .collect(Collectors.toList()) :
                        (ArrayList<BonusGiver>) bonuses.stream()
                                .sorted(Comparator.comparing(BonusGiver::getStudent))
                                .collect(Collectors.toList());
                adapter = new BonusesAdapter(this, bonuses);
                students.setAdapter(adapter);
                isSortedAlphabet = !isSortedAlphabet;
                break;
            }

            case R.id.sort_size: {
                bonuses = isSortedSize ? (ArrayList<BonusGiver>) bonuses.stream()
                        .sorted(Comparator.comparingInt(BonusGiver::getBonus)
                                .reversed())
                        .collect(Collectors.toList()) :
                        (ArrayList<BonusGiver>) bonuses.stream()
                                .sorted(Comparator.comparingInt(BonusGiver::getBonus))
                                .collect(Collectors.toList());
                adapter = new BonusesAdapter(this, bonuses);
                students.setAdapter(adapter);
                isSortedSize = !isSortedSize;
                break;
            }

            case R.id.refresh_list:{
                setBonusesList();
                break;
            }

        }
        return true;
    }

    private void setBindings() {
        tvDate = findViewById(R.id.tvDate);
        tvTeacher = findViewById(R.id.tvTeacher);
        tvLessonInfo = findViewById(R.id.tvTime);
        tvGroup = findViewById(R.id.tvGroup);
        tvDiscipline = findViewById(R.id.tvDiscipline);
        students = findViewById(R.id.studentList);
        addBonus = findViewById(R.id.buttonAddBonus);
        if (!Login.user) addBonus.setVisibility(View.INVISIBLE);
    }

    private void setListeners() {
        addBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(FullTimetable.this);
                View addBonus = inflater.inflate(R.layout.add_bonuses, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(FullTimetable.this);
                builder.setView(addBonus);
                studentSpinner = addBonus.findViewById(R.id.spinnerStudentsAdding);
                try {
                    mapStudents = Login.jdbcHelper.getStudents(
                            Login.jdbcHelper.getGroupIdByNumber
                                    (Integer.parseInt(timetableView.group)),
                            timetableView.id);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                studentAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mapStudents.values().toArray());
                studentSpinner.setAdapter(studentAdapter);
                bonus_adding = addBonus.findViewById(R.id.editTextStudentBonuses);
                note_adding = addBonus.findViewById(R.id.editTextStudentNote);
                builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bonus_adding.getText().toString().equals("")) {
                            bonus_adding.setError("Пожалуйста, введите бонусы студента!");
                            return;
                        }

                        if (note_adding.getText().toString().equals("")) {
                            note_adding.setError("Пожалуйста, введите заметку о студенте!");
                            return;
                        }
                        int studentKey = 0;
                        for (Map.Entry<Integer, String> entry : mapStudents.entrySet()) {
                            if (entry.getValue().equals(studentSpinner.getSelectedItem().toString()))
                                studentKey = entry.getKey();
                        }
                        int bonus = Integer.parseInt(bonus_adding.getText().toString());
                        String note = note_adding.getText().toString();
                        try {
                            Login.jdbcHelper.addBonuses(timetableView.id, studentKey, bonus, note);
                            extractBonusesList(Login.jdbcHelper.getBonuses(timetableView.id, Integer.parseInt(timetableView.group)));
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        alertDialog.dismiss();
                        if (bonuses != null) {
                            setBonusesList();
                        }

                    }
                });
            }
        });
    }

    private void extractBonusesList(ResultSet result) throws SQLException {
        if (result.getFetchSize() >= 1) {
            bonuses = new ArrayList<>();
            while (result.next()) {
                BonusGiver bonus = new BonusGiver();
                bonus.student = result.getString(1);
                bonus.bonus = Integer.parseInt(result.getString(2));
                bonus.note = result.getString(3);
                bonuses.add(bonus);
            }
        }
    }

    private void extractDataFromActivity(Intent prevActivity) {
        if (prevActivity.hasExtra("timetable")) {
            timetableView = (TimetableView) prevActivity.getSerializableExtra("timetable");
            tvDate.setText(timetableView.date);
            tvTeacher.setText(timetableView.teacher);
            tvLessonInfo.setText(timetableView.lesson);
            tvGroup.setText(timetableView.group);
            tvDiscipline.setText(timetableView.discipline);
        }
    }

}