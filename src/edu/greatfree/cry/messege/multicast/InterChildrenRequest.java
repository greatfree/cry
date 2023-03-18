package edu.greatfree.cry.messege.multicast;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.util.IPAddress;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
public class InterChildrenRequest extends ClusterRequest
{
	private static final long serialVersionUID = 1269551605242323406L;

//	private String subRootIP;
//	private int subRootPort;
	private IPAddress subRootIP;
	
	private IntercastRequest ir;

	public InterChildrenRequest(IntercastRequest ir)
	{
		super(MulticastMessageType.INTER_CHILDREN_REQUEST, ir.getApplicationID());
		this.ir = ir;
	}

	/*
	public void setSubRootIP(String ip)
	{
		this.subRootIP = ip;
	}
	
	public String getSubRootIP()
	{
		return this.subRootIP;
	}
	
	public void setSubRootPort(int port)
	{
		this.subRootPort = port;
	}
	
	public int getSubRootPort()
	{
		return this.subRootPort;
	}
	*/
	
	public void setSubRootIP(IPAddress ip)
	{
		this.subRootIP = ip;
	}
	
	public IPAddress getSubRootIP()
	{
		return this.subRootIP;
	}
	
	public IntercastRequest getIntercastRequest()
	{
		return this.ir;
	}
}
