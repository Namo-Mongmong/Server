package com.example.namo2.domain.moim.application.converter;

import com.example.namo2.domain.moim.domain.Moim;
import com.example.namo2.domain.moim.domain.MoimAndUser;
import com.example.namo2.domain.user.domain.User;

import java.util.List;
import java.util.stream.Collectors;

public class MoimAndUserConverter {
    private MoimAndUserConverter() {
        throw new IllegalArgumentException("Util Class");
    }

    public static MoimAndUser toMoimAndUser(String moimCustomName, Integer color, User user, Moim moim) {
        return MoimAndUser.builder()
                .moimCustomName(moimCustomName)
                .color(color)
                .user(user)
                .moim(moim)
                .build();
    }

    public static List<User> toUsers(List<MoimAndUser> moimAndUsers) {
        return moimAndUsers.stream()
                .map(MoimAndUser::getUser)
                .collect(Collectors.toList());
    }
}