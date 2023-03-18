package edu.greatfree.cry.cluster.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.cluster.message.ClusterMessageType;
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
import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.messege.multicast.ChildResponse;
import edu.greatfree.cry.messege.multicast.ChildRootRequest;
import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.messege.multicast.ClusterRequest;
import edu.greatfree.cry.messege.multicast.CollectedClusterResponse;
import edu.greatfree.cry.messege.multicast.InterChildrenRequest;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;
import edu.greatfree.cry.messege.multicast.RootAddressNotification;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
// final class ClusterChildTask implements ServerTask
final class ClusterChildTask extends ClusterTask
{
	private final static Logger log = Logger.getLogger("edu.greatfree.cry.cluster.child");

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
		switch (notification.getType())
		{
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
					ChildServiceProvider.CRY().processIntercastNotification(itn);
				}
				catch (IOException | InterruptedException | DistributedNodeFailedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | ClassNotFoundException | SymmetricKeyUnavailableException | CryptographyMismatchException | PublicKeyUnavailableException | RemoteReadException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
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
		}
		
	}

	@Override
	public ServerMessage processRequest(ServerMessage request)
	{
		/*
		switch (request.getType())
		{
			case MulticastMessageType.INTERCAST_REQUEST:
				log.info("INTERCAST_REQUEST received at " + Calendar.getInstance().getTime());
				IntercastRequest ir = (IntercastRequest)request;
				try
				{
					CollectedClusterResponse ccr = ChildServiceProvider.CRY().processIntercastRequest(ir);
					ChildServiceProvider.CRY().processIntercastResponse(ccr);
					return ccr;
				}
				catch (DistributedNodeFailedException | IOException | InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | RemoteReadException | CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
		}
		*/
		return null;
	}

	@Override
	public void processNotification(PrimitiveMulticastResponse notification)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
//	public void processNotification(MulticastNotification notification)
//	public void processNotification(ClusterNotification notification)
	public void processNotification(PrimitiveMulticastNotification notification)
	{
		log.info("MulticastNotification to be processed ...");
//		switch (notification.getType())
		switch (notification.getMultiAppID())
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
				catch (IOException | InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | ClassNotFoundException | SymmetricKeyUnavailableException | CryptographyMismatchException | PublicKeyUnavailableException | DistributedNodeFailedException | RemoteReadException | ShortBufferException | CheatingException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				break;

				/*
			case MulticastMessageType.CLUSTER_NOTIFICATION:
				log.info("CLUSTER_NOTIFICATION received at " + Calendar.getInstance().getTime());
				ClusterNotification cn = (ClusterNotification)notification;
				try
				{
					ChildServiceProvider.CRY().processNotification(cn);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException
						| DistributedNodeFailedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | SymmetricKeyUnavailableException | CryptographyMismatchException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				*/
		}		
	}

	/*
	@Override
//	public CollectedClusterResponse processRequest(PrimitiveMulticastRequest request)
	public CollectedClusterResponse processRequest(ClusterRequest request)
	{
//		switch (request.getType())
		switch (request.getMultiAppID())
		{
			case MulticastMessageType.CLUSTER_REQUEST:
				log.info("CLUSTER_REQUEST received at " + Calendar.getInstance().getTime());
				ClusterRequest cr = (ClusterRequest)request;
				PrimitiveMulticastResponse response;
				if (cr.getRequestType() == MulticastMessageType.INTER_CHILDREN_REQUEST)
				{
					InterChildrenRequest icr = (InterChildrenRequest)cr;
					Child.CRY().forward(icr);
					response = ChildServiceProvider.CRY().processRequest(icr);
					try
					{
						Child.CRY().notifySubRoot(icr.getSubRootIP(), icr.getSubRootPort(), response);
					}
					catch (IOException | InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | ClassNotFoundException | SymmetricKeyUnavailableException | CryptographyMismatchException | PublicKeyUnavailableException | DistributedNodeFailedException | RemoteReadException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					Child.CRY().forward(cr);
					response = ChildServiceProvider.CRY().processRequest(cr);
					try
					{
						Child.CRY().notifyRoot(response);
					}
					catch (IOException | InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | ClassNotFoundException | SymmetricKeyUnavailableException | CryptographyMismatchException | PublicKeyUnavailableException | DistributedNodeFailedException | RemoteReadException e)
					{
						e.printStackTrace();
					}
				}
				break;
		}
		return null;
	}
	*/

	@Override
	public void processNotification(ClusterNotification notification)
	{
		log.info("CLUSTER_NOTIFICATION received at " + Calendar.getInstance().getTime());
//		ClusterNotification cn = (ClusterNotification)notification;
		try
		{
			ChildServiceProvider.CRY().processNotification(notification);
		}
		catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException
				| DistributedNodeFailedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | SymmetricKeyUnavailableException | CryptographyMismatchException | PublicKeyUnavailableException | RemoteIPNotExistedException e)
		{
			e.printStackTrace();
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void processChildRequest(ClusterRequest request)
	{
		PrimitiveMulticastResponse response;
		if (request.getRequestType() == MulticastMessageType.INTER_CHILDREN_REQUEST)
		{
			log.info("INTER_CHILDREN_REQUEST being processed ...");
			InterChildrenRequest icr = (InterChildrenRequest)request;
			Child.CRY().forward(icr);
			response = ChildServiceProvider.CRY().processRequest(icr);
			try
			{
//				Child.CRY().notifySubRoot(icr.getSubRootIP(), icr.getSubRootPort(), response);
				Child.CRY().notifySubRoot(icr.getSubRootIP(), response);
			}
			catch (IOException | InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | ClassNotFoundException | SymmetricKeyUnavailableException | CryptographyMismatchException | PublicKeyUnavailableException | DistributedNodeFailedException | RemoteReadException | RemoteIPNotExistedException e)
			{
				e.printStackTrace();
			}
			catch (PeerNameIsNullException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			Child.CRY().forward(request);
			response = ChildServiceProvider.CRY().processRequest(request);
			try
			{
				Child.CRY().notifyRoot(response);
			}
			catch (IOException | InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | ClassNotFoundException | SymmetricKeyUnavailableException | CryptographyMismatchException | PublicKeyUnavailableException | DistributedNodeFailedException | RemoteReadException | RemoteIPNotExistedException e)
			{
				e.printStackTrace();
			}
			catch (PeerNameIsNullException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public ChildRootResponse processRequest(ChildRootRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerMessage processRequest(PrimitiveMulticastRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CollectedClusterResponse processRootRequest(ClusterRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerMessage processRequest(IntercastRequest request)
	{
		try
		{
			CollectedClusterResponse ccr = ChildServiceProvider.CRY().processIntercastRequest(request);
			List<PrimitiveMulticastResponse> reses = ccr.getResponses();
			if (reses != null)
			{
				log.info("PrimitiveMulticastResponse's size = " + reses.size());
			}
			else
			{
				log.info("PrimitiveMulticastResponse is NULL!");
			}
			ChildServiceProvider.CRY().processIntercastResponse(ccr);
			return ccr;
		}
		catch (DistributedNodeFailedException | IOException | InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | RemoteReadException | CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException | PublicKeyUnavailableException e)
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

}
