package com.feicuiedu.gitdroid.favorite;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.feicuiedu.gitdroid.R;
import com.feicuiedu.gitdroid.commons.ActivityUtils;
import com.feicuiedu.gitdroid.favorite.dao.DbHelper;
import com.feicuiedu.gitdroid.favorite.dao.LocalRepoDao;
import com.feicuiedu.gitdroid.favorite.dao.RepoGroupDao;
import com.feicuiedu.gitdroid.favorite.model.LocalRepo;
import com.feicuiedu.gitdroid.favorite.model.RepoGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/8 0008.
 */
public class FavoriteFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    @Bind(R.id.tvGroupType) TextView tvGroupType;
    @Bind(R.id.listView) ListView listView;

    private LocalRepoAdapter adapter;

    private RepoGroupDao repoGroupDao; // 仓库类别dao
    private LocalRepoDao localRepoDao; // 本地仓库dao

    private ActivityUtils activityUtils;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repoGroupDao = new RepoGroupDao(DbHelper.getInstance(getContext()));
        localRepoDao = new LocalRepoDao(DbHelper.getInstance(getContext()));
        activityUtils = new ActivityUtils(this);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }


    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        adapter = new LocalRepoAdapter();
        listView.setAdapter(adapter);
        // 首先进入时默认显示全部本地仓库
        adapter.setData(localRepoDao.queryForAll());
        //
        registerForContextMenu(listView);
    }

    private LocalRepo currentLocalRepo; // 当前操作的本地仓库

    @Override public void onCreateContextMenu(
            ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listView) {
            // 得到当前作用在listview上的menu上按下的位置
            AdapterView.AdapterContextMenuInfo adapterMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
            int position = adapterMenuInfo.position;
            // 得到当前操作的本地仓库
            currentLocalRepo = adapter.getItem(position);
            //
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.menu_context_favorite, menu);
            //
            SubMenu subMenu = menu.findItem(R.id.sub_menu_move).getSubMenu();
            // 从本地数据库拿出所有分类
            List<RepoGroup> localGroups = repoGroupDao.queryForAll();
            for (RepoGroup repoGroup : localGroups) {
                // 都加到menu_group_move这个组上
                subMenu.add(R.id.menu_group_move, repoGroup.getId(), Menu.NONE, repoGroup.getName());
            }
        }
    }

    @Override public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete) {
            localRepoDao.delete(currentLocalRepo);
            resetData();
            return true;
        }
        int groupId = item.getGroupId();
        // 在移动至里 (将当前操作的仓库的 类别进行了更改, reset)
        if (groupId == R.id.menu_group_move) {
            if (id == R.id.repo_group_no) { // 未分类
                currentLocalRepo.setRepoGroup(null);
            } else {
                currentLocalRepo.setRepoGroup(repoGroupDao.queryForId(id));
            }
            localRepoDao.createOrUpdate(currentLocalRepo);
            resetData();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @OnClick(R.id.btnFilter)
    public void showPopupMenu(View view) {
        // PopupMenu
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.setOnMenuItemClickListener(this);
        // menu项(上面默认只有全部和未分类)
        popupMenu.inflate(R.menu.menu_popup_repo_groups);
        // 拿到Menu对象 (上面默认只有全部和未分类 -  添加上我们自己的)
        Menu menu = popupMenu.getMenu();
        // 拿到我们本地数据库内的所有类别数据
        List<RepoGroup> repoGroups = repoGroupDao.queryForAll();
        for (RepoGroup repoGroup : repoGroups) {
            // groupid不要,  itemId
            menu.add(Menu.NONE, repoGroup.getId(), Menu.NONE, repoGroup.getName());
        }
        popupMenu.show();
    }

    private int repoGroupID;

    @Override public boolean onMenuItemClick(MenuItem item) {
        tvGroupType.setText(item.getTitle());
        repoGroupID = item.getItemId(); // 12312312 34234324 1 2 3 4 5 6
        resetData();
        return true;
    }

    private void resetData() {
        // ListView控件 ,上面的数据要重置更新刷新
        switch (repoGroupID) {
            case R.id.repo_group_all:
                adapter.setData(localRepoDao.queryForAll());
                break;
            case R.id.repo_group_no:
                adapter.setData(localRepoDao.queryForNoGroup());
                break;
            default:
                adapter.setData(localRepoDao.queryForGroupId(repoGroupID));
                break;
        }
    }
}
