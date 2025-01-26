package com.group6.tinderforfood;


import java.lang.reflect.Field;

public class FoodCategory { //FoodCategory object represents a single food category ie pizza, Chinese, American, etc
    private String name; //The name of the category
    private int picId; //The link (online or offline) to the image representation of a category

    public FoodCategory(String name){
        this.name = name;
        picId = getResId(name.replaceAll(" ","_").toLowerCase(), R.drawable.class);
        //for the purposes of this app prototype, the image used for each category will be a .jpg with the same name stored in res/drawable/
    }

    public String getName(){
        return name.toLowerCase();
    }

    public int getPicId(){
        return picId;
    }

    public void setName(String name){
        this.name = name;
        picId = getResId(name.replaceAll(" ","_").toLowerCase(), R.drawable.class);
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
