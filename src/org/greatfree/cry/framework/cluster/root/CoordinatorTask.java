package org.greatfree.cry.framework.cluster.root;

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

import org.greatfree.cluster.RootTask;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.cluster.message.ClusterAppID;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
final class CoordinatorTask implements RootTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.cluster.root");

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case ClusterAppID.STOP_ROOT_NOTIFICATION:
				log.info("STOP_ROOT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					ClusterRoot.CRY().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| InterruptedException | RemoteReadException | CryptographyMismatchException
						| DistributedNodeFailedException | PublicKeyUnavailableException | SymmetricKeyUnavailableException
						| IOException e)
				{
					e.printStackTrace();
				}
				break;
				
			case ClusterAppID.STOP_CHILDREN_NOTIFICATION:
				log.info("STOP_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					ClusterRoot.CRY().stopCluster();
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
		}
		
	}

	@Override
	public CollectedClusterResponse processRequest(ClusterRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
