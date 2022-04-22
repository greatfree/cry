package org.greatfree.cry.framework.multicast.message;

import org.greatfree.message.multicast.MulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class HelloWorldBroadcastResponse extends MulticastResponse
{
	private static final long serialVersionUID = -2503238108388279605L;

	private HelloWorld hw;

	public HelloWorldBroadcastResponse(HelloWorld hw, String collaboratorKey)
	{
		super(MultiAppID.HELLO_WORLD_BROADCAST_RESPONSE, collaboratorKey);
		this.hw = hw;
	}

	public HelloWorld getHello()
	{
		return this.hw;
	}
}
