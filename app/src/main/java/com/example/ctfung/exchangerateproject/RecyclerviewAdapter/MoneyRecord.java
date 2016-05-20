package com.example.ctfung.exchangerateproject.RecyclerviewAdapter;

/**
 * Created by CTFung on 19/5/16.
 */
public class MoneyRecord {
    private int mid, iconId;
    private String title, date, money, note;

    public MoneyRecord(int mid, int iconId, String title, String date, String money, String note) {
        this.mid = mid;
        this.iconId = iconId;
        this.title = title;
        this.date = date;
        this.money = money;
        this.note = note;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
