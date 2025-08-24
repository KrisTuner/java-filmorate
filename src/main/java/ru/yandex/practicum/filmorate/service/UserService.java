package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friendship;
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

        userFriendship.addFriend(friendId);
        friendFriendship.addFriend(userId);

        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
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
                .getFriendIds().stream()
                .map(id -> userStorage.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        validateUserExists(userId);
        validateUserExists(otherUserId);

        Set<Integer> userFriends = friendships.getOrDefault(userId, new Friendship(userId)).getFriendIds();
        Set<Integer> otherUserFriends = friendships.getOrDefault(otherUserId, new Friendship(otherUserId)).getFriendIds();

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(id -> userStorage.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void validateUserExists(Integer userId) {
        if (!userStorage.existsById(userId)) {
            throw new NoSuchElementException("Пользователь с id " + userId + " не найден");
        }
    }
}