package com.rating.user.service.controllers;

import com.rating.user.service.entities.User;
import com.rating.user.service.services.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    //Create
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User user1 = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

    int retryCount = 1;
    //get single user
    @GetMapping("/{userId}")
    //@CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelFallback")
    @Retry(name = "ratingHotelRetry", fallbackMethod = "ratingHotelFallback")
    //@RateLimiter(name = "userRateLimiter", fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<User> getSingleUser(@PathVariable String userId){
        logger.info("Get Single user Handler: UserController");
        logger.info("Retry count: {}", retryCount);
        retryCount++;
        User user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    //fallback method for circuit breaker
    public ResponseEntity<User> ratingHotelFallback(String userId, Exception ex){
        logger.info("Fallback is executed because service is down: ", ex.getMessage());
        User user = User.builder().email("dummy@dmail.com").name("Dummy").about("Dummy user").userID("123243").build();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    //get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){
        List<User> allUser = userService.getAllUsers();
        return ResponseEntity.ok(allUser);
    }
}
