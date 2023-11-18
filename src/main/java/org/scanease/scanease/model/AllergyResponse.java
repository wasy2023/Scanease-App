package org.scanease.scanease.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllergyResponse {



    private List<String> conditions = new ArrayList<>();

    public boolean isOk(){
        return conditions.isEmpty();
    }

}
