package ch.njol.skript.classes.data.bukkit;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.EnumClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.Nullable;

/**
 * Represents {@link ClassInfo ClassInfos} relating to {@link org.bukkit.event.Event Events}
 */
public class EventClasses {

	private EventClasses() {
	}

	public static void init() {
		if (Skript.classExists("com.destroystokyo.paper.event.server.PaperServerListPingEvent")) {
			Classes.registerClass(new ClassInfo<>(CachedServerIcon.class, "cachedservericon")
				.user("server ?icons?")
				.name("Server Icon")
				.description("A server icon that was loaded using the <a href='effects.html#EffLoadServerIcon'>load server icon</a> effect.")
				.examples("")
				.since("2.3")
				.parser(new Parser<>() {
					@Override
					@Nullable
					public CachedServerIcon parse(final String s, final ParseContext context) {
						return null;
					}

					@Override
					public boolean canParse(final ParseContext context) {
						return false;
					}

					@Override
					public String toString(final CachedServerIcon o, int flags) {
						return "server icon";
					}

					@Override
					public String toVariableNameString(final CachedServerIcon o) {
						return "server icon";
					}
				}));
		}

		Classes.registerClass(new ClassInfo<>(EnchantmentOffer.class, "enchantmentoffer")
			.user("enchant[ment][ ]offers?")
			.name("Enchantment Offer")
			.description("The enchantmentoffer in an enchant prepare event.")
			.examples("on enchant prepare:",
				"\tset enchant offer 1 to sharpness 1",
				"\tset the cost of enchant offer 1 to 10 levels")
			.since("2.5")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(ParseContext context) {
					return false;
				}

				@Override
				public String toString(EnchantmentOffer eo, int flags) {
					return Classes.toString(eo.getEnchantment()) + " " + eo.getEnchantmentLevel();
				}

				@Override
				public String toVariableNameString(EnchantmentOffer eo) {
					return "offer:" + Classes.toString(eo.getEnchantment()) + "=" + eo.getEnchantmentLevel();
				}
			}));

		Classes.registerClass(new EnumClassInfo<>(EntityRegainHealthEvent.RegainReason.class, "healreason", "heal reasons")
			.user("(regen|heal) (reason|cause)")
			.name("Heal Reason")
			.description("The health regain reason in a <a href='events.html#heal'>heal</a> event.")
			.since("2.5"));

		if (Skript.classExists("org.bukkit.event.inventory.InventoryCloseEvent$Reason"))
			Classes.registerClass(new EnumClassInfo<>(InventoryCloseEvent.Reason.class, "inventoryclosereason", "inventory close reasons")
				.user("inventory ?close ?reasons?")
				.name("Inventory Close Reasons")
				.description("The inventory close reason in an <a href='/events.html#inventory_close'>inventory close event</a>.")
				.requiredPlugins("Paper")
				.since("2.8.0"));

		if (Skript.classExists("org.bukkit.event.player.PlayerQuitEvent$QuitReason"))
			Classes.registerClass(new EnumClassInfo<>(PlayerQuitEvent.QuitReason.class, "quitreason", "quit reasons")
				.user("(quit|disconnect) ?(reason|cause)s?")
				.name("Quit Reason")
				.description("Represents a quit reason from a <a href='/events.html#quit'>player quit server event</a>.")
				.requiredPlugins("Paper 1.16.5+")
				.since("2.8.0"));

		Classes.registerClass(new EnumClassInfo<>(PlayerResourcePackStatusEvent.Status.class, "resourcepackstate", "resource pack states")
			.user("resource ?pack ?states?")
			.name("Resource Pack State")
			.description("The state in a <a href='events.html#resource_pack_request_action'>resource pack request response</a> event.")
			.since("2.4"));

		Classes.registerClass(new EnumClassInfo<>(CreatureSpawnEvent.SpawnReason.class, "spawnreason", "spawn reasons")
			.user("spawn(ing)? ?reasons?")
			.name("Spawn Reason")
			.description("The spawn reason in a <a href='events.html#spawn'>spawn</a> event.")
			.since("2.3"));

		Classes.registerClass(new EnumClassInfo<>(PlayerTeleportEvent.TeleportCause.class, "teleportcause", "teleport causes")
			.user("teleport ?(cause|reason|type)s?")
			.name("Teleport Cause")
			.description("The teleport cause in a <a href='events.html#teleport'>teleport</a> event.")
			.since("2.2-dev35"));

		Classes.registerClass(new EnumClassInfo<>(EntityTransformEvent.TransformReason.class, "transformreason", "transform reasons")
			.user("(entity)? ?transform ?(reason|cause)s?")
			.name("Transform Reason")
			.description("Represents a transform reason of an <a href='events.html#entity transform'>entity transform event</a>.")
			.since("2.8.0"));
	}

}
