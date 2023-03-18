package edu.greatfree.cry.multicast.root;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.concurrency.Sync;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
final class MulticastPoint
{
	private final String collaboratorKey;
	
	private Sync sync;
	private AtomicInteger receiverCount;
	private List<PrimitiveMulticastResponse> responses;
	
	public MulticastPoint(String collaboratorKey)
	{
		this.collaboratorKey = collaboratorKey;
		this.sync = new Sync(false);
		this.receiverCount = new AtomicInteger(0);
		this.responses = new CopyOnWriteArrayList<PrimitiveMulticastResponse>();
	}
	
	public String getCollabratorKey()
	{
		return this.collaboratorKey;
	}
	
	public boolean isFull()
	{
		return this.responses.size() >= this.receiverCount.get();
	}
	
	public boolean isAvailable()
	{
		return this.responses.size() > 0;
	}
	
	public void addResponse(PrimitiveMulticastResponse response)
	{
		this.responses.add(response);
	}
	
	public void addResponses(List<PrimitiveMulticastResponse> responses)
	{
		this.responses.addAll(responses);
	}

	public int getResponseCount()
	{
		return this.responses.size();
	}
	
	public void signal()
	{
		this.sync.signal();
	}
	
	public void signalAll()
	{
		this.sync.signalAll();
	}
	
	public void setReceiverSize(int count)
	{
		this.receiverCount.set(count);
	}

	public int getReceiverSize()
	{
		return this.receiverCount.get();
	}
	
	public void decrementReceiverSize()
	{
		this.receiverCount.decrementAndGet();
	}
	
	public void holdOn(long waitTime)
	{
		this.sync.holdOn(waitTime);
	}
	
	public List<PrimitiveMulticastResponse> getResponses()
	{
		return this.responses;
	}

}
