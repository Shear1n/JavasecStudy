## Java动态代理

参考：https://www.bilibili.com/video/BV16h411z7o9?p=3&vd_source=2127caee528de7abb187fc1db5af45e7



中间过程主要注意`Proxy.newProxyInstance`



漏洞联想：

类比反序列化，如果一个方法readObject内容可控，容易产生反序列化漏洞，会自动执行对应代码。

动态代理类中的invoke也是如此，假设A方法调用了B，B方法中利用了动态代理，而且动态代理加载的C对象存在readObject可控点，同样会自动执行，造成漏洞产生。



结合实际中的漏洞具体分析，这里利用到动态代理产生的漏洞最常见的就是CC和jdk7u21了。

### Jdk7u21反序列化漏洞

<<<<<<< HEAD
=======


<<<<<<< HEAD
>>>>>>> f97fc8c (Update README.md)
=======
>>>>>>> main
