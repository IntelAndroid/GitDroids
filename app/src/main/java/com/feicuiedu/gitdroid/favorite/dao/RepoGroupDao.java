package com.feicuiedu.gitdroid.favorite.dao;

import com.feicuiedu.gitdroid.favorite.model.RepoGroup;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2016/7/8 0008.
 */
public class RepoGroupDao {

    private Dao<RepoGroup, Long> dao;

    public RepoGroupDao(DbHelper dbHelper) {
        try {
            // 创建RepoGroup表的Dao
            dao = dbHelper.getDao(RepoGroup.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询所有 RepoGroup,仓库类别
     */
    public List<RepoGroup> queryForAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public RepoGroup queryForId(long id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createOrUpdate(RepoGroup repoGroup) {
        try {
            dao.createOrUpdate(repoGroup);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加和更新数据
     */
    public void createOrUpdate(List<RepoGroup> repoGroups) {
        for (RepoGroup repoGroup : repoGroups) {
            createOrUpdate(repoGroup);
        }
    }
}
