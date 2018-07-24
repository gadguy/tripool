package net.liroo.a.tripool;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    String myJSON;
    private static final String TAG_RESULTS="result";
    JSONArray json_dept_list = new JSONArray();
    ArrayList<String> dept_list = new ArrayList<>();
    ArrayList<String> dept_station_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        MapView mapView = new MapView(this);
        mapView.setDaumMapApiKey("3c8e3fff3053a6bb1ae42fc8b5fbd761");
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);


        //길찾기 url scheme 관련, 안될 경우 위치 표시만 한다
//        String url = "daummaps://route?sp="+USER Latitude+","+USER Longitude+"&ep="+ARRIVAL Latitude+","+ARRIVAL Longitude+"&by=CAR";
//        String url = "daummaps://route?sp=37.537229,127.005515&ep=37.4979502,127.0276368&by=CAR";
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        startActivity(intent);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }
    //출발지 입력하는 다이얼로그
    public void btnLayerFrom(View view) {

        Spinner dept_spinner;
        Spinner dept_station_spinner;

        // Dialog 다이얼로그 클래스로 다이얼로그를 만든다
        final Dialog layerForm = new Dialog(this); // 다이얼로그 객체 생성
        layerForm.setTitle("목적지 검색");
        layerForm.setContentView(R.layout.map_find); // 다이얼로그 화면 등록

//        List<String> data = new ArrayList<>();
//        data.add("가짜 데이터 1"); data.add("가짜 데이터 2"); data.add("가짜 데이터 3"); data.add("가짜 데이터 4"); data.add("가짜 데이터 5");
//        data.add("가짜 데이터 6"); data.add("가짜 데이터 7"); data.add("가짜 데이터 8"); data.add("가짜 데이터 9"); data.add("가짜 데이터 10");

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, (ArrayList) dept_list);
        dept_spinner = (Spinner) layerForm.findViewById(R.id.spinner_dept);
        dept_spinner.setAdapter(adapter);
        ArrayAdapter adapter2 = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, (ArrayList) dept_station_list);
        dept_station_spinner = (Spinner) layerForm.findViewById(R.id.spinner_dept_station);
        dept_station_spinner.setAdapter(adapter2);

        layerForm.show(); // 다이얼로그 띄우기

        //json 데이터를 활용한 스피너 생성
        TextView main_addr, station;
        getData("http://a.liroo.net/tripool/json_dept_list.php", "dept_list");

        // Activity 에 Dialog 를 등록하기
        layerForm.setOwnerActivity(MainActivity.this);

        //종료할 것인지 여부 true: 다이얼로그 종료, false : 종료안됨
//        layerForm.setCanceledOnTouchOutside(true); // 다이얼로그 바깥 영역을 클릭시

        //다이얼로그 닫기 버튼
        Button btnClose = (Button) layerForm.findViewById(R.id.btnCancel);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layerForm.dismiss();   //다이얼로그를 닫는 메소드입니다.
            }
        });
    }
    //도착 다이얼로그
    public void btnLayerTo(View view) {

        Toast.makeText(getApplicationContext(), "도착 다이얼로그 레이아웃 할거임", Toast.LENGTH_SHORT).show();
    }

    //검색 결과 db에서 가져오고 액티비티 이동
    public void btnSearch(View view) {






        Toast.makeText(getApplicationContext(), "검색 기능, 페이지 이동", Toast.LENGTH_SHORT).show();


    }
    //검색결과 DB에서 가져옴
    public void searchData(String url, String main_addr, String sub_addr, String station) {
        class GetDataJSON extends AsyncTask<String, Void, String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    json_dept_list = jsonObj.getJSONArray(TAG_RESULTS);
//                    for(int i=0;i<json_dept_list.length();i++){
//                        JSONObject item = json_dept_list.getJSONObject(i);
//
//                        String main_addr = item.getString("main_addr");
//                        String sub_addr = item.getString("sub_addr");
//                        String station = item.getString("station");
//
//                        dept_list.add(main_addr+ " " + sub_addr);
//                        dept_station_list.add(station);
//                    }

                    //검색 결과 페이지로 이동
                    Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                    intent.putExtra("search_list", String.valueOf(json_dept_list));
                    startActivity(intent);  //다음 화면으로 넘어가기




                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Log.e("json_arrayList", String.valueOf(dept_list));
                loading.dismiss();
            }
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String main_addr = params[1];
                String sub_addr = params[2];
                String station = params[3];

                BufferedReader bufferedReader = null;
                try {

                    String data = URLEncoder.encode("main_addr", "UTF-8") + "=" + URLEncoder.encode(main_addr, "UTF-8");
                    data += "&" + URLEncoder.encode("sub_addr", "UTF-8") + "=" + URLEncoder.encode(sub_addr, "UTF-8");
                    data += "&" + URLEncoder.encode("station", "UTF-8") + "=" + URLEncoder.encode(station, "UTF-8");

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
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url, main_addr, sub_addr, station);
    }

    public void getData(String url, final String type){
        class GetDataJSON extends AsyncTask<String, Void, String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    json_dept_list = jsonObj.getJSONArray(TAG_RESULTS);
                    if ( type.equals("dept_list") ) {
                        setData(json_dept_list);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Log.e("json_arrayList", String.valueOf(dept_list));
                loading.dismiss();
            }
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

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

    public void setData(JSONArray list) throws JSONException {
        for(int i=0;i<list.length();i++){
            JSONObject item = list.getJSONObject(i);

            String main_addr = item.getString("main_addr");
            String sub_addr = item.getString("sub_addr");
            String station = item.getString("station");

            dept_list.add(main_addr+ " " + sub_addr);
            dept_station_list.add(station);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            Toast.makeText(getApplicationContext(), "세팅버튼 입니다. 추후 개발 예정", Toast.LENGTH_LONG).show();

            SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
            String u_id = userInfo.getString("u_id", "");
            Toast.makeText(getApplicationContext(), u_id, Toast.LENGTH_SHORT).show();

            return true;
        }
        if (id == R.id.action_logout) {
            SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
            SharedPreferences.Editor userEdit = userInfo.edit();
            userEdit.clear();
            userEdit.commit();

            Toast.makeText(getApplicationContext(), "로그아웃 됩니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);  //다음 화면으로 넘어가기
        }




        return super.onOptionsItemSelected(item);
    }
}
