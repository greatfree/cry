package org.greatfree.cry.framework.multicast.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class ClientUnicastNotification extends Notification
{
	private static final long serialVersionUID = -7317880010693969435L;

	private HelloWorld hw;
	private int cryptoOption;

	public ClientUnicastNotification(HelloWorld hw, int cryptoOption)
	{
		super(MultiAppID.CLIENT_UNICAST_NOTIFICATION);
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
