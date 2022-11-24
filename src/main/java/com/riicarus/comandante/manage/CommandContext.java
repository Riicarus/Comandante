package com.riicarus.comandante.manage;

import com.riicarus.comandante.executor.CommandExecutor;
import com.riicarus.comandante.tree.*;
import com.riicarus.util.asserts.Asserts;
import com.riicarus.util.exception.EmptyStringException;
import com.riicarus.util.exception.NullObjectException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * [FEATURE INFO]<br/>
 * 指令上下文
 *
 * @author Skyline
 * @create 2022-11-16 17:43
 * @since 1.0.0
 */
public class CommandContext {
    /**
     * 指令解析时逐步运行产生的数据缓存
     */
    private final HashMap<String, Object> data = new HashMap<>();
    /**
     * 当前上下文解析的指令字符串分割后的字符串列表
     */
    private final List<String> commandStrParts;
    /**
     * 在 commandMainParts 中删除掉的非主干节点的数量
     */
    private int deletedCount = 0;
    /**
     * 当前指令按序解析出的 OptionNode 部分对应的 指令执行器
     */
    private final LinkedList<CommandExecutor> optionExecutors = new LinkedList<>();
    /**
     * 指令树主干节点对应的指令部分, 为 ExecutionNode 及其 ArgumentNode
     */
    private final List<String> commandMainParts;
    /**
     * 当前指令的主节点
     */
    private ExecutionNode mainExecutionNode;
    /**
     * 当前解析到的节点
     */
    private AbstractNode currentNode;

    public CommandContext(final List<String> commandStrParts, final RootNode rootNode) {
        this.commandStrParts = commandStrParts;
        this.currentNode = rootNode;
        this.commandMainParts = new ArrayList<>(commandStrParts);
    }

    public void increaseDeletedCount() {
        deletedCount++;
    }

    public void addOptionExecutor(final CommandExecutor commandExecutor) {
        getOptionExecutors().addLast(commandExecutor);
    }

    public void deleteNotMainPart(int index) {
        getCommandMainParts().remove(index);
        increaseDeletedCount();
    }

    public void putData(String key, Object value) throws NullObjectException, EmptyStringException {
        Asserts.notEmpty(key, "Key of data in CommandContext can not be null or empty.");
        Asserts.notNull(value, "Value of data in CommandContext can not be null.");

        getData().put(key, value);
    }

    public Object getData(String key) {
        return getData().get(key);
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public List<String> getCommandStrParts() {
        return commandStrParts;
    }

    public int getDeletedCount() {
        return deletedCount;
    }

    public LinkedList<CommandExecutor> getOptionExecutors() {
        return optionExecutors;
    }

    public List<String> getCommandMainParts() {
        return commandMainParts;
    }

    public ExecutionNode getMainExecutionNode() {
        return mainExecutionNode;
    }

    public AbstractNode getCurrentNode() {
        return currentNode;
    }

    public void setMainExecutionNode(ExecutionNode mainExecutionNode) {
        this.mainExecutionNode = mainExecutionNode;
    }

    public void setCurrentNode(AbstractNode currentNode) {
        this.currentNode = currentNode;
    }
}
