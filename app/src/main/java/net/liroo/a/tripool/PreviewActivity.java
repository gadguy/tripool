package net.liroo.a.tripool;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.liroo.a.tripool.adapter.ExpandableListAdapter;

import java.util.ArrayList;

public class PreviewActivity extends BaseActivity
{
    private int READ_STORAGE_PERMISSION;
    private int WRITE_STORAGE_PERMISSION;
    private int LOCATION_PERMISSION;
    private int CALL_PERMISSION;

    private final int PERMISSIONS_REQUEST_READ_STORAGE = 1;
    private final int PERMISSIONS_REQUEST_WRITE_STORAGE = 2;
    private final int PERMISSIONS_REQUEST_FINE_LOCATION = 3;
    private final int PERMISSIONS_REQUEST_CALL_PHONE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        settingExpandableList();
    }

    public void init()
    {
        READ_STORAGE_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        WRITE_STORAGE_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        CALL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        Button acceptBtn = findViewById(R.id.btn_accept);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Permission Check
                if ( Build.VERSION.SDK_INT >= 23 ) {
                    //권한을 하나씩 요청해서 승인을 받음, 중간에 권한 하나를 거부하더라도 다음 권한이 요청됨
                    if ( READ_STORAGE_PERMISSION != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_STORAGE);
                    }
                    else if ( WRITE_STORAGE_PERMISSION != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_STORAGE);
                    }
                    else if ( LOCATION_PERMISSION != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
                    }
                    else if ( CALL_PERMISSION != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CALL_PHONE);
                    }
                    else {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);  // 다음 화면으로 넘어가기
                    }
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);  // 다음 화면으로 넘어가기
                }
            }
        });
    }

    public void settingExpandableList()
    {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ExpandableListAdapter.Item storage = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, getString(R.string.storage));
        storage.invisibleChildren = new ArrayList<>();
        storage.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, getString(R.string.storage_permission_help)));

        ExpandableListAdapter.Item location = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, getString(R.string.location));
        location.invisibleChildren = new ArrayList<>();
        location.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, getString(R.string.location_permission_help)));

        ExpandableListAdapter.Item call = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, getString(R.string.call));
        call.invisibleChildren = new ArrayList<>();
        call.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, getString(R.string.call_permission_help)));

        ArrayList<ExpandableListAdapter.Item> data = new ArrayList<>();
        data.add(storage);
        data.add(location);
        data.add(call);

        recyclerView.setAdapter(new ExpandableListAdapter(data));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
            switch ( requestCode ) {
                case PERMISSIONS_REQUEST_READ_STORAGE :
                    ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_STORAGE);
                    break;
                case PERMISSIONS_REQUEST_WRITE_STORAGE :
                    ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
                    break;
                case PERMISSIONS_REQUEST_FINE_LOCATION :
                    ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CALL_PHONE);
                    break;
                case PERMISSIONS_REQUEST_CALL_PHONE :
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);  // 다음 화면으로 넘어가기
                    break;
            }
        }
        else {
            Toast.makeText(this, R.string.plz_accept_permission, Toast.LENGTH_SHORT).show();
        }
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
}