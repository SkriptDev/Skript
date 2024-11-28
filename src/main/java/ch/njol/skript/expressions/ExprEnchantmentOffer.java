package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Events;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.Event;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("Enchantment Offer")
@Description({"The enchantment offer in enchant prepare events.",
	"These can be changed using the 'enchantmentOffer' function."})
@Examples({"on enchant prepare:",
	"\tsend \"Your enchantment offers are: %the enchantment offers%\" to player",
	"set enchantment offer 3 to enchantmentOffer(sharpness, 10, 20)"})
@Since("2.5")
@Events("enchant prepare")
@RequiredPlugins("1.11 or newer")
public class ExprEnchantmentOffer extends SimpleExpression<EnchantmentOffer> {

	static {
		Skript.registerExpression(ExprEnchantmentOffer.class, EnchantmentOffer.class, ExpressionType.SIMPLE,
			"[all [of]] [the] enchant[ment] offers",
			"enchant[ment] offer[s] %numbers%",
			"[the] %number%(st|nd|rd|th) enchant[ment] offer");
	}

	@SuppressWarnings("null")
	private Expression<Number> exprOfferNumber;

	private boolean all;

	@SuppressWarnings({"null", "unchecked"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (!getParser().isCurrentEvent(PrepareItemEnchantEvent.class)) {
			Skript.error("Enchantment offers are only usable in enchant prepare events", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		if (matchedPattern == 0) {
			all = true;
		} else {
			exprOfferNumber = (Expression<Number>) exprs[0];
			all = false;
		}
		return true;
	}

	@SuppressWarnings({"null", "unused"})
	@Override
	@Nullable
	protected EnchantmentOffer[] get(Event e) {
		if (!(e instanceof PrepareItemEnchantEvent))
			return null;

		if (all)
			return ((PrepareItemEnchantEvent) e).getOffers();
		if (exprOfferNumber == null)
			return new EnchantmentOffer[0];
		if (exprOfferNumber.isSingle()) {
			Number offerNumber = exprOfferNumber.getSingle(e);
			if (offerNumber == null)
				return new EnchantmentOffer[0];
			int offer = offerNumber.intValue();
			if (offer < 1 || offer > ((PrepareItemEnchantEvent) e).getOffers().length)
				return new EnchantmentOffer[0];
			return new EnchantmentOffer[]{((PrepareItemEnchantEvent) e).getOffers()[offer - 1]};
		}
		List<EnchantmentOffer> offers = new ArrayList<>();
		int i;
		for (Number n : exprOfferNumber.getArray(e)) {
			i = n.intValue();
			if (i >= 1 || i <= ((PrepareItemEnchantEvent) e).getOffers().length)
				offers.add(((PrepareItemEnchantEvent) e).getOffers()[i - 1]);
		}
		return offers.toArray(new EnchantmentOffer[0]);
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.DELETE)
			return CollectionUtils.array(EnchantmentOffer.class);
		return null;
	}

	@SuppressWarnings("null")
	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		if (delta == null && mode != ChangeMode.DELETE)
			return;
		EnchantmentOffer offer = mode != ChangeMode.DELETE ? (EnchantmentOffer) delta[0] : null;
		if (event instanceof PrepareItemEnchantEvent prepareEvent) {
			switch (mode) {
				case SET:
					if (all) {
						for (int i = 0; i <= 2; i++) {
							prepareEvent.getOffers()[i] = offer;
						}
					} else {
						for (Number n : exprOfferNumber.getArray(prepareEvent)) {
							int slot = n.intValue() - 1;
							if (slot < 0 || slot > 2) continue;

							prepareEvent.getOffers()[slot] = offer;
						}
					}
					break;
				case DELETE:
					if (all) {
						Arrays.fill(prepareEvent.getOffers(), null);
					} else {
						for (Number n : exprOfferNumber.getArray(prepareEvent))
							prepareEvent.getOffers()[n.intValue() - 1] = null;
					}
					break;
				case ADD:
				case REMOVE:
				case RESET:
				case REMOVE_ALL:
					assert false;
			}
		}
	}

	@Override
	public boolean isSingle() {
		return !all && exprOfferNumber.isSingle();
	}

	@Override
	public Class<? extends EnchantmentOffer> getReturnType() {
		return EnchantmentOffer.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return all ? "the enchantment offers" : "enchantment offer(s) " + exprOfferNumber.toString(e, debug);
	}

}
