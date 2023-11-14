package net.mcparkour.anfodis;

import net.mcparkour.anfodis.annotation.executor.Executor;
import net.mcparkour.anfodis.annotation.transform.Transform;
import net.mcparkour.anfodis.command.annotation.argument.Argument;
import net.mcparkour.anfodis.command.annotation.properties.Command;
import net.mcparkour.intext.message.MessageReceiver;

@Command("foo")
public class FooCommand {

    @Argument
    private String foo;

    @Transform
    private MessageReceiver receiver;

    @Executor
    public void execute() {
        this.receiver.receivePlain("`" + this.foo + "` & pong!\n");
    }
}
