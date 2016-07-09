package com.feicuiedu.gitdroid.github.main;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.feicuiedu.gitdroid.R;
import com.feicuiedu.gitdroid.commons.ActivityUtils;
import com.feicuiedu.gitdroid.favorite.FavoriteFragment;
import com.feicuiedu.gitdroid.github.hotrepo.HotRepoFragment;
import com.feicuiedu.gitdroid.github.login.LoginActivity;
import com.feicuiedu.gitdroid.github.login.model.CurrentUser;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;


// GitHub帐号登陆我们的应用

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.drawerLayout) DrawerLayout drawerLayout; // 抽屉(包含内容+侧滑菜单)
    @Bind(R.id.navigationView) NavigationView navigationView; // 侧滑菜单视图

    private ActivityUtils activityUtils;
    private MenuItem menuItem;

    // 热门仓库页面Fragment
    private HotRepoFragment hotRepoFragment;
    // 我的收藏页面
    private FavoriteFragment favoriteFragment;

    private Button btnLogin;
    private ImageView ivIcon; // 用户头像

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);
        // ActionBar处理
        setSupportActionBar(toolbar);
        // 设置navigationView的监听器
        navigationView.setNavigationItemSelectedListener(this);
        // 设置Toolbar左上角切换侧滑菜单的按钮
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ivIcon = (ImageView) ButterKnife.findById(navigationView.getHeaderView(0), R.id.ivIcon);
        // 登陆
        btnLogin = (Button)ButterKnife.findById(navigationView.getHeaderView(0), R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                activityUtils.startActivity(LoginActivity.class);
            }
        });
        // 默认第一个menu项为选中(最热门)
        menuItem = navigationView.getMenu().findItem(R.id.github_hot_repo);
        menuItem.setChecked(true);
        // 默认显示的是hotRepoFragment热门仓库
        hotRepoFragment = new HotRepoFragment();
        replaceFragment(hotRepoFragment);
    }

    @Override protected void onStart() {
        super.onStart();
        // 还没有授权登陆
        if(CurrentUser.isEmpty()){
            btnLogin.setText(R.string.login_github);
            return;
        }
        // 已经授权登陆
        btnLogin.setText(R.string.switch_account);
        getSupportActionBar().setTitle(CurrentUser.getUser().getName());
        // 设置用户头像
        String photoUrl = CurrentUser.getUser().getAvatar();
        ImageLoader.getInstance().displayImage(photoUrl,ivIcon);
    }

    @Override public boolean onNavigationItemSelected(MenuItem item) {
        // 将默认选中项“手动”设置为false
        if (menuItem.isChecked()) {
            menuItem.setChecked(false);
        }
        switch (item.getItemId()) {
            case R.id.github_hot_repo:
                if (! hotRepoFragment.isAdded()) {
                    replaceFragment(hotRepoFragment);
                }
                break;
            case R.id.arsenal_my_repo:
                if (favoriteFragment == null) favoriteFragment = new FavoriteFragment();
                if (! favoriteFragment.isAdded()) {
                    replaceFragment(favoriteFragment);
                }
                break;
            case R.id.tips_daily:
                break;
        }
        drawerLayout.post(new Runnable() {
            @Override public void run() {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // 返回true，代表将该菜单项变为checked状态
        return true;
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        // 如NavigationView是开的 -> 关闭
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        // 如NavigationView是关的 -> 退出当前Activity
        else {
            super.onBackPressed();
        }
    }

    // 替换不同的内容Fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}