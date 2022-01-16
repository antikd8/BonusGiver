package by.bstu.fit.drugov.bonusgiver.Helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.sql.Time;
import java.util.ArrayList;

import by.bstu.fit.drugov.bonusgiver.Models.Timetable;
import by.bstu.fit.drugov.bonusgiver.Models.TimetableView;
import by.bstu.fit.drugov.bonusgiver.R;

public class TimetableAdapter extends ArrayAdapter<TimetableView> {
    public TimetableAdapter(Context context, ArrayList<TimetableView> timetableArrayList) {
        super(context, R.layout.timetable_item, timetableArrayList);
    }

    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        TimetableView timetable = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.timetable_item, parent, false);
        }
        TextView tvDate = view.findViewById(R.id.tvDate);
     //   TextView tvDay = view.findViewById(R.id.tvDay);
        TextView tvTeacher = view.findViewById(R.id.tvTeacher);
        TextView tvLessonInfo = view.findViewById(R.id.tvTime);
        TextView tvGroup = view.findViewById(R.id.tvGroup);
        TextView tvDiscipline = view.findViewById(R.id.tvDiscipline);

        tvDate.setText(timetable.date);
//        tvDay.setText(timetable.dayOfWeek);
        tvTeacher.setText(timetable.teacher);
        tvGroup.setText(timetable.group);
        tvDiscipline.setText(timetable.discipline);
        tvLessonInfo.setText(timetable.lesson);

        return view;
    }
}
