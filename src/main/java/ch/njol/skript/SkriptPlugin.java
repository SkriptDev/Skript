package ch.njol.skript;

import ch.njol.skript.classes.data.JavaClasses;
import ch.njol.skript.classes.data.SkriptClasses;
import ch.njol.skript.classes.data.bukkit.BukkitClasses;
import ch.njol.skript.classes.data.defaults.DefaultValues;
import ch.njol.skript.command.Commands;
import ch.njol.skript.doc.Documentation;
import ch.njol.skript.events.EvtSkript;
import ch.njol.skript.hooks.Hook;
import ch.njol.skript.localization.Language;
import ch.njol.skript.log.BukkitLoggerFilter;
import ch.njol.skript.log.CountingLogHandler;
import ch.njol.skript.log.ErrorDescLogHandler;
import ch.njol.skript.log.LogEntry;
import ch.njol.skript.log.LogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.log.Verbosity;
import ch.njol.skript.registrations.Feature;
import ch.njol.skript.test.runner.EffObjectives;
import ch.njol.skript.test.runner.SkriptJUnitTest;
import ch.njol.skript.test.runner.SkriptTestEvent;
import ch.njol.skript.test.runner.TestMode;
import ch.njol.skript.test.runner.TestTracker;
import ch.njol.skript.update.ReleaseStatus;
import ch.njol.skript.update.UpdateManifest;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.ExceptionUtils;
import ch.njol.skript.util.FileUtils;
import ch.njol.skript.util.Task;
import ch.njol.skript.util.Utils;
import ch.njol.skript.util.Version;
import ch.njol.skript.util.chat.BungeeConverter;
import ch.njol.skript.util.chat.ChatMessages;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Closeable;
import ch.njol.util.StringUtils;
import ch.njol.util.coll.iterator.EnumerationIterable;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.junit.After;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.skriptlang.skript.bukkit.SkriptMetrics;
import org.skriptlang.skript.lang.experiment.ExperimentRegistry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class SkriptPlugin extends JavaPlugin implements Listener {

	// ===== STATIC =====

	private static SkriptPlugin instance;
	private static boolean disabled = false;

	public static SkriptPlugin getInstance() {
		if (instance == null)
			throw new IllegalStateException();
		return instance;
	}

	// ===== CLASS =====

	@Nullable
	private Metrics metrics;
	@Nullable
	private SkriptAddon addon;
	@Nullable
	SkriptUpdater updater;
	private Skript skript;

	@SuppressWarnings({"removal", "UnstableApiUsage", "deprecation", "ThrowableNotThrown", "ResultOfMethodCallIgnored"})
	@Override
	public void onEnable() {
		if (this.skript != null || instance != null) {
			throw new IllegalStateException("Skript is already enabled.");
		}
		instance = this;
		this.skript = new Skript(this);
		Bukkit.getPluginManager().registerEvents(this, this);
		if (disabled) {
			Skript.error(Skript.message_invalid_reload.toString());
			setEnabled(false);
			return;
		}

		Skript.version = new Version(getDescription().getVersion()); // Skript version

		// Start the updater
		// Note: if config prohibits update checks, it will NOT do network connections
		try {
			this.updater = new SkriptUpdater();
		} catch (Exception e) {
			Skript.exception(e, "Update checker could not be initialized.");
		}
		Skript.experimentRegistry = new ExperimentRegistry(this.skript);
		Feature.registerAll(getAddonInstance(), Skript.experimentRegistry);

		if (!getDataFolder().isDirectory())
			getDataFolder().mkdirs();

		this.skript.scriptsFolder = new File(getDataFolder(), Skript.SCRIPTSFOLDER);
		File config = new File(getDataFolder(), "config.sk");
		File lang = new File(getDataFolder(), "lang");
		if (!this.skript.scriptsFolder.isDirectory() || !config.exists() || !lang.exists()) {
			ZipFile f = null;
			try {
				boolean populateExamples = false;
				if (!this.skript.scriptsFolder.isDirectory()) {
					if (!this.skript.scriptsFolder.mkdirs())
						throw new IOException("Could not create the directory " + this.skript.scriptsFolder);
					populateExamples = true;
				}

				boolean populateLanguageFiles = false;
				if (!lang.isDirectory()) {
					if (!lang.mkdirs())
						throw new IOException("Could not create the directory " + lang);
					populateLanguageFiles = true;
				}

				f = new ZipFile(getFile());
				for (ZipEntry e : new EnumerationIterable<ZipEntry>(f.entries())) {
					if (e.isDirectory())
						continue;
					File saveTo = null;
					if (populateExamples && e.getName().startsWith(Skript.SCRIPTSFOLDER + "/")) {
						String fileName = e.getName().substring(e.getName().indexOf("/") + 1);
						// All example scripts must be disabled for jar security.
						if (!fileName.startsWith(ScriptLoader.DISABLED_SCRIPT_PREFIX))
							fileName = ScriptLoader.DISABLED_SCRIPT_PREFIX + fileName;
						saveTo = new File(this.skript.scriptsFolder, fileName);
					} else if (populateLanguageFiles
						&& e.getName().startsWith("lang/")
						&& !e.getName().endsWith("default.lang")) {
						String fileName = e.getName().substring(e.getName().lastIndexOf("/") + 1);
						saveTo = new File(lang, fileName);
					} else if (e.getName().equals("config.sk")) {
						if (!config.exists())
							saveTo = config;
					}
					if (saveTo != null) {
						try (InputStream in = f.getInputStream(e)) {
							assert in != null;
							FileUtils.save(in, saveTo);
						}
					}
				}
				Skript.info("Successfully generated the config and the example scripts.");
			} catch (ZipException ignored) {
			} catch (IOException e) {
				Skript.error("Error generating the default files: " + ExceptionUtils.toString(e));
			} finally {
				if (f != null) {
					try {
						f.close();
					} catch (IOException ignored) {
					}
				}
			}
		}

		// initialize the Skript addon instance
		getAddonInstance();

		// Load classes which are always safe to use
		JavaClasses.init(); // These may be needed in configuration

		// Check server software, Minecraft version, etc.
		if (!Skript.checkServerPlatform()) {
			disabled = true; // Nothing was loaded, nothing needs to be unloaded
			setEnabled(false); // Cannot continue; user got errors in console to tell what happened
			return;
		}

		// And then not-so-safe classes
		Throwable classLoadError = null;
		try {
			SkriptClasses.init();
			BukkitClasses.init();
		} catch (Throwable e) {
			classLoadError = e;
		}

		// Config must be loaded after Java and Skript classes are parseable
		// ... but also before platform check, because there is a config option to ignore some errors
		SkriptConfig.load();

		// Now override the verbosity if test mode is enabled
		if (TestMode.VERBOSITY != null)
			SkriptLogger.setVerbosity(Verbosity.valueOf(TestMode.VERBOSITY));

		// Use the updater, now that it has been configured to (not) do stuff
		if (updater != null) {
			CommandSender console = Bukkit.getConsoleSender();
			assert updater != null;
			updater.updateCheck(console);
		}

		// If loading can continue (platform ok), check for potentially thrown error
		if (classLoadError != null) {
			Skript.exception(classLoadError);
			setEnabled(false);
			return;
		}

		PluginCommand skriptCommand = getCommand("skript");
		assert skriptCommand != null; // It is defined, unless build is corrupted or something like that
		skriptCommand.setExecutor(new SkriptCommand());
		skriptCommand.setTabCompleter(new SkriptCommandTabCompleter());

		// Load Bukkit stuff. It is done after platform check, because something might be missing!
		DefaultValues.init();

		ChatMessages.registerListeners();

		try {
			getAddonInstance().loadClasses("ch.njol.skript",
				"conditions", "effects", "events", "expressions", "entity", "sections", "structures");
		} catch (final Exception e) {
			Skript.exception(e, "Could not load required .class files: " + e.getLocalizedMessage());
			setEnabled(false);
			return;
		}

		Commands.registerListeners();

		if (Skript.logNormal())
			Skript.info(" " + Language.get("skript.copyright"));

		final long tick = Skript.testing() ? Bukkit.getWorlds().getFirst().getFullTime() : 0;
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@SuppressWarnings({"synthetic-access", "unchecked"})
			@Override
			public void run() {
				assert Bukkit.getWorlds().getFirst().getFullTime() == tick;

				// Load hooks from Skript jar
				try {
					try (JarFile jar = new JarFile(getFile())) {
						for (JarEntry e : new EnumerationIterable<>(jar.entries())) {
							if (e.getName().startsWith("ch/njol/skript/hooks/") && e.getName().endsWith("Hook.class") && StringUtils.count("" + e.getName(), '/') <= 5) {
								final String c = e.getName().replace('/', '.').substring(0, e.getName().length() - ".class".length());
								try {
									Class<?> hook = Class.forName(c, true, getClassLoader());
									if (Hook.class.isAssignableFrom(hook) && !Modifier.isAbstract(hook.getModifiers()) && Skript.isHookEnabled((Class<? extends Hook<?>>) hook)) {
										hook.getDeclaredConstructor().setAccessible(true);
										hook.getDeclaredConstructor().newInstance();
									}
								} catch (ClassNotFoundException ex) {
									Skript.exception(ex, "Cannot load class " + c);
								} catch (ExceptionInInitializerError err) {
									Skript.exception(err.getCause(), "Class " + c + " generated an exception while loading");
								} catch (Exception ex) {
									Skript.exception(ex, "Exception initializing hook: " + c);
								}
							}
						}
					}
				} catch (IOException e) {
					Skript.error("Error while loading plugin hooks" + (e.getLocalizedMessage() == null ? "" : ": " + e.getLocalizedMessage()));
					Skript.exception(e);
				}
				Skript.finishedLoadingHooks = true;

				if (TestMode.ENABLED) {
					Skript.info("Preparing Skript for testing...");
					Skript.tainted = true;
					try {
						getAddonInstance().loadClasses("ch.njol.skript.test.runner");
						if (TestMode.JUNIT)
							getAddonInstance().loadClasses("org.skriptlang.skript.test.junit.registration");
					} catch (IOException e) {
						Skript.exception("Failed to load testing environment.");
						Bukkit.getServer().shutdown();
					}
				}

				Skript.stopAcceptingRegistrations();


				Documentation.generate(); // TODO move to test classes?

				// Variable loading
				if (Skript.logNormal())
					Skript.info("Loading variables...");
				long vls = System.currentTimeMillis();

				LogHandler h = SkriptLogger.startLogHandler(new ErrorDescLogHandler() {
					@Override
					public LogResult log(final LogEntry entry) {
						super.log(entry);
						if (entry.level.intValue() >= Level.SEVERE.intValue()) {
							Skript.logEx(entry.message); // no [Skript] prefix
							return LogResult.DO_NOT_LOG;
						} else {
							return LogResult.LOG;
						}
					}

					@Override
					protected void beforeErrors() {
						Skript.logEx();
						Skript.logEx("===!!!=== Skript variable load error ===!!!===");
						Skript.logEx("Unable to load (all) variables:");
					}

					@Override
					protected void afterErrors() {
						Skript.logEx();
						Skript.logEx("Skript will work properly, but old variables might not be available at all and new ones may or may not be saved until Skript is able to create a backup of the old file and/or is able to connect to the database (which requires a restart of Skript)!");
						Skript.logEx();
					}
				});

				try (CountingLogHandler c = new CountingLogHandler(SkriptLogger.SEVERE).start()) {
					if (!Variables.load())
						if (c.getCount() == 0)
							Skript.error("(no information available)");
				} finally {
					h.stop();
				}

				long vld = System.currentTimeMillis() - vls;
				if (Skript.logNormal())
					Skript.info("Loaded " + Variables.numVariables() + " variables in " + ((vld / 100) / 10.) + " seconds");

				// Skript initialization done
				Skript.debug("Early init done");

				if (TestMode.ENABLED) {
					Bukkit.getScheduler().runTaskLater(SkriptPlugin.this, () -> Skript.info("Skript testing environment enabled, starting soon..."), 1);
					// Ignore late init (scripts, etc.) in test mode
					Bukkit.getScheduler().runTaskLater(SkriptPlugin.this, () -> {
						// Delay is in Minecraft ticks.
						long shutdownDelay = 0;
						if (TestMode.GEN_DOCS) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skript gen-docs");
						} else if (TestMode.DEV_MODE) { // Developer controlled environment.
							Skript.info("Test development mode enabled. Test scripts are at " + TestMode.TEST_DIR);
							return;
						} else {
							Skript.info("Loading all tests from " + TestMode.TEST_DIR);

							// Treat parse errors as fatal testing failure
							CountingLogHandler errorCounter = new CountingLogHandler(Level.SEVERE);
							try {
								errorCounter.start();
								File testDir = TestMode.TEST_DIR.toFile();
								ScriptLoader.loadScripts(testDir, errorCounter);
							} finally {
								errorCounter.stop();
							}

							Bukkit.getPluginManager().callEvent(new SkriptTestEvent());
							if (errorCounter.getCount() > 0) {
								TestTracker.testStarted("parse scripts");
								TestTracker.testFailed(errorCounter.getCount() + " error(s) found");
							}
							if (Skript.errored) { // Check for exceptions thrown while script was executing
								TestTracker.testStarted("run scripts");
								TestTracker.testFailed("exception was thrown during execution");
							}
							if (TestMode.JUNIT) {
								Skript.info("Running all JUnit tests...");
								long milliseconds = 0, tests = 0, fails = 0, ignored = 0, size = 0;
								try {
									List<Class<?>> classes = Lists.newArrayList(Utils.getClasses(SkriptPlugin.getInstance(), "org.skriptlang.skript.test", "tests"));
									// Don't attempt to run inner/anonymous classes as tests
									classes.removeIf(Class::isAnonymousClass);
									classes.removeIf(Class::isLocalClass);
									// Test that requires package access. This is only present when compiling with src/test.
									classes.add(Class.forName("ch.njol.skript.variables.FlatFileStorageTest"));
									size = classes.size();
									for (Class<?> clazz : classes) {
										// Reset class SkriptJUnitTest which stores test requirements.
										String test = clazz.getName();
										SkriptJUnitTest.setCurrentJUnitTest(test);
										SkriptJUnitTest.setShutdownDelay(0);

										Skript.info("Running JUnit test '" + test + "'");
										Result junit = JUnitCore.runClasses(clazz);
										TestTracker.testStarted("JUnit: '" + test + "'");

										//Usage of @After is pointless if the JUnit class requires delay. As the @After will happen instantly.
										//The JUnit must override the 'cleanup' method to avoid Skript automatically cleaning up the test data.
										boolean overrides = false;
										for (Method method : clazz.getDeclaredMethods()) {
											if (!method.isAnnotationPresent(After.class))
												continue;
											if (SkriptJUnitTest.getShutdownDelay() > 1)
												Skript.warning("Using @After in JUnit classes, happens instantaneously, and JUnit class '" + test + "' requires a delay. Do your test cleanup in the script junit file or 'cleanup' method.");
											if (method.getName().equals("cleanup"))
												overrides = true;
										}
										if (SkriptJUnitTest.getShutdownDelay() > 1 && !overrides)
											Skript.error("The JUnit class '" + test + "' does not override the method 'cleanup' thus the test data will instantly be cleaned up. " +
												"This JUnit test requires longer shutdown time: " + SkriptJUnitTest.getShutdownDelay());

										// Collect all data from the current JUnit test.
										shutdownDelay = Math.max(shutdownDelay, SkriptJUnitTest.getShutdownDelay());
										tests += junit.getRunCount();
										milliseconds += junit.getRunTime();
										ignored += junit.getIgnoreCount();
										fails += junit.getFailureCount();

										// If JUnit failures are present, add them to the TestTracker.
										junit.getFailures().forEach(failure -> {
											String message = failure.getMessage() == null ? "" : " " + failure.getMessage();
											TestTracker.JUnitTestFailed(test, message);
											Skript.exception(failure.getException(), "JUnit test '" + failure.getTestHeader() + " failed.");
										});
										if (SkriptJUnitTest.class.isAssignableFrom(clazz))
											((SkriptJUnitTest) clazz.getConstructor().newInstance()).cleanup();
										SkriptJUnitTest.clearJUnitTest();
									}
								} catch (IOException e) {
									Skript.exception(e, "Failed to execute JUnit runtime tests.");
								} catch (ClassNotFoundException e) {
									// Should be the Skript test jar gradle task.
									assert false : "Class 'ch.njol.skript.variables.FlatFileStorageTest' was not found.";
								} catch (InstantiationException |
										 IllegalAccessException |
										 IllegalArgumentException |
										 InvocationTargetException |
										 NoSuchMethodException |
										 SecurityException e) {
									Skript.exception(e, "Failed to initalize test JUnit classes.");
								}
								if (ignored > 0)
									Skript.warning("There were " + ignored + " ignored test cases! This can mean they are not properly setup in order in that class!");

								Skript.info("Completed " + tests + " JUnit tests in " + size + " classes with " + fails + " failures in " + milliseconds + " milliseconds.");
							}
						}
						double display = (double) shutdownDelay / 20;
						Skript.info("Testing done, shutting down the server in " + display + " second" + (display <= 1D ? "" : "s") + "...");
						// Delay server shutdown to stop the server from crashing because the current tick takes a long time due to all the tests
						Bukkit.getScheduler().runTaskLater(SkriptPlugin.this, () -> {
							if (TestMode.JUNIT && !EffObjectives.isJUnitComplete())
								EffObjectives.fail();

							Skript.info("Collecting results to " + TestMode.RESULTS_FILE);
							String results = new Gson().toJson(TestTracker.collectResults());
							try {
								Files.write(TestMode.RESULTS_FILE, results.getBytes(StandardCharsets.UTF_8));
							} catch (IOException e) {
								Skript.exception(e, "Failed to write test results.");
							}

							Bukkit.getServer().shutdown();
						}, shutdownDelay);
					}, 5);
				}

				SkriptPlugin.this.metrics = new Metrics(SkriptPlugin.getInstance(), 722); // 722 is our bStats plugin ID
				SkriptMetrics.setupMetrics(SkriptPlugin.this.metrics);

				/*
				 * Start loading scripts
				 */
				Date start = new Date();
				CountingLogHandler logHandler = new CountingLogHandler(Level.SEVERE);

				File scriptsFolder = skript.getScriptsFolder();
				ScriptLoader.updateDisabledScripts(scriptsFolder.toPath());
				ScriptLoader.loadScripts(scriptsFolder, logHandler)
					.thenAccept(scriptInfo -> {
						try {
							if (logHandler.getCount() == 0)
								Skript.info(Skript.message_no_errors.toString());
							if (scriptInfo.files == 0)
								Skript.warning(Skript.message_no_scripts.toString());
							if (Skript.logNormal() && scriptInfo.files > 0)
								Skript.info(Skript.message_scripts_loaded.toString(
									scriptInfo.files,
									scriptInfo.structures,
									start.difference(new Date())
								));

							Skript.info(Skript.message_finished_loading.toString());

							// EvtSkript.onSkriptStart should be called on main server thread
							if (!ScriptLoader.isAsync()) {
								EvtSkript.onSkriptStart();

								// Suppresses the "can't keep up" warning after loading all scripts
								// Only for non-asynchronous loading
								Filter filter = record -> {
									if (record == null)
										return false;
									return record.getMessage() == null
										|| !record.getMessage().toLowerCase(Locale.ENGLISH).startsWith("can't keep up!");
								};
								BukkitLoggerFilter.addFilter(filter);
								Bukkit.getScheduler().scheduleSyncDelayedTask(
									SkriptPlugin.this,
									() -> BukkitLoggerFilter.removeFilter(filter),
									1);
							} else {
								Bukkit.getScheduler().scheduleSyncDelayedTask(SkriptPlugin.this,
									EvtSkript::onSkriptStart);
							}
						} catch (Exception e) {
							// Something went wrong, we need to make sure the exception is printed
							throw Skript.exception(e);
						}
					});

			}
		});

		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onJoin(final PlayerJoinEvent e) {
				if (e.getPlayer().hasPermission("skript.admin")) {
					new Task(SkriptPlugin.this, 0) {
						@Override
						public void run() {
							Player p = e.getPlayer();
							SkriptUpdater updater = SkriptPlugin.this.updater;
							if (updater == null)
								return;

							// Don't actually check for updates to avoid breaking Github rate limit
							if (updater.getReleaseStatus() == ReleaseStatus.OUTDATED) {
								// Last check indicated that an update is available
								UpdateManifest update = updater.getUpdateManifest();
								assert update != null; // Because we just checked that one is available
								Skript.info(p, "" + SkriptUpdater.m_update_available.toString(update.id, Skript.getVersion()));
								p.spigot().sendMessage(BungeeConverter.convert(ChatMessages.parseToArray(
									"Download it at: <aqua><u><link:" + update.downloadUrl + ">" + update.downloadUrl)));
							}
						}
					};
				}
			}
		}, this);
	}

	@SuppressWarnings("removal")
	@Override
	public void onDisable() {
		if (disabled)
			return;
		disabled = true;
		Skript.experimentRegistry = null;

		if (!Skript.partDisabled) {
			beforeDisable();
		}

		Bukkit.getScheduler().cancelTasks(this);

		for (Closeable c : Skript.closeOnDisable) {
			try {
				c.close();
			} catch (final Exception e) {
				Skript.exception(e, "An error occurred while shutting down.", "This might or might not cause any issues.");
			}
		}
	}

	/**
	 * Registers a Closeable that should be closed when this plugin is disabled.
	 * <p>
	 * All registered Closeables will be closed after all scripts have been stopped.
	 *
	 * @param closeable Closeable to register
	 */
	public static void closeOnDisable(final Closeable closeable) {
		Skript.closeOnDisable.add(closeable);
	}

	private void beforeDisable() {
		Skript.partDisabled = true;
		EvtSkript.onSkriptStop(); // TODO [code style] warn user about delays in Skript stop events

		ScriptLoader.unloadScripts(ScriptLoader.getLoadedScripts());
	}

	/**
	 * @return A {@link SkriptAddon} representing Skript.
	 */
	public SkriptAddon getAddonInstance() {
		if (this.addon == null) {
			this.addon = new SkriptAddon(this);
			this.addon.setLanguageFileDirectory("lang");
		}
		return this.addon;
	}

	// ===== LISTENER =====

	@SuppressWarnings({"unused", "deprecation"})
	@EventHandler
	private void onPluginDisable(PluginDisableEvent event) {
		Plugin plugin = event.getPlugin();
		PluginDescriptionFile descriptionFile = plugin.getDescription();
		if (descriptionFile.getDepend().contains("Skript") || descriptionFile.getSoftDepend().contains("Skript")) {
			// An addon being disabled, check if server is being stopped
			if (Bukkit.getServer().isStopping()) {
				beforeDisable();
			}
		}
	}

}
