import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raman_Hutkovich on 11/1/2016.
 */
public class FTPManager {
    private FTPClient client;
    private boolean isConnected;

    public FTPManager() {
        client = new FTPClient();
        isConnected = false;
    }

    public FTPManagerResponse<Boolean> connect(String host, String userName, String password) {
        FTPManagerResponse<Boolean> response = new FTPManagerResponse<>();
        try {
            if (isConnected) {
                response.addError(new Exception("Manager already connected"));
            }
            client.connect(host);
            isConnected = client.login(userName, password);
            if (!isConnected) {
                client.disconnect();
                response.setContent(isConnected);
            }
        } catch (IOException e) {
            response.addError(e);
        }
        return response;
    }

    public FTPManagerResponse<Boolean> disconnect() {
        FTPManagerResponse<Boolean> response = new FTPManagerResponse<>();
        try {
            client.logout();
            client.disconnect();
        } catch (IOException e) {
            response.addError(e);
        }
        response.setContent(true);
        return response;
    }

    public FTPManagerResponse<List<String>> fileList() {
        FTPManagerResponse<List<String>> response = new FTPManagerResponse<>();
        List<String> files = new ArrayList<>();
        try {
            for(FTPFile file: client.listDirectories()) {
                files.add(file.getName());
            }
            response.setContent(files);
        } catch (IOException e) {
            response.addError(e);
        }
        return response;
    }

    private <T> FTPManagerResponse<T> checkConnection(FTPManagerResponse<T> response) {
        if (!isConnected) {
            response.addError(new Exception("Not connected."));
        }
        return response;
    }
}
