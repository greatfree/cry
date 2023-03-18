package edu.greatfree.cry.framework.multicast.message;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
// public class HelloWorldUnicastResponse extends MulticastResponse
public class HelloWorldUnicastResponse extends PrimitiveMulticastResponse
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
