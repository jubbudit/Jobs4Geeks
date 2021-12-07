/*
 * Created by Conner Owens on 2021.11.29
 * Copyright © 2021 Conner Owens. All rights reserved.
 */
package edu.vt.EntityBeans;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/*
The @Entity annotation designates this class as a JPA Entity POJO class
representing the CandidateUser table in the CloudDriveDB database.
 */
@Entity

// Name of the database table represented
@Table(name = "CandidateUser")

@NamedQueries({
        @NamedQuery(name = "CandidateUser.findAll", query = "SELECT u FROM CandidateUser u")
        , @NamedQuery(name = "CandidateUser.findById", query = "SELECT u FROM CandidateUser u WHERE u.id = :id")
        , @NamedQuery(name = "CandidateUser.findByUsername", query = "SELECT u FROM CandidateUser u WHERE u.username = :username")
        , @NamedQuery(name = "CandidateUser.findByPassword", query = "SELECT u FROM CandidateUser u WHERE u.password = :password")
        , @NamedQuery(name = "CandidateUser.findByFirstName", query = "SELECT u FROM CandidateUser u WHERE u.firstName = :firstName")
        , @NamedQuery(name = "CandidateUser.findByLastName", query = "SELECT u FROM CandidateUser u WHERE u.lastName = :lastName")
        , @NamedQuery(name = "CandidateUser.findBySecurityQuestionNumber", query = "SELECT u FROM CandidateUser u WHERE u.securityQuestionNumber = :securityQuestionNumber")
        , @NamedQuery(name = "CandidateUser.findBySecurityAnswer", query = "SELECT u FROM CandidateUser u WHERE u.securityAnswer = :securityAnswer")
        , @NamedQuery(name = "CandidateUser.findByCurrentPosition", query = "SELECT u FROM CandidateUser u WHERE u.currentPosition = :currentPosition")
        , @NamedQuery(name = "CandidateUser.findByEmail", query = "SELECT u FROM CandidateUser u WHERE u.email = :email")})

public class CandidateUser implements Serializable {
    /*
    ========================================================
    Instance variables representing the attributes (columns)
    of the CandidateUser table in the Jobs4GeeksDB database.

	CREATE TABLE CandidateUser
    (
        id INT UNSIGNED NOT NULL AUTO_INCREMENT,
        first_name VARCHAR(128) NOT NULL,
        last_name VARCHAR(128) NOT NULL,
        username VARCHAR(128) NOT NULL,
        password VARCHAR(128) NOT NULL,
        security_question_number INT NOT NULL,    Refers to the number of the selected security question
        security_answer VARCHAR(128) NOT NULL,
        current_position VARCHAR(128) NOT NULL,
        email VARCHAR(255) NOT NULL,
        twoFactorEnabled BOOLEAN NOT NULL,
        PRIMARY KEY (id)
    );
    ========================================================
     */
    private static final long serialVersionUID = 1L;
    /*
    Primary Key id is auto generated by the system as an Integer value
    starting with 1 and incremented by 1, i.e., 1,2,3,...
    A deleted entity object's primary key number is not reused.
     */
    // id INT UNSIGNED NOT NULL AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    // username VARCHAR(32) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "username")
    private String username;

    // To store Salted and Hashed Password Parts
    // password VARCHAR(256) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "password")
    private String password;

    // first_name VARCHAR(32) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "first_name")
    private String firstName;

    // last_name VARCHAR(32) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "last_name")
    private String lastName;

    // security_question_number INT NOT NULL
    // Refers to the number of the selected security question
    @Basic(optional = false)
    @NotNull
    @Column(name = "security_question_number")
    private int securityQuestionNumber;

    // security_answer VARCHAR(128) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "security_answer")
    private String securityAnswer;

    // email VARCHAR(128) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "email")
    private String email;

    // current_position VARCHAR(128) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "current_position")
    private String currentPosition;

    // twoFactorEnabled BOOLEAN NOT NULL,
    @Basic(optional = false)
    @NotNull
    @Column(name = "two_factor_enabled")
    private Boolean twoFactorEnabled;

    /*
    ===============================================================
    Class constructors for instantiating a CandidateUser entity object to
    represent a row in the CandidateUser table in the CloudDriveDB database.
    ===============================================================
     */

    // Used in createAccount method in UserController
    public CandidateUser() {
    }

    // Not used but kept for potential future use
    public CandidateUser(Integer id) {
        this.id = id;
    }

    // Not used but kept for potential future use
    public CandidateUser(Integer id, String username, String password, String firstName, String lastName,
                         int securityQuestionNumber, String securityAnswer, String currentPosition, String email, Boolean twoFactorEnabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.securityQuestionNumber = securityQuestionNumber;
        this.securityAnswer = securityAnswer;
        this.currentPosition = currentPosition;
        this.email = email;
        this.twoFactorEnabled = twoFactorEnabled;
    }

    /*
    ======================================================
    Getter and Setter methods for the attributes (columns)
    of the CandidateUser table in the CloudDriveDB database.
    ======================================================
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public int getSecurityQuestionNumber() {
        return securityQuestionNumber;
    }

    public void setSecurityQuestionNumber(int securityQuestionNumber) {
        this.securityQuestionNumber = securityQuestionNumber;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
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

    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    /*
    ================================
    Instance Methods Used Internally
    ================================
     */

    // Generate and return a hash code value for the object with database primary key id
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /*
     Checks if the CandidateUser object identified by 'object' is the same as the CandidateUser object identified by 'id'
     Parameter object = CandidateUser object identified by 'object'
     Returns True if the CandidateUser 'object' and 'id' are the same; otherwise, return False
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CandidateUser)) {
            return false;
        }
        CandidateUser other = (CandidateUser) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    // Return String representation of database primary key id
    @Override
    public String toString() {
        return id.toString();
    }

    // Returns a true or false is the user has two-factor authentication enabled
    public Boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

}
