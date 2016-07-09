package com.feicuiedu.gitdroid.favorite.model;

import android.content.Context;

import com.google.gson.Gson;
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
 * 本地收藏仓库类别表
 */
@DatabaseTable(tableName = "repostiory_groups")
public class RepoGroup {

    @DatabaseField(id = true)
    private int id;

    @DatabaseField(columnName = "NAME")
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static List<RepoGroup> DEFAULT_GROUPS;

    public static List<RepoGroup> getDefaultGroups(Context context) {
        if (DEFAULT_GROUPS != null) return DEFAULT_GROUPS;

        try {
            InputStream inputStream = context.getAssets().open("repogroup.json");
            String content = IOUtils.toString(inputStream);

            Gson gson = new Gson();
            DEFAULT_GROUPS = gson.fromJson(content, new TypeToken<List<RepoGroup>>() {
            }.getType());

            return DEFAULT_GROUPS;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}