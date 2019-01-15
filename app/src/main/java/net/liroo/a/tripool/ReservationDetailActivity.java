package net.liroo.a.tripool;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;


import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
//import com.skt.Tmap.TMapView;

import net.liroo.a.tripool.obj.ReservationItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReservationDetailActivity extends BaseActivity
{
    private View evaluationDialog;
    private TextView reservationDateText;
    private RatingBar evaluationRatingBar;

    private TMapView tMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getBundleExtra("message");
        final ReservationItem reservationItem = bundle.getParcelable("reservationItem");
        if ( reservationItem == null ) {
            finish();
            return;
        }
        boolean isHistory = bundle.getBoolean("isHistory");

        TextView toolbarText = findViewById(R.id.toolbarText);
        if ( !isHistory ) {
            toolbarText.setText("예약 확인");
        }
        else {
            toolbarText.setText("이용 내역");
        }

        // Tmap 지도 api
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("021ce310-85c0-4bec-97ca-78ae3e046731");
        ViewGroup linearLayoutTmap = findViewById(R.id.map_view);
        //TODO: 일단 전남 순천 낙안읍성으로 위치 표시, 추후에 검색된 값을 가져와서 표시해야함
        tMapView.setLocationPoint(127.338955, 34.907182);
        tMapView.setCenterPoint(127.338955, 34.907182);
        tMapView.setIconVisibility(true);   //현재위치로 표시될 아이콘을 표시할지 여부를 설정
        linearLayoutTmap.addView(tMapView);


        ProgressBar peopleProgressBar = findViewById(R.id.peopleProgressBar);

        TextView currentPeopleText = findViewById(R.id.currentPeopleText);
        TextView maxPeopleText = findViewById(R.id.maxPeopleText);

        View deadlineArea = findViewById(R.id.deadlineArea);
        TextView deadlineTimeText = findViewById(R.id.deadlineTimeText);

        TextView departureText = findViewById(R.id.departureText);
        TextView destinationText = findViewById(R.id.destinationText);
        TextView dateText = findViewById(R.id.dateText);
        TextView timeText = findViewById(R.id.timeText);
        TextView carCodeText = findViewById(R.id.carCodeText);

        departureText.setText(reservationItem.getDeparture());  // 출발지
        destinationText.setText(reservationItem.getDestination());  // 목적지
        // 날짜
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd (E)", Locale.getDefault());
        dateText.setText(df.format(reservationItem.getDeptDate() * 1000));
        // 시간
        df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeText.setText(df.format(reservationItem.getDeptDate() * 1000));

        TextView reservationCodeText = findViewById(R.id.reservationCodeText);

        TextView stagingAreaText = findViewById(R.id.stagingAreaText);

        TextView fareText = findViewById(R.id.fareText);
        TextView distanceText = findViewById(R.id.distanceText);
        TextView togetherPeopleText = findViewById(R.id.togetherPeopleText);

        TextView discountText = findViewById(R.id.discountText);
        TextView discountAmountText = findViewById(R.id.discountAmountText);

        TextView amountText = findViewById(R.id.amountText);
        TextView needAmountText = findViewById(R.id.needAmountText);

        final Button showEvaluationBtn = findViewById(R.id.showEvaluationBtn);
        // 기사님 평가하기
        if ( reservationItem.isDoEvaluation() ) {
            showEvaluationBtn.setEnabled(false);
            showEvaluationBtn.setBackgroundColor(Color.GRAY);
            showEvaluationBtn.setText(R.string.driver_evaluation_complete);
        }
        else {
            showEvaluationBtn.setEnabled(true);
            showEvaluationBtn.setBackgroundColor(Color.parseColor("#117869"));
            showEvaluationBtn.setText(R.string.driver_evaluation);
        }
        showEvaluationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEvaluationDialog(reservationItem);
            }
        });

        if ( isHistory ) {
            deadlineArea.setVisibility(View.GONE);
            showEvaluationBtn.setVisibility(View.VISIBLE);
        }

        evaluationDialog = findViewById(R.id.evaluationDialog);
        evaluationRatingBar = findViewById(R.id.evaluationRatingBar);
        reservationDateText = findViewById(R.id.reservationDateText);

        Button evaluationBtn = findViewById(R.id.evaluationBtn);
        evaluationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 평가 정보를 서버에 저장
                EvaluationTask task = new EvaluationTask(ReservationDetailActivity.this);
                task.execute("", reservationItem);

                // 기사님 평가 완료로 설정
                showEvaluationBtn.setEnabled(false);
                showEvaluationBtn.setBackgroundColor(Color.GRAY);
                showEvaluationBtn.setText(R.string.driver_evaluation_complete);

                evaluationDialog.setVisibility(View.GONE);
            }
        });

        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluationDialog.setVisibility(View.GONE);
            }
        });
    }

    public void showEvaluationDialog(ReservationItem item)
    {
        evaluationDialog.setVisibility(View.VISIBLE);
        evaluationRatingBar.setRating(1);

        // 년, 월, 일, 시, 분 표시
        SimpleDateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
        Long time = item.getDeptDate() * 1000;        // 초->밀리초로 변환
        reservationDateText.setText(df.format(time));
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

    private static class EvaluationTask extends AsyncTask<Object, Void, String>
    {
        private WeakReference<ReservationDetailActivity> activityReference;

        // only retain a weak reference to the activity
        EvaluationTask(ReservationDetailActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Object... params)
        {
            String uri = (String)params[0];
            ReservationItem item = (ReservationItem)params[1];

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
            ReservationDetailActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

//            try {
//                JSONObject jsonObj = new JSONObject(ret);
//                JSONArray jsonDeptList = jsonObj.getJSONArray(TAG_RESULTS);
//            } catch ( JSONException e ) {
//                e.printStackTrace();
//            }
        }
    }
}
