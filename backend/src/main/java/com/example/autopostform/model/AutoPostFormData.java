package com.example.autopostform.model;

import java.util.Map;

/**
 * autopost用フォームの組み立て済みデータ。
 * テンプレートに渡してHTMLを生成するために使う。
 */
public class AutoPostFormData {

    /** 送信先URL（外部サービスのエンドポイント） */
    private String actionUrl;

    /**
     * フォームに埋め込むhiddenフィールドのマップ。
     * key = フィールド名, value = フィールド値
     */
    private Map<String, String> hiddenFields;

    public AutoPostFormData(String actionUrl, Map<String, String> hiddenFields) {
        this.actionUrl = actionUrl;
        this.hiddenFields = hiddenFields;
    }

    public String getActionUrl() { return actionUrl; }
    public Map<String, String> getHiddenFields() { return hiddenFields; }
}
