package net.liroo.a.tripool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import net.liroo.a.tripool.obj.PaymentScheme;
import net.liroo.a.tripool.obj.ReservationItem;
import net.liroo.a.tripool.obj.SearchItem;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class PurchaseActivity extends BaseActivity
{
    private WebView webView;
    private int payAmount, chk_no;
    private String uid;

//    private Long dept_date;
    private String dept_date;

    private final String APP_SCHEME = "iamportapp://";

    private SearchItem searchItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Bundle bundle = getIntent().getBundleExtra("message");
        searchItem = bundle.getParcelable("search_item");
        payAmount = bundle.getInt("payAmount");
        chk_no = Integer.parseInt(searchItem.getNo());
//        dept_date = Long.parseLong(String.valueOf(searchItem.getDeptDate()));
        dept_date = String.valueOf(searchItem.getDeptDate());
        if ( searchItem == null ) {
            finish();
            return;
        }
        // 로그인 정보
        SharedPreferences userInfo = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        uid = userInfo.getString("u_id", "");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView toolbarText = findViewById(R.id.toolbarText);
        toolbarText.setText("결제하기");

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if ( !url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:") ) {
                    Intent intent = null;
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);    // Intent URI 처리
                        Uri uri = Uri.parse(intent.getDataString());

                        startActivity(new Intent(Intent.ACTION_VIEW, uri)); // 해당되는 Activity 실행
                        return true;
                    } catch ( URISyntaxException ex ) {
                        return false;
                    } catch ( ActivityNotFoundException e ) {
                        if ( intent == null )   return false;

                        if ( handleNotFoundPaymentScheme(intent.getScheme()) )  return true;    // 설치되지 않은 앱에 대해 사전 처리(Google Play 이동 등 필요한 처리)

                        String packageName = intent.getPackage();
                        if ( packageName != null ) {    // packageName이 있는 경우에는 Google Play에서 검색을 기본
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                            return true;
                        }

                        return false;
                    }
                }
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(PurchaseActivity.this)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebInterface(), "TripoolApp");

        if ( android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }

        if ( getIntent().getData() == null ) {
//            String chk_uid = null;
//            try {
//                chk_uid = URLEncoder.encode(uid, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            webView.loadUrl("http://a.liroo.net/tripool/payments/req_payment.php?" +
                    "chk_amount=" + String.valueOf(payAmount) +
                    "&chk_no=" + chk_no +
                    "&u_id=" + uid +
//                    "&dept_date="+dept_date +
                    "&dept_date=" + searchItem.getDeptDate() +
                    "&people=" + searchItem.getPeople() +
                    "&luggage=" + searchItem.getLuggage()
                    );
        }
        else {
            // isp 인증 후 복귀했을 때 결제 후속조치
            String url = getIntent().getData().toString();
            if ( url.startsWith(APP_SCHEME) ) {
                String redirectURL = url.substring(APP_SCHEME.length()+3);
                webView.loadUrl(redirectURL);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        String url = intent.toString();
        if ( url.startsWith(APP_SCHEME) ) {
            // "iamportapp://https://pgcompany.com/foo/bar"와 같은 형태로 들어옴
            String redirectURL = url.substring(APP_SCHEME.length() + "://".length());
            webView.loadUrl(redirectURL);
        }
    }

    /**
     * @return 해당 scheme에 대해 처리를 직접 하는지 여부
     *
     * 결제를 위한 3rd-party 앱이 아직 설치되어있지 않아 ActivityNotFoundException이 발생하는 경우 처리합니다.
     * 여기서 handler되지않은 scheme에 대해서는 intent로부터 Package정보 추출이 가능하다면 다음에서 packageName으로 market이동합니다.
     */
    private boolean handleNotFoundPaymentScheme(String scheme)
    {
        // PG사에서 호출하는 url에 package 정보가 없어 ActivityNotFoundException이 난 후 market 실행이 안되는 경우
        if ( PaymentScheme.ISP.equalsIgnoreCase(scheme) ) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PaymentScheme.PACKAGE_ISP)));
            return true;
        }
        else if ( PaymentScheme.BANKPAY.equalsIgnoreCase(scheme) ) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PaymentScheme.PACKAGE_BANKPAY)));
            return true;
        }
        return false;
    }

    private class WebInterface
    {
        @JavascriptInterface
        public void purchaseComplete() {
            setResult(RESULT_OK);

            Bundle bundle = new Bundle();
            bundle.putParcelable("reservationItem", new ReservationItem(searchItem));
            bundle.putBoolean("isHistory", false);

            Intent intent = new Intent(PurchaseActivity.this, ReservationDetailActivity.class);
            intent.putExtra("message", bundle);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        if ( webView.canGoBack() ) {
            webView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemID = item.getItemId();
        if ( itemID == android.R.id.home ) {   // 뒤로
            onBackPressed();
        }
        return true;
    }
}
