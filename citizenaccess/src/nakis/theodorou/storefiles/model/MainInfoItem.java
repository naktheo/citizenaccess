package nakis.theodorou.storefiles.model;

public class MainInfoItem {

	    private String stoixeio,metavliti;
	    private String count = "0";
	    // boolean to set visiblity of the counter
	    private boolean isCounterVisible = false;
	     
	    public MainInfoItem(){}
	 
	    public MainInfoItem(String stoixeio, String metavliti){
	        this.stoixeio = stoixeio;
	        this.metavliti = metavliti;
	    }
	     
	    public MainInfoItem(String stoixeio, String metavliti, boolean isCounterVisible, String count){
	        this.stoixeio = stoixeio;
	        this.metavliti = metavliti;
	        this.isCounterVisible = isCounterVisible;
	        this.count = count;
	    }
	     
	    public String getStoixio(){
	        return this.stoixeio;
	    }
	     
	    public String getmetavliti(){
	        return this.metavliti;
	    }
	     
	    public String getCount(){
	        return this.count;
	    }
	     
	    public boolean getCounterVisibility(){
	        return this.isCounterVisible;
	    }
	     
	    public void setTitle(String stoixeio){
	        this.stoixeio = stoixeio;
	    }
	     
	    public void setIcon(String metavliti){
	        this.metavliti = metavliti;
	    }
	     
	    public void setCount(String count){
	        this.count = count;
	    }
	     
	    public void setCounterVisibility(boolean isCounterVisible){
	        this.isCounterVisible = isCounterVisible;
	    }
}