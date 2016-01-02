package nakis.theodorou.storefiles.library;

import java.sql.Timestamp;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {


private static final String KEY_ID = "id";
private static final String KEY_FULLNAME = "fullname";
private static final String KEY_EMAIL= "email";
private static final String KEY_EGGRAFO = "eggrafo";
private static final String KEY_YPIRESIA = "ypiresia";
private static final String KEY_CERTNAME = "certname";
private static final String KEY_THEMA = "thema";
private static final String KEY_APANTISI = "apantisi";
private static final String KEY_CREATED = "created";
private static final String TAG = "DBAdapter";
private static final String DATABASE_NAME = "MyFilesDB";
private static final String DATABASE_TABLE = "Certs";
private static final String DATABASE_TABLE2 = "User";
private static final String DATABASE_TABLE3 = "Messages";



private static final int DATABASE_VERSION = 2;


private static final String DATABASE_CREATE = "create table Certs  " +
		"(id String , eggrafo String, ypiresia String, created String,certname String);";

private static final String DATABASE_CREATE2 = "create table User  " +
		"(fullname String, email String);";

private static final String DATABASE_CREATE3 = "create table Messages  " +
		"(thema String, apantisi String);";


private final Context context;

private DatabaseHelper DBHelper;
private SQLiteDatabase db;

public DBAdapter(Context ctx)
{
   this.context = ctx;
   DBHelper = new DatabaseHelper(context);
   
}

private static class DatabaseHelper extends SQLiteOpenHelper
{
     DatabaseHelper(Context context)
      {
           super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db)
      {
        try {
               db.execSQL(DATABASE_CREATE);
               db.execSQL(DATABASE_CREATE2);
               db.execSQL(DATABASE_CREATE3);


            } 
        catch (SQLException e) {
             e.printStackTrace();
             }
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
      {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
           + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS Certs");
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Messages");


        onCreate(db);
      }

}
      //---opens the database---
   public DBAdapter open() throws SQLException
  {
      db = DBHelper.getWritableDatabase();
      return this;
  }

   //---closes the database---
   public void close()
   {
       DBHelper.close();
   }

   public long insertRequest(String id ,String eggrafo,String ypiresia,String created ,String certname)
   {
       ContentValues initialValues = new ContentValues();
       initialValues.put(KEY_ID , id);
       initialValues.put(KEY_EGGRAFO , eggrafo);
       initialValues.put(KEY_YPIRESIA , ypiresia);
       initialValues.put(KEY_CREATED , created);
       initialValues.put(KEY_CERTNAME , certname);

      
       return db.insert(DATABASE_TABLE, null, initialValues);
  }
   
   public long insertUser(String fullname , String email)
   {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FULLNAME , fullname);
        initialValues.put(KEY_EMAIL , email);

       
        return db.insert(DATABASE_TABLE2, null, initialValues);
   }
   
   public long insertThema(String thema)
   {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_THEMA , thema);
        return db.insert(DATABASE_TABLE3, null, initialValues);
   }
   
   public long insertApantisi(String apantisi)
   {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_APANTISI , apantisi);
        return db.insert(DATABASE_TABLE3, null, initialValues);
   }
   
   public ArrayList<String>  getMessageData() {

       ArrayList<String>  requestArrayList = new ArrayList<String> ();
	 	 String selectQuery = "SELECT  * FROM Messages;";

	 	 Cursor cursor = db.rawQuery(selectQuery, null);
	 	 if (cursor.moveToFirst()) {
	 		 do {
	 			 requestArrayList.add(cursor.getString(0));
	 			 requestArrayList.add(cursor.getString(1));
	 			 } while (cursor.moveToNext()); // Move Cursor to the next row
	 		 }
	 	 // return contact list
	 	 return requestArrayList;
	 	 }

   
   public ArrayList<String>  getRequestData() {

       ArrayList<String>  requestArrayList = new ArrayList<String> ();
	 	 String selectQuery = "SELECT  * FROM Certs;";

	 	 Cursor cursor = db.rawQuery(selectQuery, null);
	 	 if (cursor.moveToFirst()) {
	 		 do {
	 			 requestArrayList.add(cursor.getString(0));
	 			 requestArrayList.add(cursor.getString(1));
	 			 requestArrayList.add(cursor.getString(3));
	 			 } while (cursor.moveToNext()); // Move Cursor to the next row
	 		 }
	 	 // return contact list
	 	 return requestArrayList;
	 	 }
   
   
   
   public ArrayList<String>  getUser() {

       ArrayList<String>  user = new ArrayList<String> ();
	 	 String selectQuery = "SELECT  * FROM User;";
	 	 Cursor cursor = db.rawQuery(selectQuery, null);
	 	 if (cursor.moveToFirst()) {
	 	            do {
	 	            	user.add(cursor.getString(0));
	 	            	user.add(cursor.getString(1));

	 	            	} while (cursor.moveToNext()); // Move Cursor to the next row
	 	        }
	 	      
	 	        // return contact list
	 	        return user;
	 	    }
   
  
   
   public boolean userExist()
   {
	   boolean is = false;
	    String selectQuery = "SELECT  * FROM User;";
	    try{
	    Cursor cursor = db.rawQuery(selectQuery, null); 
	    if (cursor.moveToFirst()) {
	    	is = true;
	    }
	    }catch(Exception e){
	    	
	    }
	   return is;  
   }
   
  
   public String  getCertName(String id) {

         String namecert="fail";
         try{
        	 String selectQuery = "SELECT * FROM Certs WHERE id="+id+";";
        	 Cursor cursor = db.rawQuery(selectQuery, null);
        	 if (cursor.moveToFirst()) {
        	 namecert = cursor.getString(cursor.getColumnIndex("certname"));
        	 }
        	 }catch(Exception e){
        		 e.printStackTrace();
        		 }
         return namecert;
         }
   
   public String  getEggrafo(String id) {

       String namecert="fail";
       try{
      	 String selectQuery = "SELECT * FROM Certs WHERE id="+id+";";
      	 Cursor cursor = db.rawQuery(selectQuery, null);
      	 if (cursor.moveToFirst()) {
      	 namecert = cursor.getString(cursor.getColumnIndex("eggrafo"));
      	 }
      	 }catch(Exception e){
      		 e.printStackTrace();
      		 }
       return namecert;
       }
   
   
   public boolean deleteRow(String id) 
   {
	   System.out.println(id);
       return db.delete(DATABASE_TABLE, "id =" + id, null) > 0;
   }

}

