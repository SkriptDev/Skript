package org.skriptlang.skript.test.tests.events;

import ch.njol.skript.test.runner.SkriptJUnitTest;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Sheep;
import org.junit.Before;
import org.junit.Test;

public class DamageEventTest extends SkriptJUnitTest {

	private Sheep sheep;
	private Pillager pillager;

	static {
		// Set the delay to 1 tick. This allows the entities to be spawned into the world.
		setShutdownDelay(1);
	}

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() {
		this.sheep = spawnTestEntity(Sheep.class);
		this.sheep.setCustomName("Damage Event Sheep");
		this.pillager = spawnTestEntity(Pillager.class);
	}

	@Test
	public void testDamage() {
		this.sheep.damage(1, this.pillager);
	}

}
