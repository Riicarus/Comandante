# Comandante
version: 2.0   

## 概述
### 指令结构
一般的, 指令由四个部分组成: `[exe] [action] [option] [argument]`. 
- `exe` 表示一个可运行的服务(或程序);  
- `action` 表示服务可以进行的操作;  
- `option` 表示操作可选的固定参数,  
  必须以 `-` (短指令)或者 `--` (长指令)开头,  
  我们强烈建议每个 `option` 都同时具备长指令和短指令;  
- `argument` 表示需要输入的参数.  

如果一个 `action` 不足以完成对应指令, 可以在其后增加一个 `subaction`, 但是只能有一个 `subaction`. 组合起来就是: `[exe] [action] [subaction] [option] [argument]`.  

如果一个 `action` 后有多个 `option` 且都为短 `option`, 那么他们可以合并为一个部分.  

指令结构搭配:  
- `exe` 后可以搭配 `action` 或 `option`
- `action` 后可以搭配 `subaction` 或 `option`
- `subaction` 只能在 `action` 后, 可搭配 `option`
- `option` 后可以搭配 `option` 或 `argument`
- `argument` 后可以搭配 `option`

> tips:  
> 如果几个连续的短指令写在一起, 可以省略前面的 `-`, 然后将它们合并到一个短指令中.  
> 如:  
> `plugin list -i -a -d dir` 可以写为 `plugin list -iad dir`  
> 插件会自动将它们解析为没有缩写的状态.
>   
> 如果参数中包含空格, 需要使用 `'` 将参数括起来, 如果要使用 `'` 原来的意义, 请使用转义字符 `\'`.  
> 如:  
> `plugin list -iad 'D:\tmp\jars\my \'jar\''`

### 简单使用
指令定义类需要继承抽象类 `BaseCommand`, 重写 `BaseCommand#defineCommand()` 方法.  
```java
public class DemoCommand extends BaseCommand {

    public static void defineCommand() {

    }
}
```
然后定义一个指令定义方法:  
```java
public class DemoCommand {
      // ...

      public void defineCommand() {
        CommandUtil.register().execution("plugin").action("load")
                .option("dir", "d").argument("dir", new StringCommandArgumentType())
                .executor(
                (args) -> Logger.log("load plugin from dir: " + args[0]),
                "从文件夹加载插件"
        );
      }
}
```
启动指令服务:  
```java
public class TestMain {

    public static void main(String[] args) {
        DemoCommand.defineCommand();
        CommandUtil.enable();
    }
}
```
至此, 就可以使用命令插件的基础服务了.

## 扩展
### IO 扩展
#### 指令输入
Comandante 内置了完善的输入输出机制, 都可以通过 `CommandUtil` 提供的 API 调用实现.  
指令输入的接口是 `CommmandUtil.dispatchToCache(String)` 方法, 使用该方法接收外界传入的指令字符串, 并由指令处理线程进行后续分发和执行处理.  
用户可以自行定义指令的输入方式, 无论是从文件中读取进行批处理还是从控制台输入, 只需要构建好获取指令的逻辑, 然后将获取到的指令通过 `CommandUtil.dispatchToCache(String)` 方法传给指令插件即可.  
指令输入支持多线程, `CommandInputHandler` 内部通过生产者消费者模式进行输出指令处理.  

#### 重定向输出
Comandante 同时支持输出重定向, 可以重定向插件内部所有输出到任意的输出流中.  
该功能由 `CommandUtil.redirectOutput(OutputStream)` 方法提供, 该方法会设置全局日志输出工具类 `Logger` 的输出流, 来达到全局重定向输出的目的.  
同时, 该接口支持设置输出流的字符集, 只需要调用 `CommandUtil.redirectOutut(OutputStream, StandardCharsets)` 方法进行输出重定即可.  

> 由上可以看出, 指令插件所有的输出都是调用 `Logger.log(String)` 方法进行的.  
> 所以如果想要完美实现重定向输出的功能, 需要在自定义指令或其他配置中都是用该方法进行输出.  

## 内置指令
目前 Comandante 有如下内置指令:  
```bash
comandante --version  查看 Comandante 版本号
comandante --author  查看 Comandante 作者
comandante --doc  查看 Comandante 文档
comandante --info  查看 Comandante 信息
comandante --help  帮助指令
comandante list -a 列出所有已注册指令
```
对所有已加载指令(可以是自定义的指令), 在其 `exe` 节点上, 我们都为其装配了对应的 help 指令, 如:  
```bash
comandante --help
exe --help
```
help 指令会输出该指令节点下所有的指令搭配, 其中 `option` 节点会用 `[]` 括起来, `argument` 节点会用 `<>` 括起来, 如:  
```bash
comandante -h
Input comandante: comandante -h.
Format: exe act sub-act [opt] <arg>
comandante [help]  帮助指令
comandante [author]  查看 Comandante 作者
comandante [doc]  查看 Comandante 文档
comandante list [all]  列出所有已注册指令
comandante [version]  查看 Comandante 版本号
comandante [info]  查看 Comandante 信息
Command execute complete.
```

## API
### 注册指令
```java
// 获取指令构建器
CommandBuilder commandBuider = CommandUtil.register();
// 注册 ExecutionCommand 节点
CommandBuilder#execution(String);
// 注册 ActionCommand 节点
CommandBuilder#action(String);
// 注册 SubActionCommand 节点
CommandBuilder#subAction(String);
// 注册 OptionCommand 节点
CommandBuilder#option(String, String);
// 注册 ArgumentCommand 节点
CommandBuilder#argument(String, CommandArgumentType<T>);
// 注册指令执行器
CommandBuilder#executor(CommandExecutor);
```
### 启动/停止指令处理线程
```java
// 启动
CommandUtil.enable();
// 停止
CommandUtil.disable();
```
### IO
```java
// 重定向输出
CommandUtil.redirectOutput(OutputStream);
CommandUtil.redirectOutput(OutputStream, StandardCharsets);
// 设置日志文件输出 文件路径必须为绝对路径
CommandUtil.setLogFile(String);
CommandUtil.setLogFile(String, StandardCharsets);
// 指令输入
CommandUtil.dispatchToCache(String);
// 输出日志
Logger.log();
// 关闭输出流, 不要关闭默认输出流
Logger.log()
```
