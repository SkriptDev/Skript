package ch.njol.skript.classes.data.bukkit;

import ch.njol.skript.bukkitutil.ParticleUtils;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.SkriptColor;
import org.bukkit.Particle;
import org.bukkit.Vibration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParticleClasses {

	private ParticleClasses() {
	}

	@SuppressWarnings("UnstableApiUsage")
	public static void init() {
		Classes.registerClass(new ClassInfo<>(Particle.class, "particle")
			.user("particles?")
			.name("Particle")
			.description("Represents a particle which can be used in the 'Particle Spawn' effect.",
				"Some particles require extra data, these are distinguished by their data type within the square brackets.",
				"DustOption, DustTransition and Vibration each have their own functions to build the appropriate data for these particles.",
				"NOTE: These are auto-generated and may differ between server versions.")
			.usage(ParticleUtils.getNamesAsString())
			.since("INSERT VERSION")
			.after("material", "blockdata")
			.parser(new Parser<>() {
				@Override
				public @Nullable Particle parse(String string, ParseContext context) {
					return ParticleUtils.parse(string);
				}

				@Override
				public String toString(Particle particle, int flags) {
					return ParticleUtils.getName(particle);
				}

				@Override
				public String toVariableNameString(Particle particle) {
					return "particle:" + ParticleUtils.getName(particle);
				}
			}));


		Classes.registerClass(new ClassInfo<>(Particle.DustOptions.class, "dustoption")
			.name(ClassInfo.NO_DOC).user("dust ?options?")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(Particle.DustOptions dustOption, int flags) {
					org.bukkit.Color bukkitColor = dustOption.getColor();
					int red = bukkitColor.getRed();
					int green = bukkitColor.getGreen();
					int blue = bukkitColor.getBlue();
					SkriptColor skriptColor = SkriptColor.fromBukkitColor(bukkitColor);

					String color;
					//noinspection ConstantConditions
					if (skriptColor != null) {
						color = skriptColor.toString();
					} else {
						color = String.format("rgb(%s,%s,%s)", red, green, blue);
					}
					return "dustOption(color=" + color + ",size=" + dustOption.getSize() + ")";
				}

				@Override
				public @NotNull String toVariableNameString(Particle.DustOptions o) {
					return toString(o, 0);
				}
			}));

		Classes.registerClass(new ClassInfo<>(Particle.DustTransition.class, "dusttransition")
			.name(ClassInfo.NO_DOC).user("dust ?transitions?"));

		Classes.registerClass(new ClassInfo<>(Vibration.class, "vibration")
			.name(ClassInfo.NO_DOC).user("vibrations?"));

		if (ParticleUtils.HAS_TRAIL && Classes.getExactClassInfo(Particle.Trail.class) == null) {
			Classes.registerClass(new ClassInfo<>(Particle.Trail.class, "trail")
				.name(ClassInfo.NO_DOC)
				.user("trails?"));
		}
	}

}
