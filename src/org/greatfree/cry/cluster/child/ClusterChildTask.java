package org.greatfree.cry.cluster.child;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
final class ClusterChildTask implements ServerTask
{
//	private final static Logger log = Logger.getLogger("org.greatfree.cry.cluster.child");

	@Override
	public void processNotification(Notification notification)
	{
		/*
		 * 
		 * The below code is useful, but the lines are commented temporarily before the revision of clustering is done. 04/29/2022, Bing Li
		 * 
		 */

		/*
		switch (notification.getType())
		{
			case MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION:
				log.info("ROOT_IPADDRESS_BROADCAST_NOTIFICATION received at " + Calendar.getInstance().getTime());
				RootAddressNotification ran = (RootAddressNotification)notification;
				Child.CRY().asyncNotify(ran);
				Child.CRY().setRootIP(ran.getRootAddress());
				try
				{
					Child.CRY().joinCluster();
				}
				catch (IOException | InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MulticastMessageType.CLUSTER_NOTIFICATION:
				log.info("CLUSTER_NOTIFICATION received at " + Calendar.getInstance().getTime());
				ClusterNotification cn = (ClusterNotification)notification;
				try
				{
					ChildServiceProvider.CHILD().processNotification(cn);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException
						| DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;

			case MulticastMessageType.CLUSTER_REQUEST:
				log.info("CLUSTER_REQUEST received at " + Calendar.getInstance().getTime());
				ClusterRequest cr = (ClusterRequest)notification;
				MulticastResponse response;
				if (cr.getRequestType() == MulticastMessageType.INTER_CHILDREN_REQUEST)
				{
					InterChildrenRequest icr = (InterChildrenRequest)cr;
					Child.CRY().forward(icr);
					response = ChildServiceProvider.CHILD().processRequest(icr);
					try
					{
						Child.CRY().notifySubRoot(icr.getSubRootIP(), icr.getSubRootPort(), response);
					}
					catch (IOException | InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					Child.CRY().forward(cr);
					response = ChildServiceProvider.CHILD().processRequest(cr);
					try
					{
						Child.CRY().notifyRoot(response);
					}
					catch (IOException | InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				break;
				
			case ClusterMessageType.CHILD_RESPONSE:
				log.info("CHILD_RESPONSE received at " + Calendar.getInstance().getTime());
				ChildResponse cres = (ChildResponse)notification;
				try
				{
					Child.CRY().saveResponse(cres);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MulticastMessageType.INTERCAST_NOTIFICATION:
				log.info("INTERCAST_NOTIFICATION received at " + Calendar.getInstance().getTime());
				IntercastNotification itn = (IntercastNotification)notification;
				try
				{
					ChildServiceProvider.CHILD().processIntercastNotification(itn);
				}
				catch (IOException | InterruptedException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
				
		}
		*/
		
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		/*
		switch (request.getType())
		{
			case MulticastMessageType.INTERCAST_REQUEST:
				log.info("INTERCAST_REQUEST received at " + Calendar.getInstance().getTime());
				IntercastRequest ir = (IntercastRequest)request;
				try
				{
					CollectedClusterResponse ccr = ChildServiceProvider.CHILD().processIntercastRequest(ir);
					ChildServiceProvider.CHILD().processIntercastResponse(ccr);
					return ccr;
				}
				catch (DistributedNodeFailedException | IOException e)
				{
					e.printStackTrace();
				}
		}
		*/
		return null;
	}

}
