package com.feicuiedu.gitdroid.github.login;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Administrator on 2016/7/5 0005.
 *
 * <p/>
 * 这是登陆页面的视图抽象
 */
public interface LoginView extends MvpView{

    // 显示进度条
    void showProgress();

    // 重置webView
    void resetWeb();

    void showMessage(String msg);

    // 导航至主页面
    void navigateToMain();
}
