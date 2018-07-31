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
        TextView fromText, toText;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {
        ViewHolder holder;
        if ( view == null ) {
            view = layoutInflater.inflate(R.layout.search_result_cell, parent, false);

            holder = new ViewHolder();
            holder.fromText = (TextView)view.findViewById(R.id.fromText);
            holder.toText = (TextView)view.findViewById(R.id.toText);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder)view.getTag();
        }

        SearchItem item = data.get(index);
        holder.fromText.setText(item.getDeptMain());
        holder.toText.setText(item.getDeptSub());



        return view;
    }
}
