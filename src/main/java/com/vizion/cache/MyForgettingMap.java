package com.vizion.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * This implementation uses a HashMap combined with doubly linked list, to save and look up associations.
 *  
 * The use of a HashMap ensures there is a constant time of O(1) for lookups. While a doubly linked list implementation 
 * has been used to allow us to update the last used association, as well as maintaing order. 
 * This implementation provides good performance, with O(1) time. ie fast look up and fast removals. 
 * 
 * The implementation stores an initial capacity, that determines when a map should have the associations removed 
 * to allow new associations to be added.
 * 
 * The two synchronized methods ensure that the class remains thread safe, and maintains a consistent state.
 * 
 * @author Asif
 *
 * @param <K>
 * @param <V>
 */

public class MyForgettingMap<K, V> implements ForgettingMap<K, V> {
	
	private final Node<K, V> head = new Node<K, V>();
	private final Node<K, V> tail = new Node<K, V>();	
	private final Map <K, Node<K,V>> map;
	private final int capacity;	
	private final Object MUTEX = new Object();
	
	public MyForgettingMap(int capacity) throws IllegalArgumentException {
		if(capacity <= 0) {
			throw new IllegalArgumentException("Initial capacity for map must be greater than 0");
		}
		this.capacity = capacity;
		this.map = new HashMap<K, Node<K, V>>();
		
		this.head.setNext(this.tail);
		this.tail.setPrev(this.head);
	}
	
	public void add(K key, V value) throws IllegalArgumentException {
		if(value == null || key == null) {
			throw new IllegalArgumentException("Value or Key cannot be null");
		}
		
		synchronized (MUTEX) {
			Node<K,V> node = this.map.get(key);
			if(node!=null) {
				// already exists
				updateUsage(node);
			} else {			
				if(this.map.size() == this.capacity) {
					this.map.remove(tail.getPrev().getKey());
					remove(tail.getPrev());
				}
				Node<K,V> newAssociation = new Node<K, V>();
				newAssociation.setKey(key);
				newAssociation.setValue(value);	
				add(newAssociation);
				this.map.putIfAbsent(key, newAssociation);
			}
		}

	}
	
	private void updateUsage(Node<K, V> node) {	
		// move node to the front ie becomes the last used.
		remove(node);
		add(node);
	}
	
	private void add(Node<K, V> node) {
		// previous first node moves along
		node.setNext(this.head.getNext());
		node.getNext().setPrev(node); 				
		
		this.head.setNext(node);
		node.setPrev(this.head);
	}

	public V find(K key) {
		synchronized (MUTEX) {
			V value = null;
			Node<K,V> node = this.map.get(key);
			if(node != null) {
				updateUsage(node);
				value = node.getValue();
			}
			return value;
		}
	}	

	private void remove(Node<K,V> node) {
		Node<K,V> nextNode = node.getNext();
		Node<K,V> previousNode =  node.getPrev();
		
		nextNode.setPrev(previousNode);		
		previousNode.setNext(nextNode);
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public int getSize() {
		synchronized (MUTEX) {
			return this.map.size();
		}
	}

}
