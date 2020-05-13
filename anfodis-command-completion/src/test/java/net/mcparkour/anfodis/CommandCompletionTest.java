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
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.CodecRegistryBuilder;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.registry.CommandWrapper;
import net.mcparkour.anfodis.command.registry.TestCommandRegistry;
import net.mcparkour.anfodis.registry.Registry;
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

	@BeforeEach
	public void setUp() {
		Map<Locale, TranslatedText> fooTexts = Map.of(Locale.US, new TranslatedText(Locale.US, "foo"), POLISH, new TranslatedText(POLISH, "foo"));
		Map<Locale, TranslatedText> barTexts = Map.of(Locale.US, new TranslatedText(Locale.US, "bar"), POLISH, new TranslatedText(POLISH, "bar"));
		Map<String, Translation> translationMap = Map.of("foo", new Translation("foo", fooTexts), "bar", new Translation("bar", barTexts));
		Translations translations = new Translations(Locale.US, translationMap);
		TestMessageReceiverFactory receiverFactory = new TestMessageReceiverFactory(translations, TestCommandSender::getLanguage);
		this.commandManager = new HashMap<>(1);
		CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = new CodecRegistryBuilder<InjectionCodec<?>>()
			.typed(MessageReceiverFactory.class, InjectionCodec.reference(receiverFactory))
			.build();
		CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry = new CodecRegistryBuilder<ArgumentCodec<?>>()
			.typed(String.class, ArgumentCodec.identity())
			.typed(Locale.class, Locale::forLanguageTag)
			.keyed("arg", String::strip)
			.keyed("null", stringValue -> null)
			.build();
		CodecRegistry<CompletionCodec> completionCodecRegistry = new CodecRegistryBuilder<CompletionCodec>()
			.typed(Locale.class, CompletionCodec.entries("en-US", "pl-PL"))
			.keyed("args", CompletionCodec.entries("1", "2", "3"))
			.build();
		this.commandRegistry = new TestCommandRegistry(injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, receiverFactory, "test", this.commandManager);
	}

	@Test
	public void testFooCommand() {
		this.commandRegistry.register(TestFooCommand.class);
		CommandWrapper fooCommand = this.commandManager.get("fooo");
		TestCommandSender sender = new TestCommandSender(Locale.US);
		fooCommand.execute(sender, new String[] {"pl-PL"});
		List<String> suggestions = fooCommand.suggest(sender, new String[] {""});
		Assertions.assertEquals("pl-PL", sender.getLastMessage());
		Assertions.assertEquals(List.of("en-US", "pl-PL"), suggestions);
	}

	@Test
	public void testBarCommand() {
		this.commandRegistry.register(TestBarCommand.class);
		CommandWrapper fooCommand = this.commandManager.get("bar");
		TestCommandSender sender = new TestCommandSender(POLISH);
		fooCommand.execute(sender, new String[] {"  te st  ", "1", "2"});
		List<String> suggestions = fooCommand.suggest(sender, new String[] {"  te st  ", ""});
		Assertions.assertEquals("te st", sender.getLastMessage());
		Assertions.assertEquals("1", sender.getLastMessage());
		Assertions.assertEquals("2", sender.getLastMessage());
		Assertions.assertEquals(List.of("1", "2", "3"), suggestions);
	}

	@Test
	public void testTestCommand() {
		this.commandRegistry.register(TestCommand.class);
		CommandWrapper test = this.commandManager.get("test");
		TestCommandSender sender = new TestCommandSender(Locale.US);
		test.execute(sender, new String[] {"foo", "pl-PL"});
		List<String> fooSuggestions = test.suggest(sender, new String[] {"foo", ""});
		Assertions.assertEquals("pl-PL", sender.getLastMessage());
		Assertions.assertEquals(List.of("en-US", "pl-PL"), fooSuggestions);
		test.execute(sender, new String[] {"bar", "  te st  ", "1", "2"});
		List<String> barSuggestions = test.suggest(sender, new String[] {"bar", "  te st  ", ""});
		Assertions.assertEquals("te st", sender.getLastMessage());
		Assertions.assertEquals("1", sender.getLastMessage());
		Assertions.assertEquals("2", sender.getLastMessage());
		Assertions.assertEquals(List.of("1", "2", "3"), barSuggestions);
	}

	@Test
	public void testOptionalArgumentCommand() {
		this.commandRegistry.register(TestOptionalArgumentCommand.class);
		CommandWrapper command = this.commandManager.get("optional");
		TestCommandSender sender = new TestCommandSender(Locale.US);
		command.execute(sender, new String[] {"test", "test2"});
		Assertions.assertNull(sender.getLastMessage());
		Assertions.assertEquals("true", sender.getLastMessage());
		Assertions.assertEquals("test2", sender.getLastMessage());
		Assertions.assertEquals("false", sender.getLastMessage());
		Assertions.assertEquals("empty", sender.getLastMessage());
	}
}
