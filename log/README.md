### MEBIUSLOG 技术&使用文档

#### 概述
- 1.使用MEBIUSLog作为对外接口类，一般无需init,支持打印方法名，行号，当前线程
- 2.默认支持打印字符串，数组,如果需要支持json等更多复杂结构，可以在application的时候用MEBIUSLogManager init全局塞一个config,这个config可以定制json处理器
- 3.config可以是全局的，也可以每次都使用MEBIUSLog.log来设置一个局部有效的
- 4.支持用悬浮窗显示recyclewview来打印日志
- 5.支持日志保存在本地，并且可控制每个日志文件的最大大小，按时间排放，可以控制每个日志的最长存在时间

#### 结构UML
![MEBIUSLog整体架构图](doc/MEBIUSlog.png)

#### 使用
- 普通日志打印

```
//不传tag参数默认使用当前使用类的simpleclassname作为tag
SodaLog.d("content")
SodaLog.d("tag","content")
SodaLog.i("content")
SodaLog.e("tag","content")
```

- 列表数据打印

```
val arrayList1 = arrayListOf<String>()
arrayList1.add("你好")
arrayList1.add("吃饭了吗")
arrayList1.add("今天天气不是")
arrayList1.add("是的呢")
arrayList1.add("再见")
SodaLog.d("SodaLogSampleActivity", arrayList1)
```

- 实体类数据打印

```
val logBean = TestLogBean()
logBean.age = 18
logBean.name = "张三"
logBean.isGood = true
logBean.sex = 1
SodaLog.d("kkkkkkkkk", logBean)

```

- 打印带有调用栈的日志

```
SodaLog.log(object : SodaLogConfig() {
    override fun stackTraceDepth(): Int {
        return 5
    }
}, contents = "test1~~~~~~~")
```

- 初始化（非必须）

```
 SodaLogManager.init(object : SodaLogConfig() {
            override fun injectJsonParser(): JsonParser? {
                return object : JsonParser {
                    override fun toJson(src: Any): String {
                    //自定义日志打印序列化规则
                        return Gson().toJson(src)
                    }
                }
            }
        }, SodaFilePrinter().apply {
        //添加额外的printer处理日志
            init(application.cacheDir.absolutePath, 60 * 1000 * 60)
        })
```
