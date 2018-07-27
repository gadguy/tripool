package net.liroo.a.tripool;

import android.app.Activity;
import android.os.Bundle;

public class NoTitledBarBaseActivity extends Activity implements IBaseActivity
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
