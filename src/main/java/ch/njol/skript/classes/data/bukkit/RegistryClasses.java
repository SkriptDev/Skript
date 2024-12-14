package ch.njol.skript.classes.data.bukkit;

import ch.njol.skript.bukkitutil.RegistryUtils;
import ch.njol.skript.bukkitutil.TagUtils;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.Tag;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;

/**
 * Represents {@link ClassInfo ClassInfos} relating to {@link RegistryKey Registries} and {@link Tag Tags}
 */
@SuppressWarnings({"UnstableApiUsage", "rawtypes"})
public class RegistryClasses {

	public RegistryClasses() {
	}

	public static void init() {
		Classes.registerClass(new ClassInfo<>(RegistryKey.class, "registrykey")
			.user("registry ?keys?")
			.name("Registry Key")
			.description("Represents the different types of registries in Minecraft.",
				"The names in the square brackets represent the Skript Type the registry represents.",
				"Some registries might not be fully supported by Skript yet.",
				"The names are auto generated based on the key for the registry and may change anytime.")
			.since("INSERT VERSION")
			.usage(RegistryUtils.getAllNames())
			.supplier(RegistryUtils.getSupplier())
			.parser(new Parser<>() {
				@Override
				public @Nullable RegistryKey parse(String string, ParseContext context) {
					return RegistryUtils.getRegistryKeyByName(string);
				}

				@Override
				public String toString(RegistryKey registryKey, int flags) {
					return RegistryUtils.toString(registryKey);
				}

				@Override
				public String toVariableNameString(RegistryKey registryKey) {
					return RegistryUtils.toString(registryKey);
				}
			}));

		if (!TagUtils.HAS_TAG) return;
		Classes.registerClass(new ClassInfo<>(Tag.class, "tag")
			.user("tags?")
			.name("Tag")
			.description("Represents a Minecraft tag.",
				"See <a href='https://minecraft.wiki/w/Tag'>McWiki Tag</a> for more information on tags.")
			.requiredPlugins("Minecraft 1.21+")
			.since("INSERT VERSION")
			.serializer(new Serializer<>() {
				@Override
				public Fields serialize(Tag tag) {
					Fields fields = new Fields();
					fields.putObject("registry", tag.registryKey().key().toString());
					fields.putObject("tag", tag.tagKey().key().toString());
					return fields;
				}

				@Override
				protected Tag deserialize(Fields fields) throws StreamCorruptedException {
					String registry = fields.getObject("registry", String.class);
					String tag = fields.getObject("tag", String.class);
					Tag<?> serializedTag = TagUtils.getSerializedTag(registry, tag);
					if (serializedTag == null) {
						throw new StreamCorruptedException("No such tag: " + tag);
					}
					return serializedTag;
				}

				@Override
				public boolean mustSyncDeserialization() {
					return false;
				}

				@Override
				protected boolean canBeInstantiated() {
					return false;
				}
			}));
	}

}
