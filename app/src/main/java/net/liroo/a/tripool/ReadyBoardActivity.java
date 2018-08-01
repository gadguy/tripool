package net.liroo.a.tripool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//탑승 준비 페이지
public class ReadyBoardActivity extends BaseActivity {

    private List<String> list = new ArrayList<>();          // 데이터를 넣은 리스트변수
    private String departure;
    private String destination;
    private long deptDate;
    private String people;
    private String luggage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readyboard);

        final Bundle bundle = getIntent().getBundleExtra("message");
        SearchResultItem item = bundle.getParcelable("search_result_item");
        if ( item != null ) {
            Log.e("ReadyBoardActivity test", item.getNo());
        }



        //검색한 결과값 중, 출발지, 도착지, 출발 시간을 가져옴
        departure = item.getDeparture();                    //출발지
        destination= item.getDestination();                 //도착지
        deptDate = item.getDeptDate();                      //출발시간 -> 리눅스타임 형태라 yyyy-mm-dd h:i:s 형태로 변경해야 함
        people = item.people();                             //인원수
        luggage = item.luggage();                           //캐리어 수


        //검색결과 가져온 값을 xml에 표시해 줘야 함



        //tripool_info에서 같은 출발지, 도착지, 출발 시간중에서 인원수를 카운트해서 가져와야 함 -> 동승자 수에 표기하기(결제를 완료한 상태만 가져오기)



        //방 만들기로 들어온 상태라면 검색한 결과값을 tripool_info에 insert 해야 함



        //결제를 하지 않고 뒤로가기를 누르면 예약이 안된다는 메시지 띄우기



        //결제하기 버튼 클릭 -> 결제화면으로 이동
        Button btnClose = (Button) findViewById(R.id.btn_pay);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //리스트뷰에서 클릭해서 온 상태면 결제 할 때, tripool_info에 insert 하기(결제완료 상태로)



                //방만들기로 들어온 상태면 tripool_info의 결제 상태값을 변경해야 함


                //결제화면으로 이동
                Intent intent = new Intent(getApplicationContext(), PayActivity.class);
                startActivity(intent);  //다음 화면으로 넘어가기
            }
        });



    }
}
