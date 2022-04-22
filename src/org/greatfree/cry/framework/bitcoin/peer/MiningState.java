package org.greatfree.cry.framework.bitcoin.peer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author libing
 * 
 * 02/26/2022
 *
 */
class MiningState
{
	private String sessionKey;
	private AtomicInteger chainLength;
	private AtomicInteger validateCount;
	private AtomicInteger joinCount;
	
	public MiningState(String sessionKey, int chainLength)
	{
		this.sessionKey = sessionKey;
		this.chainLength = new AtomicInteger(chainLength);
		this.joinCount = new AtomicInteger(0);
		this.validateCount = new AtomicInteger(0);
	}
	
	public String getSessionKey()
	{
		return this.sessionKey;
	}

	public void incrementValidation()
	{
		this.validateCount.incrementAndGet();
	}
	
	public void incrementJoin()
	{
		this.joinCount.incrementAndGet();
	}
	
	public boolean isValidationDone()
	{
		return this.validateCount.get() >= this.chainLength.get() ? true : false;
	}
	
	public boolean isJoinDone()
	{
		return this.joinCount.get() >= this.joinCount.get() ? true : false;
	}
	
	public void clear()
	{
		this.joinCount.set(0);
		this.validateCount.set(0);
	}
}
