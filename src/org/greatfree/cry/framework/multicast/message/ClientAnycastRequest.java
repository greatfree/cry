package org.greatfree.cry.framework.multicast.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class ClientAnycastRequest extends Request
{
	private static final long serialVersionUID = -2779665719447479550L;

	private HelloWorld hw;
	private int cryptoOption;

	public ClientAnycastRequest(HelloWorld hw, int cryptoOption)
	{
		super(MultiAppID.CLIENT_ANYCAST_REQUEST);
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
