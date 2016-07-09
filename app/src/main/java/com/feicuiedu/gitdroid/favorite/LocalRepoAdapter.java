package com.feicuiedu.gitdroid.favorite;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.feicuiedu.gitdroid.R;
import com.feicuiedu.gitdroid.favorite.model.LocalRepo;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/7 0007.
 */
public class LocalRepoAdapter extends BaseAdapter{

    private final ArrayList<LocalRepo> datas;

    public LocalRepoAdapter() {
        datas = new ArrayList<LocalRepo>();
    }

    public void setData(@Nullable Collection<LocalRepo> repos){
        datas.clear();
        datas.addAll(repos);
        notifyDataSetChanged();
    }

    @Override public int getCount() {
        return datas.size();
    }

    @Override public LocalRepo getItem(int position) {
        return datas.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.layout_item_repo,parent,false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        LocalRepo repo = getItem(position);
        viewHolder.tvRepoName.setText(repo.getFullName());
        viewHolder.tvRepoInfo.setText(repo.getDescription());
        viewHolder.tvRepoStars.setText(String.format("stars : %d", repo.getStarCount()));
        ImageLoader.getInstance().displayImage(repo.getAvatar(), viewHolder.ivIcon);
        return convertView;
    }

    static class ViewHolder{

        @Bind(R.id.ivIcon) ImageView ivIcon;
        @Bind(R.id.tvRepoName) TextView tvRepoName;
        @Bind(R.id.tvRepoInfo) TextView tvRepoInfo;
        @Bind(R.id.tvRepoStars) TextView tvRepoStars;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
