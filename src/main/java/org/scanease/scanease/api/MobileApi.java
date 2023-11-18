package org.scanease.scanease.api;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.scanease.scanease.model.AllergyRequest;
import org.scanease.scanease.model.AllergyResponse;
import org.scanease.scanease.repo.UserRepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Data
@RestController
public class MobileApi {


    private final UserRepo userRepo;


    @PostMapping("check-allergy")
    public AllergyResponse checkAllergy(@RequestBody AllergyRequest request){



       var user =  userRepo.findById(request.getUserId());

       var wordsInLabel = Arrays.stream(request.getLabel().toLowerCase(Locale.ROOT).split(",")).collect(Collectors.toSet());

        var response = new AllergyResponse();

       if(user.isPresent()){

           response.setConditions(user.get().getConditions().stream().filter(c ->{
               var ingredientWords = Arrays.stream(c.getHarmfulIngredients().toLowerCase(Locale.ROOT).split(",")).collect(Collectors.toSet());

               for(var ing: ingredientWords){
                   for(var labelWord: wordsInLabel){
                       if(ing.contains(labelWord) || labelWord.contains(ing)){
                          return true;
                       }
                   }
               }
               return false;
           }).map(c -> c.getNameCondition()).collect(Collectors.toList()));

           log.info(user.get().getConditions().stream().map(p ->p.getNameCondition()).toString());
       }
//       var issuffering = isSufferingRepo.findById(String.valueOf(request.getUserId()));

       log.info("Got request: {}  found user {}", request,user);


       return response;
    }

}
