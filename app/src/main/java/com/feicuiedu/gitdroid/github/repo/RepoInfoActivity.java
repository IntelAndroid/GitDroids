package com.feicuiedu.gitdroid.github.repo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feicuiedu.gitdroid.R;
import com.feicuiedu.gitdroid.commons.ActivityUtils;
import com.feicuiedu.gitdroid.github.hotrepo.pager.modle.Repo;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/7 0007.
 */
public class RepoInfoActivity extends MvpActivity<RepoInfoView,RepoInfoPresenter> implements RepoInfoView{

    @Bind(R.id.ivIcon) ImageView ivIcon;
    @Bind(R.id.tvRepoInfo) TextView tvRepoInfo;
    @Bind(R.id.tvRepoStars) TextView tvRepoStars;
    @Bind(R.id.tvRepoName) TextView tvRepoName;
    @Bind(R.id.webView) WebView webView;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.toolbar) Toolbar toolbar;

    private ActivityUtils activityUtils;

    // 当前仓库
    private Repo repo;

    private static final String KEY_REPO = "key_repo";

    public static void open(Context context, @NonNull Repo repo) {
        Intent intent = new Intent(context, RepoInfoActivity.class);
        intent.putExtra(KEY_REPO, repo);
        context.startActivity(intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUtils = new ActivityUtils(this);
        setContentView(R.layout.activity_repo_info);
        // 获取仓库readme
        getPresenter().getReadme(repo);
    }

    @Override public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        // 取出仓库
        repo = (Repo) getIntent().getSerializableExtra(KEY_REPO);
        setSupportActionBar(toolbar);
        // 显示Toolbar的返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 将Toolbar的标题设置为仓库名称
        getSupportActionBar().setTitle(repo.getName());

        // 加载仓库拥有者的头像，设置仓库关键信息(描述，全名，star和fork数量)
        ImageLoader.getInstance().displayImage(repo.getOwner().getAvatar(), ivIcon);
        tvRepoInfo.setText(repo.getDescription());
        tvRepoName.setText(repo.getFullName());
        tvRepoStars.setText(String.format("star: %d  fork: %d", repo.getStargazersCount(), repo.getForksCount()));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull @Override public RepoInfoPresenter createPresenter() {
        return new RepoInfoPresenter();
    }

    @Override public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override public void setData(String htmlContent) {
        webView.loadData(htmlContent,"text/html","UTF-8");
    }

    @Override public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }
}
