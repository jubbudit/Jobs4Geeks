package edu.vt.EntityBeans;

import edu.vt.globals.Constants;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/*
The @Entity annotation designates this class as a JPA Entity POJO class
representing the CompanyPhoto table in the CloudDriveDB database.
 */
@Entity

// Name of the database table represented
@Table(name = "CompanyPhoto")

@NamedQueries({
    /*
    private CompanyUser companyId;    --> companyId is the object reference of the CompanyUser object.
    companyId.id               --> CompanyUser object's database primary key
     */
        @NamedQuery(name = "CompanyPhoto.findPhotosByUserDatabasePrimaryKey", query = "SELECT p FROM CompanyPhoto p WHERE p.companyId.id = :primaryKey")
        , @NamedQuery(name = "CompanyPhoto.findAll", query = "SELECT u FROM CompanyPhoto u")
        , @NamedQuery(name = "CompanyPhoto.findById", query = "SELECT u FROM CompanyPhoto u WHERE u.id = :id")
        , @NamedQuery(name = "CompanyPhoto.findByExtension", query = "SELECT u FROM CompanyPhoto u WHERE u.extension = :extension")
})

public class CompanyPhoto implements Serializable {
    /*
    ========================================================
    Instance variables representing the attributes (columns)
    of the CompanyPhoto table in the CloudDriveDB database.

    CREATE TABLE CompanyPhoto
    (
           id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
           extension ENUM('jpeg', 'jpg', 'png', 'gif') NOT NULL,
           company_id INT UNSIGNED,
           FOREIGN KEY (company_id) REFERENCES CompanyUser(id) ON DELETE CASCADE
    );
    ========================================================
     */
    private static final long serialVersionUID = 1L;

    // id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    // extension ENUM('jpeg', 'jpg', 'png', 'gif') NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "extension")
    private String extension;

    // company_id INT UNSIGNED
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    @ManyToOne
    private CompanyUser companyId;

    /*
    =================================================================
    Class constructors for instantiating a CompanyPhoto entity object to
    represent a row in the CompanyPhoto table in the CloudDriveDB database.
    =================================================================
     */
    public CompanyPhoto() {
    }

    // Called from CompanyPhotoController
    public CompanyPhoto(String fileExtension, CompanyUser id) {
        this.extension = fileExtension;
        companyId = id;
    }

    /*
    ======================================================
    Getter and Setter methods for the attributes (columns)
    of the CompanyPhoto table in the CloudDriveDB database.
    ======================================================
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExtension() {
        return extension;
    }

    public CompanyUser getCompanyId() {
        return companyId;
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
     Checks if the CompanyPhoto object identified by 'object' is the same as the CompanyPhoto object identified by 'id'
     Parameter object = CompanyPhoto object identified by 'object'
     Returns True if the CompanyPhoto 'object' and 'id' are the same; otherwise, return False
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CompanyPhoto)) {
            return false;
        }
        CompanyPhoto other = (CompanyPhoto) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    // Return String representation of database primary key id
    @Override
    public String toString() {
        return id.toString();
    }

    /*
    =============
    Other Methods
    =============
     */

    /*
    CompanyUser's photo image file is named as "companyId.fileExtension", e.g., 5.jpg for user with id 5.
    Since the user can have only one photo, this makes sense.
     */
    public String getPhotoFilename() {
        return getCompanyId() + "." + getExtension();
    }

    /*
    --------------------------------------------------------------------------------
    The thumbnail photo image size is set to 200x200px in Constants.java as follows:
    public static final Integer THUMBNAIL_SIZE = 200;

    If the user uploads a large photo file, we will scale it down to THUMBNAIL_SIZE
    and use it so that the size is reasonable for performance reasons.

    The photo image scaling is properly done by using the imgscalr-lib-4.2.jar file.

    The thumbnail file is named as "companyId_thumbnail.fileExtension",
    e.g., 5_thumbnail.jpg for user with id 5.
    --------------------------------------------------------------------------------
     */
    public String getThumbnailFileName() {
        return getCompanyId() + "_thumbnail." + getExtension();
    }

    public String getPhotoFilePath() {
        return Constants.COMPANY_PHOTOS_ABSOLUTE_PATH + getPhotoFilename();
    }

    public String getThumbnailFilePath() {
        return Constants.COMPANY_PHOTOS_ABSOLUTE_PATH + getThumbnailFileName();
    }
    
}
