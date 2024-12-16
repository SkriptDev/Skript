package ch.njol.skript;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.hooks.Hook;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionInfo;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptEventInfo;
import ch.njol.skript.lang.Statement;
import ch.njol.skript.lang.SyntaxElementInfo;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.localization.Language;
import ch.njol.skript.localization.Message;
import ch.njol.skript.localization.PluralizingArgsMessage;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.log.LogEntry;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.log.Verbosity;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.update.ReleaseManifest;
import ch.njol.skript.update.ReleaseStatus;
import ch.njol.skript.util.EmptyStacktraceException;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.Utils;
import ch.njol.skript.util.Version;
import ch.njol.skript.util.chat.ChatMessages;
import ch.njol.util.Closeable;
import ch.njol.util.Kleenean;
import ch.njol.util.NullableChecker;
import ch.njol.util.StringUtils;
import ch.njol.util.coll.iterator.CheckedIterator;
import io.papermc.paper.ServerBuildInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.skriptlang.skript.lang.comparator.Comparator;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.converter.Converter;
import org.skriptlang.skript.lang.converter.Converters;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.experiment.ExperimentRegistry;
import org.skriptlang.skript.lang.script.Script;
import org.skriptlang.skript.lang.structure.Structure;
import org.skriptlang.skript.lang.structure.StructureInfo;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Skript</b> - A Bukkit plugin to modify how Minecraft behaves without having to write a single line of code (You'll likely be writing some code though if you're reading this
 * =P)
 * <p>
 * Use this class to extend this plugin's functionality by adding more {@link Condition conditions}, {@link Effect effects}, {@link SimpleExpression expressions}, etc.
 * <p>
 * If your plugin.yml contains <tt>'depend: [Skript]'</tt> then your plugin will not start at all if Skript is not present. Add <tt>'softdepend: [Skript]'</tt> to your plugin.yml
 * if you want your plugin to work even if Skript isn't present, but want to make sure that Skript gets loaded before your plugin.
 * <p>
 * If you use 'softdepend' you can test whether Skript is loaded with <tt>'Bukkit.getPluginManager().getPlugin(&quot;Skript&quot;) != null'</tt>
 * <p>
 * Once you made sure that Skript is loaded you can use <code>Skript.getInstance()</code> whenever you need a reference to the plugin, but you likely won't need it since all API
 * methods are static.
 *
 * @author Peter Güttinger
 * @see #registerAddon(JavaPlugin)
 * @see #registerCondition(Class, String...)
 * @see #registerEffect(Class, String...)
 * @see #registerExpression(Class, Class, ExpressionType, String...)
 * @see #registerEvent(String, Class, Class, String...)
 * @see EventValues#registerEventValue(Class, Class, Getter, int)
 * @see Classes#registerClass(ClassInfo)
 * @see Comparators#registerComparator(Class, Class, Comparator)
 * @see Converters#registerConverter(Class, Class, Converter)
 */
public final class Skript implements Listener {

	private static Skript instance;

	private SkriptPlugin plugin;

	public Skript(SkriptPlugin plugin) {
		if (instance != null) {
			throw new IllegalStateException("Skript already initialized");
		}
		instance = this;
		this.plugin = plugin;
	}

	/**
	 * Get an instance of {@link Skript}
	 *
	 * @return Instance of Skript
	 * @see #getSkriptPluginInstance()
	 */
	public static Skript getSkriptInstance() {
		if (instance == null) {
			throw new IllegalStateException("Skript has not initialized");
		}
		return instance;
	}

	/**
	 * Gets an instance of {@link SkriptPlugin}
	 *
	 * @return Instance of SkriptPlugin
	 * @deprecated Use {@link #getSkriptPluginInstance()} instead
	 */
	@Deprecated
	public static SkriptPlugin getInstance() {
		return getSkriptPluginInstance();
	}

	public static SkriptPlugin getSkriptPluginInstance() {
		if (instance == null || instance.plugin == null) {
			throw new IllegalStateException("Skript has not initialized");
		}
		return instance.plugin;
	}

	static final Set<Class<? extends Hook<?>>> disabledHookRegistrations = new HashSet<>();
	static boolean finishedLoadingHooks = false;
	@Nullable
	static Version version = null;
	public static final Message
		message_invalid_reload = new Message("skript.invalid reload"),
		message_finished_loading = new Message("skript.finished loading"),
		message_no_errors = new Message("skript.no errors"),
		message_no_scripts = new Message("skript.no scripts");
	static final PluralizingArgsMessage message_scripts_loaded = new PluralizingArgsMessage("skript.scripts loaded");
	@SuppressWarnings("null")
	final static Collection<Closeable> closeOnDisable = Collections.synchronizedCollection(new ArrayList<>());
	static boolean partDisabled = false;

	/**
	 * Current updater instance used by Skript.
	 */

	@Deprecated(forRemoval = true)
	// TODO this field will be replaced by a proper registry later
	static @UnknownNullability ExperimentRegistry experimentRegistry;

