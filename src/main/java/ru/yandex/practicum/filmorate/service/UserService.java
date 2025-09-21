package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final Map<Integer, Friendship> friendships = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        Friendship userFriendship = friendships.computeIfAbsent(userId, Friendship::new);
        Friendship friendFriendship = friendships.computeIfAbsent(friendId, Friendship::new);

        userFriendship.addFriend(friendId, FriendshipStatus.PENDING);
        friendFriendship.addFriend(userId, FriendshipStatus.PENDING);

        log.info("Пользователь {} отправил запрос на дружбу пользователю {}", userId, friendId);
    }

    public void confirmFriend(Integer userId, Integer friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        if (friendships.containsKey(userId) && friendships.containsKey(friendId)) {
            friendships.get(userId).updateFriendshipStatus(friendId, FriendshipStatus.CONFIRMED);
            friendships.get(friendId).updateFriendshipStatus(userId, FriendshipStatus.CONFIRMED);

            log.info("Пользователь {} подтвердил дружбу с пользователем {}", userId, friendId);
        }
    }

    public void rejectFriend(Integer userId, Integer friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        if (friendships.containsKey(userId)) {
            friendships.get(userId).removeFriend(friendId);
        }
        if (friendships.containsKey(friendId)) {
            friendships.get(friendId).removeFriend(userId);
        }

        log.info("Пользователь {} отклонил запрос на дружбу от пользователя {}", userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        if (friendships.containsKey(userId)) {
            friendships.get(userId).removeFriend(friendId);
        }
        if (friendships.containsKey(friendId)) {
            friendships.get(friendId).removeFriend(userId);
        }

        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    public List<User> getFriends(Integer userId) {
        validateUserExists(userId);

        return friendships.getOrDefault(userId, new Friendship(userId))
                .getFriends().keySet().stream()
                .map(id -> userStorage.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<User> getConfirmedFriends(Integer userId) {
        validateUserExists(userId);

        return friendships.getOrDefault(userId, new Friendship(userId))
                .getFriends().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(entry -> userStorage.findById(entry.getKey()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<User> getPendingFriendRequests(Integer userId) {
        validateUserExists(userId);

        return friendships.getOrDefault(userId, new Friendship(userId))
                .getFriends().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.PENDING)
                .map(entry -> userStorage.findById(entry.getKey()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        validateUserExists(userId);
        validateUserExists(otherUserId);

        Set<Integer> userFriends = friendships.getOrDefault(userId, new Friendship(userId))
                .getFriends().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<Integer> otherUserFriends = friendships.getOrDefault(otherUserId, new Friendship(otherUserId))
                .getFriends().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(id -> userStorage.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public FriendshipStatus getFriendshipStatus(Integer userId, Integer friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        if (friendships.containsKey(userId) && friendships.get(userId).hasFriend(friendId)) {
            return friendships.get(userId).getFriendshipStatus(friendId);
        }
        return null;
    }

    private void validateUserExists(Integer userId) {
        if (!userStorage.existsById(userId)) {
            throw new NoSuchElementException("Пользователь с id " + userId + " не найден");
        }
    }
}