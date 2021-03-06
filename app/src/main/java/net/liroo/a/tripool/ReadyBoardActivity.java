package net.liroo.a.tripool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapView;

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
import java.util.Date;
import java.util.Locale;

// 탑승 준비 페이지
public class ReadyBoardActivity extends BaseActivity
{
    private View payDialog;
    private Button cancelBtn, payBtn;
    private TextView payHelpText;
    private EditText pointField;

    private String isMakeRoom, people, luggage;
    private String ownerID, uid;
    private boolean isPyaDo;

    private float fare;
    private int myPoint, payAmount;

    private static final String TAG_RESULTS = "result"; // json으로 가져오는 값의 파라메터
    private static final int BOARD_PURCHASE = 1;

    private TMapView tMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ready_board);

        Bundle bundle = getIntent().getBundleExtra("message");
        final SearchItem searchItem = bundle.getParcelable("search_item");
        if ( searchItem == null ) {
            finish();
            return;
        }
        isMakeRoom = getIntent().getStringExtra("is_make_room");
        people = getIntent().getStringExtra("search_people");
        luggage = getIntent().getStringExtra("search_luggage");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView departureText = findViewById(R.id.departureText);
        TextView destinationText = findViewById(R.id.destinationText);

        departureText.setText(searchItem.getDeparture());       // 출발 장소
        destinationText.setText(searchItem.getDestination());   // 도착 장소

        TextView deadlineTimeText = findViewById(R.id.deadlineTimeText);

        TextView stagingAreaText = findViewById(R.id.stagingAreaText);

        TextView fareText = findViewById(R.id.fareText);
        // 요금 산출
        // 일단 100m 당 70원으로 산출함 (이동거리(km) * 1000)으로해서 m 단위로 환산 -> 100m로 나눠서 70원 곱하기
        fare = ((Float.parseFloat(searchItem.getDistance()) * 1000) / 100) * 70;


        fareText.setText(String.valueOf((int)fare) + "원");             // 요금

        TextView distanceText = findViewById(R.id.distanceText);
        distanceText.setText(searchItem.getDistance() + "km");       // 이동거리

        TextView discountText = findViewById(R.id.discountText);
        TextView discountAmountText = findViewById(R.id.discountAmountText);

        TextView amountText = findViewById(R.id.amountText);
        //10원단위 절삭
        fare = ((int)fare / 100) * 100;
        amountText.setText(String.valueOf((int)fare) + "원");

        pointField = findViewById(R.id.pointField);

        // 로그인 정보
        SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        uid = userInfo.getString("u_id", "");

        // 리스트 뷰에서 넘어온 경우
        if ( !isMakeRoom.equals("make_room") ) {
            ownerID = searchItem.getOwnerID();
        }

        // tripool_info에서 같은 출발지, 도착지, 출발 시간중에서 인원수를 카운트해서 가져와야 함 -> 동승자 수에 표기하기(결제를 완료한 상태만 가져오기)
        GetFellowTask task = new GetFellowTask(this);
        task.execute("http://a.liroo.net/tripool/json_fellow_count.php", searchItem);

        // 포인트 정보 가져오기
        GetPointTask pointTask = new GetPointTask(this);
        pointTask.execute("http://a.liroo.net/tripool/json_point_control.php", uid);

        payDialog = findViewById(R.id.payDialog);
        payHelpText = findViewById(R.id.payHelpText);
        cancelBtn = findViewById(R.id.cancelBtn);
        payBtn = findViewById(R.id.payBtn);

        // Tmap 지도 api
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("021ce310-85c0-4bec-97ca-78ae3e046731");
        ViewGroup linearLayoutTmap = findViewById(R.id.map_view);
        //TODO: 일단 전남 순천 낙안읍성으로 위치 표시, 추후에 검색된 값을 가져와서 표시해야함

        tMapView.setLocationPoint(127.338955, 34.907182);
        tMapView.setCenterPoint(127.338955, 34.907182);
        tMapView.setIconVisibility(true);   //현재위치로 표시될 아이콘을 표시할지 여부를 설정
        linearLayoutTmap.addView(tMapView);

        // 결제하기 팝업창
        Button btnClose = findViewById(R.id.btn_pay);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // 키보드 숨김
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                View focusView = getCurrentFocus();
                if ( focusView == null ) {
                    focusView = new View(ReadyBoardActivity.this);
                }
                inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);

                int inputPoint = Integer.parseInt(pointField.getText().toString());
                if ( inputPoint > myPoint ) {
                    Toast.makeText(getApplicationContext(), "포인트가 부족합니다", Toast.LENGTH_SHORT).show();
                }
                else {
                    payAmount = (int)fare - inputPoint;

                    SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm (E)", Locale.getDefault());
                    String date;
                    // 방 만들기에서 넘어온 경우
                    if ( isMakeRoom.equals("make_room") ) {
                        date = df.format(new Date(searchItem.getDeptDate()));    // 출발 일시
                    }
                    // 리스트 뷰에서 넘어온 경우
                    else {
//                        date = df.format(new Date(searchItem.getDeptDate()*1000));   // 출발 일시
                        date = df.format(new Date(searchItem.getDeptDate()));   // 출발 일시
                    }

                    String payHelp = "* From : "+searchItem.getDeparture()+"\n* To : "+searchItem.getDestination();
                    payHelp += "\n* "+date+"\n\n* 예약요금 : " + String.valueOf(payAmount) + "원";
                    payHelp += "\n\n예약 하시겠습니까?";
                    payHelpText.setText(payHelp);

                    payDialog.setVisibility(View.VISIBLE);
                }
            }
        });

        // 결제하기 팝업창
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payDialog.setVisibility(View.GONE);
            }
        });

        // 결제 팝업에서 다음 버튼
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                isPyaDo = true;

                Bundle bundle = new Bundle();
                bundle.putParcelable("search_item", searchItem);
                bundle.putInt("payAmount", payAmount);

                Intent intent = new Intent(ReadyBoardActivity.this, PurchaseActivity.class);
                intent.putExtra("message", bundle);
                startActivityForResult(intent, BOARD_PURCHASE);

