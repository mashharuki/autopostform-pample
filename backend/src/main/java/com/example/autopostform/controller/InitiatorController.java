package com.example.autopostform.controller;

import com.example.autopostform.model.AutoPostFormData;
import com.example.autopostform.model.FormRequest;
import com.example.autopostform.service.AutoPostFormService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Autopostフローの【起点】コントローラー。
 *
 * フロントエンドからPOSTを受け取り、
 * 外部サービスへ自動送信するHTMLページを返す。
 *
 * フロー:
 *   1. フロントエンド → POST /api/initiate
 *   2. このコントローラーがautopost.htmlを返す
 *   3. ブラウザがautopost.htmlを受け取り即座にフォームをsubmit
 *   4. 外部サービス（ReceiverController）へデータが届く
 */
@Controller
@RequestMapping("/api")
public class InitiatorController {

    private final AutoPostFormService autoPostFormService;

    public InitiatorController(AutoPostFormService autoPostFormService) {
        this.autoPostFormService = autoPostFormService;
    }

    /**
     * autopostフローを開始する。
     *
     * @param request   フォームの入力値（name, amount, itemName）
     * @param model     Thymeleafテンプレートへ渡すデータ
     * @return autopost.html（自動submit付きページ）
     */
    @PostMapping("/initiate")
    public String initiate(@ModelAttribute FormRequest request, Model model) {
        // 外部サービス（このサンプルでは同じサーバーの /api/receive）
        String actionUrl = "http://localhost:8080/api/receive";

        AutoPostFormData formData = autoPostFormService.buildFormData(request, actionUrl);

        // テンプレートへデータを渡す
        model.addAttribute("formData", formData);

        return "autopost"; // templates/autopost.html を返す
    }
}
