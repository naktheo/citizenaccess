package nakis.theodorou.storefiles.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;


import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

//import android.content.Context;

public class SimpleHttpClient {
	 /** The time it takes for our client to timeout */
	    public static final int HTTP_TIMEOUT = 3 * 1000; // milliseconds
//       private static Context cntxt;
	    /** Single instance of our HttpClient */
	    private static HttpClient mHttpClient;

	    /**
	     * Get our single instance of our HttpClient object.
	     *
	     * @return an HttpClient object with connection parameters set
	     */
//	    private static HttpClient getHttpClient() {
//	        if (mHttpClient == null) {
//	         //sets up parameters
//	            HttpParams params = new BasicHttpParams();
//	            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//	            HttpProtocolParams.setContentCharset(params, "UTF_8");
//	            params.setBooleanParameter("http.protocol.expect-continue", false);
//	            //registers schemes for both http and https
//	            SchemeRegistry registry = new SchemeRegistry();
//	            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
////	            registry.register(new Scheme("https", newSslSocketFactory(), 443));
//	            ClientConnectionManager manager = new ThreadSafeClientConnManager(params, registry);
//	            mHttpClient = new DefaultHttpClient(manager, params);
//	        }
//	        return mHttpClient;
//	    }
	    
	    private static HttpClient getHttpClient() {
	    	
	    	HttpParams httpParameters = new BasicHttpParams();
	    	HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
	    	HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);
	    	mHttpClient = new DefaultHttpClient(httpParameters);
	    	mHttpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
	    	mHttpClient.getParams().setParameter("http.socket.timeout", new Integer(2000));
	    	mHttpClient.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);
	    	httpParameters.setBooleanParameter("http.protocol.expect-continue", false);
	    	
	    	return mHttpClient; 	
	    }
	    
//	    private static SSLSocketFactory newSslSocketFactory() {
//	        try {
//	          KeyStore trusted = KeyStore.getInstance("bks");
//	          InputStream in = cntxt.getResources().openRawResource(R.raw.mytruststore);
//	          try {
//	            trusted.load(in, "tritheo90".toCharArray());
//	          } finally {
//	            in.close();
//	          }
//	         SSLSocketFactory f = new SSLSocketFactory(trusted);
//	         
//	          return f;
//	        } catch (Exception e) {
//	          throw new AssertionError(e);
//	        }
//	    }
	    
	  
		/**
	     * Performs an HTTP Post request to the specified url with the
	     * specified parameters.
	     *
	     * @param url The web address to post the request to
	     * @param postParameters The parameters to send via the request
	     * @return The result of the request
	     * @throws Exception
	     */
	    public static String executeHttpPost(String url, ArrayList<NameValuePair> postParameters) throws Exception {
	        BufferedReader in = null;
	        try {
	            HttpClient client = getHttpClient();
	            HttpPost request = new HttpPost(url);
	            request.getParams().setParameter("http.socket.timeout", new Integer(5000));
	            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters , HTTP.UTF_8);
	            request.setEntity(formEntity);
	            HttpResponse response = client.execute(request);
	            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

	            StringBuffer sb = new StringBuffer("");
	            String line = "";
	            String NL = System.getProperty("line.separator");
	            while ((line = in.readLine()) != null) {
	                sb.append(line + NL);
	            }
	            in.close();

	            String result = sb.toString();
//	            Log.d("RESPONSE", result);
	            return result;
	        }
	        finally {
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }

	    /**
	     * Performs an HTTP GET request to the specified url.
	     *
	     * @param url The web address to post the request to
	     * @return The result of the request
	     * @throws Exception
	     */
	    public static String executeHttpGet(String url) throws Exception {
	        BufferedReader in = null;
	        try {
	        	
//	        	HttpClient httpclient = new DefaultHttpClient();    
//	            HttpPost httppost = new HttpPost("http://example.com/your_file.php");
//	             ResponseHandler<String> responseHandler = new BasicResponseHandler();
//	             String responseBody = httpclient.execute(httppost, responseHandler);
	        	
	            HttpClient client = getHttpClient();
	            HttpGet request = new HttpGet();
	            request.setURI(new URI(url));
	            HttpResponse response = client.execute(request);
	            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

	            StringBuffer sb = new StringBuffer("");
	            String line = "";
	            String NL = System.getProperty("line.separator");
	            while ((line = in.readLine()) != null) {
	                sb.append(line + NL);
	            }
	            in.close();

	            String result = sb.toString();
	            return result;
	        }
	        finally {
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	
}