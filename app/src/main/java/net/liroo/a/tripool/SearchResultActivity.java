package net.liroo.a.tripool;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends BaseActivity {

    private List<String> list = new ArrayList<>();          // 데이터를 넣은 리스트변수
    private ListView listView;          // 검색을 보여줄 리스트변수

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
    }





}
