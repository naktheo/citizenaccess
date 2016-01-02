package nakis.theodorou.storefiles.adapter;

import java.util.ArrayList;

import nakis.theodorou.storefiles.R;
import nakis.theodorou.storefiles.model.FileItem;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter {
    
    private Context context;
    private ArrayList<FileItem> fileItems;
     
    public FileListAdapter(Context context, ArrayList<FileItem> fileItems){
        this.context = context;
        this.fileItems = fileItems;
    }
 
    @Override
    public int getCount() {
        return fileItems.size();
    }
 
    @Override
    public Object getItem(int position) {      
        return fileItems.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.file_list_item, null);
        }
          
       
        TextView txtfile = (TextView) convertView.findViewById(R.id.textViewFile);
        TextView txtcreated = (TextView) convertView.findViewById(R.id.textViewCreated);
               
        txtfile.setText(fileItems.get(position).getFile());
        txtcreated.setText(fileItems.get(position).getCreated());

//        // displaying count
//        // check whether it set visible or not
//        if(fileItems.get(position).getCounterVisibility()){
//            txtcreated.setText(fileItems.get(position).getCount());
//        }else{
//            // hide the counter view
//            txtcreated.setVisibility(View.GONE);
//        }
         
        return convertView;
    }
 
}
