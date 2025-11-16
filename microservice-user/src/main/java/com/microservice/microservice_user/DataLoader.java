package com.microservice.microservice_user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.microservice.microservice_user.model.User;
import com.microservice.microservice_user.repository.UserRepository;

import net.datafaker.Faker;

@Profile("test")
@Component
public class DataLoader implements CommandLineRunner{
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
    
        for(Long i = (long) 0; i < 10; i++){
            String firstName = faker.name().firstName();
            String fullName = faker.name().fullName();
    
            User user = new User();
            user.setId(i+1);
            user.setName(fullName);
            user.setEmail(firstName.toLowerCase() + "@duocuc.cl");
            user.setRol("USER");
    
            userRepository.save(user);
        }
    }

}
