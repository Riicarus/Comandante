# Comandante
>  version: 2.1   

## 概述
Comandante 是一个基于 Java 的命令行插件, 用于提供便捷的指令注册、解析以及执行的功能.  
Comandante 同时支持自定义多线程处理输入指令.  
对于一些错误的指令, Comandante 还可以给出一些相关的建议.  

## 基础
### 指令介绍
#### 指令组成
一般的, 指令由三个部分组成: `exe`, `opt`, `arg`. 
- `exe` 表示一个可运行的服务(或程序);   
- `opt` 表示操作可选的固定参数,  
  必须具有以 `--` 开头的**长指令**, 也可以选择提供以 `-` 开头的**短指令**.  
- `arg` 表示需要输入的参数.  

> 例1:  
> - `app --color/-c color_name`
> - `app echo message`

#### 指令结构设计:
- 指令树有一个根节点, 没有实际意义, 只保存指令的第一个 `exe` 节点, 我们称这类 `exe` 节点为**主指令节点**.  
- 指令树主干由 `exe` 节点和 `arg` 节点组成.  
- 分支节点由 `opt` 及其后续 `arg` 节点组成. 
- `opt` 节点只能有至多一个 `arg` 子节点.
- `opt` 节点之后的 `arg` 子节点的子节点只能是 `arg`.    
- `exe` 节点只能有至多一个 `arg` 子节点.
- `exe` 节点之后的 `arg` 子节点的子节点可以是除 `opt` 节点外任意类型的节点.
- `arg` 节点之后不能注册任何 `opt` 节点. 所有的 `opt` 节点都会被注册到当前的 `exe` 节点下.  

> 例2:  
> 在指令 `app --color color_name --font font_main font_next echo message` 中, 主指令节点为 `app`, 指令分支节点为 `--color color_name` 和 `--font font_main font_next`, 去掉指令分支节点, 剩下的就是主干节点 `app echo message`.  


#### 指令语义:  
- 我们将**注册了指令执行器的节点**称为**语义节点**.  
- 指令树主干上的每一个节点都可以是语义节点.  
- 一个`opt`分支都**有且只有一个**语义节点, 为其分支的**尾节点**.
- 指令解析时, 会解析所有`opt`的语义, 并将其执行, 但是只会解析当前指令对应指令树主干节点上的最后一个节点, 如果它有语义, 就执行.  

> 例3:  
> 在例2的指令中, 两个分支节点都具有语义, 会被最终执行; 指令对应指令树主干结点的部分可能有不止一个语义, 但是我们只关系尾节点 `message` 是否有语义, 如果有, 就也会被执行.  

#### 短指令合并:  
这是一个指令语法糖, 用于将指令中没有参数的 `opt` 短指令合并到一起输入, 更加便捷.  

> 例4:  
> 如果几个连续的短指令写在一起, 可以省略前面的 `-`, 然后将它们合并到一个短指令中.  
> 如:  
> `plugin list -i -a -d dir` 可以写为 `plugin list -iad dir`  
> *插件会自动将它们解析为没有缩写的状态.*

#### 参数传递规则
由于内部节点解析逻辑优先级不同, 如果参数值和当前同层级的 `exe` 节点名称相同, 会优先将其解析为 `exe` 节点.  
为避免这个问题, 在可能发生冲突的参数传递时, 可以使用 `'` 将参数括起来, 这样就能保证指令被正确解析为参数.  

同样, 传入指令字符串的节点分割操作是依据 ` `(空格) 来完成的, 如果参数中包含 ` `, 会导致指令解析错误. 我们同样可以使用 `'` 包围参数, 来避免发生错误.  

如果想要在参数中输入 `'`, 而不是作为参数括符使用, 需要在 `'` 前面加上 `\` 来表示它是一个普通的字符串.   

> 例5:  
> 在例2指令的基础上, 如果我们还有 `app echo time --format format_str` 指令, 这时在 `time` 节点上就可能发生解析冲突.  
> 使用 `'` 包围参数来解决这个冲突:  
> `app echo 'time'`  
> 其他参数传递避免冲突的用法:  
> `app echo 'hello, I've said \'hello\' to you'`

