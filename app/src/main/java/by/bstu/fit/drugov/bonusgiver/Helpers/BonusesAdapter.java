package by.bstu.fit.drugov.bonusgiver.Helpers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;

import by.bstu.fit.drugov.bonusgiver.AddTimetable;
import by.bstu.fit.drugov.bonusgiver.FullTimetable;
import by.bstu.fit.drugov.bonusgiver.Login;
import by.bstu.fit.drugov.bonusgiver.MainActivity;
import by.bstu.fit.drugov.bonusgiver.Models.BonusGiver;
import by.bstu.fit.drugov.bonusgiver.Models.TimetableView;
import by.bstu.fit.drugov.bonusgiver.R;

public class BonusesAdapter extends ArrayAdapter<BonusGiver> {

    ImageButton addBonus;
    ImageButton removeBonus;
    ImageButton saveChanges;
    BonusGiver bonusGiver;
    ArrayList<BonusGiver> bonusesArrayList;

    public BonusesAdapter(Context context, ArrayList<BonusGiver> bonusesArrayList) {
        super(context, R.layout.single_student, bonusesArrayList);
        this.bonusesArrayList = bonusesArrayList;
    }

    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        bonusGiver = (BonusGiver) getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.single_student, parent, false);
        }
        TextView name = view.findViewById(R.id.textViewName);
        TextView note = view.findViewById(R.id.textViewNote);
        TextView bonus = view.findViewById(R.id.textViewBonuses);


        addBonus = view.findViewById(R.id.addBonus);
        removeBonus = view.findViewById(R.id.removeBonus);
        saveChanges = view.findViewById(R.id.saveButton);
        if(!Login.user){
            addBonus.setVisibility(View.INVISIBLE);
            removeBonus.setVisibility(View.INVISIBLE);
            saveChanges.setVisibility(View.INVISIBLE);
            note.setEnabled(false);
        }

        addBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BonusGiver currentStudent = new BonusGiver();
                currentStudent.bonus = bonusesArrayList.get(position).bonus + 1;
                currentStudent.student = bonusesArrayList.get(position).student;
                currentStudent.note = note.getText().toString();

                bonusesArrayList.set(position, currentStudent);
                FullTimetable.adapter.notifyDataSetChanged();
            }

        });

        removeBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BonusGiver currentStudent = new BonusGiver();
                currentStudent.bonus = bonusesArrayList.get(position).bonus - 1;
                currentStudent.student = bonusesArrayList.get(position).student;
                currentStudent.note = note.getText().toString();

                bonusesArrayList.set(position, currentStudent);
                FullTimetable.adapter.notifyDataSetChanged();
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BonusGiver currentStudent = new BonusGiver();
                currentStudent.bonus = bonusesArrayList.get(position).bonus;
                currentStudent.student = bonusesArrayList.get(position).student;
                bonusesArrayList.get(position).note = note.getText().toString();
                currentStudent.note = bonusesArrayList.get(position).note;
                bonusesArrayList.set(position, currentStudent);
                System.out.println(bonusesArrayList.get(position).note);
                //TODO исправить отрицательные числа изменить тип данных на инт а в бд на стринг)))
                //Login.jdbcHelper.updateBonuses(currentStudent, FullTimetable.timetableView.id, Login.jdbcHelper.getStudentIdByName(currentStudent.student));
                Login.dbHelper.updateBonuses(currentStudent, FullTimetable.timetableView.id,
                        Login.dbHelper.getStudentIdByName(currentStudent.student));
                Toast.makeText((Context) FullTimetable.context,"Данные обновлены", Toast.LENGTH_SHORT).show();
                note.clearFocus();
            }
        });

        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0){
                    saveChanges.setEnabled(true);
                } else{
                    saveChanges.setEnabled(false);
                    note.setError("Добавьте заметку!");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        name.setText(bonusGiver.student);
        note.setText(bonusGiver.note);
        bonus.setText(String.valueOf(bonusGiver.bonus));

        return view;
    }


}
