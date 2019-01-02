package net.liroo.a.tripool;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import net.liroo.a.tripool.adapter.CheckReservationAdapter;
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
import java.util.ArrayList;

public class CheckReservationActivity extends BaseActivity
{
    private ArrayList<SearchItem> reservationList;
    private CheckReservationAdapter adapter;

    private String uid, driverPhone;

    private static final String TAG_RESULTS = "result"; // json으로 가져오는 값의 파라메터
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_reservation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO: 테스트 하기 위한 임시 데이터이므로 데이터 변경 필요
        reservationList = new ArrayList<>();
        ListView reservationListView = findViewById(R.id.reservationListView);
        adapter = new CheckReservationAdapter(CheckReservationActivity.this, reservationList);
        reservationListView.setAdapter(adapter);

        // 로그인 정보
        SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        uid = userInfo.getString("u_id", "");

        // 예약 확인 목록
        GetReservationTask task = new GetReservationTask(this);
        task.execute("http://a.liroo.net/tripool/json_resv_list.php", uid);
    }

    public void callToDriver(String phone)
    {
        this.driverPhone = phone;

        // CALL_PHONE 권한 체크 (사용권한이 없을 경우 : -1)
        if ( ContextCompat.checkSelfPermission(CheckReservationActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {
            // 권한이 없을 경우, 요청
            ActivityCompat.requestPermissions(CheckReservationActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CALL_PHONE);
        }
        else {
            // TODO: 전화번호 설정
            // 사용 권한이 있을 경우
//            String tel = "tel:" + driverPhone;
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
//                    String tel = "tel:" + driverPhone;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemID = item.getItemId();
        if ( itemID == android.R.id.home ) {   // 뒤로
            finish();
        }
        return true;
    }

    private static class GetReservationTask extends AsyncTask<Object, Void, String>
    {
        private WeakReference<CheckReservationActivity> activityReference;

        // only retain a weak reference to the activity
        GetReservationTask(CheckReservationActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Object... params)
        {
            String uri = (String)params[0];
            String uid = (String)params[1];

            BufferedReader bufferedReader;
            try {
                String data = "book_id=" + URLEncoder.encode(uid, "UTF-8");

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
            CheckReservationActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

            try {
                JSONObject jsonObj = new JSONObject(ret);
                JSONArray jsonResvList = jsonObj.getJSONArray(TAG_RESULTS);

                activity.reservationList.clear();
                for ( int i=0; i<jsonResvList.length(); i++ ) {
                    JSONObject obj = jsonResvList.getJSONObject(i);
                    activity.reservationList.add(new SearchItem(obj));
                }
                activity.adapter.notifyDataSetChanged();
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        }
    }
}
