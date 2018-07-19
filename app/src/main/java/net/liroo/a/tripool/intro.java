package net.liroo.a.tripool;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class intro extends Activity {

    private Handler handler;
    private Runnable r;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        //2초 뒤에 다음화면(MainActivity)으로 넘어가기 Handler 사용
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                //자동로그인 되어있으면 지도페이지로 바로 넘어가기
                SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
                String u_id = userInfo.getString("u_id", "");

                if ( u_id.isEmpty() ) {
                    //로그아웃 상태면 소개페이지
                    Intent intent = new Intent(getApplicationContext(), IntroFlipperActivity.class);
                    startActivity(intent);  //다음 화면으로 넘어가기
                } else {
                    //로그인 되어 있으면 바로 지도 페이지
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);  //다음 화면으로 넘어가기
                }
                finish();

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 다시 화면에 들어왔을 때, 예약 걸어주기
        handler.postDelayed(r, 2000);           //4초 뒤에 Runnable 객체 수행
    }

    @Override
    protected void onPause() {
        super.onPause();
        //화면을 벗어나면, handler에 예약해놓은 작업을 취소하자
        handler.removeCallbacks(r);
    }
}
