package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLikes;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Map<Integer, FilmLikes> filmLikes = new HashMap<>();

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        validateFilmExists(filmId);
        validateUserExists(userId);

        FilmLikes likes = filmLikes.computeIfAbsent(filmId, FilmLikes::new);
        if (likes.hasLike(userId)) {
            log.warn("Пользователь {} уже поставил лайк фильму {}", userId, filmId);
            return;
        }

        likes.addLike(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        validateFilmExists(filmId);
        validateUserExists(userId);

        if (filmLikes.containsKey(filmId)) {
            filmLikes.get(filmId).removeLike(userId);
            log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmLikes.values().stream()
                .sorted((l1, l2) -> Integer.compare(l2.getLikesCount(), l1.getLikesCount()))
                .limit(count)
                .map(like -> filmStorage.findById(like.getFilmId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public int getLikesCount(Integer filmId) {
        validateFilmExists(filmId);
        return filmLikes.getOrDefault(filmId, new FilmLikes(filmId)).getLikesCount();
    }

    private void validateFilmExists(Integer filmId) {
        if (!filmStorage.existsById(filmId)) {
            throw new NoSuchElementException("Фильм с id " + filmId + " не найден");
        }
    }

    private void validateUserExists(Integer userId) {
        if (!userStorage.existsById(userId)) {
            throw new NoSuchElementException("Пользователь с id " + userId + " не найден");
        }
    }
}