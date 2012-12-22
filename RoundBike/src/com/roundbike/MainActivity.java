package com.roundbike;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.rounbike.db.DBBicicleteriasAdapter;
import com.roundbike.entities.Bicicleterias;
import com.roundbike.extras.PreRes;

public class MainActivity extends MapActivity {
	
	private LocationManager locManager;
	private LocationListener locationListener;
	
	private MapView mapa = null;
	private PreRes preres = new PreRes();
	private ArrayList<Bicicleterias> bicicleterias = new ArrayList<Bicicleterias>();
	
	SharedPreferences pref;
	
	String provider = null;
	
	private class MyOverlay extends Overlay 
	{
		GeoPoint point;
		/* El constructor recibe el punto donde se dibujará el marker */
		public MyOverlay(GeoPoint point) {
		  super();
		  this.point = point;
		}
		
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) 
		{
		    super.draw(canvas, mapView, shadow);
		   //se traduce el punto geo localizado a un punto en la pantalla
		   Point scrnPoint = new Point();
		   mapView.getProjection().toPixels(this.point, scrnPoint);
		 
		   //se construye un bitmap a partir de la imagen
		   Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.ic_my_location);
		 
		   //se dibuja la imagen del marker
		   canvas.drawBitmap(marker, scrnPoint.x - marker.getWidth() / 2, scrnPoint.y - marker.getHeight() / 2, null);
	        
