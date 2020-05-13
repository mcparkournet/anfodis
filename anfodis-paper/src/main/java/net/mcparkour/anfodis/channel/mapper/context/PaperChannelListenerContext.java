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

package net.mcparkour.anfodis.channel.mapper.context;

import java.lang.reflect.Field;
import net.mcparkour.anfodis.channel.ChannelMessage;
import net.mcparkour.common.reflection.Reflections;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PaperChannelListenerContext {

	@Nullable
	private Field messageField;
	@Nullable
	private Field sourceField;

	public PaperChannelListenerContext(PaperChannelListenerContextData data) {
		this.messageField = data.getMessageField();
		this.sourceField = data.getSourceField();
	}

	public void setMessageField(Object instance, ChannelMessage message) {
		if (this.messageField == null) {
			return;
		}
		Reflections.setFieldValue(this.messageField, instance, message);
	}

	public void setSourceField(Object instance, Player source) {
		if (this.sourceField == null) {
			return;
		}
		Reflections.setFieldValue(this.sourceField, instance, source);
	}
}
