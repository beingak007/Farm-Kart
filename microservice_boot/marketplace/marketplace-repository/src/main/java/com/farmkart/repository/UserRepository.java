package com.farmkart.repository;

import com.farmkart.client.enums.AuthProvider;
import com.farmkart.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByMobile(String mobile);

    Optional<User> findByAuthProviderAndProviderUserId(AuthProvider authProvider, String providerUserId);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);
}
