# SkyCommand
version: 1.1  

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
由于 `SkyCommand` 是一个获取到的实例是一个单例, 我们需要在指令定义类里面先添加相关依赖和构造函数:  
```java
public class DemoCommand {

    private final SkyCommand skyCommand;

    public DemoCommand(SkyCommand skyCommand) {
        this.skyCommand = skyCommand;
    }
}
```
然后定义一个指令定义方法:  
```java
public class DemoCommand {
      // ...

      public void defineCommand() {
        skyCommand.register().execution("plugin")
        .action("load")
        .option("dir", "d").argument("dir", new StringCommandArgumentType())
        .executor(
                (args) -> System.out.println("load plugin from dir: " + args[0])
        );
      }
}
```
启动指令服务:  
```java
public class TestMain {

    public static void main(String[] args) {
        // 获取到 SkyCommand 单例对象
        SkyCommand skyCommand = SkyCommand.getSkyCommand();
        // 启动命令行服务
        skyCommand.startSkyCommand();
        // 将自定义的指令加载进命令行注册器里
        new DemoCommand(skyCommand).defineCommand();
    }
}
```
至此, 就可以在控制台输入相关指令并得到对应服务了.

## 扩展
### IO 扩展
SkyCommand 默认使用 `ConsoleIOHandler` 进行 I/O 操作, 包括指令的读入和执行结果或错误信息的输出.  
如果需要自定义 I/O, 可以实现 `com.skyline.command.manage.IOHandler` 接口, `IOHandler#doGetCommand()` 方法用于获取输入的指令, `IOHandler#redirectOutput()` 用于设置重定向输出, 我们建议 `System.out#setOut()` 方法来重定向 `System.out` 相关 print 方法的输出位置.  

## 内置指令
目前 SkyCommand 有如下内置指令:  
```bash
command --version
command --author
command --doc
command --info
command --help
```
对所有已加载指令(可以是自定义的指令), 在其 `exe` 节点上, 我们都为其装配了对应的 help 指令, 如:  
```bash
command --help
exe --help
```
help 指令会输出该指令节点下所有的指令搭配, 其中 `option` 节点会用 `[]` 括起来, `argument` 节点会用 `<>` 括起来, 如:  
```bash
command -h
Format: exe act sub-act [opt] <arg>
command [help]
command [author]
command [doc]
command [version]
command [info]
```