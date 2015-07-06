package com.example.randommeal;

/**
 * Created by 계현 on 2015-07-03.
 */
public class Store {

    private String name;
    private double grade;

    public Store(String name, double grade)
    {
        this.name = name;
        this.grade =grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
}
