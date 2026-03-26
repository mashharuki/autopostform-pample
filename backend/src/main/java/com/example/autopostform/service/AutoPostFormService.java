package com.example.autopostform.service;

import com.example.autopostform.model.AutoPostFormData;
import com.example.autopostform.model.FormRequest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Autopostフォームのビジネスロジック。
 *
 * 実際の決済システムでは、ここで以下のような処理を行う：
 * - 秘密鍵を使ったHMAC署名の生成
 * - トランザクションIDの発行
 * - タイムスタンプによるリプレイ攻撃の防止
 *
 * このサンプルでは学習目的のためSHA-256で簡易的な署名を生成する。
 */
@Service
public class AutoPostFormService {

    // 本来は環境変数や設定ファイルから取得する秘密鍵
    private static final String SECRET_KEY = "my-secret-key-for-learning";

    /**
     * フォームリクエストからautopost用データを組み立てる。
     *
     * @param request  フロントエンドからの入力データ
     * @param actionUrl 送信先URL（外部サービス）
     * @return テンプレートに渡すAutoPostFormData
     */
    public AutoPostFormData buildFormData(FormRequest request, String actionUrl) {
        String orderId = generateOrderId();
        long timestamp = Instant.now().getEpochSecond();
        String signature = generateSignature(orderId, request.getAmount(), timestamp);

        // hiddenフィールドとして外部サービスに送るデータ
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("order_id",  orderId);
        fields.put("name",      request.getName());
        fields.put("item_name", request.getItemName());
        fields.put("amount",    String.valueOf(request.getAmount()));
        fields.put("timestamp", String.valueOf(timestamp));
        fields.put("signature", signature);  // 改ざん検知用署名

        return new AutoPostFormData(actionUrl, fields);
    }

    /**
     * 受信側で署名を検証する。
     * 送信時と同じアルゴリズムで署名を再生成し一致するか確認する。
     */
    public boolean verifySignature(String orderId, int amount, long timestamp, String receivedSignature) {
        String expectedSignature = generateSignature(orderId, amount, timestamp);
        return expectedSignature.equals(receivedSignature);
    }

    // ---- private helpers ----

    private String generateOrderId() {
        return "ORD-" + Instant.now().toEpochMilli();
    }

    /**
     * 簡易署名: SHA-256(orderId + amount + timestamp + secretKey)
     * 実運用ではHMAC-SHA256を使うこと。
     */
    private String generateSignature(String orderId, int amount, long timestamp) {
        String raw = orderId + ":" + amount + ":" + timestamp + ":" + SECRET_KEY;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
