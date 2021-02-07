## 一、参考资料
https://www.jianshu.com/p/710f70a99cbc

## 二、单向tls认证
- 1、生成jks格式keystore  
这一步的目的是生成tls所使用的非对称加密公私钥对，和对称加密的密钥，这个文件是有密码的  
```
keytool -genkey -alias server -keysize 2048 -validity 3650 -keyalg RSA -dname "CN=localhost" -keypass nettyDemo -storepass nettyDemo -keystore serverStore.jks
```  
查看生成的keystore信息  
```
keytool -list -v -keystore serverStore.jks -storepass nettyDemo
``` 
- 2、导出netty服务端签名证书  
```
keytool -export -alias server -keystore serverStore.jks -storepass nettyDemo -file server.cer
```
- 3、生成netty客户端的公钥、私钥和证书keystore  
```
keytool -genkey -alias client -keysize 2048 -validity 3650 -keyalg RSA -dname "CN=localhost" -keypass nettyDemo -storepass nettyDemo -keystore clientStore.jks
```
- 4、将netty服务端的证书导入客户端的证书仓库中  
```
keytool -import -trustcacerts -alias server -file server.cer -storepass nettyDemo -keystore clientStore.jks
```

