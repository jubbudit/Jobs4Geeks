/*
 * Created by Conner Owens on 2021.11.5
 * Copyright © 2021 Conner Owens. All rights reserved.
 */
package edu.vt.managers;

import edu.vt.EntityBeans.CandidateUser;
import edu.vt.FacadeBeans.CandidateUserFacade;
import edu.vt.globals.Constants;
import edu.vt.globals.Methods;
import edu.vt.globals.Password;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named("candidatePasswordResetManager")
@SessionScoped

public class CandidatePasswordResetManager implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String username;
    private String password;
    private String confirmPassword;
    private String answerToSecurityQuestion;

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    UserFacade bean into the instance variable 'candidateUserFacade' after it is instantiated at runtime.
     */
    @EJB
    private CandidateUserFacade candidateUserFacade;

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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getAnswerToSecurityQuestion() {
        return answerToSecurityQuestion;
    }

    public void setAnswerToSecurityQuestion(String answerToSecurityQuestion) {
        this.answerToSecurityQuestion = answerToSecurityQuestion;
    }

    /*
    ================
    Instance Methods
    ================

    *************************************************
    Process the Username Submitted for Password Reset
    *************************************************
     */
    public String usernameSubmit() {

        // Since we will redirect to show the SecurityQuestion page, invoke preserveMessages()
        Methods.preserveMessages();

        // Obtain the object reference of the CandidateUser object with username
        CandidateUser user = candidateUserFacade.findByUsername(username);

        if (user == null) {
            Methods.showMessage("Fatal Error", "Unknown Username!",
                    "Entered username " + username + " does not exist!");
            return "";
        } else {
            // Redirect to show the SecurityQuestion page
            return "/CandidateAccount/PasswordChange/SecurityQuestion?faces-redirect=true";
        }
    }

    /*
    ****************************************
    Return the Security Question Selected by
    the CandidateUser at the Time of Account Creation
    ****************************************
     */
    public String getSelectedSecurityQuestionForUsername() {

        // Obtain the object reference of the CandidateUser object with username
        CandidateUser user = candidateUserFacade.findByUsername(username);

        // Obtain the number of the security question selected by the user
        int questionNumber = user.getSecurityQuestionNumber();

        // Return the security question corresponding to the question number
        return Constants.QUESTIONS[questionNumber];
    }

    /*
    *****************************************************
    Process the Submitted Answer to the Security Question
    *****************************************************
     */
    public String securityAnswerSubmit() {

        // Since we will redirect to show the ResetPassword page, invoke preserveMessages()
        Methods.preserveMessages();

        // Obtain the object reference of the CandidateUser object with username
        CandidateUser user = candidateUserFacade.findByUsername(username);

        String actualSecurityAnswer = user.getSecurityAnswer();
        String enteredSecurityAnswer = getAnswerToSecurityQuestion();

        if (actualSecurityAnswer.equals(enteredSecurityAnswer)) {
            /*
            Answer to the security question is correct. Redirect to show the ResetPassword page.
            */
            return "/CandidateAccount/PasswordChange/ResetPassword?faces-redirect=true";

        } else {
            Methods.showMessage("Error",
                    "Answer to the Security Question is Incorrect!", "");
            return "";
        }
    }

    /*
    *************************************************
    Reset Password and Redirect to Show the Home Page
    *************************************************
     */
    public String resetPassword() {
        /*
        ----------------------------------------------------------------
        Password and Confirm Password are validated under 3 conditions:

        <1> Non-empty (tested with required="true" in JSF page)
        <2> Correct composition satisfying the regex rule.
            (tested with <f:validator validatorId="passwordValidator" />
            in the JSF page)
        <3> Password and Confirm Password must match (tested below)
        ----------------------------------------------------------------
         */
        if (!password.equals(confirmPassword)) {
            Methods.showMessage("Fatal Error", "Unmatched Passwords!",
                    "Password and Confirm Password must Match!");
            return "";
        }

        /*
        ***************************************************
        |   Password and Confirm Password are Validated   |
        ***************************************************
        */
        // Since we will redirect to show the home page, invoke preserveMessages()
        Methods.preserveMessages();

        // Obtain the object reference of the CandidateUser object with username
        CandidateUser user = candidateUserFacade.findByUsername(username);

        try {
            //-------------------------------------------------------------------------------------
            // Convert the user-entered String password to a String containing the following parts
            //       "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
            // for secure storage and retrieval with Key Stretching to prevent brute-force attacks.
            //-------------------------------------------------------------------------------------
            String parts = Password.createHash(password);

            // Reset CandidateUser object's password
            user.setPassword(parts);

            // Update the database
            candidateUserFacade.edit(user);

            // Initialize the instance variables
            username = password = confirmPassword = answerToSecurityQuestion = "";

            Methods.showMessage("Information", "Success!",
                    "Your Password has been Reset!");

            // Redirect to show the index (home) page
            return "/CandidateAccount/CandidateSignIn?faces-redirect=true";

        } catch (EJBException | Password.CannotPerformOperationException ex) {
            Methods.showMessage("Fatal Error",
                    "Something went wrong while resetting your password!",
                    "See: " + ex.getMessage());
        }
        return "";
    }

}
