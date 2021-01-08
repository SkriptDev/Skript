/**
 * This file is part of Skript.
 *
 * Skript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Skript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

@Name("Block State")
@Description({"Get/set the specific block states of blocks. States will accept numbers, booleans and strings.",
	"'without updates' will stop the blocks around from updating their block states when changing a state.",
	"This has no effect when getting states. For more information regarding block state options, check McWiki, ",
	"go to a block of your choosing, and scroll down to 'block states' to see all variations."})
@Examples({"set {_age} to block state \"age\" of target block",
	"set block state \"age\" of target block to 3",
	"set block state \"facing\" of target block to north",
	"set block state \"east\" of target block without updates to false"})
@Since("INSERT VERSION")
public class ExprBlockState extends SimpleExpression<Object> {
	
	static {
		Skript.registerExpression(ExprBlockState.class, Object.class, ExpressionType.COMBINED,
			"block[ ]state %string% of %blocks%",
			"block[ ]state %string% of %blocks% without update[s]");
	}
	
	@SuppressWarnings("null")
	private Expression<String> tag;
	@SuppressWarnings("null")
	private Expression<Block> blocks;
	private int pattern;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		this.tag = (Expression<String>) exprs[0];
		this.blocks = (Expression<Block>) exprs[1];
		this.pattern = matchedPattern;
		return true;
	}
	
	@Nullable
	@Override
	protected Object[] get(Event event) {
		List<Object> list = new ArrayList<>();
		String t = this.tag.getSingle(event);
		if (t == null) return null;
		
		for (Block block : blocks.getArray(event)) {
			String tag = getTag(block.getBlockData().getAsString(), t);
			if (tag == null) return null;
			
			if (Utils.isBoolean(tag)) {
				list.add(Boolean.valueOf(tag));
			} else if (StringUtils.isNumeric(tag)) {
				list.add(Integer.parseInt(tag));
			} else {
				list.add(tag);
			}
			
		}
		return list.toArray();
	}
	
	@Nullable
	@Override
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(Object.class);
		}
		return null;
	}
	
	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		String obj = delta == null ? "" : delta[0].toString().toLowerCase(Locale.ROOT);
		String tag = this.tag.getSingle(e);
		if (tag == null) return;
		
		for (Block block : blocks.getAll(e)) {
			BlockData oldData = block.getBlockData();
			
			// only attempt to change data for a block that has possible data
			if (oldData.getAsString().contains("[")) {
				String newData = String.format("%s[%s=%s]",
					block.getType().getKey(),
					tag.toLowerCase(Locale.ROOT),
					obj);
				try {
					BlockData blockData = Bukkit.createBlockData(newData);
					blockData = oldData.merge(blockData);
					block.setBlockData(blockData, pattern == 0);
				} catch (IllegalArgumentException ignore) {
				}
			}
		}
	}
	
	@Override
	public Class<?> getReturnType() {
		return Object.class;
	}
	
	@Override
	public boolean isSingle() {
		return this.blocks.isSingle();
	}
	
	@Override
	public String toString(@Nullable Event e, boolean d) {
		return String.format("block state '%s' of block[s] %s %s",
			tag.toString(e, d),
			blocks.toString(e, d),
			pattern == 1 ? "without updates" : "");
	}
	
	// UTILS
	
	@Nullable
	private String getTag(String data, String tag) {
		if (!data.contains("[")) return null;
		
		data = data.substring(data.indexOf('[') + 1, data.length() - 1);
		for (String string : data.split(",")) {
			String[] s = string.split("=");
			if (s[0].equals(tag)) {
				return s[1];
			}
		}
		return null;
	}
	
}
