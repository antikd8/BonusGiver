package by.bstu.fit.drugov.bonusgiver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import by.bstu.fit.drugov.bonusgiver.Helpers.JDBCHelper;
import by.bstu.fit.drugov.bonusgiver.Helpers.SQLiteHelper;

public class Login extends AppCompatActivity {

    Button login;
    TextView text;
    EditText password;
    Spinner spinner;
    RadioGroup groupRB;
    public static int studentGroupNumber;
    public static boolean user;
    ArrayAdapter adapterGroup;
    public static JDBCHelper jdbcHelper;
    public static SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        jdbcHelper = new JDBCHelper();
        jdbcHelper.execute("");

        SQLiteDatabase db = this.openOrCreateDatabase("BONUSGIVER_DATABASE",MODE_PRIVATE,null);
        Login.dbHelper = new SQLiteHelper(this,"BONUSGIVER_DATABASE", null, 1);

        setBindings();
        setListeners();
        try {
            setSpinnerList(jdbcHelper);
        } catch (SQLException | InterruptedException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setSpinnerList(JDBCHelper helper) throws SQLException, InterruptedException {
        Thread.sleep(2000);
        Map<Integer, String> groups = Login.dbHelper.getGroups();
        List<Integer> sorted = new ArrayList<>();
        for (Map.Entry<Integer, String> item:
             groups.entrySet()) {
            sorted.add(Integer.parseInt(item.getValue()));
        }
        Collections.sort(sorted);
        adapterGroup = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, sorted);
        spinner.setAdapter(adapterGroup);

    }

    private void setBindings() {
        login = findViewById(R.id.buttonLogin);
        text = findViewById(R.id.textViewLoginText);
        password = findViewById(R.id.editTextPassword);
        spinner = findViewById(R.id.spinnerGroupNumber);
        groupRB = findViewById(R.id.rg);
    }

    private void setListeners() {
        groupRB.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case (R.id.radioButtonStudent): {
                        text.setText("Пожалуйста, выберите вашу группу для просмотра бонусов!");
                        password.setVisibility(View.INVISIBLE);
                        spinner.setVisibility(View.VISIBLE);
                        user = false;
                        break;
                    }
                    case (R.id.radioButtonTeacher): {
                        text.setText("Пожалуйста, введите пароль для добавления бонусов!");
                        password.setVisibility(View.VISIBLE);
                        spinner.setVisibility(View.INVISIBLE);
                        user = true;
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user) {
                    if (password.getText() == null) return;
                    if(password.getText().toString().equals("admin")){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        password.setError("Неправильный пароль!");
                        password.setText("");
                    }
                } else {
                    studentGroupNumber = Integer.parseInt(spinner.getSelectedItem().toString());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}