package skylight1.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;



import android.content.Context;
import android.util.Log;

public class Chest {
		
	private static final int CURRENT_FORMAT_VERSION = 1;
	private Context context;

	private boolean somethingWasLoaded;
	private Map<String, Record> hashmap;		
	
	//===========================	
	public interface Record {
		
		enum Type {
		    LONG, BOOLEAN, FLOAT;

			public static Type valueOf(int i) {
				
				for (Type type : Type.values()) {
					if (i == type.ordinal() ) { return type; }					
				}

				throw new IllegalArgumentException(String.format("Invalid type: '%d'", i));
			} 
		}		
		
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
			dos.writeInt(Record.Type.LONG.ordinal());			
			dos.writeLong(v);			
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
			dos.writeInt(Record.Type.BOOLEAN.ordinal());
			dos.writeBoolean(v);		
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
			dos.writeInt(Record.Type.FLOAT.ordinal());
			dos.writeFloat(v);			
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
		this.hashmap = new HashMap<String, Record>();		
	}

	/**
	 * Stores the data that was put into a file.  
	 *
	 * @param  filename
	 */	
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

	

	/**
	 * Loads data from the given file.
	 *
	 * @param  filename
	 */	
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
	            		    	  final Record.Type type =  Record.Type.valueOf(dis.readInt());	 
	            		    	  
	            		    	  final Record record;	            		    	  
	            		    	  
	            		    	  switch (type) {	            		    	  	
	            		    	  	case BOOLEAN:  	record = new BooleanRecord(dis);  	break;
	            		    	  	case FLOAT:  	record = new FloatRecord(dis);  	break;
	            		    	  	case LONG:  	record = new LongRecord(dis);  		break;
	            		    	  	
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
	
	
	
	//========================		
	private void writeData(DataOutputStream dos) throws IOException {
		  
		final Map<String, Record> hm = this.hashmap;		
		final Iterator<Entry<String, Record>> iterator = hm.entrySet().iterator();
		
		while (iterator.hasNext()) {			
			final Entry<String,Record> entry = iterator.next();
			entry.getValue().write(entry.getKey(), dos);
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
