package com.movie.file.utils;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author soul
 * @创建日期 ：Created in 2020/3/1 00:27
 * @描述：文件上传
 */
@Component
public class Utils {

    @Value("${file.fileServerPath}")
    public String fileServerPath;

    public String getFileUrl(String filePath) {
        return fileServerPath + "/" + filePath;
    }

    public String upLoadFile(byte[] fileBytes, String fileName) {

        String fileId = null;
        TrackerClient trackerClient = new TrackerClient();
        StorageClient1 client = null;
        try {
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            client = new StorageClient1(trackerServer, storeStorage);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
        if (client != null) {
            String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
            try {
                fileId = client.upload_file1(fileBytes, fileExtName, getMateValuePairs(fileBytes, fileName, fileExtName));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MyException e) {
                e.printStackTrace();
            }
        }
        return fileId;
    }

    private NameValuePair[] getMateValuePairs(byte[] fileBytes, String fileName, String fileExtName) {
        NameValuePair[] nameValuePairs = new NameValuePair[3];
        nameValuePairs[0] = new NameValuePair("fileName", fileName);
        nameValuePairs[1] = new NameValuePair("fileExtName", fileExtName);
        nameValuePairs[2] = new NameValuePair("fileLength", String.valueOf(fileBytes.length));
        return nameValuePairs;
    }
}
