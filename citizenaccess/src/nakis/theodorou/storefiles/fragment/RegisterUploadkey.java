package nakis.theodorou.storefiles.fragment;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import nakis.theodorou.storefiles.R;
import nakis.theodorou.storefiles.library.DBAdapter;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class RegisterUploadkey extends Fragment {

	private String fullname, email, extStorageDirectory;
	ProgressDialog dialog;
//    private static String upLoadServerUri ="http://10.0.2.2:1234/storefiles/uploadPublicKey.php";
    private static String upLoadServerUri ="http://192.168.8.215:1234/storefiles/uploadPublicKey.php";

    int serverResponseCode = 0;
    boolean isOk;
    DBAdapter db;



	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences regdata = getActivity().getSharedPreferences("RegisterData", Context.MODE_PRIVATE);
		 fullname = regdata.getString("fullname", "fail");
		 email = regdata.getString("email", "fail");
		 
		 File folder = new File(Environment.getExternalStorageDirectory().toString()+"/storefiles/data");
		 folder.mkdirs();
		 extStorageDirectory = folder.toString();

		 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
     	 db = new DBAdapter(getActivity());
     	 View v = inflater.inflate(R.layout.blank, container, false);

		 dialog = ProgressDialog.show(getActivity(), fullname,"Γίνεται αποστολή του δημόσιου κλειδιού σας." +
		 		"Παρακαλώ Περιμένετε...", true);
//		 	  			     Send the public Key
		 	  			   
		 	  			    		new Thread(new Runnable() {
		 		  			  			  @Override
		 		  			  			  public void run()
		 		  			  			  {	
		 		  			  				  String publickeypath = extStorageDirectory + "/" +email+".key";
		 		  			  				 isOk =  uploadPublic(publickeypath);
		 		  			  		   	 getActivity().runOnUiThread(new Runnable() {
		 		  			  	   		 @Override
		 		  			  	   		 public void run()
		 		  			  	   		 {
		 		  			  	   			 dialog.dismiss();
		 		  			  	   			 if(isOk){
		 		  			  	   				 db.open();
		 		  			  	   				 db.insertUser(fullname,email);
		 		  			  	   				 db.close();
		 		  			  	   				 Log.d("success","O χρήστης " + fullname + " με email: "+email+" έστειλε αίτηση εγγραφής.");
		 		  			  	   			
		 		  			  	   				 Intent intent = getActivity().getIntent();
		 		  			  	   				 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		 				  	   				     getActivity().overridePendingTransition(0, 0);
		 				  	   				     getActivity().finish();
		 				  	   				     getActivity().overridePendingTransition(0, 0);
		 				  	   				     startActivity(intent);		 		  			  	   			 
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

		return v;
	}

	private boolean uploadPublic(String path) {
        
		boolean ok = false;
        HttpURLConnection conn = null;
        DataOutputStream dos = null; 
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(path);
         
        if (!sourceFile.isFile()) {
             
             dialog.dismiss();
              
             Log.e("uploadFile", "Source File not exist :"+path);     
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
                 conn.setRequestProperty("uploaded_file", email+".key");
                 conn.setRequestProperty("user", email);

                  
                 dos = new DataOutputStream(conn.getOutputStream());
        
                 dos.writeBytes(twoHyphens + boundary + lineEnd);
                 dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename="+ email+".key" + lineEnd);

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
                	 ok = true;
                	 getActivity().runOnUiThread(new Runnable() {
                          public void run() {
                       	   
                              Toast.makeText(getActivity(), "To δημοσιο κλειδι στάλθηκε επιτυχώς",
                                      Toast.LENGTH_SHORT).show();


                          }
                      });               
                 }else{
               	  System.out.println("REGISTER ERROR");
                 }
                  
                 //close the streams //
                 fileInputStream.close();
                 dos.flush();
                 dos.close();
                   
            } catch (MalformedURLException ex) {
                 
                dialog.dismiss(); 
                 
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
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
                        Toast.makeText(getActivity(), "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                                                 + e.getMessage(), e); 
            }
            dialog.dismiss();      
             
         } // End else block
		return ok;
       } 
}
