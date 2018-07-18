package net.liroo.a.tripool;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity

{
    protected TripoolApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        app = (TripoolApp)getApplication();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        app.setTopActivity(this);
    }

    @Override
    protected void onDestroy()
    {
        app.removeActivity(this);

        super.onDestroy();
    }
}
