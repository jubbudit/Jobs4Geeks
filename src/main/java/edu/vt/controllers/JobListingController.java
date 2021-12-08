/*
 * Created by Osman Balci on 2021.7.20
 * Copyright © 2021 Osman Balci. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.CompanyUser;
import edu.vt.EntityBeans.JobListing;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.globals.Methods;

import edu.vt.FacadeBeans.JobListingFacade;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named("jobListingController")
@SessionScoped
public class JobListingController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private List<JobListing> listOfJobListings = null;
    private JobListing selected;
    private String searchString;
    private List<JobListing> searchItems = null;
    private Integer searchType;
    private String searchField;


    @EJB
    private JobListingFacade jobListingFacade;

    //@EJB
    //private CompanyController companyController;



    public List<JobListing> getListOfJobListings() {
        if (listOfJobListings == null) {
            listOfJobListings = jobListingFacade.findAll();
        }
        return listOfJobListings;
    }

    public JobListing getSelected() {
        return selected;
    }

    public void setSelected(JobListing selected) {
        this.selected = selected;
    }


    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }


    public void unselect() {
        selected = null;
    }

    /*
     *************************************
     *   Cancel and Display List.xhtml   *
     *************************************
     */
    public String cancel() {
        selected = null;
        return "/JobListing/List?faces-redirect=true";
    }

    public void prepareCreate() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        CompanyUser signedInUser = (CompanyUser) sessionMap.get("user");
        selected = new JobListing();
        selected.setCompanyId(signedInUser);
        selected.setCompanyName(signedInUser.getName());
    }



    public void create() {
        Methods.preserveMessages();

        persist(PersistAction.CREATE,"JobListing was Successfully Created!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The CREATE operation is successfully performed.
            selected = null;        // Remove selection
            listOfJobListings = null;
        }
    }

    public void update() {
        Methods.preserveMessages();

        persist(PersistAction.UPDATE,"JobListing was Successfully Updated!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The UPDATE operation is successfully performed.
            selected = null;        // Remove selection
            listOfJobListings = null;
        }
    }

    public void destroy() {
        Methods.preserveMessages();

        persist(PersistAction.DELETE,"JobListing was Successfully Deleted!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The DELETE operation is successfully performed.
            selected = null;        // Remove selection
            listOfJobListings = null;
        }
    }

    /**
     * @param persistAction refers to CREATE, UPDATE (Edit) or DELETE action
     * @param successMessage displayed to inform the user about the result
     */
    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            try {
                if (persistAction != PersistAction.DELETE) {

                    jobListingFacade.edit(selected);
                } else {

                    jobListingFacade.remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex,"A persistence error occurred.");
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex,"A persistence error occurred.");
            }
        }
    }

    public String search(Integer type) {
        // Set search type given as input parameter
        searchType = type;

        // Unselect previously selected country if any before showing the search results
        selected = null;

        // Invalidate list of search items to trigger re-query.
        searchItems = null;

        return "/databaseSearch/DatabaseSearchResults?faces-redirect=true";
    }

    public boolean isMyListing() {
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            try {
                CompanyUser signedInUser = (CompanyUser) sessionMap.get("user");
                if (signedInUser == null) {
                    return false;
                }
                if (Objects.equals(selected.getCompanyId().getId(), signedInUser.getId())) {
                    return true;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
    }



}
