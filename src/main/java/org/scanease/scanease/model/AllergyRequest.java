package org.scanease.scanease.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AllergyRequest {

    //here we add the label string from mobile app
    private int userId;
    private MultipartFile image;
}
