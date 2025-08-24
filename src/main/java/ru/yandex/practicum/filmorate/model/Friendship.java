package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
public class Friendship {
    private Integer userId;
    private Set<Integer> friendIds = new HashSet<>();

    public Friendship(Integer userId) {
        this.userId = userId;
    }

    public void addFriend(Integer friendId) {
        friendIds.add(friendId);
    }

    public void removeFriend(Integer friendId) {
        friendIds.remove(friendId);
    }

    public boolean hasFriend(Integer friendId) {
        return friendIds.contains(friendId);
    }
}