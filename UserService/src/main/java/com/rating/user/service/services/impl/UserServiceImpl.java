package com.rating.user.service.services.impl;

import com.rating.user.service.entities.Hotel;
import com.rating.user.service.entities.Rating;
import com.rating.user.service.entities.User;
import com.rating.user.service.exceptions.ResourceNotFoundException;
import com.rating.user.service.external.services.HotelService;
import com.rating.user.service.repositories.UserRepository;
import com.rating.user.service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;

    @Override
    public User saveUser(User user) {
        String randomUserId = UUID.randomUUID().toString();
        user.setUserID(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {

        List<User> allUsers = userRepository.findAll();
        /*for(User user: allUsers){
            ArrayList<Rating> ratingsOfUser = restTemplate.getForObject("http://localhost:8083/ratings/users/"+user.getUserID(), ArrayList.class);
            user.setRatings(ratingsOfUser);
        }*/
        return allUsers;
    }

    @Override
    public User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User with given Id is not found on server"));
        //fetch ratings for the user from RATING-SERVICE
        Rating[] ratingsOfUser = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserID(), Rating[].class);

        List<Rating> ratings = Arrays.stream(ratingsOfUser).toList();

        //get Hotel data and add to ratings
        List<Rating> ratingList = ratings.stream().map(rating -> {
            //Hotel hotel = restTemplate.getForObject("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);
            Hotel hotel = hotelService.getHotel(rating.getHotelId());
            rating.setHotel(hotel);
            return rating;
        }).collect(Collectors.toList());

        user.setRatings(ratingList);
        return user;
    }
}
