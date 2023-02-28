package com.ndt2101.ezimarket.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
public class ConfigSecretKey {
    /**
     * method to generate secret key for jwt with HS256 algorithm
     * @return secret key
     */
    @Bean
    public SecretKey secretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    @Bean
    public FirebaseApp firebaseConfig() throws IOException {

        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/ezi-market-firebase-adminsdk-18xex-fa8c704037.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://ezi-market-default-rtdb.asia-southeast1.firebasedatabase.app")
                .setStorageBucket("ezi-market.appspot.com")
                .build();

        return FirebaseApp.initializeApp(options);
    }
}
