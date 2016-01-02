package nakis.theodorou.storefiles.model;

public class MessageItem {
	   private String message,apantisi;
	    private String count = "0";
	    // boolean to set visiblity of the counter
	    private boolean isCounterVisible = false;
	     
	    public MessageItem(){}
	 
	    public MessageItem(String message, String apantisi){
	        this.message = message;
	        this.apantisi = apantisi;
	    }
	     
	    public MessageItem(String message, String apantisi, boolean isCounterVisible, String count){
	        this.message = message;
	        this.apantisi = apantisi;
	        this.isCounterVisible = isCounterVisible;
	        this.count = count;
	    }
	     
	    public String getMessage(){
	        return this.message;
	    }
	     
	    public String getApantisi(){
	        return this.apantisi;
	    }
	     
	    public String getCount(){
	        return this.count;
	    }
	     
	    public boolean getCounterVisibility(){
	        return this.isCounterVisible;
	    }
	     
	    public void setTitle(String stoixeio){
	        this.message = stoixeio;
	    }
	     
	    public void setIcon(String metavliti){
	        this.apantisi = metavliti;
	    }
	     
	    public void setCount(String count){
	        this.count = count;
	    }
	     
	    public void setCounterVisibility(boolean isCounterVisible){
	        this.isCounterVisible = isCounterVisible;
	    }
}
