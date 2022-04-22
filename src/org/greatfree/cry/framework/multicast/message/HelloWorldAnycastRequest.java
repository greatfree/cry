package org.greatfree.cry.framework.multicast.message;

import org.greatfree.message.multicast.MulticastRequest;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class HelloWorldAnycastRequest extends MulticastRequest
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
