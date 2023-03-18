package edu.greatfree.cry.cluster.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cluster.message.AdditionalChildrenRequest;
import org.greatfree.cluster.message.AdditionalChildrenResponse;
import org.greatfree.cluster.message.ClusterMessageType;
import org.greatfree.cluster.message.ClusterSizeResponse;
import org.greatfree.cluster.message.HeavyWorkloadNotification;
import org.greatfree.cluster.message.JoinNotification;
import org.greatfree.cluster.message.LeaveNotification;
import org.greatfree.cluster.message.PartitionSizeResponse;
import org.greatfree.cluster.message.SuperfluousResourcesNotification;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;

import edu.greatfree.cry.cluster.ClusterTask;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.messege.multicast.ChildResponse;
import edu.greatfree.cry.messege.multicast.ChildRootRequest;
import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.messege.multicast.ClusterRequest;
import edu.greatfree.cry.messege.multicast.CollectedClusterResponse;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;
import edu.greatfree.cry.messege.multicast.SelectedChildNotification;

/**
 * 
 * @author libing
 * 
 * 04/25/2022
 *
 */
// final class ClusterServerTask implements ServerTask
final class ClusterRootTask extends ClusterTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.cluster.root");

	@Override
	public void processNotification(Notification notification)
	{
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		return null;
	}

	@Override
	public void processNotification(ServerMessage notification)
	{
		/*
		 * 
		 * The below code is useful, but the lines are commented temporarily before the revision of clustering is done. 04/29/2022, Bing Li
		 * 
		 */
//		log.info("Before processing clustering notifications: Application ID = " + notification.getApplicationID());
//		log.info("Before processing clustering notifications: notification type = " + notification.getType());
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
				catch (RemoteIPNotExistedException e)
				{
					log.info(e.toString());
				}
				catch (PeerNameIsNullException e)
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
				catch (RemoteIPNotExistedException e)
				{
					log.info(e.toString());
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				catch (IPNotExistedException e)
				{
					log.info(e.toString());
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
				catch (RemoteIPNotExistedException e)
				{
					log.info(e.toString());
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				catch (IPNotExistedException e)
				{
					log.info(e.toString());
					e.printStackTrace();
				}
				break;
				
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
				catch (PeerNameIsNullException e)
				{
					log.info(e.toString());
				}
				catch (IPNotExistedException e)
				{
					log.info(e.toString());
					e.printStackTrace();
				}
				break;

			case ClusterMessageType.CHILD_RESPONSE:
				log.info("CHILD_RESPONSE received @" + Calendar.getInstance().getTime());
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
	}

	@Override
	public ServerMessage processRequest(ServerMessage request)
	{
		/*
		 * 
		 * The below code is useful, but the lines are commented temporarily before the revision of clustering is done. 04/29/2022, Bing Li
		 * 
		 */
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
				catch (RemoteIPNotExistedException e)
				{
					log.info(e.toString());
				}
				catch (PeerNameIsNullException e)
				{
					log.info(e.toString());
				}
				catch (IPNotExistedException e)
				{
					log.info(e.toString());
					e.printStackTrace();
				}
		}
		return null;
	}

	/*
	@Override
	public void processNotification(PrimitiveMulticastResponse notification)
	{
		
	}
	*/

	@Override
//	public void processNotification(MulticastNotification notification)
	public void processNotification(ClusterNotification notification)
	{
		log.info("CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
		try
		{
			ClusterRoot.CRY().processNotification(notification);
		}
		catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
				| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
				| PublicKeyUnavailableException e)
		{
			e.printStackTrace();
		}
		catch (RemoteIPNotExistedException e)
		{
			log.info(e.toString());
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}
		catch (IPNotExistedException e)
		{
			log.info(e.toString());
			e.printStackTrace();
		}
	}
	
	@Override
	public CollectedClusterResponse processRootRequest(ClusterRequest request)
	{
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
		catch (RemoteIPNotExistedException e)
		{
			log.info(e.toString());
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}
		catch (IPNotExistedException e)
		{
			log.info(e.toString());
			e.printStackTrace();
		}
		return null;
	}

	@Override
//	public ServerMessage processRequest(MulticastRequest request)
	public ChildRootResponse processRequest(ChildRootRequest request)
	{
		log.info("CHILD_ROOT_REQUEST received @" + Calendar.getInstance().getTime());
		return ClusterRoot.CRY().processRequest(request);
	}

	@Override
	public void processNotification(PrimitiveMulticastResponse notification)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processNotification(PrimitiveMulticastNotification notification)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServerMessage processRequest(PrimitiveMulticastRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processChildRequest(ClusterRequest request)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServerMessage processRequest(IntercastRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
