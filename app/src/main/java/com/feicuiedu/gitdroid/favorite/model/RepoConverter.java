package com.feicuiedu.gitdroid.favorite.model;


import android.support.annotation.NonNull;

import com.feicuiedu.gitdroid.github.hotrepo.pager.modle.Repo;

import java.util.ArrayList;
import java.util.List;

public class RepoConverter {

    private RepoConverter(){}

    public static @NonNull LocalRepo convert(@NonNull Repo repo) {
        LocalRepo localRepo = new LocalRepo();
        localRepo.setAvatar(repo.getOwner().getAvatar());
        localRepo.setDescription(repo.getDescription());
        localRepo.setFullName(repo.getFullName());
        localRepo.setId(repo.getId());
        localRepo.setName(repo.getName());
        localRepo.setStarCount(repo.getStargazersCount());
        localRepo.setForkCount(repo.getForksCount());
        return localRepo;
    }

    public static @NonNull List<LocalRepo> convertAll(@NonNull List<Repo> repos) {
        ArrayList<LocalRepo> localRepos = new ArrayList<>();
        for (Repo repo : repos) {
            localRepos.add(convert(repo));
        }
        return localRepos;
    }
}
