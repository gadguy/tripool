package net.liroo.a.tripool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.liroo.a.tripool.R;
import net.liroo.a.tripool.obj.ReservationItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter
{
    private ArrayList<ReservationItem> data;
    private LayoutInflater layoutInflater;

    public HistoryAdapter(Context context, ArrayList<ReservationItem> data)
    {
        this.data = data;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if ( data != null ) return data.size();
        return 0;
    }

    @Override
    public ReservationItem getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder
    {
        TextView dateText, timeText, regionText;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {
        ViewHolder holder;
        if ( view == null ) {
            view = layoutInflater.inflate(R.layout.history_cell, parent, false);

            holder = new ViewHolder();
            holder.dateText = view.findViewById(R.id.dateText);
            holder.timeText = view.findViewById(R.id.timeText);
            holder.regionText = view.findViewById(R.id.regionText);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder)view.getTag();
        }

        ReservationItem item = data.get(index);

        // 년, 월, 일 표시
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        holder.dateText.setText(df.format(item.getDeptDate() * 1000));

        // 시, 분 표시
        SimpleDateFormat dfDept = new SimpleDateFormat("HH:mm");
        holder.timeText.setText(dfDept.format(item.getDeptDate() * 1000));

        // From - To
        holder.regionText.setText(item.getDeptSub() + " - " + item.getDestSub());

        return view;
    }
}
