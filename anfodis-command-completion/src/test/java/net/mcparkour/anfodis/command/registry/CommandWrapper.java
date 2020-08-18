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
import net.mcparkour.anfodis.TestCommandSender;

public class CommandWrapper {

    private TestCommandExecutor commandExecutor;
    private TestCompletionExecutor completionExecutor;

    public CommandWrapper(TestCommandExecutor commandExecutor, TestCompletionExecutor completionExecutor) {
        this.commandExecutor = commandExecutor;
        this.completionExecutor = completionExecutor;
    }

    public void execute(TestCommandSender sender, String[] args) {
        List<String> arguments = List.of(args);
        this.commandExecutor.execute(sender, arguments);
    }

    public List<String> complete(TestCommandSender sender, String[] args) {
        List<String> arguments = List.of(args);
        return this.completionExecutor.execute(sender, arguments);
    }
}
