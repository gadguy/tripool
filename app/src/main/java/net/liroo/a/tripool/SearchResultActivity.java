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

        Log.e("test", "searchList : "+searchList.size());

        for ( int i=0; i<searchList.size(); i++ ) {
            Log.d("test", searchList.get(i).getDeptMain());
        }

    }





}
