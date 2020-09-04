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

package net.mcparkour.anfodis;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.TestMessenger;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.result.Result;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.context.CommandSender;
import net.mcparkour.anfodis.command.registry.CommandWrapper;
import net.mcparkour.anfodis.command.registry.TestCommandRegistry;
import net.mcparkour.anfodis.registry.Registry;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiverFactory;
import net.mcparkour.intext.translation.TranslatedText;
import net.mcparkour.intext.translation.Translation;
import net.mcparkour.intext.translation.Translations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandCompletionTest {

    private static final Locale POLISH = new Locale("pl", "PL");

    private Map<String, CommandWrapper> commandManager;
    private Registry commandRegistry;

    public interface NullArgumentCodec extends ArgumentCodec<String> {}

    @BeforeEach
    public void setUp() {
        Map<Locale, TranslatedText> fooTexts = Map.of(Locale.US, new TranslatedText(Locale.US, "foo"), POLISH, new TranslatedText(POLISH, "foo"));
        Map<Locale, TranslatedText> barTexts = Map.of(Locale.US, new TranslatedText(Locale.US, "bar"), POLISH, new TranslatedText(POLISH, "bar"));
        Map<String, Translation> translationMap = Map.of("foo", new Translation("foo", fooTexts), "bar", new Translation("bar", barTexts));
        Translations translations = new Translations(Locale.US, translationMap);
        TestMessageReceiverFactory receiverFactory = new TestMessageReceiverFactory(translations, TestCommandSender::getLanguage);
        TestMessenger messenger = new TestMessenger() {};
        this.commandManager = new HashMap<>(1);
        CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = CodecRegistry.<InjectionCodec<?>>builder()
            .typed(MessageReceiverFactory.class, InjectionCodec.reference(receiverFactory))
            .typed(String.class, InjectionCodec.reference("testString"))
            .self(TestInjectionCodec1.class, new TestInjectionCodec1())
            .self(TestInjectionCodec2.class, new TestInjectionCodec2())
            .build();
        CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry = CodecRegistry.<ArgumentCodec<?>>builder()
            .typed(String.class, ArgumentCodec.identity())
            .typed(Integer.class, (commandContext, argumentContext, argumentValue) -> Result.ok(Integer.parseInt(argumentValue)))
            .typed(Locale.class, (commandContext, argumentContext, argumentValue) -> Result.ok(Locale.forLanguageTag(argumentValue)))
            .self(ArgArgumentCodec.class, new ArgArgumentCodec())
            .self(NullArgumentCodec.class, (commandContext, argumentContext, argumentValue) -> Result.ok("null"))
            .build();
        CodecRegistry<CompletionCodec> completionCodecRegistry = CodecRegistry.<CompletionCodec>builder()
            .typed(Locale.class, CompletionCodec.entries("en-US", "pl-PL"))
            .self(ArgsCompletionCodec.class, new ArgsCompletionCodec())
            .build();
        this.commandRegistry = new TestCommandRegistry(injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, receiverFactory, messenger, Permission.of("test"), this.commandManager);
    }

    @Test
    public void testFooCommand() {
        this.commandRegistry.register(TestFooCommand.class);
        CommandWrapper fooCommand = this.commandManager.get("fooo");
        TestCommandSender sender = new TestCommandSender();
        fooCommand.execute(sender, "pl-PL");
        List<String> completions = fooCommand.complete(sender, "");
        Assertions.assertEquals("pl-PL", sender.getLastMessage());
        Assertions.assertIterableEquals(List.of("en-US", "pl-PL"), completions);
    }

    @Test
    public void testBarCommand() {
        this.commandRegistry.register(TestBarCommand.class);
        CommandWrapper fooCommand = this.commandManager.get("bar");
        TestCommandSender sender = new TestCommandSender();
        fooCommand.execute(sender, "\"  te st  \" 1 2");
        List<String> completions = fooCommand.complete(sender, "\"  te st  \" ");
        Assertions.assertEquals("te st", sender.getLastMessage());
        Assertions.assertEquals("1", sender.getLastMessage());
        Assertions.assertEquals("2", sender.getLastMessage());
        Assertions.assertIterableEquals(List.of("1", "2", "3"), completions);
    }

    @Test
    public void testTestCommand() {
        this.commandRegistry.register(TestCommand.class);
        CommandWrapper test = this.commandManager.get("test");
        TestCommandSender sender = new TestCommandSender();
        test.execute(sender, "foo pl-PL");
        List<String> fooSuggestions = test.complete(sender, "foo ");
        Assertions.assertEquals("pl-PL", sender.getLastMessage());
        Assertions.assertEquals(List.of("en-US", "pl-PL"), fooSuggestions);
        test.execute(sender, "bar '  te st  ' 1 2");
        List<String> barSuggestions = test.complete(sender, "bar '  te st  ' ");
        Assertions.assertEquals("te st", sender.getLastMessage());
        Assertions.assertEquals("1", sender.getLastMessage());
        Assertions.assertEquals("2", sender.getLastMessage());
        Assertions.assertIterableEquals(List.of("1", "2", "3"), barSuggestions);
    }

    @Test
    public void testTestCommandCompletions() {
        this.commandRegistry.register(TestCommand.class);
        CommandWrapper test = this.commandManager.get("test");
        TestCommandSender sender = new TestCommandSender();
        List<String> completions = test.complete(sender, "");
        Assertions.assertIterableEquals(List.of("foo", "fooo", "bar"), completions);
    }

    @Test
    public void testOptionalArgumentCommand() {
        this.commandRegistry.register(TestOptionalArgumentCommand.class);
        CommandWrapper command = this.commandManager.get("optional");
        TestCommandSender sender = new TestCommandSender();
        command.execute(sender, "test test2");
        Assertions.assertEquals("null", sender.getLastMessage());
        Assertions.assertEquals("true", sender.getLastMessage());
        Assertions.assertEquals("test2", sender.getLastMessage());
        Assertions.assertEquals("false", sender.getLastMessage());
        Assertions.assertEquals("empty", sender.getLastMessage());
    }

    @Test
    public void testTestCommandPermissions() {
        this.commandRegistry.register(TestCommand.class);
        CommandWrapper test = this.commandManager.get("test");
        TestCommandSender sender = new TestCommandSender(Locale.US, Set.of("test.test", "test.test.fooo", "test.test.bar"), false);
        test.execute(sender, null);
        Assertions.assertEquals("test - test.\nfoo [language] - foo.\nbar <arg> <args...> - bar.", sender.getLastMessage());
        test.execute(sender, "foo pl");
        Assertions.assertEquals("pl", sender.getLastMessage());
        test.execute(sender, "bar");
        Assertions.assertEquals("bar <arg> <args...> - bar.", sender.getLastMessage());
    }

    @Test
    public void testTestCommandCompletionPermissions() {
        this.commandRegistry.register(TestCommand.class);
        CommandWrapper test = this.commandManager.get("test");
        TestCommandSender sender = new TestCommandSender(Locale.US, Set.of("test.test", "test.test.fooo", "test.test.bar"), false);
        List<String> complete1 = test.complete(sender, "");
        Assertions.assertIterableEquals(List.of("foo", "fooo", "bar"), complete1);
        List<String> complete2 = test.complete(sender, "foo ");
        Assertions.assertIterableEquals(List.of("en-US", "pl-PL"), complete2);
        List<String> complete3 = test.complete(sender, "bar '' ");
        Assertions.assertIterableEquals(List.of("1", "2", "3"), complete3);
    }

    @Test
    public void testNoPermissions() {
        this.commandRegistry.register(TestCommand.class);
        CommandWrapper test = this.commandManager.get("test");
        TestCommandSender sender = new TestCommandSender(Locale.US, Set.of("test.test.bar"), false);
        test.execute(sender, null);
        Assertions.assertEquals("You do not have permission: test.test", sender.getLastMessage());
        List<String> complete1 = test.complete(sender, "");
        Assertions.assertIterableEquals(List.of(), complete1);
        test.execute(sender, "bar");
        Assertions.assertEquals("You do not have permission: test.test", sender.getLastMessage());
        List<String> complete2 = test.complete(sender, "bar '' ");
        Assertions.assertIterableEquals(List.of(), complete2);
    }

    @Test
    public void testTestCommandNoPermissions() {
        this.commandRegistry.register(TestCommand.class);
        CommandWrapper test = this.commandManager.get("test");
        TestCommandSender sender = new TestCommandSender(Locale.US, Set.of("test.test", "test.test.bar"), false);
        test.execute(sender, null);
        Assertions.assertEquals("test - test.\nbar <arg> <args...> - bar.", sender.getLastMessage());
        test.execute(sender, "foo pl");
        Assertions.assertEquals("You do not have permission: test.test.fooo", sender.getLastMessage());
        test.execute(sender, "bar");
        Assertions.assertEquals("bar <arg> <args...> - bar.", sender.getLastMessage());
    }

    @Test
    public void testTestCommandCompletionNoPermissions() {
        this.commandRegistry.register(TestCommand.class);
        CommandWrapper test = this.commandManager.get("test");
        TestCommandSender sender = new TestCommandSender(Locale.US, Set.of("test.test", "test.test.bar"), false);
        List<String> complete1 = test.complete(sender, "");
        Assertions.assertIterableEquals(List.of("bar"), complete1);
        List<String> complete2 = test.complete(sender, "foo ");
        Assertions.assertIterableEquals(List.of(), complete2);
        List<String> complete3 = test.complete(sender, "bar '' ");
        Assertions.assertIterableEquals(List.of("1", "2", "3"), complete3);
    }

    @Test
    public void testInjectionCodecs() {
        this.commandRegistry.register(TestInjectionCodecCommand.class);
        CommandWrapper inject = this.commandManager.get("inject");
        TestCommandSender sender = new TestCommandSender();
        inject.execute(sender, null);
        Assertions.assertEquals("testString", sender.getLastMessage());
        Assertions.assertNull(sender.getLastMessage());
        Assertions.assertEquals("test2", sender.getLastMessage());
    }

    @Test
    public void testOptionalVariadicCommand() {
        this.commandRegistry.register(TestOptionalVariadicCommand.class);
        CommandWrapper command = this.commandManager.get("optionalVariadic");
        TestCommandSender sender = new TestCommandSender();
        command.execute(sender, null);
        Assertions.assertEquals("false", sender.getLastMessage());
        Assertions.assertNull(sender.getLastMessage());
        command.execute(sender, "1 2 3");
        Assertions.assertEquals("true", sender.getLastMessage());
        Assertions.assertEquals("1", sender.getLastMessage());
        Assertions.assertEquals("2", sender.getLastMessage());
        Assertions.assertEquals("3", sender.getLastMessage());
        Assertions.assertNull(sender.getLastMessage());
    }

    @Test
    public void testStringQuotedCommand() {
        this.commandRegistry.register(TestStringQuotedCommand.class);
        CommandWrapper command = this.commandManager.get("quote");
        TestCommandSender sender = new TestCommandSender();
        command.execute(sender, "foo bar foobar");
        Assertions.assertEquals("foo", sender.getLastMessage());
        Assertions.assertEquals("bar", sender.getLastMessage());
        Assertions.assertEquals("foobar", sender.getLastMessage());
        Assertions.assertNull(sender.getLastMessage());
        command.execute(sender, "foo \"bar foo foobar\" foobar");
        Assertions.assertEquals("foo", sender.getLastMessage());
        Assertions.assertEquals("bar foo foobar", sender.getLastMessage());
        Assertions.assertEquals("foobar", sender.getLastMessage());
        Assertions.assertNull(sender.getLastMessage());
    }
}
