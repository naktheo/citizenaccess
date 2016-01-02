package nakis.theodorou.storefiles.adapter;

import java.util.ArrayList;

import nakis.theodorou.storefiles.R;
import nakis.theodorou.storefiles.model.MessageItem;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class MessageAdapter extends BaseAdapter {
    Context cntx;
	private ArrayList<MessageItem> array_sort;

 public MessageAdapter(Context context , ArrayList<MessageItem> array_sort)
 {
     this.cntx=context;
     this.array_sort =array_sort;

 }

 public int getCount()
 {
     return array_sort.size();
 }

 public Object getItem(int position)
 {
     return array_sort.get(position);
 }

 public long getItemId(int position)
 {
     return position;
 }

 public View getView(final int position, View convertView, ViewGroup parent)
 {
	  if (convertView == null) {
         LayoutInflater mInflater = (LayoutInflater)
                 cntx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
         convertView = mInflater.inflate(R.layout.message_list_item, null);
     }
     
     
     TextView tv = (TextView) convertView.findViewById(R.id.textViewmnm);
     TextView tv2 = (TextView)	convertView.findViewById(R.id.textViewap);
     
     tv.setText(array_sort.get(position).getMessage());
     tv2.setText(array_sort.get(position).getApantisi());
     
 return convertView;
 }
}
