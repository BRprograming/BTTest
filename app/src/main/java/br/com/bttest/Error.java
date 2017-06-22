package br.com.bttest;

/**
 * Created by bartuso on 2017-06-22.
 */

public class Error {
    private String number;
    private String date;
    private String floor;

    public Error(String number, String date, String floor) {
        this.number = number;
        this.date = date;
        this.floor = floor;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }
}
