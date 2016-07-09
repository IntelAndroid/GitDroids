package com.feicuiedu.gitdroid.github.login;

import com.feicuiedu.gitdroid.github.login.model.AccessTokenResult;
import com.feicuiedu.gitdroid.github.login.model.CurrentUser;
import com.feicuiedu.gitdroid.github.login.model.User;
import com.feicuiedu.gitdroid.github.network.GitHubApi;
import com.feicuiedu.gitdroid.github.network.GitHubClient;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/7/5 0005.
 * <p/>
 * 此类是处理登陆用例的, 并且在登陆的过程中，将触发调用LoginView
 * <p/>
 * <p/>
 * 登录过程遵循标准的OAuth2.0协议。
 * <p/>
 * 用户通过WebView登录GitHub网站，如果登录成功，且用户给我们授权，GitHub会访问我们的回调地址，给我们一个授权码。
 * <p/>
 * 我们就能过授权码去获得访问令牌, 最终就有权利访问信息了.
 */
public class LoginPresenter extends MvpNullObjectBasePresenter<LoginView> {

    private Call<AccessTokenResult> tokenCall;
    private Call<User> userCall;

    /**
     * 此方法是本Presenter中最重要的方法。视图会调用这个方法来触发登录用例。
     *
     * @param code 用户登录GitHub后，GitHub给我们的访问令牌。
     */
    public void login(String code) {
        getView().showProgress();
        if (tokenCall != null) tokenCall.cancel();
        tokenCall = GitHubClient.getInstance().getOAuthToken(GitHubApi.CLIENT_ID, GitHubApi.CLIENT_SECRET, code);
        tokenCall.enqueue(tokenCallback);
    }

    // 获取AccessToken的回调
    private Callback<AccessTokenResult> tokenCallback = new Callback<AccessTokenResult>() {
        @Override public void onResponse(Call<AccessTokenResult> call, Response<AccessTokenResult> response) {
            // 保存token到内存里面
            String token = response.body().getAccessToken();
            CurrentUser.setAccessToken(token);
            //
            if (userCall != null) userCall.cancel();
            userCall = GitHubClient.getInstance().getUserInfo();
            userCall.enqueue(userCallback);
        }

        @Override public void onFailure(Call<AccessTokenResult> call, Throwable t) {
            getView().showMessage("Fail:" + t.getMessage());
            // 失败，重置WebView
            getView().showProgress();
            getView().resetWeb();

        }
    };

    // 获取用户信息的回调
    private Callback<User> userCallback = new Callback<User>() {
        @Override public void onResponse(Call<User> call, Response<User> response) {
            // 保存user到内存里面
            User user = response.body();
            CurrentUser.setUser(user);
            // 导航至主页面
            getView().showMessage("登陆成功");
            getView().navigateToMain();
        }

        @Override public void onFailure(Call<User> call, Throwable t) {
            // 清除缓存的用户信息，
            CurrentUser.clear();
            getView().showMessage("Fail:" + t.getMessage());
            // 重置WebView
            getView().showProgress();
            getView().resetWeb();
        }
    };

    @Override public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (!retainInstance) {
            if (tokenCall != null) tokenCall.cancel();
            if (userCall != null) userCall.cancel();
        }
    }
}
