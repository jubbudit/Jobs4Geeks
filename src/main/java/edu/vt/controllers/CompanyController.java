/*
 * Created by Conner Owens on 2021.11.29
 * Copyright © 2021 Conner Owens. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.CompanyUser;
import edu.vt.FacadeBeans.CompanyUserFacade;
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

@Named("companyController")
@SessionScoped
public class CompanyController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String username;
    private String password;
    private String confirmPassword;

    private String name;
    private String homeURL;
    private String description;

    private int securityQuestionNumber;
    private String answerToSecurityQuestion;

    private Map<String, Object> security_questions;

    private CompanyUser selected;

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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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

    public CompanyUser getSelected() {
        if (selected == null) {
            // Store the object reference of the signed-in CompanyUser into the instance variable selected.
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            selected = (CompanyUser) sessionMap.get("user");
        }
        // Return the object reference of the selected (i.e., signed-in) CompanyUser object
        return selected;
    }

    public void setSelected(CompanyUser selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHomeURL() {
        return homeURL;
    }

    public void setHomeURL(String homeURL) {
        this.homeURL = homeURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    /*
    ================
    Instance Methods
    ================
     */

    /*
    **********************************
    Return True if a CompanyUser is Signed In
    **********************************
     */
    public boolean isLoggedIn() {
        /*
        The username of a signed-in user is put into the SessionMap in the
        initializeSessionMap() method in LoginManager upon user's sign in.
        If there is a username, that means, there is a signed-in user.
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return (sessionMap.get("username") != null && sessionMap.get("type").equals("company"));
    }

    /*
    *******************************************************************
    Create CompanyUser's Account and Redirect to Show the SignIn Page
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
        // Obtain the object reference of a CompanyUser object with the username entered by the user
        CompanyUser aUser = companyUserFacade.findByUsername(username);

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
            // Instantiate a new CompanyUser object
            CompanyUser newUser = new CompanyUser();

            /*
             Set the properties of the newly created newUser object with the values
             entered by the user in the AccountCreationForm in CreateAccount.xhtml
             */
            newUser.setName(name);
            newUser.setHomeURL(homeURL);
            newUser.setSecurityQuestionNumber(securityQuestionNumber);
            newUser.setSecurityAnswer(answerToSecurityQuestion);
            newUser.setDescription(description);
            newUser.setUsername(username);

            //-------------------------------------------------------------------------------------
            // Convert the user-entered String password to a String containing the following parts
            //       "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
            // for secure storage and retrieval with Key Stretching to prevent brute-force attacks.
            //-------------------------------------------------------------------------------------
            String parts = Password.createHash(password);
            newUser.setPassword(parts);

            // Create the user in the database
            companyUserFacade.create(newUser);

        } catch (EJBException | Password.CannotPerformOperationException ex) {
            username = "";
            Methods.showMessage("Fatal Error",
                    "Something went wrong while creating user's account!",
                    "See: " + ex.getMessage());
            return "";
        }

        Methods.showMessage("Information", "Success!",
                "Company Account is Successfully Created!");

        /*
         The Profile page cannot be shown since the new CompanyUser has not signed in yet.
         Therefore, we show the Sign In page for the new CompanyUser to sign in first.
         */
        return "/CompanyAccount/CompanySignIn.xhtml?faces-redirect=true";
    }
}