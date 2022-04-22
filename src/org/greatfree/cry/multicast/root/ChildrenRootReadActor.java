package org.greatfree.cry.multicast.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.multicast.root.ChildrenMulticastRequest;

/**
 * 
 * @author libing
 * 
 * 04/08/2022
 *
 */
final class ChildrenRootReadActor extends AsyncMulticastor<ChildrenMulticastRequest>
{

//	public ChildrenRootReadActor(RootSyncMulticastor multicastor)
	public ChildrenRootReadActor(RootSyncMulticastor multicastor, int cryptoOption)
	{
		super(multicastor, cryptoOption);
//		super(multicastor);
	}

	@Override
	public void perform(ChildrenMulticastRequest request)
	{
		try
		{
//			super.getMulticastor().readWithinNChildren(request.getChildrenKeys(), request.getRequest(), super.getCryptoOption());
			super.getMulticastor().readWithinNChildren(request.getChildrenKeys(), request.getRequest());
		}
		catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| SignatureException | DistributedNodeFailedException | IOException | RemoteReadException
				| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
				| PublicKeyUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(ChildrenMulticastRequest request, int cryptoOption)
	{
	}
	*/

}
