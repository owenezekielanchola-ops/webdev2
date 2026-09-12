package com.copal.webdev2;

public class Product {
    private String name;
    private double price;
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(double price) {
        if(price>=0){
         this.price=price;
        }
    }
    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
      @Override
    public String toString() {
        return name + " - " + price;
    }
    
}
