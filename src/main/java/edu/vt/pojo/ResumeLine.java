package edu.vt.pojo;
/*
 * Created by Luke Janoschka on 2021.12.05
 * Copyright © 2021 Luke Janoschka. All rights reserved.
 */


public class ResumeLine {
    private boolean header;
    private int id;
    private String col1;
    private String col2;
    private String col3;

    public ResumeLine(String col1, String col2, String col3, int id, boolean header) {
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.id = id;
        this.header = header;
    }

    public ResumeLine(String col2, boolean header, int id) {
        this.header = header;
        this.id = id;
        this.col2 = col2;
    }

    public ResumeLine(int id) {
        this.id = id;
        this.header = false;
    }

    public String getCol1() {
        return col1;
    }

    public void setCol1(String col1) {
        this.col1 = col1;
    }

    public String getCol2() {
        return col2;
    }

    public void setCol2(String col2) {
        this.col2 = col2;
    }

    public String getCol3() {
        return col3;
    }

    public void setCol3(String col3) {
        this.col3 = col3;
    }

    public int getId() {
        return id;
    }

    public boolean isHeader() {
        return header;
    }
}
