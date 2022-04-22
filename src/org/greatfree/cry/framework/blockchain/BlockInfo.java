package org.greatfree.cry.framework.blockchain;

import java.io.Serializable;

/***
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
// public final class BlockInfo implements Serializable
public class BlockInfo implements Serializable
{
	private static final long serialVersionUID = -2528305725971591495L;

//	private String sessionKey;
	private String nodeName;
	private String fingerPrint;
//	private Date timeStamp;
//	private IPAddress ip;
	
	public BlockInfo(String nodeName, String fingerPrint)
	{
		this.nodeName = nodeName;
		this.fingerPrint = fingerPrint;
//		this.ip = ip;
	}

	/*
	public BlockInfo(String sessionKey, String nodeName, String fingerPrint, Date timeStamp)
	{
		this.sessionKey = sessionKey;
		this.nodeName = nodeName;
		this.fingerPrint = fingerPrint;
		this.timeStamp = timeStamp;
	}
	
	public String getSessionKey()
	{
		return this.sessionKey;
	}
	*/

	public String getNodeName()
	{
		return this.nodeName;
	}

	public String getFingerPrint()
	{
		return this.fingerPrint;
	}

	/*
	public Date getTimeStamp()
	{
		return this.timeStamp;
	}
	*/

	/*
	public IPAddress getIP()
	{
		return this.ip;
	}
	*/
}
