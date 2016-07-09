package com.feicuiedu.gitdroid.github.repo;

import android.util.Base64;

import com.feicuiedu.gitdroid.github.hotrepo.pager.modle.Repo;
import com.feicuiedu.gitdroid.github.network.GitHubClient;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/7/7 0007.
 * <p/>
 * 仓库详情信息显示页面的业务
 */
public class RepoInfoPresenter extends MvpNullObjectBasePresenter<RepoInfoView> {

    private Call<RepoContentResult> repoContentCall;
    private Call<ResponseBody> mdCall;

    /**
     * 获取指定仓库的readme
     */
    public void getReadme(Repo repo) {
        getView().showProgress();
        String login = repo.getOwner().getLogin();
        String name = repo.getName();

        if (repoContentCall != null) repoContentCall.cancel();
        repoContentCall = GitHubClient.getInstance().getReadme(login, name);
        repoContentCall.enqueue(repoContentCallback);
    }

    private Callback<RepoContentResult> repoContentCallback = new Callback<RepoContentResult>() {
        @Override public void onResponse(Call<RepoContentResult> call, Response<RepoContentResult> response) {
            String content = response.body().getContent();
            // BASE64解码
            byte[] data = Base64.decode(content, Base64.DEFAULT);
            String mdContent = new String(data);
            // 根据Markdown格式的内容获取HTML格式的内容
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), mdContent);
            if(mdCall!=null)mdCall.cancel();
            mdCall = GitHubClient.getInstance().markdown(body);
            mdCall.enqueue(mdCallback);
        }

        @Override public void onFailure(Call<RepoContentResult> call, Throwable t) {
            getView().hideProgress();
            getView().showMessage("Error:" + t.getMessage());
        }
    };

    private Callback<ResponseBody> mdCallback = new Callback<ResponseBody>() {
        @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                String htmlContent = response.body().string();
                getView().setData(htmlContent);
                getView().hideProgress();
            } catch (IOException e) {
                onFailure(call, e);
            }
        }

        @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
            getView().hideProgress();
            getView().showMessage("Error:" + t.getMessage());
        }
    };
}