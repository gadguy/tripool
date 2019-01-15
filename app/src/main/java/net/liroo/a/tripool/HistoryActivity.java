package net.liroo.a.tripool;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.liroo.a.tripool.adapter.HistoryAdapter;
import net.liroo.a.tripool.obj.ReservationItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HistoryActivity extends BaseActivity
{
    private ArrayList<ReservationItem> historyList;
    private HistoryAdapter adapter;

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
        historyList.add(new ReservationItem("1", "순천", "드라마세트장", "순천 드라마세트장", "순천", "순천역", "순천 순천역", 1547037000, "2", "2", false));
        historyList.add(new ReservationItem("1", "순천", "낙안읍성", "순천 낙안읍성", "순천", "순천역", "순천 순천역", 1547078400, "2", "2", true));
//        historyList.add(new ReservationItem("1", "순천", "순천만정원", "순천 순천만정원", "경주", "안압지", "경주 안압지", 1547078400, "1", "1", true));

        ListView historyListView = findViewById(R.id.historyListView);
        adapter = new HistoryAdapter(this, historyList);
        historyListView.setAdapter(adapter);

        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReservationItem item = historyList.get(position);

                Bundle bundle = new Bundle();
                bundle.putParcelable("reservationItem", item);
                bundle.putBoolean("isHistory", true);

                Intent intent = new Intent(HistoryActivity.this, ReservationDetailActivity.class);
                intent.putExtra("message", bundle);
                startActivity(intent);
            }
        });

        // 이용내역 목록
        GetHistoryTask task = new GetHistoryTask(this);
        task.execute("");
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
}
