package com.feicuiedu.gitdroid.github.hotrepo.pager.modle;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 搜索仓库结果
 */
public class RepoResult {
//    "total_count": 2103761,
//            "incomplete_results": false,
//            "items":[]
    // 总量
    @SerializedName("total_count")
    private int totalCount;

    // 仓库列表
    @SerializedName("items")
    private List<Repo> repoList;

    @SerializedName("incomplete_results")
    private boolean incompleteResults;

    public int getTotalCount() {
        return totalCount;
    }

    public boolean isIncompleteResults() {
        return incompleteResults;
    }

    public List<Repo> getRepoList() {
        return repoList;
    }
}
