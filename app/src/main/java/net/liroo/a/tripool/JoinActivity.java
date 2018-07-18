package net.liroo.a.tripool;

import android.app.ProgressDialog;
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

public class JoinActivity extends BaseActivity {

    EditText et_id, et_pw, et_pw_chk;
    String sId, sPw, sPw_chk;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        et_id = (EditText)findViewById(R.id.emailInput);
        et_pw = (EditText)findViewById(R.id.passwordInput);
        et_pw_chk = (EditText)findViewById(R.id.passwordchkInput);
    }

    public void btnJoin(View view) {
        sId = et_id.getText().toString();
        sPw = et_pw.getText().toString();
        sPw_chk = et_pw_chk.getText().toString();

        if ( sId.isEmpty() ) {
            Toast.makeText(getApplicationContext(), "msg", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        //  앱 아예 종료
        killAll();
    }

    private void killAll()
    {
        app.clearActivityPool();

        finish();

        app.killApplication();
    }

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
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                if ( s.equals("1") ) {
                    // 지도 페이지로 이동
                }
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String u_id = (String) params[0];
                    String u_pw = (String) params[1];

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
