package org.skriptlang.skript.test.tests.classes;

import org.bukkit.GameMode;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryType;
import org.junit.Test;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.SkriptColor;
import ch.njol.skript.util.StructureType;
import ch.njol.skript.util.Time;
import ch.njol.skript.util.Timeperiod;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.WeatherType;

public class ClassesTest {

	@Test
	public void serializationTest() {
		Object[] random = {
				// Java
				(byte) 127, (short) 2000, -1600000, 1L << 40, -1.5f, 13.37,
				"String",
				
				// Skript
				SkriptColor.BLACK, StructureType.RED_MUSHROOM, WeatherType.THUNDER,
				new Date(System.currentTimeMillis()), new Timespan(1337), new Time(12000), new Timeperiod(1000, 23000),
				
				// Bukkit - simple classes only
				GameMode.ADVENTURE, InventoryType.CHEST, DamageCause.FALL,
				
				// there is also at least one variable for each class on my test server which are tested whenever the server shuts down.
		};
		for (Object o : random)
			Classes.serialize(o); // includes a deserialisation test
	}

}
