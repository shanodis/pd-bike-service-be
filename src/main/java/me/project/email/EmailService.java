package me.project.email;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailService {

    private final static Logger LOGGER =
            LoggerFactory.getLogger(EmailService.class);

    private JavaMailSender mailSender;


    @Async
    public void send(String to, String email , String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(subject);
            //helper.setFrom("bike-no-reply@op.pl");
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            LOGGER.error("failed to send email", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"failed to send email");
        }
    }


}
