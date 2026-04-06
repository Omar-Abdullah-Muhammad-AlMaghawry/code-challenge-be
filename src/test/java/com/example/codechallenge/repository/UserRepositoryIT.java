package com.example.codechallenge.repository;

import com.example.codechallenge.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    private User saveUser(String username) {
        return userRepository.save(User.builder()
                .username(username)
                .password("encodedPass")
                .build());
    }

    // --- findByUsername ---

    @Test
    void findByUsername_existingUser_returnsUser() {
        saveUser("alice");

        Optional<User> result = userRepository.findByUsername("alice");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("alice");
    }

    @Test
    void findByUsername_unknownUser_returnsEmpty() {
        Optional<User> result = userRepository.findByUsername("ghost");

        assertThat(result).isEmpty();
    }

    // --- existsByUsername ---

    @Test
    void existsByUsername_existingUser_returnsTrue() {
        saveUser("bob");

        assertThat(userRepository.existsByUsername("bob")).isTrue();
    }

    @Test
    void existsByUsername_unknownUser_returnsFalse() {
        assertThat(userRepository.existsByUsername("nobody")).isFalse();
    }

    // --- save ---

    @Test
    void save_assignsId() {
        User saved = saveUser("carol");

        assertThat(saved.getId()).isNotNull();
    }
}
