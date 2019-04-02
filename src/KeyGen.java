import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.SignatureSpi;
import java.util.Base64.Encoder;
import java.util.Base64;

public class KeyGen{

	private KeyPairGenerator keyGen;
	private KeyPair pair;
	private PrivateKey privateKey;
	private PublicKey publicKey;

	public KeyGen(){	
	
	}
	
	public byte[] sign(PrivateKey privKey, String nonce) throws InvalidKeyException, Exception{
		
		Signature rsa = Signature.getInstance("MD5WithRSA");
		rsa.initSign((PrivateKey) privKey);
		rsa.update(nonce.getBytes("UTF-8"));	
		
		return rsa.sign();
	}
	
	public boolean verifySign(String nonce, byte[] signature, Key publicKey) throws Exception{
		
		Signature rsa = Signature.getInstance("MD5WithRSA");
		rsa.initVerify((PublicKey) publicKey);
		rsa.update(nonce.getBytes());
		
		return rsa.verify(signature);
	}

	public void writeToFile(String path, PublicKey key) throws IOException {

		File outFile = new File(path);

		FileOutputStream out1 = new FileOutputStream(outFile + "public.key");
		out1.write(publicKey.getEncoded());
		out1.close();
		
		FileOutputStream out2 = new FileOutputStream(outFile + "private.key");
		out2.write(privateKey.getEncoded());
		out2.close();
	}
	
}