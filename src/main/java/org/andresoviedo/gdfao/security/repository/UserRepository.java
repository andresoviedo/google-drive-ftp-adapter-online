package org.andresoviedo.gdfao.security.repository;

import org.andresoviedo.gdfao.security.model.User;
import org.springframework.data.repository.Repository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@org.springframework.stereotype.Repository
public interface UserRepository extends Repository<User, Long> {

    User save(User user);

    boolean existsByUsername(String username);

    User findByUsername(String username) throws UsernameNotFoundException;

    void deleteByUsername(String username);
}

