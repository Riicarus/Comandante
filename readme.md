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

