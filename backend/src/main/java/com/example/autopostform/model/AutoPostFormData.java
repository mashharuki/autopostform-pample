package com.example.autopostform.model;

import java.util.Map;

/**
 * autopost用HTMLフォームの組み立て済みデータ。
 * Thymeleafテンプレート（autopost.html）に渡してHTMLを生成するために使う。
 *
 * このクラスはデータの「入れ物」で、ロジックは持たない（いわゆるDTO: Data Transfer Object）。
 */
public class AutoPostFormData {

    /**
     * フォームの送信先URL。
     * HTMLの <form action="..."> に相当する。
     * 実際の決済では「https://payment.example.com/pay」のような外部URLになる。
     */
    private String actionUrl;

    /**
     * フォームに埋め込む hidden フィールドの一覧。
     * key   = HTMLの name 属性  (例: "order_id", "amount")
     * value = HTMLの value 属性 (例: "ORD-123",  "9800")
     *
     * LinkedHashMap を使うと登録した順番が保持されるため、
     * デバッグ時にHTMLを確認しやすい（HashMap は順不同）。
     */
    private Map<String, String> hiddenFields;

    public AutoPostFormData(String actionUrl, Map<String, String> hiddenFields) {
        this.actionUrl = actionUrl;
        this.hiddenFields = hiddenFields;
    }

    // Thymeleaf が ${formData.actionUrl} で参照するために getter が必要
    public String getActionUrl() { return actionUrl; }

    // Thymeleaf が ${formData.hiddenFields} で参照するために getter が必要
    public Map<String, String> getHiddenFields() { return hiddenFields; }
}
