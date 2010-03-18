package skylight1.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;


import android.content.Context;
import android.util.Log;

public class Chest {
		
	private static final int CURRENT_FORMAT_VERSION = 1;
	private Context context;

	private boolean somethingWasLoaded;
	private Hashtable<String, Record> hashmap;		
	
	//===========================	
	public interface Record {
		
		public static final int TYPE_LONG 		= 1;
		public static final int TYPE_BOOLEAN 	= 2;
		public static final int TYPE_FLOAT 		= 3;
		
		public void write(String k, DataOutputStream dos) throws IOException;

		public String valueAsString();
	}
		
	//========================
	private class LongRecord implements Record {

		private long v;
		
		public LongRecord(long v) {	
			this.v = v;		
		}
		
		public LongRecord(DataInputStream dis) throws IOException {
			this(dis.readLong());			
		}

		public void write(String k,DataOutputStream dos) throws IOException {
			dos.writeUTF(k);			
			dos.writeInt(Record.TYPE_LONG);			
			dos.writeLong(v);			
		}
		
		private void log(String k, int type, long v) {
			Log.v(this.getClass().getName(),"");
			Log.v(this.getClass().getName(),"");
			Log.v(this.getClass().getName(),"------SAVE----------");
			Log.v(this.getClass().getName(),"key="+k);
			Log.v(this.getClass().getName(),"type="+type);
			Log.v(this.getClass().getName(),"value="+v);
		}

		public long get() {
			return v;
		}

		public String valueAsString() {
			return String.valueOf(this.v);
		}

	}
	//###############
	
	private class BooleanRecord implements Record {

		private boolean v;
		
		public BooleanRecord(boolean v) {	
			this.v = v;		
		}

		public BooleanRecord(DataInputStream dis) throws IOException {
			this(dis.readBoolean());
		}

		public void write(String k,DataOutputStream dos) throws IOException {
			dos.writeUTF(k);
			dos.writeInt(Record.TYPE_BOOLEAN);
			dos.writeBoolean(v);
		//	this.log(k,Record.TYPE_BOOLEAN,v);
		}
		
		private void log(String k, int type, boolean v) {
			Log.v(this.getClass().getName(),"");
			Log.v(this.getClass().getName(),"");
			Log.v(this.getClass().getName(),"------SAVE----------");
			Log.v(this.getClass().getName(),"key="+k);
			Log.v(this.getClass().getName(),"type="+type);
			Log.v(this.getClass().getName(),"value="+v);
		}
		
		public boolean get() {
			return v;
		}
		
		public String valueAsString() {
			return String.valueOf(this.v);
		}

	}
	
//###############
	
	private class FloatRecord implements Record {

		private float v;
		
		public FloatRecord(float v) {	
			this.v = v;		
		}
		
		public FloatRecord(DataInputStream dis) throws IOException {
			this(dis.readFloat());
		}

		public void write(String k,DataOutputStream dos) throws IOException {
			dos.writeUTF(k);
			dos.writeInt(Record.TYPE_FLOAT);
			dos.writeFloat(v);	
		//	this.log(k,Record.TYPE_FLOAT,v);
		}
		
		private void log(String k, int type, float v) {

			Log.v(this.getClass().getName(),"");
			Log.v(this.getClass().getName(),"");
			Log.v(this.getClass().getName(),"------SAVE----------");
			Log.v(this.getClass().getName(),"key="+k);
			Log.v(this.getClass().getName(),"type="+type);
			Log.v(this.getClass().getName(),"value="+v);
		}
		
		public float get() {
			return v;
		}
		
		public String valueAsString() {
			return String.valueOf(this.v);
		}

	}
	
	//==============
	public Chest(Context context) {

		this.context = context;		
		this.somethingWasLoaded = false;		
		this.hashmap = new Hashtable<String, Record>();		
	}

