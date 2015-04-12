package fr.mydedibox.libarcade.emulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.greatlittleapps.utility.Utility;

import fr.mydedibox.libarcade.R;
import fr.mydedibox.libarcade.emulator.utility.EmuPreferences;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Licence 
{
	public static void showLicenceDialog( final Context pCtx, final EmuPreferences pPrefs )
	{
		if( pPrefs != null )
		{
			if( pPrefs.licenceRead() )
				return;
			
			pPrefs.setLicenceRead( true );
		}
		
		
		 //set up dialog
        final Dialog dialog = new Dialog(pCtx);
        dialog.setContentView(R.layout.notice);
        dialog.setTitle( "About" );
        dialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        //set up text
        TextView text = (TextView) dialog.findViewById(R.id.TextView01);
        text.setText( getLicence( pCtx ) );
/*
        //set up image view
        ImageView img = (ImageView) dialog.findViewById(R.id.ImageView01);
        img.setImageResource(R.drawable.icon);
*/
        //set up button
        Button button = (Button) dialog.findViewById(R.id.Button01);
        button.setOnClickListener(new OnClickListener() 
        {
        	@Override
            public void onClick(View v) 
        	{
        		dialog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it    
        dialog.show();
	}
	
	public static String getLicence( final Context pCtx )
	{
		InputStream inputStream = pCtx.getResources().openRawResource( R.raw.copying );
		InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line = "";
        StringBuilder text = new StringBuilder();
        
        text.append( "neodroid is based on GnGeo, which was released under the GNU GPL licence. " +
        		"GnGeo android source code can be obtained on demand by sending a mail to cpasjuste@gmail.com. You can read the GNU GPL licence terms below.\n\n\n" );
        
        try
        {
        	while (( line = buffreader.readLine()) != null)
        	{
        		text.append(line);
        		text.append('\n');
        	}
        } 
        catch (IOException e) 
        {
        	Utility.loge( e.toString() );
        	return "";
        }
        return text.toString();
	}
}
