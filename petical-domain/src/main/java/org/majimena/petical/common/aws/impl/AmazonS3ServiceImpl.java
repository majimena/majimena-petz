package org.majimena.petical.common.aws.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.majimena.petical.common.aws.AmazonS3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

/**
 * Created by todoken on 2015/08/13.
 */
public class AmazonS3ServiceImpl implements AmazonS3Service {
    /**
     * ログ.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AmazonS3ServiceImpl.class);

    /**
     * S3クライアント.
     */
    private AmazonS3Client amazonS3Client;

    /**
     * バケット名.
     */
    private String bucketName;

    /**
     * ファイルを保存、取得する際のコンテントタイプ.
     */
    private String contentType;

    public void setAmazonS3Client(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String upload(String filename, byte[] bytes) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(bytes.length);

        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        PutObjectRequest request = new PutObjectRequest(bucketName, filename, stream, metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        PutObjectResult result = amazonS3Client.putObject(request);
        LOG.debug(ToStringBuilder.reflectionToString(result));

        String url = amazonS3Client.getResourceUrl(bucketName, filename);
        LOG.info("file upload url [" + url + "].");
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String filename) {
        // TODO キーが微妙で消せない（エンドポイント付きでDBに保存されているため）
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, filename);
        amazonS3Client.deleteObject(request);
    }
}
