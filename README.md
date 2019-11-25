# Java版本简单数据库
原文C语言版：[Let’s Build a Simple Database](https://cstack.github.io/db_tutorial/)

github代码：[Java版本简单数据库](https://github.com/ccran/SimpleDatabase)

## 1. 交互式Shell
第一讲主要是如何建立一个交互式的Shell（类似于Windows命令行窗口）

整个程序流程和代码都很简单：

- 整个程序为一个死循环

- 首先打印提示字符串**db >**
- 阻塞直到用户输入命令
  - 输入的是 **\.exit** 则退出整个程序
  - 否则输出无法识别命令

## 2. SQL编译器和虚拟机

第二讲主要是简单实现了SQL编译器和虚拟机

> SQL编译器：将用户输入的命令编译成虚拟机能看懂的命令格式
>
> 虚拟机：解释和执行命令

区分用户输入字符串为两类命令（SQL编译器）：

- 元命令（用于操作虚拟机；如 **\.exit** 进行退出）
- SQL语句（用于数据的CRUD；如**select**进行查询）

执行元命令和SQL语句（虚拟机）：

- **doMetaCommand**执行元命令
- **executeStatement**执行SQL语句

## 3. 内存实现数据库存储

第三讲核心为：

1. 对象的序列化与反序列化
2. 实现了一个简单的表结构以及表的插入查询操作

数据库特点：

- 支持两种操作：插入一行和输出所有行
- 存放在内存中
- 支持单一硬编码的表

### Row序列化与反序列化

- 序列化：Row转换为byte[]
- 反序列化：byte[]转换为Row

> 保证数据固定字节是为了方便存取

```
Row{id=1, userName='abc', email='bcd'}
// 4个字节为id,32个字节为userName,255个字节为email，总共291字节存储一行记录
[1, 0, 0, 0, 97, 98, 99, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
0, 0, 0, 0, 0, 0, 0, 0, 98, 99, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
Row{id=1, userName='abc', email='bcd'}
```

### Table类的封装

```java
public class Table {
    public static int PAGE_SIZE = 4096;//一页的大小
    public static int TABLE_MAX_PAGES = 100;//表的最大页
    public static int ROWS_PER_PAGE = PAGE_SIZE / Row.ROW_SIZE; //一页可以存放的Row行数
    public static int TABLE_MAX_ROWS = ROWS_PER_PAGE * TABLE_MAX_PAGES;//表可以存放的最多行数

    private int numRows;//目前行数
    private Page[] pages;//页
    class Page {
        byte[] content;
        Page() {
            content = new byte[PAGE_SIZE];
        }
    }
}
```

插入方法**executeInsert**：

1. 首先找到插入行的某页，该页没有则创建
2. 找到需要插入行在页内的字节偏移
3. 序列化Row为byte[]并且赋值给页

```java
/**
     * 插入一行
     *
     * @param statement
     * @return
     */
    public ExecuteResult executeInsert(Statement statement) {
        // 满了
        if (numRows >= TABLE_MAX_ROWS)
            return ExecuteResult.EXECUTE_TABLE_FULL;
        // 序列化
        byte[] serializeBytes = Util.serializeRow(statement.getRowToInsert());
        int page_num = numRows / ROWS_PER_PAGE;//找到页数
        if (pages[page_num] == null) {//没有页则创建页
            pages[page_num] = new Page();
        }
        // 找到字节偏移
        int byte_offset = (numRows % ROWS_PER_PAGE) * Row.ROW_SIZE;
        // 填充数据
        for (int i = 0; i < Row.ROW_SIZE; i++) {
            pages[page_num].content[byte_offset + i] = serializeBytes[i];
        }
        //System.out.println(Arrays.toString(pages[page_num].content));
        // 行数+1
        numRows++;
        return ExecuteResult.EXECUTE_SUCCESS;
    }
```

查询方法**executeSelect**：

1. 遍历每一行，获取每一行的页号以及页内的字节偏移
2. 根据Row字节数组大小反序列为Row对象

```java
/**
     * 查询所有内容
     *
     * @return
     */
    public ExecuteResult executeSelect() {
        for (int i = 0; i < numRows; i++) {
            byte[] deserializeBytes = new byte[Row.ROW_SIZE];
            int page_num = i / ROWS_PER_PAGE;//找到页数
            int byte_offset = (i % ROWS_PER_PAGE) * Row.ROW_SIZE;// 找到字节偏移
            for (int j = 0; j < Row.ROW_SIZE; j++) {//赋值
                deserializeBytes[j] = pages[page_num].content[byte_offset + j];
            }
            System.out.println(Util.deserializeRow(deserializeBytes));//输出对象
        }
        return ExecuteResult.EXECUTE_SUCCESS;
    }
```

结果演示：

![结果](https://github.com/ccran/SimpleDatabase/blob/master/pic/part3.png)

## 4. 代码测试

这部分主要是对之前写好代码的测试。

作者主要做了以下几件事：

1. 通过RSpec测试代码，类似于JUnit
2. 测试用户名、邮箱发现有bug；用户名通过**char username[32]**存储，因为c语言的字符串必须以'\0'结尾，所以只能存31个字符；邮箱类似
3. 修复以上bug，判别输入的用户名、邮箱的字符串长度是否大于限定长度
4. 修复ID为负数的bug；

> 贴出prepareStatement代码

```java
public static PrepareResult prepareStatement(String input, Statement statement) {
        // 插入语句
        if (input.startsWith("insert")) {
            statement.setType(Statement.StatementType.STATEMENT_INSERT);
            // 获取插入行
            Row row = new Row();
            // 严格根据空格分隔
            String[] sepInput = input.split(" ");
            if (sepInput.length != 4) {
                return PrepareResult.PREPARE_SYNTAX_ERROR;
            }
            // id转换
            try {
                row.setId(Integer.parseInt(sepInput[1]));
            } catch (NumberFormatException e) {
                return PrepareResult.PREPARE_SYNTAX_ERROR;
            }
            // 判别ID是否为负数
            if(row.getId()<0){
                return PrepareResult.PREPARE_NEGATIVE_ID;
            }
            // 判别长度是否大于限定长度
            if (sepInput[2].length() > Row.USERNAME_SIZE || sepInput[3].length() > Row.EMAIL_SIZE) {
                return PrepareResult.PREPARE_STRING_TOO_LONG;
            }
            row.setUserName(sepInput[2]);
            row.setEmail(sepInput[3]);
            // 设置给statement
            statement.setRowToInsert(row);
            return PrepareResult.PREPARE_SUCCESS;
        } else if (input.startsWith("select")) {
            statement.setType(Statement.StatementType.STATEMENT_SELECT);
            return PrepareResult.PREPARE_SUCCESS;
        }
        return PrepareResult.PREPARE_UNRECOGNIZED_STATEMENT;
    }
```

