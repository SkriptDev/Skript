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

import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
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
@Name("Biome")
@Description("The biome of a block. Do not that changing a biome may not be visible until the client reloads the chunks.")
@Examples({"every real minute:",
	"\tloop all players:",
	"\t\tif biome at loop-player is desert:",
	"\t\t\tdamage the loop-player by 1"})
@Since("1.4.4")
public class ExprBiome extends PropertyExpression<Block, Biome> {
	static {
		Skript.registerExpression(ExprBiome.class, Biome.class, ExpressionType.PROPERTY,
			"[the] biome of %blocks%", "%blocks%'[s] biome");
	}
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		setExpr((Expression<? extends Block>) exprs[0]);
		return true;
	}
	
	@Override
	protected Biome[] get(final Event e, final Block[] source) {
		return get(source, Block::getBiome);
	}
	
	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return new Class[]{Biome.class};
		return super.acceptChange(mode);
	}
	
	@Override
	public void change(final Event e, final @Nullable Object[] delta, final ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			assert delta != null;
			for (final Block block : getExpr().getArray(e))
				block.setBiome((Biome) delta[0]);
		} else {
			super.change(e, delta, mode);
		}
	}
	
	@Override
	public Class<? extends Biome> getReturnType() {
		return Biome.class;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the biome of " + getExpr().toString(e, debug);
	}
	
}
