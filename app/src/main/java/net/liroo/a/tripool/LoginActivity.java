package net.liroo.a.tripool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class LoginActivity extends BaseActivity {

    EditText et_id, et_pw;
    String sId, sPw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        // 로그아웃 후에는 back 버튼 없도록 설정
        if ( app.getActivityPool().size() != 0 ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        et_id = (EditText)findViewById(R.id.emailInput);
        et_pw = (EditText)findViewById(R.id.passwordInput);
    }

    public void btnLogin(View view) {
        sId = et_id.getText().toString();
        sPw = et_pw.getText().toString();

        if ( sId.isEmpty() ) {
            Toast.makeText(getApplicationContext(), "ID를 입력하여 주십시오.", Toast.LENGTH_SHORT).show();
            return;
        }
        if ( sPw.isEmpty() ) {
            Toast.makeText(getApplicationContext(), "비밀번호를 입력하여 주십시오.", Toast.LENGTH_SHORT).show();
            return;
        }

        loginToDB(sId, sPw);
        //서버로 데이터 전송
//        registDB rdb = new registDB(sId, sPw);
//        rdb.execute();
    }

    public void btnSignin(View view) {
        Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
        startActivity(intent);  //다음 화면으로 넘어가기
    }

    private void loginToDB(String u_id, String u_pw) {
        class loginData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                if ( s.equals("auth_login") ) {

                    Toast.makeText(getApplicationContext(), "로그인 완료", Toast.LENGTH_LONG).show();
                    //sharedpreference로 로그인 유지하고 지도 페이지 이동
                    SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor userEdit = userInfo.edit();
                    userEdit.putString("u_id", sId);
                    userEdit.putString("chk_autologin", "auto_login");
                    userEdit.commit();
                    //지도 페이지로 이동
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);  //다음 화면으로 넘어가기


                } else if ( s.equals("no_id") ) {

                    Toast.makeText(getApplicationContext(), "ID 혹은 비밀번호가 공백이면 안됩니다.", Toast.LENGTH_LONG).show();

                } else if ( s.equals("wrong_id") ) {

                    Toast.makeText(getApplicationContext(), "ID 혹은 비밀번호가 틀립니다. 비밀번호는 대소문자를 구분합니다.", Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(getApplicationContext(), "로그인 실패, 다시 시도해 주십시오.", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String u_id = (String) params[0];
                    String u_pw = (String) params[1];

                    if ( u_id == null || u_pw == null ) {
                        Toast.makeText(getApplicationContext(), "ID 혹은 비밀번호가 공백이면 안됩니다.", Toast.LENGTH_LONG).show();
                    }

                    String link = "http://a.liroo.net/tripool/member_login.php";
                    String data = URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(u_id, "UTF-8");
                    data += "&" + URLEncoder.encode("u_pw", "UTF-8") + "=" + URLEncoder.encode(u_pw, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        loginData task = new loginData();
        task.execute(u_id, u_pw);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemID = item.getItemId();
        if ( itemID == android.R.id.home ) {   // 뒤로
            finish();
        }
        return true;
    }
}
