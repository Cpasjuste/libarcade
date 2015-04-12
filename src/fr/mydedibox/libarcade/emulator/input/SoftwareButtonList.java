package fr.mydedibox.libarcade.emulator.input;

import java.util.ArrayList;

import com.greatlittleapps.utility.Utility;
import fr.mydedibox.libarcade.R;
import android.view.View;

public class SoftwareButtonList implements IButtons
{
	private ArrayList<CustomDrawable> buttons;
	private final View parent;
	int buttons_count = -1;
	
	public SoftwareButtonList( final String packageName, final View view, int pButtonCount )
	{
		Utility.log( "SoftwareButtonList: parent view: " + view.getWidth()+":"+view.getHeight() ); 
		parent = view;
		buttons = new ArrayList<CustomDrawable>();
		buttons_count = pButtonCount;
		
		buttons.add( new CustomDrawable( parent.getContext(), R.drawable.button_start, BTN_START ) );
		buttons.get( 0 ).setPosition( 0, 0 );
		buttons.add( new CustomDrawable( parent.getContext(), R.drawable.button_coins, BTN_COINS ) );	
		buttons.get( 1 ).setPosition( 0, buttons.get(0).getHeight() );
		
		for( int i=0; i<pButtonCount;i++ )
		{
			int num = i+1;
			
			String button_name = "button_"+num;
			
			Utility.log( "CustomDrawable: loading " + button_name + " - package="+packageName );
			
			CustomDrawable button = 
					new CustomDrawable( parent.getContext(), parent.getResources().getIdentifier( button_name, "drawable", packageName ), 0 );
			
			if( num == 1 )
				button.id = BTN_1;
			else if( num == 2 )
				button.id = BTN_2;
			else if( num == 3 )
				button.id = BTN_3;
			else if( num == 4 )
				button.id = BTN_4;
			else if( num == 5 )
				button.id = BTN_5;
			else if( num == 6 )
				button.id = BTN_6;		
			
			buttons.add( button );
		}
	}
	
	public CustomDrawable getButton( int pButton )
	{
		for( CustomDrawable d : buttons )
		{
			if( d.id == pButton )
			{
				return d;
			}
		}
		return null;
	}
	
	public void setCenter( int pButton, int x, int y )
	{
		for( CustomDrawable d : buttons )
		{
			if( d.id == pButton )
			{
				d.setCenter(x, y);
				break;
			}
		}
	}
	
	public ArrayList<CustomDrawable> getButtons()
	{
		return this.buttons;
	}
	
	public void setAlpha( int pAlpha )
	{
		for( CustomDrawable d : buttons )
			d.setAlpha( pAlpha );
	}
	
	public void setVisibility( boolean pVisibility )
	{
		for( CustomDrawable d : buttons )
			d.setVisibility( pVisibility );
	}
	
	public void setScale( float pScale )
	{
		for( CustomDrawable d : buttons )
			d.setScale( pScale );
	}
	
	public void load( int orientation )
	{
		boolean loaded = false;
		
		for( CustomDrawable d : buttons )
		{
			loaded = d.load( buttons_count, orientation );
			if( !loaded )
				break;
		}
		
		if( !loaded )
			updateRects();
	}
	
	public void save( int bcount, int orientation )
	{
		for( CustomDrawable d : buttons )
			d.save( bcount, orientation );
	}

	private void updateRects()
	{
		int count = buttons.size();
		
		buttons.get( 0 ).setPosition( 0, 0 );
		buttons.get( 1 ).setPosition( 0, buttons.get(0).getHeight() );
		
		for( int i=0; i<count;i++ )
		{
			CustomDrawable button = buttons.get(i);
			
			switch( count-2 )
			{
				case 1:
					if( button.id == BTN_1 )
						button.setPosition( parent.getWidth()-button.getWidth()*2, parent.getHeight()-button.getHeight() );
				break;
			
				case 2:
					if( button.id == BTN_1 )
						button.setPosition( parent.getWidth()-button.getWidth()*2, parent.getHeight()-button.getHeight() );
					else if( button.id == BTN_2 )
						button.setPosition( parent.getWidth()-button.getWidth(), parent.getHeight()-button.getHeight() );
				break;
				
				case 3:
					if( button.id == BTN_1 )
						button.setPosition( parent.getWidth()-button.getWidth()*3, parent.getHeight()-button.getHeight() );
					else if( button.id == BTN_2 )
						button.setPosition( parent.getWidth()-button.getWidth()*2, parent.getHeight()-button.getHeight() );
					else if( button.id == BTN_3 )
						button.setPosition( parent.getWidth()-button.getWidth(), parent.getHeight()-button.getHeight() );
				break;
					
				case 4:
					if( button.id == BTN_1 )
						button.setPosition( parent.getWidth()-button.getWidth()*2, parent.getHeight()-button.getHeight() );
					else if( button.id == BTN_2 )
						button.setPosition( parent.getWidth()-button.getWidth(), parent.getHeight()-button.getHeight() );
					else if( button.id == BTN_3 )
						button.setPosition( parent.getWidth()-button.getWidth()*2, parent.getHeight()-button.getHeight()*2 );
					else if( button.id == BTN_4 )
						button.setPosition( parent.getWidth()-button.getWidth(), parent.getHeight()-button.getHeight()*2 );
				break;
					
				case 5:
					if( button.id == BTN_1 )
						button.setPosition( parent.getWidth()-button.getWidth()*3, parent.getHeight()-button.getHeight() );
					else if( button.id == BTN_2 )
						button.setPosition( parent.getWidth()-button.getWidth()*2, parent.getHeight()-button.getHeight() );
					else if( button.id == BTN_3 )
						button.setPosition( parent.getWidth()-button.getWidth(), parent.getHeight()-button.getHeight() );
					else if( button.id == BTN_4 )
						button.setPosition( parent.getWidth()-button.getWidth()*3, parent.getHeight()-button.getHeight()*2 );
					else if( button.id == BTN_5 )
						button.setPosition( parent.getWidth()-button.getWidth()*2, parent.getHeight()-button.getHeight()*2 );
				break;
				
				case 6:
					if( button.id == BTN_1 )
						button.setPosition( parent.getWidth()-button.getWidth()*3, parent.getHeight()-button.getHeight() );
					else if( button.id == BTN_2 )
						button.setPosition( parent.getWidth()-button.getWidth()*2, parent.getHeight()-button.getHeight() );
					else if( button.id == BTN_3 )
						button.setPosition( parent.getWidth()-button.getWidth(), parent.getHeight()-button.getHeight() );
					else if( button.id == BTN_4 )
						button.setPosition( parent.getWidth()-button.getWidth()*3, parent.getHeight()-button.getHeight()*2 );
					else if( button.id == BTN_5 )
						button.setPosition( parent.getWidth()-button.getWidth()*2, parent.getHeight()-button.getHeight()*2 );
					else if( button.id == BTN_6 )
						button.setPosition( parent.getWidth()-button.getWidth(), parent.getHeight()-button.getHeight()*2 );
				break;
			}
			//Utility.log( "button["+button.id+"] updated: " + button.getBounds().left+":"+button.getBounds().top + " (width:"+button.getWidth()+")" );
		}
	}
}


