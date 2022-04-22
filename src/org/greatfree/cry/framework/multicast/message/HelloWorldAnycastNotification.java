package org.greatfree.cry.framework.multicast.message;

import org.greatfree.message.multicast.MulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class HelloWorldAnycastNotification extends MulticastNotification
{
	private static final long serialVersionUID = -4161579838621944312L;

	private HelloWorld hello;

	public HelloWorldAnycastNotification(HelloWorld hello)
	{
		super(MultiAppID.HELLO_WORLD_ANYCAST_NOTIFICATION);
		this.hello = hello;
	}

	public HelloWorld getHello()
	{
		return this.hello;
	}
}
