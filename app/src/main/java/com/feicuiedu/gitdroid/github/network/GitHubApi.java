package com.feicuiedu.gitdroid.github.network;

import com.feicuiedu.gitdroid.github.hotrepo.pager.modle.RepoResult;
import com.feicuiedu.gitdroid.github.login.model.AccessTokenResult;
import com.feicuiedu.gitdroid.github.login.model.User;
import com.feicuiedu.gitdroid.github.repo.RepoContentResult;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/7/5 0005.
 */
public interface GitHubApi {
    // GitHub开发者，申请时填写的(重定向返回时的一个标记)
    String CALL_BACK = "feicui";
    // GitHub开发者，申请就行
    String CLIENT_ID = "aa7a3fb1b42f8c07a232";
    String CLIENT_SECRET = "841a9cfd15ded1abb9d75ba51d2d8dd0189ebb77";

    // 授权时申请的可访问域
    String INITIAL_SCOPE = "user,public_repo,repo";

    // WebView来加载此URL,用来显示GitHub的登陆页面
    String AUTH_URL =
            "https://github.com/login/oauth/authorize?client_id=" +
                    CLIENT_ID + "&" + "scope=" + INITIAL_SCOPE;

    /**
     * 获取OAuth 2.0协议的AccessToken
     *
     * @param client       @see {@link #CLIENT_ID}
     * @param clientSecret @see {@link #CLIENT_SECRET}
     * @param code         授权码
     * @return 授权结果
     */
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("https://github.com/login/oauth/access_token") Call<AccessTokenResult> getOAuthToken(
            @Field("client_id") String client,
            @Field("client_secret") String clientSecret,
            @Field("code") String code);

    /**
     * @return 获取用户信息
     */
    @GET("user") Call<User> getUserInfo();

    /**
     * @param query  查询参数           java [232323123] items[{},{},{}]
     * @param pageId 查询页数，从1开始
     * @return 查询结果
     */
    @GET("/search/repositories")
    Call<RepoResult> searchRepo(
            @Query("q") String query,
            @Query("page") int pageId);

    // https://api.github.com/repos/square/okhttp/readme
    /**
     * @param owner 仓库的拥有者
     * @param repo 仓库的名称
     * @return 仓库的Readme页面的内容，{@link RepoContentResult#content}将是Markdown格式。
     */
    @GET("repos/{owner}/{repo}/readme")
    Call<RepoContentResult> getReadme(@Path("owner") String owner, @Path("repo") String repo);

    /**
     * 获取一个Markdown内容对应的HTMl页面
     * @param body 请求体，内容来自{@link RepoContentResult#content}
     * @return Call对象
     */
    @Headers({
            "Content-Type:text/plain"
    })
    @POST("/markdown/raw")
    Call<ResponseBody> markdown(@Body RequestBody body);
}