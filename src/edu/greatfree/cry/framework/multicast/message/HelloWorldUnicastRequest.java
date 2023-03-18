package edu.greatfree.cry.framework.multicast.message;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
// public class HelloWorldUnicastRequest extends MulticastRequest
public class HelloWorldUnicastRequest extends PrimitiveMulticastRequest
{
	private static final long serialVersionUID = 6757869474866283680L;

	private HelloWorld hw;
	private int cryptoOption;

	public HelloWorldUnicastRequest(HelloWorld hw, int cryptoOption)
	{
		super(MultiAppID.HELLO_WORLD_UNICAST_REQUEST);
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
