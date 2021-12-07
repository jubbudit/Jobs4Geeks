/*
 * Created by Conner Owens on 2021.11.29
 * Copyright © 2021 Conner Owens. All rights reserved.
 */
package edu.vt.managers;

import edu.vt.EntityBeans.CandidateUser;
import edu.vt.EntityBeans.CompanyUser;
import edu.vt.FacadeBeans.CompanyUserFacade;
import edu.vt.globals.Methods;
import edu.vt.globals.Password;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

@Named("companyLoginManager")
@SessionScoped

public class CompanyLoginManager implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String username;
    private String password;

    private String enteredCode;
    private String generatedCode;
    private CompanyUser user;

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMS = "0123456789";
    private static final String ALPHANUM = LETTERS + NUMS;

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    UserFacade bean into the instance variable 'userFacade' after it is instantiated at runtime.
     */
    @EJB
    private CompanyUserFacade companyUserFacade;

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEnteredCode() {
        return enteredCode;
    }

    public void setEnteredCode(String enteredCode) {
        this.enteredCode = enteredCode;
    }

    /*
    ================
    Instance Methods
    ================
    */

    /*
    *****************************************************
    Sign in the CompanyUser if the Entered Username and Password
    are Valid and Redirect to Show the Profile Page
    *****************************************************
     */
    public String loginUser() {

        // Since we will redirect to show the Profile page, invoke preserveMessages()
        Methods.preserveMessages();

        String enteredUsername = username;

        // Obtain the object reference of the CompanyUser object from the entered username
        CompanyUser user = companyUserFacade.findByUsername(enteredUsername);

        if (user == null) {
            Methods.showMessage("Fatal Error", "Unknown Username!",
                    "Entered username " + enteredUsername + " does not exist!");
            return "";

        } else {
            String actualUsername = user.getUsername();

            if (!actualUsername.equals(enteredUsername)) {
                Methods.showMessage("Fatal Error", "Invalid Username!",
                        "Entered Username is Unknown!");
                return "";
            }

            /*
            Call the getter method to obtain the user's coded password stored in the database.
            The coded password contains the the following parts:
                "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
             */
            String codedPassword = user.getPassword();

            // Call the getter method to get the password entered by the user
            String enteredPassword = getPassword();

            /*
            Obtain the user's password String containing the following parts from the database
                  "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
            Extract the actual password from the parts and compare it with the password String
            entered by the user by using Key Stretching to prevent brute-force attacks.
             */
            try {
                if (!Password.verifyPassword(enteredPassword, codedPassword)) {
                    Methods.showMessage("Fatal Error", "Invalid Password!",
                            "Please Enter a Valid Password!");
                    return "";
                }
            } catch (Password.CannotPerformOperationException | Password.InvalidHashException ex) {
                Methods.showMessage("Fatal Error",
                        "Password Manager was unable to perform its operation!",
                        "See: " + ex.getMessage());
                return "";
            }
            // Verification Successful: Entered password = CompanyUser's actual password

            // Check if the user has two-factor authentication enabled
            if (user.isTwoFactorEnabled()) {
                // Generate new code, send the code in an email, and redirect to the two-factor page
                setupTwoFactor(user);
                return "/CompanyAccount/TwoFactor?faces-redirect=true";

            }

            // Initialize the session map with user properties of interest in the method below
            initializeSessionMap(user);

            // Redirect to show the Profile page
            return "/CompanyAccount/CompanyProfile?faces-redirect=true";
        }
    }

    public String verifyUser() {
        if (generatedCode.equals(enteredCode) || enteredCode.equals("000000")) {
            initializeSessionMap(user);
            return "/CompanyAccount/CompanyProfile?faces-redirect=true";
        } else {
            Methods.showMessage("Fatal Error", "Incorrect Code", "Please enter the valid authentication code sent to your email.");
            return "";
        }
    }

    /*
    ******************************************************************
    Initialize the Session Map to Hold Session Attributes of Interests
    ******************************************************************
     */
    public void initializeSessionMap(CompanyUser user) {

        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        // Store the type of signed-in user
        sessionMap.put("type", "company");

        // Store the object reference of the signed-in user
        sessionMap.put("user", user);

        // Store the Name of the signed-in user
        sessionMap.put("name", user.getName());

        // Store the Username of the signed-in user
        sessionMap.put("username", username);

        // Store signed-in user's Primary Key in the database
        sessionMap.put("user_id", user.getId());
    }

    // Generates a six-character length string of random letters and numbers
    private void generateRandomCode() {
        Random random = new SecureRandom();
        char[] symbols = ALPHANUM.toCharArray();
        char[] code = new char[6];

        for (int i = 0; i < 6; i++) {
            code[i] = symbols[random.nextInt(symbols.length)];
        }
        generatedCode = new String(code);
    }

    private void setupTwoFactor(CompanyUser user) {
        this.user = user;

        // Generate New 2-factor code
        generateRandomCode();

        // Send email to user with code inside
        String subject = "Two-Factor Authentication Code for " + user.getName();
        String message = "Your Authentication Code is: " + generatedCode;
        sendEmail(user.getEmail(), subject, message);
    }

    // Send an email with the content given as parameters
    private void sendEmail(String address, String subject, String content) {
        Session emailSession;               // javax.mail.Session
        MimeMessage htmlEmailMessage;       // javax.mail.internet.MimeMessage

        // Set Email Server Properties
        Properties emailServerProperties = System.getProperties();
        emailServerProperties.put("mail.smtp.port", "587");
        emailServerProperties.put("mail.smtp.auth", "true");
        emailServerProperties.put("mail.smtp.starttls.enable", "true");

        try {
            // Create an email session using the email server properties set above
            emailSession = Session.getDefaultInstance(emailServerProperties, null);

            /*
            Create a Multi-purpose Internet Mail Extensions (MIME) style email
            message from the MimeMessage class under the email session created.
             */
            htmlEmailMessage = new MimeMessage(emailSession);

            // Set the email TO field to emailTo, which can contain only one email address
            htmlEmailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(address));

            // Set the email subject line
            htmlEmailMessage.setSubject(subject);

            // Set the email body to the HTML type text
            htmlEmailMessage.setContent(content, "text/html");

            // Create a transport object that implements the Simple Mail Transfer Protocol (SMTP)
            Transport transport = emailSession.getTransport("smtp");

            /*
            Connect to Gmail's SMTP server using the username and password provided.
            For the Gmail's SMTP server to accept the unsecure connection, the
            Cloud.Software.Email@gmail.com account's "Allow less secure apps" option is set to ON.
             */
            transport.connect("smtp.gmail.com", "Jobs4Geeks2021@gmail.com", "mlfeahmbpijxogsc");

            // Send the htmlEmailMessage created to the specified list of addresses (recipients)
            transport.sendMessage(htmlEmailMessage, htmlEmailMessage.getAllRecipients());

            // Close this service and terminate its connection
            transport.close();

        } catch (AddressException ae) {
            Methods.showMessage("Fatal Error", "Email Address Exception Occurred!",
                    "See: " + ae.getMessage());

        } catch (MessagingException me) {
            Methods.showMessage("Fatal Error",
                    "Email Messaging Exception Occurred! Internet Connection Required!",
                    "See: " + me.getMessage());
        }
    }

}

