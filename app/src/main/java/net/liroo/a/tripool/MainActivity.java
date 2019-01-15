package net.liroo.a.tripool;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import net.liroo.a.tripool.obj.NoticeItem;
import net.liroo.a.tripool.obj.SearchItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navView;
    private TextView myEmailText;

    private TMapView tMapView;
    private EditText editTextFrom, editTextTo;
    private Button searchBtn;

    private ArrayList<String> deptList = new ArrayList<>();     // 지역 spinner에서 쓰임
    private ArrayList<String> deptStationList = new ArrayList<>();    // 장소 spinner에서 쓰임
    private ArrayAdapter regionAdapter, placeAdapter;
    private static final String TAG_RESULTS = "result";   // json으로 가져오는 값의 파라메터

    private View dateBtn, timeBtn;
    private TextView dateText, timeText;
    private int year, month, day, hour, minute;

    private View searchDialog;
    private EditText peopleInput, carrierInput;
    private Button dialogOkBtn;

    private double deptLat, deptLon, destLat, destLon;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Side Menu Setting
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navView = findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(this);

        SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        String id = userInfo.getString("u_id", "");

        View headerView = navView.getHeaderView(0);
        myEmailText = headerView.findViewById(R.id.myEmailText);
        myEmailText.setText(id);

        // 검색 정보
        editTextFrom = findViewById(R.id.editTextFrom);
        editTextTo = findViewById(R.id.editTextTo);
        searchBtn = findViewById(R.id.btn_search);

        searchDialog = findViewById(R.id.searchDialog);
        dialogOkBtn = findViewById(R.id.dialogOkBtn);
        peopleInput = findViewById(R.id.peopleInput);
        carrierInput = findViewById(R.id.carrierInput);

        // 검색하기 버튼
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( editTextFrom.getText().toString().isEmpty() || editTextFrom.getText().toString().equals(getString(R.string.setting_departure)) ) {
                    Toast.makeText(getApplicationContext(), R.string.plz_select_departure, Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( editTextTo.getText().toString().isEmpty() || editTextTo.getText().toString().equals(getString(R.string.setting_destination)) ) {
                    Toast.makeText(getApplicationContext(), R.string.plz_select_destination, Toast.LENGTH_SHORT).show();
                    return;
                }
                searchDialog.setVisibility(View.VISIBLE);
            }
        });
        // 인원수, 캐리어 입력 버튼
        // php DB에서 검색 후, 검색결과 화면으로 이동
        dialogOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( peopleInput.getText().toString().isEmpty() ) {
                    Toast.makeText(getApplicationContext(), R.string.plz_enter_people_number, Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( carrierInput.getText().toString().isEmpty() ) {
                    Toast.makeText(getApplicationContext(), R.string.plz_enter_carrier_number, Toast.LENGTH_SHORT).show();
                    return;
                }

                // 키보드 숨기기
                peopleInput.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager)peopleInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(peopleInput.getWindowToken(), 0);
                    }
                });

                carrierInput.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager)carrierInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(carrierInput.getWindowToken(), 0);
                    }
                });

                String no = "";
                String[] deptInfo = editTextFrom.getText().toString().split(" ");
                String deptMain = deptInfo[0];
                String deptSub = deptInfo[1];

                String departure;
                if ( deptInfo.length > 4 ) {
                    departure = deptInfo[2] + " " + deptInfo[3] + " " + deptInfo[4];
                }
                else if ( deptInfo.length > 3 ) {
                    departure = deptInfo[2] + " " + deptInfo[3];
                }
                else {
                    departure = deptInfo[2];
                }

                String[] destInfo = editTextTo.getText().toString().split(" ");
                String destMain = destInfo[0];
                String destSub = destInfo[1];
                String destination;
                if ( destInfo.length > 4 ) {
                    destination = destInfo[2] + " " + destInfo[3] + " " + destInfo[4];
                }
                else if ( destInfo.length > 3 ) {
                    destination = destInfo[2] + " " + destInfo[3];
                }
                else {
                    destination = destInfo[2];
                }

                // 검색한 날짜
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour, minute);
                long deptDate = calendar.getTimeInMillis();

                SearchItem item = new SearchItem(no, deptMain, deptSub, departure, destMain, destSub, destination, deptDate, peopleInput.getText().toString(), carrierInput.getText().toString());
                SearchTask task = new SearchTask(MainActivity.this);
                task.execute("http://a.liroo.net/tripool/json_search_result.php", item);
            }
        });

        // 다음 지도 api
