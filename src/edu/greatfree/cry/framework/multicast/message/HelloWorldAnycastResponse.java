package edu.greatfree.cry.framework.multicast.message;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
// public class HelloWorldAnycastResponse extends MulticastResponse
public class HelloWorldAnycastResponse extends PrimitiveMulticastResponse
{
	private static final long serialVersionUID = -5842256911989895925L;

	private HelloWorld hw;

	public HelloWorldAnycastResponse(HelloWorld hw, String collaboratorKey)
	{
		super(MultiAppID.HELLO_WORLD_ANYCAST_RESPONSE, collaboratorKey);
		this.hw = hw;
	}

	public HelloWorld getHello()
	{
		return this.hw;
	}
}
