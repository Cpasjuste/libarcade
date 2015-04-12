package fr.mydedibox.libarcade.emulator.activity;

import java.lang.reflect.Field;

import com.greatlittleapps.utility.Utility;
import com.greatlittleapps.utility.UtilityMessage;

import fr.mydedibox.libarcade.R;
import fr.mydedibox.libarcade.emulator.StateAdapter;
import fr.mydedibox.libarcade.emulator.StateInfo;
import fr.mydedibox.libarcade.emulator.StateList;
import fr.mydedibox.libarcade.emulator.effects.Effect;
import fr.mydedibox.libarcade.emulator.effects.EffectList;
import fr.mydedibox.libarcade.emulator.input.HardwareInput;
import fr.mydedibox.libarcade.emulator.input.IButtons;
import fr.mydedibox.libarcade.emulator.input.SoftwareInputView;
import fr.mydedibox.libarcade.emulator.sdl.SDLAudio;
import fr.mydedibox.libarcade.emulator.sdl.SDLJni;
import fr.mydedibox.libarcade.emulator.sdl.SDLSurface;
import fr.mydedibox.libarcade.emulator.utility.EmuPreferences;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.*;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.os.*;

/**
    SDL Activity
*/
public class Main extends ActionBarListActivity implements OnKeyListener
{
    public static Main activity;
    
    private static UtilityMessage mMessage;
 
    public EmuPreferences mPrefs;
    public Effect effectView;
    public EffectList mEffectList;

    private RelativeLayout mainView;
    private SoftwareInputView inputView;
    private HardwareInput inputHardware;
    public static SDLSurface surfaceView;

    public static int mScreenHolderSizeX = 320;
    public static int buttonCount = 4;
	public static int mScreenHolderSizeY = 240;
	public static int mScreenEmuSizeX = 320;
	public static int mScreenEmuSizeY = 240;
	public static boolean vertical = false;
	public static String[] args = null;
	
	
	private AlertDialog inputHardwareDialog;
	private int inputHardwareButtonNow = 0;
	private boolean inputHardwareEdit = false;
	
	private ActionBar actionBar;
	private Menu menu;
	public ListView stateMenu; //"@+id/android:list"
	public StateAdapter statesAdapter;
	Display display;
	
	public static int actionBarHeight;
	
    // Setup
    @SuppressLint({ "InlinedApi", "InflateParams" }) // for Window.FEATURE_ACTION_BAR_OVERLAY
	protected void onCreate( Bundle savedInstanceState ) 
    {
    	System.loadLibrary( "SDL" );
		System.loadLibrary( "arcade" );
		
        super.onCreate( savedInstanceState );

        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        
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
        
        if( this.getIntent().getExtras() != null )
        {
        	vertical = this.getIntent().getExtras().getBoolean( "vertical" );
	        buttonCount = this.getIntent().getExtras().getInt( "buttons" );
	        mScreenHolderSizeX = this.getIntent().getExtras().getInt( "screenW" );
	    	mScreenHolderSizeY = this.getIntent().getExtras().getInt( "screenH" );
	    	mScreenEmuSizeX = mScreenHolderSizeX;
	    	mScreenEmuSizeY = mScreenHolderSizeY;	
	        SDLJni.datapath = this.getIntent().getExtras().getString( "data" );
	        SDLJni.statespath = this.getIntent().getExtras().getString( "states" );
			SDLJni.rompath = this.getIntent().getExtras().getString( "roms" );
			SDLJni.rom = this.getIntent().getExtras().getString( "rom" );
        }
        else
        {
        	vertical = true;
	        buttonCount = 2;
	        mScreenHolderSizeX = 384;
	    	mScreenHolderSizeY = 224;
	    	mScreenEmuSizeX = mScreenHolderSizeX;
	    	mScreenEmuSizeY = mScreenHolderSizeY;
	        SDLJni.datapath = Environment.getExternalStorageDirectory().getPath()+"/aFBA";
	        SDLJni.statespath = Environment.getExternalStorageDirectory().getPath()+"/aFBA/states";
			SDLJni.rompath = Environment.getExternalStorageDirectory().getPath()+"/aFBA/roms";
			SDLJni.rom = "19xx";
        }
       
        if( vertical )
        {
	        int newWidth = mScreenHolderSizeY;
	        int newHeight = mScreenHolderSizeX;
	        mScreenHolderSizeX  = newWidth;
	        mScreenHolderSizeY = newHeight;
	        mScreenEmuSizeX = newWidth;
	        mScreenEmuSizeY = newHeight;
        }
        
		args = new String[1];
		args[0] = SDLJni.rom;
 
        activity = this;
        
        mPrefs = new EmuPreferences( Main.this );
        mMessage = new UtilityMessage( Main.this );
        mEffectList = new EffectList();
        inputHardware = new HardwareInput( Main.this );
        
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mainView = (RelativeLayout)layoutInflater.inflate( R.layout.emulator, null );
	
        inputView = new SoftwareInputView( display, getPackageName(), mainView, buttonCount, mPrefs.useVibration(), false );
		mainView.addView( inputView );
        
		effectView = new Effect( this );
       	mainView.addView( effectView );
       	
        setContentView( mainView );

        surfaceView = (SDLSurface)this.findViewById( R.id.SDLSurface );
        surfaceView.setKeepScreenOn( true );
        surfaceView.setOnKeyListener( this );

       	stateMenu = (ListView)this.findViewById( android.R.id.list );
		stateMenu.setOnItemClickListener( statesListener );
		stateMenu.setVisibility( View.GONE );
        statesAdapter = new StateAdapter( activity, R.layout.statelist );
        activity.setListAdapter( this.statesAdapter );

		applyRatioAndEffect();  
		actionBar = this.getSupportActionBar();
		actionBar.setDefaultDisplayHomeAsUpEnabled(true);
		this.hideNavBar();
		
		if( !mPrefs.useSwInput() )
			inputView.setVisibility( View.GONE );
		else
		{
			inputView.requestFocus();
			inputView.bringToFront();
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) 
		{
			uiChangeListener();
		}
    }

