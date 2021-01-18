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

import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Targeted Block")
@Description({"The block at the crosshair of a living entity.",
	"This regards all blocks that are not air as fully solid, e.g. torches will be like a solid stone block for this expression.",
	"Using the 'exact target block' pattern will return the exact block the entity is targeting and take block collisions into account."})
@Examples({"# A command to set the block a player looks at to a specific type:",
	"command /setblock <itemtype>:",
	"\ttrigger:",
	"\t\tset targeted block to arg-1", "",
	"set {_t} to exact target block of player",
	"set {_block} to target block of player",
	"teleport player to location above target block of player"})
@Since("1.0, INSERT VERSION (exact target)")
public class ExprTargetedBlock extends PropertyExpression<LivingEntity, Block> {
	static {
		Skript.registerExpression(ExprTargetedBlock.class, Block.class, ExpressionType.COMBINED,
			"[the] target[ed] block[s] [of %livingentities%]",
			"%livingentities%'[s] target[ed] block[s]",
			"[the] (exact|actual[ly]) target[ed] block[s] [of %livingentities%]",
			"%livingentities%'[s] (exact|actual[ly]) target[ed] block[s]");
	}
	
	private boolean exactTargetBlock;
	
	@Nullable
	private static Event last = null;
	private static final WeakHashMap<LivingEntity, Block> TARGET_BLOCKS = new WeakHashMap<>();
	private static final WeakHashMap<LivingEntity, Block> EXACT_TARGET_BLOCKS = new WeakHashMap<>();
	private static long blocksValidForTick = 0;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parser) {
		setExpr((Expression<LivingEntity>) exprs[0]);
		exactTargetBlock = matchedPattern >= 2;
		return true;
	}
	
	@Override
	protected Block[] get(final Event e, final LivingEntity[] source) {
		return get(source, entity -> getTargetedBlock(entity, e));
	}
	
	@SuppressWarnings("ConstantConditions")
	@Nullable
	Block getTargetedBlock(final @Nullable LivingEntity entity, final Event e) {
		if (entity == null)
			return null;
		final long time = Bukkit.getWorlds().get(0).getFullTime();
		if (last != e || time != blocksValidForTick) {
			TARGET_BLOCKS.clear();
			blocksValidForTick = time;
			last = e;
		}
		if (getTime() <= 0) {
			if (exactTargetBlock && EXACT_TARGET_BLOCKS.containsKey(entity))
				return EXACT_TARGET_BLOCKS.get(entity);
			else if (!exactTargetBlock && TARGET_BLOCKS.containsKey(entity))
				return TARGET_BLOCKS.get(entity);
		}
		try {
			Block block;
			if (exactTargetBlock) {
				block = entity.getTargetBlockExact(SkriptConfig.maxTargetBlockDistance.value());
			} else {
				block = entity.getTargetBlock(null, SkriptConfig.maxTargetBlockDistance.value());
			}
			if (block != null && block.getType() == Material.AIR)
				block = null;
			if (exactTargetBlock)
				EXACT_TARGET_BLOCKS.put(entity, block);
			else
				TARGET_BLOCKS.put(entity, block);
			return block;
		} catch (final IllegalStateException ex) {// Bukkit my throw this (for no reason?)
			return null;
		}
	}
	
	@Override
	public Class<Block> getReturnType() {
		return Block.class;
	}
	
	@Override
	public boolean isDefault() {
		return false;
	}
	
	@Override
	public boolean setTime(final int time) {
		super.setTime(time);
		return true;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return String.format("%s target block of %s",
			exactTargetBlock ? "exact" : "",
			getExpr().toString(e, debug));
	}
	
}
