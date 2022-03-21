# NiceDoc
快速、高效、优雅地利用word、excel模板，根据相关标识自动生成相应格式的优美文档工具。

# 示例
## word模板
resources/Template/test.docx 示例模板
目前只支持docx格式的word模板导出，模板格式如下：
![Image text](images/tem.png)
## 示例代码
        //打开模板
        String path = URLDecoder.decode(Main.class.getClassLoader().getResource("Template").getPath(), "UTF-8"); + "/";
        NiceDoc docx = new NiceDoc(path + "test.docx");
        
        //标签填充
        Map<String, Object> labels = new HashMap<>();
        labels.put("startTime", "1881年9月25日");
        labels.put("endTime", "1936年10月19日");
        labels.put("title", "精选作品目录");
        labels.put("press", "鲁迅同学出版社");
        docx.pushLabels(labels);

        //表格填充
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
### 格式
{{label}} 
### 说明
标签用双大款号，标签名称与实体类（或 Map）的关键字必须大小写一至即可填充。标签样式（字体，颜色，字号等）都会保留。
支持填充正文内容、面眉、页脚、及表格里面的标签。
## 表格填充
### 格式
表头必须添加 {{table:books}} ，books为表的名称数据生成后，该行会自动删除
表行数据 {{col:name}} ，name为数据表列名
### 说明
表格生成，会保留表格里的相关格式，行会根据相关数据数量自动增加。



