package fr.mydedibox.libarcade.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import com.greatlittleapps.billing.utils.IabHelper;
import com.greatlittleapps.billing.utils.IabResult;
import com.greatlittleapps.billing.utils.Inventory;
import com.greatlittleapps.billing.utils.Purchase;
import com.greatlittleapps.utility.FileInfo;
import com.greatlittleapps.utility.Filer;
import com.greatlittleapps.utility.Utility;
import com.greatlittleapps.utility.UtilityMessage;

import fr.mydedibox.libarcade.R;
import fr.mydedibox.libarcade.ArcadeUtility;
import fr.mydedibox.libarcade.CompatibilityList;
import fr.mydedibox.libarcade.activity.romListActivity;
import fr.mydedibox.libarcade.objects.RomInfo;
import fr.mydedibox.libarcade.preferences.EmuPreferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class romListFragment extends ListFragment implements SearchView.OnQueryTextListener
{
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private Callbacks mCallbacks = sDummyCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    
    private ActionBarActivity activity;
    private ActionBar actionBar;
    
    public EmuPreferences prefs;
    private FileAdapter adapter;
	private ColorStateList defaultTextColor;
	private UtilityMessage dialog;
	private CompatibilityList compatList;
	
	private String searchFilter = "";
	private ListView listView; 
	private SearchView searchView;
	private MenuItem searchMenu;
	
	private IabHelper mIabHelper;
	private String SKU = "com.greatlittleapps.afba.support";

	public static boolean compatListIsShown = false;
	
    public interface Callbacks 
    {
        public void onItemSelected(String id);
        public void exit();
    }

    private static Callbacks sDummyCallbacks = new Callbacks() 
    {
        @Override
        public void onItemSelected(String pPath) 
        {
        	Utility.log( "onItemSelected("+pPath+")" );
        }
        
        @Override
        public void exit() 
        {
        	Utility.log( "exit" );
        }
    };
    
    public romListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	Utility.log( "onCreate" );

        activity = (ActionBarActivity)getActivity();
        actionBar = activity.getSupportActionBar();
        dialog = new UtilityMessage( activity );
        prefs = new EmuPreferences( activity );  
        compatList = romListActivity.compatList;

        super.onCreate(savedInstanceState);
        setHasOptionsMenu( true );
        
        // IAP
        String base64EncodedPublicKey = 
        		"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkh1dlB0LPZ1zGXXnmbM3K5V8jZ58VBFDmkQX77gq/LycA1S2aCX74rA1I871ZIVfqfGbBMGMUB6QnOHzV7zZhxazl/Jj8PIPR3S4psJGZDeCnbAI5Fm93IULnhpdO7sgiH68Q0iFlYL2IZQfIGy+zqtgkKq4SUf26Ypx/LfPg199znqI5XOrxtwaFxqawYSQEBik101HIDeWiT2fmfF1i/KyCG57oJuu61J84bbnsbfwi4OCw1nDLChiLfNAcXvSlNuSEtwpNcrP4fQb7KgxUynoL5XOE2iGvSJAB/3vAOQP6zzlhh+1FnO4QTx8EIry+k/IuhXJTmFbwIsiOJtFFwIDAQAB";
        mIabHelper = new IabHelper(activity, base64EncodedPublicKey);
        mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() 
    	{
        	public void onIabSetupFinished(IabResult result) 
        	{
        		Utility.log( "In-app billing setup success: " + result.isSuccess());
        		if (result.isSuccess()) 
        		{
        			mIabHelper.queryInventoryAsync(mReceivedInventoryListener);
        		}
        	}
    	});
    }
    
    @Override
	public void onDestroy() 
	{
		if (mIabHelper != null) 
			mIabHelper.dispose();
		mIabHelper = null;
		
	    super.onDestroy();
	}
    
    @Override
    public void onConfigurationChanged( Configuration newConfig ) 
    {
    	super.onConfigurationChanged( newConfig );
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
    	Utility.log( this.getClass().getSimpleName() + ": onActivityCreated" );
        super.onActivityCreated(savedInstanceState);
        
        adapter = new FileAdapter( activity, R.layout.filerlist );
        setListAdapter( adapter );
        listView = getListView();
        listView.setFastScrollEnabled( true );
        
        GetDir( prefs.getRomsPath() );
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {	
    	Utility.log( this.getClass().getSimpleName() + ": onCreateView" );
    	View v = inflater.inflate( R.layout.filerview, container, false );
        v.setOnKeyListener( new OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) 
			{
				switch( keyCode )
			   	{
			   		case KeyEvent.KEYCODE_BACK:
			   			if( !searchView.isIconified() )
			   			{
			   				MenuItemCompat.collapseActionView( searchMenu );
			   				GetDir( actionBar.getTitle().toString() );
			   				return true;
			   			}
			   			break;
			   	}	
				return false;
			}
		});
        
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) 
    {
    	Utility.log( this.getClass().getSimpleName() + ": onViewCreated" );
        super.onViewCreated(view, savedInstanceState);
        
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) 
        {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
        actionBar.setTitle( prefs.getRomsPath() );
    }
    
    @Override
    public void onCreateOptionsMenu ( Menu menu, MenuInflater inflater )
    {
    	Utility.log( "onCreateOptionsMenu" );
    	
    	super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate( R.menu.rom_list, menu );

        searchMenu = menu.findItem( R.id.menu_search );
        if( searchMenu != null )	
        {
        	searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);
        	if( searchView != null )
        	{
	        	searchView.setOnQueryTextListener(this);
	        	searchView.setOnQueryTextFocusChangeListener( new View.OnFocusChangeListener() 
	        	{            
	                public void onFocusChange( View v, boolean hasFocus ) 
	                {
	                	Utility.log( "hasFocus:"+hasFocus );
	                	if( !hasFocus )
	                	{
	                		if( !searchView.isIconified() )
	                		{
	                			//searchMenu.collapseActionView();
	                			//searchView.setIconified( true );
	                			MenuItemCompat.collapseActionView( searchMenu );
	                			GetDir( actionBar.getTitle().toString() );
	                		}
	                	}
	                }
	            });
        	}
        }

        MenuItem showClonesItem = menu.findItem( R.id.menu_clones );
        if( showClonesItem != null )
        {
        	if( prefs.GetShowClones() )
        		showClonesItem.setIcon( getResources().getDrawable( R.drawable.clones_on ) );
    		else 
    			showClonesItem.setIcon( getResources().getDrawable( R.drawable.clones_off ) );
        }
        menu.findItem( R.id.menu_compatList ).setTitle( ( romListFragment.compatListIsShown ? "romList" : "compatList") );
    }
   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	//Utility.log( item.getTitle().toString() );
    	AlertDialog.Builder builder;
    	AlertDialog alert;
    	
    	int id = item.getItemId();
        {
            if( id == R.id.menu_clones )
            {
        		prefs.SetShowClones( !prefs.GetShowClones() );
        		dialog.showToastMessageLong( "Clones are " + ( prefs.GetShowClones() ? "enabled" : "disabled" ) );
        		if( prefs.GetShowClones() )
        			item.setIcon( getResources().getDrawable( R.drawable.clones_on ) );
        		else 
        			item.setIcon( getResources().getDrawable( R.drawable.clones_off ) );
        		GetDir( actionBar.getTitle().toString() );
            	return true;
            }
            else if( id == R.id.menu_compatList )
            {
            	mCallbacks.onItemSelected( null );
            	searchFilter = null;
            	compatListIsShown = !compatListIsShown;	
            	item.setTitle( ( romListFragment.compatListIsShown ? "romList" : "compatList") );
            	GetDir( actionBar.getTitle().toString() );
            	return true;
            }	
            else if( id == R.id.menu_download_previews )
            {
            	prefs.SetDataOk( false );
            	startActivity( new Intent( activity, romListActivity.class ) );
            	activity.finish();
            	return true;
            }
            else if( id == R.id.menu_filter_years )
            {
            	builder = new AlertDialog.Builder(activity);
            	builder.setTitle("Select year");

            	builder.setItems( compatList.getFilterYearsCharSeq(),
            			new DialogInterface.OnClickListener() 
            	{
                
            		@Override
	                public void onClick( DialogInterface dialog, int which ) 
	                {
            			GetDir( actionBar.getTitle().toString(), compatList.getFilterYears().get(which) );
	                }
            	});
            	alert = builder.create();
            	alert.show();
            	return true;
            }
            else if( id == R.id.menu_filter_systems )
            {
            	builder = new AlertDialog.Builder(activity);
            	builder.setTitle("Select system");

            	builder.setItems( compatList.getFilterSystemsCharSeq(),
            			new DialogInterface.OnClickListener() 
            	{
                
            		@Override
	                public void onClick( DialogInterface dialog, int which ) 
	                {
            			GetDir( actionBar.getTitle().toString(), -1, compatList.getFilterSystems().get(which) );
	                }
            	});
            	alert = builder.create();
            	alert.show();
            	return true;
            }
            else if( id == R.id.menu_donate)
            {
            	mIabHelper.launchPurchaseFlow(activity, SKU, 10001, mPurchaseFinishedListener, "");
            }
            else if( id == R.id.menu_quit )
            {
            	mCallbacks.exit();
            }
            return super.onOptionsItemSelected(item);
        }
    }
 
    @Override
    public void onAttach(Activity activity) 
    {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) 
        {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() 
    {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) 
    {
        super.onListItemClick( listView, view, position, id );
        final FileInfo file = adapter.getItem((int)id);
        
        if( file.isDirectory() )
		{
			GetDir( file.getPath() );
		}
        else if( file.getCustomData() != null )
		{
        	mCallbacks.onItemSelected( Utility.serialize( file ) );
		}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) 
    {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) 
        {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) 
    {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    public void setActivatedPosition(int position) 
    {
        if (position == ListView.INVALID_POSITION) 
        {
            getListView().setItemChecked(mActivatedPosition, false);
        } 
        else 
        {
            getListView().setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }
    
    private class FileAdapter extends ArrayAdapter<FileInfo>
    {
    	public synchronized void add(FileInfo object) 
        {
            super.add(object);
        }
        
        public synchronized FileInfo getItem(int position) 
        {
            return super.getItem(position);
        }
        
        public synchronized void remove(FileInfo object) 
        {
            super.remove(object);
        }
        
        public synchronized void insert(FileInfo object, int index) 
        {
            super.insert(object, index);
        }
        
    	public FileAdapter( Context context, int textViewResourceId )
        {
        	super( context, textViewResourceId );
        }
    	
		@Override
        public View getView( int position, View convertView, ViewGroup parent )
        { 		
        	View v = convertView;
        	if ( v == null )
        	{
        		LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        		v = vi.inflate( R.layout.filerlist, null );	
        	}
        	
        	final FileInfo file = getItem(position);
        	if ( file != null )
        	{
        		final RomInfo rom = (RomInfo)file.getCustomData();
        				
        		TextView name = (TextView) v.findViewById( R.id.file_name );
        		if( name != null )
        		{
        			if( defaultTextColor == null )
        				defaultTextColor = name.getTextColors();

        			name.setTextColor( defaultTextColor );
        			
        			if( rom != null )
        			{
        				name.setText( rom.GetTitle() );
        				if( rom.GetStatus() == RomInfo.WORKING )
        					name.setTextColor( Color.RED );
        			}
        			else
        				name.setText( file.getName() );
        		}
        		
        		TextView info = (TextView) v.findViewById( R.id.file_info );
        		if( info != null )
        		{
        			if( file.isDirectory() )
        				info.setText( file.getChildCount() + " files" );
        			else if( rom != null ) {
        				if( compatListIsShown ) {
        					info.setText( rom.GetSystem() + " - " + rom.GetYear() );
        				} else {
        					info.setText( rom.GetSystem() + " - " + rom.GetYear() + " (" + Utility.formatFileSize( file.getSize() ) + ")" );
        				}
        			}
        			else
        				info.setText( file.getPath() + " (" + Utility.formatFileSize( file.getSize() ) + ")" );
        		}
        		
        		ImageView icon = (ImageView) v.findViewById( R.id.file_icon );
        		if( icon != null )
        		{
        			if( rom != null )
        			{
        				// get icon from res
        				Bitmap bitmap = ArcadeUtility.GetIcon( activity, rom );
        						
        				if( bitmap != null )
        					icon.setImageBitmap(bitmap);
        				else
        					icon.setImageResource( R.drawable.noicon );
        			}
        			else if( file.isDirectory() )
        				icon.setImageResource( R.drawable.folder );
        			else
        				icon.setImageResource( R.drawable.noicon );
        		}
        	}
        	return v;
        }
    } 
    
    public void GetDirBack()
	{
    	Utility.log( "GetDirBack" );
    	
    	if( searchFilter != null )
		{
    		searchFilter = null;
    		MenuItemCompat.collapseActionView( searchMenu );
			this.GetDir( actionBar.getTitle().toString() );
			return;
		}
    	
		final FileInfo file = new FileInfo( new File( actionBar.getTitle().toString() ) );
		if( file.getParent() != null )
		{
			File top = new File( file.getParent() );
			if( top.isDirectory() && top.canRead() )
			{
				GetDir( top.getAbsolutePath() );
			}
		}
	}
   
    private void GetDir( final String pPath )
    {
    	GetDir( pPath, -1 );
    }
    
    private void GetDir( final String pPath, final int year )
    {
    	GetDir( pPath, year, null );
    }
    
    private void GetDir( final String pPath, final int year, final String system )
    {
    	mCallbacks.onItemSelected( null );
    	
    	if( compatListIsShown )
    	{
    		showCompatList( year, system );
    		return;
    	}
    	
    	Utility.log( "Listing files in " + pPath );

    	new Filer( pPath, null )
    	{
    		@Override
    		public void onPreExecute()
    		{
    			dialog.show( "Listing files in " + pPath );
    		}
    		
    		@Override
    		public void onPostExecute( final ArrayList<FileInfo> files )
    		{
    			if( files == null )
    			{
    				dialog.hide();
    				return;
    			}
    			
				prefs.setRomsPath( pPath );
				actionBar.setTitle( pPath );
				adapter.clear();
				
				for( FileInfo file : files )
				{
					String nameLower = file.getName().toLowerCase(Locale.getDefault());
					//Utility.log( "nameLower: " + nameLower );
					if( nameLower.endsWith( ".zip" ) || nameLower.endsWith( ".7z" ) )
		    		{
						String name = file.getName().substring( 0, file.getName().lastIndexOf('.') );
						//Utility.log( "name: " + name );
		    			RomInfo r = compatList.GetRom( name );
		            	if( r != null )
		            	{
		            		if( filtered( r, searchFilter, year, system ) )
		            			continue;
		            		
		            		file.setCustomData( r );
		            		adapter.add( file );
		            		continue;
		            	}
		    		}
					
					if( file.isDirectory() )
						adapter.add( file );
				}
				
				adapter.notifyDataSetChanged();
				dialog.hide();
    		};
    	};
    }

    private void showCompatList( final int year, final String system )
    {
    	Utility.log( "Building compatibility list" );
    	dialog.show( "Building compatibility list" );
    	
    	adapter.clear();
    	for( RomInfo rom : this.compatList.getList() )
    	{
    		if( filtered( rom, searchFilter, year, system ) )
    			continue;
    		
    		final FileInfo f = new FileInfo();
			f.setCustomData( rom );
			adapter.add( f );
    	}
    	dialog.hide();
    }
    
    public boolean filtered( final RomInfo rom, final String match, final int year, final String system  )
    {		
    	if( !prefs.GetShowClones() && rom.GetParent() != null )
    		return true;

    	if( year != -1 && rom.GetYear() != year )
			return true;
    	
    	if( system != null && !rom.GetSystem().equals(system) )
    		return true;
    	
    	if ( match != null && !rom.GetName().toLowerCase(Locale.getDefault()).contains( match.toLowerCase(Locale.getDefault()) ) && !rom.GetTitle().toLowerCase(Locale.getDefault()).contains( match.toLowerCase(Locale.getDefault()) ) )
    		return true;
    	
    	return false;
    }
    
	@Override
	public boolean onQueryTextSubmit( String query ) 
	{
		Utility.log( "onQueryTextSubmit: " + query );
		MenuItemCompat.collapseActionView( searchMenu );
		boolean filtered = query != null && query.length() > 0;
		searchFilter = filtered ? query : null;
		this.GetDir( actionBar.getTitle().toString() );
		return true;
	}

	@Override
	public boolean onQueryTextChange( String newText ) 
	{
		Utility.log( "onQueryTextChange: " + newText );
		return false;
	}
	
	// iab donate
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = 
			new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			if (result.isFailure()) {
				Utility.loge("onIabPurchaseFinished: Support SKU not purchased: " + result.getMessage() );
			}      
			else if (purchase.getSku().equals(SKU)) {
				mIabHelper.queryInventoryAsync(mReceivedInventoryListener);
			} 
		}
	};

	IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = 
			new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			Utility.log("onQueryInventoryFinished: " + result.getMessage() );
			if (!result.isFailure()) {
				Purchase p = inventory.getPurchase(SKU);
				if(p!=null) {
					Utility.log("onQueryInventoryFinished: consumeAsync" );
					mIabHelper.consumeAsync(inventory.getPurchase(SKU), mConsumeFinishedListener);
				}
			}
	    }
	};
	
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = 
			new IabHelper.OnConsumeFinishedListener() {
				public void onConsumeFinished(Purchase purchase, IabResult result) {
				   Utility.log("onConsumeFinished: " + result.getMessage() );
			   }
			};
}

