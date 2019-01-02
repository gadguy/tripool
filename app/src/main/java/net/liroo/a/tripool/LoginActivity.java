package net.liroo.a.tripool;

import android.app.Activity;
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
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class LoginActivity extends BaseActivity
{
    private EditText idField, pwField;
    private String id, pw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        // 로그아웃 후에는 back 버튼 없도록 설정
        if ( app.getActivityPool().size() != 0 ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        idField = findViewById(R.id.emailInput);
        pwField = findViewById(R.id.passwordInput);
    }

    public void loginClick(View view)
    {
        id = idField.getText().toString();
        pw = pwField.getText().toString();

        if ( id.isEmpty() ) {
            Toast.makeText(getApplicationContext(), R.string.plz_enter_id, Toast.LENGTH_SHORT).show();
            return;
        }
        if ( pw.isEmpty() ) {
            Toast.makeText(getApplicationContext(), R.string.plz_enter_pw, Toast.LENGTH_SHORT).show();
            return;
        }

        LoginTask task = new LoginTask(this);
        task.execute(id, pw);
    }

    public void signinClick(View view)
    {
        Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
        startActivity(intent);  // 다음 화면으로 넘어가기
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

    private static class LoginTask extends AsyncTask<String, Void, String>
    {
        private WeakReference<LoginActivity> activityReference;

        // only retain a weak reference to the activity
        LoginTask(LoginActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params)
        {
            try {
                String id = params[0];
                String pw = params[1];
                if ( id == null || pw == null ) {
                    return null;
                }

                String link = "http://a.liroo.net/tripool/member_login.php";
                String data = URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                data += "&" + URLEncoder.encode("u_pw", "UTF-8") + "=" + URLEncoder.encode(pw, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                // Read Server Response
                while ( (line = reader.readLine()) != null ) {
                    sb.append(line);
                    break;
                }
                return sb.toString();
            } catch ( Exception e ) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String ret)
        {
            super.onPostExecute(ret);

            LoginActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

            if ( ret != null ) {
                if ( ret.equals("auth_login") ) {
                    // sharedpreference로 로그인 유지하고 지도 페이지 이동
                    SharedPreferences userInfo = activity.getSharedPreferences("user_info", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor userEdit = userInfo.edit();
                    userEdit.putString("u_id", activity.id);
                    userEdit.putString("chk_autologin", "auto_login");
                    userEdit.commit();

                    // 지도 페이지로 이동
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);  // 다음 화면으로 넘어가기
                    return;
                }
                else if ( ret.equals("no_id") ) {
                    Toast.makeText(activity, R.string.id_pw_no_space, Toast.LENGTH_SHORT).show();
                    return;
                }
                else if ( ret.equals("wrong_id") ) {
                    Toast.makeText(activity, R.string.id_pw_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(activity, R.string.login_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
