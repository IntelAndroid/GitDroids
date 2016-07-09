package com.feicuiedu.gitdroid.github.network;

import com.feicuiedu.gitdroid.github.hotrepo.pager.modle.RepoResult;
import com.feicuiedu.gitdroid.github.login.model.AccessTokenResult;
import com.feicuiedu.gitdroid.github.login.model.User;
import com.feicuiedu.gitdroid.github.repo.RepoContentResult;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/7/5 0005.
 */
public class GitHubClient implements GitHubApi {

    private static GitHubClient sClient;

    public static GitHubClient getInstance() {
        if (sClient == null) {
            sClient = new GitHubClient();
        }
        return sClient;
    }

    private final GitHubApi gitHubApi;

    private GitHubClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor())// 添加拦截器, 处理Log (注意添加依赖包)
                .addInterceptor(new TokenInterceptor()) // 添加拦截器, 处理AccessToken
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        gitHubApi = retrofit.create(GitHubApi.class);
    }

    @Override public Call<AccessTokenResult> getOAuthToken(@Field("client_id") String client, @Field("client_secret") String clientSecret, @Field("code") String code) {
        return gitHubApi.getOAuthToken(client, clientSecret, code);
    }

    @Override public Call<User> getUserInfo() {
        return gitHubApi.getUserInfo();
    }

    @Override public Call<RepoResult> searchRepo(@Query("q") String query, @Query("page") int pageId) {
        return gitHubApi.searchRepo(query,pageId);
    }

    @Override public Call<RepoContentResult> getReadme(@Path("owner") String owner, @Path("repo") String repo) {
        return gitHubApi.getReadme(owner,repo);
    }

    @Override public Call<ResponseBody> markdown(@Body RequestBody body) {
        return gitHubApi.markdown(body);
    }
}
