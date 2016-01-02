package nakis.theodorou.storefiles.fragment;

import org.json.JSONObject;

import nakis.theodorou.storefiles.R;
import nakis.theodorou.storefiles.library.AsyncTaskRunner;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class RegisterWriteDB extends Fragment {

	ProgressDialog dialog;
	private String fullname, email, pass, publickey;
    private AsyncTask<String, String, String> asyncTask;
   	private String response;
    JSONObject json=null;
    private static String KEY_SUCCESS = "success",KEY_ERROR = "error";
    private boolean isOk = false;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			SharedPreferences regdata = getActivity().getSharedPreferences("RegisterData", Context.MODE_PRIVATE);
			 fullname = regdata.getString("fullname", "fail");
			 email = regdata.getString("email", "fail");
			 pass = regdata.getString("pass", "fail");
		   SharedPreferences x509data = getActivity().getSharedPreferences("X509Data", Context.MODE_PRIVATE);
		     publickey = x509data.getString("PublicKey", "fail");


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View v = inflater.inflate(R.layout.blank, container, false);
		 dialog = ProgressDialog.show(getActivity(), fullname,"Γινεται αποστολή τον στοιχείων σας " +
					"Παρακαλώ περιμένετε...", true);
		 
		 new Thread(new Runnable() {
	  			  @Override
	  			  public void run()
	  			  {	
//	  				Write new data to webserver database
	  			isOk = registerUser(fullname, email, pass, publickey);  
	  			getActivity().runOnUiThread(new Runnable() {
			  	   		 @Override
			  	   		 public void run()
			  	   		 {
			  	   			 dialog.dismiss();
			  	   			 if(isOk){
			  	   				 Fragment fragment = new RegisterUploadkey();
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

		
		return v;
	}
	
	public boolean registerUser(String fullname, String email, String pass, String publickey){

		boolean ok=false;
//	    Write DB on server
        AsyncTaskRunner runner=new AsyncTaskRunner();

        asyncTask=runner.execute("register", fullname, email, pass, publickey);
        
        try {
				String asyncResultText=asyncTask.get();
				response = asyncResultText.trim();   
				json = new JSONObject(response);
				
				if (json.getString(KEY_SUCCESS) != null) {
					System.out.println(json.toString());
					String res = json.getString(KEY_SUCCESS);
					if(res.equals("1")){
						Log.d("success","Server database updated");
						ok = true;
					}else if(json.getString(KEY_ERROR).equals("2")){
						Toast.makeText(getActivity(), "Το e-mail χρησημοποίται ηδη",
								Toast.LENGTH_SHORT).show();
					}else{
                      // Error in registration

                       	Toast.makeText(getActivity(), "Error occured in registration",
                                Toast.LENGTH_SHORT).show();
                       	Log.d("error", json.toString());
                        	}
					}
				} catch (Exception e1) {response = e1.getMessage();}
		return ok;		
        }

}
