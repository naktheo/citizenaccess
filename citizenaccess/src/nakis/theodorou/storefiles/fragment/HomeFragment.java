package nakis.theodorou.storefiles.fragment;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import nakis.theodorou.storefiles.R;
import nakis.theodorou.storefiles.adapter.MainInfoAdapter;
import nakis.theodorou.storefiles.library.AsyncTaskRunner;
import nakis.theodorou.storefiles.model.MainInfoItem;
import nakis.theodorou.storefiles.model.NavDrawerItem;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HomeFragment extends Fragment {
    
	private AsyncTask<String, String, String> asyncTask;
	private String response;
    JSONObject json=null;
    String[] val = new String[11];
    String[] stoixeia;
    private ArrayList<MainInfoItem> maininfoItems;
    MainInfoAdapter adapter;
    TextView view;
    ListView l;
    static String mail;


    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences regdata = getActivity().getSharedPreferences("RegisterData", Context.MODE_PRIVATE);
		mail = regdata.getString("email", "user@example.eu");
		
		stoixeia = getResources().getStringArray(R.array.stoixeia);
	    maininfoItems = new ArrayList<MainInfoItem>();

        AsyncTaskRunner runner=new AsyncTaskRunner();
        asyncTask=runner.execute("getlist",mail);
        try{
        	String asyncResultText=asyncTask.get();
			json = new JSONObject(asyncResultText);
		    JSONObject myResponse = json.getJSONObject("jsonresult");
		    
		    for(int i=0;i<myResponse.length();i++){
		    val[i] =myResponse.getString(Integer.toString(i));
		    System.out.println(val[i]);
		    }
		    

//		    System.out.println(val);
		    
        }catch(Exception e){
        	e.printStackTrace();

        }
        for(int i=1;i<val.length;i++){
		    maininfoItems.add(new MainInfoItem(stoixeia[i] , val[i]));

		    }

		    adapter = new MainInfoAdapter(getActivity(),maininfoItems);
		    

	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
		
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        l = (ListView) rootView.findViewById(R.id.listViewHome);
        l.setAdapter(adapter);
//        view = (TextView) rootView.findViewById(R.id.textViewhome);
//        view.setText(json.toString());
		 
       
      
        
        
          
        return rootView;
    }
}