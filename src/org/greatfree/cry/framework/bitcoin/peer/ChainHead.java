package org.greatfree.cry.framework.bitcoin.peer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * The networking head is critical to manage the chain validation and the transactions mining. 02/26/2022, Bing Li
 * 
 * @author libing
 * 
 * 02/26/2022
 *
 */
class ChainHead
{
	private Map<String, MiningState> states;
	
	private ChainHead()
	{
	}
	
	private static ChainHead instance = new ChainHead();
	
	public static ChainHead COIN()
	{
		if (instance == null)
		{
			instance = new ChainHead();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void init()
	{
		this.states = new ConcurrentHashMap<String, MiningState>();
	}
	
	public void setChainLength(String sessionKey, int length)
	{
		this.states.put(sessionKey, new MiningState(sessionKey, length));
	}
	
	public void clear(String sessionKey)
	{
		this.states.get(sessionKey).clear();
	}
	
	public void remove(String sessionKey)
	{
		this.states.remove(sessionKey);
	}

	public void incrementValidation(String sessionKey)
	{
		this.states.get(sessionKey).incrementValidation();
	}
	
	public void incrementJoin(String sessionKey)
	{
		this.states.get(sessionKey).incrementJoin();
	}
	
	public boolean isValidationDone(String sessionKey)
	{
		return this.states.get(sessionKey).isValidationDone();
	}
	
	public boolean isJoinDone(String sessionKey)
	{
		return this.states.get(sessionKey).isJoinDone();
	}
}
