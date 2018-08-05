package net.liroo.a.tripool;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

public class CheckReservationAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<SearchItem> data;
    private LayoutInflater layoutInflater;

    public CheckReservationAdapter(Context context, ArrayList<SearchItem> data)
    {
        this.context = context;
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
        Button contactBtn, askBtn;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {
        ViewHolder holder;
        if ( view == null ) {
            view = layoutInflater.inflate(R.layout.check_reservation_cell, parent, false);

            holder = new ViewHolder();
            holder.contactBtn = view.findViewById(R.id.contactBtn);
            holder.askBtn = view.findViewById(R.id.askBtn);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder)view.getTag();
        }

        SearchItem item = data.get(index);
//        holder.departureText.setText(item.getDeparture());
//        holder.destinationText.setText(item.getDestination());
//
//        //시,분만 표시
//        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
////        Log.e("search_result_date", item.getDeptDate());
//
//        Long searchTime = item.getDeptDate() * 1000;        //초->밀리초로 변환
//        holder.deptDateText.setText(df.format(searchTime));

        holder.contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 전화번호 설정
//                ((CheckReservationActivity)context).callToDriver(phone);
            }
        });

        holder.askBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AskActivity.class);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
