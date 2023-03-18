package edu.greatfree.cry.cluster.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Set;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cluster.message.ClusterMessageType;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

import edu.greatfree.cry.cluster.ChildTask;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.messege.multicast.ClusterRequest;
import edu.greatfree.cry.messege.multicast.CollectedClusterResponse;
import edu.greatfree.cry.messege.multicast.InterChildrenNotification;
import edu.greatfree.cry.messege.multicast.InterChildrenRequest;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;
import edu.greatfree.cry.messege.multicast.SelectedChildNotification;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
final class ChildServiceProvider
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.cluster.child");
	
	private ChildTask task;
	
	private ChildServiceProvider()
	{
	}
	
	private static ChildServiceProvider instance = new ChildServiceProvider();
	
	public static ChildServiceProvider CRY()
	{
		if (instance == null)
		{
			instance = new ChildServiceProvider();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void init(ChildTask task)
	{
		this.task = task;
	}
	
	/*
	 * The method is revised in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
	 */
	public void processNotification(ClusterNotification notification) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		/*
		 * The intercasting has to be implemented again with the child-based approach. The below code is not useful. 02/26/2019, Bing Li
		 */
		/*
		 * Some of the code is written in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
		 */
		/*
		if (notification.getNotificationType() == MulticastMessageType.INTERCAST_NOTIFICATION)
		{
//			Child.CONTAINER().notifyRoot(this.task.processNotificationBySourceChild((IntercastNotification)notification));
			IntercastNotification in = (IntercastNotification)notification;
			InterChildrenNotification icn = this.task.prepareNotification(in);
			if (!in.getDestinationKey().equals(UtilConfig.EMPTY_STRING))
			{
				Child.CONTAINER().addPartnerIP(in.getDestinationIP());
				
				Child.CONTAINER().interUnicastNotify(icn);
			}
			else
			{
				Child.CONTAINER().addPartnerIPs(in.getDestinationIPs());
				if (icn.getIntercastNotification().getNotificationType() == MulticastMessageType.INTER_BROADCAST_NOTIFICATION)
				{
					Child.CONTAINER().interBroadcastNotify(icn);
				}
				else
				{
					Child.CONTAINER().interAnycastNotify(icn);
				}
			}
		}
		else
		{
			this.task.processNotification(notification);
		}
		*/
		/*
		 * The condition line is added to forward intercasting notifications. 04/26/2019, Bing Li
		 */
		/*
		 * I am implementing the root-based intercasting. It seems that the below lines are not necessary temporarily. 02/15/2019, Bing Li
		 */
		if (notification.getNotificationType() == MulticastMessageType.INTER_CHILDEN_NOTIFICATION)
		{
//			Child.CONTAINER().forward(notification);
//			System.out.println("ChildNotificationThread: INTER_CHILDEN_NOTIFICATION is received and it will be forwarded ...");
			Child.CRY().forward((InterChildrenNotification)notification);
		}
		else
		{
			/*
			 * One internal message, SelectedChildNotification, is processed here. 09/11/2020, Bing Li 
			 */
			if (notification.getMultiAppID() == ClusterMessageType.SELECTED_CHILD_NOTIFICATION)
			{
				log.info("SELECTED_CHILD_NOTIFICATION received at " + Calendar.getInstance().getTime());
				SelectedChildNotification scn = (SelectedChildNotification)notification;
				if (scn.isBusy())
				{
					Child.CRY().forward(notification);
				}
				Child.CRY().leaveCluster();
				Child.CRY().reset(scn.getRootKey(), scn.getClusterRootIP());
//				Child.CRY().joinCluster(scn.getClusterRootIP().getIP(), scn.getClusterRootIP().getPort());
				Child.CRY().joinCluster(scn.getClusterRootIP());
			}
			else
			{
				Child.CRY().forward(notification);
			}
		}
		this.task.processNotification(notification);
		/*
		else if (notification.getNotificationType() == MulticastMessageType.INTERCAST_REQUEST)
		{
//			Child.CONTAINER().notifyRoot(this.task.processSourceRequest((IntercastRequest)notification));
			
			// The intercasting has to be implemented again with the child-based approach. The below code is not useful. 02/26/2019, Bing Li
//			Response response = (Response)Child.CONTAINER().read(this.task.processSourceRequest((IntercastRequest)notification));
		}
		*/
		/*
		else if (notification.getNotificationType() == MulticastMessageType.INTER_CHILD_UNICAST_NOTIFICATION || notification.getNotificationType() == MulticastMessageType.INTER_CHILD_ANYCAST_NOTIFICATION || notification.getNotificationType() == MulticastMessageType.INTER_CHILD_BROADCAST_NOTIFICATION)
		{
			this.task.processDestinationNotification((IntercastChildNotification)notification);
		}
		else
		{
			this.task.processNotification(notification);
		}
		*/
	}
	
	/*
	 * The method is revised in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
	 */
	public PrimitiveMulticastResponse processRequest(ClusterRequest request)
	{
		/*
		else if (request.getRequestType() == MulticastMessageType.INTER_CHILD_UNICAST_REQUEST || request.getRequestType() == MulticastMessageType.INTER_CHILD_ANYCAST_REQUEST || request.getRequestType() == MulticastMessageType.INTER_CHILD_BROADCAST_REQUEST)
		{
			return this.task.processDestinationRequest((InterChildrenRequest)request);
		}
		*/
		return this.task.processRequest(request);
	}
	
	public void processIntercastNotification(IntercastNotification in) throws IOException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
//		Child.CONTAINER().notifyRoot(this.task.processNotificationBySourceChild((IntercastNotification)notification));
//		IntercastNotification in = (IntercastNotification)notification;
		InterChildrenNotification icn = this.task.prepareNotification(in);
		if (!in.getDestinationKey().equals(UtilConfig.EMPTY_STRING))
		{
//			log.info("********************* The IntercastNotification is Unicast one! ********************");
			// In case that the destination IP is identical to the one of the local child. The below condition is required. 02/19/2023, Bing Li
			if (!in.getDestinationIP().getIP().equals(Child.CRY().getChildIP()) || in.getDestinationIP().getPort() != Child.CRY().getChildPort())
			{
				Child.CRY().addPartnerIP(in.getDestinationIP());
			}

			log.info("Destination IP = " + in.getDestinationIP());

			/*
			 * The intercast-unicasting should get a new notification, which is processed locally, and the message to the destination child. 03/02/2019, Bing Li
			 */
			Child.CRY().interUnicastNotify(icn);
		}
		else
		{
//			log.info("********************* To forward the InterBroadcastNotification ... ********************");
			Set<IPAddress> ips = in.getDestinationIPs();
			for (IPAddress entry : ips)
			{
				log.info("Destination IP = " + entry);
			}
			Child.CRY().addPartnerIPs(in.getDestinationIPs());
//			log.info("getIntercastType() = " + icn.getIntercastNotification().getIntercastType());
//			if (icn.getIntercastNotification().getNotificationType() == MulticastMessageType.INTER_BROADCAST_NOTIFICATION)
			if (icn.getIntercastNotification().getIntercastType() == MulticastMessageType.INTER_BROADCAST_NOTIFICATION)
			{
//				log.info("********************* (1) To forward the InterBroadcastNotification ... ********************");
				Child.CRY().interBroadcastNotify(icn);
			}
			else
			{
//				log.info("********************* (2) To forward the InterBroadcastNotification ... ********************");
				Child.CRY().interAnycastNotify(icn);
			}
		}
	}

	/*
	 * The below problem is resolved. 02/21/2023, Bing Li
	 * 
	 * The method has a big bug. It is possible that the destination IPs are equal to the local one. If so, further inter-multicasting should exclude the IP. The issue is not considered in the method. 02/19/2023, Bing Li
	 */
	public CollectedClusterResponse processIntercastRequest(IntercastRequest ir) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