	//==============
	public void saveToDisk(String filename) {
			
			FileOutputStream fOut;
			try {
				fOut = context.openFileOutput(filename,Context.MODE_PRIVATE);
			} catch (FileNotFoundException e) {
				fOut = null;
				Log.e(this.getClass().getName(),"Could open file to store gamesate. Not saving.");
			}
			
			if (fOut!= null) { 
			
				final DataOutputStream dos = new DataOutputStream(fOut);
				try {
					dos.writeInt(CURRENT_FORMAT_VERSION);
							
					writeData(dos);		
		            dos.close();
		            fOut.close();
	            
				} catch (IOException e) {
					Log.e(this.getClass().getName(),"Could not store game state to file. Removing file");					
					context.deleteFile(filename);					
				}
            
			}
		
}
		
	//========================		
	private void writeData(DataOutputStream dos) throws IOException {
		  
		final Hashtable<String, Record> hm = this.hashmap;		
		final Enumeration<String> e = hm.keys();
		
		while(e.hasMoreElements()) {		
			final String k = e.nextElement();	        
	        final Record r =  hashmap.get(k);		        
	        r.write(k,dos);
		  }		
	}

	//========================
	public void loadFromDisk(String filename) {
				
		try {			
			FileInputStream fis = context.openFileInput(filename);
			DataInputStream dis = new DataInputStream(fis);
	               
	            if ( dis != null ) {
	            		            	
	            	final int formatversion = dis.readInt();
	            	
	            	if (formatversion != CURRENT_FORMAT_VERSION) {	            		
	            		final String msg = String.format("Invalid file format version: is %d. Required: %d ",formatversion,CURRENT_FORMAT_VERSION);
						throw new RuntimeException(msg);	            			            		
	            	} else {
	            	
	            		this.hashmap.clear();
	            		
	            		try {	            	
	            		      while (true) { // exception deals catches EOF
	            		    	  
	            		    	  final String 	k = dis.readUTF();	            		    	  
	            		    	  final int 	type = dis.readInt();	 
	            		    	  
	            		    	  final Record record;	            		    	  
	            		    	  
	            		    	  switch (type) {	            		    	  	
	            		    	  	case Record.TYPE_BOOLEAN:  	record = new BooleanRecord(dis);  	break;
	            		    	  	case Record.TYPE_FLOAT:  	record = new FloatRecord(dis);  	break;
	            		    	  	case Record.TYPE_LONG:  	record = new LongRecord(dis);  		break;
	            		    	  	
	            		    	  	default: throw new RuntimeException(String.format("Err loading chest: unknown type: '%d'",type));
	            		    	  }
	            		    	  
	            		    	  this.hashmap.put(k, record);	            		    	              		    	  
	            		    	  
	            		        
	            		      }
	            		    } catch (EOFException eof) {
	            		    	//this is not an error
	            		    }
	            	}
	            }	           
	            
	        	dis.close();	        	
	        	somethingWasLoaded = true;
			
		} catch (FileNotFoundException e) {
			// no file found.. we're just not loading anything...
		} catch (IOException e) {
			Log.e(this.getClass().getName(),"Could not load gamedata from file");
		}					
		}
	
	//=====================
	public boolean isEmpty() {		
		return !somethingWasLoaded;
	}
	
	
	//======================
	public void putLong(String k,long v) 	{this.hashmap.put(k, 	new LongRecord(v));	}
	public void putBool(String k,boolean v) {this.hashmap.put(k, 	new BooleanRecord(v));	}	
	public void putFloat(String k,float v)  {this.hashmap.put(k, 	new FloatRecord(v));	}
	
	
	//======================
	public long getLong(String k, long defaultValue) {
	
		final long result;		
		final LongRecord record = (LongRecord) 	this.hashmap.get(k);
		
		if (record != null) {
			result = record.get();
		} else {
			result = defaultValue;			
		}

		return result;		
	}
	
	//======================
	public boolean 	getBoolean(String k, boolean defaultValue) {
	
		final boolean result;
		
		final BooleanRecord record = (BooleanRecord) 	this.hashmap.get(k);
		
		if (record != null) {
			result = record.get();
		} else {
			result = defaultValue;			
		}

		return result;		
	}
	
	//======================
	public float 	getFloat(String k, float defaultValue) {
	
		final float result;
		
		final FloatRecord record = (FloatRecord) 	this.hashmap.get(k);
		
		if (record != null) {
			result = record.get();
		} else {
			result = defaultValue;			
		}

		return result;		
	}	
	

}
