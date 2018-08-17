package org.andresoviedo.gdfao.user.repository;

import org.andresoviedo.gdfao.drive.model.DriveAuthorization;
import org.andresoviedo.gdfao.user.model.FtpUser;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface FtpUsersRepository extends Repository<FtpUser, String> {

    FtpUser save(FtpUser authorization);

    boolean existsById(String id);

    void delete(FtpUser authorization);

    FtpUser findById(String id);

    FtpUser findByFtpusername(String ftpusername);

    void deleteById(String id);

    boolean existsByFtpusername(String id);
}

