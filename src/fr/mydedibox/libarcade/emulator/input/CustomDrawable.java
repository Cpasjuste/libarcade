package fr.mydedibox.libarcade.emulator.input;

import com.greatlittleapps.utility.*;

import fr.mydedibox.libarcade.emulator.sdl.SDLJni;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;

public class CustomDrawable 
{
	SharedPreferences prefs;
	BitmapDrawable drawable;
	Rect bounds = new Rect();
	Point center = new Point();
	ButtonData data = new ButtonData();
	int alpha = 100;
	float scale = 1;
	public int id = 0;
	public int pointer = IButtons.INVALID_POINTER_ID;
	
	public CustomDrawable( Context ctx, int resid, int button_id )
	{
		prefs = PreferenceManager.getDefaultSharedPreferences( ctx );
		id = button_id;
		drawable = (BitmapDrawable)ctx.getResources().getDrawable( resid );
		drawable.setBounds( 0, 0, drawable.getBitmap().getWidth(), drawable.getBitmap().getHeight() );
		bounds = drawable.getBounds();
		center.set( bounds.centerX(), bounds.centerY() );
	}
	
	public void setCenter( int x, int y )
	{
		bounds.set( x-bounds.width()/2, y-bounds.height()/2, x+bounds.width()/2, y+bounds.height()/2 );
		center.set( bounds.centerX(), bounds.centerY() );
	}
	public void setPosition( int x, int y )
	{
		bounds.set( x, y, x+bounds.width(), y+bounds.height() );
		center.set( bounds.centerX(), bounds.centerY() );
	}
	
	public void setScale( float _scale )
	{
		Point c = new Point( center ); // save center
		
		//Utility.log( "setScaleOLD: "+getWidth()+"x"+getHeight()+" (scale="+scale+")" );
		
		scale = _scale;
		bounds.set( 
				bounds.left,
				bounds.top,
				bounds.left+(int)((float)drawable.getBitmap().getWidth()*scale), 
				bounds.top+(int)((float)drawable.getBitmap().getHeight()*scale) 
				);
		
		//Utility.log( "setScaleNEW: "+getWidth()+"x"+getHeight()+" (scale="+scale+")" );
		
		setCenter( c.x, c.y ); // restore center
	}
	public float getScale()
	{
		return this.scale;
	}
	public void setVisibility( boolean pVisible )
	{
		this.drawable.setVisible( pVisible, false );
	}
	public Bitmap getBitmap()
	{
		return this.drawable.getBitmap();
	}
	public Rect getBounds()
	{
		return this.bounds;
	}
	public Point getCenter()
	{
		return center;
	}
	public BitmapDrawable getDrawable()
	{
		return this.drawable;
	}
	public int getAlpha()
	{
		return this.alpha;
	}
	public void setAlpha( int _alpha )
	{
		alpha = _alpha;
		this.drawable.setAlpha( alpha );
	}
	
	public int getWidth()
	{
		return this.bounds.width();
	}
	public int getHeight()
	{
		return this.bounds.height();
	}
	
	public boolean load( int button_count, int orientation )
	{
		//Utility.log( button_count+", "+orientation );
		
		data = (ButtonData) Utility.unserialize( prefs.getString( createID(orientation), null ) );
		if( data != null )
		{
			Utility.log( "custom configuration found for: "+ SDLJni.rom + ", orientation: "+orientation );
			setCenter( data.x, data.y );
			setScale( data.s );
			setAlpha( data.a );
			return true;
		}
		
		data = (ButtonData) Utility.unserialize( prefs.getString( createIDDefault( button_count, orientation), null ) );
		if( data != null )
		{
			Utility.log( "default configuration found for buttons: "+ button_count + ", orientation: "+orientation );
			setCenter( data.x, data.y );
			setScale( data.s );
			setAlpha( data.a );
			return true;
		}
		
		Utility.log( "default configuration not found" );
		return false;
	}
	
	
	public void save( int button_count, int orientation )
	{
		if( data == null ) 
			data = new ButtonData();
			//|| data.x != getCenter().x || data.y != getCenter().y 
			//	|| data.s != scale || data.a != alpha )
		{
			Utility.log( createID(orientation)+" -> x="+getCenter().x+":y="+getCenter().y+":s="+scale+":a="+alpha );
	
			if( button_count > -1 )
			{
				if( !prefs.edit().putString( createIDDefault(button_count, orientation), getData() ).commit() )
					Utility.loge( "save input configuration failed (button_count > -1)" );
			}
			else
			{
				if( !prefs.edit().putString( createID(orientation), getData() ).commit() )
					Utility.loge( "save input configuration failed" );
			}
		}
	}
	
	public void reset( int orientation )
	{
		prefs.edit().remove( createID(orientation) ).commit();
	}
	
	private String createID( int orientation )
	{
		return SDLJni.rom+String.valueOf(id)+String.valueOf(orientation);
	}
	
	private String createIDDefault( int button_count, int orientation )
	{
		return button_count+String.valueOf(id)+String.valueOf(orientation);
	}
	
	
	private String getData()
	{
		if( data == null )
			data = new ButtonData();
		
		data.x = getCenter().x;
		data.y = getCenter().y;//data.y == getCenter().y ? getCenter().y : getCenter().y + Main.actionBarHeight;
		data.s = scale;
		data.a = alpha;
		return Utility.serialize( data );
	}
}

