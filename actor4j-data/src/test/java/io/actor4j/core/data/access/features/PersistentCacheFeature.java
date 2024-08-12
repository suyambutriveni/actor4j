/*
 * Copyright (c) 2015-2020, David A. Bauer. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.actor4j.core.data.access.features;

import org.junit.Test;

import io.actor4j.core.ActorSystem;
import io.actor4j.core.actors.Actor;
import io.actor4j.core.actors.ActorWithCache;
import io.actor4j.core.messages.ActorMessage;
import io.actor4j.core.utils.ActorGroup;
import io.actor4j.core.utils.ActorGroupSet;
import io.actor4j.core.utils.Pair;
import io.actor4j.core.data.access.PersistentDTO;
import io.actor4j.core.data.access.PrimaryPersistentCacheActor;
import io.actor4j.core.data.access.SecondaryPersistentCacheActor;
import io.actor4j.core.data.access.ims.IMSDataAccessActor;
import io.actor4j.core.data.access.utils.PersistentActorCacheManager;

import static io.actor4j.core.logging.ActorLogger.*;
import static org.junit.Assert.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import static io.actor4j.core.data.access.AckMode.*;

public class PersistentCacheFeature {
	@Test(timeout=5000)
	public void test_primary_secondary_persistent_cache_actor() {
		ActorSystem system = ActorSystem.create(AllFeaturesTest.factory());
		final int COUNT = 4/*system.getParallelismMin()*system.getParallelismFactor()*/;
		
		CountDownLatch testDone = new CountDownLatch(COUNT);
		
		UUID mediator = system.addActor(() -> new Actor("mediator") {
			protected final String[] keys = {"key4", "key1", "key3", "key2"};
			protected final String[] values = {"value4", "value1", "value3", "value2"};
			protected int i = 0;
			
			@Override 
			public void preStart() {
				UUID dataAccess = system.addActor(() -> new IMSDataAccessActor<String, TestObject>("dc"));
				
				ActorGroup group = new ActorGroupSet();
				AtomicInteger k = new AtomicInteger(0);
				system.addActor(() -> new PrimaryPersistentCacheActor<String, TestObject>(
						"primary", group, "cache1", (id) -> () -> new SecondaryPersistentCacheActor<String, TestObject>("secondary-"+k.getAndIncrement(), group, id, 500), COUNT-1, 500, dataAccess, NONE));

				tell(PersistentDTO.create("key1", new TestObject("key1", "value1"), "key", "test", self()), ActorWithCache.SET, "cache1");
				tell(PersistentDTO.create("key2", new TestObject("key2", "value2"), "key", "test", self()), ActorWithCache.SET, "cache1");
				tell(PersistentDTO.create("key3", new TestObject("key3", "value3"), "key", "test", self()), ActorWithCache.SET, "cache1");
				tell(PersistentDTO.create("key4", new TestObject("key4", "value4"), "key", "test", self()), ActorWithCache.SET, "cache1");
			}
			
			@Override
			public void receive(ActorMessage<?> message) {
				tell(PersistentDTO.create(keys[i], "key", "test", self()), ActorWithCache.GET, "cache1");
				
				await((msg) -> msg.tag()==ActorWithCache.GET && msg.source()!=system.SYSTEM_ID() && msg.value()!=null, (msg) -> {
					@SuppressWarnings("unchecked")
					PersistentDTO<String, TestObject> payload = ((PersistentDTO<String, TestObject>)msg.value());
					if (payload.value()!=null) {
						assertEquals(values[i], payload.entity().value());
						logger().log(DEBUG, payload.entity().value());
						if (i<keys.length-1)
							i++;
						testDone.countDown();
					}/*
					else
						logger().debug(false);*/
					unbecome();
				});
			}
		});
		
		system.start();
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				system.send(ActorMessage.create(null, 0, system.SYSTEM_ID(), mediator));
			}
		}, 0, 100);
		
		try {
			testDone.await();
			timer.cancel();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		system.shutdownWithActors(true);
	}
	
	@Test(timeout=5000)
	public void test_primary_secondary_persistent_cache_actor_with_manager_imdb() {
		ActorSystem system = ActorSystem.create(AllFeaturesTest.factory());
		final int COUNT = 4/*system.getParallelismMin()*system.getParallelismFactor()*/;
		
		CountDownLatch testDone = new CountDownLatch(COUNT);
		
		UUID mediator = system.addActor(() -> new Actor("mediator") {
			protected PersistentActorCacheManager<String, TestObject> manager;
			
			protected final String[] keys = {"key4", "key1", "key3", "key2"};
			protected final String[] values = {"value4", "value1", "value3", "value2"};
			protected int i = 0;
			
			@Override 
			public void preStart() {
				UUID dataAccess = system.addActor(() -> new IMSDataAccessActor<String, TestObject>("dc"));
				
				manager = new PersistentActorCacheManager<>(this, "cache1", "key", "test");
				system.addActor(manager.create(COUNT, 500, dataAccess, NONE));
				
				manager.set("key1", new TestObject("key1", "value1"));
				manager.set("key2", new TestObject("key2", "value2"));
				manager.set("key3", new TestObject("key3", "value3"));
				manager.set("key4", new TestObject("key4", "value4"));
			}
			
			@Override
			public void receive(ActorMessage<?> message) {
				manager.get(keys[i]);
				
				await((msg) -> msg.tag()==ActorWithCache.GET && msg.source()!=system.SYSTEM_ID() && msg.value()!=null, (msg) -> {
					Pair<String, TestObject> pair = manager.get(msg);
					
					if (pair!=null && pair.b()!=null) {
						assertEquals(keys[i], pair.a());
						assertEquals(keys[i], pair.b().key());
						assertEquals(values[i], pair.b().value());
						logger().log(DEBUG, pair.b().value());
						if (i<keys.length-1)
							i++;
						testDone.countDown();
					}/*
					else
						logger().debug(false);*/
					unbecome();
				});
			}
		});
		
		system.start();
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				system.send(ActorMessage.create(null, 0, system.SYSTEM_ID(), mediator));
			}
		}, 0, 100);
		
		try {
			testDone.await();
			timer.cancel();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		system.shutdownWithActors(true);
	}
}
