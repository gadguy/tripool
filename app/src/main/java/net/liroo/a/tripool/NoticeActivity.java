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

import net.liroo.a.tripool.adapter.NoticeAdapter;
import net.liroo.a.tripool.obj.NoticeItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NoticeActivity extends BaseActivity
{
    private NoticeAdapter adapter;
    private ArrayList<NoticeItem> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = new ArrayList<>();

        // For Test
        data.add(new NoticeItem("2018.12.02", "Test 서비스 오픈", "1", "http://a.liroo.net/bbs/board.php?bo_table=notice", "Test 서비스 오픈"));
        data.add(new NoticeItem("2018.12.10", "서비스 요금 안내", "1", "http://a.liroo.net/bbs/board.php?bo_table=notice", "서비스 요금 안내"));
        data.add(new NoticeItem("2018.12.15", "서비스 이용 가능 지역 및 장소 안내", "0", "http://a.liroo.net/bbs/board.php?bo_table=notice", "서비스 이용 가능 지역 및 장소 안내"));

        ListView noticeListView = findViewById(R.id.noticeListView);
        adapter = new NoticeAdapter(this, data);
        noticeListView.setAdapter(adapter);

        noticeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoticeItem item = data.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("title", item.getTitle());
                bundle.putString("url", item.getContentURL());

                Intent intent = new Intent(NoticeActivity.this, WebViewActivity.class);
                intent.putExtra("message", bundle);
                startActivity(intent);
            }
        });

        // 공지사항 목록
        NoticeTask task = new NoticeTask(this);
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

    private static class NoticeTask extends AsyncTask<Object, Void, String>
    {
        private WeakReference<NoticeActivity> activityReference;

        // only retain a weak reference to the activity
        NoticeTask(NoticeActivity context) {
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
            NoticeActivity activity = activityReference.get();
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
