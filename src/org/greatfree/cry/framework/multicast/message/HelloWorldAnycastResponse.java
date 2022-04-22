package org.greatfree.cry.framework.multicast.message;

import org.greatfree.message.multicast.MulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class HelloWorldAnycastResponse extends MulticastResponse
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
