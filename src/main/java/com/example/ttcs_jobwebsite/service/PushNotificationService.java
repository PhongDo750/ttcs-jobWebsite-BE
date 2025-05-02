package com.example.ttcs_jobwebsite.service;

import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.redis.PresenceService;
import com.example.ttcs_jobwebsite.token.TokenHelper;
import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
public class PushNotificationService {
    @Value("${vapid.public.key}")
    private String publicKey;
    @Value("${vapid.private.key}")
    private String privateKey;

    private PushService pushService;
    @Autowired
    private final PresenceService presenceService;

    public PushNotificationService(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @PostConstruct
    private void init() throws GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        pushService = new PushService(publicKey, privateKey);
    }

    public String getPublicKey() {
        return publicKey;
    }

    public ApiResponse<?> subscribe(String accessToken, Subscription subscription) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        System.out.println("Subscribed to " + subscription.endpoint);
        presenceService.plusSubscription(String.valueOf(userId), subscription);
        return ApiResponse.builder()
                .code(200)
                .message("Đăng ký nhận thông báo thành công")
                .build();
    }

    public ApiResponse<?> unsubscribe(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        presenceService.deleteSubscription(String.valueOf(userId));
        return ApiResponse.builder()
                .code(200)
                .message("Hủy nhận thông báo thành công")
                .build();
    }

    public void sendNotification(Long userId, String messageJson) {
        try {
            Subscription subscription = presenceService.getSubscription(String.valueOf(userId));
            if (!Objects.isNull(subscription)) {
                pushService.send(new Notification(subscription, messageJson));
            }
        } catch (GeneralSecurityException | IOException | JoseException | ExecutionException
                 | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
