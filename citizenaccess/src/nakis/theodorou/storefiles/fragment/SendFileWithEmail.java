package nakis.theodorou.storefiles.fragment;

import java.io.File;

import nakis.theodorou.storefiles.R;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendFileWithEmail extends Fragment {
	
	public static String  reqStorageDirectory;
	EditText t1,t2;
	String email;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File req = new File(Environment.getExternalStorageDirectory().toString()+"/storefiles/myfiles");
		req.mkdirs();
		reqStorageDirectory = req.toString();
		SharedPreferences regdata = getActivity().getSharedPreferences("RegisterData", Context.MODE_PRIVATE);
		email = regdata.getString("email", "user@example.eu");
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.send_message_fragment, container,false);
		t1 = (EditText) v.findViewById(R.id.editTextthema);
		t2 = (EditText) v.findViewById(R.id.editTextMessage);

		Button btn = (Button) v.findViewById(R.id.buttonSendMessage);
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Uri uri = Uri.fromFile(new File(reqStorageDirectory, "request.xml"));
				String sub = t1.getText().toString();
				String body = t2.getText().toString();
				try{
					Intent i = new Intent(android.content.Intent.ACTION_SEND);
					i.setType("message/rfc822");
					i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
					i.putExtra(Intent.EXTRA_SUBJECT, sub);
					i.putExtra(Intent.EXTRA_TEXT   , body);
//					i.putExtra(Intent.EXTRA_STREAM, uri);
					startActivity(android.content.Intent.createChooser(i, "Send mail..."));
					}catch(Exception e){
						System.out.println(e);
						Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				    }
			}
		});

		return v;
		
	}

}
