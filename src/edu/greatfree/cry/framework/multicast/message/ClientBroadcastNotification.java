package edu.greatfree.cry.framework.multicast.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class ClientBroadcastNotification extends Notification
{
	private static final long serialVersionUID = -7971836253396385676L;
	
	private HelloWorld hw;
	private int cryptoOption;
	
	public ClientBroadcastNotification(HelloWorld hw, int cryptoOption)
	{
		super(MultiAppID.CLIENT_BROADCAST_NOTIFICATION);
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