		   return true;
		}

		@Override
		public boolean onTap(GeoPoint p, MapView mapView) 
		{
			Location loc1 = new Location("loc1");
				loc1.setLatitude(this.point.getLatitudeE6() / 1E6);
				loc1.setLongitude(this.point.getLongitudeE6() / 1E6);
			Location loc2 = new Location("loca2");
				loc2.setLatitude(p.getLatitudeE6() / 1E6);
				loc2.setLongitude(p.getLongitudeE6() / 1E6);
		    
			//calcular distancia para mostrar dialog de posicion. Solo 
			//se mostrara el dialog si el radio es menor a 80 metros
			if(calcDistance(loc1, loc2) <= 80)
			{
				String address = null;
				address = convertPointToLocation(p);
				if(address == "")
					address = "Dirección no encontrada";
				showDialogMyPosition(address); 
			}
			
			return true;
		}    
		
	}
	
	
	public class MyOverlayItemized extends ItemizedOverlay {
		 
		private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
	 
		private Context context;
	 
		public MyOverlayItemized(Drawable defaultMarker) {
			  super(boundCenterBottom(defaultMarker));
		}
	 
		public MyOverlayItemized(Drawable defaultMarker, Context context) {
			  this(defaultMarker);
			  this.context = context;
		}
	 
		@Override
		protected OverlayItem createItem(int i) {
			return (OverlayItem) mapOverlays.get(i);
		}
	 
		@Override
		public int size() {
			return mapOverlays.size();
		}
	 
		@Override
		protected boolean onTap(int index) {
			OverlayItem item = (OverlayItem) mapOverlays.get(index);
	
			final int bicicleterias_index = Integer.parseInt(item.getSnippet());
			
			// custom dialog
			final Dialog dialog = new Dialog(context);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
			dialog.setContentView(R.layout.custom_dialog);
 
			// set the custom dialog components - text, image and button
			TextView tvNombre = (TextView) dialog.findViewById(R.id.tvNombre);
			TextView tvDomicilio = (TextView) dialog.findViewById(R.id.tvDomicilio);
			TextView tvTelefono = (TextView) dialog.findViewById(R.id.tvTelefono);
			TextView tvServicio = (TextView) dialog.findViewById(R.id.tvServicio);
			TextView tvDistancia = (TextView) dialog.findViewById(R.id.tvDistancia);
			
			pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
			
			tvNombre.setText(bicicleterias.get(bicicleterias_index).getNombre());
			tvDomicilio.setText(bicicleterias.get(bicicleterias_index).getDomicilio());
			tvTelefono.setText("Tel. " +  bicicleterias.get(bicicleterias_index).getTelefono());
			tvServicio.setText("Servicio: " + bicicleterias.get(bicicleterias_index).getServicioDescripcion());
			tvDistancia.setText("Distancia Aprox. " + preres.getOutDistance(bicicleterias.get(bicicleterias_index).getDistance(), pref));
			
			
			
			
			/*ImageView image = (ImageView) dialog.findViewById(R.id.image);
			image.setImageResource(R.drawable.ic_launcher);*/
 
			Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonClose);
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {    
					dialog.dismiss();
				}
			});
			
			Button dialogButtonReservar = (Button) dialog.findViewById(R.id.dialogButtonReservar);
			dialogButtonReservar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(bicicleterias.get(bicicleterias_index).getEmail().trim().length() > 0){
						Intent ir_a_reservas = new Intent("com.roundbike.RESERVAS");
						Bundle bolsa = new Bundle();
						bolsa.putInt("ID", bicicleterias.get(bicicleterias_index).get_id());
						ir_a_reservas.putExtras(bolsa);
						startActivity(ir_a_reservas);  
					}else{
						dialog.dismiss();
						Toast.makeText(MainActivity.this, "No existe email de contacto!", Toast.LENGTH_SHORT).show();
					}
				}              
			});
 
			dialog.show();
						
			return true;
		}
	 
		public void addOverlay(OverlayItem overlay) {
			mapOverlays.add(overlay);
		    this.populate();
		}
	 
	}
	
	private class GPSLocationListener implements LocationListener 
	{

		@Override
		public void onLocationChanged(Location location) {
			updateLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			//Intent intent = new Intent( android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		    //startActivity(intent);
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		} 

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		
		}
	  
	}
	  
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setTitle("Bicicleterias");
        actionBar.setHomeAction(new IntentAction(this, new Intent("com.roundbike.MAIN"), R.drawable.ic_title_home_default));
        actionBar.addAction(new IntentAction(this, goListActivity(), R.drawable.ic_action_list));
        actionBar.addAction(new IntentAction(this, new Intent("com.roundbike.PREFERENCIAS"), R.drawable.ic_action_preferences));
        
        //Obtenemos una referencia al control MapView
        mapa = (MapView)findViewById(R.id.mapa);
        //Mostramos los controles de zoom sobre el mapa
        mapa.setBuiltInZoomControls(true);
        
        beginLocation();
        
    }
	
    @Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
    
	private void beginLocation()
	{
	    //Obtenemos una referencia al LocationManager
	    locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        
	    //Obtenemos mejor proveedor de geolocalizacion
	    provider = preres.getBestProvider(locManager);
	    
	    if(provider != null){
	    	
	    	//Obtenemos la última posición conocida
		    Location loc = locManager.getLastKnownLocation(provider);
		    
		    locationListener = new GPSLocationListener();
		    
		    //Mostramos la última posición conocida  
		    updateLocation(loc);
		    showBicicleteriasInMap(loc);
		 
		    locManager.requestLocationUpdates(provider, 6000, 50, locationListener);
	    }else{
	    	Toast.makeText(this, "No hay proveedor de geolocalización disponible en tu dispositivo", Toast.LENGTH_LONG).show(); 
	    }
	    
	    Log.d("PROVEEDOR", provider);
        
	}
	
	public void showPosition(Location loc)
	{
		if(loc != null)
	    {
	        //lblLatitud.setText("Latitud: " + String.valueOf(loc.getLatitude()));
	        //lblLongitud.setText("Longitud: " + String.valueOf(loc.getLongitude()));
	        //lblPresicion.setText("Precision: " + String.valueOf(loc.getAccuracy()));
	        Log.i("LocAndroid", String.valueOf(
	                loc.getLatitude() + " - " + String.valueOf(loc.getLongitude())));
	    }
	    else
	    {
	        //lblLatitud.setText("Latitud: (sin_datos)");
	        //lblLongitud.setText("Longitud: (sin_datos)");
	        //lblPresicion.setText("Precision: (sin_datos)");
	    }
	}
	
	
	protected void updateLocation(Location location)
	{
		if (location != null) {
			MapView mapView = (MapView) findViewById(R.id.mapa);
			MapController mapController = mapView.getController();
			GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
			mapController.animateTo(point);
			//mapController.setZoom(16);
			centerMap(point);
			
			showBicicleteriasInMap(location);
			
		    Toast. makeText(this, "Mi ubicación: " + convertPointToLocation(point), Toast.LENGTH_SHORT).show();
		    //String address = convertPointToLocation(point);
		    //Toast.makeText(this, address, Toast.LENGTH_LONG).show();
			
			List<Overlay> mapOverlays = mapView.getOverlays();
			
			MyOverlay marker = new MyOverlay(point);
			mapOverlays.add(marker);
			
			
			/*Drawable drawable = this.getResources().getDrawable(R.drawable.mylocation);
			MyOverlayItemized itemizedOverlay = new MyOverlayItemized(drawable, this);
			OverlayItem overlayitem = new OverlayItem(point, "Hola",
					"!Te saludamos desde Ciudad de México!");
			itemizedOverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedOverlay);  
			mapController.animateTo(point);
			mapController.setZoom(13);*/
			mapView.postInvalidate();
		}
	}
	
	
	public String convertPointToLocation(GeoPoint point) {   
	    String address = "";
	    Geocoder geoCoder = new Geocoder(
	        this, Locale.getDefault());
	    try {
	      List<Address> addresses = geoCoder.getFromLocation(
	        point.getLatitudeE6()  / 1E6, 
	        point.getLongitudeE6() / 1E6, 1);      
	 
	      if (addresses.size() > 0) {
	        for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
	        		address += addresses.get(0).getAddressLine(index) + " ";
	      }
	    }
	    catch (IOException e) {        
	      e.printStackTrace();
	    }   
	    
	    return address;
	} 
	
	public void showBicicleteriasInMap(Location myPosition)
	{
		DBBicicleteriasAdapter dbBiciAdapter = new DBBicicleteriasAdapter(this); 
		
		Bicicleterias b = new Bicicleterias();
		pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		b.setServicio(preres.getPreferenceServicio(pref));
		bicicleterias = dbBiciAdapter.getAll(b);
		
		MapView mapView = (MapView) findViewById(R.id.mapa);
		MapController mapController = mapView.getController();
		List<Overlay> mapOverlays = mapView.getOverlays();
		
		for(int i = 0; i<bicicleterias.size(); i++){
			if(bicicleterias.get(i).getLat() != null && bicicleterias.get(i).getLon() != null)
			{
				Drawable drawable = null;    
				
				//creamos nueva ubicacion para calcular distancia entre mi posicion y un punto de referencia
				Location loc = new Location("reverseGeocoded");
				loc.setLatitude(Double.parseDouble(bicicleterias.get(i).getLat()));
				loc.setLongitude(Double.parseDouble(bicicleterias.get(i).getLon()));
				
				bicicleterias.get(i).setDistance(calcDistance(myPosition, loc));
				
				if(preres.checkDistance(bicicleterias.get(i).getDistance(), pref))
				{
					//mostramos un estilo de marcador segun el tipo de servicio
					if(bicicleterias.get(i).getServicio() == 1)
						 drawable = this.getResources().getDrawable(R.drawable.ic_item_location);
					if(bicicleterias.get(i).getServicio() == 2)
						drawable = this.getResources().getDrawable(R.drawable.ic_item_location_pago); 
					
					MyOverlayItemized itemizedOverlay = new MyOverlayItemized(drawable, this);
			 
					GeoPoint point = new GeoPoint((int)(Double.parseDouble(bicicleterias.get(i).getLat()) * 1E6), (int)(Double.parseDouble(bicicleterias.get(i).getLon()) * 1E6));
					
					OverlayItem overlayitem = new OverlayItem(point, bicicleterias.get(i).getNombre(), String.valueOf(i)); 
			 
					itemizedOverlay.addOverlay(overlayitem);
					mapOverlays.add(itemizedOverlay);  
					//mapController.animateTo(point);
					//mapController.setZoom(13);
				}
				
				//Log.d("COORDENADAS", "Lat: " + bicicleterias.get(i).getLat() + " Lon: " + bicicleterias.get(i).getLon());
			}
		}
		mapView.invalidate();
		
	}
	
	
	public int calcDistance(Location myPosition, Location loc2)
	{
		int distance = 0;
		if(myPosition != null){
			distance = (int)myPosition.distanceTo(loc2);  // Value = 12637795 ???
			//str = "Distancia Aprox. " + String.valueOf(preres.roundDouble(preres.getDistanceInKm(distance))) + " km.";
		}
        return distance; 
	}
	
	
	public Intent goListActivity()
	{
		Intent ir_a_lista = new Intent("com.roundbike.LISTA");	
		return ir_a_lista;
	}
	
	
	public void centerMap(GeoPoint point)
	{
		//Double latitud = -34.5973*1E6;
        //Double longitud = -58.382*1E6;
        
        MapController controlMapa = mapa.getController();
        
        //GeoPoint loc = new GeoPoint(latitud.intValue(), longitud.intValue());
 
        controlMapa.setCenter(point);
        controlMapa.setZoom(14);
	}
	
	public void showDialogMyPosition(String address)
	{
		// custom dialog
		final Dialog dialog_my_position = new Dialog(this);
		dialog_my_position.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		dialog_my_position.setContentView(R.layout.custom_dialog_my_position);
 
		// set the custom dialog components - text, image and button
		TextView tvAddress = (TextView) dialog_my_position.findViewById(R.id.tvAddress);
		
		pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		
		tvAddress.setText(address);

		/*ImageView image = (ImageView) dialog.findViewById(R.id.image);
		image.setImageResource(R.drawable.ic_launcher);*/
 
		Button dialogButton = (Button) dialog_my_position.findViewById(R.id.dialogBtnMyPositionClose);
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog_my_position.dismiss();
			}
		});
 
		dialog_my_position.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{	
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item)
	{	
		switch(item.getItemId()){
			case R.id.menu_about:
				Intent ir_a_acerca_de = new Intent(MainActivity.this,AboutActivity.class);
				startActivity(ir_a_acerca_de);
				return true;
			default:
	            return super.onOptionsItemSelected(item);
		}
	}

}
