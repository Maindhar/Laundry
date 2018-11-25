package com.example.manidharkodurupaka.laundry;

public class Search {
    String total;
    String enrollmentno;
    String uid;
    String name;
    String status;
    String date;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getEnrollmentno() {
        return enrollmentno;
    }

    public void setEnrollmentno(String enrollmentno) {
        this.enrollmentno = enrollmentno;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Search(String total, String enrollmentno, String uid, String name, String status, String date) {
        this.total = total;
        this.enrollmentno = enrollmentno;
        this.uid = uid;
        this.name = name;
        this.status = status;
        this.date = date;
    }

    public Search() {
    }
}
