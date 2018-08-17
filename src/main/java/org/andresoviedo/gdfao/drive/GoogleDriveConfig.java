package org.andresoviedo.gdfao.drive;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Configuration
public class GoogleDriveConfig {

    private static Logger logger = Logger.getLogger(GoogleDriveConfig.class.getName());

    /**
     * Authorization code flow to be used across all HTTP servlet requests.
     */
    @Bean
    public AuthorizationCodeFlow authorizationCodeFlow() throws IOException {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
                new InputStreamReader(this.getClass().getResourceAsStream("/client_secrets.json")));

        // set up authorization code flow
        Set<String> scopes = new HashSet<String>();
        scopes.add(DriveScopes.DRIVE);
        scopes.add(DriveScopes.DRIVE_METADATA);

        AuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance(), clientSecrets,
                scopes).setDataStoreFactory(
                new FileDataStoreFactory(new File("data"))).setAccessType("offline").build();
        return flow;
    }
}
