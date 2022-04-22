package org.greatfree.cry.framework.multicast.message;

import org.greatfree.message.multicast.MulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class HelloWorldUnicastResponse extends MulticastResponse
{
	private static final long serialVersionUID = 2903177691383490980L;

	private HelloWorld hw;

	public HelloWorldUnicastResponse(HelloWorld hw, String collaboratorKey)
	{
		super(MultiAppID.HELLO_WORLD_UNICAST_RESPONSE, collaboratorKey);
		this.hw = hw;
	}

	public HelloWorld getHello()
	{
		return this.hw;
	}
}
