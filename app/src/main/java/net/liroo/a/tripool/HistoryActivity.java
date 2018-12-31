package net.liroo.a.tripool;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import net.liroo.a.tripool.adapter.HistoryAdapter;
import net.liroo.a.tripool.obj.HistoryItem;
import net.liroo.a.tripool.obj.SearchItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HistoryActivity extends BaseActivity
{
    private ArrayList<HistoryItem> historyList;
    private HistoryAdapter adapter;

    private View evaluationDialog;
    private TextView reservationDateText;
    private RatingBar evaluationRatingBar;
    private HistoryItem selectedItem;

    private static final String TAG_RESULTS = "result";   // json으로 가져오는 값의 파라메터

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        historyList = new ArrayList<>();
        // TODO: 임시 데이터이므로, 서버에서 데이터 가져오면 삭제
        historyList.add(new HistoryItem("1", "순천", "순천만정원", "순천 순천만정원", "경주", "안압지", "경주 안압지", 1592099999, "1", "1", false));
        historyList.add(new HistoryItem("1", "순천", "순천만정원", "순천 순천만정원", "경주", "안압지", "경주 안압지", 1592099999, "1", "1", true));

        ListView historyListView = findViewById(R.id.historyListView);
        adapter = new HistoryAdapter(this, historyList);
        historyListView.setAdapter(adapter);

        evaluationDialog = findViewById(R.id.evaluationDialog);
        evaluationRatingBar = findViewById(R.id.evaluationRatingBar);
        reservationDateText = findViewById(R.id.reservationDateText);

        Button evaluationBtn = findViewById(R.id.evaluationBtn);
        evaluationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( selectedItem != null ) {
                    // 평가 정보를 서버에 저장
                    EvaluationTask task = new EvaluationTask(HistoryActivity.this);
                    task.execute("", selectedItem);

                    // 기사님 평가 완료로 설정
                    int index = historyList.indexOf(selectedItem);
                    historyList.get(index).setDoEvaluation(true);
                    adapter.notifyDataSetChanged();

                    evaluationDialog.setVisibility(View.GONE);
                }
            }
        });

        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluationDialog.setVisibility(View.GONE);
            }
        });

        // 이용내역 목록
        GetHistoryTask task = new GetHistoryTask(this);
        task.execute("");
    }

    public void showEvaluationDialog(HistoryItem item)
    {
        selectedItem = item;

        evaluationDialog.setVisibility(View.VISIBLE);
        evaluationRatingBar.setRating(1);

        // 년, 월, 일, 시, 분 표시
        SimpleDateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
        Long time = item.getDeptDate() * 1000;        // 초->밀리초로 변환
        reservationDateText.setText(df.format(time));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemID = item.getItemId();
        if ( itemID == android.R.id.home ) {   // 뒤로
            onBackPressed();
        }
        return true;
    }

    private static class GetHistoryTask extends AsyncTask<Object, Void, String>
    {
        private WeakReference<HistoryActivity> activityReference;

        // only retain a weak reference to the activity
        GetHistoryTask(HistoryActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Object... params)
        {
            String uri = (String)params[0];

            BufferedReader bufferedReader;
            try {
                // TODO: 이용목록 서버에서 가져올 수 있도록 데이터 설정
                String data = "";
//                String data = "dept_main=" + URLEncoder.encode(item.getDeptMain(), "UTF-8");
//                data += "&dept_sub=" + URLEncoder.encode(item.getDeptSub(), "UTF-8");
//                data += "&departure=" + URLEncoder.encode(item.getDeparture(), "UTF-8");
//                data += "&dest_main=" + URLEncoder.encode(item.getDestMain(), "UTF-8");
//                data += "&dest_sub=" + URLEncoder.encode(item.getDestSub(), "UTF-8");
//                data += "&destination=" + URLEncoder.encode(item.getDestination(), "UTF-8");
//                data += "&dept_date=" + item.getDestinationeptDate()/100000;        //DB에서 찾을 때는 초단위로 변경

                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String json;
                while ( (json = bufferedReader.readLine()) != null ) {
                    sb.append(json+"\n");
                }
                return sb.toString().trim();
            } catch ( Exception e ) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String ret)
        {
            HistoryActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

//            try {
//                JSONObject jsonObj = new JSONObject(ret);
//                JSONArray jsonDeptList = jsonObj.getJSONArray(TAG_RESULTS);
//
//                activity.historyList.clear();
//                for ( int i=0; i<jsonDeptList.length(); i++ ) {
//                    JSONObject obj = jsonDeptList.getJSONObject(i);
//                    activity.historyList.add(new HistoryItem(obj));
//                }
//                activity.adapter.notifyDataSetChanged();
//            } catch ( JSONException e ) {
//                e.printStackTrace();
//            }
        }
    }

    private static class EvaluationTask extends AsyncTask<Object, Void, String>
    {
        private WeakReference<HistoryActivity> activityReference;

        // only retain a weak reference to the activity
        EvaluationTask(HistoryActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Object... params)
        {
            String uri = (String)params[0];
            HistoryItem item = (HistoryItem)params[1];

            BufferedReader bufferedReader;
            try {
                // TODO: 이용목록 서버에서 가져올 수 있도록 데이터 설정
                String data = "";
//                String data = "dept_main=" + URLEncoder.encode(item.getDeptMain(), "UTF-8");
//                data += "&dept_sub=" + URLEncoder.encode(item.getDeptSub(), "UTF-8");
//                data += "&departure=" + URLEncoder.encode(item.getDeparture(), "UTF-8");
//                data += "&dest_main=" + URLEncoder.encode(item.getDestMain(), "UTF-8");
//                data += "&dest_sub=" + URLEncoder.encode(item.getDestSub(), "UTF-8");
//                data += "&destination=" + URLEncoder.encode(item.getDestination(), "UTF-8");
//                data += "&dept_date=" + item.getDestinationeptDate()/100000;        //DB에서 찾을 때는 초단위로 변경

                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String json;
                while ( (json = bufferedReader.readLine()) != null ) {
                    sb.append(json+"\n");
                }
                return sb.toString().trim();
            } catch ( Exception e ) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String ret)
        {
            HistoryActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

//            try {
//                JSONObject jsonObj = new JSONObject(ret);
//                JSONArray jsonDeptList = jsonObj.getJSONArray(TAG_RESULTS);
//            } catch ( JSONException e ) {
//                e.printStackTrace();
//            }
        }
    }
}
