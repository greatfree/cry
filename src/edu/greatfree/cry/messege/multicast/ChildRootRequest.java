package edu.greatfree.cry.messege.multicast;

import org.greatfree.message.multicast.MulticastMessageType;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
public abstract class ChildRootRequest extends PrimitiveMulticastRequest
{
	private static final long serialVersionUID = -8525828687307881647L;

	private int applicationID;

	public ChildRootRequest(int applicationID)
	{
		super(MulticastMessageType.CHILD_ROOT_REQUEST);
		this.applicationID = applicationID;
	}

	public int getClusterAppID()
	{
		return this.applicationID;
	}
}
