package fr.mydedibox.libarcade.emulator.effects;

import fr.mydedibox.libarcade.emulator.utility.EmuPreferences;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class Effect extends RelativeLayout 
{
	private final Context mCtx;
	private EmuPreferences mPrefs;
	
	public Effect(Context context) 
	{
		super(context);
		mCtx = context;
		init();
	}

	public Effect(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		mCtx = context;
		init();
	}

	public Effect(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		mCtx = context;
		init();
	}
	
	public void init()
	{
		mPrefs = new EmuPreferences( mCtx );
		
	}
	
	@SuppressWarnings("deprecation")
	public void applyEffect( RelativeLayout.LayoutParams pParams, final EffectList pEffect )
	{
		if( mPrefs.getEffectFast().contentEquals( EffectList.effect_none_name ) )
		{
			setBackgroundDrawable( null );
			setLayoutParams( pParams );
		}
		else
		{
			Bitmap bitmap = BitmapFactory.decodeResource( getResources(), pEffect.getID() );
			BitmapDrawable background = new BitmapDrawable( bitmap );
			background.setTileModeXY( android.graphics.Shader.TileMode.REPEAT, android.graphics.Shader.TileMode.REPEAT );
			if( pEffect.getName().contentEquals( EffectList.effect_twisty_name ) )
				background.setAlpha( 75 );
			setBackgroundDrawable( background );	
			setLayoutParams( pParams );
		}
	}
}
