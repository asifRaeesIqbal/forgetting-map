package com.vizion.cache;


/**
 * 
 * A basic implementation of a forgetting map.  The map stores associations based on a key - K, and a value - V.
 * 
 * When the map reaches its capacity, any further associations being added result in removal of the last used association. 
 * This ensures that the forgetting most can have a max size of the initial capacity set.
 * 
 * @author Asif
 *
 * @param <K> - unique key for map
 * @param <V> - value for map
 */
public interface ForgettingMap<K, V> {

	/**
	 * 
	 * A new association is added to the ForgettingMap, with a key - K and a value - V. This method is thread safe.
	 * And addition of the duplicate associations with the same key will result in the value being overwritten.
	 * And the association being moved up the order of last accessed.
	 * 
	 * @param key    - Key value that is unique for the association.
	 * @param value  - Value for this association.
	 * 
	 * @throws IllegalArgumentException - if supplied key or value is null
	 */
	void add(K key, V value) throws IllegalArgumentException;

	/**
	 * 
	 * Find a value in the cache with a key - K. Return null if no value is found. This method is thread safe.
	 * 
	 * @param key   - Key that identifies the association.
	 * @return      - A value or null
	 */
	V find(K key);
	
	/**
	 * 
	 * Returns the capacity for the map, that was set on creation. 
	 * 
	 * @return
	 */
	int getCapacity();
	
	/**
	 * 
	 * Returns the the current size of the map. Thread safe method.
	 * 
	 * @return
	 */
	int getSize();

}