    public void updateStatesList()
    {
    	activity.runOnUiThread( new Runnable()
		{
			@Override
			public void run() 
			{
				statesAdapter.clear();
				statesAdapter.add( new StateInfo( getResources().getDrawable( R.drawable.state ) ) );
				
				final StateList statelist = new StateList( SDLJni.statespath, SDLJni.rom );
				for( int i=0; i<statelist.getStates().size(); i++ )
				{
					statesAdapter.add( statelist.getStates().get(i) );
				}
				stateMenu.setVisibility( View.VISIBLE );
				stateMenu.bringToFront();
				stateMenu.setFocusable(true);
				stateMenu.requestFocus();
			}
		});
    }
    
	private OnItemClickListener statesListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick( AdapterView<?> parent, View v, final int position, final long id ) 
		{
			final StateInfo state = statesAdapter.getItem( (int)id );
			if( state.date.contentEquals( "Create new save" ) )
			{
				final int num = statesAdapter.getCount() - 1;
				Utility.log( "Saving state in slot " + num );
				SDLJni.statesave( num );
				
				updateStatesList();
			}
			else
			{
				//dialogStates( state );
				Utility.log( "Loading state from slot " + state.id );
				SDLJni.stateload( state.id );
				handlePauseMenu();
			}
		}
	};

    @Override
    public boolean onCreateOptionsMenu ( Menu pMenu )
    {
    	getMenuInflater().inflate( R.menu.menu, pMenu );
    	menu = pMenu;
    	menu.findItem( R.id.menu_input_usesw ).setChecked( mPrefs.useSwInput() );
    	menu.findItem( R.id.menu_input_vibrate ).setChecked( mPrefs.useVibration() );
    	updateFskip( mPrefs.getFrameSkip() );
    	return super.onCreateOptionsMenu( menu );
    }

    @SuppressLint("NewApi")
	public void uiChangeListener()
    {
        final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @SuppressLint("NewApi")
			@Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(
                    		View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            				| View.SYSTEM_UI_FLAG_FULLSCREEN
            				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });
    }
    
    @SuppressLint("NewApi")
	public void hideNavBar()
    {
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    		mainView.setSystemUiVisibility(
    				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    				| View.SYSTEM_UI_FLAG_FULLSCREEN
    				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    	}
    	actionBar.hide();
    }
    
    public void showNavBar()
    {
    	actionBar.show();
    }
    
    public void updateFskip( int fskip )
    {
    	// handle fskip checkbox's
    	menu.findItem( R.id.menu_fskip_0 ).setChecked( fskip == 0 ? true : false );
    	menu.findItem( R.id.menu_fskip_1 ).setChecked( fskip == 1 ? true : false );
    	menu.findItem( R.id.menu_fskip_2 ).setChecked( fskip == 2 ? true : false );
    	menu.findItem( R.id.menu_fskip_3 ).setChecked( fskip == 3 ? true : false );
    	menu.findItem( R.id.menu_fskip_4 ).setChecked( fskip == 4 ? true : false );
    	menu.findItem( R.id.menu_fskip_5 ).setChecked( fskip == 5 ? true : false );
    	menu.findItem( R.id.menu_fskip_6 ).setChecked( fskip == 6 ? true : false );
    	menu.findItem( R.id.menu_fskip_7 ).setChecked( fskip == 7 ? true : false );
    	menu.findItem( R.id.menu_fskip_8 ).setChecked( fskip == 8 ? true : false );
    	menu.findItem( R.id.menu_fskip_9 ).setChecked( fskip == 9 ? true : false );
    	
    	SDLJni.setfskip( fskip );
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	int pad_data = 0;
    	
        int itemId = item.getItemId();
		if (itemId == R.id.menu_scale) {
			int newSize = mPrefs.getScreenSize();
			newSize++;
			if( newSize>=EffectList.ScreenSize.values().length ) {
				newSize = 0;
			}
			mPrefs.setScreenSize( newSize );
			applyRatioAndEffect();
			return true;
		} else if (itemId == R.id.menu_effects) {
			selectEffect();
			return true;
		} else if (itemId == R.id.menu_fskip_0) {
			mPrefs.setFrameSkip( 0 );
			updateFskip( 0 );
			return true;
		} else if (itemId == R.id.menu_fskip_1) {
			mPrefs.setFrameSkip( 1 );
			updateFskip( 1 );
			return true;
		} else if (itemId == R.id.menu_fskip_2) {
			mPrefs.setFrameSkip( 2 );
			updateFskip( 2 );
			return true;
		} else if (itemId == R.id.menu_fskip_3) {
			mPrefs.setFrameSkip( 3 );
			updateFskip( 3 );
			return true;
		} else if (itemId == R.id.menu_fskip_4) {
			mPrefs.setFrameSkip( 4 );
			updateFskip( 4 );
			return true;
		} else if (itemId == R.id.menu_fskip_5) {
			mPrefs.setFrameSkip( 5 );
			updateFskip( 5 );
			return true;
		} else if (itemId == R.id.menu_fskip_6) {
			mPrefs.setFrameSkip( 6 );
			updateFskip( 6 );
			return true;
		} else if (itemId == R.id.menu_fskip_7) {
			mPrefs.setFrameSkip( 7 );
			updateFskip( 7 );
			return true;
		} else if (itemId == R.id.menu_fskip_8) {
			mPrefs.setFrameSkip( 8 );
			updateFskip( 8 );
			return true;
		} else if (itemId == R.id.menu_fskip_9) {
			mPrefs.setFrameSkip( 9 );
			updateFskip( 9 );
			return true;
		} else if (itemId == R.id.menu_states) {
			updateStatesList();
			return true;
		} else if (itemId == R.id.menu_input_edit) {
			AlertDialog alertDialog = new AlertDialog.Builder(Main.this).create();
			alertDialog.setTitle( "Choose" );
			alertDialog.setMessage("You can either edit the default configuration for all games with "+buttonCount+" buttons or for this game ("+SDLJni.rom+")" );
			alertDialog.setButton( AlertDialog.BUTTON_NEGATIVE, "Default "+buttonCount+" buttons", new DialogInterface.OnClickListener() 
			{
			      public void onClick( DialogInterface dialog, int which ) 
			      {
			    	  startSupportActionMode( new InputEditActionMode( buttonCount ) );
			      }
			});
			alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, "This game: "+SDLJni.rom, new DialogInterface.OnClickListener() 
			{
			      public void onClick(DialogInterface dialog, int which) 
			      {
			    	  startSupportActionMode( new InputEditActionMode( -1 ) );
			      }
			});
			alertDialog.show();
			return true;
		} else if (itemId == R.id.menu_input_vibrate) {
			boolean vibrate = !mPrefs.useVibration();
			mPrefs.useVibration( vibrate );
			inputView.setVibration( vibrate );
			item.setChecked( vibrate );
			return true;
		} else if (itemId == R.id.menu_input_usesw) {
			boolean useSw = !mPrefs.useSwInput();
			mPrefs.useSwInput( useSw );
			inputView.setVisibility( useSw ? View.VISIBLE : View.GONE);
			item.setChecked( useSw );
			return true;
		} else if (itemId == R.id.menu_input_sethw) {
			showInputHardwareDialog();
			return true;
		} else if (itemId == R.id.menu_switchs_service) {
			handlePauseMenu();
			pad_data = 0;
			pad_data |= IButtons.VALUE_TEST;
			Main.setPadData( 0, pad_data );
			return true;
		} else if (itemId == R.id.menu_switchs_reset) {
			handlePauseMenu();
			pad_data = 0;
			pad_data |= IButtons.VALUE_RESET;
			Main.setPadData( 0, pad_data );
			return true;
		} else if (itemId == R.id.menu_quit) {
			dialogConfirmExit();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }
    
    public void selectEffect()
    {
    	final CharSequence[] charseq = mEffectList.getCharSequenceList();
		new AlertDialog.Builder(activity)
        .setTitle("Choose an effect")
        .setItems( charseq, new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int which) 
            {
            	Utility.log( "Selected effect: " + charseq[which].toString() );
            	mPrefs.setEffectFast( charseq[which].toString() );
            	applyRatioAndEffect();
            }
        })
        .create().show(); 
    }
    
    @SuppressWarnings("deprecation")
	public void applyRatioAndEffect()
    {
    	float w = RelativeLayout.LayoutParams.MATCH_PARENT, h = RelativeLayout.LayoutParams.MATCH_PARENT;
    	int orientation = getResources().getConfiguration().orientation;
		final float width = display.getWidth();
		final float height = display.getHeight();
		final float ratio = ((float)mScreenEmuSizeX/(float)mScreenEmuSizeY);
		Utility.log( "Display: " + (int)width+"x"+(int)height+ "(ratio:"+ratio+")" );
		
		switch ( EffectList.ScreenSize.values()[mPrefs.getScreenSize()] )
        {
        	case EFFECT_FITSCREEN:
        	{
        		mMessage.showToastMessageShort( "screen: fit" );
        		final float scaledw = (float)height * ratio;
        		if( scaledw > width )
        		{
        			w = width;
        			h = (float)w / ratio;
        		}
        		else
        		{
        			w = scaledw;
        			h = display.getHeight();
        		}
        		break;
        	}
        	
        	case EFFECT_43:
        	{
        		mMessage.showToastMessageShort( "screen: 4:3" );
        		final float scaledw = (float)height * ((float)4/(float)3);
        		if( scaledw > width )
        		{
        			w = width;
        			h = (float)w / ratio;
        		}
        		else
        		{
        			w = scaledw;
        			h = display.getHeight();
        		}
        		break;
        	}
        		
        	case EFFECT_FULLSCREEN:
        		mMessage.showToastMessageShort( "screen: full" );
        		break;
        		
        	case EFFECT_ORIGINALSCREEN:
        		mMessage.showToastMessageShort( "screen: original" );
        		w = mScreenEmuSizeX; h = mScreenEmuSizeY;
        		break;
        		
        	case EFFECT_2X:
        		mMessage.showToastMessageShort( "screen: 2x" );
        		w = mScreenEmuSizeX*2; h = mScreenEmuSizeY*2;
        		if( w > width )
        		{
        			mPrefs.setScreenSize( 2 );
        			applyRatioAndEffect();
        			return;
        		}
        		break;
        }
		
		Utility.log( "View: " + (int)w + "x" + (int)h );
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams( (int)w, (int)h );
		p.addRule( RelativeLayout.CENTER_HORIZONTAL );
		p.addRule( orientation == Configuration.ORIENTATION_PORTRAIT ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.CENTER_VERTICAL );
		surfaceView.setLayoutParams( p );
		effectView.applyEffect( p, mEffectList.getByName( mPrefs.getEffectFast() ) );
		inputView.requestLayout();
    }
    
    public void dialogConfirmExit( )
	{
    	activity.runOnUiThread( new Runnable()
        {
        	public void run()
        	{
				new AlertDialog.Builder( activity )
				.setTitle( "Confirm" )
				.setMessage( "\nStop emulation ?\n" )
				.setPositiveButton( "Confirm", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						resume();
						activity.finish();
					}
				})
				.setNegativeButton( "Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton){}
				}).create().show();
            }
        });
	}
    
    private final class InputEditActionMode implements ActionMode.Callback 
    {
    	boolean canceled = false;
    	int buttons = -1;
    	
    	public InputEditActionMode( int bcount )
    	{
    		buttons = bcount;
    	}
    	
        @Override
        public boolean onCreateActionMode( ActionMode mode, Menu menu ) 
        {
        	actionBarHeight = actionBar.getHeight();
            Utility.loge( "actionBarHeight: "+actionBarHeight );
            hideNavBar();
        	mMessage.showToastMessageLong( "Touch buttons to move them..." );
        	inputView.setEditMode(true);
        	Main.this.getMenuInflater().inflate( R.menu.menu_edit_input, menu );
            return true;
        }

        @Override
        public boolean onPrepareActionMode( ActionMode mode, Menu menu ) 
        {
            return false;
        }

        @Override
        public boolean onActionItemClicked( ActionMode mode, MenuItem item ) 
        {
        	Utility.log( "onActionItemClicked: " + item.getTitle() + " ("+item.getItemId()+")");
        	
        	float stick_scale = inputView.getStickScale();
        	int stick_alpha = inputView.getStickAlpha();
        	float buttons_scale = inputView.getButtonsScale();
        	
        	int id = item.getItemId();
            //switch ( item.getItemId() )
            {
            	if( id == R.id.menu_edit_scale_sminus )
            	{
            		stick_scale-=0.1f;
            		inputView.setStickScale( stick_scale );
            		return true;
            	}
            	else if( id == R.id.menu_edit_scale_splus )
            	{
            		stick_scale+=0.1f;
            		inputView.setStickScale( stick_scale );
            		return true;
            	}
            	else if( id == R.id.menu_edit_scale_bminus )
            	{
            		buttons_scale-=0.1f;
            		inputView.setButtonsScale( buttons_scale );
            		return true;
            	}	
            	else if( id == R.id.menu_edit_scale_bplus )
            	{
            		buttons_scale+=0.1f;
            		inputView.setButtonsScale( buttons_scale );
            		return true;
            	}
            	else if( id == R.id.menu_edit_alpha_minus )
            	{
            		stick_alpha-=10;
            		inputView.setAlpha( stick_alpha );
            		return true;
            	}
            	else if( id == R.id.menu_edit_alpha_plus )
            	{
            		stick_alpha+=10;
            		inputView.setAlpha( stick_alpha );
            		return true;
            	}
            	else if( id == R.id.menu_edit_cancel )
            	{
            		canceled = true;
            		mode.finish();
            		return true;
            	}
            	return false;
            }
        }

        @Override
        public void onDestroyActionMode( ActionMode mode ) 
        {
        	Utility.log( "onDestroyActionMode" );
        	inputView.setEditMode( false );
        	showNavBar();
        	if( !canceled )
        		inputView.save( buttons );
        }
    }
  
    // Controls
    public void setAnalogData(int i, float x, float y){ }
    public static void setPadData( int i, long data )
    {
    	SDLJni.setPadData(i, data);
    }
    
    @Override
	public boolean onKey(View v, int keyCode, KeyEvent event)
    {
    	if( this.inputHardwareEdit )
			return true;
    	
    	if( !inputView.isShown() && !actionBar.isShowing() )
    		return inputHardware.onKey( v, keyCode, event );
    	
		return false;
	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
    	if( this.inputHardwareEdit )
			return true;
    	
	    switch( keyCode )
	    {
	    	case KeyEvent.KEYCODE_SEARCH:
	    			return true;
	    	
	    	case KeyEvent.KEYCODE_BACK:
	    		return handlePauseMenu();
	    		
	    	case KeyEvent.KEYCODE_MENU:
	    		if( actionBar.isShowing() )
	    			return super.onKeyDown( keyCode, event );
	    		else 
	    			return handlePauseMenu();
	    }
		return super.onKeyDown( keyCode, event );
	}
 
    public boolean handlePauseMenu()
    {
    	Utility.log( "### actionBar.isShowing: " + actionBar.isShowing() );
    	if( actionBar.isShowing() )
    	{
    		stateMenu.setVisibility( View.GONE );
    		hideNavBar();
    		resume();
    	}
    	else
    	{
    		showNavBar();
    		pause();
    	}
    	return true;
    }
  
    @Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
    	// ignore orientation/keyboard change
    	super.onConfigurationChanged(newConfig);
    	applyRatioAndEffect();
    }

    @Override
    protected void onPause() 
    {
    	Utility.log("onPause()");
        super.onPause();
        pause();
    }
   
    @Override
    protected void onResume() 
    {
    	Utility.log("onResume()");
        super.onResume();
        resume();
    }
    
    @Override
    protected void onDestroy()
    {
    	Utility.log( "onDestroy()" );
    	stop();
        super.onDestroy();
    }
    
    private void showInputHardwareDialog()
	{	
		inputHardwareEdit = true;
		inputHardwareButtonNow = 0;
		inputHardwareDialog = new AlertDialog.Builder( this )
        .setTitle( "Map hardware keys" )
        .setMessage( "\nPlease press a button for: " + HardwareInput.ButtonKeys.get( inputHardwareButtonNow ) + "\n" )
        .create();

		inputHardwareDialog.setOnKeyListener( new DialogInterface.OnKeyListener()
		{
			@Override
			public boolean onKey( DialogInterface dialog, int keyCode, KeyEvent event) 
			{
				if( event.getAction() == KeyEvent.ACTION_DOWN )
				{
					mPrefs.setPad( HardwareInput.ButtonKeys.get( inputHardwareButtonNow ), keyCode );
					inputHardwareButtonNow++;
					if( inputHardwareButtonNow < HardwareInput.ButtonKeys.size() )
					{
						inputHardwareDialog.setMessage( "\nPlease press a button for: " 
														+ HardwareInput.ButtonKeys.get( inputHardwareButtonNow ) + "\n" );
					}
					else
					{
						dialog.dismiss();
						inputHardwareEdit = false;
					}
				}
				return true;
			}
		});
		inputHardwareDialog.show();
	}
    
    public static void resume()
    {
    	if( SDLJni.ispaused() == 0 )
    		return;
    	
    	SDLJni.resumeemu();
    	SDLAudio.play();
    }
    public static void pause()
    {
    	if( SDLJni.ispaused() == 1 )
    		return;
    	
    	SDLJni.pauseemu();
    	SDLAudio.pause();
    }
    public static void stop()
    {
    	if( SDLJni.ispaused() == 1 )
    		resume();
    		
    	SDLJni.emustop();
    	
        // Now wait for the SDL thread to quit
        if ( surfaceView.mSDLThread != null) 
        {
        	Utility.log( "Emulator thread is running, waiting for it to finnish..." );
            try 
            {
            	surfaceView.mSDLThread.join();
            } 
            catch(Exception e) 
            {
            	Utility.loge("Problem stopping thread: " + e);
            }
            surfaceView.mSDLThread = null;
            Utility.log("Finished waiting for emulator thread");
        }
        else
        {
        	Utility.log( "Emulator thread not running" );
        }
        System.gc();
    }
    
    //private static String mErrorMessage = null;
    private static int progress = 0;
    private static int max = 0;
    private static String progressMessage = "Please Wait";
    private static Handler mHandler = new Handler();
    private static Runnable updateProgressBar = new Runnable()
    {
		@Override
		public void run() 
		{
			//mMessage.setDialogProgress( progressMessage, progress );
			mMessage.show( progressMessage, progress, max );
			mHandler.postDelayed( updateProgressBar, 500 );
		}
    };
    
    public static void setErrorMessage( String pMessage ) 
    {
    	Utility.loge( "###setErrorMessage### ==> " + pMessage );
    	Intent i = activity.getIntent();
    	i.putExtra( "error", pMessage );
    	activity.setResult( RESULT_OK, i );
    	activity.finish();
    }
    public static void showProgressBar( String pMessage, int pMax ) 
    {
    	//Utility.loge( "###showProgressBar### ==> " + pMessage );
    	max = pMax;
        mMessage.show( pMessage, 0, pMax );
        mHandler.post( updateProgressBar );
    }
    public static void hideProgressBar() 
    {
    	//Utility.loge( "###hideProgressBar###" );
    	mHandler.removeCallbacks(updateProgressBar);
        mMessage.hide();
    }
    public static void setProgressBar ( String pMessage, int pProgress ) 
    {
    	//Utility.loge( "###setProgressBar### ==> " + pProgress );
    	//progressMessage = pMessage;
    	//progress = pProgress;
    	mMessage.show( pMessage );
    }
    
}
