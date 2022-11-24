package com.riicarus.comandante.executor;

import com.riicarus.comandante.manage.CommandContext;
import com.riicarus.comandante.tree.AbstractNode;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * [FEATURE INFO]<br/>
 * 指令执行器, 用于定义指令可执行节点的执行方法和相关属性
 *
 * @author Skyline
 * @create 2022-10-15 0:09
 * @since 1.0
 */
public class CommandExecutor {
    /**
     * 执行器
     */
    private Executable executor;
    /**
     * 指令执行器所属的节点
     */
    private final AbstractNode node;
    /**
     * 调用次数
     */
    private final AtomicInteger useCount = new AtomicInteger(0);

    public CommandExecutor(Executable executor, AbstractNode node) {
        this.executor = executor;
        this.node = node;
    }

    protected CommandExecutor(AbstractNode node) {
        this.node = node;
    }

    /**
     * 指令执行方法, 调用执行器执行, 由 CommandDispatcher 进行调用<br/>
     *
     * @param context 指令上下文
     * @throws Exception 执行时抛出的异常
     */
    public final void execute(CommandContext context) throws Exception {
        useCount.incrementAndGet();
        executor.execute(context);
    }

    public Executable getExecutor() {
        return executor;
    }

    public AbstractNode getNode() {
        return node;
    }

    public int getUseCount() {
        return useCount.get();
    }

    public void setExecutor(Executable executor) {
        this.executor = executor;
    }
}
