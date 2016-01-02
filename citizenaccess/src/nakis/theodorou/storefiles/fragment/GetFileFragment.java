package nakis.theodorou.storefiles.fragment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import nakis.theodorou.storefiles.R;
import nakis.theodorou.storefiles.adapter.FileListAdapter;
import nakis.theodorou.storefiles.library.DBAdapter;
import nakis.theodorou.storefiles.model.FileItem;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class GetFileFragment extends Fragment{
	
	private ListView fileslist;
	private ArrayList<FileItem> items;
	public String[] files;
	private static String reqStorageDirectory,filename;
	private FileListAdapter adapter;
	private DBAdapter db;
	private ArrayList<String> request =new ArrayList<String>();
	private ArrayList<String> user =new ArrayList<String>();
	String eggrafo,created;
	private ArrayList<String> id =new ArrayList<String>();
	public int pos;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog mProgressDialog;
	Context c;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File dat = new File(Environment.getExternalStorageDirectory().toString()+"/storefiles/myfiles");
		dat.mkdirs();
		reqStorageDirectory = dat.toString();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,
			Bundle savedInstanceState) {
		db = new DBAdapter(getActivity());
		View v = inflater.inflate(R.layout.getfile_fragment, container,false);
		fileslist = (ListView) v.findViewById(R.id.listViewMyfiles);
		items = new ArrayList<FileItem>();
		db.open();
		request = db.getRequestData();
		user = db.getUser();
		db.close();

		int size = request.size();
		for (int i=0;i<size; i=i+3){
			
		    String rid = request.get(i).toString();
		    id.add(rid);
			eggrafo = request.get(i+1).toString();
			created = request.get(i+2).toString();
			items.add(new FileItem(eggrafo,created));	
			}
		System.out.println(id);
		adapter = new FileListAdapter(getActivity(),
                items);
		
		fileslist.setAdapter(adapter);
		
		fileslist.setOnItemClickListener(new OnItemClickListener() {

 			public void onItemClick(AdapterView<?> arg0,View arg1, int position, long arg3){
 				
 				db.open();
 				filename = db.getCertName(id.get(position));
 				db.close();
 				System.out.println(filename);
// 				String url = "http://10.0.2.2:1234/storefiles/"+filename;
 				String url = "http://192.168.8.215:1234/storefiles/"+filename;
 				startDownload(url);
 			}
 			   
 		});
		
		fileslist.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				   System.out.println(position);
				   pos = position;
				   FragmentTransaction ft = getFragmentManager().beginTransaction();
			        // Create and show the dialog.
				   DeleteFileDialog newFragment = new DeleteFileDialog ();
			        newFragment.show(ft, "dialog");
				
				return false;
			}
			
		});
		
		return v;
	}
	
	
	private void startDownload(String url) {
       
        new DownloadFileAsync().execute(url);

    }
	
	
	class DownloadFileAsync extends AsyncTask<String, String, String> {


	    @Override
	    protected String doInBackground(String... aurl) {
	        int count;
	       
	   try {

	    URL url = new URL(aurl[0]);
	    URLConnection conexion = url.openConnection();
	    conexion.connect();

	    int lenghtOfFile = conexion.getContentLength();
	    if (lenghtOfFile== -1){
	    	getActivity().runOnUiThread(new Runnable(){

	            @Override
	            public void run(){
	            	Toast.makeText(getActivity(), "Το αρχείο δεν ειναι έτοιμο ακομα", 
	 	    			   Toast.LENGTH_LONG).show();
	            }
	         });

	    	
	    }else{
	    	getActivity().runOnUiThread(new Runnable(){

	            @Override
	            public void run(){
	            	Toast.makeText(getActivity(), "Το έγγραφο αποθηκεύτικε στην sdcard", 
	 	    			   Toast.LENGTH_LONG).show();
	            }
	         });
	    	
	    Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
//	    FileOutputStream obj;
	    InputStream input = new BufferedInputStream(url.openStream());
	    //obj=openFileOutput("some_photo_from_gdansk_poland.jpg", Context.MODE_PRIVATE);
	    String Path= reqStorageDirectory;//"/data/data/com.server/";

	    OutputStream output = new FileOutputStream(Path+"/"+filename);//obj

	    byte data[] = new byte[lenghtOfFile];

	    long total = 0;

	        while ((count = input.read(data)) != -1) {
	            total += count;
	            publishProgress(""+(int)((total*100)/lenghtOfFile));
	            output.write(data, 0, count);
	        }

	        output.flush();
	        output.close();
	        input.close();
	    }
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return null;

	    }
	    
	}
	
	@SuppressLint("ValidFragment")
	public class DeleteFileDialog extends DialogFragment {

	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        return new AlertDialog.Builder(getActivity())
	            .setTitle("Διαγραφή Αρχείου")
	            .setMessage("Είστε σίγουρος οτι θέλετε να διαγράψετε το αρχείο")
	            .setNegativeButton(android.R.string.no, new OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                	Fragment fragment = new GetFileFragment();
	    				FragmentManager fragmentManager = getFragmentManager();
	    		        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
	                }
	            })
	            .setPositiveButton(android.R.string.yes,  new OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                	db.open();
	    				db.deleteRow(id.get(pos));
	    				db.close();
	    				File file = new File(reqStorageDirectory+"/"+user.get(1).toString()+id.get(pos)+".nakis");
	    				boolean deleted = file.delete();
	    				if (deleted){
	    				Fragment fragment = new GetFileFragment();
	    				FragmentManager fragmentManager = getFragmentManager();
	    		        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
	    				}
	                }
	            })
	            .create();
	    }
	}

	
	
}
