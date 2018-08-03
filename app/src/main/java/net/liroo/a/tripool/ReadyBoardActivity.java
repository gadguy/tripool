package net.liroo.a.tripool;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.List;
import java.util.Locale;

//탑승 준비 페이지
public class ReadyBoardActivity extends BaseActivity {

    String myJSON;
    private static final String TAG_RESULTS="result";                       //json으로 가져오는 값의 파라메터
    JSONArray json_list = new JSONArray();                                  //php에서 넘겨받는 값
    private List<String> list = new ArrayList<>();          // 데이터를 넣은 리스트변수
    private String departure;
    private String destination;
    private long deptDate;
    private String people;
    private String luggage;

    // ----------------------------------------------------------------------------------------
    // 수정
    private View payDialog, cannotPayAlert, scheduleIngAlert, scheduleFinishAlert;
    private Button cancelBtn, nextBtn, checkReservationBtn;
    // ----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readyboard);

        Bundle bundle = getIntent().getBundleExtra("message");
        final SearchItem searchItem = bundle.getParcelable("search_item");
        // ----------------------------------------------------------------------------------------
        // 수정
        SearchResultItem searchResultItem = bundle.getParcelable("search_result_item");
        if ( searchItem == null && searchResultItem == null ) {
            finish();
            return;
        }
        // ----------------------------------------------------------------------------------------
        final String is_make_room = getIntent().getStringExtra("is_make_room");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ----------------------------------------------------------------------------------------
        // 수정
        payDialog = findViewById(R.id.payDialog);
        cannotPayAlert = findViewById(R.id.cannotPayAlert);
        cancelBtn = findViewById(R.id.cancelBtn);
        nextBtn = findViewById(R.id.nextBtn);
        scheduleIngAlert = findViewById(R.id.scheduleIngAlert);
        scheduleFinishAlert = findViewById(R.id.scheduleFinishAlert);
        checkReservationBtn = findViewById(R.id.checkReservationBtn);
        // ----------------------------------------------------------------------------------------

        TextView departureText = findViewById(R.id.departureText);
        TextView destinationText = findViewById(R.id.destinationText);
        TextView deptDateText = findViewById(R.id.deptDateText);
        TextView peopleText = findViewById(R.id.peopleText);
        TextView luggageText = findViewById(R.id.luggageText);
        TextView departurePointText = findViewById(R.id.departurePointText);

        //방 만들기 하면 정보를 가져오지 못함...
        if ( searchItem != null ) {
            departurePointText.setText(searchItem.getDeptMain() + " " + searchItem.getDeptSub());      //출발 도 + 시
            departureText.setText(searchItem.getDeparture());       //출발 장소
            destinationText.setText(searchItem.getDestination());   //도착 장소

            SimpleDateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 (E)", Locale.getDefault());
            deptDateText.setText(df.format(new Date(searchItem.getDeptDate() * 1000)));        //출발 일시

            peopleText.setText(searchItem.getPeople() + "명");             //인원수
//        luggageText.setText(searchItem.getLuggage()+"개");           //짐 개수
        }

        //tripool_info에서 같은 출발지, 도착지, 출발 시간중에서 인원수를 카운트해서 가져와야 함 -> 동승자 수에 표기하기(결제를 완료한 상태만 가져오기)
//        getFellowCount("json_fellow_count.php", searchItem);




        //TODO: 방 만들기를 통해서 들어왔다면, 뒤로 가기 누를 때 메인 페이지로 넘어감
        if ( is_make_room.equals("make_room")) {


        }
        //TODO: 리스트뷰를 통해서 들어왔다면, 결제를 하지 않고 뒤로가기를 누르면 예약이 안된다는 메시지 띄우기 -> 검색 결과 페이지로 넘어감
        else {


        }


        //결제하기 버튼 클릭 -> 결제화면으로 이동
        Button btnClose = (Button) findViewById(R.id.btn_pay);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //방만들기로 들어온 상태면 tripool_info의 결제 상태값을 변경해야 함
                if ( is_make_room.equals("make_room")) {


                }
                //리스트뷰에서 클릭해서 온 상태면 결제 할 때, tripool_info에 insert 하기(결제완료 상태로)
                else {


                }

