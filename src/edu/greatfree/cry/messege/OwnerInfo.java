package edu.greatfree.cry.messege;

import java.io.Serializable;

/**
 * 
 * @author Bing Li
 * 
 * 02/07/2022
 *
 */
public class OwnerInfo implements Serializable
{
	private static final long serialVersionUID = 6228198372657961753L;

//	private String sessionKey;
	private String signature;
	private String ownerName;
	
//	public OwnerInfo(String sessionKey, String signature, String ownerName)
	public OwnerInfo(String signature, String ownerName)
	{
//		this.sessionKey = sessionKey;
		this.signature = signature;
		this.ownerName = ownerName;
	}

	/*
	public String getSessionKey()
	{
		return this.sessionKey;
	}
	*/
	
	public String getSignature()
	{
		return this.signature;
	}

	public String getOwnerName()
	{
		return this.ownerName;
	}
	
	public String toString()
	{
//		return "SessionKey = " + this.sessionKey + ";\nSignature = " + this.signature + ";\nOwnerName = " + this.ownerName;
		return "Signature = " + this.signature + ";\nOwnerName = " + this.ownerName;
	}
}
