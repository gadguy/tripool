package net.liroo.a.tripool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.liroo.a.tripool.R;
import net.liroo.a.tripool.obj.NoticeItem;

import java.util.ArrayList;

public class NoticeAdapter extends BaseAdapter
{
    private ArrayList<NoticeItem> data;
    private LayoutInflater layoutInflater;

    public NoticeAdapter(Context context, ArrayList<NoticeItem> data)
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
    public NoticeItem getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder
    {
        TextView dateText, titleText, readText;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent)
    {
        ViewHolder holder;
        if ( view == null ) {
            view = layoutInflater.inflate(R.layout.notice_cell, parent, false);

            holder = new ViewHolder();
            holder.dateText = view.findViewById(R.id.dateText);
            holder.titleText = view.findViewById(R.id.titleText);
            holder.readText = view.findViewById(R.id.readText);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder)view.getTag();
        }

        NoticeItem item = data.get(index);

        holder.dateText.setText(item.getDate());
        holder.titleText.setText(item.getTitle());
        holder.readText.setText(item.getReadCount());

        return view;
    }
}
