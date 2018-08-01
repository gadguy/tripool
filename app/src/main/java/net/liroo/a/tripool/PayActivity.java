package net.liroo.a.tripool;

import android.os.Bundle;

//결제 페이지, 일단은 그냥 결제화면만 보여주고, 기능은 2차 개발
public class PayActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        //현재는 현장결제만 가능하다는 메시지를 띄우고, 확인을 누르면 예약확인 페이지로 이동(TicketActivity)
        //예약확인 페이지에서 배차 관련 정보를 표시 해줌


    }
}
