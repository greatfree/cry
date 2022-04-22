package org.greatfree.cry.framework.multicast.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class ClientAnycastNotification extends Notification
{
	private static final long serialVersionUID = -2398527215277284426L;

	private HelloWorld hw;
	private int cryptoOption;

	public ClientAnycastNotification(HelloWorld hw, int cryptoOption)
	{
		super(MultiAppID.CLIENT_ANYCAST_NOTIFICATION);
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
