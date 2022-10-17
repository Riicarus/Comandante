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

### 简单使用
先获取 `SkyCommand` 单例:  
```java
public static final SkyCommand skyCommand = SkyCommand.startSkyCommand();
```
然后进行指令注册:  
```java
public static void defineCommand() {
    skyCommand.register()
            .execution("plugin")
            .action("load")
            .option("dir", "d")
            .argument("dir", new StringCommandArgumentType())
            .executor(
            (args) -> System.out.println("load plugin from dir: " + args[0])
    );
}
```
最后调用指令方法完成注册:  
```java
defineCommand();
```

## 扩展
...