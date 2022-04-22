package org.greatfree.cry.testing;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 
 * @author Bing Li
 *
 */
class ConcurrentLinkedQueueTester
{

	public static void main(String[] args)
	{
		ConcurrentLinkedQueue<String> q = new ConcurrentLinkedQueue<String>();
		q.add("0");
		q.add("1");
		q.add("2");

		String s = q.peek();
		System.out.println(s);
		
		LinkedBlockingDeque<String> x = new LinkedBlockingDeque<String>();
		x.add("1");
	}

}
