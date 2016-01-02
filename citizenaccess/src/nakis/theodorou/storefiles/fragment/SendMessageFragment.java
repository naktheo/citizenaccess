package nakis.theodorou.storefiles.fragment;

import org.json.JSONObject;

import nakis.theodorou.storefiles.R;
import nakis.theodorou.storefiles.library.AsyncTaskRunner;
import nakis.theodorou.storefiles.library.DBAdapter;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendMessageFragment extends Fragment {
	
	EditText etthema, etmessage ;
	Button send;
	private String thema,message,fullname,email,response;
	ProgressDialog dialog;
    private AsyncTask<String, String, String> asyncTask;
    private JSONObject json=null;
    private static String KEY_SUCCESS = "success",KEY_ERROR = "error";
    TextView t;
    int num;
    DBAdapter db;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		db = new DBAdapter(getActivity());
		View v = inflater.inflate(R.layout.send_message_fragment, container, false);
		etthema = (EditText) v.findViewById(R.id.editTextthema);
		etmessage = (EditText) v.findViewById(R.id.editTextMessage);
		send = (Button) v.findViewById(R.id.buttonSendMessage);
		
		send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				thema = etthema.getText().toString();
				message = etmessage.getText().toString();
				if(thema.equals("") || message.equals("")){
					  Toast.makeText(getActivity(), "Παρακαλώ εισάγετε θέμα και μήνυμα." ,Toast.LENGTH_LONG).show();

				}else{
	
				SharedPreferences msgdata = getActivity().getSharedPreferences("Themata", Context.MODE_PRIVATE);
				msgdata.edit().putString("thema", thema).commit();

				SharedPreferences regdata = getActivity().getSharedPreferences("RegisterData", Context.MODE_PRIVATE);
				fullname = regdata.getString("fullname", "user");
				email = regdata.getString("email", "user@example.eu");
				
				dialog = ProgressDialog.show(getActivity(), fullname,"Αποστολή Μηνύματος. Παρακαλώ Περιμένετε...", true);
				new Thread(new Runnable() {
					  @Override
					  public void run()
					  {
						  AsyncTaskRunner runner=new AsyncTaskRunner();
						  asyncTask=runner.execute("sendmessage", fullname, email, thema, message);
						  try {
							  String asyncResultText=asyncTask.get();
							  response = asyncResultText.trim();
							  json = new JSONObject(response);
							  
							  if (json.getString(KEY_SUCCESS) != null) {
								  System.out.println(json.toString());
								  String res = json.getString(KEY_SUCCESS);
								  if(res.equals("1")){
									  Log.d("success","Message sended successfully");
									SharedPreferences msgcount = getActivity().getSharedPreferences("MessageCount", Context.MODE_PRIVATE);
									num = msgcount.getInt("msgplus", 0);
									msgcount.edit().putInt("msgplus", ++num).commit();
									db.open();
									db.insertThema(thema);
									db.close();
									
									  }else {
										  // Error in registration
										  Toast.makeText(getActivity(), "Error occured in sending" ,Toast.LENGTH_SHORT).show();
										  Log.d("error", json.toString());
										  }
							  }
								  } catch (Exception e1) {response = e1.getMessage();}
								  getActivity().runOnUiThread(new Runnable() {
								   		 @Override
								   		 public void run()
								   		 {
								   			 dialog.dismiss();
								   			
								   				 Fragment fragment = new MessageListFragment();
								   				 FragmentManager fragmentManager = getFragmentManager();
								   				 fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
								   			 }
								   		 });
								  }
								}).start();
				}
			}
		});
		
		
		return v;
	}
	

}


//		Write new data to webserver database

		  
