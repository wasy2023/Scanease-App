package org.scanease.scanease.api;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.scanease.scanease.model.AllergyRequest;
import org.scanease.scanease.model.AllergyResponse;
import org.scanease.scanease.model.LogInRequest;
import org.scanease.scanease.model.LogInResponse;
import org.scanease.scanease.repo.User;
import org.scanease.scanease.repo.UserRepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
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


        var response = new AllergyResponse();

       if(user.isPresent()){
           String ocrText = performOCR(request.getLabel());
           var wordsInLabel = Arrays.stream(ocrText.toLowerCase(Locale.ROOT).split(",")).collect(Collectors.toList());

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

    private String performOCR(String imagePath) {
        File imageFile = new File(imagePath);
        ITesseract instance = new Tesseract();
        instance.setDatapath("C:\\Program Files\\Tesseract-OCR");

        try{
            return instance.doOCR(imageFile);
        }catch (TesseractException e)
        {
            e.printStackTrace();
            return"";
        }
    }

    @PostMapping("check-login")
    public LogInResponse checkLogIn(@RequestBody LogInRequest request)
    {
        var response = new LogInResponse();
        var username = request.getUsername();
        var password = request.getPassword();
        var users = userRepo.findAll();
        for(User user : users)
        {
            if(username == user.getUserName())
            {
                if(password == user.getPassword()) {
                    response.setId(user.getId());
                    log.info("Got request: {}  found user {}", request,user);
                    break;
                }
                else
                    response.setId(-1);
            }
        }

        return response;

    }

}
