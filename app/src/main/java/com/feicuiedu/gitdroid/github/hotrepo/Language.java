package com.feicuiedu.gitdroid.github.hotrepo;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/7/7 0007.
 */
public class Language implements Serializable{

    private String path; //
    private String name; //

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    private static List<Language> DEFAULT_LANGS;

    public static List<Language> getDefaultLanguage(Context context) {
        if (DEFAULT_LANGS != null)return DEFAULT_LANGS;
        try {
            //
            InputStream inputStream = context.getAssets().open("langs.json");
            String content = IOUtils.toString(inputStream);
            //
            Gson gson = new Gson();
            DEFAULT_LANGS = gson.fromJson(content, new TypeToken<List<Language>>(){}.getType());
            return DEFAULT_LANGS;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
