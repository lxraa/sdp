## 一、官方文档地址：
http://www.cipherdyne.org/fwknop/docs/fwknop-tutorial.html
## 二、单包授权的原理
单包授权是一个传输层的概念，即针对端口层的管理，与应用层协议无关。单包授权是使用UDP协议，为TCP端口创建一条【有时间限制】的【客户端到服务器】的【防火墙】白名单规则的过程。目前fwknop支持iptables和firewall-cmd两种linux自带的防火墙

## 三、安装fwknop-client

- 1、下载源代码（或rpm），下载地址：http://www.ciperdyne.org/fwknop/download
- 2、编译  
`
./configure --prefix=/usr --sysconfdir=/etc && make
`  
或使用rpm安装  
`
rpm -ivh xxx.rpm
`  
- 3、生成客户端的密钥，并保存在~/.fwknoprc。这一步支持两种模式：对称加密和非对称加密，这里以对称加密为例  
`
fwknop -A tcp/1234 -a 192.168.21.128 -D 192.168.21.132 --key-gen --use-hmac --save-rc-stanza
`  
生成的配置文件形如：  
```
[default]
[192.168.21.132]
KEY_BASE64                  jnj2fAYu4eR/Npp+LmElj4Tzw6ph9PHmb98vxCGb9+o=
HMAC_KEY_BASE64             OBoveH9PuOUcA2fc1g4xD5LoeRi1ZbZIYcShCWAvS0sXfeeTE+MT18K/iYFt3zmiqMpAxs79o748dJ65FhOwaQ==
ALLOW_IP                    192.168.21.128
ACCESS                      tcp/1234
SPA_SERVER                  192.168.21.132
USE_HMAC                    Y

```
- 4、使用生成的密钥发送敲门包（客户端配置完成后才能进行这一步）  
`
fwknop -n 192.168.21.128
`
##四、安装fwknop-server
- 1、下载源代码，下载地址：http://www.cipherdyne.org/fwknop/download/
- 2、编译 
`
./configure --prefix=/usr --sysconfdir=/etc && make
`
- 3、配置文件  
fwknopd需要两个配置文件：/etc/fwknop/access.conf和fwknopd.conf  
access.conf：配置了客户端的密钥，可以采用两种配置方式①对称加密②非对称加密。例：  
```
#### fwknopd access.conf stanzas ###

SOURCE                     ANY
OPEN_PORTS                 tcp/1234
REQUIRE_SOURCE_ADDRESS     Y
REQUIRE_USERNAME           root
FW_ACCESS_TIMEOUT          30
KEY_BASE64                 [客户端的key]
HMAC_KEY_BASE64            [客户端的hmac_key]

```
这里需要说明下，由于采用对称加密认证，所以服务器必须要知道客户端的key，即上述配置的KEY_BASE64和HMAC_KEY_BASE64  
fwknopd.conf：服务器本身的配置，如监听的网卡，监听的端口等。例：
```
PCAP_INTF                   ens37;
ENABLE_UDP_SERVER           Y;
UDPSERV_PORT                62201;
PCAP_FILTER                 udp port 62201;
ENABLE_SPA_PACKET_AGING     Y;
MAX_SPA_PACKET_AGE          60;
ENABLE_SPA_OVER_HTTP        N;
ENABLE_DESTINATION_RULE     N;
FLUSH_FIREWD_AT_INIT        Y;
FLUSH_FIREWD_AT_EXIT        Y;
ENABLE_FIREWD_FORWARDING    Y;
ENABLE_FIREWD_LOCAL_NAT     N;
ENABLE_FIREWD_OUTPUT        N;
FIREWD_INPUT_ACCESS         ACCEPT, filter, INPUT, 1, FWKNOP_INPUT, 1;
FIREWD_FORWARD_ACCESS       ACCEPT, filter, FORWARD, 1, FWKNOP_FORWARD, 1;
ENABLE_FIREWD_COMMENT_CHECK N;
FIREWALL_EXE                /usr/bin/firewall-cmd;
```
- 4、启动fwknopd  
`fwknopd -a /etc/fwknop/access.conf -c /etc/fwknop/fwknopd.conf`  
查看服务状态：  
`fwknopd --status`  
(进行完这一步，就可以使用fwknop客户端发送单包授权了)

## 四、这玩意有什么问题

一大堆客户端使用同一个出口IP的时候如何做控制？（未测试）

