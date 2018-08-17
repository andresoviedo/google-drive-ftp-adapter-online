package org.andresoviedo.gdfao.drive.repository;

import org.andresoviedo.gdfao.drive.model.DriveAuthorization;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface DriveAuthorizationRepository extends Repository<DriveAuthorization, String> {

    DriveAuthorization save(DriveAuthorization authorization);

    boolean existsById(String id);

    void delete(DriveAuthorization authorization);

    DriveAuthorization findById(String id);

    void deleteById(String id);
}

