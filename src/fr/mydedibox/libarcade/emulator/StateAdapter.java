package fr.mydedibox.libarcade.emulator;

import fr.mydedibox.libarcade.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StateAdapter extends ArrayAdapter<StateInfo>
{
	private final Context mCtx;
	
	public synchronized void add( StateInfo object ) 
    {
        super.add(object);
    }
    
    public synchronized StateInfo getItem(int position) 
    {
        return super.getItem(position);
    }
    
    public synchronized void remove(StateInfo object) 
    {
        super.remove(object);
    }
    
    public synchronized void insert(StateInfo object, int index) 
    {
        super.insert(object, index);
    }
    
	public StateAdapter( Context context, int textViewResourceId )
    {
    	super( context, textViewResourceId );
    	mCtx = context;
    }
	
	@Override
    public View getView( int position, View convertView, ViewGroup parent )
    { 		
    	View v = convertView;
    	if ( v == null )
    	{
    		LayoutInflater vi = (LayoutInflater)mCtx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    		v = vi.inflate( R.layout.statelist, null );
    	}
           
    	final StateInfo state = getItem(position);
    	if ( state != null )
    	{
    		TextView name = (TextView) v.findViewById( R.id.file_name );
    		if( name != null )
    		{
    			name.setText( state.date );
    		}
    		TextView info = (TextView) v.findViewById( R.id.file_info );
    		if( info != null )
    		{
    			info.setVisibility( View.GONE );
    		}
    		ImageView icon = (ImageView) v.findViewById( R.id.file_icon );
    		if( icon != null )
    		{
    			icon.setImageDrawable( state.drawable );
    		}
    		ImageView delete = (ImageView) v.findViewById( R.id.state_delete );
    		if( delete != null )
    		{
    			if( state.date.contentEquals( "Create new save" ) )
    			{
    				delete.setVisibility( View.GONE );
    			}
    			else
    			{
    				delete.setVisibility( View.VISIBLE );
	    			delete.setOnClickListener( new OnClickListener()
	    			{
						@Override
						public void onClick(View v) 
						{
							new AlertDialog.Builder( mCtx )
							.setTitle( "Confirm" )
							.setCancelable( true )
							.setMessage( "\nReally delete this save state ?\n" )
							.setPositiveButton( "Confirm", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int whichButton)
								{
									state.delete();
									//updateStatesList();
									remove( state );
								}
							})
							.setNegativeButton( "Cancel", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int whichButton)
								{
								}
							}).create().show();
						}
	    			});
    			}
    		}
    	}
    	return v;
    }
}

