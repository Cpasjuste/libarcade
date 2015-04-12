package fr.mydedibox.libarcade.emulator.input;

import fr.mydedibox.libarcade.R;
import android.graphics.Rect;
import android.view.View;

public class SoftwareStick implements IButtons
{
	private final View parent;
	
	public CustomDrawable stickBottom;
	public CustomDrawable stick;

	public Rect[] mStickRectDirection = new Rect[9]; // Stick touchable rect's for on touch
	public Rect[] mScreenRectDirection = new Rect[9]; // Screen touchable rect's for on move
	public int mStickPointerID = INVALID_POINTER_ID;
	int buttons_count = -1;
	
	public SoftwareStick( int buttons, final View view )
	{
		parent = view;
		buttons_count = buttons;
		stick = new CustomDrawable( parent.getContext(), R.drawable.stick, BTN_UP ); // BTN_UP and BTN_LEFT are unused integer so use it here as refererence
		stickBottom = new CustomDrawable( parent.getContext(), R.drawable.stick_bottom, BTN_LEFT );
		stick.setCenter( stickBottom.getCenter().x, stickBottom.getCenter().y );
	}
	
	public void setScale( float pScale )
	{
		stick.setScale(pScale);
		stickBottom.setScale(pScale);
	}
	
	public void setAlpha( int pAlpha )
	{
		stickBottom.setAlpha( pAlpha );
		stick.setAlpha( pAlpha );
	}
	
	public void setVisibility( boolean pVisibility )
	{
		stickBottom.setVisibility( pVisibility );
		stick.setVisibility( pVisibility );
	}
	
	public void setCenter( int x, int y )
	{
		stickBottom.setCenter( x, y );
		stick.setCenter( stickBottom.getCenter().x,  stickBottom.getCenter().y );
	}
	
	public void setStickCenter( int x, int y )
	{
		stick.setCenter( x, y );
	}

	public void load( int orientation )
	{
		boolean loaded = stick.load( buttons_count, orientation );
		if( !loaded )
		{
			stickBottom.setPosition( 0, parent.getHeight()-stickBottom.getHeight() );
			stick.setCenter( stickBottom.getCenter().x, stickBottom.getCenter().y );
			updateRects();
			return;
		}
		
		loaded = stickBottom.load( buttons_count, orientation );
		if( !loaded )
		{
			stickBottom.setPosition( 0, parent.getHeight()-stickBottom.getHeight() );
			stick.setCenter( stickBottom.getCenter().x, stickBottom.getCenter().y );
			updateRects();
			return;
		}
		updateRects();
	}
	
	public void save( int bcount, int orientation )
	{
		stick.save( bcount, orientation );
		stickBottom.save( bcount, orientation );
		updateRects();
	}
	
	void updateRects()
	{
		final int width = stickBottom.getWidth()/3;
		
		for( int i=0;i<9;i++ )
		{
			if( mStickRectDirection[i] == null )
				mStickRectDirection[i] = new Rect();
			
			if( mScreenRectDirection[i] == null )
				mScreenRectDirection[i] = new Rect();
		}
		
		// UP_LEFT
		mStickRectDirection[STICK_UP_LEFT].set( stickBottom.getBounds().left, stickBottom.getBounds().top, 
				stickBottom.getBounds().left + width, stickBottom.getBounds().top + width );
		mScreenRectDirection[STICK_UP_LEFT].set( 0, 0, 
				stickBottom.getBounds().left + width, stickBottom.getBounds().top + width );
		
		// UP_RIGHT
		mStickRectDirection[STICK_UP_RIGHT].set( stickBottom.getBounds().right - width, stickBottom.getBounds().top, 
				stickBottom.getBounds().right, stickBottom.getBounds().top + width  );
		mScreenRectDirection[STICK_UP_RIGHT].set( stickBottom.getBounds().right - width, 0, 
				parent.getWidth(), stickBottom.getBounds().top + width  );
		
		// DOWN_RIGHT
		mStickRectDirection[STICK_DOWN_RIGHT].set( mStickRectDirection[STICK_UP_RIGHT].left, stickBottom.getBounds().bottom - width, 
				stickBottom.getBounds().right, stickBottom.getBounds().bottom );
		mScreenRectDirection[STICK_DOWN_RIGHT].set( mStickRectDirection[STICK_UP_RIGHT].left, stickBottom.getBounds().bottom - width, 
				parent.getWidth(), parent.getHeight() );
		
		// DOWN_LEFT
		mStickRectDirection[STICK_DOWN_LEFT].set( stickBottom.getBounds().left, stickBottom.getBounds().bottom - width, 
				stickBottom.getBounds().left + width, stickBottom.getBounds().bottom );
		mScreenRectDirection[STICK_DOWN_LEFT].set( 0, stickBottom.getBounds().bottom - width, 
				stickBottom.getBounds().left + width, parent.getHeight() );
		
		
		mStickRectDirection[STICK_UP].set( mStickRectDirection[STICK_UP_LEFT].right, stickBottom.getBounds().top, 
				mStickRectDirection[STICK_UP_RIGHT].left, mStickRectDirection[STICK_UP_RIGHT].bottom );
		mScreenRectDirection[STICK_UP].set( mStickRectDirection[STICK_UP_LEFT].right, 0, 
				mStickRectDirection[STICK_UP_RIGHT].left, mStickRectDirection[STICK_UP_RIGHT].bottom );
		
		
		mStickRectDirection[STICK_DOWN].set( mStickRectDirection[STICK_DOWN_LEFT].right, mStickRectDirection[STICK_DOWN_LEFT].top, 
				mStickRectDirection[STICK_DOWN_RIGHT].left, stickBottom.getBounds().bottom );
		mScreenRectDirection[STICK_DOWN].set( mStickRectDirection[STICK_DOWN_LEFT].right, mStickRectDirection[STICK_DOWN_LEFT].top, 
				mStickRectDirection[STICK_DOWN_RIGHT].left, parent.getHeight() );
		
		
		mStickRectDirection[STICK_LEFT].set( stickBottom.getBounds().left, mStickRectDirection[STICK_UP_LEFT].bottom, 
				mStickRectDirection[STICK_UP_LEFT].right, mStickRectDirection[STICK_DOWN_LEFT].top );
		mScreenRectDirection[STICK_LEFT].set( 0, mStickRectDirection[STICK_UP_LEFT].bottom, 
				mStickRectDirection[STICK_UP_LEFT].right, mStickRectDirection[STICK_DOWN_LEFT].top );
		
		
		mStickRectDirection[STICK_RIGHT].set( mStickRectDirection[STICK_UP_RIGHT].left, mStickRectDirection[STICK_UP_RIGHT].bottom, 
				stickBottom.getBounds().right, mStickRectDirection[STICK_DOWN_RIGHT].top );
		mScreenRectDirection[STICK_RIGHT].set( mStickRectDirection[STICK_UP_RIGHT].left, mStickRectDirection[STICK_UP_RIGHT].bottom, 
				parent.getWidth(), mStickRectDirection[STICK_DOWN_RIGHT].top );
	}
}
