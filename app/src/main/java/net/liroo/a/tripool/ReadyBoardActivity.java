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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readyboard);

//        final ArrayList<SearchItem> searchResult = (ArrayList<SearchItem>)getIntent().getSerializableExtra("search_result");
//        final ArrayList searchResult = (ArrayList)getIntent().getSerializableExtra("search_result");



        //검색 결과로 목록 출력하기
//        Log.e("test", "searchResultList : "+searchResult.size());

        //SearchResultActivity에서 받은 정보를 가져옴
//        for ( int i=0; i<searchList.size(); i++ ) {
//            Log.d("test_search_result", searchList.get(i).getDeptMain());
//            list.add(searchList.get(i).getDeptMain());
//        }

        //결제하기 버튼 클릭 -> 결제화면으로 이동
        Button btnClose = (Button) findViewById(R.id.btn_pay);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //결제화면으로 이동
                Intent intent = new Intent(getApplicationContext(), PayActivity.class);
                startActivity(intent);  //다음 화면으로 넘어가기
//                Toast.makeText(getApplicationContext(), "결제 페이지로 이동", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
