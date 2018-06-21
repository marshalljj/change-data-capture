# change-data-capture
## introduction
此项目主要用来将sql中的数据通过binlog的方式同步到es中。
比如：sql中存在如下2张表
- 用户

```json
{
    "user_name":"aa",//用户姓名
    "user_id":1234,
    "tag":"tag1,tag2" //标签
}
```
- 订单 

```json
{         
    "user_id":1234,
    "create_time":"2018-04-11T16:24:13.000",
    "id":2476,//订单id
    "status":107 //状态
}
```


用户和订单是一对多的关系，假设需要进行如下搜索：
1. 根据用户姓名，身份证搜索用户
2. 搜索包含某一状态的订单的用户（多选）
3. 搜索某一笔订单的用户
4. 搜索符合某一标签的订单（多选）

这种情况下我们可以通过在es中构造如下宽表结构来解决

```json
{
   "user_name":"aa",
    "user_id":1234,
    "tag":["tag1","tag2"],
    "status":107, 
    "order":[
        {       
            "id":2478,
            "status":107,
            "user_id":1234,
            "create_time":"2018-04-11T16:24:13.000"		
        },
        {    
            "id":2476,
            "status":108,   
            "user_id":1234,
            "create_time":"2018-04-11T16:24:13.000"
        }
    ]
}
```
如何将sql中的数据以如上结构同步到es？
change-data-captrue可以让你用少量的配置达到此目的。
- 同步方式：binlog实时同步+定时任务增量补偿



## quick start
1.引入启用binlog

在任意Configuration类中增加`@EnableBinlog`

2.编辑xml配置文件

```xml
<?xml version = "1.0"?>
<processors>
    <processor name="user">
        <es>
            <type>user</type>
        </es>
        <!--监听表user, 主键名id, rootKey=id, 更新时间 modify_time-->
        <change-source>
            <table>user</table>
            <primary-key>id</primary-key>
            <update-time-field>modify_time</update-time-field>
        </change-source>
        
        <root-key>id</root-key>
        <!--构造数据结构-->
        <node>
            <name>user</name>
            <!--user数据-->
            <sql>select * from user where id=:id</sql>
            <multi>false</multi>
            <node>
                <name>order</name>
                <!--user下的订单-->
                <sql>select * from orders where user_id=:id</sql>
                <!--一个user多笔订单-->
                <multi>true</multi>
            </node>
        </node>
    </processor>

</processors>
```
- 根据上面的node-tree, 组件会将`select * from user where id=:id`作为root对象的属性;并且将`select * from orders where user_id=:id`作为 root子对象，名为order,类型为数组
- `:name`是占位符，代表user表的一列
- root-key是聚合对象对id,也是es的doc_id。

3.配置properties:

```
#es
binlog.elasticsearch.cluster-name=xxx
binlog.elasticsearch.cluster-nodes=xxx.xxx.xxx.xx1:9300,xxx.xxx.xxx.xx1:9300
binlog.elasticsearch.properties.shield.user=username:password
binlog.elasticsearch.index=xxx
#config-file
binlog.factory.config-file=processor.xml
#kafka
binlog.kafka.bootstrap-servers=xxx.xxx.xxx.xxx:9092
binlog.kafka.group-id=xxx
binlog.kafka.topic=xxx

#ds
binlog.datasource.url=xxx
binlog.datasource.username=xxx
binlog.datasource.password=xxx
binlog.datasource.driver=xxx

```

4.配置定时任务

```java
@Component
@Slf4j
public class RowScannerExecutor {

    public static final String PROCESSOR_NAME = "case";
    @Autowired
    private ProcessorFactory processorFactory;

    @Scheduled(cron="")
    public boolean execute() {
        TaskProcessor taskProcessor = processorFactory.getTaskProcessor(PROCESSOR_NAME).orElseThrow(
            () -> new IllegalStateException("processor not state of " + PROCESSOR_NAME)
        );
        try {
            log.info("binlog补偿任务开始");
            List<Map<String, Object>> rawRows = getRows();
            List<Row> rows = rawRows.stream().map(MapRow::new).collect(Collectors.toList());
            taskProcessor.scan(rows);
            log.info("binlog补偿任务完成");
            return true;
        } catch (Exception e) {
            log.error("binlog补偿任务执行中断",e);
            return false;
        }

    }
}
```

## 高级功能
### 支持对指定表的指定字段做自定义转换处理
如上例子，我们希望对user中的tag做进一步解析（目前是字符串），使之成为数组

```json
转变前
{
    "user_name":"aa",//用户姓名
    "user_id":1234,
    "tag":"tag1,tag2"
}
转变后
{
    "user_name":"aa",//用户姓名
    "user_id":1234,
    "tag":["tag1","tag2"]
}
```

步骤如下：
在xml文件`<field>`中指定convert名字(converter)与sql中的列名(name)。
```xml
<?xml version = "1.0"?>
<processors>
    <processor name="user">
        <es>
            <type>user</type>
        </es>
        <!--监听表user, 主键名id, rootKey=id, 更新时间 modify_time-->
        <change-source>
            <table>user</table>
            <primary-key>id</primary-key>
            <update-time-field>modify_time</update-time-field>
        </change-source>
        <root-key>id</root-key>
        <!--构造数据结构-->
        <node>
            <name>user</name>
            <!--案件数据-->
            <sql>select * from user where id=:id</sql>
            <fields>
            	<field converter="stringSplitter" name="tag"/>
            </fields>
            <multi>false</multi>
            <node>
                <name>order</name>
                <!--案件下的订单-->
                <sql>select * from orders where user_id=:id</sql>
                <!--一个案件下多笔订单-->
                <multi>true</multi>
            </node>
        </node>
    </processor>

</processors>

```
目前支持的converter有"json2map","json2stringArray".

## 注意
此组件只处理update, insert类型的binlog变更，不处理delete类型(主要因为无法做补偿)
