package com.mercadolibre.test.Model;

public class Item {

    private String thumbnail;

    private String description;

    private String price;

    private Boolean shipping;

    private String condition;

    private int quantity;

    private int soldQuantity;

    public Item() {
    }

    public Item(String thumbnail, String description, String price, Boolean shipping, String condition, int quantity, int soldQuantity) {
        this.thumbnail = thumbnail;

        this.description = description;

        this.price = price;

        this.shipping = shipping;

        this.condition = condition;

        this.quantity = quantity;

        this.soldQuantity = soldQuantity;
    }

    public String GetThumbnail() {
        return this.thumbnail;
    }

    public void SetThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String GetDescription() {
        return this.description;
    }

    public void SetDescription(String description) {
        this.description = description;
    }

    public String GetPrice() {
        return this.price;
    }

    public void SetPrice(String price) {
        this.price = price;
    }

    public Boolean GetShipping() {
        return this.shipping;
    }

    public void SetShipping(Boolean shipping) {
        this.shipping = shipping;
    }

    public String GetCondition() {
        return this.condition;
    }

    public void SetCondition(String condition) {
        this.condition = condition;
    }

    public int GetQuantity() {
        return this.quantity;
    }

    public void SetQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int GetSoldQuantity() {
        return this.soldQuantity;
    }

    public void SetSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }
}