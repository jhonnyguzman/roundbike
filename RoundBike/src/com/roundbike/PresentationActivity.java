package com.roundbike;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class PresentationActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.presentation_activity);
		
		//ir a actividad principal
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		  @Override
		  public void run() {
			  Intent ir_a_main = new Intent("com.roundbike.MAIN");
			  startActivity(ir_a_main);
		  }
		}, 300);
		
	}
	
	

}
