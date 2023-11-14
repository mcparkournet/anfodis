package net.mcparkour.anfodis.command.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class CommandMap {

    private final Map<String, CommandMapEntry> commands;

    CommandMap() {
        this.commands = new HashMap<>(8);
    }

    public void register(final CommandMapEntry entry) {
        var command = entry.getCommand();
        var properties = command.getProperties();
        for (final String alias : properties.getAllNames()) {
            this.commands.put(alias, entry);
        }
    }

    public Optional<CommandMapEntry> getCommand(final String name) {
        var entry = this.commands.get(name);
        return Optional.ofNullable(entry);
    }
}
