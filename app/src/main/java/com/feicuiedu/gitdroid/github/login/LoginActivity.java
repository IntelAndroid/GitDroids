package com.feicuiedu.gitdroid.github.login;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.feicuiedu.gitdroid.R;
import com.feicuiedu.gitdroid.commons.ActivityUtils;
import com.feicuiedu.gitdroid.github.main.MainActivity;
import com.feicuiedu.gitdroid.github.network.GitHubApi;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2016/7/5 0005.
 *
 */
public class LoginActivity extends MvpActivity<LoginView,LoginPresenter> implements LoginView{
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.webView) WebView webView;
    // 显示一个Gif图片作为加载动画
    @Bind(R.id.gifImageView) GifImageView gifImageView;

    private ActivityUtils activityUtils;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUtils = new ActivityUtils(this);
        setContentView(R.layout.activity_login);
    }

    @Override public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        // 显示标题栏左上角的返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initWebView();
    }

    // WebView的初始化
    private void initWebView() {
        // 删除所有的Cookie，主要是为了清除以前的登录历史记录
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        // 授权登陆URL
        webView.loadUrl(GitHubApi.AUTH_URL);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);
    }

    private WebViewClient webViewClient = new WebViewClient(){
        @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            // 检测加载到的新URL是否是用我们规定好的CALL_BACK开头的
            if(GitHubApi.CALL_BACK.equals(uri.getScheme())){
                // 获取授权码
                String code = uri.getQueryParameter("code");
                // 执行登陆的操作Presenter
                getPresenter().login(code);
                //
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    };

    private WebChromeClient webChromeClient = new WebChromeClient(){
        @Override public void onProgressChanged(WebView view, int newProgress) {
            if(newProgress == 100){
                gifImageView.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull @Override public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override public void showProgress() {
        gifImageView.setVisibility(View.VISIBLE);
    }

    @Override public void resetWeb() {
        initWebView();
    }

    @Override public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override public void navigateToMain() {
        activityUtils.startActivity(MainActivity.class);
        finish();
    }
}