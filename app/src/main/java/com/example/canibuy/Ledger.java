package com.example.canibuy;

public class Ledger {
    String category;
    String ammount;
    String itemName;
    boolean debited;

    public Ledger() {
    }

    public Ledger(String category, String ammount, String itemName, boolean debited) {
        this.category = category;
        this.ammount = ammount;
        this.itemName = itemName;
        this.debited = debited;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmmount() {
        return ammount;
    }

    public void setAmmount(String ammount) {
        this.ammount = ammount;
    }

    public boolean isDebited() {
        return debited;
    }

    public void setDebited(boolean debited) {
        this.debited = debited;
    }
}
