package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class Friendship {
    private Integer userId;
    private Map<Integer, FriendshipStatus> friends = new HashMap<>(); // friendId -> status

    public Friendship(Integer userId) {
        this.userId = userId;
    }

    public void addFriend(Integer friendId, FriendshipStatus status) {
        friends.put(friendId, status);
    }

    public void removeFriend(Integer friendId) {
        friends.remove(friendId);
    }

    public boolean hasFriend(Integer friendId) {
        return friends.containsKey(friendId);
    }

    public FriendshipStatus getFriendshipStatus(Integer friendId) {
        return friends.get(friendId);
    }

    public void updateFriendshipStatus(Integer friendId, FriendshipStatus status) {
        if (friends.containsKey(friendId)) {
            friends.put(friendId, status);
        }
    }
}