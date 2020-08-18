/*
 * MIT License
 *
 * Copyright (c) 2020 MCParkour
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.mcparkour.anfodis.command.registry;

import java.util.List;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import org.checkerframework.checker.nullness.qual.NonNull;

class CommandWrapper implements Command {

    private final VelocityCommandExecutor commandExecutor;
    private final VelocityCompletionExecutor completionExecutor;

    CommandWrapper(VelocityCommandExecutor commandExecutor, VelocityCompletionExecutor completionExecutor) {
        this.commandExecutor = commandExecutor;
        this.completionExecutor = completionExecutor;
    }

    @Override
    public void execute(CommandSource source, @NonNull String[] args) {
        List<String> arguments = List.of(args);
        this.commandExecutor.execute(source, arguments);
    }

    @Override
    public List<String> suggest(CommandSource source, @NonNull String[] currentArgs) {
        List<String> arguments = List.of(currentArgs);
        return this.completionExecutor.execute(source, arguments);
    }

    @Override
    public boolean hasPermission(CommandSource source, @NonNull String[] args) {
        return true;
    }
}
