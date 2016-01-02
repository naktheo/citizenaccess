//package nakis.theodorou.storefiles.library;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.ObjectOutput;
//import java.io.ObjectOutputStream;
//import java.math.BigInteger;
//import java.security.KeyPair;
//import java.security.KeyStore;
//import java.security.cert.X509Certificate;
//import java.sql.Date;
//
//import javax.security.auth.x500.X500Principal;
//
//import org.bouncycastle.cert.X509v3CertificateBuilder;
//import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
//import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
//import org.bouncycastle.operator.ContentSigner;
//import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
//import org.bouncycastle.util.encoders.Base64;
//
//public class GenerateX509Certificate {
//    private static final String BC = org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;
//
//	public  GenerateX509Certificate(){
//
//	}
//	
//	public String[] getX509Data(KeyPair pair ,String info , File keystore ,String keystorePassword ){
//		String[] X509 = new String[2];
//		try{
//			// Generate self-signed certificate
//		    X500Principal principal = new X500Principal(info);
//
//		    Date notBefore = new Date(System.currentTimeMillis());
//		    Date notAfter = new Date((long) (System.currentTimeMillis() +(long) 365*24*60*60*1000));
//		    BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
//
//		    X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(principal, serial,
//		                    notBefore, notAfter, principal, pair.getPublic());
//		    ContentSigner sigGen = new JcaContentSignerBuilder("SHA1WithRSAEncryption")
//		                    .setProvider(BC).build(pair.getPrivate());
//		    X509Certificate cert = new JcaX509CertificateConverter().setProvider(BC)
//		                    .getCertificate(certGen.build(sigGen));
//		    cert.checkValidity();
//		    cert.verify(cert.getPublicKey());
//		   
//		    byte[] derCert = cert.getEncoded();
//    
//		    X509[0] = new String(Base64.encode(derCert));
//		    X509[1] = cert.getIssuerDN().getName();
//		    
//			}catch(Exception e){
//				X509[0]="fail";
//				X509[1]="fail";
//				System.out.println(e);
//				
//
//			}
//		return X509;
//	}
//}
