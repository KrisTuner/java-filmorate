package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на список всех пользователей. Текущее количество: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user.setId(idCounter++);
        setUserNameFromLoginIfEmpty(user);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Попытка обновления несуществующего пользователя с ID: {}", user.getId());
            throw new NoSuchElementException("Пользователь с id " + user.getId() + " не найден");
        }
        setUserNameFromLoginIfEmpty(user);
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    private void setUserNameFromLoginIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Для пользователя {} установлено имя из логина", user.getLogin());
        }
    }
}