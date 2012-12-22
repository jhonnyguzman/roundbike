package com.roundbike;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.rounbike.db.DBBicicleteriasAdapter;
import com.roundbike.entities.Bicicleterias;

public class ReservasActivity extends Activity{
	
	private TextView tvNombre, tvDomicilio, tvServicio;
	private EditText etAsunto, etTelefoContacto, etApellNombre, etEmailContacto; 
	private Button btnEnviar;
	
	Bicicleterias bicicleteria = null;
	
	private Bundle bundle = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reservas_activity);
		
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionBarContacto);
        actionBar.setTitle("Contactar");
        actionBar.setHomeAction(new IntentAction(this, new Intent("com.roundbike.MAIN"), R.drawable.ic_title_home_default));
        
        tvNombre = (TextView) findViewById(R.id.tvNombreContacto);
        tvDomicilio = (TextView) findViewById(R.id.tvDomicilioContacto);
        tvServicio = (TextView) findViewById(R.id.tvServicioContacto);
        etTelefoContacto = (EditText) findViewById(R.id.etTelefonoDeContacto);
        etApellNombre = (EditText) findViewById(R.id.etApellNombreDeContacto);
        etEmailContacto = (EditText) findViewById(R.id.etEmailDeContacto);
        etAsunto = (EditText) findViewById(R.id.etAsunto);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        
        bundle = getIntent().getExtras(); 
        showDataBicicleteria();
        
        btnEnviar.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View arg0) {
				sendMail();
			}
        	
        });
	}
	
	public void showDataBicicleteria()
	{
		DBBicicleteriasAdapter dbBiciAdapter = new DBBicicleteriasAdapter(this);
		bicicleteria = dbBiciAdapter.getById(bundle.getInt("ID"));
		
		if(bicicleteria != null)
		{
			tvNombre.setText(bicicleteria.getNombre());
			tvDomicilio.setText(bicicleteria.getDomicilio());
			tvServicio.setText("Servicio: " + bicicleteria.getServicioDescripcion()); 
		}
		
	}
	
	public void sendMail()
	{
		if(etApellNombre.getText().toString().trim().length() == 0){
			Toast.makeText(this, "Ingresa Apellido y Nombre", Toast.LENGTH_SHORT).show();
		}else if(etEmailContacto.getText().toString().trim().length() == 0){
			Toast.makeText(this, "Ingresa Email de contacto", Toast.LENGTH_SHORT).show();
		}else if(etAsunto.getText().toString().trim().length() == 0){
			Toast.makeText(this, "Ingresa Asunto", Toast.LENGTH_SHORT).show();
		}else{
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL  , new String[]{bicicleteria.getEmail()});
			i.putExtra(Intent.EXTRA_SUBJECT, "Nuevo Contacto de alquiler de bicicletas");
			i.putExtra(android.content.Intent.EXTRA_TEXT, getExtraText());
			//i.putExtra(android.content.Intent.EXTRA_TEXT,Html.fromHtml(
			//		"<p><b>Apellido y Nombre: "+etApellNombre.getText()+"</b></p><p><b>Teléfono: "+ etTelefoContacto.getText()+"</b><p><p><b>Email: " + etEmailContacto.getText() + "</b></p><p><b>Asunto:</b><br>" + etAsunto.getText() + "</p><br>"));
			
			try {
			    startActivity(Intent.createChooser(i, "Envío de Email"));
			} catch (android.content.ActivityNotFoundException ex) {
			    Toast.makeText(this, "No hay cliente de correo electrónico instalado en tu dispositivo", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public String getExtraText()
	{
		String text = "";
		text = "Apellido y Nombre: " + etApellNombre.getText() + "\nTeléfono: " + etTelefoContacto.getText()+"\nEmail: " + etEmailContacto.getText() + "\nAsunto:\n " + etAsunto.getText();
		return text;
	}

}
