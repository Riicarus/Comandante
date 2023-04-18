# Comandante

> version: 3.0

## 概述

Comandante 是一个基于 Java 的命令行插件, 用于提供便捷的指令注册、解析以及执行的功能.  
Comandante 同时支持自定义多线程处理输入指令.  
对于一些错误的指令, Comandante 还可以给出一些相关的建议.  

新版 Comandante 的指令解析借鉴了一部分词法/语法分析器的内容, 逻辑更加完善.

## 基础

### 指令介绍

#### 指令组成

一般的, 指令由三个部分组成: `main`, `opt`, `arg`.

- `main` 表示一个可执行的事件, 也是指令中的主要节点, 一般可能由多个 action + source 构成;
- `opt` 表示操作可选的固定参数,  
  必须具有以 `--` 开头的**长指令**, 也可以选择提供以 `-` 开头的**短指令**.  
- `arg` 表示需要输入的参数.  

> 例1:  
>
> - `app --color/-c color_name`
> - `app echo message`

#### 指令结构设计

- 指令中的所有 item 都以 `CommandItem` 的形式保存在 `CommandItemManager` 中统一管理;
- Item 分为三类: `main`, `opt` 和 `arg`;
- `main` 之后可以添加任何节点, `main` 可以注册在指令的任何部分;
- 跟随在一个 `main` 之后的 `opt` 都注册在同一个 `main` item 中, `opt` 可以携带 `arg` 参数;
- `arg` 可以注册在 `main` 或者 `opt` 之后, 一个 `main` 或者 `opt` 可以有多个 `arg` item;

> 例2:  
> 在指令 `app --color color_name --font font_main font_next echo message` 中, `main` item 为 `app` 和 `echo`, `opt` item 及其参数为 `--color color_name` 和 `--font font_main font_next`, 去掉 `opt`, 剩下的就是 `main` 及其参数 `app echo message`.  

#### 指令语义  

- 我们将**注册了指令执行器的节点**称为**语义节点**;  
- `main`, `opt` 和 `arg` 都可能是语义节点;
- 一个 `opt` 都**有且只有一个**语义节点, 为其**尾节点**;
- 指令解析时, 会解析所有 `opt` 的语义, 并将其执行, 但是只会解析当前指令的最后一个 `main` item, 如果它有语义, 就执行.  
- 对于 pipeline `|`, 会将 pipeline 前指令的执行结果作为参数传入其后的指令中, 如: `comandante -i | grep version`;
- 对于 command linker `&`, 会是两条指令依次执行;

> 例3:  
> 在例2的指令中, 两个 `opt` 都具有语义, 会被最终执行; `app echo message` 也有语义, 会被执行.

#### 短指令合并  

这是一个指令语法糖, 用于将指令中**没有参数**的 `opt` 短指令合并到一起输入, 更加便捷.  

> 例4:  
> 如果几个连续的短指令写在一起, 可以省略前面的 `-`, 然后将它们合并到一个短指令中.  
> 如:  
> `plugin list -i -a -d dir` 可以写为 `plugin list -iad dir`  
> *插件会自动将它们解析为没有缩写的状态.*

#### 参数传递规则

由于内部节点解析逻辑优先级不同, 如果参数值和当前同层级的 `main`  item 名称相同, 会优先将其解析为 `main` item.  
为避免这个问题, 在可能发生冲突的参数传递时, 可以使用 `'` 将参数括起来, 这样就能保证指令被正确解析为参数.  

由于部分词法解析由空格作为终止符, 如果参数中包含空格, 会导致指令解析错误. 我们同样可以使用 `'` 包围参数, 来避免发生错误.  

如果想要在参数中输入 `'`, 而不是作为参数括符使用, 需要在 `'` 前面加上 `\` 来表示它是一个普通的字符串. 该特性只能在被 `'` 括起来的参数中使用.  

> 例5:  
> 在例2指令的基础上, 如果我们还有 `app echo time --format format_str` 指令, 这时在 `time` 上就可能发生解析冲突.  
> 使用 `'` 包围参数来解决这个冲突:  
> `app echo 'time'`  
> 其他参数传递避免冲突的用法:  
> `app echo 'hello, I've said \'hello\' to you'`.

#### 指令词法

$$
\left\{
  \begin{array}{l}
  S \to CN \\
  N \to TCN \mid \epsilon \\
  C \to MM_1Y \\
  M_1 \to MM_1 \mid \epsilon \\
  T \to | \mid \& \\
  Y \to OY \mid AY \mid \epsilon \\
  O \to -O_1O_2 \mid --O_1 \\
  O_2 \to O_1O_2 \mid \epsilon \\
  A \to A_1 \mid 'A_1'
  \end{array}
\right.
$$

### 简单使用

定义一个指令定义类, 包含一个定义指令的方法, 推荐命名为 `defineCommand()`.  

```java
public class DemoCommand {

    public static void defineCommand() {

    }
}
```

然后在对应方法中注册指令:  

```java
public class DemoCommand {
      // ...

    public void defineCommand() {
        CommandLauncher.register().builder()
                .main("app")
                .main("echo")
                .arg("message")
                .executor(
                        (args, pipedArgs) -> "app echos"
                );

        CommandLauncher.register().builder()
                .main("grep")
                .arg("value")
                .executor(
                        (args, pipedArgs) -> args.toString() + "/" + pipedArgs
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
执行器的参数包括了两部分, 一部分是指令中的参数, 另一部分是 pipeline 提供的参数, 定义如下:  

```java
public interface Executable {
    /**
     * Execute method uses command context to get or put intermediate data to interact with related executors.
     *
     * @param args method arguments
     * @param pipedArgs method arguments getting from pipeline
     * @return result
     * @throws Exception command execute exception
     */
    Object execute(Object args, Object pipedArgs) throws Exception;

}
```

#### 指令传入参数

对于指令中传入参数的获取, 不需要手动完成, GrammarAnalyzer 会在分析时自动将对应的参数保存, 在执行时作为 `args` 参数传入, 所以我们只需要在 `args` 中获取对应的值即可.

#### Pipeline 产生参数

和上面类似, 对于使用 pipeline 特性的指令, 执行时, `pipedArgs` 即为所需的参数.

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
comandante --author/a                   查看 Comandante 作者
comandante --doc/d                      查看 Comandante 文档
comandante --version/v                  查看 Comandante 版本号
comandante --info/i                     查看 Comandante 信息
comandante --list/l                     列出所有已注册指令及其使用情况
```

对于自定义指令, 我们推荐使用者为其添加一些 `--help/-h` 指令, 来提高使用体验.

## API

### 注册指令

```java
// 获取指令构建器
CommandBuilder commandBuilder = CommandLauncher.register();
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
