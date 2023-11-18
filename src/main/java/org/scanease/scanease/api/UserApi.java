package org.scanease.scanease.api;

import lombok.Data;
import org.scanease.scanease.repo.User;
import org.scanease.scanease.repo.UserRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Data
@RestController
public class UserApi {

    private final UserRepo userRepo;

    @GetMapping("/users")
    public List<User> users(){
        return userRepo.findAll();
    }
}
