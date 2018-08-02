package net.liroo.a.tripool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
            holder.readyText = view.findViewById(R.id.readyText);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder)view.getTag();
        }

        SearchItem item = data.get(index);
        holder.departureText.setText(item.getDeparture());
        holder.destinationText.setText(item.getDestination());

        // TODO: 수정 필요 (데이터가 어떤 형태로 오는지 확인 불가)
        holder.deptDateText.setText(item.getDeptMain());
        holder.readyText.setText(item.getDeptSub());

        return view;
    }
}
