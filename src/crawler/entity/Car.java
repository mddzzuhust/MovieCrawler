package crawler.entity;

import java.util.HashSet;

public class Car {
    private String Brand;
    private HashSet<String> imgUrlSet = new HashSet<String>(); 
    
    public String getBrand(){
    	return Brand;
    }
    public void setBrand(String brand){
    	this.Brand=brand;
    }
    
    public HashSet<String> getimgUrlSet(){
    	return imgUrlSet;
    }
    public void setimgUrlSet(HashSet<String> imgUrlSet){
    	this.imgUrlSet=imgUrlSet;
    }
}
