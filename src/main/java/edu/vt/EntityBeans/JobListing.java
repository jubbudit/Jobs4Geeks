/*
 * Created by Ethan Xie on 2021.12.7
 * Copyright © 2021 Ethan Xie. All rights reserved.
 */
package edu.vt.EntityBeans;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity

// Name of the database table represented
@Table(name = "JobListing")


public class JobListing implements Serializable {

    private static final long serialVersionUID = 1L;

    // id INT UNSIGNED NOT NULL AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "company_name")
    private String companyName;

    @JoinColumn(name = "company_id", referencedColumnName = "id")
    @ManyToOne
    private CompanyUser companyId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "title")
    private String title;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 4096)
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "field")
    private String field;

    @Basic(optional = true)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "application_url")
    private String applicationUrl;



    public JobListing() {
    }

    public JobListing(Integer id) {
        this.id = id;
    }

    public JobListing(Integer id, String title, String description, CompanyUser companyId, String field, String companyName, String applicationUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.companyId = companyId;
        this.field = field;
        this.companyName = companyName;
        this.applicationUrl = applicationUrl;
    }

    public JobListing(String companyName, String title, String description, String field, String applicationUrl) {
        this.companyName = companyName;
        this.title = title;
        this.description = description;
        this.field = field;
        this.applicationUrl = applicationUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CompanyUser getCompanyId() {
        return companyId;
    }

    public void setCompanyId(CompanyUser companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
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

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JobListing)) {
            return false;
        }
        JobListing other = (JobListing) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    // Return String representation of database primary key id
    @Override
    public String toString() {
        return id.toString();
    }

}
