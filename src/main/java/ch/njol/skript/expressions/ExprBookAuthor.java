package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

@Name("Book Author")
@Description("The author of a book.")
@Examples({"on book sign:",
	"\tmessage \"Book Title: %author of event-item%\""})
@Since("2.2-dev31")
public class ExprBookAuthor extends SimplePropertyExpression<ItemStack, String> {

	static {
		register(ExprBookAuthor.class, String.class, "[book] (author|writer|publisher)", "itemstacks");
	}

	@Nullable
	@Override
	public String convert(ItemStack item) {
		ItemMeta meta = item.getItemMeta();

		if (meta instanceof BookMeta)
			return ((BookMeta) meta).getAuthor();

		return null;
	}

	@Nullable
	@Override
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.RESET || mode == ChangeMode.DELETE)
			return CollectionUtils.array(String.class);
		return null;
	}

	@SuppressWarnings("null")
	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		String author = delta == null ? null : (String) delta[0];

		for (ItemStack item : getExpr().getArray(e)) {
			ItemMeta meta = item.getItemMeta();

			if (meta instanceof BookMeta bookMeta) {
				bookMeta.setAuthor(author);
				item.setItemMeta(meta);
			}
		}
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	protected String getPropertyName() {
		return "book author";
	}

}

