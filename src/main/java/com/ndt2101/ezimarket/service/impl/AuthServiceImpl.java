package com.ndt2101.ezimarket.service.impl;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.ndt2101.ezimarket.dto.*;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import com.ndt2101.ezimarket.repository.EmailValidationStatusRepository;
import com.ndt2101.ezimarket.repository.RoleRepository;
import com.ndt2101.ezimarket.repository.UserRepository;
import com.ndt2101.ezimarket.service.AuthService;
import com.ndt2101.ezimarket.service.MailService;
import com.ndt2101.ezimarket.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private EmailValidationStatusRepository emailStatusRepository;
    @Override
    public String register(UserDTO userDTO) throws Exception {
        if (!userRepository.existsByLoginName(userDTO.getLoginName())) { // th chua ton tai login name
            if (!userRepository.existsByEmailAddress(userDTO.getEmailAddress())) { // th chua ton tai email
                return saveUser(userDTO);
            } else { // th da ton tai email
                UserLoginDataEntity userLoginData = userRepository.findByEmailAddress(userDTO.getEmailAddress()).get();
                if (userLoginData.getEmailValidationStatus().getStatusDescription().equals("invalid")) { // th da ton tai email nhung chua valid
                    return checkTimoutAndSave(userLoginData, userDTO, "Email address has already taken!");
                } else { // th da ton tai email va da valid
                    throw ApplicationException.builder()
                            .status(HttpStatus.CONFLICT)
                            .message("Email address has already taken!")
                            .build();
                }
            }
        } else { // th da ton tai login name
            UserLoginDataEntity userLoginData = userRepository.findByLoginName(userDTO.getLoginName()).get();
            if (userLoginData.getEmailValidationStatus().getStatusDescription().equals("invalid")) { // th chua valid
                if (!userLoginData.getEmailAddress().equals(userDTO.getEmailAddress())) { // th khac email
                    return checkTimoutAndSave(userLoginData, userDTO, "Login name is taken!");
                } else { // th cung email
                    userRepository.deleteById(userLoginData.getId());
                    return saveUser(userDTO);
                }
            } else { // th da valid
                throw ApplicationException.builder()
                        .status(HttpStatus.CONFLICT)
                        .message("Login name has already taken!")
                        .build();
            }
        }
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        if (securityContext.getAuthentication().isAuthenticated()) {
            String jwt = jwtUtils.generateToken(request.getUsername());
            UserLoginDataEntity userLoginData = userRepository.findByLoginName(request.getUsername())
                    .orElseThrow(() -> new NotFoundException("User not found!"));
            AuthenticationResponse authenticationResponse = modelMapper.map(userLoginData, AuthenticationResponse.class);
            authenticationResponse.setAccessToken(jwt);
            return authenticationResponse;
        } else {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Authentication fail");
        }
    }
    // comment neu khong insert duoc db
    @Override
    public AuthenticationResponse loginWithGoogle(String token) throws Exception {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        if (decodedToken != null) {
            String email = decodedToken.getEmail();
            Optional<UserLoginDataEntity> userLoginDataOptional = userRepository.findByEmailAddress(email);
            UserLoginDataEntity userLoginData;
            if (userLoginDataOptional.isPresent()) {
                userLoginData = userLoginDataOptional.get();
            } else {
                String password = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 12);
                Date time = new Date();
                String verifiedToken = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 8);
                userLoginData = UserLoginDataEntity.builder()
                        .firstName("Email:")
                        .lastName(decodedToken.getName())
                        .phone(null)
                        .role(roleRepository.getByDescription("ROLE_USER")
                                .orElseThrow(() -> new NotFoundException("Role user not found")))
                        .loginName(decodedToken.getUid().substring(0,14))
                        .password(passwordEncoder.encode(password))
                        .emailAddress(email)
                        .confirmationToken(verifiedToken)
                        .confirmationTokenGeneratedTime(time)
                        .avatarUrl(decodedToken.getPicture())
                        .emailValidationStatus(emailStatusRepository.findByStatusDescription("valid")
                                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Email status not found!")))
                        .build();
                userRepository.save(userLoginData);
            }
            String jwt = jwtUtils.generateToken(userLoginData.getLoginName());
            AuthenticationResponse authenticationResponse = modelMapper.map(userLoginData, AuthenticationResponse.class);
            authenticationResponse.setAccessToken(jwt);
            return authenticationResponse;
        } else {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Authentication fail");
        }
    }
    //

    @Override
    public String resetPassword(ResetUserPasswordDTO resetUserPasswordDTO) {
        UserLoginDataEntity userLoginData = userRepository.findByLoginName(resetUserPasswordDTO.getLoginName())
                .orElseThrow(() -> new NotFoundException("User " + resetUserPasswordDTO.getLoginName() +" not found!"));

        String time = new Date().toString();
        String newPassword = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 12);

        Context context = new Context();
        context.setVariable("password", newPassword);
        context.setVariable("time", time);
        String html = templateEngine.process("password-change-email", context);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            try {
                mailService.send("Password change", html, userLoginData.getEmailAddress(), true);
                userLoginData.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(userLoginData);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });
        return "New password has been sent to your email, please check your inbox";
    }

    @Override
    public String verify(VerifiedRequestDTO verifiedRequestDTO) throws ApplicationException {
        UserLoginDataEntity userLoginData = userRepository.findByLoginName(verifiedRequestDTO.getLoginName())
                .orElseThrow(() -> new NotFoundException("User " + verifiedRequestDTO.getLoginName() + " not found!"));
        Date tokenGeneratedTime = userLoginData.getConfirmationTokenGeneratedTime();
        Date nowDate = new Date();
        long timeDiff = (nowDate.getTime() - tokenGeneratedTime.getTime()) / 1000;
        if (verifiedRequestDTO.getVerifiedToken().equals(userLoginData.getConfirmationToken())) {  // th cung token
            if (timeDiff < 60*3) { // < 3 minus
                userLoginData.setEmailValidationStatus(
                        emailStatusRepository.findByStatusDescription("valid")
                                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Email status not found!")));
                userRepository.save(userLoginData);
                return "Verify successfully! Your account is validated";
            } else { // > 3 minus
                throw new ApplicationException(HttpStatus.REQUEST_TIMEOUT, "Timeout!. Try again later");
            }
        } else { // th khac token. th1: nhap sai; th2: user khac da lay thanh cong login name do thoi gian validate da qua 1 ngay
            throw new ApplicationException(HttpStatus.CONFLICT, "Wrong token!. Try again later");
        }
    }

    @Override
    public String resendVerifiedToken(ResendVerifiedTokenDTO email) {
        UserLoginDataEntity userLoginData = userRepository.findByEmailAddress(email.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found!. Please register again"));
        sendVerifiedTokenEmail(userLoginData);
        return "Validating";
    }

    private void sendVerifiedTokenEmail(UserLoginDataEntity userLoginData) {
        Date time = new Date();
        String verifiedToken = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 8);

        Context context = new Context();
        context.setVariable("verifiedToken", verifiedToken);
        context.setVariable("time", time.toString());
        String html = templateEngine.process("verify-account-email", context);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            try {
                mailService.send("Verify account", html, userLoginData.getEmailAddress(), true);
                userLoginData.setConfirmationToken(verifiedToken);
                userLoginData.setConfirmationTokenGeneratedTime(time);
                userRepository.save(userLoginData);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String saveUser(UserDTO userDTO) throws Exception {
        UserLoginDataEntity userLoginData = modelMapper.map(userDTO, UserLoginDataEntity.class);
        userLoginData.setRole(roleRepository.getByDescription("ROLE_USER")
                .orElseThrow(() -> new NotFoundException("Role user not found")));
        userLoginData.setPassword(passwordEncoder.encode(userLoginData.getPassword()));
        userLoginData.setEmailValidationStatus(emailStatusRepository.findByStatusDescription("invalid")
                .orElseThrow(() -> new NotFoundException("Email status not found!")));
        // send email
        sendVerifiedTokenEmail(userLoginData);
        return "Validating";
    }

    private String checkTimoutAndSave(UserLoginDataEntity userLoginData, UserDTO userDTO, String errorMessage) throws Exception {
        long timeDiff = (new Date().getTime() - userLoginData.getConfirmationTokenGeneratedTime().getTime()) / 1000; // in second
        if (timeDiff > 60*60*24) { // th da qua 1 ngay
            userRepository.deleteById(userLoginData.getId());
            return saveUser(userDTO);
        } else { // th chua qua 1 ngay, dang doi validate tu email
            throw ApplicationException.builder()
                    .status(HttpStatus.CONFLICT)
                    .message(errorMessage)
                    .build();
        }
    }
}