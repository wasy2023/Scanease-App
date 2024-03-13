package org.scanease.scanease.api;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.scanease.scanease.model.*;
import org.scanease.scanease.repo.User;
import org.scanease.scanease.repo.UserRepo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Data
@RestController
public class MobileApi {


    private final UserRepo userRepo;


    @RequestMapping(method = RequestMethod.POST, value = "/fileUpload",
            consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public AllergyResponse checkAllergy(@RequestPart(value = "file", required = true ) MultipartFile file, @RequestParam("userId") int userId){

       var user =  userRepo.findById(userId);


        var response = new AllergyResponse();

       if(user.isPresent()){
           String ocrText = performOCR(file);
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

       log.info("Got request: {}  found user {}", userId);


       return response;
    }

    private String performOCR(MultipartFile imageFile) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp-image", null);
            imageFile.transferTo(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

            ITesseract instance = new Tesseract();
        instance.setDatapath("C:\\Program Files\\Tesseract-OCR");

        try{
            return instance.doOCR(tempFile);
        }catch (TesseractException p)
        {
            p.printStackTrace();
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
    private boolean checkPassword(String password)
    {
        if(password.length() > 10)
            return false;
        if(password.indexOf('1')==-1)
            return false;
        return true;
    }
    @PostMapping("CheckSignUp")
    public SignUpResponse checkSignUp( @RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("email") String email)
    {
        //it breaks when given values, possibly because of the database, given error: Invalid object Utilizator_seq????
        var response = new SignUpResponse();
        // var username = request.getUserName();
        // var password = request.getPassword();
        //var email = request.getEmail();
        for(User user : userRepo.findAll())
        {
            if(user.getUserName() == username)
            {
                response.setVerified(false);
                response.setMessage("Username already exists!");
                return response;
            }
            if(user.getEmail() == email)
            {
                response.setMessage("Email is already registered!");
                response.setVerified(false);
                return response;
            }
        }
        if(!checkPassword(password))
        {
            response.setVerified(false);
            response.setMessage("Your password must contain at least one character 1");
            return response;
        }
        User user1 = new User();
        user1.setUserName(username);
        user1.setEmail(email);
        user1.setPassword(password);
        user1.setId(userRepo.findAll().size()+1);
        userRepo.save(user1);
        response.setMessage("You signed up !");
        response.setVerified(true);
        return response;

    }

}