### 简单使用
定义一个指令定义类, 包含一个静态的方法, 推荐命名为 `defineCommand()`.  
```java
public class DemoCommand extends BaseCommand {

    public static void defineCommand() {

    }
}
```
然后在对应方法中注册指令:  
```java
public class DemoCommand {
      // ...

    public void defineCommand() {
        CommandLauncher.register().exe("comandante")
                .opt("version", "v")
                .executor(
                        (args) -> CommandCommandLogger.log(CommandConfig.getVersion()),
                        "查看 Comandante 版本号"
                );

        CommandLauncher.register()
                .exe("app")
                .opt("color", "c")
                .arg("color", new StringCommandArgumentType())
                .executor(
                        context -> CommandCommandLogger.log("set app color to "
                                + ((HashMap<String, String>) context.getData("color")).get("color"))
                );
    }
}
```
启动指令服务:  
```java
public class TestMain {

    public static void main(String[] args) {
        // 这里会真正将指令注册进指令树中
        DemoCommand.defineCommand();
        // 启动指令解析执行进程
        CommandLauncher.enable();
    }
}
```
至此, 就可以使用命令插件的基础服务了.  

## 扩展
### 执行器
主要是执行器参数的获取问题:  
执行器方法在调用时会被传入一个 `CommandContext` 对象, 其中的 `data` 属性保存了指令解析/执行过程中产生的所有参数.  

#### 指令传入参数
对于指令中传入参数的获取, 主要有两种:  
1. 属于 `opt` 节点的参数:
   ```java
   CommandLauncher.register()
        .exe("app")
        .opt("font", "f")
        .arg("font_main", new StringCommandArgumentType())
        .arg("font_next", new StringCommandArgumentType())
        .executor(
                context -> CommandLogger.log("set app font to "
                        + ((HashMap<String, String>) context.getData("font")).get("font_main")
                        + "/"
                        + ((HashMap<String, String>) context.getData("font")).get("font_next"))
        );
   ```
2. 属于 `exe` 节点的参数:  
   ```java
   CommandLauncher.register()
        .exe("app")
        .exe("echo")
        .arg("message", new StringCommandArgumentType())
        .executor(
                context -> CommandLogger.log("app echo: " + context.getData("echo" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "message"))
        );

   // 多个相连的参数获取 
   CommandLauncher.register()
        .exe("app")
        .exe("echo")
        .exe("move")
        .arg("from", new StringCommandArgumentType())
        .arg("to", new StringCommandArgumentType())
        .executor(
                context -> CommandLogger.log("app echo: move from " +
                        context.getData("move" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "from")
                + " to "
                + context.getData("from" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "to"))
        ); 
   ```

#### 指令执行产生参数
在指令执行过程中, 可能会有多个具有语义的部分被执行, 同时产生一些参数, 而后续执行的执行器需要前面产生的参数. 这就引出了一些需要注意的点:  
1. 有参数传递顺序的语义对应的指令要按顺序输入.  
2. 需要将产生的参数放入 `CommandContext.data` 中, 提供给之后的执行器使用.
3. 执行器从 `CommandContext.data` 中获取参数.  

```java
// 向 CommandContext.data 放入参数
CommandContext#putData(String key, Object value);
// 从 CommandContext.data 取出参数
CommandContext#getData(String key);
```

### IO 扩展
#### 指令输入
Comandante 内置了完善的输入输出机制, 都可以通过 `CommandLauncher` 提供的 API 调用实现.  
指令输入的接口是 `CommandLauncher.dispatchToCache(String)` 方法, 使用该方法接收外界传入的指令字符串, 并由指令处理线程进行后续分发和执行处理.  
用户可以自行定义指令的输入方式, 无论是从文件中读取进行批处理还是从控制台输入, 只需要构建好获取指令的逻辑, 然后将获取到的指令通过 `CommandLauncher.dispatchToCache(String)` 方法传给指令插件即可.  
指令输入支持多线程, `CommandInputHandler` 内部通过生产者消费者模式进行输出指令处理.  

