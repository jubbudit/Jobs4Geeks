/*
 * Created by Conner Owens on 2021.11.29
 * Copyright © 2021 Conner Owens. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.CandidateUser;
import edu.vt.EntityBeans.UserPhoto;
import edu.vt.FacadeBeans.CandidateUserFacade;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
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

    @EJB
    private UserPhotoFacade userPhotoFacade;


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
        return (sessionMap.get("username") != null && sessionMap.get("type").equals("candidate"));
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

    /*
    ***********************************************************
    Update CandidateUser's Account and Redirect to Show the Profile Page
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
        CandidateUser editUser = (CandidateUser) sessionMap.get("user");

        try {
            /*
             Set the signed-in user's properties to the values entered by
             the user in the EditAccountProfileForm in EditAccount.xhtml.
             */
            editUser.setFirstName(this.selected.getFirstName());
            editUser.setLastName(this.selected.getLastName());
            editUser.setCurrentPosition(this.selected.getCurrentPosition());
            editUser.setEmail(this.selected.getEmail());

            // Store the changes in the database
            candidateUserFacade.edit(editUser);

            Methods.showMessage("Information", "Success!",
                    "CandidateUser's Account is Successfully Updated!");

        } catch (EJBException ex) {
            username = "";
            Methods.showMessage("Fatal Error",
                    "Something went wrong while updating user's profile!",
                    "See: " + ex.getMessage());
            return "";
        }

        // Account update is completed, redirect to show the Profile page.
        return "/CandidateAccount/CandidateProfile?faces-redirect=true";
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
            deleteAllUserPhotos(userPrimaryKey);

            // Delete the CandidateUser entity, whose primary key is user_id, from the database
            candidateUserFacade.deleteUser(userPrimaryKey);

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

    /*
    **********************************************
    Logout CandidateUser and Redirect to Show the Home Page
    **********************************************
     */
    public void logout() {

        // Clear the signed-in CandidateUser's session map
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.clear();

        // Reset the signed-in CandidateUser's properties
        username = password = confirmPassword = "";
        firstName = lastName = currentPosition = "";
        securityQuestionNumber = 0;
        answerToSecurityQuestion = email = "";
        selected = null;

        // Since we will redirect to show the home page, invoke preserveMessages()
        Methods.preserveMessages();

        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

            // Invalidate the signed-in CandidateUser's session
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

    /*
    ****************************************
    Return the Security Question Selected by
    the CandidateUser at the Time of Account Creation
    ****************************************
     */
    public String getSelectedSecurityQuestion() {
        /*
        'user', the object reference of the signed-in user, was put into the SessionMap
        in the initializeSessionMap() method in LoginManager upon user's sign in.
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        CandidateUser signedInUser = (CandidateUser) sessionMap.get("user");

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
        CandidateUser signedInUser = (CandidateUser) sessionMap.get("user");

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
    *********************************************************
    Return Signed-In CandidateUser's Thumbnail Photo Relative Filepath
    *********************************************************
     */
    public String userPhoto() {

        /*
        The database primary key of the signed-in CandidateUser object was put into the SessionMap
        in the initializeSessionMap() method in LoginManager upon user's sign in.
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Integer primaryKey = (Integer) sessionMap.get("user_id");

        List<UserPhoto> photoList = userPhotoFacade.findPhotosByUserPrimaryKey(primaryKey);

        if (photoList.isEmpty()) {
            // No user photo exists. Return defaultUserPhoto.png from the BevqPhotoStorage directory.
            return Constants.USER_PHOTOS_URI + "defaultUserPhoto.png";
        }

        /*
        photoList.get(0) returns the object reference of the first Photo object in the list.
        getThumbnailFileName() message is sent to that Photo object to retrieve its
        thumbnail image file name, e.g., 5_thumbnail.jpeg
         */
        String thumbnailFileName = photoList.get(0).getThumbnailFileName();

        return Constants.USER_PHOTOS_URI + thumbnailFileName;
    }

    /*
    *********************************************************
    Delete the photo and thumbnail image files that belong to
    the CandidateUser object whose database primary key is primaryKey
    *********************************************************
     */
    public void deleteAllUserPhotos(int primaryKey) {

        /*
        Obtain the list of Photo objects that belong to the CandidateUser whose
        database primary key is userId.
         */
        List<UserPhoto> photoList = userPhotoFacade.findPhotosByUserPrimaryKey(primaryKey);

        // photoList.isEmpty implies that the user has never uploaded a photo file
        if (!photoList.isEmpty()) {

            // Obtain the object reference of the first Photo object in the list.
            UserPhoto photo = photoList.get(0);
            try {
                /*
                NOTE: Files and Paths are imported as
                        import java.nio.file.Files;
                        import java.nio.file.Paths;

                 Delete the user's photo if it exists.
                 getFilePath() is given in UserPhoto.java file.
                 */
                Files.deleteIfExists(Paths.get(photo.getPhotoFilePath()));

                /*
                 Delete the user's thumbnail image if it exists.
                 getThumbnailFilePath() is given in UserPhoto.java file.
                 */
                Files.deleteIfExists(Paths.get(photo.getThumbnailFilePath()));

                // Delete the photo file record from the database.
                // UserPhotoFacade inherits the remove() method from AbstractFacade.
                userPhotoFacade.remove(photo);

                /*
                 Delete the user's captured photo file if it exists.
                 The file is named "user's primary key_tempFile".
                 */
                String capturedPhotoFilepath = Constants.USER_PHOTOS_ABSOLUTE_PATH + primaryKey + "_tempFile";
                Files.deleteIfExists(Paths.get(capturedPhotoFilepath));

            } catch (IOException ex) {
                Methods.showMessage("Fatal Error",
                        "Something went wrong while deleting user's photo!",
                        "See: " + ex.getMessage());
            }
        }
    }
}
