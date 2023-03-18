package edu.greatfree.cry.framework.multicast.message;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
// public class HelloWorldAnycastNotification extends MulticastNotification
public class HelloWorldAnycastNotification extends PrimitiveMulticastNotification
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
