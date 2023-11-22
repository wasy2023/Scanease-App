package org.scanease.scanease.model;

import lombok.Data;

@Data
public class SignupRequest {

    private String userName;

    private String email;

    private String password;

    private String Conditions;
}
