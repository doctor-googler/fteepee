package model;

import javafx.concurrent.Task;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FTPManager {
    private FTPClient client;
    private boolean connected;

    public FTPManager() {
        client = new FTPClient();
        connected = false;
    }

    /**
     * Establish connection to the secured host
     *
     * @param host
     * @param userName
     * @param password
     * @return
     */
    public FTPManagerResponse<Boolean> connect(String host, String userName, String password) {
        FTPManagerResponse<Boolean> response = new FTPManagerResponse<>();
        try {
            String[] hostParameters = host.split(":");
            if (hostParameters.length > 1) {
                client.connect(hostParameters[0], Integer.parseInt(hostParameters[1]));
            } else {
                client.connect(host);
            }
            client.enterLocalPassiveMode();
            connected = client.login(userName, password);
            response.setContent(connected);
        } catch (IOException e) {
            response.addError(e);
        }
        return response;
    }

    /**
     * Disconnect from the host
     *
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
     *
     * @return
     */
    public FTPManagerResponse<List<FTPFile>> fileList() {
        FTPManagerResponse<List<FTPFile>> response = new FTPManagerResponse<>();
        if (!isConnected(response)) {
            return response;
        }
        List<FTPFile> files = new ArrayList<>();
        try {
            //TODO: change listDirs to listNames
            for (FTPFile file : client.listFiles()) {
                files.add(file);
            }
            response.setContent(files);
        } catch (IOException e) {
            response.addError(e);
        }
        return response;
    }

    /**
     * Change current directory to other
     *
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
     *
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


    //TODO unbind from javafx concurrent

    /**
     * Download file with progress representation
     */
    public FTPManagerResponse<Task<Boolean>> downloadWithProgresss(String remotePath, String localPath) {
        FTPManagerResponse<Task<Boolean>> response = new FTPManagerResponse<>();
        if (!isConnected(response)) {
            return response;
        }
        FTPFile ftpFile = fileInfo(remotePath).getContent();
        if (ftpFile == null) {
            response.addError(new Exception("File doesn't exist"));
        }
        if (ftpFile.isDirectory()) {
            response.addError(new Exception("Unable to download directory"));
        }
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                boolean result = false;
                File localFile = new File(localPath);
                long size = ftpFile.getSize();
                byte[] buf = new byte[1024];
                long done = 0;
                int byteCount = 0;
                client.setFileType(FTP.BINARY_FILE_TYPE);
                try (InputStream is = client.retrieveFileStream(remotePath);
                     FileOutputStream fos = new FileOutputStream(localFile);) {
                    while ((byteCount = is.read(buf)) > 0) {
                        done += byteCount;
                        fos.write(buf, 0, byteCount);
                        updateProgress(done, size);
                    }
                    fos.flush();
                    ;
                } catch (IOException e) {
                    System.err.println(e);
                } finally {
                    result = client.completePendingCommand();
                    System.out.println("Download operation result: " + result);
                }
                return result;
            }
        };
        response.setContent(task);
        return response;
    }

    //TODO unbind from javafx concurrent

    /**
     * Upload file with progress representation
     */
    public FTPManagerResponse<Task<Boolean>> uploadWithProgress(String remotePath, String localPath) {
        FTPManagerResponse<Task<Boolean>> response = new FTPManagerResponse<>();
        if (!isConnected(response)) {
            return response;
        }
        File localFile = new File(localPath);
        if (!localFile.exists()) {
            response.addError(new Exception("File not exists"));
            return response;
        }
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                boolean result = false;
                client.setFileType(FTP.BINARY_FILE_TYPE);
                try (FileInputStream fis = new FileInputStream(localFile);
                     OutputStream os = client.appendFileStream(remotePath)) {

                    long fileSize = localFile.length();
                    long sended = 0L;
                    int bytesRead = 0;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = fis.read(buffer)) > 0) {
                        os.write(buffer, 0, bytesRead);
                        sended += bytesRead;
                        updateProgress(sended, fileSize);
                    }
                    os.flush();
                } catch (IOException e) {
                    System.err.println(e);
                } finally {
                    result = client.completePendingCommand();
                    System.out.println("Upload operation result: " + result);
                }

                return result;
            }
        };
        response.setContent(task);

        return response;
    }

    /**
     * Delete remote file
     *
     * @param path
     * @return
     */
    public FTPManagerResponse<Boolean> delete(String path) {
        FTPManagerResponse<Boolean> response = new FTPManagerResponse<>();
        if (!isConnected(response)) {
            return response;
        }
        try {
            response.setContent(client.deleteFile(path));
        } catch (IOException e) {
            response.addError(e);
        }
        return response;
    }

    /**
     * Rename remote file
     *
     * @param oldPath
     * @param newPath
     * @return
     */
    public FTPManagerResponse<Boolean> rename(String oldPath, String newPath) {
        FTPManagerResponse<Boolean> response = new FTPManagerResponse<>();
        if (!isConnected(response)) {
            return response;
        }
        try {
            response.setContent(client.rename(oldPath, newPath));
        } catch (IOException e) {
            response.addError(e);
        }
        return response;
    }


    //TODO unbind from transport-level file representation (FTPFile class)

    /**
     * Retrieves file info from the server
     *
     * @param filePath
     * @return
     */
    public FTPManagerResponse<FTPFile> fileInfo(String filePath) {
        FTPManagerResponse<FTPFile> response = new FTPManagerResponse<>();
        if (!isConnected(response)) {
            return response;
        }
        try {
            FTPFile ftpFile = client.mlistFile(filePath);
            response.setContent(ftpFile);
        } catch (IOException e) {
            response.addError(e);
        }
        return response;
    }

    public boolean isConnected() {
        return connected;
    }

    private <T> boolean isConnected(FTPManagerResponse<T> response) {
        if (!connected) {
            response.addError(new Exception("Not connected."));
        }
        return connected;
    }
}
