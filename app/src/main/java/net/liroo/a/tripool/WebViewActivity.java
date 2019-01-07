package net.liroo.a.tripool;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

public class WebViewActivity extends BaseActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = "", url = "";
        Bundle bundle = getIntent().getBundleExtra("message");
        if ( bundle != null ) {
            title = bundle.getString("title");
            url = bundle.getString("url");
        }

        TextView toolbarText = findViewById(R.id.toolbarText);
        toolbarText.setText(title);

        WebView webView = findViewById(R.id.webView);
        webView.loadUrl(url);
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
