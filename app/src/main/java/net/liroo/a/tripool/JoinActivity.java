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

public class JoinActivity extends BaseActivity {

    EditText et_id, et_pw, et_pw_chk;
    String sId, sPw, sPw_chk;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // ----------------------------------------------------------------------------------------
        // 수정
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ----------------------------------------------------------------------------------------

        et_id = (EditText)findViewById(R.id.emailInput);
        et_pw = (EditText)findViewById(R.id.passwordInput);
        et_pw_chk = (EditText)findViewById(R.id.passwordchkInput);
    }

    public void btnJoin(View view) {
        sId = et_id.getText().toString();
        sPw = et_pw.getText().toString();
        sPw_chk = et_pw_chk.getText().toString();

        if ( sId.isEmpty() ) {
            Toast.makeText(getApplicationContext(), "ID를 입력하여 주십시오.", Toast.LENGTH_SHORT).show();
            return;
        }
        if ( sPw.isEmpty() ) {
            Toast.makeText(getApplicationContext(), "비밀번호를 입력하여 주십시오.", Toast.LENGTH_SHORT).show();
            return;
        }
        if ( sPw_chk.isEmpty() ) {
            Toast.makeText(getApplicationContext(), "비밀번호를 확인하여 주십시오.", Toast.LENGTH_SHORT).show();
            return;
        }


        //pw와 pw_chk이 같으면
        if ( sPw.equals(sPw_chk)) {


            insertToDatabase(sId, sPw);

            //서버로 데이터 전송
//            registDB rdb = new registDB(sId, sPw);
//            rdb.execute();


//            Toast.makeText(this, "패스워드가 일치 합니다.", Toast.LENGTH_SHORT).show();

        }
        else {
            Toast.makeText(this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();


        }


    }

    // ----------------------------------------------------------------------------------------
    // 수정

//    @Override
//    public void onBackPressed()
//    {
//        super.onBackPressed();
//        //TODO
//        //뒤로가기 한번 누르면 토스트로 '뒤로가기를 한 번더 누르면 앱이 종료됩니다' 띄우고
//        //뒤로가기 두번 누르면 앱 종료 시키기
//        //뒤로가기 두번 누르는 것은 핸들러를 통해서 할 수 있다
//
//
//
//        //  앱 아예 종료
//        killAll();
//    }
//
//    private void killAll()
//    {
//        app.clearActivityPool();
//
//        finish();
//
//        app.killApplication();
//    }

    // ----------------------------------------------------------------------------------------

    private void insertToDatabase(String u_id, String u_pw) {

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(JoinActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                if ( s.equals("done") ) {
                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                    //sharedpreference로 로그인 유지하고 지도 페이지 이동
                    SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor userEdit = userInfo.edit();
                    userEdit.putString("u_id", sId);
                    userEdit.putString("chk_autologin", "auto_login");
                    userEdit.commit();
                    // 지도 페이지로 이동
                    // ----------------------------------------------------------------------------------------
                    // 수정
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);  //다음 화면으로 넘어가기
                    // ----------------------------------------------------------------------------------------

                } else if ( s.equals("no_id") ) {
                    Toast.makeText(getApplicationContext(), "ID 혹은 비밀번호가 공백이면 안됩니다.", Toast.LENGTH_LONG).show();
                } else if ( s.equals("same_id") ) {
                    Toast.makeText(getApplicationContext(), "같은 ID가 존재합니다.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "회원 가입 오류, 다시 시도해 주십시오.", Toast.LENGTH_LONG).show();

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

                    String link = "http://a.liroo.net/tripool/member_join.php";
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
        InsertData task = new InsertData();
        task.execute(u_id, u_pw);
    }

    // ----------------------------------------------------------------------------------------
    // 수정
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemID = item.getItemId();
        if ( itemID == android.R.id.home ) {   // 뒤로
            finish();
        }
        return true;
    }
    // ----------------------------------------------------------------------------------------
}
