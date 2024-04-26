package common.service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.Part;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import common.Env;

public class UploadService {
        Regions clientRegion = Regions.AP_SOUTHEAST_1;
        String bucketName = Env.S3_BUCKET_NAME;
        AWSCredentials awsCredentials = new BasicAWSCredentials(Env.AWS_ACCESS_KEY, Env.AWS_SECRET_KEY);
        AmazonS3 s3client = AmazonS3ClientBuilder
                        .standard()
                        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                        .withRegion(Regions.AP_SOUTHEAST_1)
                        .build();

        public String uploadImage(Part imagePart, String imageName, int userId) throws IOException {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("image/*");
                String filePath = userId + "/images/" + imageName;
                PutObjectRequest request = new PutObjectRequest(bucketName, filePath,
                                imagePart.getInputStream(), metadata);
                s3client.putObject(request);
                return Env.S3_ACCESS_POINT + "/" + filePath;
        }
}
