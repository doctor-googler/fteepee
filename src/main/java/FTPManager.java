import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by doctor-googler on 11/1/2016.
 */
public class FTPManager {
    private FTPClient client;
    private boolean connected;

    public FTPManager() {
        client = new FTPClient();
        connected = false;
    }

    /**
     * Establish connection to the secured host
     * @param host
     * @param userName
     * @param password
     * @return
     */
    public FTPManagerResponse<Boolean> connect(String host, String userName, String password) {
        FTPManagerResponse<Boolean> response = new FTPManagerResponse<>();
        try {
            client.connect(host);
            connected = client.login(userName, password);
            response.setContent(connected);
        } catch (IOException e) {
            response.addError(e);
        }
        return response;
    }

    /**
     * Disconnect from the host
     * @return
     */
    public FTPManagerResponse<Boolean> disconnect() {
        FTPManagerResponse<Boolean> response = new FTPManagerResponse<>();
        if (!isConnected(response)) {
            return response;
        }
        try {
            client.logout();
            client.disconnect();
            connected = false;
            response.setContent(!connected);
        } catch (IOException e) {
            response.addError(e);
        }
        return response;
    }

    /**
     * Retrieve a file list of current directory
     * @return
     */
    public FTPManagerResponse<List<String>> fileList() {
        FTPManagerResponse<List<String>> response = new FTPManagerResponse<>();
        if (!isConnected(response)) {
            return response;
        }
        List<String> files = new ArrayList<>();
        try {
            //TODO: change listDirs to listNames
            for(FTPFile file: client.listFiles()) {
                files.add(file.getName());
            }
            response.setContent(files);
        } catch (IOException e) {
            response.addError(e);
        }
        return response;
    }

    /**
     * Change current directory to other
     * @param directory
     * @return
     */
    public FTPManagerResponse<Boolean> changeDirectory(String directory) {
        FTPManagerResponse<Boolean> response = new FTPManagerResponse<>();
        if (!isConnected(response)) {
            return response;
        }
        try {
            client.changeWorkingDirectory(directory);
            response.setContent(true);
        } catch (IOException e) {
            response.addError(e);
        }
        return response;
    }

    /**
     * Download file
     * @param remotePath
     * @param localPath
     * @return
     */
    public FTPManagerResponse<Boolean> download(String remotePath, String localPath) {
        FTPManagerResponse<Boolean> response = new FTPManagerResponse<>();
        if (!isConnected(response)) {
            return response;
        }
        File file = new File(localPath);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            client.retrieveFile(remotePath, fos);
            response.setContent(true);
        } catch (IOException e) {
            response.addError(e);
        }
        return response;
    }

    private <T> boolean isConnected(FTPManagerResponse<T> response) {
        if (!connected) {
            response.addError(new Exception("Not connected."));
        }
        return connected;
    }
}
