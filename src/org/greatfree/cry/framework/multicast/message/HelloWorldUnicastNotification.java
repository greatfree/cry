package org.greatfree.cry.framework.multicast.message;

import org.greatfree.message.multicast.MulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class HelloWorldUnicastNotification extends MulticastNotification
{
	private static final long serialVersionUID = -8078073415213224559L;

	private HelloWorld hello;

	public HelloWorldUnicastNotification(HelloWorld hello)
	{
		super(MultiAppID.HELLO_WORLD_UNICAST_NOTIFICATION);
		this.hello = hello;
	}

	public HelloWorld getHello()
	{
		return this.hello;
	}
}
