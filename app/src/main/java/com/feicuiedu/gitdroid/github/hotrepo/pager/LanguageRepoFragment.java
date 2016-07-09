package com.feicuiedu.gitdroid.github.hotrepo.pager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.feicuiedu.gitdroid.R;
import com.feicuiedu.gitdroid.commons.ActivityUtils;
import com.feicuiedu.gitdroid.components.FooterView;
import com.feicuiedu.gitdroid.favorite.dao.DbHelper;
import com.feicuiedu.gitdroid.favorite.dao.LocalRepoDao;
import com.feicuiedu.gitdroid.favorite.model.LocalRepo;
import com.feicuiedu.gitdroid.favorite.model.RepoConverter;
import com.feicuiedu.gitdroid.github.hotrepo.Language;
import com.feicuiedu.gitdroid.github.hotrepo.pager.modle.Repo;
import com.feicuiedu.gitdroid.github.hotrepo.pager.view.LanguageView;
import com.feicuiedu.gitdroid.github.repo.RepoInfoActivity;
import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.mugen.Mugen;
import com.mugen.MugenCallbacks;

import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

/**
 * 本Fragment的主要内容是一个ListView，我们通过GitHub API查询某种编程语言最热门的开源库，
 * 查询结果根据star数量排序。此GitHub API有分页特性，分页索引以1开始，每页默认有30项。
 * 更多内容可以查阅GitHub API文档。
 * <p/>
 * <p/>
 * 我们使用了三方库android-Ultra-Pull-To-Refresh来实现下拉刷新特性，这是一位中国程序员构建的优秀的开源库。
 * 你可以自己尝试使用google的SwipeRefreshLayout来替代，这一工作应该是很简单的。
 * <p/>
 * <p/>
 * 至于无穷滚动特性，有很多不同的开源实现。我们使用了一个微型库Mugen的实现来节省时间，
 * 当然，即使从头自己来实现这一功能也并不困难。
 */
public class LanguageRepoFragment extends MvpFragment<LanguageView, LanguageRepoPresenter> implements LanguageView {

    private static final String KEY_LANGUAGE = "key_language";

