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
 * @Controller: このクラスがHTTPリクエストを処理するコントローラーであることを示す。
 *              メソッドの戻り値はテンプレート名（Viewの名前）として解釈される。
 *              ※ @RestController との違い: @RestControllerはJSONを返す用途。
 *                @Controller はHTMLを返す用途に使う。
 *
 * フロー全体:
 *   1. フロントエンド (index.html) → POST /api/initiate
 *   2. このコントローラーが autopost.html を返す
 *   3. ブラウザが autopost.html を受け取り、即座にフォームを自動 submit
 *   4. 外部サービス (ReceiverController) へデータが届く
 */
@Controller
@RequestMapping("/api")  // このクラスのメソッド全てに "/api" のプレフィックスを付ける
public class InitiatorController {

    // コンストラクタインジェクション: Spring が AutoPostFormService のインスタンスを自動で注入する
    // フィールドに @Autowired を付ける方法もあるが、コンストラクタ注入の方がテストしやすく推奨される
    private final AutoPostFormService autoPostFormService;

    public InitiatorController(AutoPostFormService autoPostFormService) {
        this.autoPostFormService = autoPostFormService;
    }

    /**
     * autopostフローを開始するエンドポイント。
     *
     * @PostMapping("/initiate"): HTTP POST の /api/initiate にマッピングする。
     *
     * @ModelAttribute FormRequest request:
     *   HTMLフォームの入力値（name, amount, itemName）を
     *   FormRequest クラスのフィールドに自動でバインドしてくれる。
     *   例: <input name="amount" value="9800"> → request.getAmount() == 9800
     *
     * Model model:
     *   コントローラーからThymeleafテンプレートへデータを渡すための入れ物。
     *   model.addAttribute("key", value) で追加した値は
     *   テンプレート内で ${key} として参照できる。
     *
     * @return "autopost" → Thymeleaf が templates/autopost.html を探してレンダリングする
     */
    @PostMapping("/initiate")
    public String initiate(@ModelAttribute FormRequest request, Model model) {
        // 送信先URL（外部サービスのエンドポイント）
        // このサンプルでは同じサーバーの /api/receive を外部サービスに見立てている
        String actionUrl = "http://localhost:8080/api/receive";

        // サービスクラスで署名・注文IDを生成し、hiddenフィールドのデータを組み立てる
        AutoPostFormData formData = autoPostFormService.buildFormData(request, actionUrl);

        // Thymeleafテンプレートへ formData を渡す
        // テンプレート内では ${formData.actionUrl}, ${formData.hiddenFields} で参照できる
        model.addAttribute("formData", formData);

        // "autopost" を返すと Spring が src/main/resources/templates/autopost.html を返す
        return "autopost";
    }
}
