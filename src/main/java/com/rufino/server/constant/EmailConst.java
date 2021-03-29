package com.rufino.server.constant;

public class EmailConst {
    public static final String FROM_EMAIL = "noreplay@rufino.com";
    public static final String CC_EMAIL = "";
    public static final String EMAIL_SUBJECT = "Rufino, LLC - New Password";
    public static final String GMAIL_SMTP_SERVER = "smtp.gmail.com";
    public static final String TRANSPORT_PROTOCOL = "mail.transport.protocol";
    public static final String SMTP_AUTH = "mail.smtp.auth";
    public static final String SMTP_PORT = "mail.smtp.port";
    public static final int DEFAULT_PORT = 587;
    public static final String SMTP_START_TLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String NEW_PASSWORD_MSG =  "Hello %s \n\n Your new account password is: %s \n\n The Support Team";
}
