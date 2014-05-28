package phat.util;
/**
 * A generic to represent lazy evaluation. It assumes that getLazy will return null until evaluated
 * @author escalope
 *
 * @param <T>
 */
public abstract class Lazy<T> {		
	/**
	 * It will return null until the lazy evaluation succeeds. Then a T class instance will be returned.
	 * 
	 * @return null until the lazy evaluation succeeds. Then a T class instance will be returned.
	 */
	public abstract T getLazy();
}
