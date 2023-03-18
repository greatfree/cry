package edu.greatfree.cry.framework.multicast.message;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
// public class HelloWorldAnycastRequest extends MulticastRequest
public class HelloWorldAnycastRequest extends PrimitiveMulticastRequest
{
	private static final long serialVersionUID = -5003772506416607373L;

	private HelloWorld hw;
	private int cryptoOption;

	public HelloWorldAnycastRequest(HelloWorld hw, int cryptoOption)
	{
		super(MultiAppID.HELLO_WORLD_ANYCAST_REQUEST);
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
