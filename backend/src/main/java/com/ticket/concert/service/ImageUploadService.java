package com.ticket.concert.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ticket.concert.dto.ImageInfo;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageUploadService {
    private final Cloudinary cloudinary;

    public List<ImageInfo> uploadImages(List<MultipartFile> files) {
        List<ImageInfo> result = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Map<String, Object> uploaded = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.emptyMap()
                );
                result.add(new ImageInfo(
                        (String) uploaded.get("secure_url"),
                        (String) uploaded.get("public_id")
                ));
            } catch (IOException e) {
                throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        }

        return result;
    }

    public void delete(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }
}
