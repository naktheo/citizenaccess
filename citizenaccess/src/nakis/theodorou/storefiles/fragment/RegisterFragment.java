package nakis.theodorou.storefiles.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Date;

import javax.security.auth.x500.X500Principal;

import nakis.theodorou.storefiles.R;
import nakis.theodorou.storefiles.library.DBAdapter;
import nakis.theodorou.storefiles.library.KryptoUtil;

import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterFragment extends Fragment{

	Button register;
    EditText inputName,inputLastName,inputEmail,inputPassword;

    private static final String BC = org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

    TextView registerErrorMsg;
//    private AsyncTask<String, String, String> asyncTask;
//   	private String response;
//	private String registerResponse="fail" ,uploadResponse="fail";
    JSONObject json=null;
//    private static String KEY_SUCCESS = "success",KEY_ERROR = "error";
    DBAdapter db;
    private static String KEY_NAME,KEY_LASTNAME,KEY_EMAIL,KEY_PASS,KEY_FULLNAME,extStorageDirectory;
    File keystore;
    public static String[] X509 = new String[3];
    static Context c;
    int serverResponseCode = 0;
    ProgressDialog dialog = null,dialog2 = null,dialog3 = null;
        
    String upLoadServerUri ="http://192.168.8.215:1234/storefiles/uploadPublicKey.php";
//    String upLoadServerUri ="http://10.0.2.2:1234/storefiles/uploadPublicKey.php";

    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 c = getActivity().getApplicationContext();
		 File folder = new File(Environment.getExternalStorageDirectory().toString()+"/storefiles/data");
		 folder.mkdirs();
		 extStorageDirectory = folder.toString();
		   X509[0] = "fail";
	       X509[1] = "fail";
	       X509[2] = "fail";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		 View v = inflater.inflate(R.layout.register_fragment, container, false);
		 inputName = (EditText) v.findViewById(R.id.editTextregname);
		 inputLastName = (EditText) v.findViewById(R.id.editTextreglastname);

		 inputEmail = (EditText) v.findViewById(R.id.editTextregmail);
		 inputPassword = (EditText) v.findViewById(R.id.editTextregpass);
		 register = (Button) v.findViewById(R.id.buttonreg);
		 registerErrorMsg = (TextView) v.findViewById(R.id.textViewrerror);

		 register.setOnClickListener(new View.OnClickListener() {        
	            public void onClick(View view) {
	            	db = new DBAdapter(getActivity());
	  			    
	  			KEY_NAME = inputName.getText().toString();
	  			KEY_LASTNAME = inputLastName.getText().toString();
	  			KEY_EMAIL = inputEmail.getText().toString();
	  			KEY_PASS = inputPassword.getText().toString();
	  			KEY_FULLNAME  = KEY_NAME+"  "+KEY_LASTNAME;
				  
	  			SharedPreferences regdata = getActivity().getSharedPreferences("RegisterData", Context.MODE_PRIVATE);
				  regdata.edit().putString("fullname", KEY_FULLNAME).commit();
				  regdata.edit().putString("email", KEY_EMAIL).commit();
				  regdata.edit().putString("pass", KEY_PASS).commit();



	  			dialog = ProgressDialog.show(getActivity(), KEY_FULLNAME,"Δημιουργεία Κλειδιών Ασφαλείας. Παρακαλώ Περιμένετε...", true);
//	  			Create self-sign Certificate and KeyPair
	  			new Thread(new Runnable() {
		  			  @Override
		  			  public void run()
		  			  {	
		  				  File keystore = new File(extStorageDirectory, "truststore");
		  				  X509 = generateSelfSignedCertificate("localhost", keystore, KEY_EMAIL ,KEY_PASS , "CN = "+KEY_FULLNAME);
		  				  SharedPreferences prefs = getActivity().getSharedPreferences("X509Data", Context.MODE_PRIVATE);
			  				  prefs.edit().putString("X509SubjectName", X509[1]).commit();
			  				  prefs.edit().putString("X509Certificate", X509[0]).commit();
			  				  prefs.edit().putString("PublicKey", X509[2]).commit();
			  				getActivity().runOnUiThread(new Runnable() {
		  			  	   		 @Override
		  			  	   		 public void run()
		  			  	   		 {
		  			  	   			 dialog.dismiss();
		  			  	   			 if(X509[0] != "fail"){
		  			  	   				 Log.d("success", "Keypair generated successfull");
		  			  	   				 Fragment fragment = new RegisterWriteDB();
		  			  	   				 FragmentManager fragmentManager = getFragmentManager();
		  			  	   				 fragmentManager.beginTransaction().replace(R.id.frame_login_register, fragment).commit();
		  			  	   			 }else{
		  			  	   				 Intent intent = getActivity().getIntent();
		  			  	   				 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		  			  	   				 getActivity().overridePendingTransition(0, 0);
		  			  	   				 getActivity().finish();
		  			  	   				 getActivity().overridePendingTransition(0, 0);
		  			  	   				 startActivity(intent);	
		  			  	   			 }
		  			  	   			 }
		  			  	   		 });
			  				  }
		  			  }).start();
	  			}
	            });
		 return v;
	}
	
	private static String[] generateSelfSignedCertificate(String alias, File keystore,String mail,
	        String keystorePassword, String info) {
		
		String[] X509 = new String[3];
		   

	try {
		
		    KryptoUtil util = new KryptoUtil();
	        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

	        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
	        kpGen.initialize(2048, new SecureRandom());
	        KeyPair pair = kpGen.generateKeyPair();
	        PrivateKey privateKey = pair.getPrivate();
	        PublicKey publicKey = pair.getPublic();
	        KeyPair pai2r = new KeyPair(publicKey,privateKey);

	        util.storeKeysPublictoSD(extStorageDirectory + "/"+mail+".key", publicKey);
	        util.storeKeyPublic("public.key",publicKey,c);
//	        util.storeKeysPrivatetoSD(extStorageDirectory+ "/private.key", privateKey);
	        util.storeKeyPrivate("private.key",privateKey,c);


//	        SaveKeyPair(extStorageDirectory, pair);

	       

	        // Generate self-signed certificate
	        X500Principal principal = new X500Principal(info);

	        Date notBefore = new Date(System.currentTimeMillis());
	        Date notAfter = new Date((long) (System.currentTimeMillis() +(long) 365*24*60*60*1000));
	        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

	        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(principal, serial,
	                        notBefore, notAfter, principal, publicKey);
	        ContentSigner sigGen = new JcaContentSignerBuilder("SHA1WithRSAEncryption")
	                        .setProvider(BC).build(privateKey);
	        X509Certificate cert = new JcaX509CertificateConverter().setProvider(BC)
	                        .getCertificate(certGen.build(sigGen));
	        cert.checkValidity();
	        cert.verify(cert.getPublicKey());
	        

	        // Save to keystore
	        KeyStore store = KeyStore.getInstance("BKS");
	        if (keystore.exists()) {
	                FileInputStream fis = new FileInputStream(keystore);
	                store.load(fis, keystorePassword.toCharArray());
	                fis.close();
	        } else {
	                store.load(null);
	        }
	        store.setKeyEntry(alias, privateKey, keystorePassword.toCharArray(),
	                        new java.security.cert.Certificate[] { cert });
	     
	        FileOutputStream fos = new FileOutputStream(keystore);
	        store.store(fos, keystorePassword.toCharArray());
	        
	        fos.close();

	        System.out.print(cert.toString());
	            String X509Certificate = convertToPem(cert);
	            System.out.println(X509Certificate);
	            String X509SubjectName = cert.getIssuerDN().toString();
	            
	            X509[0] = X509Certificate;
	            X509[1] = X509SubjectName;
	            X509[2] = publicKey.toString();
	       System.out.println(X509[1]);

	       
	} catch (Throwable t) {
		   
	       t.printStackTrace();
	       throw new RuntimeException("Failed to generate self-signed certificate!", t);
	}
	return X509;
	}
	
	protected static String convertToPem(X509Certificate cert) throws CertificateEncodingException {
		 byte[] derCert = cert.getEncoded();
		 String pemCertPre = new String(Base64.encode(derCert));
		 return pemCertPre;
		}

	public static void SaveKeyPair(String path, KeyPair keyPair) throws IOException {
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
		FileOutputStream fos = new FileOutputStream(path + "/public.key");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();
 
		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
		fos = new FileOutputStream(path + "/private.key");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
		}


}
