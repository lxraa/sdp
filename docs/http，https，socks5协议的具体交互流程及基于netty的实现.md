## 一、http（s） proxy：



http代理有两种形式：

- [RFC 7230](http://tools.ietf.org/html/rfc7230)

- [RFC 7231](https://tools.ietf.org/html/rfc7231#section-4.3.6)

第一种无握手，一般用在http proxy，对于客户端传来的包，服务器只需要关心应用层的数据。需要处理两个东西：①从http header里拿出host，作为目标主机。②加一个X-FORWARDED-FOR header，用来标记源IP。

其他直接透传给后端即可。

![image-20210208110800916](C:\Users\LIUXR\Desktop\code\proxy\docs\image-20210208110800916.png)

第二种需要预先处理http connect包，一般用在https proxy。客户端会预先发送一个HTTP握手包，方法是CONNECT，目标主机信息还是在host header里。网关与目标主机建立tcp连接后，需要返回一个HTTP status为200的包，告诉客户端连接已经建立好了，客户端就会开始tls握手。

①如果proxy想解析应用层信息，那么proxy就要和客户端进行tls握手，还要和目标主机进行tls握手，对于客户端来说，认证的是网关的证书。

②如果proxy不想解析应用层信息，那么proxy直接把TCP流透传给后端，并把目标主机的TCP流透传给客户端即可。

![image-20210208110832064](C:\Users\LIUXR\Desktop\code\proxy\docs\image-20210208110832064.png)

## 二、socks5 proxy

[RFC1928](https://tools.ietf.org/html/rfc1928)

![image-20210208112236481](C:\Users\LIUXR\Desktop\code\proxy\docs\image-20210208112236481.png)

①客户端会先问一下proxy认证类型，所有类型如下

![image-20210208133339032](C:\Users\LIUXR\Desktop\code\proxy\docs\image-20210208133339032.png)

②按照认证类型发送socks5请求包，连接建立完成后发送socks5 response，客户端开始发送tcp流。