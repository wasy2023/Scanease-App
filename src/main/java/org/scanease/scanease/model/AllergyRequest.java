package org.scanease.scanease.model;

import lombok.Data;

@Data
public class AllergyRequest {

    private String label;
    //here we add the label string from mobile app
    private int userId;
}
