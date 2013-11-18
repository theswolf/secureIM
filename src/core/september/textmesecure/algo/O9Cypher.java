package core.september.textmesecure.algo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.util.encoders.Base64;

import core.september.textmesecure.sql.models.KeyRepo;

public class O9Cypher {
	
	private static O9Cypher cypher;
	private static final String TAG = O9Cypher.class.getSimpleName();
	
	private O9Cypher() {
		 Security.addProvider(new BouncyCastleProvider());
	}
	
	public static O9Cypher getInstance() {
		if(cypher == null) {
			cypher = new O9Cypher();
		}
		return cypher;
	}
	
	private static byte[] serialize(Object input) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] bytes;
		try {
		  try {
			out = new ObjectOutputStream(bos);
			 out.writeObject(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		 
		  bytes = bos.toByteArray();
		  
		} finally {
		  try {
			 if(out != null)
			out.close();
			 if(bos != null)
			 bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		  
		}
		return bytes;
	}
	
	 private static byte[] cipherData(PaddedBufferedBlockCipher cipher, byte[] data)
		      throws Exception {
		    int minSize = cipher.getOutputSize(data.length);

		    byte[] outBuf = new byte[minSize];

		    int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);

		    int length2 = cipher.doFinal(outBuf, length1);

		    int actualLength = length1 + length2;

		    byte[] result = new byte[actualLength];

		    System.arraycopy(outBuf, 0, result, 0, result.length);

		    return result;
		  }
	  
	  private static  byte[] decrypt(byte[] cipher, byte[] key)
		      throws Exception {

		    PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(
		        new CBCBlockCipher(new AESEngine()));
		    
		   // System.out.println("Block size:"+aes.getBlockSize());
		    
		    //iv = new byte[aes.getBlockSize()];
		    //(new SecureRandom()).nextBytes(iv);

		    CipherParameters parms = new ParametersWithRandom(new KeyParameter(key));

		    aes.init(false, parms);

		    return cipherData(aes, cipher);
		  }

		  private static byte[] encrypt(byte[] plain, byte[] key) throws Exception {

			  
		    PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(
		        new CBCBlockCipher(

		        new AESEngine()));

		   // iv = new byte[aes.getBlockSize()];
		    //(new SecureRandom()).nextBytes(iv);
		    
		    CipherParameters parms = new ParametersWithRandom(new KeyParameter(key));

		    aes.init(true, parms);

		    return cipherData(aes, plain);
		  }
	
	
	private static Object deserialize(byte[] input) {
		Object o = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(input);
		ObjectInput in = null;
		try {
		  try {
			in = new ObjectInputStream(bis);
			 o = in.readObject(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
		} finally {
		  try {
			 if(bis != null)
			bis.close();
			 if(in != null)
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		}
		return o;
	}
	
	public String crypt(String myKey, String friendKey, String message) {
		try {
			KeyAgreement keyAgree = KeyAgreement.getInstance("DH", "BC");
			//PrivateKey pk = (PrivateKey)Base64.decode(myKey.getBytes());
			PrivateKey pk = (PrivateKey) O9Cypher.deserialize(Base64.decode(myKey.getBytes()));
			PublicKey pubKey =  (PublicKey) O9Cypher.deserialize(Base64.decode(friendKey.getBytes()));
			keyAgree.init(pk);
			keyAgree.doPhase(pubKey, true);
			byte[] crypted = O9Cypher.encrypt(message.getBytes(), keyAgree.generateSecret("AES").getEncoded());
			return new String(Base64.encode(crypted));
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			android.util.Log.d(TAG, e.getMessage(), e);
			return null;
		} 
	}

	public KeyRepo generateKey(String actualFriendLogin) {
		try {
			SecureRandom secRandom = new SecureRandom();
		    int bitLength = 512;
		    BigInteger g512 = BigInteger.probablePrime(bitLength, secRandom);
		    BigInteger p512 = BigInteger.probablePrime(bitLength, secRandom);

		    DHParameterSpec dhParams = new DHParameterSpec(p512, g512);
		    KeyPairGenerator keyGen;
			keyGen = KeyPairGenerator.getInstance("DH", "BC");
			keyGen.initialize(dhParams, new SecureRandom());
			KeyPair pair = keyGen.generateKeyPair();
			
			PrivateKey privateKey = pair.getPrivate();
			PublicKey pubKey = pair.getPublic();
			
			
			KeyRepo repo = new KeyRepo();
			
			repo.setMyPrivateKey(new String(Base64.encode(O9Cypher.serialize(privateKey))));
			repo.setMyPublicKey(new String(Base64.encode(O9Cypher.serialize(pubKey))));
			
			return repo;
			
		}  catch (Throwable e) {
			// TODO Auto-generated catch block
			android.util.Log.d(TAG, e.getMessage(), e);
			return null;
		} 

	   
	}
}
