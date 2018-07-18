package net.liroo.a.tripool;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class PreviewActivity extends AppCompatActivity {

    public static String TAG = "PreviewActivity";

//    private final int REQUEST_PERMISSION = 1;

    private final int PERMISSIONS_REQUEST_CAMERA = 1;
    private final int PERMISSIONS_REQUEST_READ_STORAGE = 2;
    private final int PERMISSIONS_REQUEST_WRITE_STORAGE = 3;
    private final int PERMISSIONS_REQUEST_FINE_LOCATION = 4;

    private RecyclerView mRecyclerView;

    private Button btn_accept;

    int CAMERA_PERMISSION;
    int READ_STORAGE_PERMISSION;
    int WRITE_STORAGE_PERMISSION;
    int LOCATION_PERMISSION;

    Boolean chk_login = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        init();
        expandableList();



    }

    public void init()
    {
        CAMERA_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        READ_STORAGE_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        WRITE_STORAGE_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        btn_accept = (Button)findViewById(R.id.btn_accept);

//        btn_accept.setOnClickListener(mOnClickListener);  //따로 메소드로 빼지 않고 바로 아래에 작성함

        btn_accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                if (v == btn_accept) {
                    // Permission Check
                    if (Build.VERSION.SDK_INT >= 23) {
//                        if (CAMERA_PERMISSION != PackageManager.PERMISSION_GRANTED && READ_STORAGE_PERMISSION != PackageManager.PERMISSION_GRANTED &&
//                                WRITE_STORAGE_PERMISSION != PackageManager.PERMISSION_GRANTED && LOCATION_PERMISSION != PackageManager.PERMISSION_GRANTED) {
//                            // Permissions are not granted yet
//                            grantPermission();
//                        } else {
//                            // Permissions are already granted
//                            // You can implement here to have a custom dialog that explains why your app requires permissions
//                        }
                        //권한을 하나씩 요청해서 승인을 받음, 중간에 권한 하나를 거부하더라도 다음 권한이 요청됨
                        if (CAMERA_PERMISSION != PackageManager.PERMISSION_GRANTED ) {
                            // Permissions are not granted yet
                            ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
                        }
                        else if ( READ_STORAGE_PERMISSION != PackageManager.PERMISSION_GRANTED ) {
                            ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_STORAGE);
                        }
                        else if ( WRITE_STORAGE_PERMISSION != PackageManager.PERMISSION_GRANTED ) {
                            ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_STORAGE);
                        }
                        else if ( LOCATION_PERMISSION != PackageManager.PERMISSION_GRANTED ) {
                            ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
                        }
                        else {
                            //무조건 소개페이지로 이동, 소개페이징에서는 무조건 로그인 페이지로 이동

                            //권한이 다 있는 경우 소개 페이지(뷰 플리퍼)로 넘어감

                            if ( chk_login == true ) {
                                Intent intent = new Intent(getApplicationContext(), IntroFlipperActivity.class);
                                startActivity(intent);  //다음 화면으로 넘어가기
                            }
                            else {
                                //추후 자동 로그인 시에는 바로 메인으로, 로그 아웃 상태일 때만 뷰 플리퍼로 넘어가야 함
                                Intent intent = new Intent(getApplicationContext(), IntroFlipperActivity.class);
                                startActivity(intent);  //다음 화면으로 넘어가기
                            }


                            //권한이 다 있어도 안내 페이지를 보여주고 메인 페이지로 넘어감
//                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                            startActivity(intent);
                        }
                    }
//                }

            }
        });
    }

    public void expandableList()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        List<ExpandableListAdapter.Item> data = new ArrayList<>();

//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Camera"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Camera Permission Needs Permission"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "To Take Photos and Videos"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Storage"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Storage Permission Needs Permission"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "READ_EXTERNAL_STORAGE"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "WRITE_EXTERNAL_STORAGE"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "To Save Photos and Videos"));

        ExpandableListAdapter.Item camera = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Camera");
        camera.invisibleChildren = new ArrayList<>();
        camera.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Camera Permission Needs Permission"));
        camera.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Camera"));
        camera.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "To Take Photos and Videos"));


        ExpandableListAdapter.Item storage = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Storage");
        storage.invisibleChildren = new ArrayList<>();
        storage.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Storage Permission Needs Permission"));
        storage.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "READ_EXTERNAL_STORAGE"));
        storage.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "WRITE_EXTERNAL_STORAGE"));
        storage.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "To Save Photos and Videos"));


        ExpandableListAdapter.Item location = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Location");
        location.invisibleChildren = new ArrayList<>();
        location.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Location Permission Needs Permission"));
        location.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "ACCESS_FINE_LOCATION"));
        location.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "To Locate Your Location"));


        data.add(camera);
        data.add(storage);
        data.add(location);

        mRecyclerView.setAdapter(new ExpandableListAdapter(data));
    }

//    Button.OnClickListener mOnClickListener = new View.OnClickListener() {
//        public void onClick( View v ) {
//            if (v == btn_accept)
//            {
//                // Permission Check
//                if (Build.VERSION.SDK_INT >= 23)
//                {
//                    if (CAMERA_PERMISSION != PackageManager.PERMISSION_GRANTED && READ_STORAGE_PERMISSION != PackageManager.PERMISSION_GRANTED &&
//                            WRITE_STORAGE_PERMISSION != PackageManager.PERMISSION_GRANTED && LOCATION_PERMISSION != PackageManager.PERMISSION_GRANTED)
//                    {
//                        // Permissions are not granted yet
//                        grantPermission();
//                    }
//                    else
//                    {
//                        // Permissions are already granted
//                        // You can implement here to have a custom dialog that explains why your app requires permissions
//                    }
//                }
//            }
//
//        }
//    };

//    private void grantPermission()
//    {
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION},
//                REQUEST_PERMISSION);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSIONS_REQUEST_CAMERA:
                    ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_STORAGE);
                    break;
                case PERMISSIONS_REQUEST_READ_STORAGE:
                    ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_STORAGE);
                    break;
                case PERMISSIONS_REQUEST_WRITE_STORAGE:
                    ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
                    break;
                case PERMISSIONS_REQUEST_FINE_LOCATION:
                    if ( chk_login == true ) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);  //다음 화면으로 넘어가기
                    }
                    else {
                        //추후 자동 로그인 시에는 바로 메인으로, 로그 아웃 상태일 때만 뷰 플리퍼로 넘어가야 함
                        Intent intent = new Intent(getApplicationContext(), IntroFlipperActivity.class);
                        startActivity(intent);  //다음 화면으로 넘어가기
                    }
                    //권한이 다 있는 경우 소개 페이지(뷰 플리퍼)로 넘어감
//                    Intent intent = new Intent(getApplicationContext(), IntroFlipperActivity.class);
//                    startActivity(intent);  //다음 화면으로 넘어가기
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(intent);
                    break;
            }
        }
        else {
            //위의 요청에 해당하지 않으면?

        }
    }
}