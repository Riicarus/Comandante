package com.riicarus.comandante.manage;

import com.riicarus.comandante.tree.*;

import java.util.HashMap;
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
     * 当前上下文解析的指令字符串
     */
    private final String commandStr;
    /**
     * 当前上下文解析的指令字符串分割后的字符串列表
     */
    private final List<String> commandStrParts;
    /**
     * 当前解析到的指令字符串列表的索引
     */
    private int currentIndex;
    /**
     * 当前解析到的节点
     */
    private AbstractNode currentNode;
    /**
     * 当前解析到的 ExecutionNode 节点
     */
    private ExecutionNode currentExecutionNode;
    /**
     * 当前解析到的 OptionNode 节点
     */
    private OptionNode currentOptionNode;
    /**
     * 当前解析到的 ArgumentNode 节点
     */
    private ArgumentNode<?> currentArgumentNode;

    public CommandContext(String commandStr, List<String> commandStrParts, final RootNode rootNode) {
        this.commandStr = commandStr;
        this.commandStrParts = commandStrParts;
        this.currentNode = rootNode;
    }

    public boolean isEnd() {
        return currentIndex >= commandStrParts.size();
    }

    public boolean isLast() {
        return currentIndex == commandStrParts.size() - 1;
    }

    public void increaseCurrentIndex() {
        currentIndex++;
    }

    public void putData(String key, Object value) {
        getData().put(key, value);
    }

    public Object getData(String key) {
        return getData().get(key);
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public String getCommandStr() {
        return commandStr;
    }

    public List<String> getCommandStrParts() {
        return commandStrParts;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public AbstractNode getCurrentNode() {
        return currentNode;
    }

    public ExecutionNode getCurrentExecutionNode() {
        return currentExecutionNode;
    }

    public OptionNode getCurrentOptionNode() {
        return currentOptionNode;
    }

    public ArgumentNode<?> getCurrentArgumentNode() {
        return currentArgumentNode;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public void setCurrentNode(AbstractNode currentNode) {
        this.currentNode = currentNode;
    }

    public void setCurrentExecutionNode(ExecutionNode currentExecutionNode) {
        this.currentExecutionNode = currentExecutionNode;
    }

    public void setCurrentOptionNode(OptionNode currentOptionNode) {
        this.currentOptionNode = currentOptionNode;
    }

    public void setCurrentArgumentNode(ArgumentNode<?> currentArgumentNode) {
        this.currentArgumentNode = currentArgumentNode;
    }
}
