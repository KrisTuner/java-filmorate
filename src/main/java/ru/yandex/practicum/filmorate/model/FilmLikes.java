package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmLikes {
    private Integer filmId;
    private Set<Integer> userLikes = new HashSet<>();

    public FilmLikes(Integer filmId) {
        this.filmId = filmId;
    }

    public void addLike(Integer userId) {
        userLikes.add(userId);
    }

    public void removeLike(Integer userId) {
        userLikes.remove(userId);
    }

    public boolean hasLike(Integer userId) {
        return userLikes.contains(userId);
    }

    public int getLikesCount() {
        return userLikes.size();
    }
}