    /**
     * 获取(每次重新创建)当前Fragment对象
     * <p/>
     * 当Fragment需要进行参数传递时，应该使用Bundle进行处理,我们这里就是将语言类型传入了(在获取语言仓库列表数据时要用到)
     * <p/>
     */
    public static LanguageRepoFragment getInstance(Language language) {
        LanguageRepoFragment fragment = new LanguageRepoFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_LANGUAGE, language);
        fragment.setArguments(args);
        return fragment;
    }

    private Language getLanguage() {
        return (Language) getArguments().getSerializable(KEY_LANGUAGE);
    }

    // 用于下拉刷新
    @Bind(R.id.ptrClassicFrameLayout) PtrClassicFrameLayout ptrFrameLayout;
    // 本Fragment的主要内容
    @Bind(R.id.lvRepos) ListView listView;
    // 下拉刷新如果得到的结果是空的，显示此视图
    @Bind(R.id.emptyView) TextView emptyView;
    // 下拉刷新发生异常，显示此视图
    @Bind(R.id.errorView) TextView errorView;

    @BindString(R.string.refresh_error) String refreshError;

    private LanguageRepoAdapter adapter;
    // 上拉加载时，显示在ListView最后的视图
    private FooterView footerView;

    private ActivityUtils activityUtils;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUtils = new ActivityUtils(this);
        adapter = new LanguageRepoAdapter();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_repo_list, container, false);
    }

    // 重写Mosby库父类MvpFragment的方法,返回当前视图所使用的Presenter对象
    @Override public LanguageRepoPresenter createPresenter() {
        return new LanguageRepoPresenter(getLanguage());
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        // 注意此处必须调用父类方法
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取当前click的仓库
                Repo repo = adapter.getItem(position);
                RepoInfoActivity.open(getContext(), repo);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Repo repo = adapter.getItem(position);
                LocalRepo localRepo = RepoConverter.convert(repo);
                // 将当前长按的Repo(已转换为LocalRepo)添加到本地仓库表
                new LocalRepoDao(DbHelper.getInstance(getContext())).createOrUpdate(localRepo);
                activityUtils.showToast(R.string.set_favorite_success);
                return true;
            }
        });
        // 初始下拉刷新
        initPullToRefresh();
        // 初始上拉加载
        initLoadMoreScroll();
        // 如果当前页面没有数据，开始自动刷新
        if (adapter.getCount() == 0) {
            ptrFrameLayout.postDelayed(new Runnable() {
                @Override public void run() {
                    ptrFrameLayout.autoRefresh();
                }
            }, 200);
        }
    }

    private void initPullToRefresh() {
        // 使用本对象作为key，来记录上一次刷新时间，如果两次下拉间隔太近，不会触发刷新方法
        ptrFrameLayout.setLastUpdateTimeRelateObject(this);
        ptrFrameLayout.setBackgroundResource(R.color.colorRefresh);
        // 关闭Header所耗时长
        ptrFrameLayout.setDurationToCloseHeader(1500);

        // 以下的代码只是一个好玩的Header效果，非什么重要内容
        StoreHouseHeader header = new StoreHouseHeader(getContext());
        header.setPadding(0, 60, 0, 60);
        header.initWithString("I like " + getLanguage().getName());
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
        // 下拉刷新处理
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override public void onRefreshBegin(PtrFrameLayout frame) {
                // 执行下拉刷新数据业务
                getPresenter().refresh();
            }
        });
    }

    private void initLoadMoreScroll() {
        footerView = new FooterView(getContext());
        // 当加载更多失败时，用户点击FooterView，会再次触发加载
        footerView.setErrorClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                presenter.loadMore();
            }
        });
        // 使用了一个微型库Mugen来处理滚动监听
        Mugen.with(listView, new MugenCallbacks() {
            // ListView滚动到底部，触发加载更多，此处要执行加载更多的业务逻辑
            @Override public void onLoadMore() {
                // 执行上拉加载数据业务
                getPresenter().loadMore();
            }

            // 是否正在加载，此方法用来避免重复加载
            @Override public boolean isLoading() {
                return listView.getFooterViewsCount() > 0 && footerView.isLoading();
            }

            // 是否所有数据都已加载
            @Override public boolean hasLoadedAllItems() {
                return listView.getFooterViewsCount() > 0 && footerView.isComplete();
            }
        }).start();
    }

    @OnClick({R.id.emptyView, R.id.errorView})
    public void autoRefresh() {
        activityUtils.showToast("auto");
        ptrFrameLayout.autoRefresh();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    // 这是拉刷新视图的实现----------------------------------------------------------------
    @Override public void showContentView() {
        ptrFrameLayout.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    @Override public void showErroView(String msg) {
        ptrFrameLayout.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(String.format(refreshError, msg));
    }

    @Override public void showEmptyView() {
        ptrFrameLayout.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    @Override public void showMessage(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override public void refreshData(List<Repo> datas) {
        adapter.clear();
        if (datas == null) return;
        adapter.addAll(datas);
    }

    @Override public void stopRefresh() {
        ptrFrameLayout.refreshComplete(); // 下拉刷新完成
    }

    // 这是上拉加载更多视图层实现------------------------------------------------------
    @Override public void addMoreData(List<Repo> datas) {
        if (datas == null) return;
        adapter.addAll(datas);
    }

    @Override public void hideLoadMore() {
        listView.removeFooterView(footerView);
    }

    @Override public void showLoadMoreLoading() {
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(footerView);
        }
        footerView.showLoading();
    }

    @Override public void showLoadMoreErro(String msg) {
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(footerView);
        }
        footerView.showError(msg);
    }

    @Override public void showLoadMoreEnd() {
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(footerView);
        }
        footerView.showComplete();
    }
}