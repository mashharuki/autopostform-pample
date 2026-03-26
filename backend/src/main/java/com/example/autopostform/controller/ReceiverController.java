package com.example.autopostform.controller;

import com.example.autopostform.service.AutoPostFormService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Autopostフローの【受信側】コントローラー。
 *
 * 実際のシステムでは「外部の決済サービス」「外部認証サービス」などが担う役割。
 * このサンプルでは学習用に同じサーバー内に実装している。
 *
 * 主な責務:
 *   1. autopost されたフォームデータを受け取る
 *   2. 署名を検証し、データが改ざんされていないか確認する
 *   3. 処理結果をテンプレートで表示する
 */
@Controller
@RequestMapping("/api")
public class ReceiverController {

    private final AutoPostFormService autoPostFormService;

    public ReceiverController(AutoPostFormService autoPostFormService) {
        this.autoPostFormService = autoPostFormService;
    }

    /**
     * autopost されたデータを受け取り、署名検証の上で結果を表示する。
     *
     * @RequestParam("order_id") String orderId:
     *   autopost.html の <input type="hidden" name="order_id"> の値を受け取る。
     *   パラメータ名は HTML の name 属性と一致させる必要がある（スネークケースに注意）。
     *
     * 各パラメータの対応:
     *   order_id  → <input name="order_id">  → 注文ID
     *   name      → <input name="name">      → 注文者名
     *   item_name → <input name="item_name"> → 商品名
     *   amount    → <input name="amount">    → 金額（int型に自動変換される）
     *   timestamp → <input name="timestamp"> → タイムスタンプ（long型に自動変換される）
     *   signature → <input name="signature"> → 改ざん検知用署名
     *
     * @return "received" → templates/received.html をレンダリングして返す
     */
    @PostMapping("/receive")
    public String receive(
            @RequestParam("order_id")  String orderId,
            @RequestParam("name")      String name,
            @RequestParam("item_name") String itemName,
            @RequestParam("amount")    int amount,       // Stringから自動でintに変換される
            @RequestParam("timestamp") long timestamp,   // Stringから自動でlongに変換される
            @RequestParam("signature") String signature,
            Model model) {

        // 署名検証: 受け取ったデータから同じ計算で署名を再生成し、送られてきた署名と比較する
        // 一致すれば「バックエンドが正規に生成したデータ」と判断できる
        boolean isValid = autoPostFormService.verifySignature(orderId, amount, timestamp, signature);

        // 受信したデータをすべてテンプレートへ渡す（画面に表示するため）
        model.addAttribute("orderId",   orderId);
        model.addAttribute("name",      name);
        model.addAttribute("itemName",  itemName);
        model.addAttribute("amount",    amount);
        model.addAttribute("timestamp", timestamp);
        model.addAttribute("signature", signature);
        model.addAttribute("isValid",   isValid); // 署名検証の結果（true/false）

        // templates/received.html を返す
        return "received";
    }
}
