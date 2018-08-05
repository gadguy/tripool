package net.liroo.a.tripool;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;

import java.util.ArrayList;

public class TripoolApp extends Application
{
    public IBaseActivity topActivity;	// 현재 최상위 액티비티
    private ArrayList<IBaseActivity> activityPool = new ArrayList<>();	// Activity 관리

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    /**
     * 새로운 액티버티 추가.
     * 중복검사.
     */
    private void addActivity(IBaseActivity activity)
    {
        boolean isExist = false;
        for ( IBaseActivity old : activityPool ) {
            if ( old == activity ) {
                isExist = true;
                break;
            }
        }

        if ( !isExist ) {
            activityPool.add(activity);
        }
    }

    /**
     * 액티버티 종료 및 목록에서 제거.
     */
    public void removeActivity(IBaseActivity activity)
    {
        activityPool.remove(activity);
        ((Activity)activity).finish();
    }

    @SuppressWarnings("rawtypes")
    public void removeActivity(Class cls)
    {
        for ( IBaseActivity activity : activityPool ) {
            if ( activity.getClass() == cls ) {
                ((Activity)activity).finish();
                activityPool.remove(activity);

                // 탑 액티버티를 지우는 경우 탑 액티버티 레퍼런스를 옮긴다.
                if ( activity == topActivity ) {
                    if ( activityPool.size() > 0 ) {
                        topActivity = activityPool.get(activityPool.size()-1);
                    }
                    else {
                        topActivity = null;
                    }
                }
                break;
            }
        }
    }

    // activity 관리
    public IBaseActivity getTopActivity() { return topActivity; }

    public void setTopActivity(IBaseActivity activity)
    {
        topActivity = activity;
        addActivity(activity);
    }

    public ArrayList<IBaseActivity> getActivityPool() {
        if ( activityPool == null ) {
            return new ArrayList<>();
        }
        return activityPool;
    }

    /**
     * 탑을 제외한 모든 액티버티를 종료하고 목록을 1개로 초기화.
     */
    public void clearActivityPool()
    {
        if ( activityPool == null ) return;
        for ( IBaseActivity activity : activityPool ) {
            if ( activity != topActivity )
                ((Activity)activity).finish();
        }
        activityPool.clear();
    }

    public void killApplication()
    {
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        am.killBackgroundProcesses(getPackageName());

        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
