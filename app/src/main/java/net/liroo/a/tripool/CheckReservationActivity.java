package net.liroo.a.tripool;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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

public class CheckReservationActivity extends BaseActivity {

    private ArrayList<SearchItem> reservationList;
    private String phone;

    private ProgressDialog loading;
    String myJSON;
    private static final String TAG_RESULTS="result";                       //json으로 가져오는 값의 파라메터
    JSONArray json_resv_list = new JSONArray();                             //예매 목록, DB에서 가져온 값
    private String u_id;

    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_reservation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO: 테스트 하기 위한 임시 데이터이므로 데이터 변경 필요
        reservationList = new ArrayList<>();
//        reservationList.add(new SearchItem());

        //자동로그인 되어있으면 로그인된 아이디 세팅
        SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        u_id = userInfo.getString("u_id", "");
        //예약 확인 목록을 DB에서 가져와서 세팅함
        searchResvData("http://a.liroo.net/tripool/json_resv_list.php");
    }

    public void callToDriver(String phone)
    {
        this.phone = phone;

        // CALL_PHONE 권한 체크 (사용권한이 없을 경우 : -1)
        if ( ContextCompat.checkSelfPermission(CheckReservationActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {
            // 권한이 없을 경우, 요청
            ActivityCompat.requestPermissions(CheckReservationActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CALL_PHONE);
        }
        else {
            // TODO: 전화번호 설정
            // 사용 권한이 있을 경우
//            String tel = "tel:" + phone;
//            Intent intent = new Intent(Intent.ACTION_CALL);
//            intent.setData(Uri.parse(tel));
//            context.startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
            switch ( requestCode ) {
                case PERMISSIONS_REQUEST_CALL_PHONE:
                    // TODO: 전화번호 설정
                    // 사용 권한이 있을 경우
//                    String tel = "tel:" + phone;
//                    Intent intent = new Intent(Intent.ACTION_CALL);
//                    intent.setData(Uri.parse(tel));
//                    context.startActivity(intent);
                    break;
            }
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.plz_accept_permission, Toast.LENGTH_SHORT).show();
        }
    }


    //검색결과 DB에서 가져옴
    public void searchResvData(String url) {

        AsyncTask<Object, Void, String> task = new AsyncTask<Object, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String result){
                myJSON=result;
//                Log.e("RESV_LIST", result);
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    json_resv_list = jsonObj.getJSONArray(TAG_RESULTS);

//                    reservationList = new ArrayList<>();
                    for ( int i=0; i<json_resv_list.length(); i++ ) {
                        JSONObject obj = json_resv_list.getJSONObject(i);
                        reservationList.add(new SearchItem(obj));
                    }
                    //리스트뷰 세팅
                    CheckReservationAdapter adapter = new CheckReservationAdapter(CheckReservationActivity.this, reservationList);
                    ListView reservationListView = findViewById(R.id.reservationListView);
                    reservationListView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                loading.dismiss();
            }
            @Override
            protected String doInBackground(Object... params) {
                String uri = (String)params[0];

                BufferedReader bufferedReader = null;
                try {

                    String data = "book_id=" + URLEncoder.encode(u_id, "UTF-8");

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
