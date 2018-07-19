package net.liroo.a.tripool;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;


public class IntroFlipperActivity extends BaseActivity implements ViewFlipperAction.ViewFlipperCallback {
//public class IntroFlipperActivity extends AppCompatActivity {
    //UI
    //뷰 플리퍼
    ViewFlipper flipper;
    //인덱스
    List<ImageView> indexes;
    //

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introflipper);

        //UI
         flipper = findViewById(R.id.flipper);
        ImageView index0 = (ImageView)findViewById(R.id.imgIndex0);
        ImageView index1 = (ImageView)findViewById(R.id.imgIndex1);
        ImageView index2 = (ImageView)findViewById(R.id.imgIndex2);

        //인덱스 리스트
        indexes = new ArrayList<>();
        indexes.add(index0);
        indexes.add(index1);
        indexes.add(index2);

        //xml을 inflate 하여 flipper view에 추가하기
        //inflate
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.viewflipper1, flipper, false);
        View view2 = inflater.inflate(R.layout.viewflipper2, flipper, false);
        View view3 = inflater.inflate(R.layout.viewflipper3, flipper, false);

        if ( view1 == null ) {
            Log.e("tri", "view1 null");
        }
        else {
            Log.e("tri", "view1 not null");
        }
        //inflate한 view 추가
        flipper.addView(view1);
        flipper.addView(view2);
        flipper.addView(view3);

        //리스너 설정 - 좌우 터치시 화면 넘어가기
        flipper.setOnTouchListener(new ViewFlipperAction(this, flipper));
    }
    //인덱스 업데이트
    public void onFlipperActionCallback(int position) {
        for( int i=0; i<indexes.size(); i++ ) {
            ImageView index = indexes.get(i);
            //현재화면의 인덱스 위치면 녹색
            if ( i == position ) {
                index.setImageResource(R.drawable.tripool_logo);
            }
            //그외
            else {
                index.setImageResource(R.drawable.expand_arrow);
            }
        }
    }

    public void btnToMain(View v) {
        //여기에다 할 일을 적어주세요.
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);

    }



}
