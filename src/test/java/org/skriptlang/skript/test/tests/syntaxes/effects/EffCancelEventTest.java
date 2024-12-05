package org.skriptlang.skript.test.tests.syntaxes.effects;

import ch.njol.skript.test.runner.SkriptJUnitTest;
import org.bukkit.entity.Pig;
import org.junit.Test;

public class EffCancelEventTest extends SkriptJUnitTest {

	static {
		setShutdownDelay(1);
	}

	@Test
	public void test() {
		spawnTestEntity(Pig.class);
	}

}
