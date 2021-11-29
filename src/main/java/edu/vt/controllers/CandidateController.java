/*
 * Created by Conner Owens on 2021.11.29
 * Copyright © 2021 Conner Owens. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.CandidateUser;
import edu.vt.FacadeBeans.CandidateUserFacade;
import edu.vt.globals.Constants;
import edu.vt.globals.Methods;
import edu.vt.globals.Password;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Named("candidateController")
@SessionScoped
public class CandidateController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String username;
    private String password;
    private String confirmPassword;

    private String firstName;
    private String lastName;
    private String email;
    private String currentPosition;

    private int securityQuestionNumber;
    private String answerToSecurityQuestion;

    private Map<String, Object> security_questions;

    private CandidateUser selected;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getSecurityQuestionNumber() {
        return securityQuestionNumber;
    }

    public void setSecurityQuestionNumber(int securityQuestionNumber) {
        this.securityQuestionNumber = securityQuestionNumber;
    }

    public String getAnswerToSecurityQuestion() {
        return answerToSecurityQuestion;
    }

    public void setAnswerToSecurityQuestion(String answerToSecurityQuestion) {
        this.answerToSecurityQuestion = answerToSecurityQuestion;
    }

    public Map<String, Object> getSecurity_questions() {
        if (security_questions == null) {
            /*
            Difference between HashMap and LinkedHashMap:
            HashMap stores key-value pairings in no particular order.
                Values are retrieved based on their corresponding Keys.
            LinkedHashMap stores and retrieves key-value pairings
                in the order they were put into the map.
             */
            security_questions = new LinkedHashMap<>();

            for (int i = 0; i < Constants.QUESTIONS.length; i++) {
                security_questions.put(Constants.QUESTIONS[i], i);
            }
        }
        return security_questions;
    }

    public void setSecurity_questions(Map<String, Object> security_questions) {
        this.security_questions = security_questions;
    }

    public CandidateUser getSelected() {
        if (selected == null) {
            // Store the object reference of the signed-in CandidateUser into the instance variable selected.
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            selected = (CandidateUser) sessionMap.get("user");
        }
        // Return the object reference of the selected (i.e., signed-in) CandidateUser object
        return selected;
    }

    public void setSelected(CandidateUser selected) {
        this.selected = selected;
    }


    /*
    ================
    Instance Methods
    ================
     */

    /*
    **********************************
    Return True if a CandidateUser is Signed In
    **********************************
     */
    public boolean isLoggedIn() {
        /*
        The username of a signed-in user is put into the SessionMap in the
        initializeSessionMap() method in LoginManager upon user's sign in.
        If there is a username, that means, there is a signed-in user.
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return sessionMap.get("username") != null;
    }

    /*
    *******************************************************************
    Create CandidateUser's Account and Redirect to Show the SignIn Page
    *******************************************************************
     */
    public String createAccount() {

        /*
        ----------------------------------------------------------------
        Password and Confirm Password are validated under 3 tests:

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

        //--------------------------------------------
        // Password and Confirm Password are Validated
        //--------------------------------------------

        /*
        Redirecting to show a JSF page involves more than one subsequent requests and
        the messages would die from one request to another if not kept in the Flash scope.
        Since we will redirect to show the SignIn page, we invoke preserveMessages().
         */
        Methods.preserveMessages();

        //-----------------------------------------------------------
        // First, check if the entered username is already being used
        //-----------------------------------------------------------
        // Obtain the object reference of a CandidateUser object with the username entered by the user
        CandidateUser aUser = candidateUserFacade.findByUsername(username);

        if (aUser != null) {
            // A user already exists with the username entered by the user
            username = "";
            Methods.showMessage("Fatal Error",
                    "Username Already Exists!",
                    "Please Select a Different One!");
            return "";
        }

        //----------------------------------
        // The entered username is available
        //----------------------------------
        try {
            // Instantiate a new CandidateUser object
            CandidateUser newUser = new CandidateUser();

            /*
             Set the properties of the newly created newUser object with the values
             entered by the user in the AccountCreationForm in CreateAccount.xhtml
             */
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setSecurityQuestionNumber(securityQuestionNumber);
            newUser.setSecurityAnswer(answerToSecurityQuestion);
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setCurrentPosition(currentPosition);

            //-------------------------------------------------------------------------------------
            // Convert the user-entered String password to a String containing the following parts
            //       "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
            // for secure storage and retrieval with Key Stretching to prevent brute-force attacks.
            //-------------------------------------------------------------------------------------
            String parts = Password.createHash(password);
            newUser.setPassword(parts);

            // Create the user in the database
            candidateUserFacade.create(newUser);

        } catch (EJBException | Password.CannotPerformOperationException ex) {
            username = "";
            Methods.showMessage("Fatal Error",
                    "Something went wrong while creating user's account!",
                    "See: " + ex.getMessage());
            return "";
        }

        Methods.showMessage("Information", "Success!",
                "Candidate Account is Successfully Created!");

        /*
         The Profile page cannot be shown since the new CandidateUser has not signed in yet.
         Therefore, we show the Sign In page for the new CandidateUser to sign in first.
         */
        return "/CandidateAccount/CandidateSignIn.xhtml?faces-redirect=true";
    }
}
