package nakis.theodorou.storefiles.library;


import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;


public class AsyncTaskRunner extends  AsyncTask<String,String,String>{
	

	 private String resp;
	

	
	 public static AsyncTaskRunner GetAsyncTaskRunner(Context applicationContext) {
		    
		 
		return new AsyncTaskRunner();
	}
	 
	

	 
	@Override
	 protected String doInBackground(String... params) {
	   String tag = params[0].toString();  
	   
	         if(tag.equals("login")){
	        	 
	         ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		       postParameters.add(new BasicNameValuePair("tag",params[0]));
	           postParameters.add(new BasicNameValuePair("email",params[1]));
	           postParameters.add(new BasicNameValuePair("password",params[2]));
	           String response = null;
	           try {
//	        	   response = SimpleHttpClient.executeHttpPost("http://10.0.2.2:1234/storefiles/index.php", postParameters);
	        	   response = SimpleHttpClient.executeHttpPost("http://192.168.8.215:1234/storefiles/index.php", postParameters);
//	        	   response = SimpleHttpClient.executeHttpPost("http://83.212.135.189:1234/storefiles/index.php", postParameters);

	        	   resp = response.toString();
	        	   } catch (Exception e) {
	        		   e.printStackTrace();
	        		   resp = e.getMessage();
	        		   }
	           
	         }else if (tag.equals("register")){
	        	 ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			       postParameters.add(new BasicNameValuePair("tag",params[0]));
		           postParameters.add(new BasicNameValuePair("name",params[1]));
		           postParameters.add(new BasicNameValuePair("email",params[2]));
		           postParameters.add(new BasicNameValuePair("password",params[3]));
		           postParameters.add(new BasicNameValuePair("publickey",params[4]));

		           String response = null;
	           try {
//	        	   response = SimpleHttpClient.executeHttpPost("http://10.0.2.2:1234/storefiles/index.php", postParameters);
	        	   response = SimpleHttpClient.executeHttpPost("http://192.168.8.215:1234/storefiles/index.php", postParameters);
//	        	   response = SimpleHttpClient.executeHttpPost("http://83.212.135.189:1234/storefiles/index.php", postParameters);

	        	   resp = response.toString();
//	        	   System.out.println(res);
	        	   } catch (Exception e) {
	        		   e.printStackTrace();
	        		   resp = e.getMessage();
	        		   }
	           
	         }else if (tag.equals("sendmessage")){
	        	 ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			       postParameters.add(new BasicNameValuePair("tag",params[0]));
		           postParameters.add(new BasicNameValuePair("fullname",params[1]));
		           postParameters.add(new BasicNameValuePair("email",params[2]));
		           postParameters.add(new BasicNameValuePair("thema",params[3]));
		           postParameters.add(new BasicNameValuePair("message",params[4]));


		           String response = null;
		           try {
//		        	   response = SimpleHttpClient.executeHttpPost("http://10.0.2.2:1234/storefiles/index.php", postParameters);
		        	   response = SimpleHttpClient.executeHttpPost("http://192.168.8.215:1234/storefiles/index.php", postParameters);
//		        	   response = SimpleHttpClient.executeHttpPost("http://83.212.135.189:1234/storefiles/index.php", postParameters);

		        	   resp = response.toString();
		        	   System.out.println(resp);
		        	   } catch (Exception e) {
		        		   e.printStackTrace();
		        		   resp = e.getMessage();
		        		   }

	         
	         }else if (tag.equals("getlist")){
	        	 String response = null;
	        	 ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			       postParameters.add(new BasicNameValuePair("tag",params[0]));
		           postParameters.add(new BasicNameValuePair("email",params[1]));

	        	 try{
//		        	   response = SimpleHttpClient.executeHttpPost("http://10.0.2.2:1234/storefiles/index.php", postParameters);
		        	   response = SimpleHttpClient.executeHttpPost("http://192.168.8.215:1234/storefiles/index.php", postParameters);

	        		 resp = response.toString();
	        		 
	        	 }catch(Exception e){
	        		 e.printStackTrace();
	        		  resp = e.getMessage();
	        	 }
	         }else if (tag.equals("getmessages")){
	        	 String response = null;
	        	 ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			       postParameters.add(new BasicNameValuePair("tag",params[0]));
		           postParameters.add(new BasicNameValuePair("email",params[1]));

	        	 try{
//		        	   response = SimpleHttpClient.executeHttpPost("http://10.0.2.2:1234/storefiles/index.php", postParameters);
		        	   response = SimpleHttpClient.executeHttpPost("http://192.168.8.215:1234/storefiles/index.php", postParameters);
//		        	   response = SimpleHttpClient.executeHttpPost("http://83.212.135.189:1234/storefiles/index.php", postParameters);

	        		 resp = response.toString();
	        	 }catch(Exception e){
	        		 e.printStackTrace();
	        		  resp = e.getMessage();
	        	 }
	        	 
	         }else{
	         
	          resp="Invalid number of arguments-"+tag;
	         }
	         return resp;
	 }
	
}