# NiceDoc
快速、高效、优雅地利用word、excel模板，根据相关标识自动生成相应格式的优美文档工具。

# 示例
## word模板
resources/Template/test.docx 示例模板
目前只支持docx格式的word模板导出，模板格式如下：
![Image text](images/tem.png)
## 示例代码
        //测试示例模板
        String path = Main.class.getClassLoader().getResource("Template").getPath() + "/";
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        NiceDoc docx = new NiceDoc(path + "test.docx");

        Map<String, Object> labels = new HashMap<>();
        //值标签
        labels.put("startTime", "1881年9月25日");
        labels.put("endTime", "1936年10月19日");
        labels.put("title", "精选作品目录");
        labels.put("press", "鲁迅同学出版社");

        //枚举标签
        labels.put("likeBook", 2);

        //布尔标签
        labels.put("isQ", true);
        labels.put("isNew", false);
        docx.pushLabels(labels);

        //表格
        List<Map<String, Object>> books = new ArrayList<>();
        Map<String, Object> book1 = new HashMap<>();
        book1.put("name", "汉文学史纲要");
        book1.put("time", "1938年，鲁迅全集出版社");
        books.add(book1);
        Map<String, Object> book2 = new HashMap<>();
        book2.put("name", "中国小说史略");
        book2.put("time", "1923年12月，上册；1924年6月，下册");
        books.add(book2);
        docx.pushTable("books", books);

        //生成文档
        docx.save(path, UUID.randomUUID() + ".docx");
## 生成文档
![Image text](images/res.png)
# NiceDoc 目前支持相关标签说明
## 标签填充
#### 格式
{{label}} 
#### 说明
标签用双大款号，标签名称与实体类（或 Map）的关键字必须大小写一至即可填充。标签样式（字体，颜色，字号等）都会保留。
</br>支持填充正文内容、面眉、页脚、及表格里面的标签。
## 表格填充
#### 格式
表头必须添加 {{table:books}} ，books为表的名称数据生成后，该行会自动删除
</br>表行数据 {{col:name}} ，name为数据表列名
#### 说明
表格生成，会保留表格里的相关格式，行会根据相关数据数量自动增加。

## 枚举标签填充
#### 格式
{{label#[1:《彷徨》,2:《花边文学》,3:《野草》]}}
#### 说明
标签用双大款号，标签名称与实体类（或 Map）的关键字必须大小写一至即可填充。
[]为枚举数组，“1：《彷徨》” 1为该枚举值，:号后“《彷徨》”为枚举的别名，即填充值。

## 布尔标签填充
#### 格式
{{label#√:□}}
#### 说明
标签用双大款号，标签值必须为true或false，井号后是对应true：false的填充值。




