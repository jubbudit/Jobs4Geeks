/*
 * Created by Conner Owens on 2021.11.29
 * Copyright © 2021 Conner Owens. All rights reserved.
 */
package edu.vt.globals;

public final class Constants {

    /* FILE STRUCTURE

       DocRoot
       |
       | - > Jobs4GeeksStorage
             |
             | - > CandidatePhotoStorage
             |
             | - > CompanyPhotoStorage
             |
             | - > CandidateFileStorage

     */

    //-------------------------
    // To run locally (Windows)
    //-------------------------
    public static final String USER_PHOTOS_ABSOLUTE_PATH = "C:/Users/conne/DocRoot/Jobs4GeeksStorage/CandidatePhotoStorage/";
    public static final String USER_PHOTOS_URI = "http://localhost:8080/candidatephotos/";
    public static final String USER_FILES_ABSOLUTE_PATH = "C:/Users/conne/DocRoot/Jobs4GeeksStorage/CandidateFileStorage/";
    public static final String USER_FILES_URI = "http://localhost:8080/candidatefiles/";
    public static final String COMPANY_PHOTOS_ABSOLUTE_PATH = "C:/Users/conne/DocRoot/Jobs4GeeksStorage/CompanyPhotoStorage/";
    public static final String COMPANY_PHOTOS_URI = "http://localhost:8080/companyphotos/";

    //--------------------------------
    // To run on your AWS EC2 instance
    //--------------------------------
    //    public static final String PHOTOS_ABSOLUTE_PATH = "/opt/wildfly/DocRoot/SurveyUserPhotoStorage/";
    //    public static final String PHOTOS_URI = "http://54.84.71.63:8080/userphotos/";

    /*
     A security question is selected and answered by the user at the time of account creation.
     The selected question/answer is used as a second level of authentication for
     (a) resetting user's password, and (b) deleting user's account.
     */
    public static final String[] QUESTIONS = {
            "In what city or town did your mother and father meet?",
            "In what city or town were you born?",
            "What did you want to be when you grew up?",
            "What do you remember most from your childhood?",
            "What is the name of the boy or girl that you first kissed?",
            "What is the name of the first school you attended?",
            "What is the name of your favorite childhood friend?",
            "What is the name of your first pet?",
            "What is your mother's maiden name?",
            "What was your favorite place to visit as a child?"
    };

    public static final Integer THUMBNAIL_SIZE = 200;
}
