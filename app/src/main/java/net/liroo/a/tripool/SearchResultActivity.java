package net.liroo.a.tripool;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;

public class SearchResultActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        ArrayList<SearchItem> searchList = (ArrayList<SearchItem>)getIntent().getSerializableExtra("search_list");

        //검색 결과로 목록 출력하기
        //http://sharp57dev.tistory.com/11



        Log.e("test", "searchList : "+searchList.size());

        for ( int i=0; i<searchList.size(); i++ ) {
            Log.d("test_search_result", searchList.get(i).getDeptMain());
        }

    }





}
