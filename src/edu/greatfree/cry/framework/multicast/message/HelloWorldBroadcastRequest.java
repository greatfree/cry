package edu.greatfree.cry.framework.multicast.message;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
// public class HelloWorldBroadcastRequest extends MulticastRequest
public class HelloWorldBroadcastRequest extends PrimitiveMulticastRequest
{
	private static final long serialVersionUID = -784338323906684103L;

	private HelloWorld hw;
	private int cryptoOption;

	public HelloWorldBroadcastRequest(HelloWorld hw, int cryptoOption)
	{
		super(MultiAppID.HELLO_WORLD_BROADCAST_REQUEST);
		this.hw = hw;
		this.cryptoOption = cryptoOption;
	}

	public HelloWorld getHello()
	{
		return this.hw;
	}
	
	public int getCryptoOption()
	{
		return this.cryptoOption;
	}
}
