package com.example.autopostform.model;

/**
 * フロントエンドから送られてくる注文情報。
 * autopostフローの起点となるデータ。
 */
public class FormRequest {

    /** 注文者名 */
    private String name;

    /** 金額（円） */
    private int amount;

    /** 商品名 */
    private String itemName;

    public FormRequest() {}

    public FormRequest(String name, int amount, String itemName) {
        this.name = name;
        this.amount = amount;
        this.itemName = itemName;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
}
