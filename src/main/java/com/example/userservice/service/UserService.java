package com.example.userservice.service;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto create(CreateUserRequest request) {
        User user = new User(request.name(), request.email(), request.age());
        User saved = userRepository.save(user);
        entityManager.refresh(saved);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public UserDto getById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Не найден пользователь с id: " + userId));
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public UserDto update(long userId, UpdateUserRequest request) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Не найден пользователь с id: " + userId));

        existing.setName(request.name());
        existing.setEmail(request.email());
        existing.setAge(request.age());

        User saved = userRepository.save(existing);
        return toDto(saved);
    }

    @Transactional
    public void delete(long userId) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Не найден пользователь с id: " + userId));
        userRepository.delete(existing);
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}
