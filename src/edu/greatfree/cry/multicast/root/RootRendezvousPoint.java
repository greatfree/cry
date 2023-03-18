package edu.greatfree.cry.multicast.root;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.greatfree.exceptions.Prompts;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
public final class RootRendezvousPoint
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.multicast.root");	
	
	private Map<String, MulticastPoint> points;
	// The time to wait for responses. If it lasts too long, it might get problems for the request processing. 11/28/2014, Bing Li
	private long waitTime;

	public RootRendezvousPoint(long waitTime)
	{
		this.points = new ConcurrentHashMap<String, MulticastPoint>();
		this.waitTime = waitTime;
	}
	
	public void dispose() throws InterruptedException
	{
		// Signal all of the current threads waiting for responses
		for (MulticastPoint entry : this.points.values())
		{
			entry.signalAll();
		}
		this.points.clear();
		this.points = null;
	}
	
	/*
	 * Save one particular response from the remote node. 11/28/2014, Bing Li
	 */
//	public void saveResponse(PrimitiveMulticastResponse response) throws InterruptedException
	public void saveResponse(PrimitiveMulticastResponse response) throws InterruptedException
	{
		log.info("I save response ...");
		if (response == null)
		{
			log.info("response is NULL!");
			return;
		}
		if (response.getCollaboratorKey() == null)
		{
			log.info("response.getCollaboratorKey() is NULL!");
			return;
		}
		// Check whether the response corresponds to the requestor. 11/29/2014, Bing Li
		if (!this.points.containsKey(response.getCollaboratorKey()))
		{
			this.points.put(response.getCollaboratorKey(), new MulticastPoint(response.getCollaboratorKey()));
		}
		
		// The below line might cause NullPointerException. 09/09/2020, Bing Li
		MulticastPoint point = this.points.get(response.getCollaboratorKey());
		if (point != null)
		{
			point.addResponse(response);
		}
		this.notify(response.getCollaboratorKey());
	}
	
	private void notify(String collaboratorKey)
	{
		try
		{
			if (this.points.get(collaboratorKey).isFull())
			{
				/*
				 * Sometimes it gets NullPointerException. It needs to be resolved. 12/01/2018, Bing Li	
				 */
				this.points.get(collaboratorKey).signal();
			}
			else
			{
//				System.out.println("3) RootRendezvousPoint-notify(): ");
			}
		}
		catch (NullPointerException e)
		{
			// The exception is caught during the procedure of anycasting. Any response must already signal the waiter. Thus, the multicast point is removed. 12/04/2018, Bing Li
			System.out.println(Prompts.ANYCAST_ALREADY_DONE);
		}
	}

	/*
	 * The failed node should be removed from the waiting counts. 08/26/2018, Bing Li
	 */
	public void decrementReceiverSize(String collaboratorKey)
	{
		this.points.get(collaboratorKey).decrementReceiverSize();
	}
	
	public void setReceiverSize(String collaboratorKey, int size)
	{
		if (!this.points.containsKey(collaboratorKey))
		{
			this.points.put(collaboratorKey, new MulticastPoint(collaboratorKey));
		}
		this.points.get(collaboratorKey).setReceiverSize(size);
	}
	
	public int getReceiverSize(String collaboratorKey)
	{
		return this.points.get(collaboratorKey).getReceiverSize();
	}

	/*
	 * Waiting for responses from the distributed nodes. 08/26/2018, Bing Li
	 */
	public List<PrimitiveMulticastResponse> waitForResponses(String collaboratorKey)
	{
		// It is possible that the responses are received before the waiting is set up. So it is required to ensure no responses are received or the count of the received responses is lower than the required one. 11/03/2018, Bing Li
		// Wait for responses from distributed nodes
		if (!this.points.containsKey(collaboratorKey))
		{
			this.points.put(collaboratorKey, new MulticastPoint(collaboratorKey));
		}
		if (!this.points.get(collaboratorKey).isFull())
		{
//			log.info("RootRendezvousPoint is waiting for signal ...");
			this.points.get(collaboratorKey).holdOn(this.waitTime);
//			log.info("RootRendezvousPoint's waiting is finished ...");
		}
		List<PrimitiveMulticastResponse> results = this.points.get(collaboratorKey).getResponses();
		// Remove the list from the map
		this.points.remove(collaboratorKey);
		return results;
	}

	/*
	 * Waiting for responses from the distributed nodes. But in the case, a single response from one partition is enough since data within partition is replicated and identical such that it is not necessary to wait additional ones. 08/26/2018, Bing Li
	 */
	public PrimitiveMulticastResponse waitForResponseUponPartition(String collaboratorKey)
	{
		if (!this.points.containsKey(collaboratorKey))
		{
			this.points.put(collaboratorKey, new MulticastPoint(collaboratorKey));
		}
		// Once if a replica is available, it is not necessary to wait. 09/09/2020, Bing Li
		if (!this.points.get(collaboratorKey).isAvailable())
		{
			this.points.get(collaboratorKey).holdOn(this.waitTime);
		}
		List<PrimitiveMulticastResponse> results = this.points.get(collaboratorKey).getResponses();
		this.points.remove(collaboratorKey);
		return results.get(0);
	}
}
