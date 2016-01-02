package nakis.theodorou.storefiles.model;


public class FileItem {

	 private String file;
	 private String created;
	    private String count = "0";
	    // boolean to set visiblity of the counter
	    private boolean isCounterVisible = false;
	     
	    public FileItem(){}
	 
	    public FileItem(String file, String created){
	        this.file = file;
	        this.created = created;
	    }
	     
	    public FileItem(String file, String created, boolean isCounterVisible, String count){
	    	this.file = file;
	        this.created = created;
	        this.isCounterVisible = isCounterVisible;
	        this.count = count;
	    }
	     
	    public String getFile(){
	        return this.file;
	    }
	    
	    public String getCreated(){
	        return this.created;
	    }
	     
	    
	     
	    public String getCount(){
	        return this.count;
	    }
	     
	    public boolean getCounterVisibility(){
	        return this.isCounterVisible;
	    }
	     
	    public void setFile(String file){
	        this.file = file;
	    }
	     
	    public void setCreated(String created){
	        this.created = created;
	    }
	    
	     
	    public void setCount(String count){
	        this.count = count;
	    }
	     
	    public void setCounterVisibility(boolean isCounterVisible){
	        this.isCounterVisible = isCounterVisible;
	    }
}
