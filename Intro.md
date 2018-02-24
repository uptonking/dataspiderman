# webmagic 爬虫框简介

## 特色
- 简单易用，支持无配置用POJO+注解爬虫
- 模块化设计，可扩展性强
- 支持爬取js动态渲染的页面

## 总体架构
#### WebMagic的四个组件
1. Downloader  
Downloader负责从互联网上下载页面，以便后续处理。   
WebMagic默认使用了Apache HttpClient作为下载工具。

2. PageProcessor  
PageProcessor负责解析页面，抽取有用信息，以及发现新的链接。WebMagic使用Jsoup作为HTML解析工具，并基于其开发了解析XPath的工具Xsoup。    
在这四个组件中，PageProcessor对于每个站点每个页面都不一样，是需要使用者定制的部分。

3. Scheduler  
Scheduler负责管理待抓取的URL，以及一些去重的工作。  
WebMagic默认提供了JDK的内存队列来管理URL，并用集合来进行去重。也支持使用Redis进行分布式管理。
除非项目有一些特殊的分布式需求，否则无需自己定制Scheduler。

4. Pipeline    
Pipeline负责抽取结果的处理，包括计算、持久化到文件、数据库等。  
WebMagic默认提供了“输出到控制台”和“保存到文件”两种结果处理方案。  
Pipeline定义了结果保存的方式，如果你要保存到指定数据库，则需要编写对应的Pipeline。对于一类需求一般只需编写一个Pipeline。  

#### Spider  
Spider是WebMagic操作的入口，它封装了爬虫的创建、启动、停止、多线程等功能。  
Downloader、PageProcessor、Scheduler、Pipeline都是Spider的一个属性，这些属性是可以自由设置的，通过设置这个属性可以实现不同的功能。

