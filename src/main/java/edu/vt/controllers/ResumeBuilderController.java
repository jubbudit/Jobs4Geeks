package edu.vt.controllers;
/*
 * Created by Luke Janoschka on 2021.12.05
 * Copyright © 2021 Luke Janoschka. All rights reserved.
 */

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import edu.vt.pojo.ResumeLine;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;

@Named("resumeBuilderController")
@SessionScoped
public class ResumeBuilderController implements Serializable {

    private int lineId;
    private ResumeLine selected;
    private ResumeLine nameHeader;
    private ResumeLine detailsHeader;
    private ResumeLine educationHeader;
    private ResumeLine activitiesHeader;
    private ResumeLine experienceHeader;
    private ArrayList<ResumeLine> resumeTable;

    public ResumeLine getSelected() {
        return selected;
    }

    public void setSelected(ResumeLine selected) {
        this.selected = selected;
    }

    public ResumeLine getNameHeader() {
        return nameHeader;
    }

    public void setNameHeader(ResumeLine nameHeader) {
        this.nameHeader = nameHeader;
    }

    public ResumeLine getDetailsHeader() {
        return detailsHeader;
    }

    public void setDetailsHeader(ResumeLine detailsHeader) {
        this.detailsHeader = detailsHeader;
    }

    public ResumeLine getEducationHeader() {
        return educationHeader;
    }

    public void setEducationHeader(ResumeLine educationHeader) {
        this.educationHeader = educationHeader;
    }

    public ResumeLine getActivitiesHeader() {
        return activitiesHeader;
    }

    public void setActivitiesHeader(ResumeLine activitiesHeader) {
        this.activitiesHeader = activitiesHeader;
    }

    public ResumeLine getExperienceHeader() {
        return experienceHeader;
    }

    public void setExperienceHeader(ResumeLine experienceHeader) {
        this.experienceHeader = experienceHeader;
    }

    public ArrayList<ResumeLine> getResumeTable() {
        if (resumeTable == null) {
            initResume();
        }
        return resumeTable;
    }

    public void setResumeTable(ArrayList<ResumeLine> resumeTable) {
        this.resumeTable = resumeTable;
    }

    public void initResume() {
        resumeTable = new ArrayList<>();
        nameHeader = new ResumeLine("Name Here", true, ++lineId);
        educationHeader = new ResumeLine("Education", true, ++lineId);
        activitiesHeader = new ResumeLine("Activities", true, ++lineId);
        experienceHeader = new ResumeLine("Experience", true, ++lineId);
        detailsHeader = new ResumeLine("Address", "Phone Number", "Email", ++lineId, true);
        resumeTable.add(nameHeader);
        resumeTable.add(detailsHeader);
        resumeTable.add(educationHeader);
        resumeTable.add(activitiesHeader);
        resumeTable.add(experienceHeader);
    }

    public void preProcessPDF(Object document) {
        Document pdf = (Document) document;
        pdf.setPageSize(PageSize.LETTER);
        pdf.open();
    }

    public void destroy() {
        if (!selected.isHeader()) {
            resumeTable.remove(selected);
        }
    }

    public void updateDetails() {
    }

    public void prepareCreate() {
        selected = new ResumeLine(++lineId);
    }

    public void saveRow(int type) {
        switch (type) {
            case 1:
                resumeTable.add(resumeTable.indexOf(educationHeader)+1, selected);
                break;
            case 2:
                resumeTable.add(resumeTable.indexOf(activitiesHeader)+1, selected);
                break;
            case 3:
                //if (resumeTable.indexOf(experienceHeader) == resumeTable.size()) {
                    //resumeTable.add(selected);
                //} else {
                    resumeTable.add(resumeTable.indexOf(experienceHeader) + 1, selected);
                //}
                break;
        }
        selected = null;
    }
}
