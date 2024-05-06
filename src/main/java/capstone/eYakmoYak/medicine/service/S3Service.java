package capstone.eYakmoYak.medicine.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;  // S3 버킷 이름

    public String uploadImage(String imageUrl, String dirName) throws IOException {
        URL url = new URL(imageUrl);  // 이미지 URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        String uniqueFileName = UUID.randomUUID().toString() + ".jpg";  // 고유한 파일 이름 생성
        String s3Key = dirName + "/" + uniqueFileName;

        try (InputStream inputStream = connection.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentDisposition("inline");  // 브라우저에서 렌더링 가능
            metadata.setContentType("image/jpeg");  // 이미지 유형

            amazonS3.putObject(new PutObjectRequest(bucket, s3Key, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));  // S3 업로드 및 권한 설정

            return amazonS3.getUrl(bucket, s3Key).toString();  // S3 URL 반환

        } finally {
            connection.disconnect();  // 연결 해제
        }
    }
}
