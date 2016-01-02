package nakis.theodorou.storefiles.fragment;

import org.json.JSONObject;

import nakis.theodorou.storefiles.MainActivity;
import nakis.theodorou.storefiles.R;
import nakis.theodorou.storefiles.library.AsyncTaskRunner;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment{
	
	EditText inputEmail;
    EditText inputPassword;
    Button login;
    private AsyncTask<String, String, String> asyncTask;
	private String response;
    JSONObject json=null;
    private static String KEY_SUCCESS = "success";
    String mail,remail;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View v = inflater.inflate(R.layout.login_fragment, container, false);
		   
		 inputEmail = (EditText) v.findViewById(R.id.editTextlogmail);
		 inputPassword = (EditText) v.findViewById(R.id.editTextlogpass);
	     login = (Button) v.findViewById(R.id.buttonlog);
	     
	     login.setOnClickListener(new View.OnClickListener() {
	    	 
	            public void onClick(View view) {
	            	SharedPreferences regdata = getActivity().getSharedPreferences("RegisterData", Context.MODE_PRIVATE);
	        		 mail = regdata.getString("email", "user@example.eu"); 
	        		 remail = inputEmail.getText().toString();
	        		if(mail.equals(remail)){
	            	loginUser();
	        		}else{
	        			Toast.makeText(getActivity(), "Λάθος email...",
                                Toast.LENGTH_LONG).show();
	        		}
	            }

				});
	     return v;
	}

	public  void loginUser(){
    	
    	String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
    	AsyncTaskRunner runner=new AsyncTaskRunner();
		asyncTask=runner.execute("login",email,password);
		try {
			
			String asyncResultText=asyncTask.get();
			response = asyncResultText.trim();   
			json = new JSONObject(response);
			if (json.getString(KEY_SUCCESS) != null) {
				
				String res = json.getString(KEY_SUCCESS);
				if(Integer.parseInt(res) == 1){
			    	
			    	Intent intent = new Intent(getActivity(), MainActivity.class);
			    	intent.putExtra("emailaddress", email);
			    	startActivity(intent);
			    	getActivity().finish();
			    	
			    }else{
			    	Toast.makeText(getActivity(), "Λάθος password...",
	                        Toast.LENGTH_LONG).show();
			    }
			}
		}catch(Exception e){
			
			System.out.println(e);
		}
    	
    }
}
