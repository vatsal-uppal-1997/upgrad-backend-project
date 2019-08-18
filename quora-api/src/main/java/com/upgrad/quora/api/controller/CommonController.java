package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserDetailsService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class CommonController {
    @Autowired
    private UserDetailsService userDetailsService;
    @RequestMapping(method = RequestMethod.GET,path = "/userprofile/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        final UserEntity userEntity = userDetailsService.getUser(userUuid,authorization);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(userEntity.getFirstName()).lastName(userEntity.getLastName()).userName(userEntity.getUsername()).emailAddress(userEntity.getEmail()).contactNumber(userEntity.getContactnumber()).country(userEntity.getCountry()).aboutMe(userEntity.getAboutme()).dob(userEntity.getDob());
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
}
