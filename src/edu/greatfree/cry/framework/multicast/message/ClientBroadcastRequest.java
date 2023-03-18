package edu.greatfree.cry.framework.multicast.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class ClientBroadcastRequest extends Request
{
	private static final long serialVersionUID = 6969591300394857096L;

	private HelloWorld hw;
	private int cryptoOption;

	public ClientBroadcastRequest(HelloWorld hw, int cryptoOption)
	{
		super(MultiAppID.CLIENT_BROADCAST_REQUEST);
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
