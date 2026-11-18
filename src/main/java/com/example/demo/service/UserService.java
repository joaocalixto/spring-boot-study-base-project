package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.UserV2;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@NullMarked
public class UserService {

    private final Map<Long, User> store = Map.of(
            1L, new User(1L, "Maria Lima", "maria@example.com", null),
            2L, new User(2L, "Joao Calixto", "joao@example.com", "+55 11 99999-0000")
    );

    public @Nullable User findById(Long id) {
        return store.get(id);
    }

    public UserV2 toV2(User user) {
        String[] parts = user.name().split(" ", 2);
        String first = parts[0];
        String last = parts.length > 1 ? parts[1] : "";
        return new UserV2(
                user.id(),
                first,
                last,
                user.email(),
                user.phone(),
                "https://i.pravatar.cc/150?u=" + user.id()
        );
    }
}
