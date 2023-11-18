package org.scanease.scanease.repo;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Utilizator")
public class User {

    @Id
    @GeneratedValue
    private int id;

    private String userName;

    private String email;

    @ManyToMany
    @JoinTable(name = "issuffering", joinColumns = {@JoinColumn(name ="user_id")}
            , inverseJoinColumns = {@JoinColumn(name ="name_condition")})
    private List<HealthCondition> conditions = new ArrayList<>();

}
