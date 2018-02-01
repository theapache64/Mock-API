package com.theah64.mock_api.utils;

import com.theah64.webengine.exceptions.MailException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Created by theapache64 on 9/1/18.
 */
public class MailHelper {

    private static String gmailUsername, gmailPassword;

    public static void init(final String gmailUsername, final String gmailPassword) {
        MailHelper.gmailUsername = gmailUsername;
        MailHelper.gmailPassword = gmailPassword;
    }

    @SuppressWarnings("SameParameterValue")
    public static void sendMail(String to, final String subject, String message, String fromName) throws MailException {

        if (gmailUsername == null || gmailPassword == null) {
            throw new IllegalArgumentException("Gmail username and password shouldn't be null");
        }







        final Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(gmailUsername, gmailPassword);
            }
        });

        Message mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(gmailUsername, fromName));
            if (to.contains(",")) {
                //Bulk mail
                mimeMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(to));
            } else {
                //Single mail
                mimeMessage.setRecipient(Message.RecipientType.TO, InternetAddress.parse(to)[0]);
            }
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(message, "text/html; charset=utf-8");

            Transport.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new MailException(e.getMessage());
        }

    }

}
