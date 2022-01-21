package by.bstu.fit.drugov.bonusgiver.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import by.bstu.fit.drugov.bonusgiver.Models.Statistics;
import by.bstu.fit.drugov.bonusgiver.R;

public class StatisticsAdapter extends ArrayAdapter<Statistics> {
    public StatisticsAdapter(Context context, ArrayList<Statistics> timetableArrayList) {
        super(context, R.layout.single_statistics_item, timetableArrayList);
    }

    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Statistics timetable = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.single_statistics_item, parent, false);
        }
        TextView tvName = view.findViewById(R.id.textViewName);
        TextView tvBonus = view.findViewById(R.id.textViewBonusesStatistics);

        tvName.setText(timetable.student);
        tvBonus.setText(String.valueOf(timetable.bonuses));

        return view;
    }
}
