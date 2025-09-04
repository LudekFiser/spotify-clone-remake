package com.example.spotifycloneremade.service;

import com.example.spotifycloneremade.utils.otp.OtpService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.util.StringUtils;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final OtpService otpService;


    @Value("${spring.mail.properties.mail.smtp.from}")
    private String sentBy;

    @Value("${app.frontend.base-url:https://app.example}") // URL pro tlačítko
    private String frontendBaseUrl;

    /**
     * Sends a beautifully designed post-registration email with HTML and plain text fallback
     *
     * @param toEmail recipient email address
     * @param name user's name for personalization
     * @throws MessagingException if email sending fails
     */
    public void sendPostRegisterEmail(String toEmail, String name) throws MessagingException {
        try {
            // 1) Create Thymeleaf context with variables
            Context context = new Context();
            context.setVariable("name", StringUtils.capitalize(name.trim()));
            context.setVariable("ctaUrl", frontendBaseUrl);
            context.setVariable("currentYear", Year.now().getValue());

            // 2) Process HTML template
            String htmlContent = templateEngine.process("email/post-register", context);

            // 3) Create plain text fallback
            String plainTextContent = createPlainTextFallback(name, frontendBaseUrl);

            // 4) Build and send email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            // Set email properties
            helper.setFrom(sentBy);
            helper.setTo(toEmail);
            helper.setSubject("🎉 Welcome to Our Platform - Registration Successful!");
            helper.setText(plainTextContent, htmlContent); // plain text first, then HTML

            // Optional: Add priority header for important welcome emails
            helper.setPriority(3); // High priority (1=highest, 3=normal, 5=lowest)

            // Optional: Add custom headers for better deliverability
            mimeMessage.setHeader("X-Auto-Response-Suppress", "OOF, DR, RN, NRN");
            mimeMessage.setHeader("List-Unsubscribe-Post", "List-Unsubscribe=One-Click");

            // Send the email
            mailSender.send(mimeMessage);

            log.info("Registration success email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send registration email to: {} - Error: {}", toEmail, e.getMessage());
            throw new MessagingException("Failed to send registration email", e);
        }
    }

    /**
     * Creates a clean plain text version for email clients that don't support HTML
     */
    private String createPlainTextFallback(String name, String ctaUrl) {
        return String.format("""
            🎉 WELCOME TO OUR PLATFORM!
            
            Hello %s!
            
            ✅ REGISTRATION SUCCESSFUL!
            
            Your account has been successfully created and is ready to use.
            Welcome to our amazing community!
            
            We're thrilled to have you join our growing community! You now have
            access to all our premium features and services.
            
            FEATURES YOU'LL LOVE:
            ⚡ Lightning Fast Performance
            🔒 Secure & Safe Environment
            📱 Mobile-Ready Experience
            
            If you have any questions or need assistance, our support team is
            always here to help. Don't hesitate to reach out!
            
            GET STARTED: %s
            
            Thank you for choosing our services! 💖
            
            ---
            This is an automated message, please do not reply to this email.
            """,
                StringUtils.capitalize(name.trim()),
                ctaUrl
        );
    }

    /**
     * Alternative method for sending with additional options
     */
    public void sendPostRegisterEmailWithOptions(String toEmail, String name,
                                                 Map<String, Object> additionalVariables) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", StringUtils.capitalize(name.trim()));
        context.setVariable("ctaUrl", frontendBaseUrl);
        context.setVariable("currentYear", Year.now().getValue());

        // Add any additional variables
        if (additionalVariables != null) {
            additionalVariables.forEach(context::setVariable);
        }

        String htmlContent = templateEngine.process("email/post-register", context);
        String plainTextContent = createPlainTextFallback(name, frontendBaseUrl);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setFrom(sentBy);
        helper.setTo(toEmail);
        helper.setSubject("🎉 Welcome to Our Platform - Registration Successful!");
        helper.setText(plainTextContent, htmlContent);

        mailSender.send(mimeMessage);
        log.info("Enhanced registration email sent successfully to: {}", toEmail);
    }

    public void sendResetPasswordCode(String toEmail, String otp) {
        var message = new SimpleMailMessage();
        message.setFrom(sentBy);
        message.setTo(toEmail);
        message.setSubject("Password Reset CODE");
        message.setText("This is your password reset code! ---->" + otp + "<----\n\nUse this CODE to proceed with resetting your password");
        mailSender.send(message);
    }

    public void sendAccountDeletionCode(String toEmail, String otp) {
        var message = new SimpleMailMessage();
        message.setFrom(sentBy);
        message.setTo(toEmail);
        message.setSubject("Account deletion CODE");
        message.setText("Your account deletion code ---->" + otp + "<----\n\nUse this CODE to proceed with deleting your account");
        mailSender.send(message);
    }

    public void sendAccountVerificationCode(String toEmail, String otp) {
        var message = new SimpleMailMessage();
        message.setFrom(sentBy);
        message.setTo(toEmail);
        message.setSubject("Account verification code");
        message.setText("This is your account verification code! ---->" + otp + "<----\n\nUse this CODE to proceed with verification code");
        mailSender.send(message);
    }

    public void send2FAVerificationCode(String toEmail, String otp) {
        var message = new SimpleMailMessage();
        message.setFrom(sentBy);
        message.setTo(toEmail);
        message.setSubject("2FA Login code");
        message.setText("This is your 2FA Login code! ---->" + otp + "<----\n\nUse this CODE to proceed with verification code");
        mailSender.send(message);
    }

}
