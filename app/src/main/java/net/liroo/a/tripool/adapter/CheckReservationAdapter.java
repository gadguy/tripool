package net.liroo.a.tripool.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import net.liroo.a.tripool.AskActivity;
import net.liroo.a.tripool.R;
import net.liroo.a.tripool.obj.SearchItem;

import java.text.SimpleDateFormat;
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
        TextView reservationDeptText, reservationDestText, reservationDateText, readyText;
        TextView reservationDeptTimeText, reservationDestTimeText, peopleText;
        Button contactBtn, askBtn;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent)
    {
        ViewHolder holder;
        if ( view == null ) {
            view = layoutInflater.inflate(R.layout.check_reservation_cell, parent, false);

            holder = new ViewHolder();
            holder.reservationDeptText = view.findViewById(R.id.reservationDeptText);
            holder.reservationDestText = view.findViewById(R.id.reservationDestText);
            holder.reservationDateText = view.findViewById(R.id.reservationDateText);
            holder.reservationDeptTimeText = view.findViewById(R.id.reservationDeptTimeText);
            holder.reservationDestTimeText = view.findViewById(R.id.reservationDestTimeText);
            holder.peopleText = view.findViewById(R.id.peopleText);

            holder.contactBtn = view.findViewById(R.id.contactBtn);
            holder.askBtn = view.findViewById(R.id.askBtn);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder)view.getTag();
        }

        SearchItem item = data.get(index);
        holder.reservationDeptText.setText(item.getDeparture());
        holder.reservationDestText.setText(item.getDestination());

        // 년, 월, 일, 시, 분 표시
        SimpleDateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
        Long searchTime = item.getDeptDate() * 1000;        // 초->밀리초로 변환
        holder.reservationDateText.setText(df.format(searchTime));

        // 시, 분만 표시
        SimpleDateFormat dfDept = new SimpleDateFormat("HH:mm");
        holder.reservationDeptTimeText.setText(dfDept.format(searchTime));
        holder.reservationDestTimeText.setText(dfDept.format(searchTime+3600000));

        // 동승인원
        holder.peopleText.setText(item.getPeople()+"명");

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
