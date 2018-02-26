# Roadmap for data spiderman

## Target
1. data crawler 
2. support multi export

## todo 
- xpath匹配allText()取出的文字以逗号连接，方便生成标签
- xpath匹配text()取出的文字添加截取前N个字符的功能  
方便生成摘要，也可以取二级标题生成摘要
- 支持图片下载
- 支持文件下载
- 持久化到jdbc数据库的pipeline
- 采用字节码编程，动态生产spider bean
- 登陆相关模块
- spider ui
- 自定义数据格式化方法
- ResultItems支持二进制流

## Discuss
- 爬虫的顺序控制

## FAQ
- 如何获取TargetUrl的url及url中的部分路径
- 取出a标签的href 相对路径补充绝对路径
- @ExtractBy 直接指定值，用于测试
- css直接抽取文本，不是outerHTML
- 抓取Css属性的内容
