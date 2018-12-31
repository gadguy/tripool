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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinActivity extends BaseActivity
{
    private EditText idField, pwField, pwChkField, mobileField;
    private RadioButton menRadio, womanRadio;
    private CheckBox privacyAgreeCheck;

    private String id, pw, pwChk, phone, gender, privacyAgree;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idField = findViewById(R.id.emailInput);
        pwField = findViewById(R.id.passwordInput);
        pwChkField = findViewById(R.id.passwordchkInput);
        mobileField = findViewById(R.id.phoneInput);

        menRadio = findViewById(R.id.menRadio);
        womanRadio = findViewById(R.id.womanRadio);
        privacyAgreeCheck = findViewById(R.id.privacyAgreeCheck);
    }

    public void joinClick(View view)
    {
        id = idField.getText().toString();
        pw = pwField.getText().toString();
        pwChk = pwChkField.getText().toString();
        phone = mobileField.getText().toString();

        if ( id.isEmpty() ) {
            Toast.makeText(getApplicationContext(), R.string.plz_enter_id, Toast.LENGTH_SHORT).show();
            return;
        }

        String emailRegex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(emailRegex);
        Matcher m = p.matcher(id);
        if ( !m.matches() ) {
            Toast.makeText(getApplicationContext(), R.string.id_not_match_regex, Toast.LENGTH_SHORT).show();
            return;
        }

        if ( pw.isEmpty() ) {
            Toast.makeText(getApplicationContext(), R.string.plz_enter_pw, Toast.LENGTH_SHORT).show();
            return;
        }

        String pwRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,11}$";
        p = Pattern.compile(pwRegex);
        m = p.matcher(pw);
        if ( !m.matches() ) {
            Toast.makeText(getApplicationContext(), R.string.pw_not_match_regex, Toast.LENGTH_SHORT).show();
            return;
        }

        if ( pwChk.isEmpty() || !pw.equals(pwChk) ) {
            Toast.makeText(getApplicationContext(), R.string.pw_not_match, Toast.LENGTH_SHORT).show();
            return;
        }

        if ( !privacyAgreeCheck.isChecked() ) {
            Toast.makeText(getApplicationContext(), R.string.plz_check_privacy, Toast.LENGTH_SHORT).show();
            return;
        }

        // 성별
        if ( menRadio.isChecked() ) {
            gender = "m";
        }
        else {
            gender = "f";
        }
        // 개인 정보 수집 동의
        privacyAgree = "개인정보수집동의";

        JoinTask task = new JoinTask(this);
        task.execute(id, pw, phone, gender, privacyAgree);
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

    private static class JoinTask extends AsyncTask<String, Void, String>
    {
        private WeakReference<JoinActivity> activityReference;

        // only retain a weak reference to the activity
        JoinTask(JoinActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params)
        {
            try {
                String id = params[0];
                String pw = params[1];
                String phone = params[2];
                String gender = params[3];
                String privacyAgree = params[4];
                if ( id == null || pw == null || phone == null || gender == null || privacyAgree == null ) {
                    return null;
                }

                String link = "http://a.liroo.net/tripool/member_join.php";
                String data = "u_id=" + URLEncoder.encode(id, "UTF-8");
                data += "&u_pw=" + URLEncoder.encode(pw, "UTF-8");
                data += "&mb_hp=" + URLEncoder.encode(phone, "UTF-8");
                data += "&mb_sex=" + gender;
                data += "&mb_1=" + URLEncoder.encode(privacyAgree, "UTF-8");

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

            JoinActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

            if ( ret != null ) {
                if ( ret.equals("done") ) {
                    Toast.makeText(activity, R.string.join_success, Toast.LENGTH_SHORT).show();
                    // sharedpreference로 로그인 유지하고 지도 페이지 이동
                    SharedPreferences userInfo = activity.getSharedPreferences("user_info", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor userEdit = userInfo.edit();
                    userEdit.putString("u_id", activity.id);
                    userEdit.putString("chk_autologin", "auto_login");
                    userEdit.commit();

                    // 지도 페이지로 이동
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);  //다음 화면으로 넘어가기
                    return;
                }
                else if ( ret.equals("no_id") ) {
                    Toast.makeText(activity, R.string.id_pw_no_space, Toast.LENGTH_SHORT).show();
                    return;
                }
                else if ( ret.equals("same_id") ) {
                    Toast.makeText(activity, R.string.exist_id, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(activity, R.string.join_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
