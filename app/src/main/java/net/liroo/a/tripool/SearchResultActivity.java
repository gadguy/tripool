package net.liroo.a.tripool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

//검색 결과 페이지
public class SearchResultActivity extends BaseActivity {

    String myJSON;
    private static final String TAG_RESULTS="result";                       //json으로 가져오는 값의 파라메터
    JSONArray json_result_array = new JSONArray();                             //php에 insert하고 DB에서 가져온 값

    private List<String> list = new ArrayList<>();          // 데이터를 넣은 리스트변수
    private List<String> clickedList = new ArrayList<>();          // 리스트뷰에서 클릭한 아이템의 데이터
    private ListView listView;          // 검색을 보여줄 리스트변수
    private Button makeBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        final ArrayList<SearchItem> searchList = (ArrayList<SearchItem>)getIntent().getSerializableExtra("search_list");

        //검색 결과로 목록 출력하기
        Log.e("test", "searchList : "+searchList.size());

        //MainActivity에서 받은 정보를 가져옴
        //TODO: From, To, 출발시간, 준비완료 카운트도 추가해야 함
        //From, To는 장소만 표시해줌
        //방 만들 때는 From의 지역,장소, To의 지역장소, 출발날짜, 출발시간이 필요 함 -> 2차원 배열? or 오브젝트?
        for ( int i=0; i<searchList.size(); i++ ) {
            Log.d("test_search_result", searchList.get(i).getDeptMain());
            list.add(searchList.get(i).getDeptMain());

        }
        //리스트뷰 세팅
        //TODO: 상단에 컬럼명 표시해줘야 함
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list) ;
        listView =(ListView)findViewById(R.id.search_list);
        listView.setAdapter(adapter);

        //리스트뷰 클릭할 경우
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //클릭한 정보만 탑승준비 페이지로 넘김
//                clickedList.add(searchList.get(i).getDeptMain());


                //탑승준비 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), ReadyBoardActivity.class);
////                intent.putParcelableArrayListExtra("search_result", clickedList);
//                intent.putStringArrayListExtra("search_result", (ArrayList<String>) clickedList);
                startActivity(intent);  //다음 화면으로 넘어가기

//                Toast.makeText(SearchResultActivity.this ,searchList.get(i).getDeptMain(),Toast.LENGTH_LONG).show();
            }
        });



        //방만들기 버튼
        makeBtn = findViewById(R.id.btn_make_room);
        //클릭하면 검색된 정보로 DB에 insert하고 해당 정보를 바탕으로 한 결제페이지로 이동
        makeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //php 통해서 DB에 insert하는 메소드
                //book_id, book_no, dept_main, dept_sub, departure, dest_main, dest_sub, destination, dept_date 보내야함
                //json_encode(array('result'=>$result)); 로 결과 받음


                //클릭할 때, 위의 정보를 받아서 아래 함수로 넘겨야 함 -> 결제하기를 하면 방이 만들어져야 함
                insertSearchInfo("http://a.liroo.net/tripool/trip_control.php", "add");


//                Toast.makeText(getApplicationContext(), "방 만들고, 탑승준비 페이지로 이동", Toast.LENGTH_SHORT).show();
                //탑승준비 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), ReadyBoardActivity.class);
//                intent.putParcelableArrayListExtra("search_result", searchList);
                startActivity(intent);  //다음 화면으로 넘어가기


            }
        });


    }

    //지역, 장소를 json타입으로 php DB에서 가져옴
    public void insertSearchInfo(String url, final String type){
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
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    json_result_array = jsonObj.getJSONArray(TAG_RESULTS);
//                    if ( type.equals("add") ) {
//                        setData(json_dept_list);
//                        Intent intent = new Intent(getApplicationContext(), ReadyBoardActivity.class);
//                        intent.putParcelableArrayListExtra("search_result", searchList);
//                        startActivity(intent);  //다음 화면으로 넘어가기
//                    } else {
//                        setPlaceData(json_dept_list);

//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Log.e("json_result_array", String.valueOf(json_result_array));
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
                    String data = URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
                    data += "&" + URLEncoder.encode("dept_main", "UTF-8") + "=" + URLEncoder.encode("전북", "UTF-8");
                    data += "&" + URLEncoder.encode("dept_sub", "UTF-8") + "=" + URLEncoder.encode("전주", "UTF-8");
                    data += "&" + URLEncoder.encode("departure", "UTF-8") + "=" + URLEncoder.encode("전주한옥마을", "UTF-8");
//                    data += "&" + URLEncoder.encode("dept_main", "UTF-8") + "=" + URLEncoder.encode(searchList.get(0).getDeptMain(), "UTF-8");
//                    data += "&" + URLEncoder.encode("dept_sub", "UTF-8") + "=" + URLEncoder.encode(searchList.get(0).getDeptSub(), "UTF-8");
//                    data += "&" + URLEncoder.encode("departure", "UTF-8") + "=" + URLEncoder.encode(searchList.get(0).getDeparture(), "UTF-8");


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





}
