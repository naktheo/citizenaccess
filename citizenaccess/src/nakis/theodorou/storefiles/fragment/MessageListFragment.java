package nakis.theodorou.storefiles.fragment;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import nakis.theodorou.storefiles.R;
import nakis.theodorou.storefiles.adapter.MessageAdapter;
import nakis.theodorou.storefiles.fragment.GetFileFragment.DeleteFileDialog;
import nakis.theodorou.storefiles.library.AsyncTaskRunner;
import nakis.theodorou.storefiles.library.DBAdapter;
import nakis.theodorou.storefiles.model.MainInfoItem;
import nakis.theodorou.storefiles.model.MessageItem;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

public class MessageListFragment extends Fragment {
	
	ListView mlist;
	Button b;
	String mail,response;
	private AsyncTask<String, String, String> asyncTask;
    JSONObject json=null;
    String[] val = new String[7];
    int num,i;
    private ArrayList<MessageItem> messageItems;
    MessageAdapter adapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences regdata = getActivity().getSharedPreferences("RegisterData", Context.MODE_PRIVATE);
		mail = regdata.getString("email", "user@example.eu");
	    messageItems = new ArrayList<MessageItem>();

		AsyncTaskRunner runner=new AsyncTaskRunner();
        asyncTask=runner.execute("getmessages",mail);
        try{
	
			String asyncResultText=asyncTask.get();
			json = new JSONObject(asyncResultText);
			Log.d("JSONObject", json.toString(5));
			JSONObject j = json.getJSONObject("jsonresult");
			int leng = j.length();
			for(i=1; i<=leng; i++){
				JSONObject k = j.getJSONObject(Integer.toString(i));
				JSONObject answer = k.getJSONObject("responses");
		        messageItems.add(new MessageItem((answer.getString("message")) , (answer.getString("apantisi"))));
		        }
			adapter = new MessageAdapter(getActivity(),messageItems);

			
        }catch(Exception e){
        	e.printStackTrace();

        }
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.messagelist_fragment, container, false);
		mlist = (ListView) v.findViewById(R.id.listViewMessage);
		mlist.setAdapter(adapter);
		b = (Button) v.findViewById(R.id.buttonNewmsg) ;
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Fragment fragment = new SendMessageFragment();
	   		    FragmentManager fragmentManager = getFragmentManager();
	   		    fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
				
			}
		});
		
		return v;
	}
	
	

}
