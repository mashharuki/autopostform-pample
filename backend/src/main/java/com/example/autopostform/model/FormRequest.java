package com.example.autopostform.model;

/**
 * フロントエンドから送られてくる注文情報を保持するモデルクラス。
 * autopostフローの起点となるデータ。
 *
 * Spring MVCの @ModelAttribute を使うと、
 * HTMLフォームの name 属性とこのクラスのフィールド名が自動でマッピングされる。
 *
 * 例: <input name="amount"> → setAmount(int) が自動で呼ばれる
 *
 * 引数なしコンストラクタ（デフォルトコンストラクタ）が必須。
 * Spring がまずインスタンスを生成してから各フィールドにセットするため。
 */
public class FormRequest {

    /** 注文者名 */
    private String name;

    /** 金額（円） */
    private int amount;

    /** 商品名 */
    private String itemName;

    // Spring の @ModelAttribute バインディングに必要な引数なしコンストラクタ
    public FormRequest() {}

    // テスト等で直接インスタンスを作りたいときに使うコンストラクタ
    public FormRequest(String name, int amount, String itemName) {
        this.name = name;
        this.amount = amount;
        this.itemName = itemName;
    }

    // --- getter / setter ---
    // Spring が @ModelAttribute でフォーム値をセットするために setter が必要

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
}
