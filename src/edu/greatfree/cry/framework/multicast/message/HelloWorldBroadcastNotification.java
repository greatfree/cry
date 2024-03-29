package edu.greatfree.cry.framework.multicast.message;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
// public class HelloWorldBroadcastNotification extends MulticastNotification
public class HelloWorldBroadcastNotification extends PrimitiveMulticastNotification
{
	private static final long serialVersionUID = -1214238832367002342L;

	private HelloWorld hello;
	private int cryptoOption;

	public HelloWorldBroadcastNotification(HelloWorld hello, int cryptoOption)
	{
		super(MultiAppID.HELLO_WORLD_BROADCAST_NOTIFICATION);
		this.hello = hello;
		this.cryptoOption = cryptoOption;
	}

	public HelloWorld getHello()
	{
		return this.hello;
	}
	
	public int getCryptoOption()
	{
		return this.cryptoOption;
	}
}
