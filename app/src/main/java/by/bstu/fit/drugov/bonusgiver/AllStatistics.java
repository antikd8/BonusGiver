package by.bstu.fit.drugov.bonusgiver;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import by.bstu.fit.drugov.bonusgiver.Helpers.BonusesAdapter;
import by.bstu.fit.drugov.bonusgiver.Helpers.JSONHelper;
import by.bstu.fit.drugov.bonusgiver.Helpers.StatisticsAdapter;
import by.bstu.fit.drugov.bonusgiver.Models.BonusGiver;
import by.bstu.fit.drugov.bonusgiver.Models.Statistics;

public class AllStatistics extends AppCompatActivity {

    ListView bonusesLV;
    Spinner groupSpinner;
    ArrayList<Statistics> stats;
    StatisticsAdapter adapter;
    boolean isSortedAlphabet;
    boolean isSortedSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_statistics);
        setBindings();
        setListeners();
        setSpinnerItems();
        try {
            setStatsList();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.add_student_group).setVisible(false);
        menu.findItem(R.id.refresh_list).setVisible(false);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        LayoutInflater inflater = LayoutInflater.from(AllStatistics.this);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(AllStatistics.this);
        switch (id) {
            case R.id.sort_alphabet: {
                stats = isSortedAlphabet ?
                        (ArrayList<Statistics>) stats.stream()
                                .sorted(Comparator.comparing(Statistics::getStudentName)
                                        .reversed())
                                .collect(Collectors.toList()) :
                        (ArrayList<Statistics>) stats.stream()
                                .sorted(Comparator.comparing(Statistics::getStudentName))
                                .collect(Collectors.toList());
                adapter = new StatisticsAdapter(this, stats);
                bonusesLV.setAdapter(adapter);
                isSortedAlphabet = !isSortedAlphabet;
                break;
            }

            case R.id.sort_size: {
                stats = isSortedSize ? (ArrayList<Statistics>) stats.stream()
                        .sorted(Comparator.comparingInt(Statistics::getBonuses)
                                .reversed())
                        .collect(Collectors.toList()) :
                        (ArrayList<Statistics>) stats.stream()
                                .sorted(Comparator.comparingInt(Statistics::getBonuses))
                                .collect(Collectors.toList());
                adapter = new StatisticsAdapter(this, stats);
                bonusesLV.setAdapter(adapter);
                isSortedSize = !isSortedSize;
                break;
            }

            case R.id.download_bonuses: {
                JSONHelper helper = new JSONHelper();
                helper.exportToJSONPublicStats(AllStatistics.this, stats);
                Toast.makeText(AllStatistics.this, "Список бонусов успешно сохранен!", Toast.LENGTH_SHORT).show();
                break;
            }


        }
        return true;
    }


    public void setListeners() {
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    setStatsList();
                    System.out.println(stats.size());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void setBindings() {
        bonusesLV = findViewById(R.id.listViewStatistics);
        groupSpinner = findViewById(R.id.spinnerGroupForStats);
    }

    public void setSpinnerItems() {
        Map<Integer, String> groups = null;
        try {
            groups = Login.jdbcHelper.getGroups();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        List<Integer> sorted = new ArrayList<>();
        for (Map.Entry<Integer, String> item :
                groups.entrySet()) {
            sorted.add(Integer.parseInt(item.getValue()));
        }
        Collections.sort(sorted);
        ArrayAdapter adapterGroup = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, sorted);
        groupSpinner.setAdapter(adapterGroup);
    }

    public void setStatsList() throws SQLException {
        ResultSet result = Login.jdbcHelper.getAllBonusesByGroup(Integer.parseInt(groupSpinner.getSelectedItem().toString()));
        stats = new ArrayList<>();
        while (result.next()) {
            Statistics item = new Statistics();
            item.student = result.getString(1);
            item.bonuses = Integer.parseInt(result.getString(2));
            stats.add(item);
        }
        if (stats != null) {
            adapter = new StatisticsAdapter(getApplicationContext(), stats);
            bonusesLV.setAdapter(adapter);
        }
    }


}