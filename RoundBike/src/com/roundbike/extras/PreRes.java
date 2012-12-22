package com.roundbike.extras;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.LocationManager;
import android.util.Log;

public class PreRes {
	
	private Context context;
	
	public void PreRes(Context ctx)
	{
		this.context = ctx;
	}
	
	public boolean checkLocation(LocationManager locManager)
	{
		//Si el GPS no está habilitado
		if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
		     return false;
		}else{
			return true;
		}
	}
	
	public Double getDistanceInKm(int metros)
	{
		Double result = 0.0;
		result = metros * 0.001;
		//Log.d("KM", String.valueOf(result));
		return result;
	}
	
	public String roundDouble(Double num){
		 DecimalFormat df = new DecimalFormat("#.#");
	     return df.format(num);
	}
	
	public String getFilters(ArrayList<String> filters)
	{
		String filter_str = "";
		int cant = (filters.size() - 1);
		
		if(filters.size() > 0){
			for(int i = 0; i < filters.size(); i++){
				/*if(i == cant){
					filter_str+= filters.get(i);
				}else{*/
					filter_str+= filters.get(i) + " AND ";
				//}
			}
		}
		
		return filter_str;
		
	}
	
	
	public int getPreferenceServicio(SharedPreferences pref)
	{
		int value = 0;
		int servicio = Integer.parseInt(pref.getString("arrServicio", "3"));
		
		if(servicio == 3){
			value = 0;
		}else if(servicio == 2){
			value = 2;
		}else if(servicio == 1){
			value = 1;
		}
		//Log.d("Preferencia servicio: ", pref.getString("arrServicio", "3"));
		return value;
	}
	
	
	public String getOutDistance(int distance, SharedPreferences pref)
	{
		int unidad = Integer.parseInt(pref.getString("arrDistance", "1"));
		String salida = "";
		if(unidad == 1){
			salida = String.valueOf(roundDouble(getDistanceInKm(distance))) + " km.";
		}else if(unidad == 2){
			salida = String.valueOf(distance) + " mts.";
		}
		return salida;
	}
	
	public boolean checkDistance(int distance, SharedPreferences pref)
	{
		double distancia_2 = Double.parseDouble(pref.getString("radiobusqueda", "20"));
		double distancia_1 = getDistanceInKm(distance);
		boolean estado = false;
		
		if(distancia_1 < distancia_2)
			estado = true;
		return estado;
		
	}
	
	public String getBestProvider(LocationManager myLocationManager)
	{
		Criteria criteria = new Criteria(); 
		criteria.setAccuracy(Criteria.ACCURACY_FINE); 
		criteria.setAltitudeRequired(true); 
		criteria.setBearingRequired(false); 
		criteria.setCostAllowed(true); 
		criteria.setPowerRequirement(Criteria.POWER_LOW); 

		String provider = myLocationManager.getBestProvider(criteria,true); 
		return provider;
	}
}
