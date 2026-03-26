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
 * Autopostフォームのビジネスロジックを担うサービスクラス。
 *
 * @Service を付けると Spring が起動時にこのクラスをインスタンス化（Bean登録）し、
 * @Autowired や コンストラクタインジェクションで他のクラスから使えるようになる。
 *
 * 実際の決済システムでは、ここで以下のような処理を行う：
 *   - 秘密鍵を使った HMAC-SHA256 署名の生成
 *   - トランザクションIDの発行・DBへの保存
 *   - タイムスタンプによるリプレイ攻撃の防止（古い署名を無効化）
 *
 * このサンプルでは学習目的のため SHA-256 で簡易的な署名を生成する。
 */
@Service
public class AutoPostFormService {

    // 本来は application.properties や環境変数 (System.getenv) から取得する秘密鍵
    // ソースコードに直書きするのは厳禁（このサンプルは学習用のため例外）
    private static final String SECRET_KEY = "my-secret-key-for-learning";

    /**
     * フォームリクエストから autopost 用のデータを組み立てる。
     *
     * 処理の流れ:
     *   1. ユニークな注文IDを発行
     *   2. 現在のUNIXタイムスタンプを取得
     *   3. 注文ID・金額・タイムスタンプから署名を生成
     *   4. hiddenフィールドのマップにまとめて AutoPostFormData を返す
     *
     * @param request    フロントエンドからの入力データ（名前・金額・商品名）
     * @param actionUrl  送信先URL（外部サービスのエンドポイント）
     * @return テンプレートに渡す AutoPostFormData
     */
    public AutoPostFormData buildFormData(FormRequest request, String actionUrl) {
        // 1. 注文IDを発行（現在のミリ秒を使うことで一意性を確保）
        String orderId = generateOrderId();

        // 2. UNIXタイムスタンプ（1970年1月1日からの経過秒数）を取得
        //    受信側でこの値を使い、有効期限チェックなどを行う
        long timestamp = Instant.now().getEpochSecond();

        // 3. 署名を生成（改ざん防止のため。詳細は generateSignature を参照）
        String signature = generateSignature(orderId, request.getAmount(), timestamp);

        // 4. autopostフォームの hidden フィールドとして外部サービスへ送るデータをまとめる
        //    LinkedHashMap は挿入順を保持するため、HTMLに展開された際の並び順が安定する
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("order_id",  orderId);
        fields.put("name",      request.getName());
        fields.put("item_name", request.getItemName());
        fields.put("amount",    String.valueOf(request.getAmount()));
        fields.put("timestamp", String.valueOf(timestamp));
        fields.put("signature", signature);  // 改ざん検知用署名。受信側で必ず検証する

        return new AutoPostFormData(actionUrl, fields);
    }

    /**
     * 受信側で署名を検証する。
     *
     * 仕組み: 送信時と全く同じ入力値・アルゴリズムで署名を再計算し、
     *         受け取った署名と一致するかを比較する。
     *         一致すれば「バックエンドが生成した正規のデータ」と判断できる。
     *
     * @param orderId           受信した注文ID
     * @param amount            受信した金額
     * @param timestamp         受信したタイムスタンプ
     * @param receivedSignature 受信した署名
     * @return 署名が正しければ true、改ざんが疑われる場合は false
     */
    public boolean verifySignature(String orderId, int amount, long timestamp, String receivedSignature) {
        // 送信時と同じ計算で「正しい署名」を再生成する
        String expectedSignature = generateSignature(orderId, amount, timestamp);
        // 期待値と受信値を比較（一致すれば改ざんなし）
        return expectedSignature.equals(receivedSignature);
    }

    // ---- private ヘルパーメソッド ----

    /**
     * ユニークな注文IDを生成する。
     * "ORD-" プレフィックス + 現在のミリ秒数 で構成。
     * 本番では UUID.randomUUID() や DB のシーケンスを使うことが多い。
     */
    private String generateOrderId() {
        return "ORD-" + Instant.now().toEpochMilli();
    }

    /**
     * 簡易署名を生成する。
     *
     * アルゴリズム: SHA-256( orderId + ":" + amount + ":" + timestamp + ":" + secretKey )
     *
     * SHA-256 とは:
     *   どんな入力でも必ず256ビット(64文字の16進数)のハッシュ値を出力するハッシュ関数。
     *   入力が1文字でも変わると全く異なるハッシュになる（改ざん検知に使える）。
     *   ハッシュから元の値を復元することは事実上不可能（一方向性）。
     *
     * 実運用では HMAC-SHA256 を使うこと。
     *   HMAC は秘密鍵をより安全に組み込む方式で、長さ拡張攻撃への耐性がある。
     *
     * @param orderId   注文ID
     * @param amount    金額
     * @param timestamp タイムスタンプ
     * @return 16進数文字列の署名（64文字）
     */
    private String generateSignature(String orderId, int amount, long timestamp) {
        // 署名対象の文字列を ":" で連結して組み立てる
        String raw = orderId + ":" + amount + ":" + timestamp + ":" + SECRET_KEY;

        try {
            // MessageDigest は Java 標準ライブラリのハッシュ計算クラス
            // getInstance("SHA-256") で SHA-256 アルゴリズムを指定する
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 文字列をバイト配列に変換してハッシュ計算
            // StandardCharsets.UTF_8 を明示することで実行環境によらず同じ結果になる
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));

            // バイト配列 → 16進数文字列に変換（例: 0xFF → "ff"）
            // %02x は「2桁の16進数で、1桁の場合は先頭に0を付ける」という書式指定
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString(); // 64文字の16進数文字列

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 は Java SE で必ずサポートされているため、通常ここには到達しない
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
