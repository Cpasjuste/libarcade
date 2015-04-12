package fr.mydedibox.libarcade.emulator.input;

import com.greatlittleapps.utility.*;

import fr.mydedibox.libarcade.emulator.activity.Main;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Vibrator;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import android.widget.RelativeLayout;
import android.view.View.OnTouchListener;

@SuppressLint("ViewConstructor")
public class SoftwareInputView extends RelativeLayout implements IButtons, OnTouchListener
{
	private static final int INVALID_POINTER_ID = -1;
	
	private boolean mActive = true;
	private boolean editMode = false;
	private int mButtonCount = 4;
	
	private final Context mCtx;
	private final View parent;
	private SoftwareButtonList buttons;
	private SoftwareStick mStick;
	
	private Vibrator vibrator = null;
	private boolean vibrate = true;
	
	private int mActionPrev = STICK_NONE;
	private int mInputData = 0;
	private int mInputDataLast = 0;
	private Display display;
	
	Paint paint = new Paint();
	
	public SoftwareInputView( Display _display, String packageName, View pParent, int pButtonCount, boolean pVibrate, boolean pVertical ) 
	{
		super( pParent.getContext() );
		mCtx = pParent.getContext();
		parent = pParent;
		display = _display;
		
		setWillNotDraw( false );		
	
		paint.setARGB(255, 255, 255, 255);
		
		vibrator = (Vibrator) mCtx.getSystemService(Context.VIBRATOR_SERVICE);
		vibrate = pVibrate;
		
		mButtonCount = pButtonCount;	
		
		mStick = new SoftwareStick( pButtonCount, parent );
		buttons = new SoftwareButtonList( packageName, parent, mButtonCount );
		
		setOnTouchListener( this );
	}

	@SuppressWarnings("deprecation")
	@Override 
	protected void onLayout( boolean changed, int l, int t, int r, int b )
	{
		//if( changed )
		{
			Utility.log( changed + ": "+parent.getWidth()+":"+parent.getHeight() );
			super.onLayout( changed, parent.getLeft(), parent.getTop(), parent.getRight(), parent.getBottom() );
			Utility.log( "this view: " + getWidth()+":"+getHeight() );
			mStick.load( display.getOrientation() );
			buttons.load( display.getOrientation() );
			paint.setAlpha( mStick.stick.getAlpha() );
			invalidate();
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
	{
		//Utility.log( "onMeasure: " + parent.getWidth() + "x" + parent.getHeight() );
		if( display != null )
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				Point size = new Point();
				display.getRealSize(size);
				Utility.log( size.x + "x" + size.y );
				setMeasuredDimension( size.x, size.y );
			}
			else
			{
				Utility.log( display.getWidth() + "x" + display.getHeight() );
				setMeasuredDimension( display.getWidth(), display.getHeight() );
			}
		}
	}

	
	@Override
    protected void onDraw( Canvas canvas ) 
	{
		//Utility.log( "onDraw" );
		super.onDraw( canvas );

		//paint.setAlpha( mStick.stick.getAlpha() );
		
		canvas.drawBitmap( mStick.stickBottom.getBitmap(), null, mStick.stickBottom.getBounds(), paint );
		canvas.drawBitmap( mStick.stick.getBitmap(), null, mStick.stick.getBounds(), paint );
		for( CustomDrawable d : buttons.getButtons() )
			canvas.drawBitmap( d.getBitmap(), null, d.getBounds(), paint );
	}
	
	public void setEditMode( boolean enabled )
	{
		editMode = enabled;
	}

	public void setActive( boolean pValue )
	{
		mActive = pValue;
	}
	
	public void setAlpha( int pAlpha )
	{
		mStick.setAlpha( pAlpha );
		buttons.setAlpha( pAlpha );
		paint.setAlpha( pAlpha );
		invalidate();	
	}
	public int getStickAlpha()
	{
		return mStick.stick.getAlpha();
	}
	public void setVibration( boolean value )
	{
		vibrate = value;
	}
	
	public void setButtonsScale( float scale )
	{
		buttons.setScale(scale);
		invalidate();
	}
	public void setStickScale( float scale )
	{
		mStick.setScale(scale);
		invalidate();
	}
	public float getButtonsScale()
	{
		return buttons.getButtons().get(0).getScale();
	}
	public float getStickScale()
	{
		return mStick.stick.getScale();
	}
	
	@SuppressWarnings("deprecation")
	public void save( int bcount )
	{
		Utility.log( "saving new input configuration!" );
		mStick.save( bcount, display.getOrientation() );
		buttons.save( bcount,  display.getOrientation() );
		invalidate();
		
	}
	
