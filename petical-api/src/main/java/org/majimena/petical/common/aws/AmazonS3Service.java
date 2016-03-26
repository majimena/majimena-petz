package org.majimena.petical.common.aws;

/**
 * Created by todoken on 2015/08/13.
 */
public interface AmazonS3Service {

    /**
     * S3ストレージにファイルをアップロードする.
     *
     * @param filename ファイル名
     * @param bytes    アップロードするファイルのバイナリデータ
     * @return アップロード先のURL
     */
    String upload(String filename, byte[] bytes);

    /**
     * S3ストレージのファイルを削除する.
     *
     * @param filename ファイル名
     */
    void delete(String filename);
}