	private static Version minecraftVersion = new Version(666), UNKNOWN_VERSION = new Version(666);
	private static ServerPlatform serverPlatform = ServerPlatform.BUKKIT_UNKNOWN; // Start with unknown... onLoad changes this

	/**
	 * Check minecraft version and assign it to minecraftVersion field
	 * This method is created to update MC version before onEnable method
	 * To fix {@link Utils#HEX_SUPPORTED} being assigned before minecraftVersion is properly assigned
	 */
	public static void updateMinecraftVersion() {
		String bukkitV = Bukkit.getBukkitVersion();
		Matcher m = Pattern.compile("\\d+\\.\\d+(\\.\\d+)?").matcher(bukkitV);
		if (!m.find()) {
			minecraftVersion = new Version(666, 0, 0);
		} else {
			minecraftVersion = new Version("" + m.group());
		}
	}

	public static Version getVersion() {
		return version;
	}

	public static ServerPlatform getServerPlatform() {
		if (classExists("net.glowstone.GlowServer")) {
			return ServerPlatform.BUKKIT_GLOWSTONE;
		} else if (classExists("io.papermc.paper.ServerBuildInfo")) {
			return ServerPlatform.BUKKIT_PAPER;
		} else if (classExists("org.spigotmc.SpigotConfig")) {
			return ServerPlatform.BUKKIT_SPIGOT;
		} else if (classExists("org.bukkit.craftbukkit.CraftServer") || classExists("org.bukkit.craftbukkit.Main")) {
			// At some point, CraftServer got removed or moved
			return ServerPlatform.BUKKIT_CRAFTBUKKIT;
		} else { // Probably some ancient Bukkit implementation
			return ServerPlatform.BUKKIT_UNKNOWN;
		}
	}

	/**
	 * Checks if server software and Minecraft version are supported.
	 * Prints errors or warnings to console if something is wrong.
	 *
	 * @return Whether Skript can continue loading at all.
	 */
	static boolean checkServerPlatform() {
		String bukkitV = Bukkit.getBukkitVersion();
		Matcher m = Pattern.compile("\\d+\\.\\d+(\\.\\d+)?").matcher(bukkitV);
		if (!m.find()) {
			Skript.error("The Bukkit version '" + bukkitV + "' does not contain a version number which is required for Skript to enable or disable certain features. " +
				"Skript will still work, but you might get random errors if you use features that are not available in your version of Bukkit.");
			minecraftVersion = new Version(666, 0, 0);
		} else {
			minecraftVersion = new Version("" + m.group());
		}
		Skript.debug("Loading for Minecraft " + minecraftVersion);

		// Check that MC version is supported
		if (!isRunningMinecraft(1, 20, 6)) {
			// Prevent loading when not running at least Minecraft 1.9
			Skript.error("This version of Skript does not work with Minecraft " + minecraftVersion + " and requires Minecraft 1.20.6+");
			Skript.error("You probably want Skript 2.x from SkriptLang (Google to find where to get it)");
			Skript.error("Note that those versions are, of course, completely unsupported by SkriptDev!");
			return false;
		}

		// Check that current server platform is somewhat supported
		serverPlatform = getServerPlatform();
		Skript.debug("Server platform: " + serverPlatform);
		if (!serverPlatform.works) {
			Skript.error("It seems that this server platform (" + serverPlatform.name + ") does not work with Skript.");
			if (SkriptConfig.allowUnsafePlatforms.value()) {
				Skript.error("However, you have chosen to ignore this. Skript will probably still not work.");
			} else {
				Skript.error("To prevent potentially unsafe behaviour, Skript has been disabled.");
				Skript.error("You may re-enable it by adding a configuration option 'allow unsafe platforms: true'");
				Skript.error("Note that it is unlikely that Skript works correctly even if you do so.");
				Skript.error("A better idea would be to install Paper or Spigot in place of your current server.");
				return false;
			}
		} else if (!serverPlatform.supported) {
			Skript.warning("This server platform (" + serverPlatform.name + ") is not supported by Skript.");
			Skript.warning("It may still work, but if it does not, you are on your own.");
			Skript.warning("Skript officially supports Paper and its forks.");
		}

		// If nothing got triggered, everything is probably ok
		return true;
	}

	/**
	 * Checks whether a hook has been enabled.
	 *
	 * @param hook The hook to check.
	 * @return Whether the hook is enabled.
	 * @see #disableHookRegistration(Class[])
	 */
	public static boolean isHookEnabled(Class<? extends Hook<?>> hook) {
		return !disabledHookRegistrations.contains(hook);
	}

	/**
	 * @return whether hooks have been loaded,
	 * and if {@link #disableHookRegistration(Class[])} won't error because of this.
	 */
	public static boolean isFinishedLoadingHooks() {
		return finishedLoadingHooks;
	}

