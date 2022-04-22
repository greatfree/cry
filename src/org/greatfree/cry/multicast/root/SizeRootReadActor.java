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
import org.greatfree.multicast.root.SizeMulticastRequest;

/**
 * 
 * @author libing
 * 
 * 04/08/2022
 *
 */
final class SizeRootReadActor extends AsyncMulticastor<SizeMulticastRequest>
{

//	public SizeRootReadActor(RootSyncMulticastor multicastor)
	public SizeRootReadActor(RootSyncMulticastor multicastor, int cryptoOption)
	{
		super(multicastor, cryptoOption);
//		super(multicastor);
	}

	@Override
	public void perform(SizeMulticastRequest request)
	{
		try
		{
//			super.getMulticastor().readWithNResponses(request.getRequest(), request.getChildrenSize(), super.getCryptoOption());
			super.getMulticastor().readWithNResponses(request.getRequest(), request.getChildrenSize());
		}
		catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
				| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
				| PublicKeyUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(SizeMulticastRequest request, int cryptoOption)
	{
	}
	*/

}
