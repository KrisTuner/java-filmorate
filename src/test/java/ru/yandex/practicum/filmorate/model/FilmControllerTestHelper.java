package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;

public class FilmControllerTestHelper {
    public static void validateFilmForTest(Film film) throws ValidationException {
        FilmController controller = new FilmController();
        controller.create(film);
    }
}