/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.events;

import org.bukkit.TreeType;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Checker;
import ch.njol.util.coll.CollectionUtils;

public class EvtGrow extends SkriptEvent {
	
	/**
	 * Growth event restriction.
	 * 
	 * ANY means any grow event goes.
	 * 
	 * Structure/block restrict for structure/block grow events only.
	 */
	public static final int ANY = 0, STRUCTURE = 1, BLOCK = 2;
	
	static {
		Skript.registerEvent("Grow", EvtGrow.class, CollectionUtils.array(StructureGrowEvent.class, BlockGrowEvent.class),
				"grow [of (1¦%-treetype%|2¦%-itemtype%)]")
				.description("Called when a tree, giant mushroom or plant grows to next stage.")
				.examples("on grow:", "on grow of a tree:", "on grow of a huge jungle tree:")
				.since("1.0 (2.2-dev20 for plants)");
	}
	
	@Nullable
	private Literal<TreeType> types;
	@Nullable
	private Literal<ItemType> blocks;
	private int evtType;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Literal<?>[] args, final int matchedPattern, final ParseResult parser) {
		evtType = parser.mark; // ANY, STRUCTURE or BLOCK
		if (evtType == STRUCTURE)
			types = (Literal<TreeType>) args[0];
		else if (evtType == BLOCK)
			blocks = (Literal<ItemType>) args[1]; // Arg 1 may not be present... but it is in the array still, as null
		// Else: no type restrictions specified
		return true;
	}
	
	@Override
	public boolean check(final Event e) {
		if (evtType == STRUCTURE  && types != null && e instanceof StructureGrowEvent) {
			return types.check(e, t -> ((StructureGrowEvent) e).getSpecies() == t);
		} else if (evtType == BLOCK && blocks != null && e instanceof BlockGrowEvent) {
			return blocks.check(e, t -> t.isOfType(((BlockGrowEvent) e).getBlock()));
		} else if (evtType == ANY) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		if (evtType == STRUCTURE)
			return "grow" + (types != null ? " of " + types.toString(e, debug) : "");
		else if (evtType == BLOCK)
			return "grow" + (blocks != null ? " of " + blocks.toString(e, debug) : "");
		return "grow";
	}
	
}
