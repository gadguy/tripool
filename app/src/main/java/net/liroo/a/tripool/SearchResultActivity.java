package net.liroo.a.tripool;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends BaseActivity {

    private List<String> list = new ArrayList<>();          // 데이터를 넣은 리스트변수
    private ListView listView;          // 검색을 보여줄 리스트변수
    private Button makeBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        ArrayList<SearchItem> searchList = (ArrayList<SearchItem>)getIntent().getSerializableExtra("search_list");

        //검색 결과로 목록 출력하기
        //http://sharp57dev.tistory.com/11


        Log.e("test", "searchList : "+searchList.size());

        //MainActivity에서 받은 정보를 가져옴
        //TODO: From, To, 출발시간, 준비완료 카운트도 추가해야 함
        //From, To는 장소만 표시해줌
        //방 만들 때는 From의 지역,장소, To의 지역장소, 출발날짜, 출발시간이 필요 함
        for ( int i=0; i<searchList.size(); i++ ) {
            Log.d("test_search_result", searchList.get(i).getDeptMain());
            list.add(searchList.get(i).getDeptMain());

        }
        //리스트뷰 세팅
        //TODO: 상단에 컬럼명 표시해줘야 함
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list) ;
        listView =(ListView)findViewById(R.id.search_list);
        listView.setAdapter(adapter);

        //방만들기 버튼
        makeBtn = findViewById(R.id.btn_make_room);
        //클릭하면 검색된 정보로 DB에 insert하고 해당 정보를 바탕으로 한 결제페이지로 이동
        makeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //php 통해서 DB에 insert하는 메소드
            //book_id, book_no, dept_main, dept_sub, departure, dest_main, dest_sub, destination, dept_date 보내야함
            //json_encode(array('result'=>$result)); 로 결과 받음


            //intent 통해서 결제페이지로 이동, 검색된 정보도 intent에 추가해줌


            Toast.makeText(getApplicationContext(), "방 만들고, 결제 페이지로 이동", Toast.LENGTH_SHORT).show();
            }
        });


    }





}
