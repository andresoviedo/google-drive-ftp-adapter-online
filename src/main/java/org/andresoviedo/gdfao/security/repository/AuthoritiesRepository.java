package org.andresoviedo.gdfao.security.repository;

import org.andresoviedo.gdfao.security.model.Authority;
import org.springframework.data.repository.Repository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@org.springframework.stereotype.Repository
public interface AuthoritiesRepository extends Repository<Authority, String> {

    String ROLE_USER = "ROLE_USER";
    String ROLE_ADMIN = "ROLE_ADMIN";

    Authority findById(String id) throws UsernameNotFoundException;

    Authority save(Authority user);
}

