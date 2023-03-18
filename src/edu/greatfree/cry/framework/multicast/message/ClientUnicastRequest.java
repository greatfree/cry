package edu.greatfree.cry.framework.multicast.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class ClientUnicastRequest extends Request
{
	private static final long serialVersionUID = 4385140816994384318L;

	private HelloWorld hw;
	private int cryptoOption;

	public ClientUnicastRequest(HelloWorld hw, int cryptoOption)
	{
		super(MultiAppID.CLIENT_UNICAST_REQUEST);
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
