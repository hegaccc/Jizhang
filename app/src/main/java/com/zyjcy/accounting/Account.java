package com.zyjcy.accounting;


public class Account {
    private int id;
    private String date;
    private String item;
    private String type;
    private Double amount;
    private String remark;


    public Account() {
    }

    public Account(int id, String date, String item, String type, Double amount, String remark) {
        this.id = id;
        this.date = date;
        this.item = item;
        this.type = type;
        this.amount = amount;
        this.remark = remark;
    }

    /**
     * 获取
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取
     * @return date
     */
    public String getDate() {
        return date;
    }

    /**
     * 设置
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 获取
     * @return item
     */
    public String getItem() {
        return item;
    }

    /**
     * 设置
     * @param item
     */
    public void setItem(String item) {
        this.item = item;
    }

    /**
     * 获取
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * 设置
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取
     * @return amount
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * 设置
     * @param amount
     */
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * 获取
     * @return remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String toString() {
        return "Account{id = " + id + ", date = " + date + ", item = " + item + ", type = " + type + ", amount = " + amount + ", remark = " + remark + "}";
    }
}
