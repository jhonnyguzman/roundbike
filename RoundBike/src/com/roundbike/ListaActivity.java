package com.roundbike;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.rounbike.db.DBBicicleteriasAdapter;
import com.roundbike.entities.Bicicleterias;
import com.roundbike.extras.PreRes;

public class ListaActivity extends Activity{
	
	private ListView lvLista;
	private ArrayList<Bicicleterias> bicicleterias = new ArrayList<Bicicleterias>();
	private AdaptadorLista adaptador;
	private LocationManager locManager;
	private LocationListener locationListener;
	private PreRes preres = new PreRes();
	private SharedPreferences pref;
	String provider = null;
	
	static class ViewHolder {
        TextView lblNombre;
        TextView lblDomicilio;
        TextView lblDistancia;
        TextView lblTelefono;
        TextView lblServicio;
    }
	
	private class GPSLocationListener implements LocationListener 
	{

		@Override
		public void onLocationChanged(Location location) {
			sortListaByDistance(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		
		}
	  
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_activity);
		
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setTitle("Bicicleterias");
        actionBar.setHomeAction(new IntentAction(this, new Intent("com.roundbike.MAIN"), R.drawable.ic_title_home_default));
        
		lvLista = (ListView) findViewById(R.id.lvLista);
		
		showBicicleterias();
		prepareLocation();
		
		lvLista.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) 
			{ 
				
				if(bicicleterias.get(position).getEmail().trim().length() > 0){
					Log.d("EMAIL", bicicleterias.get(position).getEmail().toString());
					Intent ir_a_reservas = new Intent("com.roundbike.RESERVAS");
					Bundle bolsa = new Bundle();
					bolsa.putInt("ID", bicicleterias.get(position).get_id());
					ir_a_reservas.putExtras(bolsa);
					startActivity(ir_a_reservas);
				}else{
					Toast.makeText(ListaActivity.this, "No existe email de contacto!", Toast.LENGTH_SHORT).show();
				}
			}
	 		 
	 	 });
	}
	
	public void prepareLocation()
	{
		//Obtenemos una referencia al LocationManager
	    locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        
	    //Obtenemos mejor proveedor de geolocalizacion
	    provider = preres.getBestProvider(locManager);
	    
	    if(provider != null)
	    {	
	    	//Obtenemos la última posición conocida
		    Location loc = locManager.getLastKnownLocation(provider);
		    
		    locationListener = new GPSLocationListener();
		    
		    //Mostramos la última posición conocida  
		    sortListaByDistance(loc);
		    
		    locManager.requestLocationUpdates(provider, 6000, 50, locationListener);
	    }else{
	    	Toast.makeText(this, "No hay proveedor de geolocalización disponible en tu dispositivo", Toast.LENGTH_LONG).show(); 
	    }
	}
	
	public void showBicicleterias()
	{
		DBBicicleteriasAdapter dbBiciAdapter = new DBBicicleteriasAdapter(this);
		
		pref = PreferenceManager.getDefaultSharedPreferences(ListaActivity.this);
		Bicicleterias b = new Bicicleterias();
		b.setServicio(preres.getPreferenceServicio(pref));
		
		bicicleterias = dbBiciAdapter.getAll(b);
		adaptador =  new AdaptadorLista(this);
	 	lvLista.setAdapter(adaptador);
		
	}
	
	class AdaptadorLista extends ArrayAdapter<Bicicleterias> {
    	
    	Activity context;
    	
    	AdaptadorLista(Activity context) {
    		super(context, R.layout.listitem_lista, bicicleterias);
    		this.context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
			
    		View item = convertView;
		    ViewHolder holder;
		    pref = PreferenceManager.getDefaultSharedPreferences(ListaActivity.this);
		    
		    if(item == null)
		    {
	    		LayoutInflater inflater = context.getLayoutInflater();
				item = inflater.inflate(R.layout.listitem_lista, null);
				
				 holder = new ViewHolder();
				 holder.lblNombre = (TextView)item.findViewById(R.id.lblNombre);
				 holder.lblDomicilio = (TextView)item.findViewById(R.id.lblDomicilio);
				 holder.lblDistancia = (TextView)item.findViewById(R.id.lblDistancia);
				 holder.lblTelefono = (TextView)item.findViewById(R.id.lblTelefono);
				 holder.lblServicio = (TextView)item.findViewById(R.id.lblServicio);
				 item.setTag(holder);
		    }else{
		    	 holder = (ViewHolder)item.getTag();
		    }
		
	    
	    	holder.lblNombre.setText(bicicleterias.get(position).getNombre());
	    	holder.lblDomicilio.setText(bicicleterias.get(position).getDomicilio());
	    	holder.lblDistancia.setText(preres.getOutDistance(bicicleterias.get(position).getDistance(), pref));
	    	holder.lblTelefono.setText("Tel. " + bicicleterias.get(position).getTelefono());
	    	holder.lblServicio.setText(bicicleterias.get(position).getServicioDescripcion());
		    
			//si es true colocamos un background en el item del listview
			/*if(pos == position) {
				item.setBackgroundColor(Color.argb(200, 49, 176, 17));
			}else{
				item.setBackgroundColor(Color.argb(0, 0, 0, 0));
			}*/
			
			return(item);
		}
    }
	
	private class comparatorDistance implements Comparator<Bicicleterias>{

		@Override
		public int compare(Bicicleterias b1, Bicicleterias b2) {
			return b1.getDistance() - b2.getDistance();
		}
	}
	
	public void sortListaByDistance(Location myPosition)
	{
		ArrayList<Bicicleterias> b = new ArrayList<Bicicleterias>();
		if (myPosition != null) {
			
			for(int i = 0; i<bicicleterias.size(); i++){
				if(bicicleterias.get(i).getLat() != null && bicicleterias.get(i).getLon() != null)
				{
					Location loc = new Location("reverseGeocoded");
					loc.setLatitude(Double.parseDouble(bicicleterias.get(i).getLat()));
					loc.setLongitude(Double.parseDouble(bicicleterias.get(i).getLon()));
					bicicleterias.get(i).setDistance(getCalcDistance(myPosition,loc));
					
					//Log.d("DISTANCIA", String.valueOf(bicicleterias.get(i).getDistance()));
				}
			}
			
			Collections.sort(bicicleterias, new comparatorDistance());
			
			for(int i=0; i<bicicleterias.size(); i++){
				b.add(bicicleterias.get(i));
			}
			
			bicicleterias.clear();
			pref = PreferenceManager.getDefaultSharedPreferences(ListaActivity.this);
			
			for(int i=0; i < b.size(); i++){
			    if(preres.checkDistance(b.get(i).getDistance(), pref)) 
			    {
			    	//Log.d("ELIMINAR ITEM",String.valueOf(b.get(i).getDistance()));
			    	//bicicleterias.remove(i);
			    	bicicleterias.add(b.get(i));
			    }
			}
			
			adaptador.notifyDataSetChanged();

		}
	}
	
	public int getCalcDistance(Location myPosition, Location loc2)
	{
		int distance = 0;
		if(myPosition != null){
			distance = (int)myPosition.distanceTo(loc2);  // Value = 12637795 ???
		}
        return distance; 
	}
}