//        MapView mapView = new MapView(this);
//        mapView.setDaumMapApiKey("3c8e3fff3053a6bb1ae42fc8b5fbd761");
//        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
//        mapViewContainer.addView(mapView);

        // Tmap 지도 api
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("021ce310-85c0-4bec-97ca-78ae3e046731");
        ViewGroup linearLayoutTmap = findViewById(R.id.map_view);
//        tMapView.setCenterPoint(126.988205, 37.551135);
//        tMapView.setCenterPoint(126.985302, 37.570841);
        tMapView.setLocationPoint(126.985302, 37.570841);

        tMapView.setIconVisibility(true);   //현재위치로 표시될 아이콘을 표시할지 여부를 설정


//        TMapMarkerItem markerItem1 = new TMapMarkerItem();
//
//        TMapPoint tMapPoint1 = new TMapPoint(37.570841, 126.985302); // SKT타워
//        // 마커 아이콘
//        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.point);
//
//        markerItem1.setIcon(bitmap); // 마커 아이콘 지정
//        markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
//        markerItem1.setTMapPoint( tMapPoint1 ); // 마커의 좌표 지정
//        markerItem1.setName("SKT타워"); // 마커의 타이틀 지정
//        tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가

        linearLayoutTmap.addView(tMapView);

        setGps(); // 지도에서 현재위치를 표시
