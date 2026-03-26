package com.example.autopostform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * アプリケーションのエントリーポイント（起動クラス）。
 *
 * @SpringBootApplication は以下の3つのアノテーションをまとめたもの:
 *   - @Configuration     : このクラスがBean定義のソースであることを示す
 *   - @EnableAutoConfiguration : Spring Bootが依存関係を見て自動設定を有効にする
 *                                 (例: Thymeleafがあれば自動でテンプレートエンジンを設定)
 *   - @ComponentScan     : このパッケージ配下の @Controller, @Service などを自動検出する
 */
@SpringBootApplication
public class AutopostformApplication {

    public static void main(String[] args) {
        // SpringApplicationクラスがTomcatを起動し、アプリ全体を初期化する
        SpringApplication.run(AutopostformApplication.class, args);
    }
}
