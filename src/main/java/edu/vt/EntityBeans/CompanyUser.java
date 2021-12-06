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
representing the CompanyUser table in the CloudDriveDB database.
 */
@Entity

// Name of the database table represented
@Table(name = "CompanyUser")

@NamedQueries({
        @NamedQuery(name = "CompanyUser.findAll", query = "SELECT u FROM CompanyUser u")
        , @NamedQuery(name = "CompanyUser.findById", query = "SELECT u FROM CompanyUser u WHERE u.id = :id")
        , @NamedQuery(name = "CompanyUser.findByName", query = "SELECT u FROM CompanyUser u WHERE u.name = :name")
        , @NamedQuery(name = "CompanyUser.findByUsername", query = "SELECT u FROM CompanyUser u WHERE u.username = :username")
        , @NamedQuery(name = "CompanyUser.findByPassword", query = "SELECT u FROM CompanyUser u WHERE u.password = :password")
        , @NamedQuery(name = "CompanyUser.findByEmail", query = "SELECT u FROM CompanyUser u WHERE u.email = :email")
        , @NamedQuery(name = "CompanyUser.findBySecurityQuestionNumber", query = "SELECT u FROM CompanyUser u WHERE u.securityQuestionNumber = :securityQuestionNumber")
        , @NamedQuery(name = "CompanyUser.findBySecurityAnswer", query = "SELECT u FROM CompanyUser u WHERE u.securityAnswer = :securityAnswer")
        , @NamedQuery(name = "CompanyUser.findByDescription", query = "SELECT u FROM CompanyUser u WHERE u.description = :description")
        , @NamedQuery(name = "CompanyUser.findByHomeURL", query = "SELECT u FROM CompanyUser u WHERE u.homeURL = :homeURL")
})

public class CompanyUser implements Serializable {
    /*
    ========================================================
    Instance variables representing the attributes (columns)
    of the CompanyUser table in the Jobs4GeeksDB database.

	CREATE TABLE CompanyUser
    (
        id INT UNSIGNED NOT NULL AUTO_INCREMENT,
        name VARCHAR(255) NOT NULL,
        username VARCHAR(128) NOT NULL,
        password VARCHAR(128) NOT NULL,
        email VARCHAR(128) NOT NULL,
        security_question_number INT NOT NULL,   /* Refers to the number of the selected security question
        security_answer VARCHAR(128) NOT NULL,
        description VARCHAR(1024) NOT NULL,
        home_url VARCHAR(255) NOT NULL,		/* URL of the company home page
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

    // username VARCHAR(128) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "username")
    private String username;

    // name VARCHAR(255) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;

    // To store Salted and Hashed Password Parts
    // password VARCHAR(128) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "password")
    private String password;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "email")
    private String email;

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

    // description VARCHAR(1024) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1024)
    @Column(name = "description")
    private String description;

    // home_url VARCHAR(255) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "home_url")
    private String homeURL;

    /*
    ===============================================================
    Class constructors for instantiating a CompanyUser entity object to
    represent a row in the CompanyUser table in the CloudDriveDB database.
    ===============================================================
     */

    // Used in createAccount method in UserController
    public CompanyUser() { }

    // Not used but kept for potential future use
    public CompanyUser(Integer id) {
        this.id = id;
    }

    // Not used but kept for potential future use
    public CompanyUser(Integer id, String name, String username, String password, String email, int securityQuestionNumber,
                       String securityAnswer, String description, String homeURL) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.securityQuestionNumber = securityQuestionNumber;
        this.securityAnswer = securityAnswer;
        this.description = description;
        this.homeURL = homeURL;
    }

    /*
    ======================================================
    Getter and Setter methods for the attributes (columns)
    of the CompanyUser table in the CloudDriveDB database.
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

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHomeURL() {
        return homeURL;
    }

    public void setHomeURL(String homeURL) {
        this.homeURL = homeURL;
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
     Checks if the CompanyUser object identified by 'object' is the same as the CompanyUser object identified by 'id'
     Parameter object = CompanyUser object identified by 'object'
     Returns True if the CompanyUser 'object' and 'id' are the same; otherwise, return False
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CompanyUser)) {
            return false;
        }
        CompanyUser other = (CompanyUser) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    // Return String representation of database primary key id
    @Override
    public String toString() {
        return id.toString();
    }

}