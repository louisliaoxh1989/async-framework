package com.lefu.async.test;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lefu.async.Flow;
import com.lefu.async.QueueContainer;
import com.lefu.async.core.SimpleQueueContainer;
import com.lefu.async.disruptor.DefaultDisruptorQueue;

import junit.framework.TestCase;

public class QueueTest extends TestCase {
	private QueueContainer queueContainer;
	
	@Before
	public void setUp() {
		queueContainer = new SimpleQueueContainer();
		DefaultDisruptorQueue queue = new DefaultDisruptorQueue(512);
		queue.setName(TipEventListener.QUEUE_NAME);
		queue.setLogUseTime(false);
		queue.addEventListener(new TipEventListener(), 5);
		DefaultDisruptorQueue queue2 = new DefaultDisruptorQueue(512);
		queue2.setName(HelloEventListener.QUEUE_NAME);
		queue2.addEventListener(new HelloEventListener(), 3);
		queueContainer.putQueue(queue);
		queueContainer.putQueue(queue2);
		queueContainer.start();
	}
	
	@Test
	public void testQueue() throws InterruptedException {
		for (int i = 0; i < 5; i++) {
			Flow flow = queueContainer.startFlow();
			TipEventData data = new TipEventData(flow);
			data.setMessage(UUID.randomUUID().toString());
			while (!queueContainer.hasAvailableCapacity(TipEventListener.QUEUE_NAME)) {
				System.out.println(Thread.currentThread().getName() + " - " + "Sleep 10 ms");
				Thread.sleep(10);
			}
			queueContainer.publish(TipEventListener.QUEUE_NAME, data);
		}
	}
	
	@After
	public void tearDown() {
		queueContainer.shutdown();
	}
	
}