                //결제화면으로 이동
                Intent intent = new Intent(getApplicationContext(), PayActivity.class);
                startActivity(intent);  //다음 화면으로 넘어가기

                payDialog.setVisibility(View.VISIBLE);
                cannotPayAlert.setVisibility(View.VISIBLE);
            }
        });

        // ----------------------------------------------------------------------------------------
        // 수정
        // 결제하기 팝업창
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payDialog.setVisibility(View.GONE);
                cannotPayAlert.setVisibility(View.VISIBLE);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cannotPayAlert.setVisibility(View.GONE);
                scheduleIngAlert.setVisibility(View.VISIBLE);

                // TODO : 배차 완료되면 화면 바뀌도록 변경 필요 (현재는 화면 전환 확인하기 위해 자동으로 변경)
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scheduleIngAlert.setVisibility(View.GONE);
                        scheduleFinishAlert.setVisibility(View.VISIBLE);
                    }
                }, 3000);
            }
        });

        checkReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 예약 확인 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), CheckReservationActivity.class);
                startActivity(intent);

                finish();
            }
        });

        // ----------------------------------------------------------------------------------------

    }

    // ----------------------------------------------------------------------------------------
    // 수정
    @Override
    public void onBackPressed()
    {
        if ( payDialog.getVisibility() == View.VISIBLE && cannotPayAlert.getVisibility() == View.VISIBLE ) {
            payDialog.setVisibility(View.GONE);
        }
        else {
            super.onBackPressed();
        }
    }
    // ----------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemID = item.getItemId();
        if ( itemID == android.R.id.home ) {   // 뒤로
            finish();
        }
        return true;
    }
    //현재 동승자 수를 DB에서 가져옴
    public void getFellowCount(String url, final SearchItem item) {

        AsyncTask<Object, Void, String> task = new AsyncTask<Object, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    json_list = jsonObj.getJSONArray(TAG_RESULTS);

//                    Log.e("test", "json_dept_list : "+json_dept_list.length());

//                    ArrayList<SearchItem> searchList = new ArrayList<>();
//                    for ( int i=0; i<json_list.length(); i++ ) {
                        JSONObject obj = json_list.getJSONObject(0);
                        String together_people =  obj.getString("together_people");
//                        searchList.add(new SearchItem(obj));
//                    }
                    TextView togetherPeopleText = findViewById(R.id.togetherPeopleText);
                    togetherPeopleText.setText(together_people+"명");             //인원수

//                    Bundle bundle = new Bundle();
//                    bundle.putParcelable("search_result_item", item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Log.e("json_arrayList", String.valueOf(dept_list));
//                loading.dismiss();
            }
            @Override
            protected String doInBackground(Object... params) {

                String uri = (String)params[0];
//                String main_addr = params[1];
//                String sub_addr = params[2];
//                String station = params[3];

                BufferedReader bufferedReader = null;
                try {

                    String data = "mode=" + URLEncoder.encode("people_cnt", "UTF-8");
                    data += "&dept_main=" + URLEncoder.encode(item.getDeptMain(), "UTF-8");
                    data += "&dept_sub=" + URLEncoder.encode(item.getDeptSub(), "UTF-8");
                    data += "&departure=" + URLEncoder.encode(item.getDeparture(), "UTF-8");
                    data += "&dest_main=" + URLEncoder.encode(item.getDestMain(), "UTF-8");
                    data += "&dest_sub=" + URLEncoder.encode(item.getDestSub(), "UTF-8");
                    data += "&destination=" + URLEncoder.encode(item.getDestination(), "UTF-8");
                    data += "&dept_date=" + item.getDeptDate() / 1000;              //DB입력할때 만 변경함
//                    data += "&people=" + item.getPeople();
//                    data += "&luggage=" + item.getLuggage();
                    //자동로그인 되어있으면 로그인 정보 가져와서 같이 insert하기
//                    SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
//                    String u_id = userInfo.getString("u_id", "");
//                    data += "&book_id=" + u_id;


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
                } catch(Exception e) {
                    return null;
                }
            }
        };
        task.execute(url);
    }

    //리스트뷰에서 온 경우, list view에서 받아온 정보를 토대로 결제할때 DB에 insert 함


    //방 만들기에서 온 경우, 검색 결과 값을 토대로 해당 DB를 update 함



}
