package fr.mydedibox.libarcade.activity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.greatlittleapps.utility.HTTPDownload;
import com.greatlittleapps.utility.Utility;
import com.greatlittleapps.utility.UtilityMessage;

import fr.mydedibox.libarcade.R;

import fr.mydedibox.libarcade.CompatibilityList;
import fr.mydedibox.libarcade.fragments.romDetailFragment;
import fr.mydedibox.libarcade.fragments.romListFragment;
import fr.mydedibox.libarcade.objects.RomInfo;
import fr.mydedibox.libarcade.preferences.EmuPreferences;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewConfiguration;
import android.widget.Toast;

public class romListActivity extends ActionBarActivity implements romListFragment.Callbacks
{
	public UtilityMessage message;
    public static CompatibilityList compatList;
	public EmuPreferences prefs;
	
	public boolean quit = false;
	private boolean mTwoPane;
	private String selectedRomID = null;
	private boolean paused = false;
	private romDetailFragment fragment;
	private romListFragment romListFragment;
	private Toast toast;
	private long lastBackPressTime = 0;
	private boolean toastShowed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_rom_list );

        // force overflow menu
        try 
        {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField( "sHasPermanentMenuKey" );
            if( menuKeyField != null )
            {
                menuKeyField.setAccessible( true );
                menuKeyField.setBoolean( config, false );
            }
        } 
        catch (Exception ex){}
       
        romListFragment = (romListFragment)getSupportFragmentManager().findFragmentById( R.id.rom_list);
        if ( findViewById( R.id.rom_detail_container ) != null ) 
        {
            mTwoPane = true;
            romListFragment.setActivateOnItemClick( true );
        }
    }
    
    public void Init( String dataName, ArrayList<RomInfo> romsList )
    {
    	message = new UtilityMessage( romListActivity.this );
        prefs = new EmuPreferences( this );
        
    	compatList = new CompatibilityList( romsList );
    	EmuPreferences.DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dataName;
    	EmuPreferences.ROM_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    	EmuPreferences.ROMINFO_PATH = EmuPreferences.DATA_PATH + "/rominfo";
    	EmuPreferences.TITLES_PATH = EmuPreferences.DATA_PATH + "/titles";
    	EmuPreferences.PREVIEWS_PATH = EmuPreferences.DATA_PATH + "/previews";
    	EmuPreferences.ICONS_PATH = EmuPreferences.DATA_PATH + "/icons";
    	EmuPreferences.STATE_PATH = EmuPreferences.DATA_PATH + "/states";
    	new File( EmuPreferences.ROMINFO_PATH ).mkdirs();
    	new File( EmuPreferences.ROM_PATH ).mkdirs();
    	new File( EmuPreferences.ROMINFO_PATH ).mkdirs();
    	new File( EmuPreferences.TITLES_PATH ).mkdirs();
    	new File( EmuPreferences.PREVIEWS_PATH ).mkdirs();
    	new File( EmuPreferences.ICONS_PATH ).mkdirs();
    	new File( EmuPreferences.STATE_PATH ).mkdirs();
    	try 
    	{
			new File( EmuPreferences.ICONS_PATH +"/"+".nomedia" ).createNewFile();
			new File( EmuPreferences.PREVIEWS_PATH +"/"+".nomedia" ).createNewFile();
			new File( EmuPreferences.TITLES_PATH +"/"+".nomedia" ).createNewFile();
		}
    	catch (IOException e) {e.printStackTrace();}
    	
    	if( !prefs.GetDataOk() )
    	{
    		dialogdownloadPreviews();
    	}
    }
    
    boolean doubleBackToExitPressedOnce = false;
    
    @Override
    public void onBackPressed() 
    {
    	if(quit) {
    		return;
    	}
    	if (doubleBackToExitPressedOnce) {
    		exit();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        
        if( !toastShowed ) {
			toast = Toast.makeText( this, "Press back twice to exit", Toast.LENGTH_SHORT );
			toast.show();
			toastShowed = true;
        } else if (toast != null) {
        	toast.cancel();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
                if( !quit && romListFragment != null ) {
                	romListFragment.GetDirBack();
                }
            }
        }, 500 );
    }
  
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    }
    
    @Override 
	public void onPause()
	{
    	Utility.log( "onPause" );
    	paused = true;
    	super.onPause();
	}
    
    @Override 
	public void onResume()
	{
    	Utility.log( "onResume" );
    	
		super.onResume();
		paused = false;
		//backCount = 0;
		
		if( quit ) {
			return;
		}
		
		if( mTwoPane && selectedRomID != null )
			fragmentShow( selectedRomID );
	}
    
    @Override
    public void onConfigurationChanged( Configuration newConfig ) 
    {
    	Utility.log( "onConfigurationChanged" );
    	
    	if( mTwoPane && !paused && selectedRomID != null )
    			fragmentShow( selectedRomID );
   
    	super.onConfigurationChanged( newConfig );
    }
    
    @Override
    public void onItemSelected( String id ) 
    {
    	Utility.log( "mTwoPane="+mTwoPane );
    	
        if( mTwoPane ) 
        {
        	if( id == null )
        	{
        		this.fragmentDelete();
        		return;
        	}
        	fragmentShow( id );
        } 
        else 
        {
        	if( id == null )
        		return;
        	
            Intent detailIntent = new Intent( this, romDetailActivity.class );
            detailIntent.putExtra( romDetailFragment.ARG_FILE_PATH, id );
            startActivity( detailIntent );
        }
    }
    
    void fragmentShow( String id )
    {
    	fragmentDelete();
    	selectedRomID = id;
        Bundle arguments = new Bundle();
        arguments.putString( romDetailFragment.ARG_FILE_PATH, id );
        fragment = new romDetailFragment();
        fragment.setArguments( arguments );
        getSupportFragmentManager().beginTransaction().replace( R.id.rom_detail_container, fragment ).commit();
    }
    
    void fragmentDelete()
    {
    	if( fragment != null )
    		getSupportFragmentManager().beginTransaction().remove( fragment ).commit();
    }
    
    public void dialogdownloadPreviews()
	{
    	prefs.SetDataOk( true );
    	
    	new AlertDialog.Builder( romListActivity.this )
			.setTitle( "Confirm" )
			.setCancelable( false )
			.setMessage( "\nTo improve the experience, you can download an extra package " +
					"which provide screenshots for your games.\nYou may use the menu to download it later...\n\n" )
			.setPositiveButton( "Download", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					downloadPreviews();
				}
			})
			.setNegativeButton( "Cancel", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton){}
			}).create().show();
	} 
    
    private void downloadPreviews()
    {
		HTTPDownload d = new HTTPDownload( EmuPreferences.DATA_URL, EmuPreferences.DATA_PATH + "/data.zip" )
		{
			@Override
			public void onProgress( int current, int total )
			{
				message.show( "Downloading ... "+Utility.formatFileSize(current)+" / "+Utility.formatFileSize(total)+"\n", current, total );
			}
			
			@Override
			public void onTerminate( boolean success )
			{
				message.hide();

				if( !success )
					message.showMessageError( "Sorry, an error occured while downloading data, you can try again from the menu..." );
			}
		};
		d.download();
    }
    
    public void exit() {
    	
    	quit = true;
    	
    	new AlertDialog.Builder( romListActivity.this )
		.setTitle( "Exit ?" )
		.setCancelable( false )
		.setMessage( "\nDo you want to exit the application ?\n\n" )
		.setPositiveButton( "Exit", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				romListActivity.this.finish();
			}
		})
		.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				quit = false;
			}
		}).create().show();
    }
}
