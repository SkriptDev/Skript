package ch.njol.skript.hooks.economy.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.hooks.VaultHook;
import ch.njol.skript.lang.Expression;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Money")
@Description("How much virtual money a player has (can be changed).")
@Examples({"message \"You have $%player's money%\"",
	"remove 20 from the player's balance",
	"add 200 to the player's account"})
@Since("2.0, 2.5 (offline players)")
@RequiredPlugins({"Vault", "an economy plugin that supports Vault"})
public class ExprBalance extends SimplePropertyExpression<OfflinePlayer, Number> {

	static {
		register(ExprBalance.class, Number.class,
			"(money|balance|[bank] account)", "offlineplayers");
	}

	@Override
	public Number convert(OfflinePlayer player) {
		try {
			return VaultHook.economy.getBalance(player);
		} catch (Exception ignore) {
			//noinspection deprecation
			return VaultHook.economy.getBalance(player.getName());
		}
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.REMOVE_ALL)
			return null;
		return new Class[]{Number.class};
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		Expression<? extends OfflinePlayer> expr = getExpr();
		if (expr == null) return;

		if (delta == null) { // RESET/DELETE
			for (OfflinePlayer p : expr.getArray(event))
				VaultHook.economy.withdrawPlayer(p, VaultHook.economy.getBalance(p));
			return;
		}

		double money = delta[0] instanceof Number number ? number.doubleValue() : 0;
		for (OfflinePlayer player : expr.getArray(event)) {
			switch (mode) {
				case SET:
					double balance = VaultHook.economy.getBalance(player);
					if (balance < money) {
						VaultHook.economy.depositPlayer(player, money - balance);
					} else if (balance > money) {
						VaultHook.economy.withdrawPlayer(player, balance - money);
					}
					break;
				case ADD:
					VaultHook.economy.depositPlayer(player, money);
					break;
				case REMOVE:
					VaultHook.economy.withdrawPlayer(player, money);
					break;
			}
		}
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	protected String getPropertyName() {
		return "money";
	}

}
