package nakis.theodorou.storefiles.fragment;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.sql.Timestamp;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import nakis.theodorou.storefiles.R;
import nakis.theodorou.storefiles.library.DBAdapter;
import nakis.theodorou.storefiles.library.KryptoUtil;
import nakis.theodorou.storefiles.library.XmlSigner;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RequestFragment extends Fragment{

	 private DBAdapter db ;
	 private Spinner spinner1, spinner2;
	 private Button btnSubmit;
	 public String[] navMenuTitles,eggrafa,ypiresies;
     public CharSequence mTitle;
	 private static String reqStorageDirectory,keyStorageDirectory,infoStorageDirectory;
	 private static String KEY_FULLNAME = "fullname";
	 private static String KEY_EMAIL = "email";
	 private static String KEY_NO = "no";
	 private static String KEY_KEP = "kep";
	 private static String KEY_CERTNAME = "certname";
	 private static String KEY_TIMESTAMP = "timestamp";
	 private static String KEY_DIGEST = "digest";
	 private static String KEY_SIGNATURE = "signature";
	 private static String KEY_X509SUBJECTNAME = "X509SubjectName";
	 private static String KEY_X509CERTIFICATE = "X509Certificate";
	 private static String KEY_EGGRAFO = "eggrafo";
	 private static String KEY_YPIRESIA = "ypiresia";
	 public static String path;
	 static int num;
	 boolean verify;
	 static Context c;

	 
//	 UploadToServer ///////////
	 
	    TextView messageText;
	    Button uploadButton;
	    int serverResponseCode = 0;
	    ProgressDialog dialog = null;
	        
	    String upLoadServerUri = null;
	     
	    /**********  File Path *************/
	    final String uploadFilePath = reqStorageDirectory;
	    String uploadFileName = "request.sign";
//////////////////////////////////////////////////////////////////


	 // DER encoded ASN.1 identifier for SHA1 digest: "1.3.14.3.2.26".
//	 private static final byte[] DER_SHA1_DIGEST_IDENTIFIER = new byte[]{48, 33, 48, 9, 6, 5, 43, 14,3, 2, 26, 5, 0, 4, 20};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        eggrafa = getResources().getStringArray(R.array.list_egg);
        ypiresies = getResources().getStringArray(R.array.list_yp);
		c = getActivity().getApplicationContext();

        SharedPreferences prefs = getActivity().getSharedPreferences("X509Data", Context.MODE_PRIVATE);
		  KEY_X509SUBJECTNAME = prefs.getString("X509SubjectName", "fail");
		  KEY_X509CERTIFICATE = prefs.getString("X509Certificate", "fail");
		
		File dat = new File(Environment.getExternalStorageDirectory().toString()+"/storefiles/data");
		dat.mkdirs();
		keyStorageDirectory = dat.toString();
		
		File req = new File(Environment.getExternalStorageDirectory().toString()+"/storefiles/myfiles");
		req.mkdirs();
		reqStorageDirectory = req.toString();
		
		File info = new File(Environment.getExternalStorageDirectory().toString()+"/storefiles/info");
		info.mkdirs();
		infoStorageDirectory = info.toString();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		db = new DBAdapter(getActivity());
		 View v = inflater.inflate(R.layout.request_layout, container, false);

		   db.open();
			KEY_FULLNAME = db.getUser().get(0).toString();
			KEY_EMAIL = db.getUser().get(1).toString();
			uploadFileName = KEY_EMAIL;
		   db.close();
			addListenerOnSpinnerItemSelection(v);
			addListenerOnButton(v);

		
		return v;
	}

	// get the selected dropdown list value
	  private void addListenerOnButton(View v) {
	 
		spinner1 = (Spinner) v.findViewById(R.id.spinner1);
		spinner2 = (Spinner) v.findViewById(R.id.spinner2);
		btnSubmit = (Button) v.findViewById(R.id.buttonSend);
		messageText = (TextView) v.findViewById(R.id.textViewError);
	 
//        upLoadServerUri = "http://10.0.2.2:1234/storefiles/uploadXml.php";
        upLoadServerUri = "http://192.168.8.215:1234/storefiles/uploadXml.php";

		btnSubmit.setOnClickListener(new View.OnClickListener() {
	 
		  @Override
		  public void onClick(View v) {
			  
			  SharedPreferences plus = getActivity().getSharedPreferences("plus", Context.MODE_PRIVATE);
			  num = plus.getInt("plus", 0);
			  plus.edit().putInt("plus", ++num).commit();
			  java.util.Date date= new java.util.Date();
			  Timestamp created = new Timestamp(date.getTime());
			  KEY_TIMESTAMP = created.toString();
			  dialog = ProgressDialog.show(getActivity(), "", "Sending Request...", true);
			  WriteXMLFile();
			  verify = signXMLFile();
			  WriteSignXMLFile();
			  if(verify){
			  new Thread(new Runnable() {
				  public void run() {
					  getActivity().runOnUiThread(new Runnable() {
						  @Override
						  public void run() {
							  messageText.setText("sending request.....");
							  }
						  });
					String certpath = reqStorageDirectory + "/" + uploadFileName + KEY_NO+".nakis";
					KEY_CERTNAME = uploadFileName + KEY_NO+".nakis";
					int j = uploadFile(certpath); 
					  if(j==200){
						  db.open();
						  db.insertRequest(KEY_NO , KEY_EGGRAFO, KEY_YPIRESIA,KEY_TIMESTAMP,KEY_CERTNAME);
						  db.close();
						  File file = new File(certpath);
		    			  file.delete();
					  }
				  }
				  }).start();
			 
			  }

			  
			  }
		  });
		}


	  public void addListenerOnSpinnerItemSelection(View v) {
			spinner1 = (Spinner) v.findViewById(R.id.spinner1);
			spinner1.setOnItemSelectedListener(new CertOnItemSelectedListener());
			
			spinner2 = (Spinner) v.findViewById(R.id.spinner2);
			spinner2.setOnItemSelectedListener(new CityOnItemSelectedListener());
		  }
	
	  public class CertOnItemSelectedListener implements OnItemSelectedListener {
		  
		  public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
			  KEY_NO = Integer.valueOf(position).toString();
			  KEY_EGGRAFO = eggrafa[position];
			  }
		  @Override
		  public void onNothingSelected(AdapterView<?> arg0) {}
		  }
	
      public class CityOnItemSelectedListener implements OnItemSelectedListener {
		  
		  public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
			  KEY_KEP = Integer.valueOf(position).toString();
			  KEY_YPIRESIA = ypiresies[position];
			  }
		  @Override
		  public void onNothingSelected(AdapterView<?> arg0) {}
		  }

	  private static void WriteXMLFile() {
	    	Document doc=null;

	    	try {
	   		 
	   			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	   			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	   	 
	   			// root elements
	   			doc = docBuilder.newDocument();
	   			doc.setXmlStandalone(true);
	   			
	   			
	   			Element CertRequest = doc.createElement("CertRequest");
	   			doc.appendChild(CertRequest);
	   	 
	   			// fullname elements
	   			Element name = doc.createElement("FullName");
	   			name.appendChild(doc.createTextNode(KEY_FULLNAME));
	   			CertRequest.appendChild(name);
	   			
	   		// fullname elements
	   			Element email = doc.createElement("Email");
	   			email.appendChild(doc.createTextNode(KEY_EMAIL));
	   			CertRequest.appendChild(email);
	   	 
	   			// eggrafo elements
	   			Element actno = doc.createElement("ActNo");
	   			actno.appendChild(doc.createTextNode(KEY_NO));
	   			CertRequest.appendChild(actno);
	   			
	   		   // ypiresia elements
	   			Element city = doc.createElement("City");
	   			city.appendChild(doc.createTextNode(KEY_KEP));
	   			CertRequest.appendChild(city);
	   			
	   		   // created elements
	   			Element created = doc.createElement("CreatedAt");
	   			created.appendChild(doc.createTextNode(KEY_TIMESTAMP));
	   			CertRequest.appendChild(created);
	   	 
	   			// write the content into xml file
	   			TransformerFactory transformerFactory = TransformerFactory.newInstance();
	   			Transformer transformer = transformerFactory.newTransformer();
	   			DOMSource source = new DOMSource(doc);
	   			
	   			
	      	    File request = new File(reqStorageDirectory, "request.xml");
	   			StreamResult result = new StreamResult(request);
	   			 
	   			 transformer.transform(source, result);
	   			
	   			 System.out.println("File saved!");
	   			 } catch (ParserConfigurationException pce) {
	   				 pce.printStackTrace();
	   				 } catch (TransformerException tfe) {
	   					 tfe.printStackTrace();
	   					 }
		}

	  public static boolean signXMLFile() {
		  boolean verify=false;
			String xml,base64XmlDigest=null,base64RsaSignature=null;
			String originalXmlFilePath = reqStorageDirectory+"/request.xml";
//			String privateKeyFilePath = keyStorageDirectory + "/private.key";
//			String pubKeyFilePath = keyStorageDirectory + "/public.key";

			byte[] xmlBytes,signatureBytes;
			KryptoUtil util= new KryptoUtil();
			XmlSigner signer = new XmlSigner();
			PrivateKey privateKey;
			PublicKey pubkey;

//			Create the digest from original xmlfile
			
				try {
					privateKey = util.getStoredPrivateKey("private.key",c);
					pubkey = util.getStoredPublicKey("public.key",c);
					
					Document doc = signer.getXmlDocument(originalXmlFilePath);
				    xml = toString(doc);
					// get bytes from the XML
					xmlBytes = xml.getBytes("UTF-8");
					// calculate SHA1 digest from the XML bytes
					byte[] xmlDigestBytes = XmlSigner.sha1Digest(xmlBytes);
					base64XmlDigest = XmlSigner.base64encode(xmlDigestBytes, false);
					
					signatureBytes =  signData(xmlDigestBytes,privateKey);
					verify  = verifySig(xmlDigestBytes,pubkey,signatureBytes);
				    base64RsaSignature = XmlSigner.base64encode(signatureBytes, false);

					} catch (Exception e1) {
						e1.printStackTrace();
						}
////           Create the signature from signedInfo
//				PrivateKey privateKey;
//				PublicKey pubkey;
//				// initialize RSA cipher with the parameters from the private key
//			    Cipher cipher;
//				try {
////					privateKey = util.getStoredPrivateKeyinSD(privateKeyFilePath);
//					privateKey = util.getStoredPrivateKey("private.key",c);
////					pubkey = util.getStoredPublicKeyinSD(pubKeyFilePath);
//					pubkey = util.getStoredPublicKey("public.key",c);
//					cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
//				    cipher.init(Cipher.ENCRYPT_MODE, privateKey);
//				    Document doc2 = getSignedInfo(base64XmlDigest);
//				    doc2.getElementsByTagName("SingedInfo");
//				    // get string with SignedInfo
//				    String signedInfo = toString(doc2);
//				    String remove = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
//				    int length=remove.length();
//				    String signedInfofinal = signedInfo.substring(length, signedInfo.length());
//				    
////				    System.out.println(signedInfo2);
//
//				    // get the bytes from SignedInfo that will be signed
//				    byte[] signedInfoBytes = signedInfofinal.getBytes("UTF-8");
//				    //////////////////////////////////////////////////////////
//				    
//				    String b64;
//				    try {
//						signatureBytes =  signData(signedInfoBytes,privateKey);
////						b64 = XmlSigner.base64encode(sgndata, false);
//						verify  = verifySig(signedInfoBytes,pubkey,signatureBytes);
//					    base64RsaSignature = XmlSigner.base64encode(signatureBytes, false);
//
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
				    //////////////////////////////////////////////////////////
				    // calculate SHA1 digest of the signed info bytes
//				    byte[] signedInfoSha1Digest = XmlSigner.sha1Digest(signedInfoBytes);
//				    // encode the digest identifier and the SHA1 digest in DER format
//				    byte[] signedInfoDerSha1Digest = XmlSigner.mergeArrays(DER_SHA1_DIGEST_IDENTIFIER, signedInfoSha1Digest);
//				    // encrypt the DER encoded SHA1 digest of signed info
//				    signatureBytes = cipher.doFinal(signedInfoDerSha1Digest);
//				    // encode the signature bytes into base64 string
//				    		new String(Base64.encode(signatureBytes));
//				    		XmlSigner.base64encode(signatureBytes, true);
				    		
//				    		
//					
//					System.out.println(base64RsaSignature);
//
//
//				} catch (InvalidKeyException e) {
//						e.printStackTrace();
//				} catch (NoSuchAlgorithmException e) {
//					e.printStackTrace();
//				} catch (NoSuchPaddingException e) {
//					e.printStackTrace();
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
			 
				KEY_DIGEST = base64XmlDigest;
				KEY_SIGNATURE = base64RsaSignature;
				return verify;
		

		}

	  public static String toString(Document doc) {
		    try {
		        StringWriter sw = new StringWriter();
		        TransformerFactory tf = TransformerFactory.newInstance();
		        Transformer transformer = tf.newTransformer();
//		        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
//		        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//		        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//		        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		        transformer.transform(new DOMSource(doc), new StreamResult(sw));
		        return sw.toString();
		    } catch (Exception ex) {
		        throw new RuntimeException("Error converting to String", ex);
		    }
		}

	  public static Document getSignedInfo(String dgst){
				Document doc=null;
				try {
					DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
					// root elements
					doc = docBuilder.newDocument();
					doc.setXmlStandalone(true);
					
					Element SignedInfo = doc.createElement("SignedInfo");
		   			doc.appendChild(SignedInfo);
		   			
		   			Element CanonicalizationMethod  = doc.createElement("CanonicalizationMethod");
		   			SignedInfo.appendChild(CanonicalizationMethod);
		   			Attr attr1 = doc.createAttribute("Algorithm");
		   			attr1.setValue("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
		   			CanonicalizationMethod.setAttributeNode(attr1);
		   			
		   			Element SignatureMethod  = doc.createElement("SignatureMethod");
		   			SignedInfo.appendChild(SignatureMethod );
		   			Attr attr2 = doc.createAttribute("Algorithm");
		   			attr2.setValue("http://www.w3.org/2000/09/xmldsig#rsa-sha1");
		   			SignatureMethod.setAttributeNode(attr2);
		   			
		   			Element Reference  = doc.createElement("Reference");
		   			SignedInfo.appendChild(Reference );
		   			Attr attr3 = doc.createAttribute("URI");
		   			attr3.setValue("");
		   			Reference.setAttributeNode(attr3);
		   			
		   			Element Transforms = doc.createElement("Transforms");
		   			Reference.appendChild(Transforms);
		   			
		   			Element Transform = doc.createElement("Transform");
		   			Transforms.appendChild(Transform);
		   			Attr attr4 = doc.createAttribute("Algorithm");
		   			attr4.setValue("http://www.w3.org/2000/09/xmldsig#enveloped-signature");
		   			Transform.setAttributeNode(attr4);
		   			
		   			
		   			Element DigestMethod = doc.createElement("DigestMethod");
		   			Reference.appendChild(DigestMethod);
		   			Attr attr5 = doc.createAttribute("Algorithm");
		   			attr5.setValue("http://www.w3.org/2000/09/xmldsig#sha1");
		   			DigestMethod.setAttributeNode(attr5);
		   			
		   			
		   			Element DigestValue = doc.createElement("DigestValue");
		   			DigestValue.appendChild(doc.createTextNode(dgst));
		   			Reference.appendChild(DigestValue);
		   			
//		   			TransformerFactory transformerFactory = TransformerFactory.newInstance();
//		   			Transformer transformer = transformerFactory.newTransformer();
//		   			DOMSource source = new DOMSource(doc);
//		   			
//		      	    File request = new File(infoStorageDirectory, "keyinfo");
//		   			StreamResult result = new StreamResult(request);
//		   			
//		   			// Output to console for testing   			 
//		   			 transformer.transform(source, result);
		   			 }catch (ParserConfigurationException pce) {
		   				 pce.printStackTrace();
		   				 } 
//		   				 catch (TransformerException tfe) {
//		   					 tfe.printStackTrace();
//		   					 }
				return doc;
				}
			
	  public static byte[] signData(byte[] data, PrivateKey key) throws Exception {
		  Signature signer = Signature.getInstance("SHA1withRSA");
		  signer.initSign(key);
		  signer.update(data);
		  return (signer.sign());
		  }
	  
	  public static boolean verifySig(byte[] data, PublicKey key, byte[] sig) throws Exception {
		  Signature signer = Signature.getInstance("SHA1withRSA");
		  signer.initVerify(key);
		  signer.update(data);
		  return (signer.verify(sig));
		  }
	  
	  private static void WriteSignXMLFile() {
			Document doc=null;
	    	

		   	 try {
			   DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root elements
				doc = docBuilder.newDocument();
				doc.setXmlStandalone(true);
				
				
				Element CertRequest = doc.createElement("CertRequest");
				doc.appendChild(CertRequest);
				
				// fullname elements
	   			Element name = doc.createElement("FullName");
	   			name.appendChild(doc.createTextNode(KEY_FULLNAME));
	   			CertRequest.appendChild(name);
	   			
	   		  // email elements
	   			Element email = doc.createElement("Email");
	   			email.appendChild(doc.createTextNode(KEY_EMAIL));
	   			CertRequest.appendChild(email);
	   	 
	   			// eggrafo elements
	   			Element actno = doc.createElement("ActNo");
	   			actno.appendChild(doc.createTextNode(KEY_NO));
	   			CertRequest.appendChild(actno);
	   			
	   		// ypiresia elements
	   			Element city = doc.createElement("City");
	   			city.appendChild(doc.createTextNode(KEY_KEP));
	   			CertRequest.appendChild(city);
	   			
	   		// created elements
	   			Element created = doc.createElement("CreatedAt");
	   			created.appendChild(doc.createTextNode(KEY_TIMESTAMP));
	   			CertRequest.appendChild(created);
	   			
	   			Element Signature = doc.createElement("Signature");
	   			CertRequest.appendChild(Signature);
	   			
	   			Attr attr = doc.createAttribute("xmlns");
	   			attr.setValue("http://www.w3.org/2000/09/xmldsig#");
	   			Signature.setAttributeNode(attr);
	   			
	   			Element SignedInfo = doc.createElement("SignedInfo");
	   			Signature.appendChild(SignedInfo);
	   			
	   			Element CanonicalizationMethod  = doc.createElement("CanonicalizationMethod");
	   			SignedInfo.appendChild(CanonicalizationMethod);
	   			Attr attr1 = doc.createAttribute("Algorithm");
	   			attr1.setValue("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
	   			CanonicalizationMethod.setAttributeNode(attr1);
	   			
	   			Element SignatureMethod  = doc.createElement("SignatureMethod");
	   			SignedInfo.appendChild(SignatureMethod );
	   			Attr attr2 = doc.createAttribute("Algorithm");
	   			attr2.setValue("http://www.w3.org/2000/09/xmldsig#rsa-sha1");
	   			SignatureMethod.setAttributeNode(attr2);
	   			
	   			Element Reference  = doc.createElement("Reference");
	   			SignedInfo.appendChild(Reference );
	   			Attr attr3 = doc.createAttribute("URI");
	   			attr3.setValue("");
	   			Reference.setAttributeNode(attr3);
	   			
	   			Element Transforms = doc.createElement("Transforms");
	   			Reference.appendChild(Transforms);
	   			
	   			Element Transform = doc.createElement("Transform");
	   			Transforms.appendChild(Transform);
	   			Attr attr4 = doc.createAttribute("Algorithm");
	   			attr4.setValue("http://www.w3.org/2000/09/xmldsig#enveloped-signature");
	   			Transform.setAttributeNode(attr4);
	   			
	   			
	   			Element DigestMethod = doc.createElement("DigestMethod");
	   			Reference.appendChild(DigestMethod);
	   			Attr attr5 = doc.createAttribute("Algorithm");
	   			attr5.setValue("http://www.w3.org/2000/09/xmldsig#sha1");
	   			DigestMethod.setAttributeNode(attr5);
	   			
	   			
	   			Element DigestValue = doc.createElement("DigestValue");
	   			DigestValue.appendChild(doc.createTextNode(KEY_DIGEST));
	   			Reference.appendChild(DigestValue);
	   			
	   			Element SignatureValue = doc.createElement("SignatureValue");
	   			SignatureValue.appendChild(doc.createTextNode(KEY_SIGNATURE));
	   			Signature.appendChild(SignatureValue);
	   			
	   			Element KeyInfo = doc.createElement("KeyInfo");
	   			Signature.appendChild(KeyInfo);
	   			
	   			Element X509Data = doc.createElement("X509Data");
	   			KeyInfo.appendChild(X509Data);
	   			
	   			Element X509SubjectName = doc.createElement("X509SubjectName");
	   			X509SubjectName.appendChild(doc.createTextNode(KEY_X509SUBJECTNAME));
	   			X509Data.appendChild(X509SubjectName);
	   			
	   			Element X509Certificate = doc.createElement("X509Certificate");
	   			X509Certificate.appendChild(doc.createTextNode(KEY_X509CERTIFICATE));
	   			X509Data.appendChild(X509Certificate);
	   			

	   			TransformerFactory transformerFactory = TransformerFactory.newInstance();
	   			Transformer transformer = transformerFactory.newTransformer();
	   			DOMSource source = new DOMSource(doc);
	   			
	      	    File request = new File(reqStorageDirectory, KEY_EMAIL+KEY_NO+".nakis");
	      	    path = reqStorageDirectory+"/"+KEY_EMAIL+Integer.valueOf(num).toString()+".nakis";
	   			StreamResult result = new StreamResult(request);
	   			transformer.transform(source, result);
	   			System.out.println("File saved!");
	   			 } catch (ParserConfigurationException pce) {
	   				 pce.printStackTrace();
	   				 } catch (TransformerException tfe) {
	   					 tfe.printStackTrace();
	   					 }
		   	 }
 
	  public void setTitle(CharSequence title) {
	        mTitle = title;
	        getActivity().setTitle(mTitle);
	    }

	  private int uploadFile(String sourceFileUri) {
          
          
          String fileName = sourceFileUri;
  
          HttpURLConnection conn = null;
          DataOutputStream dos = null; 
          String lineEnd = "\r\n";
          String twoHyphens = "--";
          String boundary = "*****";
          int bytesRead, bytesAvailable, bufferSize;
          byte[] buffer;
          int maxBufferSize = 1 * 1024 * 1024;
          File sourceFile = new File(sourceFileUri);
           
          if (!sourceFile.isFile()) {
               
               dialog.dismiss();
                
               Log.e("uploadFile", "Source File not exist :"
                                   +reqStorageDirectory + "/" + uploadFileName + Integer.valueOf(num).toString()+".nakis");
                
               getActivity().runOnUiThread(new Runnable() {
                   public void run() {
                       messageText.setText("Source File not exist :" +reqStorageDirectory + "/" + uploadFileName + Integer.valueOf(num).toString()+".nakis");
                   }
               });
                
               return 0;
            
          }
          else
          {
        	  
               try {
                    
                     // open a URL connection to the Servlet
                   FileInputStream fileInputStream = new FileInputStream(sourceFile);
                   URL url = new URL(upLoadServerUri);
                    
                   // Open a HTTP  connection to  the URL
                   conn = (HttpURLConnection) url.openConnection();
                   conn.setDoInput(true); // Allow Inputs
                   conn.setDoOutput(true); // Allow Outputs
                   conn.setUseCaches(false); // Don't use a Cached Copy
                   conn.setRequestMethod("POST");
                   conn.setRequestProperty("Connection", "Keep-Alive");
                   conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                   conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                   conn.setRequestProperty("uploaded_file", fileName);
                   conn.setRequestProperty("user", KEY_EMAIL);

                    
                   dos = new DataOutputStream(conn.getOutputStream());
          
                   dos.writeBytes(twoHyphens + boundary + lineEnd);
                   dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename="+ fileName + lineEnd);

                   dos.writeBytes(lineEnd);
          
                   // create a buffer of  maximum size
                   bytesAvailable = fileInputStream.available();
          
                   bufferSize = Math.min(bytesAvailable, maxBufferSize);
                   buffer = new byte[bufferSize];
          
                   // read file and write it into form...
                   bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
                      
                   while (bytesRead > 0) {
                        
                     dos.write(buffer, 0, bufferSize);
                     bytesAvailable = fileInputStream.available();
                     bufferSize = Math.min(bytesAvailable, maxBufferSize);
                     bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
                      
                    }
          
                   // send multipart form data necesssary after file data...
                   dos.writeBytes(lineEnd);
                   dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
          
                   // Responses from the server (code and message)
                   serverResponseCode = conn.getResponseCode();
                   String serverResponseMessage = conn.getResponseMessage();
                     
                   Log.i("uploadFile", "HTTP Response is : "
                           + serverResponseMessage + ": " + serverResponseCode);
                    
                   if(serverResponseCode == 200){
                        
                       getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                 
                                String msg = "Η Αίτηση στάλθηκε επιτυχώς";
                                 
                                messageText.setText(msg);

                            }
                        });               
                   }   
                    
                   //close the streams //
                   fileInputStream.close();
                   dos.flush();
                   dos.close();
                     
              } catch (MalformedURLException ex) {
                   
                  dialog.dismiss(); 
                  ex.printStackTrace();
                   
                  getActivity().runOnUiThread(new Runnable() {
                      public void run() {
                          messageText.setText("MalformedURLException Exception : check script url.");
                          Toast.makeText(getActivity(), "MalformedURLException",
                                                              Toast.LENGTH_SHORT).show();
                      }
                  });
                   
                  Log.e("Upload file to server", "error: " + ex.getMessage(), ex); 
              } catch (Exception e) {
                   
                  dialog.dismiss(); 
                  e.printStackTrace();
                   
                  getActivity().runOnUiThread(new Runnable() {
                      public void run() {
                          messageText.setText("Got Exception : see logcat ");
                          Toast.makeText(getActivity(), "Got Exception : see logcat ",
                                  Toast.LENGTH_SHORT).show();
                      }
                  });
                  Log.e("Upload file to server Exception", "Exception : "
                                                   + e.getMessage(), e); 
              }
              dialog.dismiss();      
              return serverResponseCode;
               
           } // End else block
         } 
	  
}

