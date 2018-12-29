package com.mobicule.myapp;

import android.graphics.Bitmap;

public class NewModal {
    String text;
    Bitmap bmp;

    public NewModal() {
    }

    public NewModal(String text) {

        this.text = text;
    }
    public NewModal(String text , Bitmap bmp) {
        this.text = text;
        this.bmp = bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public Bitmap getBmp() {
        return bmp;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
