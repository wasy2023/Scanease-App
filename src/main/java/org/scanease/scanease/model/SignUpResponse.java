package org.scanease.scanease.model;

import lombok.Data;

@Data
public class SignUpResponse {
    private boolean verified;

    private String message;
}
