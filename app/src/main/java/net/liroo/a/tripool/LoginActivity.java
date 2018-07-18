package net.liroo.a.tripool;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends BaseActivity {

    EditText et_id, et_pw;
    String sId, sPw;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = (EditText)findViewById(R.id.emailInput);
        et_pw = (EditText)findViewById(R.id.passwordInput);
            }

    public void btnLogin(View view) {
        sId = et_id.getText().toString();
        sPw = et_pw.getText().toString();

        //서버로 데이터 전송
//        registDB rdb = new registDB(sId, sPw);
//        rdb.execute();
    }

    public void btnSignin(View view) {
        Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
        startActivity(intent);  //다음 화면으로 넘어가기
    }
}
