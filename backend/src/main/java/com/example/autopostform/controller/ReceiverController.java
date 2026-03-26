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
 * - autopostされたフォームデータの受け取り
 * - 署名の検証（改ざんされていないか確認）
 * - 処理結果の表示
 */
@Controller
@RequestMapping("/api")
public class ReceiverController {

    private final AutoPostFormService autoPostFormService;

    public ReceiverController(AutoPostFormService autoPostFormService) {
        this.autoPostFormService = autoPostFormService;
    }

    /**
     * autopostされたデータを受け取り、署名検証の上で結果を表示する。
     *
     * @param orderId   注文ID
     * @param name      注文者名
     * @param itemName  商品名
     * @param amount    金額
     * @param timestamp タイムスタンプ
     * @param signature 署名（改ざん検知用）
     * @param model     Thymeleafテンプレートへ渡すデータ
     * @return received.html（受信結果ページ）
     */
    @PostMapping("/receive")
    public String receive(
            @RequestParam("order_id")  String orderId,
            @RequestParam("name")      String name,
            @RequestParam("item_name") String itemName,
            @RequestParam("amount")    int amount,
            @RequestParam("timestamp") long timestamp,
            @RequestParam("signature") String signature,
            Model model) {

        // 署名検証: 送信時と同じ計算で一致するか確認
        boolean isValid = autoPostFormService.verifySignature(orderId, amount, timestamp, signature);

        model.addAttribute("orderId",   orderId);
        model.addAttribute("name",      name);
        model.addAttribute("itemName",  itemName);
        model.addAttribute("amount",    amount);
        model.addAttribute("timestamp", timestamp);
        model.addAttribute("signature", signature);
        model.addAttribute("isValid",   isValid);

        return "received"; // templates/received.html を返す
    }
}
