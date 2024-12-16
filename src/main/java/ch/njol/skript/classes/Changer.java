package ch.njol.skript.classes;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.classes.data.defaults.DefaultChangers;
import ch.njol.skript.lang.Expression;

/**
 * An interface to declare changeable values. All Expressions implement something similar like this by default, but refuse any change if {@link Expression#acceptChange(ChangeMode)}
 * isn't overridden.
 * <p>
 * Some useful Changers can be found in {@link DefaultChangers}
 * 
 * @author Peter Güttinger
 * @see DefaultChangers
 * @see Expression
 */
public interface Changer<T> {
	
	public static enum ChangeMode {
		ADD, SET, REMOVE, REMOVE_ALL, DELETE, RESET;
	}
	
	/**
	 * Tests whether this changer supports the given mode, and if yes what type(s) it expects the elements of <code>delta</code> to be.
	 * <p>
	 * Unlike {@link Expression#acceptChange(ChangeMode)} this method must not print errors.
	 * 
	 * @param mode
	 * @return An array of types that {@link #change(Object[], Object[], ChangeMode)} accepts as its <code>delta</code> parameter (which can be arrays to denote that multiple of
	 *         that type are accepted), or null if the given mode is not supported. For {@link ChangeMode#DELETE} and {@link ChangeMode#RESET} this can return any non-null array to
	 *         mark them as supported.
	 */
	@Nullable
	public abstract Class<?>[] acceptChange(ChangeMode mode);
	
	/**
	 * @param what The objects to change
	 * @param delta An array with one or more instances of one or more of the the classes returned by {@link #acceptChange(ChangeMode)} for the given change mode (null for
	 *            {@link ChangeMode#DELETE} and {@link ChangeMode#RESET}). <b>This can be a Object[], thus casting is not allowed.</b>
	 * @param mode
	 * @throws UnsupportedOperationException (optional) if this method was called on an unsupported ChangeMode.
	 */
	public abstract void change(T[] what, @Nullable Object[] delta, ChangeMode mode);
	
	public static abstract class ChangerUtils {
		
		@SuppressWarnings("unchecked")
		public static <T, V> void change(final Changer<T> changer, final Object[] what, final @Nullable Object[] delta, final ChangeMode mode) {
			changer.change((T[]) what, delta, mode);
		}
		
		/**
		 * Tests whether an expression accepts changes of a certain type. If multiple types are given it test for whether any of the types is accepted.
		 * 
		 * @param e The expression to test
		 * @param mode The ChangeMode to use in the test
		 * @param types The types to test for
		 * @return Whether <tt>e.{@link Expression#change(Event, Object[], ChangeMode) change}(event, type[], mode)</tt> can be used or not.
		 */
		public static boolean acceptsChange(final Expression<?> e, final ChangeMode mode, final Class<?>... types) {
			final Class<?>[] cs = e.acceptChange(mode);
			if (cs == null)
				return false;
			for (final Class<?> type : types) {
				for (final Class<?> c : cs) {
					if (c.isArray() ? c.getComponentType().isAssignableFrom(type) : c.isAssignableFrom(type))
						return true;
				}
			}
			return false;
		}
		
	}
	
}
