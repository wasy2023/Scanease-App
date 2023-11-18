package org.scanease.scanease.repo;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "healthcondition")

public class HealthCondition {
    @Id
    private String nameCondition;

    private String conditionType;

    private String harmfulIngredients;
}
