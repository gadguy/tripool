package net.liroo.a.tripool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import net.liroo.a.tripool.HistoryActivity;
import net.liroo.a.tripool.R;
import net.liroo.a.tripool.obj.HistoryItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<HistoryItem> data;
    private LayoutInflater layoutInflater;

    public HistoryAdapter(Context context, ArrayList<HistoryItem> data)
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
    public HistoryItem getItem(int i) {
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
        Button evaluationBtn;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {
        ViewHolder holder;
        if ( view == null ) {
            view = layoutInflater.inflate(R.layout.history_cell, parent, false);

            holder = new ViewHolder();
            holder.reservationDeptText = view.findViewById(R.id.reservationDeptText);
            holder.reservationDestText = view.findViewById(R.id.reservationDestText);
            holder.reservationDateText = view.findViewById(R.id.reservationDateText);
            holder.reservationDeptTimeText = view.findViewById(R.id.reservationDeptTimeText);
            holder.reservationDestTimeText = view.findViewById(R.id.reservationDestTimeText);
            holder.peopleText = view.findViewById(R.id.peopleText);

            holder.evaluationBtn = view.findViewById(R.id.evaluationBtn);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder)view.getTag();
        }

        final HistoryItem item = data.get(index);
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

        // 기사님 평가하기
        if ( item.isDoEvaluation() ) {
            holder.evaluationBtn.setEnabled(false);
            holder.evaluationBtn.setBackgroundColor(Color.GRAY);
            holder.evaluationBtn.setText(R.string.driver_evaluation_complete);
        }
        else {
            holder.evaluationBtn.setEnabled(true);
            holder.evaluationBtn.setBackgroundColor(Color.parseColor("#117869"));
            holder.evaluationBtn.setText(R.string.driver_evaluation);
        }

        holder.evaluationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HistoryActivity)context).showEvaluationDialog(item);
            }
        });

        return view;
    }
}