//        drawLine(deptLat, deptLon, destLat, destLon);   // 길찾기 정보 그리기

        // 검색시 필요한 날짜 및 시간
        dateBtn = findViewById(R.id.dateBtn);
        dateText = findViewById(R.id.dateText);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int m, int d)
                    {
                        year = y;
                        month = m;
                        day = d;

                        dateText.setText(String.valueOf(year) + "." + String.valueOf(month+1) + "." + String.valueOf(day));
                    }
                };
                new DatePickerDialog(MainActivity.this, dateSetListener, year, month, day).show();
            }
        });

        timeBtn = findViewById(R.id.timeBtn);
        timeText = findViewById(R.id.timeText);
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int m)
                    {
                        hour = hourOfDay;
                        minute = m;

                        if ( minute >= 0 && minute < 10 )
                            timeText.setText(String.valueOf(hour) + " : 0" + String.valueOf(minute));
                        else
                            timeText.setText(String.valueOf(hour) + " : " + String.valueOf(minute));
                    }
                };
                new TimePickerDialog(MainActivity.this, timeSetListener, hour, minute, false).show();
            }
        });

        // 현재 시간
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        dateText.setText(String.valueOf(year) + "." + String.valueOf(month+1) + "." + String.valueOf(day));
        if ( minute >= 0 && minute < 10 )
            timeText.setText(String.valueOf(hour) + " : 0" + String.valueOf(minute));
        else
            timeText.setText(String.valueOf(hour) + " : " + String.valueOf(minute));

        // 공지사항
        final View noticeDialog = findViewById(R.id.noticeDialog);
        TextView noticeText = findViewById(R.id.noticeText);
        Button noticeCloseBtn = findViewById(R.id.noticeCloseBtn);
        noticeCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeDialog.setVisibility(View.GONE);
            }
        });

        GetNoticeTask noticeTask = new GetNoticeTask(this);
        noticeTask.execute("");

        // For Test
        NoticeItem item = new NoticeItem("2018.12.02", "Test 서비스 오픈", "1", "http://a.liroo.net/bbs/board.php?bo_table=notice", "현재 서비스가 Test 중이므로 일부 지역 및 일부 여행지에서만 이용 가능합니다 :(\n\n* 전라도 순천\n순천역, 순천만정원, 순천만습지, 와온해변 (총 4곳)");
        noticeDialog.setVisibility(View.VISIBLE);
        noticeText.setText(item.getContent());

        // 장소 목록을 php에서 가져옴
        GetDataTask task = new GetDataTask(this);
        task.execute("http://a.liroo.net/tripool/json_region_list.php", "region_list");
    }

    // Tmap 현재 위치 잡음
    public void setGps()
    {
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                locationListener);
    }

    // Tmap 현재위치로 이동
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if ( location != null ) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                tMapView.setLocationPoint(longitude, latitude);
                tMapView.setCenterPoint(longitude, latitude);
            }
        }
        public void onProviderDisabled(String provider) {
        }
        public void onProviderEnabled(String provider) {
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    // Tmap 경로 찍기, 스레드 사용
    // TODO: 생성된 스레드는 경로 다 그리고 죽어야하는데 어떻게 죽임?
    private void drawLine(final double deptLat, final double deptLon, final double destLat, final double destLon)
    {
        new Thread() {
            public void run() {
//                TMapPoint tMapPointStart = new TMapPoint(37.570841, 126.985302); // SKT타워(출발지)
//                TMapPoint tMapPointEnd = new TMapPoint(37.551135, 126.988205); // N서울타워(목적지)

                TMapPoint tMapPointStart = new TMapPoint(deptLat, deptLon);
                TMapPoint tMapPointEnd = new TMapPoint(destLat, destLon);

                try {
                    //            TMapPolyLine tMapPolyLine = new TMapData().findPathData(tMapPointStart, tMapPointEnd);
                    TMapPolyLine tMapPolyLine = new TMapPolyLine();
                    TMapData tMapData = new TMapData();
                    tMapPolyLine = tMapData.findPathData(tMapPointStart, tMapPointEnd);
                    //            tMapPolyLine.addLinePoint(tMapPointStart);
                    //            tMapPolyLine.addLinePoint(tMapPointEnd);
                    tMapPolyLine.setLineColor(Color.BLUE);
                    tMapPolyLine.setLineWidth(2);
                    tMapView.addTMapPolyLine("Line1", tMapPolyLine);
                    Log.e("Tmap_line_test", String.valueOf(tMapPointStart));

                } catch ( Exception e ) {
                    e.printStackTrace();
                    Log.e("Tmap_line_error", String.valueOf(tMapPointStart));
                }
            }
        }.start();
    }

    // 출발지 입력하는 다이얼로그 띄움
    public void layerFromClick(View view)
    {
        // Dialog 다이얼로그 클래스로 다이얼로그를 만든다
        final Dialog layerForm = new Dialog(this); // 다이얼로그 객체 생성
        layerForm.setContentView(R.layout.map_find_dialog); // 다이얼로그 화면 등록

        final Spinner deptSpinner = layerForm.findViewById(R.id.spinner_dept);
        final Spinner deptStationSpinner = layerForm.findViewById(R.id.spinner_dept_station);

        // 지역 스피너
        // TODO:다시 선택하더라도 선택된 값이 선택되어야 함
        regionAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, deptList);
        deptSpinner.setAdapter(regionAdapter);
        deptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                // 장소 스피너, 지역이 선택되면 그에 해당하는 값을 php에서 가져옴
                GetDataTask task = new GetDataTask(MainActivity.this);
                task.execute("http://a.liroo.net/tripool/json_space_list.php", String.valueOf(deptSpinner.getItemAtPosition(position)));

                placeAdapter = new ArrayAdapter(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, deptStationList);
                deptStationSpinner.setAdapter(placeAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 입력
        Button btnInput = layerForm.findViewById(R.id.btnInput);
        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if ( deptSpinner.getSelectedItem().toString().isEmpty() || deptSpinner.getSelectedItem().toString().equals(getString(R.string.which_region)) ) {
                    Toast.makeText(getApplicationContext(), R.string.plz_select_region, Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( deptStationSpinner.getSelectedItem().toString().isEmpty() || deptStationSpinner.getSelectedItem().toString().equals(getString(R.string.which_place)) ) {
                    Toast.makeText(getApplicationContext(), R.string.plz_select_boarding_place, Toast.LENGTH_SHORT).show();
                    return;
                }
                editTextFrom.setText(deptSpinner.getSelectedItem().toString().trim() + " " + deptStationSpinner.getSelectedItem().toString().trim());

                // TODO: 이 부분에서 Tmap에 출발지 좌표를 찍어줘야 함
                // 해당 지역 + 탑승지로 php에 요청해서 좌표를 받아서 출발지 좌표를 세팅해야 함(destLat, destLon)
                // 만약, 출발지, 도착지가 다 찍혀 있으면 경로를 찍어주는 스레드를 실행함

                layerForm.dismiss();   // 다이얼로그를 닫는 메소드
            }
        });

        // 취소
        Button btnClose = layerForm.findViewById(R.id.btnCancel);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layerForm.dismiss();   // 다이얼로그를 닫는 메소드
            }
        });

        layerForm.setOwnerActivity(MainActivity.this);  // Activity에 Dialog를 등록하기
        layerForm.setCanceledOnTouchOutside(false); // 다이얼로그 바깥 영역을 클릭시 true: 다이얼로그 종료, false : 종료안됨
        layerForm.show(); // 다이얼로그 띄우기
    }

    // 도착지 입력하는 다이얼로그 띄움
    public void layerToClick(View view)
    {
        // Dialog 다이얼로그 클래스로 다이얼로그를 만든다
        final Dialog layerForm = new Dialog(this); // 다이얼로그 객체 생성
        layerForm.setContentView(R.layout.map_find_dialog); // 다이얼로그 화면 등록
        TextView placeText = layerForm.findViewById(R.id.placeText);
        placeText.setText(getString(R.string.alight_place));

        final Spinner deptSpinner = layerForm.findViewById(R.id.spinner_dept);
        final Spinner deptStationSpinner = layerForm.findViewById(R.id.spinner_dept_station);

        // 지역 스피너
        // TODO:다시 선택하더라도 선택된 값이 선택되어야 함
        regionAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, deptList);
        deptSpinner.setAdapter(regionAdapter);
        deptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                // 장소 스피너, 지역이 선택되면 그에 해당하는 값을 php에서 가져옴
                GetDataTask task = new GetDataTask(MainActivity.this);
                task.execute("http://a.liroo.net/tripool/json_space_list.php", String.valueOf(deptSpinner.getItemAtPosition(position)));

                placeAdapter = new ArrayAdapter(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, deptStationList);
                deptStationSpinner.setAdapter(placeAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 입력
        Button btnInput = layerForm.findViewById(R.id.btnInput);
        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if ( deptSpinner.getSelectedItem().toString().isEmpty() || deptSpinner.getSelectedItem().toString().equals(getString(R.string.which_region)) ) {
                    Toast.makeText(getApplicationContext(), R.string.plz_select_region, Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( deptStationSpinner.getSelectedItem().toString().isEmpty() || deptStationSpinner.getSelectedItem().toString().equals(getString(R.string.which_place)) ) {
                    Toast.makeText(getApplicationContext(), R.string.plz_select_alight_place, Toast.LENGTH_SHORT).show();
                    return;
                }
                editTextTo.setText(deptSpinner.getSelectedItem().toString().trim() + " " + deptStationSpinner.getSelectedItem().toString().trim());

                // TODO: 이 부분에서 Tmap에 도착지 좌표를 찍어줘야 함
                // 해당 지역 + 하차지로 php에 요청해서 좌표를 받아서 도착지 좌표를 세팅해야 함(destLat, destLon)
                // 만약, 출발지, 도착지가 다 찍혀 있으면 경로를 찍어주는 스레드를 실행함

                layerForm.dismiss();   //다이얼로그를 닫는 메소드입니다.
            }
        });

        // 취소
        Button btnClose = layerForm.findViewById(R.id.btnCancel);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layerForm.dismiss();   //다이얼로그를 닫는 메소드입니다.
            }
        });

        layerForm.setOwnerActivity(MainActivity.this);  // Activity에 Dialog를 등록하기
        layerForm.setCanceledOnTouchOutside(false); // 다이얼로그 바깥 영역을 클릭시 true: 다이얼로그 종료, false : 종료안됨
        layerForm.show(); // 다이얼로그 띄우기
    }

    // 지역명 세팅
    public void setData(JSONArray list) throws JSONException
    {
        deptList.clear();
        deptList.add(getString(R.string.which_region));
        deptStationList.clear();
        deptStationList.add(getString(R.string.which_place));

        for ( int i=0; i<list.length(); i++ ) {
            JSONObject item = list.getJSONObject(i);
            String mainAddr = item.getString("main_addr");
            String subAddr = item.getString("sub_addr");
            deptList.add(mainAddr+ " " + subAddr);
        }

        if ( regionAdapter != null ) {
            regionAdapter.notifyDataSetChanged();
        }
        if ( placeAdapter != null ) {
            placeAdapter.notifyDataSetChanged();
        }
    }

    // 탑승지 세팅
    public void setPlaceData(JSONArray list) throws JSONException
    {
        deptStationList.clear();
        deptStationList.add(getString(R.string.which_place));

        for ( int i=0; i<list.length(); i++ ) {
            JSONObject item = list.getJSONObject(i);
            String station = item.getString("place");
            deptStationList.add(station);
        }

        if ( placeAdapter != null ) {
            placeAdapter.notifyDataSetChanged();
        }
    }

    // 앱을 완전히 종료 FrontPage에서만 사용
    private boolean quitFlag;
    private Handler quitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg)
        {
            if ( msg.what == 0 )
                quitFlag = false;
        }
    };

    private void killAll()
    {
        app.clearActivityPool();
        finish();

        app.killApplication();
    }

    @Override
    public void onBackPressed()
    {
        if ( drawerLayout.isDrawerOpen(GravityCompat.START) ) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if ( searchDialog.getVisibility() == View.VISIBLE ) {
            searchDialog.setVisibility(View.GONE);
        }
        else if ( !quitFlag ) {
            Toast.makeText(getApplicationContext(), R.string.exit_confirm, Toast.LENGTH_LONG).show();
            quitFlag = true;
            quitHandler.sendEmptyMessageDelayed(0, 2000);
        }
        else {
            killAll();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        int id = menuItem.getItemId();

        if ( id == R.id.nav_notice ) {
            Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
            startActivity(intent);
        }
        else if ( id == R.id.nav_check_reservation ) {
            Intent intent = new Intent(getApplicationContext(), CheckReservationActivity.class);
            startActivity(intent);
        }
        else if ( id == R.id.nav_history ) {
            Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
            startActivity(intent);
        }
        else if ( id == R.id.nav_logout ) {
            app.clearActivityPool();

            SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
            SharedPreferences.Editor userEdit = userInfo.edit();
            userEdit.clear();
            userEdit.commit();

            Toast.makeText(getApplicationContext(), R.string.do_logout, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);  // 다음 화면으로 넘어가기
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private static class GetDataTask extends AsyncTask<String, Void, String>
    {
        private WeakReference<MainActivity> activityReference;
        private String region;

        // only retain a weak reference to the activity
        GetDataTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params)
        {
            String uri = params[0];
            region = params[1];

            BufferedReader bufferedReader;
            try {
                // php로 보낼 데이터 세팅
                String data = URLEncoder.encode("region", "UTF-8") + "=" + URLEncoder.encode(region, "UTF-8");

                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String json;
                while ( (json = bufferedReader.readLine()) != null ) {
                    sb.append(json+"\n");
                }
                return sb.toString().trim();
            } catch ( Exception e ) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String ret)
        {
            MainActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

            try {
                JSONObject jsonObj = new JSONObject(ret);
                JSONArray jsonDeptList = jsonObj.getJSONArray(TAG_RESULTS);
                if ( region.equals("region_list") ) {
                    activity.setData(jsonDeptList);
                }
                else {
                    activity.setPlaceData(jsonDeptList);
                }
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        }
    }

    private static class SearchTask extends AsyncTask<Object, Void, String>
    {
        private WeakReference<MainActivity> activityReference;
        private SearchItem item;

        // only retain a weak reference to the activity
        SearchTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Object... params)
        {
            String uri = (String)params[0];
            item = (SearchItem)params[1];

            BufferedReader bufferedReader;
            try {
                String data = "dept_main=" + URLEncoder.encode(item.getDeptMain(), "UTF-8");
                data += "&dept_sub=" + URLEncoder.encode(item.getDeptSub(), "UTF-8");
                data += "&departure=" + URLEncoder.encode(item.getDeparture(), "UTF-8");
                data += "&dest_main=" + URLEncoder.encode(item.getDestMain(), "UTF-8");
                data += "&dest_sub=" + URLEncoder.encode(item.getDestSub(), "UTF-8");
                data += "&destination=" + URLEncoder.encode(item.getDestination(), "UTF-8");
                data += "&dept_date=" + item.getDeptDate()/100000;        //DB에서 찾을 때는 초단위로 변경

                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String json;
                while ( (json = bufferedReader.readLine()) != null ) {
                    sb.append(json+"\n");
                }
                return sb.toString().trim();
            } catch ( Exception e ) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String ret)
        {
            MainActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

            try {
                JSONObject jsonObj = new JSONObject(ret);
                JSONArray jsonDeptList = jsonObj.getJSONArray(TAG_RESULTS);

                ArrayList<SearchItem> searchList = new ArrayList<>();
                for ( int i=0; i<jsonDeptList.length(); i++ ) {
                    JSONObject obj = jsonDeptList.getJSONObject(i);
                    searchList.add(new SearchItem(obj));
                }

                Bundle bundle = new Bundle();
                bundle.putParcelable("search_item", item);

                // 검색 결과 페이지로 이동
                Intent intent = new Intent(activity, SearchResultActivity.class);
                intent.putParcelableArrayListExtra("search_result_list", searchList);
                intent.putExtra("message", bundle);
                activity.startActivity(intent);  // 다음 화면으로 넘어가기
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        }
    }

    private static class GetNoticeTask extends AsyncTask<String, Void, String>
    {
        private WeakReference<MainActivity> activityReference;

        // only retain a weak reference to the activity
        GetNoticeTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params)
        {
            String uri = params[0];

            BufferedReader bufferedReader;
            try {
                // php로 보낼 데이터 세팅
                String data = "";
//                String data = URLEncoder.encode("region", "UTF-8") + "=" + URLEncoder.encode(region, "UTF-8");

                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String json;
                while ( (json = bufferedReader.readLine()) != null ) {
                    sb.append(json+"\n");
                }
                return sb.toString().trim();
            } catch ( Exception e ) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String ret)
        {
            MainActivity activity = activityReference.get();
            if ( activity == null || activity.isFinishing() ) return;

//            try {
//                JSONObject jsonObj = new JSONObject(ret);
//                JSONArray jsonDeptList = jsonObj.getJSONArray(TAG_RESULTS);
//                if ( region.equals("region_list") ) {
//                    activity.setData(jsonDeptList);
//                }
//                else {
//                    activity.setPlaceData(jsonDeptList);
//                }
//            } catch ( JSONException e ) {
//                e.printStackTrace();
//            }
        }
    }
}
