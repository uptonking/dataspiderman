# webmagic 爬虫框架简介
详细内容请见gitbook [webmagic in action](http://webmagic.io/docs/zh/)

## 特点
- 简单易用，支持无配置基于 `POJO + 注解` 爬虫
- 模块化设计，可扩展性强
- 支持爬取js动态渲染的页面

## 总体架构

#### WebMagic的四个组件

1. Scheduler  
Scheduler负责管理待爬取的URL，以及一些去重的工作。  
WebMagic默认使用JDK的 `LinkedBlockingQueue / PriorityBlockingQueue` 队列来管理URL，并用集合来进行去重，也支持使用Redis进行分布式管理。  
除非项目有一些特殊的分布式需求，否则无需自己定制Scheduler。

2. Downloader  
Downloader负责从互联网上下载页面，以便后续处理。   
WebMagic默认使用了Apache HttpClient作为下载工具。

3. PageProcessor  
PageProcessor负责解析页面，抽取有用信息，以及发现新的链接。  
WebMagic使用Jsoup作为HTML解析工具，并基于其开发了解析XPath的工具Xsoup。    
在这四个组件中，PageProcessor对于每个站点每个页面都不一样，是需要使用者定制的部分。

4. Pipeline    
Pipeline负责抽取结果的处理，包括计算、持久化到文件、数据库等。  
WebMagic默认提供了输出到控制台和文件的 `ConsolePipeline` ,  `FilePipeline` 两种处理方案。  
Pipeline定义了结果保存的方式，如果你要保存到指定数据库，则需要编写对应的Pipeline。对于一类需求一般只需编写一个Pipeline。  

#### Spider 爬虫入口类  
Spider是WebMagic操作的入口，它封装了爬虫的创建、启动、停止、多线程等功能。    
Downloader、PageProcessor、Scheduler、Pipeline都是Spider的一个属性，这些属性是可以自由设置的，通过设置这个属性可以实现不同的功能。

#### 爬虫常用数据模型

- Request  
Request是对URL地址的一层封装，一个Request对应一个URL地址。  
它是PageProcessor与Downloader交互的载体，也是PageProcessor控制Downloader唯一方式。  
除了URL本身外，它还包含一个Key-Value结构的字段extra，可以在extra中保存一些特殊的属性，然后在其他地方读取，以完成不同的功能。例如附加上一个页面的一些信息等。

- Site  
Site是爬取站点时使用的http连接信息Java Bean，包括domain, userAgent, cookie, charset以及抓取间隔、超时时间、重试次数等信息。  
一个站点的Spider、Downloader、PageProcessor对应一个Site。

- Page  
Page代表了从Downloader下载到的一个页面 - 可能是HTML，也可能是JSON或者其他文本格式的内容。  
Page是WebMagic抽取过程的核心对象，它提供一些方法可供抽取、结果保存等。

- ResultItems   
ResultItems相当于一个Map，它保存PageProcessor处理的结果，供Pipeline使用。   
它的API与Map很类似，值得注意的是它有一个字段skip，若设置为true，则不会被Pipeline处理。

- PageModel  
In webmagic, we call a POJO containing extract result as "page model".

## 使用说明
使用webmagic实现一个基本的爬虫只需要编写一个类，实现PageProcessor接口即可，这个类基本上包含了抓取一个网站你需要写的所有代码。  

- 注解模式的开发方式
    1. 首先定义你需要抽取的数据，并编写类。
    2. 在类上写明@TargetUrl注解，定义对哪些URL进行下载和抽取。
    3. 在类的字段上加上@ExtractBy注解，定义这个字段使用什么方式进行抽取。
    4. 定义结果的存储方式。

注解模式的入口是OOSpider，它继承了Spider类，提供了特殊的创建方法，其他的方法是类似的。  
创建一个注解模式的爬虫需要一个或者多个Model类，以及一个或者多个PageModelPipeline——定义处理结果的方式。

注解模式其实是完全基于webmagic-core中的PageProcessor和Pipeline扩展实现的。
  
- HelpUrl和TargetUrl
HelpUrl/TargetUrl是一个非常有效的爬虫开发模式，TargetUrl是我们最终要抓取的URL，最终想要的数据都来自这里；  
而HelpUrl则是为了发现这个最终URL，我们需要访问的页面。    
几乎所有垂直爬虫的需求，都可以归结为对这两类URL的处理：
    - 对于博客页，HelpUrl是列表页，TargetUrl是文章页。
    - 对于论坛，HelpUrl是帖子列表，TargetUrl是帖子详情。
    - 对于电商网站，HelpUrl是分类列表，TargetUrl是商品详情。

- WebMagic自己定制的适合URL的正则表达式，主要由两点改动：  
    - 将URL中常用的字符.默认做了转义，变成了\.
    - 将"*"替换成了".*"，直接使用可表示通配符。

在WebMagic中，从TargetUrl页面得到的URL，只要符合TargetUrl的格式，也是会被下载的。  
所以即使不指定HelpUrl也是可以的——例如某些博客页总会有“下一篇”链接，这种情况下无需指定HelpUrl。

TargetUrl还支持定义sourceRegion，这个参数是一个XPath表达式，指定了这个URL从哪里得到——不在sourceRegion的URL不会被抽取。

## 页面元素抽取方法

提供了4种方法抽取页面中的元素

1. XPath  基于xsoup实现
XPath本来是用于XML中获取元素的一种查询语言，但是用于Html也是比较方便的。

2. CSS选择器  基于jsoup实现
CSS选择器是与XPath类似的语言。如果大家做过前端开发，肯定知道$('h1.entry-title')这种写法的含义。  
客观的说，它比XPath写起来要简单一些，但是如果写复杂一点的抽取规则，就相对要麻烦一点。

3. 正则表达式  
正则表达式则是一种通用的文本抽取语言。

4. JsonPath    
对于JSON格式的内容，可使用JsonPath进行解析。  
JsonPath是于XPath很类似的一个语言，它用于从Json中快速定位一条内容。


