package nakis.theodorou.storefiles.library;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import android.content.Context;
import android.content.SharedPreferences;


public class KryptoUtil {

    /**
     * Name of the algorithm
     */
    private static final String ALGORITHM = "RSA";

    /**
     * This method is used to generate key pair based upon the provided
     * algorithm
     *
     * @return KeyPair
     */
    private KeyPair generateKeyPairs() {
        KeyPair keyPair = null;
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(2048);
            keyPair = keyGen.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyPair;
    }

    /**
     * Method used to store Private and Public Keys inside a directory
     *
     * @param dirPath to store the keys
     */
    public void storeKeyPairs(String dirPath) {
        KeyPair keyPair = generateKeyPairs();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        storeKeysPublictoSD(dirPath + "publickey.key", publicKey);
        storeKeysPrivatetoSD(dirPath + "privatekey.key", privateKey);
    }

    /**
     * Method used to store the PublicKey 
     *
     * @param filePath , name of the file
     * @param key
     */
    public void storeKeysPublictoSD(String filePath, PublicKey key) {
        byte[] keyBytes = key.getEncoded();
        OutputStream outStream = null;
        
        try {
            outStream = new FileOutputStream(filePath);
            outStream.write(keyBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void storeKeyPublic(String filename ,PublicKey key, Context context){
    	byte[] keyBytes = key.getEncoded();
        SharedPreferences datalength = context.getSharedPreferences("length", Context.MODE_PRIVATE);
        datalength.edit().putInt("public.key", keyBytes.length).commit();
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);

    	FileOutputStream output;
    	try{
    		output = context.openFileOutput(filename, Context.MODE_PRIVATE);
            output.write(x509EncodedKeySpec.getEncoded());
            output.close();
            }catch(Exception e){
            	
            }
    	
    }
    /**
     * Method used to store the PrivateKey
     *
     * @param filePath , name of the file
     * @param key
     */
    public void storeKeysPrivatetoSD(String filePath, PrivateKey key) {
        byte[] keyBytes = key.getEncoded();
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(filePath);
            outStream.write(keyBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
    public void storeKeyPrivate(String filename ,PrivateKey key,Context context){
    	byte[] keyBytes = key.getEncoded();
    	SharedPreferences datalength = context.getSharedPreferences("length", Context.MODE_PRIVATE);
        datalength.edit().putInt("private.key", keyBytes.length).commit();
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
    	FileOutputStream output;
    	try{
    		output = context.openFileOutput(filename, Context.MODE_PRIVATE);
            output.write(pkcs8EncodedKeySpec.getEncoded());
            output.close();
            }catch(Exception e){
            	
            }
    }
    /**
     * Method used to retrieve the keys in the form byte array
     *
     * @param filePath of the key
     * @return byte array
     */
    private byte[] getKeyData(String filePath) {
        File file = new File(filePath);
        byte[] buffer = new byte[(int) file.length()];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer;
    }
    /**
     * Method used to retrieve the keys in the form byte array
     */
    private byte[] getByteData(String filename,Context context){
    	SharedPreferences datalength = context.getSharedPreferences("length", Context.MODE_PRIVATE);
    	int l = datalength.getInt(filename, 20);

		byte[] buffer = new byte[l];
		FileInputStream fis;
    	try{
    		fis = context.openFileInput(filename);
    		fis.read(buffer);
    		fis.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return buffer;
    }

    /**
     * Method used to get the Private Key
     *
     * @param filePath of the PrivateKey file
     * @return PrivateKey
     */
    public PrivateKey getStoredPrivateKeyinSD(String filePath) {
        PrivateKey privateKey = null;
        byte[] keydata = getKeyData(filePath);
        PKCS8EncodedKeySpec encodedPrivateKey = new PKCS8EncodedKeySpec(keydata);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(encodedPrivateKey);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public PrivateKey getStoredPrivateKey(String filename,Context c) {
        PrivateKey privateKey = null;
        byte[] keydata = getByteData(filename,c);
        PKCS8EncodedKeySpec encodedPrivateKey = new PKCS8EncodedKeySpec(keydata);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(encodedPrivateKey);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }
    
    /**
     * Method used to get the Public Key from Storage Path
     *
     * @param filePath of the PublicKey file
     * @return PublicKey
     */
    public PublicKey getStoredPublicKeyinSD(String filePath) {
        PublicKey publicKey = null;
        byte[] keydata = getKeyData(filePath);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        X509EncodedKeySpec encodedPublicKey = new X509EncodedKeySpec(keydata);
        try {
            publicKey = keyFactory.generatePublic(encodedPublicKey);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public PublicKey getStoredPublicKey(String filename,Context c) {
        PublicKey publicKey = null;
        byte[] keydata = getByteData(filename,c);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        X509EncodedKeySpec encodedPublicKey = new X509EncodedKeySpec(keydata);
        try {
            publicKey = keyFactory.generatePublic(encodedPublicKey);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }
}
