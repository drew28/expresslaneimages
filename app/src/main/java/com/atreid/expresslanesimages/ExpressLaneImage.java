package com.atreid.expresslanesimages;

public class ExpressLaneImage {
    public String icon;
    public String title;
    public String route;
    public String direction;
    public ExpressLaneImage(){
        super();
    }

    public ExpressLaneImage(String icon, String title, String route, String direction) {
        super();
        this.icon = icon;
        this.title = title;
        this.route = route;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return this.title;
    }
}