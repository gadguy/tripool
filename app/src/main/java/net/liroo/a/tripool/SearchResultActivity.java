package net.liroo.a.tripool;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.liroo.a.tripool.adapter.SearchResultAdapter;
import net.liroo.a.tripool.obj.SearchItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SearchResultActivity extends BaseActivity
{
    private SearchItem searchItem;
    private final static int RESERVATION_FINISH = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        final ArrayList<SearchItem> searchResultList = (ArrayList<SearchItem>)getIntent().getSerializableExtra("search_result_list");

        final Bundle bundle = getIntent().getBundleExtra("message");
        searchItem = bundle.getParcelable("search_item");
        if ( searchItem == null ) {
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView departureText = findViewById(R.id.departureText);
        TextView destinationText = findViewById(R.id.destinationText);
        TextView deptDateText = findViewById(R.id.deptDateText);
        TextView peopleText = findViewById(R.id.peopleText);
        TextView luggageText = findViewById(R.id.luggageText);

        departureText.setText(searchItem.getDeparture());
        destinationText.setText(searchItem.getDestination());

        SimpleDateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.getDefault());
        deptDateText.setText(df.format(new Date(searchItem.getDeptDate())));

        peopleText.setText(searchItem.getPeople()+"명");
        luggageText.setText(searchItem.getLuggage()+"개");

        // 리스트뷰 세팅
        SearchResultAdapter adapter = new SearchResultAdapter(this, searchResultList);
        ListView listView = findViewById(R.id.search_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Bundle bundle = new Bundle();
                bundle.putParcelable("search_item", searchResultList.get(i));

                // 탑승준비 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), ReadyBoardActivity.class);
                intent.putExtra("message", bundle);
                intent.putExtra("is_make_room", "click_room");
                intent.putExtra("search_people", searchItem.getPeople());
                intent.putExtra("search_luggage", searchItem.getLuggage());

                startActivityForResult(intent, RESERVATION_FINISH);
            }
        });

        // 방만들기 버튼
        // 현재 사용하지 않음
//        Button makeBtn = findViewById(R.id.btn_make_room);
//        makeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                // 클릭하면 검색된 정보로 DB에 insert하고 해당 정보를 바탕으로 한 탑승준비 페이지로 이동
//                SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
//                String uid = userInfo.getString("u_id", "");
//
//                MakeTask task = new MakeTask(SearchResultActivity.this);
//                task.execute("http://a.liroo.net/tripool/trip_control.php", "add", searchItem, uid);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        Log.e("SearchResultActivity", "REQ:"+requestCode+",RET:"+resultCode+",DATA:"+intent);
        if ( resultCode != RESULT_OK ) return;

        if ( requestCode == RESERVATION_FINISH ) {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemID = item.getItemId();
        if ( itemID == android.R.id.home ) {   // 뒤로
            finish();
        }
        return true;
    }

    private static class MakeTask extends AsyncTask<Object, Void, String>
    {
        private WeakReference<SearchResultActivity> activityReference;
        private SearchItem searchItem;

        // only retain a weak reference to the activity
        MakeTask(SearchResultActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Object... params)
        {
            String uri = (String)params[0];
            String type = (String)params[1];
            searchItem = (SearchItem)params[2];
            String uid = (String)params[3];

            BufferedReader bufferedReader;
            try {
                String data = "mode=" + URLEncoder.encode(type, "UTF-8");
                data += "&dept_main=" + URLEncoder.encode(searchItem.getDeptMain(), "UTF-8");
                data += "&dept_sub=" + URLEncoder.encode(searchItem.getDeptSub(), "UTF-8");
                data += "&departure=" + URLEncoder.encode(searchItem.getDeparture(), "UTF-8");
                data += "&dest_main=" + URLEncoder.encode(searchItem.getDestMain(), "UTF-8");
                data += "&dest_sub=" + URLEncoder.encode(searchItem.getDestSub(), "UTF-8");
                data += "&destination=" + URLEncoder.encode(searchItem.getDestination(), "UTF-8");
                data += "&dept_date=" + searchItem.getDeptDate() / 100000;              //DB입력할때 만 변경함
                data += "&people=" + searchItem.getPeople();
                data += "&luggage=" + searchItem.getLuggage();
                // 자동로그인 되어있으면 로그인 정보 가져와서 같이 insert하기
                data += "&book_id=" + uid;
                data += "&owner_id=" + uid;

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
            SearchResultActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

            if ( ret != null ) {
                if ( ret.contains("same_room_error") ) {
                    Toast.makeText(activity, R.string.exist_room, Toast.LENGTH_SHORT).show();
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("search_item", searchItem);

                    // 탑승준비 페이지로 이동
                    Intent intent = new Intent(activity, ReadyBoardActivity.class);
                    intent.putExtra("message", bundle);
                    intent.putExtra("is_make_room", "make_room");
                    intent.putExtra("search_people", searchItem.getPeople());
                    intent.putExtra("search_luggage", searchItem.getLuggage());
                    activity.startActivityForResult(intent, RESERVATION_FINISH);
                }
                return;
            }
            Toast.makeText(activity, R.string.make_room_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
