package edu.vt.pojo;
/*
 * Created by Luke Janoschka on 2021.11.30
 * Copyright © 2021 Luke Janoschka. All rights reserved.
 */

public class ApiJob {

    /*
CREATE TABLE JobListingv
(
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
    company_name VARCHAR(128) NOT NULL,
	company_id INT UNSIGNED,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(4096) NOT NULL,
    field VARCHAR(255) NOT NULL,
    application_url VARCHAR(255),
    FOREIGN KEY (company_id) REFERENCES CompanyUser(id) ON DELETE CASCADE
);     */

    private Integer id;
    private String companyName;
    private String title;
    private String description;
    private String field;
    private String appUrl;

    public ApiJob(Integer id, String companyName, String title, String description, String field, String appUrl) {
        this.id = id;
        this.companyName = companyName;
        this.title = title;
        this.description = description;
        this.field = field;
        this.appUrl = appUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }
}