	private void handleStick( int event, int x, int y )
	{
		if( editMode )
		{
			mStick.setCenter( x, y );
			invalidate();
			return;
		}
		
		final Rect[] rects = ( event == MotionEvent.ACTION_DOWN ? mStick.mStickRectDirection : mStick.mScreenRectDirection );
		
		mInputData &= ~BTN_UP;
		mInputData &= ~BTN_LEFT;
		mInputData &= ~BTN_DOWN;
		mInputData &= ~BTN_RIGHT;
		
		if( rects[STICK_UP_LEFT].contains( x, y ) )
		{
			//Utility.log( "STICK_UP_LEFT" );
			if( mActionPrev != IButtons.STICK_UP_LEFT )
			{
				//mStick.setImageDrawable( mStickUPLEFT );
				mStick.setStickCenter( mStick.mStickRectDirection[STICK_UP_LEFT].left+(int)((float)mStick.stick.getWidth()/1.5f),
						mStick.mStickRectDirection[STICK_UP_LEFT].top+(int)((float)mStick.stick.getHeight()/1.5f) );
				invalidate();
				if( vibrate )
					vibrator.vibrate(15);
			}
			mInputData |= BTN_UP;
			mInputData |= BTN_LEFT;
			mActionPrev = IButtons.STICK_UP_LEFT;
		}
		else if( rects[STICK_UP].contains( x, y ) )
		{
			//Utility.log( "STICK_UP" );
			if( mActionPrev != IButtons.STICK_UP )
			{
				//mStick.setImageDrawable( mStickUP );
				mStick.setStickCenter( mStick.mStickRectDirection[STICK_UP].centerX(), 
						mStick.mStickRectDirection[STICK_UP].top+mStick.stick.getHeight()/2 );
				invalidate();
				if( vibrate )
					vibrator.vibrate(15);
			}
			mInputData |= BTN_UP;
			mActionPrev = IButtons.STICK_UP;
		}
		else if( rects[STICK_UP_RIGHT].contains( x, y ) )
		{
			//Utility.log( "STICK_UP_RIGHT" );
			if( mActionPrev != IButtons.STICK_UP_RIGHT )
			{
				//mStick.setImageDrawable( mStickUPRIGHT );
				mStick.setStickCenter( mStick.mStickRectDirection[STICK_UP_RIGHT].right-(int)((float)mStick.stick.getWidth()/1.5f),
						mStick.mStickRectDirection[STICK_UP_RIGHT].top+(int)((float)mStick.stick.getHeight()/1.5f) );
				invalidate();
				if( vibrate )
					vibrator.vibrate(15);
			}
			mInputData |= BTN_UP;
			mInputData |= BTN_RIGHT;
			mActionPrev = IButtons.STICK_UP_RIGHT;
		}
		else if( rects[STICK_RIGHT].contains( x, y ) )
		{
			//Utility.log( "STICK_RIGHT" );
			if( mActionPrev != IButtons.STICK_RIGHT )
			{
				//mStick.setImageDrawable( mStickRIGHT );
				mStick.setStickCenter( mStick.mStickRectDirection[STICK_RIGHT].right-mStick.stick.getWidth()/2, 
						mStick.mStickRectDirection[STICK_RIGHT].centerY() );
				invalidate();
				if( vibrate )
					vibrator.vibrate(15);
			}
			mInputData |= BTN_RIGHT;
			mActionPrev = IButtons.STICK_RIGHT;
		}
		else if( rects[STICK_DOWN_RIGHT].contains( x, y ) )
		{
			//Utility.log( "STICK_DOWN_RIGHT" );
			if( mActionPrev != IButtons.STICK_DOWN_RIGHT )
			{
				//mStick.setImageDrawable( mStickDOWNRIGHT );
				mStick.setStickCenter( mStick.mStickRectDirection[STICK_DOWN_RIGHT].right-(int)((float)mStick.stick.getWidth()/1.5f), 
						mStick.mStickRectDirection[STICK_DOWN_RIGHT].bottom-(int)((float)mStick.stick.getHeight()/1.5f) );
				invalidate();
				if( vibrate )
					vibrator.vibrate(15);
			}
			mInputData |= BTN_DOWN;
			mInputData |= BTN_RIGHT;
			mActionPrev = IButtons.STICK_DOWN_RIGHT;
		}
		else if( rects[STICK_DOWN].contains( x, y ) )
		{
			//Utility.log( "STICK_DOWN" );
			if( mActionPrev != IButtons.STICK_DOWN )
			{
				//mStick.setImageDrawable( mStickDOWN );
				mStick.setStickCenter( mStick.mStickRectDirection[STICK_DOWN].centerX(), 
						mStick.mStickRectDirection[STICK_DOWN].bottom-mStick.stick.getHeight()/2 );
				invalidate();
				if( vibrate )
					vibrator.vibrate(15);
			}
			mInputData |= BTN_DOWN;
			mActionPrev = IButtons.STICK_DOWN;
		}
		else if( rects[STICK_DOWN_LEFT].contains( x, y ) )
		{
			//Utility.log( "STICK_DOWN_LEFT" );
			if( mActionPrev != IButtons.STICK_DOWN_LEFT )
			{
				//mStick.setImageDrawable( mStickDOWNLEFT );
				mStick.setStickCenter( mStick.mStickRectDirection[STICK_DOWN_LEFT].left+(int)((float)mStick.stick.getWidth()/1.5f), 
						mStick.mStickRectDirection[STICK_DOWN_LEFT].bottom-(int)((float)mStick.stick.getHeight()/1.5f) );
				invalidate();
				if( vibrate )
					vibrator.vibrate(15);
			}
			mInputData |= BTN_DOWN;
			mInputData |= BTN_LEFT;
			mActionPrev = IButtons.STICK_DOWN_LEFT;
		}
		else if( rects[STICK_LEFT].contains( x, y ) )
		{
			//Utility.log( "STICK_LEFT" );
			if( mActionPrev != IButtons.STICK_LEFT )
			{
				//mStick.setImageDrawable( mStickLEFT );
				mStick.setStickCenter( mStick.mStickRectDirection[STICK_LEFT].left + mStick.stick.getWidth()/2, 
						mStick.mStickRectDirection[STICK_LEFT].centerY() );
				invalidate();
				if( vibrate )
					vibrator.vibrate(15);
			}
			mInputData |= BTN_LEFT;
			mActionPrev = IButtons.STICK_LEFT;
		}
	}

