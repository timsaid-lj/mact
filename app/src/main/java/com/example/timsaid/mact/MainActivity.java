package com.example.timsaid.mact;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private String url = "http://192.168.1.102:8080/mact_web/a/mact/mobile/index";
    private String Tag = "MQL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        webView = (WebView) this.findViewById(R.id.webView);

        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);

        //支持弹出alert提示
        webView.setWebChromeClient(new WebChromeClient());


        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new Radiojs(this,webView),"$App");


        //如果不设置WebViewClient，请求会跳转系统浏览器
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //该方法在Build.VERSION_CODES.LOLLIPOP以前有效，从Build.VERSION_CODES.LOLLIPOP起，建议使用shouldOverrideUrlLoading(WebView, WebResourceRequest)} instead
                //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
                //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242

                if (url.toString().contains("sina.cn")){
                    webView.loadUrl(url);
                    return true;
                }

                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
            {
                //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
                //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.getUrl().toString().contains("sina.cn")){
                        view.loadUrl(url);
                        return true;
                    }
                }

                return false;
            }

        });
        webView.loadUrl(url);
    }



}


/*
public class MainActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        webView = (WebView) this.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webView.getSettings().setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        */
/*webView.addJavascriptInterface(new WebPlugin(), "WebPlugin");
        webView.loadUrl(this.getString(R.string.server_url));*//*

        webView.addJavascriptInterface(new Radiojs(this,webView),"$App");

       */
/* webView.loadUrl("http://192.168.1.100:3000/user/information-collection");*//*

        webView.loadUrl("http://192.168.1.100:8080/smart_javamg");

    }
}*/
