/*
 * Created by Conner Owens on 2021.11.29
 * Copyright © 2021 Conner Owens. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.*;
import edu.vt.EntityBeans.CompanyUser;
import edu.vt.FacadeBeans.CandidateUserFacade;
import edu.vt.FacadeBeans.CompanyPhotoFacade;
import edu.vt.FacadeBeans.CompanyUserFacade;
import edu.vt.FacadeBeans.UserPhotoFacade;
import edu.vt.globals.Constants;
import edu.vt.globals.Methods;
import edu.vt.globals.Password;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
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
    private String email;
    private String description;

    private int securityQuestionNumber;
    private String answerToSecurityQuestion;
    private Boolean twoFactorEnabled;

    private Map<String, Object> security_questions;

    private CompanyUser selected;

    @EJB
    private CompanyUserFacade companyUserFacade;

    @EJB
    private CandidateUserFacade candidateUserFacade;

    @EJB
    private UserPhotoFacade userPhotoFacade;
    @EJB
    private CompanyPhotoFacade companyPhotoFacade;


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

    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    /*
        ****************************************
        Return the Security Question Selected by
        the CompanyUser at the Time of Account Creation
        ****************************************
         */
    public String getSelectedSecurityQuestion() {
        /*
        'user', the object reference of the signed-in user, was put into the SessionMap
        in the initializeSessionMap() method in LoginManager upon user's sign in.
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        CompanyUser signedInUser = (CompanyUser) sessionMap.get("user");

        // Obtain the number of the security question selected by the user
        int questionNumber = signedInUser.getSecurityQuestionNumber();

        // Return the security question corresponding to the question number
        return Constants.QUESTIONS[questionNumber];
    }

    /*
    *********************************************
    Process Submitted Answer to Security Question
    *********************************************
     */
    public void securityAnswerSubmit() {
        /*
        'user', the object reference of the signed-in user, was put into the SessionMap
        in the initializeSessionMap() method in LoginManager upon user's sign in.
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        CompanyUser signedInUser = (CompanyUser) sessionMap.get("user");

        String actualSecurityAnswer = signedInUser.getSecurityAnswer();

        // getAnswerToSecurityQuestion() is the Getter method for the property 'answerToSecurityQuestion'
        String enteredSecurityAnswer = getAnswerToSecurityQuestion();

        if (actualSecurityAnswer.equals(enteredSecurityAnswer)) {
            // Answer to the security question is correct; Delete the user's account.
            // deleteAccount() method is given below.
            deleteAccount();
        } else {
            Methods.showMessage("Error",
                    "Answer to the Security Question is Incorrect!", "");
        }
    }

    /*
  *****************************************************************
  Delete CandidateUser's Account, Logout, and Redirect to Show the Home Page
  *****************************************************************
   */
    public void deleteAccount() {
        Methods.preserveMessages();
        /*
        The database primary key of the signed-in CandidateUser object was put into the SessionMap
        in the initializeSessionMap() method in LoginManager upon user's sign in.
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        int userPrimaryKey = (int) sessionMap.get("user_id");

        try {
            // Delete all of the photo files associated with the signed-in user whose primary key is userPrimaryKey
            //deleteAllUserPhotos(userPrimaryKey);

            // Delete the CandidateUser entity, whose primary key is user_id, from the database
            companyUserFacade.deleteUser(userPrimaryKey);

            Methods.showMessage("Information", "Success!",
                    "Your Account is Successfully Deleted!");

        } catch (EJBException ex) {
            username = "";
            Methods.showMessage("Fatal Error",
                    "Something went wrong while deleting user's account!",
                    "See: " + ex.getMessage());
            return;
        }

        // Execute the logout() method given below
        logout();
    }

    public void setSecurity_questions(Map<String, Object> security_questions) {
        this.security_questions = security_questions;
    }

    public CompanyUser getSelected() {
        if (selected == null) {
            // Store the object reference of the signed-in CompanyUser into the instance variable selected.
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

            if(!(sessionMap.get("user") instanceof CompanyUser)){
                selected = new CompanyUser();
            }
            else
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

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
    /*
    ================
    Instance Methods
    ================
     */

    public List<CompanyUser> getAllCompanies() {
        return companyUserFacade.findAll();
    }

    public String getPhoto(CompanyUser companyUser){
        List<CompanyPhoto> photoList = companyPhotoFacade.findPhotosByUserPrimaryKey(companyUser.getId());
        if (photoList.isEmpty()) {
            // No user photo exists. Return defaultUserPhoto.png.
            return Constants.COMPANY_PHOTOS_URI + "defaultUserPhoto.png";
        }
        String thumbnailFileName = photoList.get(0).getThumbnailFileName();

        return Constants.COMPANY_PHOTOS_URI + thumbnailFileName;
    }

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
            newUser.setEmail(email);
            newUser.setTwoFactorEnabled(twoFactorEnabled);

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

    /*
   ***********************************************************
   Update CompanyUser's Account and Redirect to Show the Profile Page
   ***********************************************************
    */
    public String updateAccount() {

        // Since we will redirect to show the Profile page, invoke preserveMessages()
        Methods.preserveMessages();

        /*
        'user', the object reference of the signed-in user, was put into the SessionMap
        in the initializeSessionMap() method in LoginManager upon user's sign in.
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        CompanyUser editUser = (CompanyUser) sessionMap.get("user");

        try {
            /*
             Set the signed-in user's properties to the values entered by
             the user in the EditAccountProfileForm in EditAccount.xhtml.
             */
            editUser.setName(this.selected.getName());
            editUser.setDescription(this.selected.getDescription());
            editUser.setHomeURL(this.selected.getHomeURL());
            editUser.setEmail(this.selected.getEmail());
            editUser.setTwoFactorEnabled(this.selected.isTwoFactorEnabled());

            // Store the changes in the database
            companyUserFacade.edit(editUser);

            Methods.showMessage("Information", "Success!",
                    "CompanyUser's Account is Successfully Updated!");

        } catch (EJBException ex) {
            username = "";
            Methods.showMessage("Fatal Error",
                    "Something went wrong while updating user's profile!",
                    "See: " + ex.getMessage());
            return "";
        }

        // Account update is completed, redirect to show the Profile page.
        return "/CompanyAccount/CompanyProfile?faces-redirect=true";
    }

    /*
    *********************************************************
    Return Signed-In CompanyUser's Thumbnail Photo Relative Filepath
    *********************************************************
     */
    public String userPhoto() {

        /*
        The database primary key of the signed-in CandidateUser object was put into the SessionMap
        in the initializeSessionMap() method in LoginManager upon user's sign in.
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Integer primaryKey = (Integer) sessionMap.get("user_id");

        List<CompanyPhoto> photoList = companyPhotoFacade.findPhotosByUserPrimaryKey(primaryKey);

        if (photoList.isEmpty()) {
            // No user photo exists. Return defaultUserPhoto.png from the BevqPhotoStorage directory.
            System.out.println("EMPTY PHOTO LIST");
            return Constants.COMPANY_PHOTOS_URI + "defaultUserPhoto.png";
        }

        /*
        photoList.get(0) returns the object reference of the first Photo object in the list.
        getThumbnailFileName() message is sent to that Photo object to retrieve its
        thumbnail image file name, e.g., 5_thumbnail.jpeg
         */
        String thumbnailFileName = photoList.get(0).getThumbnailFileName();

        System.out.println(Constants.COMPANY_PHOTOS_URI + thumbnailFileName);
        return Constants.COMPANY_PHOTOS_URI + thumbnailFileName;
    }

    /*
    **********************************************
    Logout User and Redirect to Show the Home Page
    **********************************************
     */
    public void logout() {

        // Clear the signed-in User's session map
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.clear();

        // Reset the signed-in User's properties
        username = password = confirmPassword = "";
        name = description = "";
        securityQuestionNumber = 0;
        answerToSecurityQuestion = homeURL = "";
        selected = null;

        // Since we will redirect to show the home page, invoke preserveMessages()
        Methods.preserveMessages();

        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

            // Invalidate the signed-in User's session
            externalContext.invalidateSession();

            /*
            getRequestContextPath() returns the URI of the Web Pages directory of the application.
            Obtain the URI of the index (home) page to redirect to.
             */
            String redirectPageURI = externalContext.getRequestContextPath() + "/index.xhtml";

            // Redirect to show the index (home) page
            externalContext.redirect(redirectPageURI);

            /*
            NOTE: We cannot use: return "/index?faces-redirect=true";
            here because the user's session is invalidated.
             */
        } catch (IOException ex) {
            Methods.showMessage("Fatal Error",
                    "Unable to redirect to the index (home) page!",
                    "See: " + ex.getMessage());
        }
    }
}