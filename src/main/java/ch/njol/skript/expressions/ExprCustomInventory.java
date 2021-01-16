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
package ch.njol.skript.expressions;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Merchant;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

@Name("Custom Inventory")
@Description({"Create a custom inventory that can be modified and opened to the player.",
	"NOTE: Inventories are not serialized, and will not save as variables once the server stops.",
	"Also, slots of a merchant inventory can't be set, these requires merchant recipes."})
@Examples({"set {_i} to custom chest inventory with size 4 named \"Ma Chest\"",
	"set slot 1 of {_i} to a diamond sword",
	"open {_i} to player",
	"open a custom barrel inventory to player",
	"open a custom hopper inventory named \"Señor Hoppie\" to player"})
@Since("INSERT VERSION")
public class ExprCustomInventory extends SimpleExpression<Object> {
	
	static {
		Skript.registerExpression(ExprCustomInventory.class, Object.class, ExpressionType.COMBINED,
			"[a] custom %inventorytype% [with size %-number%] [(named|with name[s]) %-string%] [with holder %-inventoryholder%]",
			"[a] custom %inventorytype% [with %-number% rows] [(named|with name[s]) %-string%] [with holder %-inventoryholder%]");
	}
	
	@SuppressWarnings("null")
	private Expression<InventoryType> invType;
	@Nullable
	private Expression<Number> rows;
	@Nullable
	private Expression<String> name;
	@Nullable
	private Expression<InventoryHolder> holder;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		invType = (Expression<InventoryType>) exprs[0];
		rows = (Expression<Number>) exprs[1];
		name = (Expression<String>) exprs[2];
		holder = (Expression<InventoryHolder>) exprs[3];
		return true;
	}
	
	@SuppressWarnings("ConstantConditions")
	@Nullable
	@Override
	protected Object[] get(Event e) {
		Number rows = this.rows != null ? this.rows.getSingle(e) : null;
		InventoryType invType = this.invType.getSingle(e);
		InventoryHolder holder = this.holder != null ? this.holder.getSingle(e) : null;
		if (invType == null)
			return null;
		
		String name = this.name != null ? this.name.getSingle(e) : null;
		
		if (invType == InventoryType.MERCHANT) { // Requires a special inventory
			return new Merchant[]{Bukkit.createMerchant(name)};
		}
		// Some inventory types (such as creative, and player inv) cant be created
		if (!invType.isCreatable()) {
			return null;
		}
		return getInventory(invType, holder, name, rows);
	}
	
	@Override
	public boolean isSingle() {
		return true;
	}
	
	@Override
	public Class<?> getReturnType() {
		return Object.class;
	}
	
	@Override
	public String toString(@Nullable Event e, boolean debug) {
		String size = this.rows != null ? String.format("with size '%s'", this.rows.toString(e, debug)) : "";
		String name = this.name != null ? String.format("named '%s'", this.name.toString(e, debug)) : "";
		String holder = this.holder != null ? String.format("with holder '%s'", this.holder.toString(e, debug)) : "";
		return String.format("%s %s %s %s", invType.toString(e, debug), size, name, holder);
	}
	
	private Inventory[] getInventory(InventoryType type, @Nullable InventoryHolder holder, @Nullable String name, @Nullable Number rows) {
		if (rows != null) {
			int size = rows.intValue();
			if (size > 6 || size < 1)
				size = 3;
			size *= 9;
			if (name != null)
				return new Inventory[]{Bukkit.createInventory(holder, size, name)};
			return new Inventory[]{Bukkit.createInventory(holder, size)};
		}
		if (name != null)
			return new Inventory[]{Bukkit.createInventory(holder, type, name)};
		return new Inventory[]{Bukkit.createInventory(holder, type)};
	}
	
}