//		InterChildrenRequest icr = this.task.prepareRequest(Child.CONTAINER().getChildIP(), Child.CONTAINER().getChildPort(), ir);
		InterChildrenRequest icr = this.task.prepareRequest(ir);
//		icr.setSubRootIP(Child.CRY().getChildIP());
//		icr.setSubRootPort(Child.CRY().getChildPort());
		icr.setSubRootIP(Child.CRY().getChildAddress());
		if (!ir.getDestinationKey().equals(UtilConfig.EMPTY_STRING))
		{
			// In case that the destination IP is identical to the one of the local child. The below condition is required. 02/19/2023, Bing Li
			if (!ir.getDestinationIP().getIP().equals(Child.CRY().getChildIP()) || ir.getDestinationIP().getPort() != Child.CRY().getChildPort())
			{
				Child.CRY().addPartnerIP(ir.getDestinationIP());
			}
		}
		else
		{
			for (IPAddress entry : ir.getDestinationIPs())
			{
				log.info("destination = " + entry);
			}
			Child.CRY().addPartnerIPs(ir.getDestinationIPs());
		}
		
		if (ir.getIntercastType() == MulticastMessageType.INTER_UNICAST_REQUEST)
		{
//			System.out.println("ChildServiceProvider-processIntercastRequest(): I am processing INTER_UNICAST_REQUEST ...");
			/*
			 * One problem exists. When performing multicasting, the message types, INTER_BROADCAST_REQUEST or INTER_ANYCAST_REQUEST, are included in the previous multicasting code. So it is required to fix the problem here. 03/12/2019, Bing Li
			 */
			return Child.CRY().interUnicastRead(icr);
		}
		else if (ir.getIntercastType() == MulticastMessageType.INTER_BROADCAST_REQUEST)
		{
			return Child.CRY().interBroadcastRead(icr);
		}
		else
		{
			CollectedClusterResponse ccr = Child.CRY().interAnycastRead(icr);
			if (ccr.getResponses() != null)
			{
				log.info("CollectedClusterResponse's responses size is " + ccr.getResponses().size());
			}
			else
			{
				log.info("CollectedClusterResponse's responses are NULL!");
			}
//			return Child.CRY().interAnycastRead(icr);
			return ccr;
		}
	}

	public PrimitiveMulticastResponse processRequest(InterChildrenRequest request)
	{
		return this.task.processRequest(request);
	}
	
	public void processNotification(InterChildrenNotification notification)
	{
		this.task.processNotification(notification);
	}
	
	public void processIntercastResponse(CollectedClusterResponse res)
	{
		this.task.processResponse(res);
	}

	/*
	public Response processRequestAtRoot(Request request)
	{
		return this.task.processRequestAtRoot(request);
	}
	*/
}
