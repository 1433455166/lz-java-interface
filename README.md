# lz-java-interface

## 重新编译
mvn clean compile

## 启动
mvn spring-boot:run

## 获取本机IP地址  连接 Wi-Fi 时
ipconfig getifaddr en0

## 目录
src
└── main
    ├── java
    │   └── com
    │       └── example
    │           └── lz_java_test
    │               ├── config
    │               ├── controller     ← 接口层（Controller）
    │               ├── model
    │               ├── repository
    │               ├── service        ← 业务层（Service）
    │               ├── dto            ← 请求/响应参数对象 ⭐
    │               ├── websocket
    │               └── JavaTestApplication.java
    └── resources
        └── data
            └── threeBody
                ├── threeBody1.json
                ├── threeBody2.json
                └── threeBody3.json