	@Override
	public boolean onTouch( View v, MotionEvent ev ) 
	{
		if( ! mActive )
			return true;
		
        final int action = ev.getAction();
        final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    	final int pointerId = ev.getPointerId(pointerIndex);
    	
        switch ( action & MotionEvent.ACTION_MASK ) 
        {
        	case MotionEvent.ACTION_DOWN:
        	case MotionEvent.ACTION_POINTER_DOWN:
        	{
        		final int x = ( action & MotionEvent.ACTION_MASK ) == MotionEvent.ACTION_DOWN ? (int)ev.getX() : (int)ev.getX( pointerIndex );
        		final int y = ( action & MotionEvent.ACTION_MASK ) == MotionEvent.ACTION_DOWN ? (int)ev.getY() : (int)ev.getY( pointerIndex );
	        		
        		if( mStick.stickBottom.getBounds().contains( x, y ) )
        		{
        			mStick.mStickPointerID = pointerId;
        			handleStick( MotionEvent.ACTION_DOWN, x, y );
        		}
        		else
        		{
        			for( CustomDrawable button : buttons.getButtons() )
        			{
        				if( button.getBounds().contains( x, y ) )
        				{
        					button.pointer = pointerId;
        					mInputData |= button.id;
        					if( vibrate )
        						vibrator.vibrate(15);
        					break;
        				}
        			}
        		}
        		break;
        	}

	    	case MotionEvent.ACTION_MOVE: 
	    	case MotionEvent.ACTION_OUTSIDE:
	    	{
	    		final int index = ev.findPointerIndex( mStick.mStickPointerID );
	    		if( index != INVALID_POINTER_ID )
	    		{
	    			final int x = (int)ev.getX( index );
		    		final int y = (int)ev.getY( index );
		    		handleStick( MotionEvent.ACTION_MOVE, x, y );
	    		}
	    		
	    		if( editMode )
				{
	    			final int x = ( action & MotionEvent.ACTION_MASK ) == MotionEvent.ACTION_MOVE ? (int)ev.getX() : (int)ev.getX( pointerIndex );
	    			final int y = ( action & MotionEvent.ACTION_MASK ) == MotionEvent.ACTION_MOVE ? (int)ev.getY() : (int)ev.getY( pointerIndex );
	    			for( CustomDrawable button : buttons.getButtons() )
        			{
        				if( button.getBounds().contains( x, y ) )
        				{
							buttons.setCenter( button.id, x, y );
							break;
        				}
        			}
	    			invalidate();
				}
	    		break;    
	    	}
		        
	    	case MotionEvent.ACTION_UP:
	    	case MotionEvent.ACTION_POINTER_UP:
	    	case MotionEvent.ACTION_CANCEL:
	    	{
	    		if( pointerId == mStick.mStickPointerID )
	    		{
	    			//Utility.log( "Stick released" );
	    			//if( mActionPrev != IButtons.STICK_NONE )
	    			//	mStick.setImageDrawable( mStickDrawable );
	    			
	    			if( !editMode )
	    			{
	    				mStick.setStickCenter( mStick.stickBottom.getCenter().x, mStick.stickBottom.getCenter().y );
	    				invalidate();
	    			}
	    			
	    			mInputData &= ~BTN_UP;
	    			mInputData &= ~BTN_LEFT;
	    			mInputData &= ~BTN_DOWN;
	    			mInputData &= ~BTN_RIGHT;
	    			mStick.mStickPointerID = INVALID_POINTER_ID;
	    			mActionPrev = IButtons.STICK_NONE;
	    		}

	    		for( CustomDrawable button : buttons.getButtons() )
    			{
    				if( pointerId == button.pointer )
    				{
    					button.pointer = INVALID_POINTER_ID;
    					mInputData &= ~button.id;
    					break;
    				}
    			}
	    		break;
	    	}
        }
        
        if( !editMode )
        {
        	if( mInputDataLast != mInputData )
        	{
        		mInputDataLast = mInputData;
        		Main.setPadData( 0, mInputData );
        	}
        }
        
		return true;
	}
}