	/**
	 * Disables the registration for the given hook classes. If Skript has been enabled, this method
	 * will throw an API exception. It should be used in something like {@link JavaPlugin#onLoad()}.
	 *
	 * @param hooks The hooks to disable the registration of.
	 * @see #isHookEnabled(Class)
	 */
	@SafeVarargs
	public static void disableHookRegistration(Class<? extends Hook<?>>... hooks) {
		if (finishedLoadingHooks) { // Hooks have been registered if Skript is enabled
			throw new SkriptAPIException("Disabling hooks is not possible after Skript has been enabled!");
		}
		Collections.addAll(disabledHookRegistrations, hooks);
	}

	/**
	 * The folder containing all Scripts.
	 * Never reference this field directly. Use {@link #getScriptsFolder()}.
	 */
	File scriptsFolder;

	/**
	 * @return The manager for experimental, optional features.
	 */
	public static ExperimentRegistry experiments() {
		return experimentRegistry;
	}

	public static void setInstance(Skript instance) {
		Skript.instance = instance;
	}

	/**
	 * @return The folder containing all Scripts.
	 */
	public File getScriptsFolder() {
		if (!scriptsFolder.isDirectory())
			//noinspection ResultOfMethodCallIgnored
			scriptsFolder.mkdirs();
		return scriptsFolder;
	}

	public static Version getMinecraftVersion() {
		return minecraftVersion;
	}

	/**
	 * @return Whether this server is running Minecraft <tt>major.minor</tt> or higher
	 */
	public static boolean isRunningMinecraft(final int major, final int minor) {
		if (minecraftVersion.compareTo(UNKNOWN_VERSION) == 0) { // Make sure minecraftVersion is properly assigned.
			updateMinecraftVersion();
		}
		return minecraftVersion.compareTo(major, minor) >= 0;
	}

	public static boolean isRunningMinecraft(final int major, final int minor, final int revision) {
		if (minecraftVersion.compareTo(UNKNOWN_VERSION) == 0) {
			updateMinecraftVersion();
		}
		return minecraftVersion.compareTo(major, minor, revision) >= 0;
	}

	public static boolean isRunningMinecraft(final Version v) {
		if (minecraftVersion.compareTo(UNKNOWN_VERSION) == 0) {
			updateMinecraftVersion();
		}
		return minecraftVersion.compareTo(v) >= 0;
	}

