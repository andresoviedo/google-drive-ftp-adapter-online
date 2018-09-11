package org.andresoviedo.gdfao.drive;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import org.andresoviedo.gdfao.security.model.UserDetails;
import org.andresoviedo.gdfao.security.repository.UserDetailsRepository;
import org.andresoviedo.gdfao.user.model.FtpUser;
import org.andresoviedo.gdfao.user.repository.FtpUsersRepository;
import org.andresoviedo.google_drive_ftp_adapter.controller.Controller;
import org.andresoviedo.google_drive_ftp_adapter.model.Cache;
import org.andresoviedo.google_drive_ftp_adapter.model.GoogleDrive;
import org.andresoviedo.google_drive_ftp_adapter.model.GoogleDriveFactory;
import org.andresoviedo.google_drive_ftp_adapter.model.SQLCache;
import org.andresoviedo.google_drive_ftp_adapter.service.FtpGdriveSynchService;
import org.andresoviedo.google_drive_ftp_adapter.view.ftp.Authorities;
import org.andresoviedo.google_drive_ftp_adapter.view.ftp.FtpCommands;
import org.andresoviedo.google_drive_ftp_adapter.view.ftp.FtpFileSystemView;
import org.andresoviedo.google_drive_ftp_adapter.view.ftp.FtpletController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.command.CommandFactoryFactory;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class GoogleDriveFtpAdapter extends FtpServerFactory implements FileSystemFactory, UserManager, CommandLineRunner {

    private static final Log LOG = LogFactory.getLog(GoogleDriveFtpAdapter.class);

    private static final String DEFAULT_ILLEGAL_CHARS_REGEX = "\\/|[\\x00-\\x1F\\x7F]|\\`|\\?|\\*|\\\\|\\<|\\>|\\||\\\"|\\:";

    @Autowired
    private AuthorizationCodeFlow flow;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private FtpUsersRepository ftpUsersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Pattern illegalChars;

    private FtpServer server;

    private final File dataDir;

    public GoogleDriveFtpAdapter() {
        super();

        // initialize the database directory
        final String pathname = "data/cache";
        dataDir = new File(pathname);
        if (!dataDir.exists()) {
            LOG.info("Creating cache '" + dataDir + "'...");
            if (!dataDir.mkdirs()) {
                throw new RuntimeException("Could not create database folder " + dataDir.getAbsolutePath());
            }
        }
    }

    @Override
    public void run(String[] args) {

        LOG.info("Starting FTP Server...");
        LOG.info("FTP cache folder: "+dataDir);

        this.server = this.createServer();
        this.illegalChars = Pattern.compile(DEFAULT_ILLEGAL_CHARS_REGEX);
        LOG.info("Configured illegalchars '" + illegalChars + "'");

        setFileSystem(this);
        setUserManager(this);

        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setMaxThreads(100);
        connectionConfigFactory.setAnonymousLoginEnabled(false);
        setConnectionConfig(connectionConfigFactory.createConnectionConfig());

        // MFMT for directories (default mina command doesn't support it)
        CommandFactoryFactory ccf = new CommandFactoryFactory();
        ccf.addCommand("MFMT", new FtpCommands.MFMT());
        setCommandFactory(ccf.createCommandFactory());

        // TODO: set ftplet to control all commands
        Map<String, Ftplet> ftplets = new HashMap<String,Ftplet>();
        ftplets.put("default", new FtpletController());
        setFtplets(ftplets);

        // set the port of the listener
        ListenerFactory listenerFactory = new ListenerFactory();
        LOG.info("Starting ftp server at port "+System.getProperty("ftp.port","50000"));
        listenerFactory.setPort(Integer.valueOf(System.getProperty("ftp.port","50000")));
        //listenerFactory.setServerAddress("127.0.0.1");
        listenerFactory.setIdleTimeout(300);
        DataConnectionConfigurationFactory dataConnFactory = new DataConnectionConfigurationFactory();
        dataConnFactory.setPassivePorts("50001-51000");

        if (Arrays.asList(args).contains("--ftps")) {
            LOG.info("Setting up SSL configuration...");
            SslConfigurationFactory sslConfigurationFactory = new SslConfigurationFactory();
            sslConfigurationFactory.setKeystoreFile(new File("./keystore.p12"));
            sslConfigurationFactory.setKeyAlias("ftpdrive");
            sslConfigurationFactory.setKeystoreType("PKCS12");
            sslConfigurationFactory.setKeystorePassword(System.getProperty("PASSWORD"));
            SslConfiguration sslConfiguration = sslConfigurationFactory.createSslConfiguration();

            //listenerFactory.setImplicitSsl(true);
            listenerFactory.setSslConfiguration(sslConfiguration);

            //dataConnFactory.setImplicitSsl(true);
            dataConnFactory.setSslConfiguration(sslConfiguration);
        }

        listenerFactory.setDataConnectionConfiguration(dataConnFactory.createDataConnectionConfiguration());

        // replace the default listener
        addListener("default", listenerFactory.createListener());

        start();
    }

    private void start() {
        try {
            server.start();
            LOG.info("FTP Server started!");
        } catch (FtpException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void stop() {
        server.stop();
        LOG.info("FTP Server stopped.");
    }

    @Override
    public FileSystemView createFileSystemView(User user) throws FtpException {
        try {
            final File dataFile =  new File(dataDir,user.getName()+".db");
            final String jdbcUrl = "jdbc:h2:"+dataFile.getAbsolutePath()+";USER="+user.getName()+";PASSWORD="+user.getPassword();
            final Cache cache = new SQLCache("org.h2.Driver", jdbcUrl);

            final FtpUser ftpUser = ftpUsersRepository.findByFtpusername(user.getName());
            final Credential credential = flow.loadCredential(ftpUser.getId());
            if (credential == null){
                throw new FtpException("couldn't load credential");
            }

            final GoogleDrive googleDrive = new GoogleDrive(GoogleDriveFactory.build(credential));

            final FtpGdriveSynchService cacheUpdater = new FtpGdriveSynchService(cache, googleDrive);
            cacheUpdater.start();

            final Controller controller = new Controller(cache, googleDrive, cacheUpdater);

            return new FtpFileSystemView(controller, cache, illegalChars, user, cacheUpdater);
        } catch (FtpException e) {
            throw e;
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserByName(String username) throws FtpException {
        try {
            return getFtpUser(username);
        } catch (Exception e) {
            throw new FtpException(e);
        }
    }

    private User getFtpUser(String username) throws Exception {
        FtpUser ftpUser = ftpUsersRepository.findByFtpusername(username);
        if (ftpUser == null){
            return null;
        }
        final List<UserDetails> userDetails = userDetailsRepository.findByEmail(ftpUser.getId());
        if  (userDetails == null || userDetails.isEmpty() || !userDetails.get(0).isTerms()){
            throw new Exception("User "+username+" has not accepted terms & conditions");
        }
        // maybe this is redundant, but just to be sure
        if (ftpUser.getFtpusername() == null || ftpUser.getFtpusername().length() == 0
                || ftpUser.getFtppassword() == null || ftpUser.getFtppassword().length() == 0){
            throw new Exception("User "+username+" doesn't have right credentials configured in database");
        }
        BaseUser user = new BaseUser();
        user.setEnabled(true);
        user.setHomeDirectory("");
        user.setMaxIdleTime(300);
        user.setName(username);
        user.setPassword(ftpUser.getFtppassword());
        List<Authority> authorities = new ArrayList<>();
        final String rights = "pwd|cd|dir|put|get|rename|delete|mkdir|rmdir|append";
        if (rights.contains("pwd")){
            authorities.add(new Authorities.PWDPermission());
        }
        if (rights.contains("cd")){
            authorities.add(new Authorities.CWDPermission());
        }
        if (rights.contains("dir")){
            authorities.add(new Authorities.ListPermission());
        }
        if (rights.contains("put")){
            authorities.add(new Authorities.StorePermission());
        }
        if (rights.contains("get")){
            authorities.add(new Authorities.RetrievePermission());
        }
        if (rights.contains("rename")){
            authorities.add(new Authorities.RenameToPermission());
        }
        if (rights.contains("delete")){
            authorities.add(new Authorities.DeletePermission());
        }
        if (rights.contains("rmdir")){
            authorities.add(new Authorities.RemoveDirPermission());
        }
        if (rights.contains("mkdir")){
            authorities.add(new Authorities.MakeDirPermission());
        }
        if (rights.contains("append")){
            authorities.add(new Authorities.AppendPermission());
        }

        authorities.add(new WritePermission());
        authorities.add(new ConcurrentLoginPermission(5, 5));
        user.setAuthorities(authorities);
        LOG.info("FTP User Manager configured for user '" + user.getName() + "'");
        LOG.info("FTP rights '" + rights + "'");
        return user;
    }

    @Override
    public String[] getAllUserNames() {
        return new String[0];
    }

    @Override
    public void delete(String s) throws FtpException {
        throw new FtpException("Unsupported operation");
    }

    @Override
    public void save(User user) throws FtpException {
        throw new FtpException("Unsupported operation");
    }

    @Override
    public boolean doesExist(String username) throws FtpException {
        try {
            return getFtpUser(username) != null;
        } catch (Exception e) {
            LOG.error("error getting ftp user ", e);
            return false;
        }
    }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        if (!UsernamePasswordAuthentication.class.isAssignableFrom(authentication.getClass())) {
            throw new AuthenticationFailedException("user is not authenticated");
        }
        UsernamePasswordAuthentication upAuth = (UsernamePasswordAuthentication) authentication;
        if (upAuth.getUsername() == null || upAuth.getUsername().length() == 0) {
            throw new AuthenticationFailedException("password can't be null");
        }
        if (upAuth.getPassword() == null || upAuth.getPassword().length() == 0) {
            throw new AuthenticationFailedException("password can't be null");
        }
        final User user;
        try {
            user = getFtpUser(upAuth.getUsername());
        } catch (Exception e) {
            LOG.error("error getting ftp user from database", e);
            throw new AuthenticationFailedException("error getting ftp user from database",e);
        }
        if (user == null) {
            throw new AuthenticationFailedException("user doesn't exists");
        }
        if (!passwordEncoder.matches(upAuth.getPassword(), user.getPassword())) {
            throw new AuthenticationFailedException("wrong credentials");
        }
        ((BaseUser) user).setPassword(((UsernamePasswordAuthentication) authentication).getPassword());
        return user;
    }

    @Override
    public String getAdminName() {
        return null;
    }

    @Override
    public boolean isAdmin(String s) {
        return false;
    }
}
