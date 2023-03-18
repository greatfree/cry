package edu.greatfree.cry.messege.multicast;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.util.IPAddress;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
public class RootAddressNotification extends PrimitiveMulticastNotification
{
	private static final long serialVersionUID = 2633140420376921598L;

	private IPAddress rootAddress;

	public RootAddressNotification(IPAddress rootAddress)
	{
		super(MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION);
		this.rootAddress = rootAddress;
	}

	public IPAddress getRootAddress()
	{
		return this.rootAddress;
	}
}
