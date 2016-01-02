package nakis.theodorou.storefiles.fragment;

import nakis.theodorou.storefiles.R;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ExitFragment extends DialogFragment {
	
	private Button btnyes,btnno;
	public boolean b;
    public String[] navMenuTitles;
    

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

	}

	public ExitFragment() {
		

	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	getDialog().setTitle("Kλείσιμο Εφαρμογής;");
        View view = inflater.inflate(R.layout.exitdialoglayout, container);
        btnyes = (Button) view.findViewById(R.id.buttonyes);
        btnno = (Button) view.findViewById(R.id.buttonno);
        btnyes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 getActivity().finish();
			}
		});
        
        btnno.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				ExitFragment.this.dismiss();          	
			}
		});


        

        return view;
    }

	
	
	
}
