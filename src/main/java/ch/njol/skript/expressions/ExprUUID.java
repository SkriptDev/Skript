package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Name("UUID")
@Description({"The UUID of a player, entity or world."})
@Examples({"# prevents people from joining the server if they use the name of a player",
	"# who has played on this server at least once since this script has been added",
	"on login:",
	"	if {uuid::%name of player%} exists:",
	"		{uuid::%name of player%} is not uuid of player",
	"		kick player due to \"Someone with your name has played on this server before\"",
	"	else:",
	"		set {uuid::%name of player%} to uuid of player"})
@Since("2.1.2, 2.2 (offline players' UUIDs), 2.2-dev24 (other entities' UUIDs)")
public class ExprUUID extends SimplePropertyExpression<Object, UUID> {

	static {
		register(ExprUUID.class, UUID.class, "UUID", "offlineplayers/worlds/entities");
	}

	@Override
	@Nullable
	public UUID convert(final Object object) {
		if (object instanceof OfflinePlayer offlinePlayer) {
			try {
				return offlinePlayer.getUniqueId();
			} catch (UnsupportedOperationException e) {
				// Some plugins (ProtocolLib) try to emulate offline players, but fail miserably
				// They will throw this exception... and somehow server may freeze when this happens
				Skript.warning("A script tried to get uuid of an offline player, which was faked by another plugin (probably ProtocolLib).");
				//noinspection CallToPrintStackTrace
				e.printStackTrace();
				return null;
			}
		} else if (object instanceof Entity entity) {
			return entity.getUniqueId();
		} else if (object instanceof World world) {
			return world.getUID();
		}
		return null;
	}

	@Override
	public Class<? extends UUID> getReturnType() {
		return UUID.class;
	}

	@Override
	protected String getPropertyName() {
		return "UUID";
	}

}
