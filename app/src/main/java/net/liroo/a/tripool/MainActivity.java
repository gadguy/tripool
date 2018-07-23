package net.liroo.a.tripool;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.daum.mf.map.api.MapView;


public class MainActivity extends AppCompatActivity {

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

    public void btnLayerFrom(View view) {

        // Dialog 다이얼로그 클래스로 다이얼로그를 만든다
        final Dialog layerForm = new Dialog(this); // 다이얼로그 객체 생성
        layerForm.setTitle("목적지 검색");
        layerForm.setContentView(R.layout.map_find); // 다이얼로그 화면 등록
        layerForm.show(); // 다이얼로그 띄우기

        // Activity 에 Dialog 를 등록하기
        layerForm.setOwnerActivity(MainActivity.this);

        //종료할 것인지 여부 true: 다이얼로그 종료, false : 종료안됨
//        layerForm.setCanceledOnTouchOutside(true); // 다이얼로그 바깥 영역을 클릭시

//        btnCloseForm
        Button btnClose = (Button) findViewById(R.id.btnCancel);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layerForm.dismiss();   //다이얼로그를 닫는 메소드입니다.
            }
        });

        Toast.makeText(getApplicationContext(), "출발 다이얼로그 레이아웃 할거임", Toast.LENGTH_SHORT).show();
    }
    public void btnLayerTo(View view) {

        Toast.makeText(getApplicationContext(), "도착 다이얼로그 레이아웃 할거임", Toast.LENGTH_SHORT).show();
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
