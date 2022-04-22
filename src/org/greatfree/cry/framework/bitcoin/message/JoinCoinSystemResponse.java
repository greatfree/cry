package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.cry.framework.bitcoin.NeighborPeers;
import org.greatfree.message.ServerMessage;

/**
 * 
 * @author Bing Li
 * 
 * 02/15/2022
 *
 */
public class JoinCoinSystemResponse extends ServerMessage
{
	private static final long serialVersionUID = -1012567376483727357L;

	private String head;
	private NeighborPeers neighbors;
//	private boolean isDone;

//	public JoinCoinSystemResponse(NeighborPeers pp, boolean isDone)
	public JoinCoinSystemResponse(String head, NeighborPeers np)
	{
		super(CoinAppID.JOIN_COIN_SYSTEM_RESPONSE);
		this.head = head;
		this.neighbors = np;
//		this.isDone = isDone;
	}
	
	public String getHead()
	{
		return this.head;
	}
	
	public NeighborPeers getNeighbors()
	{
		return this.neighbors;
	}

	/*
	public boolean isDone()
	{
		return this.isDone;
	}
	*/
}
