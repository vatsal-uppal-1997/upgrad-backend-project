package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private SignupBusinessService businessService;
    @Autowired
    AuthenticationService authenticationService;
    @RequestMapping(method = RequestMethod.POST,path="/user/signup",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest)throws SignUpRestrictedException {
        UserEntity entity = null;
        if(businessService.checkUserName(signupUserRequest.getUserName()) && businessService.checkEmail(signupUserRequest.getEmailAddress())){
            final UserEntity userEntity = new UserEntity();
            userEntity.setUuid(UUID.randomUUID().toString());
            userEntity.setFirstName(signupUserRequest.getFirstName());
            userEntity.setLastName(signupUserRequest.getLastName());
            userEntity.setUsername(signupUserRequest.getUserName());
            userEntity.setEmail(signupUserRequest.getEmailAddress());
            userEntity.setPassword(signupUserRequest.getPassword());
            userEntity.setContactnumber(signupUserRequest.getContactNumber());
            userEntity.setDob(signupUserRequest.getDob());
            userEntity.setAboutme(signupUserRequest.getAboutMe());
            userEntity.setRole("nonadmin");
            userEntity.setCountry(signupUserRequest.getCountry());
            entity = businessService.signup(userEntity);

        }
        SignupUserResponse userResponse = new SignupUserResponse().id(entity.getUuid()).status("REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse,HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST,path="/user/signin",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        UserAuthTokenEntity userAuthToken = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
        SigninResponse authorizedUserResponse = new SigninResponse().id(userAuthToken.getUuid()).message("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token",userAuthToken.getAccess_Token());
        return  new ResponseEntity<SigninResponse>(authorizedUserResponse,headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST,path="/user/signout",produces = MediaType.APPLICATION_JSON_UTF8_VALUE,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> logout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.signout(authorization);
        SignoutResponse signoutResponse = new SignoutResponse().id(userAuthTokenEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse,HttpStatus.OK);
    }
}
