package edu.vt.controllers;
/*
 * Created by Luke Janoschka on 2021.11.30
 * Copyright © 2021 Luke Janoschka. All rights reserved.
 */

import edu.vt.globals.Methods;
import edu.vt.pojo.ApiJob;
import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;

@Named("apiController")
@SessionScoped
public class ApiController implements Serializable {

    private String searchString;

    private ApiJob selected;
    private ArrayList<ApiJob> listOfSearchedJobs;


    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public ApiJob getSelected() {
        return selected;
    }

    public void setSelected(ApiJob selected) {
        this.selected = selected;
    }

    public ArrayList<ApiJob> getListOfSearchedJobs() {
        return listOfSearchedJobs;
    }

    public void setListOfSearchedJobs(ArrayList<ApiJob> listOfSearchedJobs) {
        this.listOfSearchedJobs = listOfSearchedJobs;
    }

    public String performSearch() throws Exception {

        selected = null;
        listOfSearchedJobs = new ArrayList<>();

        searchString = searchString.replaceAll(" ", "%20");

        String searchApiUrl = "https://api.adzuna.com/v1/api/jobs/gb/search/1?app_id=c3faa28e&app_key=b6b19e95adc1546833e7fe979ee2b7b9&results_per_page=20&what="
                + searchString  + "&content-type=application/json";

        try {
            // Obtain the JSON file containing the movie search results at the given page number
            String apiSearchResultsJsonData = Methods.readUrlContent(searchApiUrl);
            // Instantiate a JSON object from the JSON data obtained
            JSONObject resultsJsonObject = new JSONObject(apiSearchResultsJsonData);

            // Obtain a JSONArray of movie objects (Each movie is represented as a JSONObject)
            JSONArray jsonArrayFoundListings = resultsJsonObject.getJSONArray("results");
            try {

                for (int i = 0; i < 20; ++i) {
                    JSONObject jsonObject = jsonArrayFoundListings.getJSONObject(i);

                    // listing title
                    String title = jsonObject.optString("title", "");
                    if (title.equals("")) {
                        // skip the job listing
                        continue;
                    }

                    // listing description
                    String description = jsonObject.optString("description", "");
                    if (description.equals("")) {
                        // skip the job listing
                        continue;
                    }

                    // listing link
                    String redirect_url = jsonObject.optString("redirect_url", "");
                    if (redirect_url.equals("")) {
                        // skip the job listing
                        continue;
                    }

                    JSONObject companyObject = jsonObject.getJSONObject("company");
                    String company = companyObject.optString("display_name", "");
                    if (company.equals("")) {
                        // skip the job listing
                        continue;
                    }

                    JSONObject category = jsonObject.getJSONObject("category");
                    String field = category.optString("label", "");
                    if (field.equals("")) {
                        // skip the job listing
                        continue;
                    }



                    ApiJob job = new ApiJob(i, company, title, description, field, redirect_url);

                    listOfSearchedJobs.add(job);


                }

            } catch (Exception ex) {
                Methods.showMessage("Fatal Error", "The API limit of 4 accesses per second has been exceeded!",
                        "See: " + ex.getMessage());
                searchString = "";
                return "";
            }
        } catch (Exception ex) {
            Methods.showMessage("Information", "No Results!",
                    "No job listings found for the search query!");
            searchString = "";
            return "/Search/ApiSearchResults.xhtml?faces-redirect=true";
        }
        return "/Search/ApiSearchResults.xhtml?faces-redirect=true";


    }

    public void clearSearch() {
        searchString = null;
        listOfSearchedJobs = null;
    }
}
