package org.greatfree.cry.cluster.root;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

/**
 * 
 * @author libing
 * 
 * 04/25/2022
 *
 */
final class ClusterServerTask implements ServerTask
{
//	private final static Logger log = Logger.getLogger("org.greatfree.cry.cluster.root");

	@Override
	public void processNotification(Notification notification)
	{
		/*
		 * 
		 * The below code is useful, but the lines are commented temporarily before the revision of clustering is done. 04/29/2022, Bing Li
		 * 
		 */
		/*
		log.info("Before processing clustering notifications: Application ID = " + notification.getApplicationID());
		log.info("Before processing clustering notifications: notification type = " + notification.getType());
		switch (notification.getType())
		{
			case ClusterMessageType.JOIN_NOTIFICATION:
				log.info("JOIN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				JoinNotification jn = (JoinNotification)notification;
				try
				{
					Clustering.addChild(jn.getChildID());
				}
				catch (ClassNotFoundException | RemoteReadException | IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case ClusterMessageType.LEAVE_NOTIFICATION:
				log.info("LEAVE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				LeaveNotification ln = (LeaveNotification)notification;
				try
				{
					Clustering.removeChild(ln.getChildID());
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
				
			case ClusterMessageType.HEAVY_WORKLOAD_NOTIFICATION:
				log.info("HEAVY_WORKLOAD_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HeavyWorkloadNotification hwn = (HeavyWorkloadNotification)notification;
				try
				{
					ClusterRoot.CRY().broadcastNotifyWithinNChildren(new SelectedChildNotification(hwn.getTaskClusterRootKey(), hwn.getTaskClusterRootIP(), true), hwn.getNodeSize());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
						| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
						| PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case ClusterMessageType.SUPERFLUOUS_RESOURCES_NOTIFICATION:
				log.info("SUPERFLUOUS_RESOURCES_NOTIFICATION received @" + Calendar.getInstance().getTime());
				SuperfluousResourcesNotification srn = (SuperfluousResourcesNotification)notification;
				try
				{
					ClusterRoot.CRY().unicastNotify(new SelectedChildNotification(srn.getPoolClusterRootKey(), srn.getPoolClusterRootIP(), false));
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
						| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
						| PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;

			case MulticastMessageType.CLUSTER_NOTIFICATION:
				log.info("CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ClusterNotification cn = (ClusterNotification)notification;
				try
				{
					ClusterRoot.CRY().processNotification(cn);
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
						| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
						| PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				
			case MulticastMessageType.INTERCAST_NOTIFICATION:
				log.info("INTERCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				IntercastNotification in = (IntercastNotification)notification;
				try
				{
					ClusterRoot.CRY().processIntercastNotification(in);
				}
				catch (IOException | InterruptedException e)
				{
					e.printStackTrace();
				}
				break;

			case ClusterMessageType.CHILD_RESPONSE:
				ChildResponse cr = (ChildResponse)notification;
				try
				{
					ClusterRoot.CRY().saveResponse(cr);
				}
				catch (InterruptedException e)
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
		 * 
		 * The below code is useful, but the lines are commented temporarily before the revision of clustering is done. 04/29/2022, Bing Li
		 * 
		 */
		/*
		switch (request.getType())
		{
			case ClusterMessageType.PARTITION_SIZE_REQUEST:
				log.info("PARTITION_SIZE_REQUEST received @" + Calendar.getInstance().getTime());
//				PartitionSizeRequest psr = (PartitionSizeRequest)request;
				return new PartitionSizeResponse(ClusterRoot.CRY().getPartitionSize());
				
			case ClusterMessageType.CLUSTER_SIZE_REQUEST:
				log.info("CLUSTER_SIZE_REQUEST received @" + Calendar.getInstance().getTime());
//				ClusterSizeRequest csr = (ClusterSizeRequest)request;
				return new ClusterSizeResponse(ClusterRoot.CRY().getChildrenCount());
				
			case ClusterMessageType.ADDITIONAL_CHILDREN_REQUEST:
				log.info("ADDITIONAL_CHILDREN_REQUEST received @" + Calendar.getInstance().getTime());
				AdditionalChildrenRequest acr = (AdditionalChildrenRequest)request;
				return new AdditionalChildrenResponse(ClusterRoot.CRY().getChildrenKeys(acr.getSize()));
				
			case MulticastMessageType.CHILD_ROOT_REQUEST:
				log.info("CHILD_ROOT_REQUEST received @" + Calendar.getInstance().getTime());
				ChildRootRequest crr = (ChildRootRequest)request;
				return ClusterRoot.CRY().processRequest(crr);
				
			case MulticastMessageType.CLUSTER_REQUEST:
				log.info("CLUSTER_REQUEST received @" + Calendar.getInstance().getTime());
				ClusterRequest cr = (ClusterRequest)request;
				try
				{
					return ClusterRoot.CRY().processRequest(cr);
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | DistributedNodeFailedException | IOException | RemoteReadException
						| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
						| PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				
			case MulticastMessageType.INTERCAST_REQUEST:
				log.info("INTERCAST_REQUEST received @" + Calendar.getInstance().getTime());
				IntercastRequest ir = (IntercastRequest)request;
				try
				{
					return ClusterRoot.CRY().processIntercastRequest(ir);
				}
				catch (ClassNotFoundException | RemoteReadException | IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				
		}
		*/
		return null;
	}

}
