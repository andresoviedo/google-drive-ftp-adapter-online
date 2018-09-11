package org.andresoviedo.gdfao.security.repository;

import org.andresoviedo.gdfao.security.model.UserDetails;
import org.springframework.data.repository.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface UserDetailsRepository extends Repository<UserDetails, String> {

    List<UserDetails> findByEmail(String email);

    UserDetails findByUsername(String username);

    UserDetails save(UserDetails userDetails);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    List<UserDetails> findAll();
}
