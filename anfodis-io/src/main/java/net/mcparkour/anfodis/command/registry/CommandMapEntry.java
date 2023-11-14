package net.mcparkour.anfodis.command.registry;

import net.mcparkour.anfodis.command.context.IoCommandContext;
import net.mcparkour.anfodis.command.context.IoCommandContextBuilder;
import net.mcparkour.anfodis.command.handler.CommandContextBuilderHandler;
import net.mcparkour.anfodis.command.mapper.IoCommand;

class CommandMapEntry {
    private final IoCommand command;
    private final CommandContextBuilderHandler<IoCommandContextBuilder, IoCommandContext> handler;

    CommandMapEntry(
        final IoCommand command,
        final CommandContextBuilderHandler<IoCommandContextBuilder, IoCommandContext> handler
    ) {
        this.command = command;
        this.handler = handler;
    }

    public IoCommand getCommand() {
        return this.command;
    }

    public CommandContextBuilderHandler<IoCommandContextBuilder, IoCommandContext> getHandler() {
        return this.handler;
    }
}
