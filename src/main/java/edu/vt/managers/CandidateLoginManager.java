/*
 * Created by Conner Owens on 2021.11.29
 * Copyright © 2021 Conner Owens. All rights reserved.
 */
package edu.vt.managers;

import edu.vt.EntityBeans.CandidateUser;
import edu.vt.FacadeBeans.CandidateUserFacade;
import edu.vt.globals.Methods;
import edu.vt.globals.Password;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Map;

@Named("candidateLoginManager")
@SessionScoped

public class CandidateLoginManager implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String username;
    private String password;

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    UserFacade bean into the instance variable 'userFacade' after it is instantiated at runtime.
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

    /*
    ================
    Instance Methods
    ================
    */

    /*
    *****************************************************
    Sign in the CandidateUser if the Entered Username and Password
    are Valid and Redirect to Show the Profile Page
    *****************************************************
     */
    public String loginUser() {

        // Since we will redirect to show the Profile page, invoke preserveMessages()
        Methods.preserveMessages();

        String enteredUsername = username;

        // Obtain the object reference of the CandidateUser object from the entered username
        CandidateUser user = candidateUserFacade.findByUsername(enteredUsername);

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

            // Verification Successful: Entered password = CandidateUser's actual password

            // Initialize the session map with user properties of interest in the method below
            initializeSessionMap(user);

            // Redirect to show the Profile page
            return "/CandidateAccount/CandidateProfile?faces-redirect=true";
        }
    }

    /*
    ******************************************************************
    Initialize the Session Map to Hold Session Attributes of Interests
    ******************************************************************
     */
    public void initializeSessionMap(CandidateUser user) {

        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        // Store the type of signed-in user
        sessionMap.put("type", "candidate");

        // Store the object reference of the signed-in user
        sessionMap.put("user", user);

        // Store the First Name of the signed-in user
        sessionMap.put("first_name", user.getFirstName());

        // Store the Last Name of the signed-in user
        sessionMap.put("last_name", user.getLastName());

        // Store the Username of the signed-in user
        sessionMap.put("username", username);

        // Store signed-in user's Primary Key in the database
        sessionMap.put("user_id", user.getId());
    }

}

