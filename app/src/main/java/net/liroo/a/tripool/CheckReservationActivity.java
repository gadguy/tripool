package net.liroo.a.tripool;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CheckReservationActivity extends BaseActivity {

    private ArrayList<SearchItem> reservationList;
    private String phone;

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
        reservationList.add(new SearchItem());

        //리스트뷰 세팅
        CheckReservationAdapter adapter = new CheckReservationAdapter(this, reservationList);
        ListView reservationListView = findViewById(R.id.reservationListView);
        reservationListView.setAdapter(adapter);
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
//                    // 사용 권한이 있을 경우
//                    String tel = "tel:" + phone;
//                    Intent intent = new Intent(Intent.ACTION_CALL);
//                    intent.setData(Uri.parse(tel));
//                    context.startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
            switch ( requestCode ) {
                case PERMISSIONS_REQUEST_CALL_PHONE:
                    // TODO: 전화번호 설정
//                    // 사용 권한이 있을 경우
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