//                // 방만들기로 들어온 상태면 tripool_info의 결제 상태값을 변경해야 함
//                // 혹은 자신이 만든 방이면 결제의 상태값만 update 해야 함
//                if ( isMakeRoom.equals("make_room") || ownerID.equals(uid) ) {
//                    PayUpdateTask task = new PayUpdateTask(ReadyBoardActivity.this);
//                    task.execute("http://a.liroo.net/tripool/trip_control.php", searchItem, isMakeRoom, uid, ownerID);
//                }
//                // 리스트뷰에서 클릭해서 온 상태면 결제 할 때, tripool_info에 insert 하기(결제완료 상태로)
//                else {
//                    // owner_id 저장하기
//                    PayJoinTask task = new PayJoinTask(ReadyBoardActivity.this);
//                    task.execute("http://a.liroo.net/tripool/trip_control.php", searchItem, luggage, uid, ownerID);
//                }
//
//                // TODO : 배차 완료되면 화면 바뀌도록 변경 필요 (현재는 화면 전환 확인하기 위해 자동으로 변경)
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run()
//                    {
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }, 3000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if ( resultCode == RESULT_OK ) {
            switch ( requestCode ) {
                case BOARD_PURCHASE:
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if ( payDialog.getVisibility() == View.VISIBLE ) {
            payDialog.setVisibility(View.GONE);
        }
        else {
            if ( isMakeRoom.equals("make_room") || isPyaDo ) {
                setResult(RESULT_OK);
                super.onBackPressed();
            }
            else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReadyBoardActivity.this);
                alertDialogBuilder.setTitle(getString(R.string.alert));
                alertDialogBuilder
                        .setMessage(getString(R.string.not_pay_reservation_confirm))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        });
                alertDialogBuilder.show();
            }
        }
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

    private static class GetFellowTask extends AsyncTask<Object, Void, String>
    {
        private WeakReference<ReadyBoardActivity> activityReference;
        private SearchItem item;

        // only retain a weak reference to the activity
        GetFellowTask(ReadyBoardActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Object... params)
        {
            String uri = (String)params[0];
            item = (SearchItem)params[1];

            BufferedReader bufferedReader;
            try {
                String data = "mode=" + URLEncoder.encode("people_cnt", "UTF-8");
                data += "&dept_main=" + URLEncoder.encode(item.getDeptMain(), "UTF-8");
                data += "&dept_sub=" + URLEncoder.encode(item.getDeptSub(), "UTF-8");
                data += "&departure=" + URLEncoder.encode(item.getDeparture(), "UTF-8");
                data += "&dest_main=" + URLEncoder.encode(item.getDestMain(), "UTF-8");
                data += "&dest_sub=" + URLEncoder.encode(item.getDestSub(), "UTF-8");
                data += "&destination=" + URLEncoder.encode(item.getDestination(), "UTF-8");
                data += "&dept_date=" + item.getDeptDate() / 100000;              // DB입력할때 만 변경함
//                    data += "&people=" + people;
//                    data += "&luggage=" + luggage;

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
            ReadyBoardActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

            try {
                JSONObject jsonObj = new JSONObject(ret);
                JSONArray jsonList = jsonObj.getJSONArray(TAG_RESULTS);

                JSONObject obj = jsonList.getJSONObject(0);
                String togetherPeople = obj.getString("together_people");
                if ( togetherPeople.equals("null") ) {
                    togetherPeople = "0";
                }
                //동승인원수 체크
                Log.e("fellow_result", String.valueOf(ret));
                int together = Integer.parseInt(togetherPeople) + Integer.parseInt(activity.people);    // 동승인원수
                int maxPeople;
                if ( together < 9 ) {
                    maxPeople = 8;
                }
                else if ( together < 17 ) {
                    maxPeople = 16;
                }
                else {
                    maxPeople = 25;
                }

                TextView togetherPeopleText = activity.findViewById(R.id.togetherPeopleText);
                togetherPeopleText.setText(String.valueOf(together));

                TextView currentPeopleText = activity.findViewById(R.id.currentPeopleText);
                currentPeopleText.setText(String.valueOf(together));

                ProgressBar peopleProgressBar = activity.findViewById(R.id.peopleProgressBar);
                peopleProgressBar.setProgress(together);

                TextView maxPeopleText = activity.findViewById(R.id.maxPeopleText);
                maxPeopleText.setText(String.valueOf(maxPeople));
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        }
    }

    // 8월 6일에 php쪽 수정해야함 -> 현재 버그
    // 리스트뷰에서 온 경우
    private static class PayJoinTask extends AsyncTask<Object, Void, String>
    {
        private WeakReference<ReadyBoardActivity> activityReference;
        private SearchItem item;

        // only retain a weak reference to the activity
        PayJoinTask(ReadyBoardActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Object... params)
        {
            String uri = (String)params[0];
            item = (SearchItem)params[1];
            String people = (String)params[2];
            String luggage = (String)params[3];
            String uid = (String)params[4];
            String ownerID = (String)params[5];

            BufferedReader bufferedReader;
            try {
                String data = "mode=" + URLEncoder.encode("pay_add", "UTF-8");
                data += "&dept_main=" + URLEncoder.encode(item.getDeptMain(), "UTF-8");
                data += "&dept_sub=" + URLEncoder.encode(item.getDeptSub(), "UTF-8");
                data += "&departure=" + URLEncoder.encode(item.getDeparture(), "UTF-8");
                data += "&dest_main=" + URLEncoder.encode(item.getDestMain(), "UTF-8");
                data += "&dest_sub=" + URLEncoder.encode(item.getDestSub(), "UTF-8");
                data += "&destination=" + URLEncoder.encode(item.getDestination(), "UTF-8");
                data += "&dept_date=" + item.getDeptDate(); // DB 입력할때만 변경함
                data += "&people=" + people;
                data += "&luggage=" + luggage;
                data += "&book_id=" + URLEncoder.encode(uid, "UTF-8");
                data += "&owner_id=" + URLEncoder.encode(ownerID, "UTF-8");

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
            ReadyBoardActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

            try {
                JSONObject jsonObj = new JSONObject(ret);
                JSONArray jsonList = jsonObj.getJSONArray(TAG_RESULTS);
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        }
    }

    // 방 만들기에서 온 경우 혹은 자신이 만든 방일경우
    private static class PayUpdateTask extends AsyncTask<Object, Void, String>
    {
        private WeakReference<ReadyBoardActivity> activityReference;
        private SearchItem item;

        // only retain a weak reference to the activity
        PayUpdateTask(ReadyBoardActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Object... params)
        {
            String uri = (String)params[0];
            item = (SearchItem)params[1];
            String isMakeRoom = (String)params[2];
            String uid = (String)params[3];
            String ownerID = (String)params[4];

            BufferedReader bufferedReader;
            try {
                String data = "mode=" + URLEncoder.encode("pay_update", "UTF-8");
                data += "&dept_main=" + URLEncoder.encode(item.getDeptMain(), "UTF-8");
                data += "&dept_sub=" + URLEncoder.encode(item.getDeptSub(), "UTF-8");
                data += "&departure=" + URLEncoder.encode(item.getDeparture(), "UTF-8");
                data += "&dest_main=" + URLEncoder.encode(item.getDestMain(), "UTF-8");
                data += "&dest_sub=" + URLEncoder.encode(item.getDestSub(), "UTF-8");
                data += "&destination=" + URLEncoder.encode(item.getDestination(), "UTF-8");
                if ( isMakeRoom.equals("make_room") ) {
                    data += "&dept_date=" + item.getDeptDate() / 100000;    // DB입력할 때만 변경함
                    data += "&owner_id=" + uid;
                }
                else {
                    data += "&dept_date=" + item.getDeptDate(); // DB입력할 때만 변경함
                    data += "&owner_id=" + ownerID;
                }
                data += "&book_id=" + uid;
                data += "&is_make_room=" + isMakeRoom;

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
            ReadyBoardActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

            try {
                JSONObject jsonObj = new JSONObject(ret);
                JSONArray jsonList = jsonObj.getJSONArray(TAG_RESULTS);
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        }
    }

    private static class GetPointTask extends AsyncTask<Object, Void, String>
    {
        private WeakReference<ReadyBoardActivity> activityReference;

        // only retain a weak reference to the activity
        GetPointTask(ReadyBoardActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Object... params)
        {
            String uri = (String)params[0];
            String uid = (String)params[1];

            BufferedReader bufferedReader;
            try {
                String data = "mode=" + URLEncoder.encode("get", "UTF-8");
                data += "&u_id=" + URLEncoder.encode(uid, "UTF-8");

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
            ReadyBoardActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

            try {
                JSONObject jsonObj = new JSONObject(ret);

                String point = String.valueOf(jsonObj.get(TAG_RESULTS));
                activity.myPoint = Integer.parseInt(point);
                //포인트체크
//                Log.e("point_result", String.valueOf(ret));

                TextView pointText = activity.findViewById(R.id.pointText);
                pointText.setText("포인트 사용 (" + point + "점) : ");
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        }
    }
}
