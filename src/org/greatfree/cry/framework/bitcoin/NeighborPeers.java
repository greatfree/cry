package org.greatfree.cry.framework.bitcoin;

import java.io.Serializable;

/**
 * 
 * @author Bing Li
 * 
 * 02/16/2022
 *
 */
public final class NeighborPeers implements Serializable
{
	private static final long serialVersionUID = -5588134138783012747L;

	private String hostPN;
	private String precedingPN;
	private String succeedingPN;

	/*
	 * For the first joined peer, it preceding and succeeding neighbors are itself. 02/17/2022, Bing Li
	 * 
	 * The first joined peer invokes the constructor. 02/16/2022, Bing Li
	 */
	public NeighborPeers(String hostPN)
	{
		this.hostPN = hostPN;
		this.precedingPN = hostPN;
		this.succeedingPN = hostPN;
	}

	/*
	 * Later joined peers invoke the constructor. 02/16/2022, Bing Li
	 */
	public NeighborPeers(String hostPN, String precedingPN, String succeedingPN)
	{
		this.hostPN = hostPN;
		this.precedingPN = precedingPN;
		this.succeedingPN = succeedingPN;
	}
	
	public String getHostPN()
	{
		return this.hostPN;
	}
	
	public synchronized void setPrecedingPN(String precedingPN)
	{
		this.precedingPN = precedingPN;
	}
	
	public synchronized String getPrecedingPN()
	{
		return this.precedingPN;
	}
	
	public synchronized void setSucceedingPN(String succeedingPN)
	{
		this.succeedingPN = succeedingPN;
	}
	
	public synchronized String getSucceedingPN()
	{
		return this.succeedingPN;
	}
}
