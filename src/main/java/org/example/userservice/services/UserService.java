package org.example.userservice.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.userservice.dtos.SendEmailEventDto;
import org.example.userservice.exceptions.EmailTakenException;
import org.example.userservice.exceptions.InvalidPasswordException;
import org.example.userservice.exceptions.InvalidTokenException;
import org.example.userservice.exceptions.NotFoundException;
import org.example.userservice.models.Token;
import org.example.userservice.models.User;
import org.example.userservice.repositories.TokenRepository;
import org.example.userservice.repositories.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository,
                       TokenRepository tokenRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       KafkaTemplate<String, String> kafkaTemplate,
                       ObjectMapper objectMapper) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public User signup(String name, String email, String password) throws EmailTakenException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            //redirect user to the login page
            throw new EmailTakenException("Email Already Exists");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);

        //TODO- use BcryptPasswordEncoder to hash the password
        user.setPassword(bCryptPasswordEncoder.encode(password));

        user=userRepository.save(user);

        SendEmailEventDto sendEmailEventDto = new SendEmailEventDto();
        sendEmailEventDto.setEmail(email);
        sendEmailEventDto.setSubject("welcome message");
        sendEmailEventDto.setBody("welcome to userService");

        //Kafka
        kafkaTemplate.send(
                "sendEmailEvent",
                objectMapper.writeValueAsString(sendEmailEventDto)
        );

        return user;
    }

    public Token login(String email, String password) throws NotFoundException, InvalidPasswordException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            //redirect to signup page or throw exception
            throw new NotFoundException("Email Not Found");
        }
        User user = optionalUser.get();

        //if password not matched
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            //throw exception
            throw new InvalidPasswordException("Invalid Password");
        }

        Token token = new Token();
        token.setUser(user);

        LocalDate localDate = LocalDate.now().plusDays(30);
        Date expiryDate=Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());


        token.setExpiryAt(expiryDate);

        token.setValue(RandomStringUtils.randomAlphanumeric(120));//using apache commons lang3

        return tokenRepository.save(token);
    }

    public User validateToken(String tokenValue) throws InvalidTokenException {
        //token should be present in the DB, and expiry date is greater than current date
        Optional<Token> optionalToken = tokenRepository.findByValueAndExpiryAtAfter(tokenValue, new Date());
        if(optionalToken.isEmpty()){
            //token Invalid
            throw new InvalidTokenException("Invalid token or token expired please login again");
        }
        Token token = optionalToken.get();

        return token.getUser();
    }
}