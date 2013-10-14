package core.september.textmesecure.algo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
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

public class Cypher {
	static {
		Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
	}

	public static byte[] cipherData(PaddedBufferedBlockCipher cipher, byte[] data)
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

	public  byte[] decrypt(byte[] cipher, byte[] key)
			throws Exception {

		PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(
				new CBCBlockCipher(new AESEngine()));

		// System.out.println("Block size:"+aes.getBlockSize());

		//iv = new byte[aes.getBlockSize()];
		//(new SecureRandom()).nextBytes(iv);

		CipherParameters ivAndKey = new ParametersWithRandom(new KeyParameter(key));

		aes.init(false, ivAndKey);

		return cipherData(aes, cipher);
	}

	public byte[] encrypt(byte[] plain, byte[] key) throws Exception {


		PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(
				new CBCBlockCipher(

						new AESEngine()));

		// iv = new byte[aes.getBlockSize()];
		//(new SecureRandom()).nextBytes(iv);

		CipherParameters ivAndKey = new ParametersWithRandom(new KeyParameter(key));

		aes.init(true, ivAndKey);

		return cipherData(aes, plain);
	}

	public  byte[] encryptRSA(Key pubkey, byte[] data) {
		try {
			Cipher rsa;
			rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.ENCRYPT_MODE, pubkey);
			return rsa.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public  byte[] decryptRSA(Key decryptionKey, byte[] buffer) {
		try {
			Cipher rsa;
			rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.DECRYPT_MODE, decryptionKey);
			byte[] utf8 = rsa.doFinal(buffer);
			return utf8;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(b);
		o.writeObject(obj);
		return b.toByteArray();
	}

	public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}

	public static void main1(String[] args) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] text = "I'm a super secret text After that, I get the InvalidKeyException: Key length not 128/192/256 bits.But as you can see, the SecretKey has a length of 128 Bits!".getBytes("UTF-8");
        byte[] key = new byte[32];
        byte[] iv = new byte[16];
        secureRandom.nextBytes(key);
        secureRandom.nextBytes(iv);
       
        Cypher crypt = new Cypher();
        byte[] cipher = crypt.encrypt(text, key);
        
        
//        byte[] decrypted = crypt.decrypt(cipher, key, iv);
//       
//        System.out.println(new String(cipher));
//        System.out.println(new String(decrypted));
       
        /////////////////-----> key pair generator
       
//        RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
//       
//        generator.init(new RSAKeyGenerationParameters
//            (
//                BigInteger.probablePrime(2048, new Random()),//publicExponent
//                SecureRandom.getInstance("SHA1PRNG"),//prng
//                2048,//strength
//                80//certainty
//            ));
//
//        AsymmetricCipherKeyPair keyPair = generator.generateKeyPair();
//        System.out.println(keyPair.getPrivate());
//        System.out.println(keyPair.getPublic());
       
       
        Security.addProvider(new BouncyCastleProvider());
         KeyPairGenerator    kpg = KeyPairGenerator.getInstance("RSA", "BC");
        kpg.initialize(2048);
        KeyPair  kp = kpg.generateKeyPair();
       
        //System.out.println(new String(crypt.serialize(kp.getPublic())));
       
       // byte[] cryptedKey = crypt.encryptRSA(kp.getPublic(), key);
       // byte[] cryptedIv = crypt.encryptRSA(kp.getPublic(), iv);
       
       
       
        String cryptedKeyPublic = new String(
                Base64.encode(crypt.serialize(kp.getPublic()))
                );
       
        System.out.println(cryptedKeyPublic);
       
        PublicKey pk = (PublicKey) crypt.deserialize(Base64.decode(cryptedKeyPublic));
       
        byte[] cryptedKey = crypt.encryptRSA(pk, key);
//        byte[] cryptedKey = crypt.encryptRSA((PublicKey)crypt.deserialize(crypt.serialize(kp.getPublic())), key);
        byte[] cryptedIv = crypt.encryptRSA((PublicKey)crypt.deserialize(crypt.serialize(kp.getPublic())), iv);
       
       
       
       
        byte[] decryptedKey = crypt.decryptRSA(kp.getPrivate(), cryptedKey);
        byte[] decryptedIv = crypt.decryptRSA(kp.getPrivate(), cryptedIv);
       
        byte[] decryptedMessage = crypt.decrypt(cipher,decryptedKey);
       
        System.out.println(new String(decryptedMessage));
       
       
       
        int bitLength = 256;
        SecureRandom rnd = new SecureRandom();
        BigInteger g512 = BigInteger.probablePrime(bitLength, rnd);
        BigInteger p512 = BigInteger.probablePrime(bitLength, rnd);
       
        System.out.println(g512);
        System.out.println(p512);

        DHParameterSpec dhParams = new DHParameterSpec(p512, g512);
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");

        keyGen.initialize(dhParams, new SecureRandom());

        KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
        KeyPair aPair = keyGen.generateKeyPair();
        KeyAgreement bKeyAgree = KeyAgreement.getInstance("DH", "BC");
        KeyPair bPair = keyGen.generateKeyPair();

        aKeyAgree.init(aPair.getPrivate());
        bKeyAgree.init(bPair.getPrivate());

        aKeyAgree.doPhase(bPair.getPublic(), true);
        bKeyAgree.doPhase(aPair.getPublic(), true);

        MessageDigest hash = MessageDigest.getInstance("SHA1", "BC");
        System.out.println(new String(hash.digest(aKeyAgree.generateSecret())));
        System.out.println(new String(hash.digest(bKeyAgree.generateSecret())));
       
        System.out.println(aKeyAgree.generateSecret().length);
        System.out.println(bKeyAgree.generateSecret().length);
       
        System.out.println("Starting AES");
       
          //byte[] text = "I'm a super secret text After that, I get the InvalidKeyException: Key length not 128/192/256 bits.But as you can see, the SecretKey has a length of 128 Bits!".getBytes("UTF-8");
          //byte[] key = aKeyAgree.generateSecret();
          //byte[] iv = new byte[aKeyAgree.generateSecret().length/2];
       
          //secureRandom.nextBytes(iv);
         
          //Crypt crypt = new Crypt();
          //byte[] cipher = crypt.encrypt(text, key);
          byte[] decipher = crypt.decrypt(cipher, bKeyAgree.generateSecret());
         
          System.out.println(new String(decipher));

       
        //BigInteger bi = new BigInteger("-100");
       // System.out.println(BigInteger.probablePrime(2048, new Random()));
    }
	
	public static void main(String[] args) {
		System.out.println("main");
	}

}
