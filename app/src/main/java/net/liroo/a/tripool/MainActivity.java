package net.liroo.a.tripool;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);






        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            Toast.makeText(getApplicationContext(), "세팅버튼 입니다. 추후 개발 예정", Toast.LENGTH_LONG).show();

            SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
            String u_id = userInfo.getString("u_id", "");
            Toast.makeText(getApplicationContext(), u_id, Toast.LENGTH_SHORT).show();

            return true;
        }
        if (id == R.id.action_logout) {
            SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
            SharedPreferences.Editor userEdit = userInfo.edit();
            userEdit.clear();
            userEdit.commit();

            Toast.makeText(getApplicationContext(), "로그아웃 됩니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);  //다음 화면으로 넘어가기
        }




        return super.onOptionsItemSelected(item);
    }
}
