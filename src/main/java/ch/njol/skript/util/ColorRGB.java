package ch.njol.skript.util;

import ch.njol.skript.variables.Variables;
import ch.njol.util.Math2;
import ch.njol.yggdrasil.Fields;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorRGB implements Color {

	private static final Pattern RGB_PATTERN = Pattern.compile("(?>rgb|RGB) (\\d+), (\\d+), (\\d+)");

	private org.bukkit.Color bukkit;
	@Nullable
	private DyeColor dye;
	
	public ColorRGB(int red, int green, int blue) {
		this.bukkit = org.bukkit.Color.fromRGB(
			Math2.fit(0, red, 255),
			Math2.fit(0, green, 255),
			Math2.fit(0, blue, 255));
		this.dye = DyeColor.getByColor(bukkit);
	}
	
	@Override
	public org.bukkit.Color asBukkitColor() {
		return bukkit;
	}
	
	@Override
	@Nullable
	public DyeColor asDyeColor() {
		return dye;
	}
	
	@Override
	public String getName() {
		return "rgb " + bukkit.getRed() + ", " + bukkit.getGreen() + ", " + bukkit.getBlue();
	}

	@Nullable
	public static ColorRGB fromString(String string) {
		Matcher matcher = RGB_PATTERN.matcher(string);
		if (!matcher.matches())
			return null;
		return new ColorRGB(
			NumberUtils.toInt(matcher.group(1)),
			NumberUtils.toInt(matcher.group(2)),
			NumberUtils.toInt(matcher.group(3))
		);
	}

	@Override
	public Fields serialize() throws NotSerializableException {
		return new Fields(this, Variables.yggdrasil);
	}
	
	@Override
	public void deserialize(Fields fields) throws StreamCorruptedException, NotSerializableException {
		org.bukkit.Color b = fields.getObject("bukkit", org.bukkit.Color.class);
		DyeColor d = fields.getObject("dye", DyeColor.class);
		if (b == null)
			return;
		if (d == null)
			dye = DyeColor.getByColor(b);
		else
			dye = d;
		bukkit = b;
	}
}
