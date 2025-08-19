package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.controller.UserController;

public class UserControllerTestHelper {
    public static User createUserForTest(User user) {
        UserController controller = new UserController();
        return controller.create(user);
    }
}