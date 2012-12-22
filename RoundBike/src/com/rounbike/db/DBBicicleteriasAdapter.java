package com.rounbike.db;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.roundbike.entities.Bicicleterias;
import com.roundbike.extras.PreRes;


public class DBBicicleteriasAdapter {
	
	private static final String DATABASE_TABLE = "bicicleterias"; 
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	private PreRes preres = new PreRes();
	
	Context contexto;
	
	public DBBicicleteriasAdapter(Context ctx)
	{
		this.contexto = ctx;
		this.myDbHelper = new DataBaseHelper(this.contexto);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	
	}
	
	
	public boolean add(Bicicleterias bicicleteria)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		valores.put("_id", bicicleteria.get_id());
		valores.put("nombre", bicicleteria.getNombre());
		valores.put("descripcion", bicicleteria.getDescripcion());
		valores.put("domicilio", bicicleteria.getDomicilio());
		valores.put("telefono", bicicleteria.getTelefono());
		valores.put("email", bicicleteria.getEmail());
		valores.put("sitioweb", bicicleteria.getSitioweb());
		valores.put("lat", bicicleteria.getLat());
		valores.put("lon", bicicleteria.getLon());
		valores.put("estado", bicicleteria.getEstado());
		valores.put("servicio", bicicleteria.getServicio());
		valores.put("created_at", dateFormat.format(date));
		valores.put("updated_at", dateFormat.format(date));
	
		
		long rowsid = db.insert(DATABASE_TABLE, null, valores);
		if(rowsid != -1){
			estado =  true;
		}else estado = false;
		
		this.myDbHelper.close();
		return estado;
	}
	
	
	public boolean edit(Bicicleterias bicicleteria)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		String sql = "UPDATE "+ DATABASE_TABLE +" SET ";
		
		if(bicicleteria.getNombre() != null){
			valores.put("nombre", bicicleteria.getNombre());
		}
		if(bicicleteria.getDescripcion() != null){
			valores.put("descripcion", bicicleteria.getDescripcion());
		}
		if(bicicleteria.getDomicilio() != null){
			valores.put("domicilio", bicicleteria.getDomicilio());
		}
		if(bicicleteria.getTelefono() != null){
			valores.put("telefono", bicicleteria.getTelefono());
		}
		if(bicicleteria.getEmail() != null){
			valores.put("email", bicicleteria.getEmail());
		}
		if(bicicleteria.getSitioweb() != null){
			valores.put("sitioweb", bicicleteria.getSitioweb());
		}
		if(bicicleteria.getLat() != null){
			valores.put("lat", bicicleteria.getLat());
		}
		if(bicicleteria.getLon() != null){
			valores.put("lon", bicicleteria.getLon());
		}
		if(bicicleteria.getEstado() != 0){
			valores.put("estado", bicicleteria.getEstado());
		}
		if(bicicleteria.getServicio() != 0){
			valores.put("servicio", bicicleteria.getServicio());
		}
		
		valores.put("updated_at", dateFormat.format(date));
		

		
		String where = "_id = " + bicicleteria.get_id();
		
		int affectedrows = db.update(DATABASE_TABLE, valores, where,null);
		
		if(affectedrows == 1){
			estado = true;
		}else estado = false;
		
		myDbHelper.close();
		return estado;
	}
	
	public ArrayList<Bicicleterias> getAll(Bicicleterias b)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Bicicleterias> lista = new ArrayList<Bicicleterias>();
		ArrayList<String> filters = new ArrayList<String>();
		
		if(b.get_id() != 0)
			filters.add("_id = " + b.get_id() + ""); 
		if(b.getServicio() != 0)
			filters.add("servicio = " + b.getServicio() ); 
		
		Cursor c = db.rawQuery("SELECT b.* " +
				"FROM bicicleterias as b WHERE " + preres.getFilters(filters) + " b.lat <> '' AND b.lon <> '' ORDER BY b.created_at ",null);
		
		if (c.moveToFirst()) {      
		     do {
		    	 Bicicleterias bicicleteria = new Bicicleterias();
		 		
		    	 bicicleteria.set_id(c.getInt(0));
		    	 bicicleteria.setNombre(c.getString(1));
		    	 bicicleteria.setDescripcion(c.getString(2));
		    	 bicicleteria.setDomicilio(c.getString(3));
		    	 bicicleteria.setTelefono(c.getString(4));
		    	 bicicleteria.setEmail(c.getString(5));
		    	 bicicleteria.setSitioweb(c.getString(6));
		    	 bicicleteria.setLat(c.getString(7));
		    	 bicicleteria.setLon(c.getString(8));
		    	 bicicleteria.setEstado(c.getInt(9));
		    	 bicicleteria.setServicio(c.getInt(10));
		    	 bicicleteria.setCreated_at(c.getString(11));
		    	 bicicleteria.setUpdated_at(c.getString(12));
		    	  	
		    	 lista.add(bicicleteria);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Bicicleterias en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return lista;
	}
	
	
	public Bicicleterias getById(int _id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		 Bicicleterias bicicleteria = new Bicicleterias();

		Cursor c = db.rawQuery("SELECT b.* " +
				"FROM bicicleterias as b WHERE _id = "  + _id + " AND b.lat <> '' AND b.lon <> '' ORDER BY b.created_at ",null);
		
		if (c.moveToFirst()) {      
		    	 bicicleteria.set_id(c.getInt(0));
		    	 bicicleteria.setNombre(c.getString(1));
		    	 bicicleteria.setDescripcion(c.getString(2));
		    	 bicicleteria.setDomicilio(c.getString(3));
		    	 bicicleteria.setTelefono(c.getString(4));
		    	 bicicleteria.setEmail(c.getString(5));
		    	 bicicleteria.setSitioweb(c.getString(6));
		    	 bicicleteria.setLat(c.getString(7));
		    	 bicicleteria.setLon(c.getString(8));
		    	 bicicleteria.setEstado(c.getInt(9));
		    	 bicicleteria.setServicio(c.getInt(10));
		    	 bicicleteria.setCreated_at(c.getString(11));
		    	 bicicleteria.setUpdated_at(c.getString(12));
		}else{
			Log.d("REGISTRO_FAIL", "No existe la Bicicleteria en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return bicicleteria;
	}
}
