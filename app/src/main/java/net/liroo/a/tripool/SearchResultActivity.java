package net.liroo.a.tripool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//검색 결과 페이지
public class SearchResultActivity extends BaseActivity {

    String myJSON;
    private static final String TAG_RESULTS="result";                       //json으로 가져오는 값의 파라메터
    JSONArray json_result_array = new JSONArray();                             //php에 insert하고 DB에서 가져온 값

    private ListView listView;          // 검색을 보여줄 리스트변수
    private Button makeBtn;
    private final static int RESERVATION_FINISH = 1;
    // ----------------------------------------------------------------------------------------
    // 수정
    private SearchItem searchItem;
    // ----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        final ArrayList<SearchItem> searchList = (ArrayList<SearchItem>)getIntent().getSerializableExtra("search_list");

//        Log.e("searchList", String.valueOf(searchList));

        final Bundle bundle = getIntent().getBundleExtra("message");
        // ----------------------------------------------------------------------------------------
        // 수정
        searchItem = bundle.getParcelable("search_item");
        // ----------------------------------------------------------------------------------------
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

        //리스트뷰 세팅
        SearchResultAdapter adapter = new SearchResultAdapter(this, searchList);
        listView = (ListView)findViewById(R.id.search_list);
        listView.setAdapter(adapter);

        //리스트뷰 클릭할 경우
        //TODO:클릭한 정보만 탑승준비 페이지로 넘겨야 함
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("search_item", searchList.get(i));
//               searchItem.setLuggage(searchItem.getLuggage());
//               searchItem.setPeople(searchItem.getPeople());

                //탑승준비 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), ReadyBoardActivity.class);
                intent.putExtra("message", bundle);
                intent.putExtra("is_make_room", "click_room");
                intent.putExtra("search_luggage", searchItem.getLuggage());
                intent.putExtra("search_people", searchItem.getPeople());

//                startActivity(intent);  //다음 화면으로 넘어가기
                startActivityForResult(intent, RESERVATION_FINISH);
            }
        });

        //방만들기 버튼
        makeBtn = findViewById(R.id.btn_make_room);
        //클릭하면 검색된 정보로 DB에 insert하고 해당 정보를 바탕으로 한 탑승준비 페이지로 이동
        makeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //php 통해서 DB에 insert하는 메소드
                //book_id, book_no, dept_main, dept_sub, departure, dest_main, dest_sub, destination, dept_date 보내야함
                //json_encode(array('result'=>$result)); 로 결과 받음

                // ----------------------------------------------------------------------------------------
                // 수정
                //클릭할 때, 검색된 정보를 이용하여 방을 만듬
                insertSearchInfo("http://a.liroo.net/tripool/trip_control.php", "add", searchItem);
                // ----------------------------------------------------------------------------------------
            }
        });
    }

    //방 만들기
    public void insertSearchInfo(String url, final String type, final SearchItem item){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SearchResultActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String result){
                myJSON=result;
//                Log.e("json_result", result);
                // ----------------------------------------------------------------------------------------
                // 수정
                if ( result.contains("same_room_error") ) {
                    Toast.makeText(getApplicationContext(), "같은 시간대에 만드신 방이 있습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        JSONObject jsonObj = new JSONObject(myJSON);
                        json_result_array = jsonObj.getJSONArray(TAG_RESULTS);
//                        Log.e("json_result_add", String.valueOf(json_result_array));

                        Bundle bundle = new Bundle();
                        bundle.putParcelable("search_item", searchItem);

                        Intent intent = new Intent(getApplicationContext(), ReadyBoardActivity.class);
                        intent.putExtra("message", bundle);
                        intent.putExtra("is_make_room", "make_room");
                        //                intent.putParcelableArrayListExtra("search_result", searchList);
                        //                startActivity(intent);  //다음 화면으로 넘어가기
                        startActivityForResult(intent, RESERVATION_FINISH);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // ----------------------------------------------------------------------------------------

                loading.dismiss();
            }
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String type = params[1];

                BufferedReader bufferedReader = null;
                try {
                    //php로 보낼 데이터 세팅
                    //book_id, book_no, dept_main, dept_sub, departure, dest_main, dest_sub, destination, dept_date 보내야함
//                    String data = URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
                    String data = "mode=" + URLEncoder.encode(type, "UTF-8");
                    data += "&dept_main=" + URLEncoder.encode(item.getDeptMain(), "UTF-8");
                    data += "&dept_sub=" + URLEncoder.encode(item.getDeptSub(), "UTF-8");
                    data += "&departure=" + URLEncoder.encode(item.getDeparture(), "UTF-8");
                    data += "&dest_main=" + URLEncoder.encode(item.getDestMain(), "UTF-8");
                    data += "&dest_sub=" + URLEncoder.encode(item.getDestSub(), "UTF-8");
                    data += "&destination=" + URLEncoder.encode(item.getDestination(), "UTF-8");
                    data += "&dept_date=" + item.getDeptDate() / 100000;              //DB입력할때 만 변경함
                    data += "&people=" + item.getPeople();
                    data += "&luggage=" + item.getLuggage();
//                    Log.e("DB RESULT", String.valueOf(data));

                    //자동로그인 되어있으면 로그인 정보 가져와서 같이 insert하기
                    SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
                    String u_id = userInfo.getString("u_id", "");
                    data += "&book_id=" + u_id;
                    data += "&owner_id=" + u_id;


                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url, type);
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
}
