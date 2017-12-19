package com.mark.framework.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.meta.InsertOnly;
import com.qcloud.cos.request.UploadFileRequest;
import com.qcloud.cos.sign.Credentials;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class CosUploader {

    @Value("${mark-framework.cosConfig.root}")
    private String root;
    @Value("${mark-framework.cosConfig.bucket}")
    private String bucket;
    @Value("${mark-framework.cosConfig.appid}")
    private Integer appid;
    @Value("${mark-framework.cosConfig.secretid}")
    private String secretid;
    @Value("${mark-framework.cosConfig.secretkey}")
    private String secretkey;
    @Value("${mark-framework.cosConfig.region}")
    private String region;

    private volatile static COSClient client = null;
    protected synchronized void init() {
        if (client == null) {
            Credentials cred = new Credentials(appid, secretid, secretkey);
            ClientConfig config = new ClientConfig();
            config.setRegion(region);
            client = new COSClient(config, cred);
        }
    }

    public JSONObject upload(String fileCosPath, byte[] bytes) {
        if (client == null) {
            init();
        }
        if (!fileCosPath.startsWith("/")) {
            fileCosPath = "/"+ fileCosPath;
        }
        if (root != null) {
            fileCosPath = root + fileCosPath;
        }

        UploadFileRequest request = new UploadFileRequest(bucket, fileCosPath, bytes);
        request.setInsertOnly(InsertOnly.OVER_WRITE);
        request.setEnableShaDigest(true);
        String result = client.uploadFile(request);
        JSONObject cosResult = JSON.parseObject(result);
        String msg = (String)cosResult.remove("message");
        cosResult.put("msg", msg);

        return cosResult;
    }

    public JSONObject upload(String fileCosPath, File localFile) throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(localFile);
        return upload(fileCosPath, bytes);
    }

    public JSONObject upload(String fileCosPath, String localFilePath) throws IOException {
        File file = new File(localFilePath);
        return upload(fileCosPath, file);
    }
}
