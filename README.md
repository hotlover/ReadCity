# ReadCity
获取全国省市区信息存入sqlite数据库

### 结果和资源在asset里

[最新县及县以上行政区划代码（截止2014年10月31日）](http://www.stats.gov.cn/tjsj/tjbz/xzqhdm/201504/t20150415_712722.html)


###代码说明

> Main.java里没有对市管辖，县进行过滤，CustomMain.java将直辖市也算为城市(level1)，直属与市和省的，parent直接就是省。  
城市一共分为三级，0,1,2，省，城市，区
