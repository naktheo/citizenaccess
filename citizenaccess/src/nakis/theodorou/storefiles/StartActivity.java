package nakis.theodorou.storefiles;

import java.io.File;

import nakis.theodorou.storefiles.fragment.LoginFragment;
import nakis.theodorou.storefiles.fragment.RegisterFragment;
import nakis.theodorou.storefiles.library.DBAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Environment;


public class StartActivity extends Activity{
	
	DBAdapter db = new DBAdapter(this);
	boolean is;
	private static String extStorageDirectory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_start);
		File folder = new File(Environment.getExternalStorageDirectory().toString()+"/storefiles/data");
		 folder.mkdirs();
		 extStorageDirectory = folder.toString();
		db.open();
		is = db.userExist();
		db.close();

		if (is==true){
			
			Fragment fragment = new LoginFragment();
			FragmentManager fragmentManager = getFragmentManager();
	        fragmentManager.beginTransaction().replace(R.id.frame_login_register, fragment).commit();
	        
		}else{
			  File keystore = new File(extStorageDirectory, "truststore");
			  if (keystore.exists()){
				  keystore.delete();
			  }
				  Fragment fragment = new RegisterFragment();
				  FragmentManager fragmentManager = getFragmentManager();
				  fragmentManager.beginTransaction().replace(R.id.frame_login_register, fragment).commit();
			  
		}

		
		
	}

	@Override
	public void onBackPressed() {
	    // your code.
	}
	
	
	

}
