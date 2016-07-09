package com.feicuiedu.gitdroid.favorite.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Administrator on 2016/7/8 0008.
 * <p/>
 * 本地仓库表
 */
@DatabaseTable(tableName = "repositories")
public class LocalRepo {
    // 主键
    @DatabaseField(id = true)
    private long id;

    // 仓库名称
    @DatabaseField
    private String name;

    // 仓库全名
    @DatabaseField(columnName = "full_name")
    @SerializedName("full_name")
    private String fullName;

    // 仓库描述
    @DatabaseField
    private String description;

    // 本仓库的star数量 (在GitHub上被关注的数量)
    @SerializedName("stargazers_count")
    @DatabaseField(columnName = "stargazers_count")
    private int starCount;

    // 本仓库的fork数量 (在GitHub上被拷贝的数量)
    @SerializedName("forks_count")
    @DatabaseField(columnName = "forks_count")
    private int forkCount;

    // 用户头像路径路径
    @DatabaseField(columnName = "avatar_url")
    @SerializedName("avatar_url")
    private String avatar;

    // 是一个外键
    @DatabaseField(canBeNull = true, foreign = true, columnName = COLUMN_GROUP_ID)
    @SerializedName("group")
    private RepoGroup repoGroup;

    public static final String COLUMN_GROUP_ID = "group_id";

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public void setForkCount(int forkCount) {
        this.forkCount = forkCount;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setRepoGroup(RepoGroup repoGroup) {
        this.repoGroup = repoGroup;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    public int getStarCount() {
        return starCount;
    }

    public int getForkCount() {
        return forkCount;
    }

    public String getAvatar() {
        return avatar;
    }

    public RepoGroup getRepoGroup() {
        return repoGroup;
    }

    @Override public boolean equals(Object o) {
        return o != null && o instanceof LocalRepo && this.id == ((LocalRepo)o).getId();
    }

    public static List<LocalRepo> getDefaultLocalRepos(Context context) {

        try {
            InputStream inputStream = context.getAssets().open("defaultrepos.json");
            // 将流转换为字符串
            String content = IOUtils.toString(inputStream);
            // 将字符串转换为对象数组
            Gson gson = new Gson();
            return gson.fromJson(content, new TypeToken<List<LocalRepo>>() {
            }.getType());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
