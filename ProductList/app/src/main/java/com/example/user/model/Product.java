package com.example.user.model;

public class Product {
    // public constants
    public final static String EXIST_YES = "yes";
    public final static String EXIST_NO = "no";
    public final static String EXIST_DEFAULT_VALUE = EXIST_NO;

    private String name;
    private String explanation;
    private String image;
    private String exist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getExist() {
        return exist;
    }

    public void setExist(String exist) {
        this.exist = exist;
    }
}
