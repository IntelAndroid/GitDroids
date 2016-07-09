package com.feicuiedu.gitdroid.github.hotrepo.pager.view;

import com.feicuiedu.gitdroid.github.hotrepo.pager.modle.Repo;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2016/7/1 0001.
 * <p/>
 * 不同语言仓库视图抽象
 */
public interface LanguageView extends MvpView, PtrView<List<Repo>>, LoadMoreView<List<Repo>> {
}