#### 重定向输出
Comandante 同时支持输出重定向, 可以重定向插件内部所有输出到任意的输出流中.  
该功能由 `CommandLauncher.redirectOutput(OutputStream)` 方法提供, 该方法会设置全局日志输出工具类 `CommandLogger` 的输出流, 来达到全局重定向输出的目的.  
同时, 该接口支持设置输出流的字符集, 只需要调用 `CommandLauncher.redirectOutut(OutputStream, StandardCharsets)` 方法进行输出重定向即可.  
  
同样的, 我们可以调用 `CommandLauncher.setLogFile(String path)` 方法来设置将日志输出到某个流或者文件中, 这个方法也支持设置字符集. 设置成功后, 所有经过  `CommandLogger.log(String)` 方法的输出, 都会被输出到对应的日志流中.  

> 由上可以看出, 指令插件所有的输出都是调用 `CommandLogger.log(String)` 方法进行的.  
> 所以如果想要完美实现重定向输出的功能, 需要在自定义指令或其他配置中都是用该方法进行输出.  
> 推荐将日志输出流设置为某一个文件, 用于保存运行时的日志.  

## 内置指令
目前 Comandante 有如下内置指令:  
```bash
comandante --help/h      查看指令帮助
comandante --author/a    查看 Comandante 作者
comandante --doc/d       查看 Comandante 文档
comandante --version/v   查看 Comandante 版本号
comandante --info/i      查看 Comandante 信息
comandante list --all    列出所有已注册指令
```
对所有已加载指令(可以是自定义的指令), 在其 `exe` 节点上, 我们都为其装配了对应的 help 指令, 如:  
```bash
comandante --help/-h
app --help/-h
```
help 指令会输出该指令节点下所有的指令搭配, 其中 `option` 节点加上 `--/-` 进行长短指令使用的提示, `argument` 节点会在其后面加上 `(type)` 来描述参数的类型; 如果在注册时设置了执行器的 `usage` 属性, 会在指令后方加入对应的描述, 如:  
```bash
app -h
Command Echo: app -h
Format: exe --opt/-o arg
app --help/h 查看指令帮助
app --color/c color(string) 
app --font/f font_main(string) font_next(string) 
app name(string) 
app echo time hello(string) 
app echo message(string) 

comandante -h
Command Echo: comandante -h
Format: exe --opt/-o arg
comandante --help/h 查看指令帮助
comandante --author/a 查看 Comandante 作者
comandante --doc/d 查看 Comandante 文档
comandante --version/v 查看 Comandante 版本号
comandante --info/i 查看 Comandante 信息
comandante list --all 列出所有已注册指令
```

## API
### 注册指令
```java
// 获取指令构建器
CommandBuilder commandBuider = CommandLauncher.register();
// 注册 ExecutionCommand 节点
CommandBuilder#exe(String);
// 注册 OptionCommand 节点
CommandBuilder#opt(String, String);
// 注册 ArgumentCommand 节点
CommandBuilder#arg(String, CommandArgumentType<T>);
// 注册指令执行器, 可设置 usage
CommandBuilder#executor(CommandExecutor);
CommandBuilder#executor(CommandExecutor, String);
```
### 启动/停止指令处理线程
```java
// 启动
CommandLauncher.enable();
// 停止
CommandLauncher.disable();
```
### IO
```java
// 重定向输出
CommandLauncher.redirectOutput(OutputStream);
CommandLauncher.redirectOutput(OutputStream, StandardCharsets);
// 设置日志文件输出 文件路径必须为绝对路径
CommandLauncher.setLogFile(String);
CommandLauncher.setLogFile(String, StandardCharsets);
// 指令输入
CommandLauncher.dispatchToCache(String);
// 全局输出
CommandLogger.log();
// 关闭输出流和日志输出流, 如果是默认输出流请不要关闭
CommandLogger.close()
```
