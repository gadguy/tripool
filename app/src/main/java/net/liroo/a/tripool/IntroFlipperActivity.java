package net.liroo.a.tripool;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class IntroFlipperActivity extends NoTitledBarBaseActivity implements FlipperTouchAction.ViewFlipperCallback
{
    private ArrayList<ImageView> indexes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_flipper);

        ViewFlipper flipper = findViewById(R.id.flipper);
        ImageView index0 = findViewById(R.id.imgIndex0);
        ImageView index1 = findViewById(R.id.imgIndex1);
        ImageView index2 = findViewById(R.id.imgIndex2);

        // 인덱스 리스트
        indexes = new ArrayList<>();
        indexes.add(index0);
        indexes.add(index1);
        indexes.add(index2);

        // xml을 inflate 하여 flipper view에 추가하기
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.intro_flipper_cell_1, flipper, false);
        View view2 = inflater.inflate(R.layout.intro_flipper_cell_2, flipper, false);
        View view3 = inflater.inflate(R.layout.intro_flipper_cell_3, flipper, false);

        // inflate한 view 추가
        flipper.addView(view1);
        flipper.addView(view2);
        flipper.addView(view3);

        // 리스너 설정 - 좌우 터치시 화면 넘어가기
        flipper.setOnTouchListener(new FlipperTouchAction(this, flipper));

        Button startBtn = findViewById(R.id.btn_start);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PreviewActivity.class);
                startActivity(intent);
            }
        });
    }

    // 인덱스 업데이트
    public void onFlipperActionCallback(int position)
    {
        for ( int i=0; i<indexes.size(); i++ ) {
            ImageView index = indexes.get(i);
            // 현재화면의 인덱스 위치면 녹색
            if ( i == position ) {
                index.setImageResource(R.drawable.intro_dot_ov);
            }
            // 그외
            else {
                index.setImageResource(R.drawable.intro_dot);
            }
        }
    }
}
