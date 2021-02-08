## 一、为什么选择netty

网关本质上是个代理，选择netty有两点原因：

- 性能。网关作为流量统一入口，需要解决性能问题。netty的nio模型和零拷贝理论性能比较高。

- 定制化。与一般的代理不同，如果要在SDP网关实现基于认证中心的身份认证+权限认证，必须能够解析流经网关的流量，不能只关心传输层的流量转发，还要能够解析应用层的数据包。

  例如对于https来说，与网关的一次ssl握手不同，客户端要与网关进行一次ssl握手，网关要与服务器进行一次握手，网关要能够透明的处理http请求，这种需求可以使用netty比较方便的实现。

但是使用异步框架编程难度会增加。

## 二、模型

这个demo是一个http代理，对于http和https由于请求方式的不同，需要使用不同的实现方式

http:

![image-20210205102409912](C:\Users\LIUXR\Desktop\code\proxy\docs\image-20210205101734143.png)

serverCodec：负责encode http request，和decode http response

aggregator：由于http的请求头和请求体是分开传输的，这个handler负责把请求头和请求体一起转发给后续handler。

requestForwarder：负责处理reuqest，并把请求转发给后续的channel

clientCodec：负责decode http request，和encode http response。

responseForwarder：负责处理response，并把response转发给后端



https：

https代理请求会先发送一个method为CONNECT的包给网关，所以首先要处理这个connect请求



![image-20210205103903892](C:\Users\LIUXR\Desktop\code\proxy\docs\image-20210205103903892.png)

connectHandler：用来处理http connect请求，调整channel 的pipeline构造

serverSSLHandler：用来处理客户端与SDP网关之间的ssl握手

clientSSLHandler：用来处理SDP网关与服务器之间的ssl握手

## 3、问题？

性能（ring0->ring3拷贝，数据处理性能，两次ssl握手...）。



## 4、怎么实现认证和权限管理



