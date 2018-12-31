package net.liroo.a.tripool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.liroo.a.tripool.R;
import net.liroo.a.tripool.obj.SearchItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SearchResultAdapter extends BaseAdapter
{
    private ArrayList<SearchItem> data;
    private LayoutInflater layoutInflater;

    public SearchResultAdapter(Context context, ArrayList<SearchItem> data)
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
    public SearchItem getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder
    {
        TextView departureText, destinationText, deptDateText, readyText;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {
        ViewHolder holder;
        if ( view == null ) {
            view = layoutInflater.inflate(R.layout.search_result_cell, parent, false);

            holder = new ViewHolder();
            holder.departureText = view.findViewById(R.id.departureText);
            holder.destinationText = view.findViewById(R.id.destinationText);
            holder.deptDateText = view.findViewById(R.id.deptDateText);
//            holder.readyText = view.findViewById(R.id.readyText); 준비는 일단 뺌-> 2차 개발로 넘김

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder)view.getTag();
        }

        SearchItem item = data.get(index);
        holder.departureText.setText(item.getDeparture());
        holder.destinationText.setText(item.getDestination());

        //시,분만 표시
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Long searchTime = item.getDeptDate() * 1000;        //초->밀리초로 변환
        holder.deptDateText.setText(df.format(searchTime));

        return view;
    }
}
