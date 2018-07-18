package net.liroo.a.tripool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    private void loginToDB(String u_id, String u_pw) {
        class InsertData extends AsyncTask<String, Void, String> {
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
                    // 지도 페이지로 이동


                } else if ( s.equals("no_id") ) {

                    Toast.makeText(getApplicationContext(), "ID 혹은 비밀번호가 공백이면 안됩니다.", Toast.LENGTH_LONG).show();

                } else if ( s.equals("wrong_id") ) {

                    Toast.makeText(getApplicationContext(), "ID 혹은 비밀번호가 틀립니다. 비밀번호는 대소문자를 구분합니다.", Toast.LENGTH_LONG).show();
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

                    String link = "http://a.liroo.net/android/member_join.php";
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




}
