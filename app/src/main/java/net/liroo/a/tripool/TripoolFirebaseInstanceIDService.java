package net.liroo.a.tripool;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class TripoolFirebaseInstanceIDService extends FirebaseInstanceIdService
{
    @Override
    public void onTokenRefresh()
    {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);

        Log.d("tripool", "Refreshed token: " + refreshedToken);
    }

    private void sendRegistrationToServer(String token)
    {

    }
}
