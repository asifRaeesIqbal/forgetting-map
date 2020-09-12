package com.vizion.cache;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class MyForgettingMapTest {
	
	@Test
	public void test_create_forgetting_map() {
		ForgettingMap<Integer, String> map = new MyForgettingMap<Integer, String>(5);
		
		Assert.assertNotNull(map);
		Assert.assertEquals(5, map.getCapacity());
	}
	
	@Test
	public void test_exception_if_capacity_less_than_1_on_creation() {
		 Assertions.assertThrows(IllegalArgumentException.class, () -> {
			 new MyForgettingMap<Integer, String>(0);	
			  });
	}
	
	@Test
	public void test_exception_if_null_is_added_as_key() {
		ForgettingMap<Integer, String> map = new MyForgettingMap<Integer, String>(5);
		
		 Assertions.assertThrows(IllegalArgumentException.class, () -> {
			 map.add(null, "value");	
			  });
	}	
	
	@Test
	public void test_exception_if_null_is_added_as_value() {
		ForgettingMap<Integer, String> map = new MyForgettingMap<Integer, String>(5);
		
		 Assertions.assertThrows(IllegalArgumentException.class, () -> {
			 map.add(1, null);	
			  });
	}	
	
	@Test
	public void test_add_association_to_forgetting_map() {
		final String value = "my map"; 
		ForgettingMap<Integer, String> map = new MyForgettingMap<Integer, String>(5);
		
		map.add(1, value);
		
		Assert.assertNotNull(map);
		Assert.assertEquals(value, map.find(1));
		Assert.assertEquals(1, map.getSize());
	}
	
	@Test
	public void test_add_association_mutliple_times_with_same_key_to_forgetting_map() {
		final String value = "my map"; 
		ForgettingMap<Integer, String> map = new MyForgettingMap<Integer, String>(5);
		
		map.add(1, value);
		map.add(1, value);
		
		Assert.assertNotNull(map);
		Assert.assertEquals(value, map.find(1));
		Assert.assertEquals(1, map.getSize());
	}
	
	@Test
	public void test_return_null_for_missing_association_in_forgetting_map() {
		ForgettingMap<Integer, String> map = new MyForgettingMap<Integer, String>(2);
		
		Assert.assertNotNull(map);
		Assert.assertNull(map.find(1));
	}
	
	
	@Test
	public void test_adding_maximum_associations_to_forgetting_map() {
		final String value = "value";
		
		ForgettingMap<Integer, String> map = new MyForgettingMap<Integer, String>(5);
		
		for(int i=1; i<=5; i++) {
			map.add(i, value+i);	
		}
		
		Assert.assertEquals(5, map.getSize());

		for(int i=1; i<=5; i++) {
			Assert.assertEquals(value+i, map.find(i));
		}

	}
	
	@Test
	public void test_removing_least_used_association_from_forgetting_map() {
		final String leastUsed = "value1";
		final String value = "value";
		
		ForgettingMap<Integer, String> map = new MyForgettingMap<Integer, String>(5);
		
		map.add(1, leastUsed);
		Assert.assertEquals(leastUsed, map.find(1));
		
		for(int i=2; i<=6; i++) {
			map.add(i, value+i);	
		}		
		
		Assert.assertEquals(5, map.getSize());
		Assert.assertNull(map.find(1));
		
		for(int i=2; i<=6; i++) {
			Assert.assertEquals(value+i, map.find(i));
		}

	}
	
	@Test
	public void test_removing_multiple_associations_from_forgetting_map() {
		final String value = "value";
		
		ForgettingMap<Integer, String> map = new MyForgettingMap<Integer, String>(5);
		
		for(int i=1; i<=10; i++) {
			map.add(i, value+i);	
		}		
			
		Assert.assertEquals(5, map.getSize());		
		
		for(int i=6; i<=10; i++) {
			Assert.assertEquals(value+i, map.find(i));
		}

	}
	
	@Test
	public void test_removing_least_recently_accessed_associations_from_forgetting_map() {
		final String value = "value";
		
		ForgettingMap<Integer, String> map = new MyForgettingMap<Integer, String>(5);
		
		for(int i=1; i<=5; i++) {
			map.add(i, value+i);	
		}
		
		Assert.assertNotNull(map);
		Assert.assertEquals(5, map.getSize());
		
		// this should make the association with key=5 move to the end, as its not been accessed
		for(int i=1; i<=4; i++) {
			map.find(i);
			Assert.assertEquals(value+i, map.find(i));
		}
		
		map.add(6, value+6); // should remove key=5	

		for(int i=1; i<=4; i++) {
			Assert.assertEquals(value+i, map.find(i));
		}
		Assert.assertEquals(value+6, map.find(6));

	}	
	
	@Test
	public void test_mutliple_threads_accessing_the_map() throws InterruptedException, BrokenBarrierException {
		final String value = "value";

		CountDownLatch startGate = new CountDownLatch(1);
		CountDownLatch endGate = new CountDownLatch(5);
		ForgettingMap<Integer, String> map = new MyForgettingMap<Integer, String>(5);

		
		for(int i=0; i < 5; i++) {
			Thread t = new Thread(()-> {
				try {					
					startGate.await();
					try {
						for(int j=1; j<=5; j++) {
							map.add(j, value+j);
							String val = map.find(j);
							assertEquals(value+j, val);
						}						
					} finally {
						Assert.assertEquals(5, map.getSize());
						endGate.countDown();
					}
				} catch (InterruptedException e) { Assert.fail();}
			});
			
			t.start();
		}
		startGate.countDown();
		endGate.await();
				
		Assert.assertNotNull(map);
		Assert.assertEquals(5, map.getSize());
		
		for(int i=1; i<=5; i++) {
			Assert.assertEquals(value+i, map.find(i));
		}
			
	}

}