	/**
	 * Tests whether a given class exists in the classpath.
	 *
	 * @param className The {@link Class#getCanonicalName() canonical name} of the class
	 * @return Whether the given class exists.
	 */
	public static boolean classExists(final String className) {
		try {
			Class.forName(className);
			return true;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * Tests whether a method exists in the given class.
	 *
	 * @param c              The class
	 * @param methodName     The name of the method
	 * @param parameterTypes The parameter types of the method
	 * @return Whether the given method exists.
	 */
	public static boolean methodExists(final Class<?> c, final String methodName, final Class<?>... parameterTypes) {
		try {
			c.getDeclaredMethod(methodName, parameterTypes);
			return true;
		} catch (final NoSuchMethodException e) {
			return false;
		} catch (final SecurityException e) {
			return false;
		}
	}

	/**
	 * Tests whether a method exists in the given class, and whether the return type matches the expected one.
	 * <p>
	 * Note that this method doesn't work properly if multiple methods with the same name and parameters exist but have different return types.
	 *
	 * @param c              The class
	 * @param methodName     The name of the method
	 * @param parameterTypes The parameter types of the method
	 * @param returnType     The expected return type
	 * @return Whether the given method exists.
	 */
	public static boolean methodExists(final Class<?> c, final String methodName, final Class<?>[] parameterTypes, final Class<?> returnType) {
		try {
			final Method m = c.getDeclaredMethod(methodName, parameterTypes);
			return m.getReturnType() == returnType;
		} catch (final NoSuchMethodException e) {
			return false;
		} catch (final SecurityException e) {
			return false;
		}
	}

	/**
	 * Tests whether a field exists in the given class.
	 *
	 * @param c         The class
	 * @param fieldName The name of the field
	 * @return Whether the given field exists.
	 */
	public static boolean fieldExists(final Class<?> c, final String fieldName) {
		try {
			c.getDeclaredField(fieldName);
			return true;
		} catch (final NoSuchFieldException e) {
			return false;
		} catch (final SecurityException e) {
			return false;
		}
	}

	// ================ CONSTANTS, OPTIONS & OTHER ================

	public final static String SCRIPTSFOLDER = "scripts";

	public static void outdatedError() {
		error("Skript v" + SkriptPlugin.getInstance().getDescription().getVersion() + " is not fully compatible with Bukkit " + Bukkit.getVersion() + ". Some feature(s) will be broken until you update Skript.");
	}

	public static void outdatedError(final Exception e) {
		outdatedError();
		if (testing())
			e.printStackTrace();
	}

	/**
	 * A small value, useful for comparing doubles or floats.
	 * <p>
	 * E.g. to test whether two floating-point numbers are equal:
	 *
	 * <pre>
	 * Math.abs(a - b) &lt; Skript.EPSILON
	 * </pre>
	 * <p>
	 * or whether a location is within a specific radius of another location:
	 *
	 * <pre>
	 * location.distanceSquared(center) - radius * radius &lt; Skript.EPSILON
	 * </pre>
	 *
	 * @see #EPSILON_MULT
	 */
	public final static double EPSILON = 1e-10;
	/**
	 * A value a bit larger than 1
	 *
	 * @see #EPSILON
	 */
	public final static double EPSILON_MULT = 1.00001;

	// TODO localise Infinity, -Infinity, NaN (and decimal point?)
	public static String toString(final double n) {
		return StringUtils.toString(n, SkriptConfig.numberAccuracy.value());
	}

	public final static UncaughtExceptionHandler UEH = new UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(final @Nullable Thread t, final @Nullable Throwable e) {
			Skript.exception(e, "Exception in thread " + (t == null ? null : t.getName()));
		}
	};

	/**
	 * Creates a new Thread and sets its UncaughtExceptionHandler. The Thread is not started automatically.
	 */
	public static Thread newThread(final Runnable r, final String name) {
		final Thread t = new Thread(r, name);
		t.setUncaughtExceptionHandler(UEH);
		return t;
	}

	// ================ REGISTRATIONS ================

	private static boolean acceptRegistrations = true;

	public static boolean isAcceptRegistrations() {
		SkriptPlugin instance = SkriptPlugin.getInstance();
		if (instance == null)
			throw new IllegalStateException("Skript was never loaded");
		return acceptRegistrations && instance.isEnabled();
	}

	public static void checkAcceptRegistrations() {
		if (!isAcceptRegistrations() && !Skript.testing())
			throw new SkriptAPIException("Registration can only be done during plugin initialization");
	}

	static void stopAcceptingRegistrations() {
		Converters.createChainedConverters();

		acceptRegistrations = false;

		Classes.onRegistrationsStop();
	}

	// ================ ADDONS ================

	private final static HashMap<String, SkriptAddon> addons = new HashMap<>();

	/**
	 * Registers an addon to Skript. This is currently not required for addons to work, but the returned {@link SkriptAddon} provides useful methods for registering syntax elements
	 * and adding new strings to Skript's localization system (e.g. the required "types.[type]" strings for registered classes).
	 *
	 * @param p The plugin
	 */
	public static SkriptAddon registerAddon(final JavaPlugin p) {
		checkAcceptRegistrations();
		if (addons.containsKey(p.getName()))
			throw new IllegalArgumentException("The plugin " + p.getName() + " is already registered");
		final SkriptAddon addon = new SkriptAddon(p);
		addons.put(p.getName(), addon);
		return addon;
	}

	@Nullable
	public static SkriptAddon getAddon(final JavaPlugin p) {
		return addons.get(p.getName());
	}

	@Nullable
	public static SkriptAddon getAddon(final String name) {
		return addons.get(name);
	}

	@SuppressWarnings("null")
	public static Collection<SkriptAddon> getAddons() {
		return Collections.unmodifiableCollection(addons.values());
	}

	// ================ CONDITIONS & EFFECTS & SECTIONS ================

	private static final Collection<SyntaxElementInfo<? extends Condition>> conditions = new ArrayList<>(50);
	private static final Collection<SyntaxElementInfo<? extends Effect>> effects = new ArrayList<>(50);
	private static final Collection<SyntaxElementInfo<? extends Statement>> statements = new ArrayList<>(100);
	private static final Collection<SyntaxElementInfo<? extends Section>> sections = new ArrayList<>(50);

	/**
	 * registers a {@link Condition}.
	 *
	 * @param condition The condition's class
	 * @param patterns  Skript patterns to match this condition
	 */
	public static <E extends Condition> void registerCondition(final Class<E> condition, final String... patterns) throws IllegalArgumentException {
		checkAcceptRegistrations();
		String originClassPath = Thread.currentThread().getStackTrace()[2].getClassName();
		final SyntaxElementInfo<E> info = new SyntaxElementInfo<>(patterns, condition, originClassPath);
		conditions.add(info);
		statements.add(info);
	}

	/**
	 * Registers an {@link Effect}.
	 *
	 * @param effect   The effect's class
	 * @param patterns Skript patterns to match this effect
	 */
	public static <E extends Effect> void registerEffect(final Class<E> effect, final String... patterns) throws IllegalArgumentException {
		checkAcceptRegistrations();
		String originClassPath = Thread.currentThread().getStackTrace()[2].getClassName();
		final SyntaxElementInfo<E> info = new SyntaxElementInfo<>(patterns, effect, originClassPath);
		effects.add(info);
		statements.add(info);
	}

	/**
	 * Registers a {@link Section}.
	 *
	 * @param section  The section's class
	 * @param patterns Skript patterns to match this section
	 * @see Section
	 */
	public static <E extends Section> void registerSection(Class<E> section, String... patterns) throws IllegalArgumentException {
		checkAcceptRegistrations();
		String originClassPath = Thread.currentThread().getStackTrace()[2].getClassName();
		SyntaxElementInfo<E> info = new SyntaxElementInfo<>(patterns, section, originClassPath);
		sections.add(info);
	}

	public static Collection<SyntaxElementInfo<? extends Statement>> getStatements() {
		return statements;
	}

	public static Collection<SyntaxElementInfo<? extends Condition>> getConditions() {
		return conditions;
	}

	public static Collection<SyntaxElementInfo<? extends Effect>> getEffects() {
		return effects;
	}

	public static Collection<SyntaxElementInfo<? extends Section>> getSections() {
		return sections;
	}

	// ================ EXPRESSIONS ================

	private final static List<ExpressionInfo<?, ?>> expressions = new ArrayList<>(100);

	private final static int[] expressionTypesStartIndices = new int[ExpressionType.values().length];

	/**
	 * Registers an expression.
	 *
	 * @param c          The expression's class
	 * @param returnType The superclass of all values returned by the expression
	 * @param type       The expression's {@link ExpressionType type}. This is used to determine in which order to try to parse expressions.
	 * @param patterns   Skript patterns that match this expression
	 * @throws IllegalArgumentException if returnType is not a normal class
	 */
	public static <E extends Expression<T>, T> void registerExpression(final Class<E> c, final Class<T> returnType, final ExpressionType type, final String... patterns) throws IllegalArgumentException {
		checkAcceptRegistrations();
		if (returnType.isAnnotation() || returnType.isArray() || returnType.isPrimitive())
			throw new IllegalArgumentException("returnType must be a normal type");
		String originClassPath = Thread.currentThread().getStackTrace()[2].getClassName();
		final ExpressionInfo<E, T> info = new ExpressionInfo<>(patterns, returnType, c, originClassPath, type);
		expressions.add(expressionTypesStartIndices[type.ordinal()], info);
		for (int i = type.ordinal(); i < ExpressionType.values().length; i++) {
			expressionTypesStartIndices[i]++;
		}
	}

	@SuppressWarnings("null")
	public static Iterator<ExpressionInfo<?, ?>> getExpressions() {
		return expressions.iterator();
	}

	public static Iterator<ExpressionInfo<?, ?>> getExpressions(final Class<?>... returnTypes) {
		return new CheckedIterator<>(getExpressions(), new NullableChecker<ExpressionInfo<?, ?>>() {
			@Override
			public boolean check(final @Nullable ExpressionInfo<?, ?> i) {
				if (i == null || i.returnType == Object.class)
					return true;
				for (final Class<?> returnType : returnTypes) {
					assert returnType != null;
					if (Converters.converterExists(i.returnType, returnType))
						return true;
				}
				return false;
			}
		});
	}

	// ================ EVENTS ================

	private static final List<SkriptEventInfo<?>> events = new ArrayList<>(50);
	private static final List<StructureInfo<? extends Structure>> structures = new ArrayList<>(10);

	/**
	 * Registers an event.
	 *
	 * @param name     Capitalised name of the event without leading "On" which is added automatically (Start the name with an asterisk to prevent this). Used for error messages and
	 *                 the documentation.
	 * @param c        The event's class
	 * @param event    The Bukkit event this event applies to
	 * @param patterns Skript patterns to match this event
	 * @return A SkriptEventInfo representing the registered event. Used to generate Skript's documentation.
	 */
	@SuppressWarnings("unchecked")
	public static <E extends SkriptEvent> SkriptEventInfo<E> registerEvent(String name, Class<E> c, Class<? extends Event> event, String... patterns) {
		return registerEvent(name, c, new Class[]{event}, patterns);
	}

	/**
	 * Registers an event.
	 *
	 * @param name     The name of the event, used for error messages
	 * @param c        The event's class
	 * @param events   The Bukkit events this event applies to
	 * @param patterns Skript patterns to match this event
	 * @return A SkriptEventInfo representing the registered event. Used to generate Skript's documentation.
	 */
	public static <E extends SkriptEvent> SkriptEventInfo<E> registerEvent(String name, Class<E> c, Class<? extends Event>[] events, String... patterns) {
		checkAcceptRegistrations();
		String originClassPath = Thread.currentThread().getStackTrace()[2].getClassName();

		String[] transformedPatterns = new String[patterns.length];
		for (int i = 0; i < patterns.length; i++)
			transformedPatterns[i] = SkriptEvent.fixPattern(patterns[i]);

		SkriptEventInfo<E> r = new SkriptEventInfo<>(name, transformedPatterns, c, originClassPath, events);
		Skript.events.add(r);
		return r;
	}

	public static <E extends Structure> void registerStructure(Class<E> c, String... patterns) {
		checkAcceptRegistrations();
		String originClassPath = Thread.currentThread().getStackTrace()[2].getClassName();
		StructureInfo<E> structureInfo = new StructureInfo<>(patterns, c, originClassPath);
		structures.add(structureInfo);
	}

	public static <E extends Structure> void registerSimpleStructure(Class<E> c, String... patterns) {
		checkAcceptRegistrations();
		String originClassPath = Thread.currentThread().getStackTrace()[2].getClassName();
		StructureInfo<E> structureInfo = new StructureInfo<>(patterns, c, originClassPath, true);
		structures.add(structureInfo);
	}

	public static <E extends Structure> void registerStructure(Class<E> c, EntryValidator entryValidator, String... patterns) {
		checkAcceptRegistrations();
		String originClassPath = Thread.currentThread().getStackTrace()[2].getClassName();
		StructureInfo<E> structureInfo = new StructureInfo<>(patterns, c, originClassPath, entryValidator);
		structures.add(structureInfo);
	}

	public static Collection<SkriptEventInfo<?>> getEvents() {
		return events;
	}

	public static List<StructureInfo<? extends Structure>> getStructures() {
		return structures;
	}

	// ================ COMMANDS ================

	/**
	 * Dispatches a command with calling command events
	 *
	 * @param sender
	 * @param command
	 * @return Whether the command was run
	 */
	public static boolean dispatchCommand(final CommandSender sender, final String command) {
		try {
			if (sender instanceof Player) {
				final PlayerCommandPreprocessEvent e = new PlayerCommandPreprocessEvent((Player) sender, "/" + command);
				Bukkit.getPluginManager().callEvent(e);
				if (e.isCancelled() || !e.getMessage().startsWith("/"))
					return false;
				return Bukkit.dispatchCommand(e.getPlayer(), e.getMessage().substring(1));
			} else {
				final ServerCommandEvent e = new ServerCommandEvent(sender, command);
				Bukkit.getPluginManager().callEvent(e);
				if (e.getCommand().isEmpty() || e.isCancelled())
					return false;
				return Bukkit.dispatchCommand(e.getSender(), e.getCommand());
			}
		} catch (final Exception ex) {
			ex.printStackTrace(); // just like Bukkit
			return false;
		}
	}

	// ================ LOGGING ================

	public static boolean logNormal() {
		return SkriptLogger.log(Verbosity.NORMAL);
	}

	public static boolean logHigh() {
		return SkriptLogger.log(Verbosity.HIGH);
	}

	public static boolean logVeryHigh() {
		return SkriptLogger.log(Verbosity.VERY_HIGH);
	}

	public static boolean debug() {
		return SkriptLogger.debug();
	}

	public static boolean testing() {
		return debug() || Skript.class.desiredAssertionStatus();
	}

	public static boolean log(final Verbosity minVerb) {
		return SkriptLogger.log(minVerb);
	}

	public static void debug(final String info) {
		if (!debug())
			return;
		SkriptLogger.log(SkriptLogger.DEBUG, info);
	}

	/**
	 * @see SkriptLogger#log(Level, String)
	 */
	@SuppressWarnings("null")
	public static void info(final String info) {
		SkriptLogger.log(Level.INFO, info);
	}

	/**
	 * @see SkriptLogger#log(Level, String)
	 */
	@SuppressWarnings("null")
	public static void warning(final String warning) {
		SkriptLogger.log(Level.WARNING, warning);
	}

	/**
	 * @see SkriptLogger#log(Level, String)
	 */
	@SuppressWarnings("null")
	public static void error(final @Nullable String error) {
		if (error != null)
			SkriptLogger.log(Level.SEVERE, error);
	}

	/**
	 * Use this in {@link Expression#init(Expression[], int, Kleenean, ch.njol.skript.lang.SkriptParser.ParseResult)} (and other methods that are called during the parsing) to log
	 * errors with a specific {@link ErrorQuality}.
	 *
	 * @param error
	 * @param quality
	 */
	public static void error(final String error, final ErrorQuality quality) {
		SkriptLogger.log(new LogEntry(SkriptLogger.SEVERE, quality, error));
	}

	private final static String EXCEPTION_PREFIX = "#!#! ";

	/**
	 * Used if something happens that shouldn't happen
	 *
	 * @param info Description of the error and additional information
	 * @return an EmptyStacktraceException to throw if code execution should terminate.
	 */
	public static EmptyStacktraceException exception(final String... info) {
		return exception(null, info);
	}

	public static EmptyStacktraceException exception(final @Nullable Throwable cause, final String... info) {
		return exception(cause, null, null, info);
	}

	public static EmptyStacktraceException exception(final @Nullable Throwable cause, final @Nullable Thread thread, final String... info) {
		return exception(cause, thread, null, info);
	}

	public static EmptyStacktraceException exception(final @Nullable Throwable cause, final @Nullable TriggerItem item, final String... info) {
		return exception(cause, null, item, info);
	}

	/**
	 * Maps Java packages of plugins to descriptions of said plugins.
	 * This is only done for plugins that depend or soft-depend on Skript.
	 */
	private static Map<String, PluginDescriptionFile> pluginPackages = new HashMap<>();
	private static boolean checkedPlugins = false;

	/**
	 * Set by Skript when doing something that users shouldn't do.
	 */
	static boolean tainted = false;

	/**
	 * Set to true when an exception is thrown.
	 */
	static boolean errored = false;

	/**
	 * Mark that an exception has occurred at some point during runtime.
	 * Only used for Skript's testing system.
	 */
	public static void markErrored() {
		errored = true;
	}

	/**
	 * Used if something happens that shouldn't happen
	 *
	 * @param cause exception that shouldn't occur
	 * @param info  Description of the error and additional information
	 * @return an EmptyStacktraceException to throw if code execution should terminate.
	 */
	public static EmptyStacktraceException exception(@Nullable Throwable cause, final @Nullable Thread thread, final @Nullable TriggerItem item, final String... info) {
		errored = true;

		// Don't send full exception message again, when caught exception (likely) comes from this method
		if (cause instanceof EmptyStacktraceException) {
			return new EmptyStacktraceException();
		}

		// First error: gather plugin package information
		if (!checkedPlugins) {
			for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
				if (plugin.getName().equals("Skript")) // Don't track myself!
					continue;

				PluginDescriptionFile desc = plugin.getDescription();
				if (desc.getDepend().contains("Skript") || desc.getSoftDepend().contains("Skript")) {
					// Take actual main class out from the qualified name
					String[] parts = desc.getMain().split("\\."); // . is special in regexes...
					StringBuilder name = new StringBuilder(desc.getMain().length());
					for (int i = 0; i < parts.length - 1; i++) {
						name.append(parts[i]).append('.');
					}

					// Put this to map
					pluginPackages.put(name.toString(), desc);
					if (Skript.debug())
						Skript.info("Identified potential addon: " + desc.getFullName() + " (" + name.toString() + ")");
				}
			}

			checkedPlugins = true; // No need to do this next time
		}

		String issuesUrl = "https://github.com/SkriptLang/Skript/issues";

		logEx();
		logEx("[Skript] Severe Error:");
		logEx(info);
		logEx();

		// Parse something useful out of the stack trace
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		Set<PluginDescriptionFile> stackPlugins = new HashSet<>();
		for (StackTraceElement s : stackTrace) { // Look through stack trace
			for (Entry<String, PluginDescriptionFile> e : pluginPackages.entrySet()) { // Look through plugins
				if (s.getClassName().contains(e.getKey())) // Hey, is this plugin in that stack trace?
					stackPlugins.add(e.getValue()); // Yes? Add it to list
			}
		}

		SkriptUpdater updater = Skript.getSkriptInstance().getUpdater();

		// Check if server platform is supported
		if (tainted) {
			logEx("Skript is running with developer command-line options.");
			logEx("If you are not a developer, consider disabling them.");
		} else if (SkriptPlugin.getInstance().getDescription().getVersion().contains("nightly")) {
			logEx("You're running a (buggy) nightly version of Skript.");
			logEx("If this is not a test server, switch to a more stable release NOW!");
			logEx("Your players are unlikely to appreciate crashes and/or data loss due to Skript bugs.");
			logEx("");
			logEx("Just testing things? Good. Please report this bug, so that we can fix it before a stable release.");
			logEx("Issue tracker: " + issuesUrl);
		} else if (!serverPlatform.supported) {
			logEx("Your server platform appears to be unsupported by Skript. It might not work reliably.");
			logEx("You can report this at " + issuesUrl + ". However, we may be unable to fix the issue.");
			logEx("It is recommended that you switch to Paper or Spigot, should you encounter more problems.");
		} else if (updater != null && updater.getReleaseStatus() == ReleaseStatus.OUTDATED) {
			logEx("You're running outdated version of Skript! Please try updating it NOW; it might fix this.");
			logEx("Run /sk update check to get a download link to latest Skript!");
			logEx("You will be given instructions how to report this error if it persists after update.");
		} else {
			logEx("Something went horribly wrong with Skript.");
			logEx("This issue is NOT your fault! You probably can't fix it yourself, either.");
			if (pluginPackages.isEmpty()) {
				logEx("You should report it at " + issuesUrl + ". Please copy paste this report there (or use paste service).");
				logEx("This ensures that your issue is noticed and will be fixed as soon as possible.");
			} else {
				logEx("It looks like you are using some plugin(s) that alter how Skript works (addons).");
				if (stackPlugins.isEmpty()) {
					logEx("Here is full list of them:");
					for (PluginDescriptionFile desc : pluginPackages.values()) {
						StringBuilder pluginsMessage = new StringBuilder(" - ");
						pluginsMessage.append(desc.getFullName());
						String website = desc.getWebsite();
						if (website != null && !website.isEmpty()) // Add website if found
							pluginsMessage.append(" (").append(desc.getWebsite()).append(")");
						logEx(pluginsMessage.toString());
					}
					logEx("We could not identify which of those are specially related, so this might also be Skript issue.");
				} else {
					logEx("Following plugins are probably related to this error in some way:");
					for (PluginDescriptionFile desc : stackPlugins) {
						StringBuilder pluginsMessage = new StringBuilder(" - ");
						pluginsMessage.append(desc.getName());
						String website = desc.getWebsite();
						if (website != null && !website.isEmpty()) // Add website if found
							pluginsMessage.append(" (").append(desc.getWebsite()).append(")");

						logEx(pluginsMessage.toString());
					}
				}

				logEx("You should try disabling those plugins one by one, trying to find which one causes it.");
				logEx("If the error doesn't disappear even after disabling all listed plugins, it is probably Skript issue.");
				logEx("In that case, you will be given instruction on how should you report it.");
				logEx("On the other hand, if the error disappears when disabling some plugin, report it to author of that plugin.");
				logEx("Only if the author tells you to do so, report it to Skript's issue tracker.");
			}
		}

		logEx();
		logEx("Stack trace:");
		if (cause == null || cause.getStackTrace().length == 0) {
			logEx("  warning: no/empty exception given, dumping current stack trace instead");
			cause = new Exception(cause);
		}
		boolean first = true;
		while (cause != null) {
			logEx((first ? "" : "Caused by: ") + cause.toString());
			for (final StackTraceElement e : cause.getStackTrace())
				logEx("    at " + e.toString());
			cause = cause.getCause();
			first = false;
		}

		logEx();
		logEx("Version Information:");
		if (updater != null) {
			ReleaseStatus status = updater.getReleaseStatus();
			logEx("  Skript: " + getVersion() + (status == ReleaseStatus.LATEST ? " (latest)"
				: status == ReleaseStatus.OUTDATED ? " (OUTDATED)"
				: status == ReleaseStatus.CUSTOM ? " (custom version)" : ""));
			ReleaseManifest current = updater.getCurrentRelease();
			logEx("    Flavor: " + current.flavor);
			logEx("    Date: " + current.date);
		} else {
			logEx("  Skript: " + getVersion() + " (unknown; likely custom)");
		}
		ServerBuildInfo serverBuildInfo = ServerBuildInfo.buildInfo();
		logEx("  Server Platform: " + serverBuildInfo.brandId());
		logEx("  Server Version: " + serverBuildInfo.asString(ServerBuildInfo.StringRepresentation.VERSION_FULL).split(" ")[0]);
		logEx("  Minecraft Version: " + serverBuildInfo.minecraftVersionId());
		logEx("  Java: " + System.getProperty("java.version") + " (" + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + ")");
		logEx("  OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
		logEx();
		logEx("Current node: " + SkriptLogger.getNode());
		logEx("Current item: " + (item == null ? "null" : item.toString(null, true)));
		if (item != null && item.getTrigger() != null) {
			Trigger trigger = item.getTrigger();
			Script script = trigger.getScript();
			logEx("Current trigger: " + trigger.toString(null, true) + " (" + (script == null ? "null" : script.getConfig().getFileName()) + ", line " + trigger.getLineNumber() + ")");
		}
		logEx();
		logEx("Thread: " + (thread == null ? Thread.currentThread() : thread).getName());
		logEx();
		logEx("Language: " + Language.getName());
		logEx("Link parse mode: " + ChatMessages.linkParseMode);
		logEx();
		logEx("End of Error.");
		logEx();

		return new EmptyStacktraceException();
	}

	static void logEx() {
		SkriptLogger.LOGGER.severe(EXCEPTION_PREFIX);
	}

	static void logEx(final String... lines) {
		for (final String line : lines)
			SkriptLogger.LOGGER.severe(EXCEPTION_PREFIX + line);
	}

	private static final Message SKRIPT_PREFIX_MESSAGE = new Message("skript.prefix");

	public static String getSkriptPrefix() {
		return SKRIPT_PREFIX_MESSAGE.getValueOrDefault("<grey>[<gold>Skript<grey>] <reset>");
	}

	public static void info(final CommandSender sender, final String info) {
		sender.sendMessage(Utils.replaceEnglishChatStyles(getSkriptPrefix() + info));
	}

	/**
	 * @param message
	 * @param permission
	 * @see #adminBroadcast(String)
	 */
	public static void broadcast(final String message, final String permission) {
		Bukkit.broadcast(Utils.replaceEnglishChatStyles(getSkriptPrefix() + message), permission);
	}

	public static void adminBroadcast(final String message) {
		broadcast(message, "skript.admin");
	}

	/**
	 * Similar to {@link #info(CommandSender, String)} but no [Skript] prefix is added.
	 *
	 * @param sender
	 * @param info
	 */
	public static void message(final CommandSender sender, final String info) {
		sender.sendMessage(Utils.replaceEnglishChatStyles(info));
	}

	public static void error(final CommandSender sender, final String error) {
		sender.sendMessage(Utils.replaceEnglishChatStyles(getSkriptPrefix() + ChatColor.DARK_RED + error));
	}

	/**
	 * Gets the updater instance currently used by Skript.
	 *
	 * @return SkriptUpdater instance.
	 */
	@Nullable
	public SkriptUpdater getUpdater() {
		return SkriptPlugin.getInstance().updater;
	}

}
