package net.liroo.a.tripool;

import android.os.Bundle;
import android.util.Log;

//티켓 예약 확인 페이지
public class TicketActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        //tripool_info에서 본인의 ID로 현재시간 이후로 예약된 목록을 가져옴




        //tripool_info에서 가져온 목록을 바탕으로 xml에 목록으로 출력함



        //상태값에 따라 배차중, 배차완료를 표시함





    }
}
