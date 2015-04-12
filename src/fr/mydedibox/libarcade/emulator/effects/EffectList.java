package fr.mydedibox.libarcade.emulator.effects;

import java.util.ArrayList;

import fr.mydedibox.libarcade.R;

public class EffectList 
{
	public enum ScreenSize {
		EFFECT_ORIGINALSCREEN,
		EFFECT_2X,
		EFFECT_43,
		EFFECT_FITSCREEN,
		EFFECT_FULLSCREEN
	}
	
	public static final String effect_none_name = "none";
	
	public static final int effect_scanlines100_drawable = R.drawable.effect_scanline_100;
	//public static final int effect_scanlines100_drawable = R.drawable.effect_scanline_test;
	public static final String effect_scanlines100_name = "scanlines_100";
	
	public static final int effect_scanlines75_drawable = R.drawable.effect_scanline_75;
	public static final String effect_scanlines75_name = "scanlines_75";
	
	public static final int effect_scanlines50_drawable = R.drawable.effect_scanline_50;
	public static final String effect_scanlines50_name = "scanlines_50";
	
	public static final int effect_scanlines25_drawable = R.drawable.effect_scanline_25;
	public static final String effect_scanlines25_name = "scanlines_25";
	
	public static final int effect_crt1_25_drawable = R.drawable.effect_crt1_25;
	public static final String effect_crt1_25_name = "crt1_25";
	
	public static final int effect_crt1_10_drawable = R.drawable.effect_crt1_10;
	public static final String effect_crt1_10_name = "crt1_10";
	
	public static final int effect_crt2_25_drawable = R.drawable.effect_crt2_25;
	public static final String effect_crt2_25_name = "crt2_25";
	
	public static final int effect_crt2_10_drawable = R.drawable.effect_crt2_10;
	public static final String effect_crt2_10_name = "crt2_10";
	
	public static final int effect_twisty_drawable = R.drawable.effect_twisty;
	public static final String effect_twisty_name = "twisty";

	private ArrayList<EffectList> list;
	private String name;
	private int id;
	
	public EffectList()
	{
		list = new ArrayList<EffectList>();
		list.add( new EffectList( 0, effect_none_name ) );
		list.add( new EffectList( effect_scanlines100_drawable, effect_scanlines100_name ) );
		list.add( new EffectList( effect_scanlines75_drawable, effect_scanlines75_name ) );
		list.add( new EffectList( effect_scanlines50_drawable, effect_scanlines50_name ) );
		list.add( new EffectList( effect_scanlines25_drawable, effect_scanlines25_name ) );
		list.add( new EffectList( effect_crt1_10_drawable, effect_crt1_10_name ) );
		list.add( new EffectList( effect_crt1_25_drawable, effect_crt1_25_name ) );
		list.add( new EffectList( effect_crt2_10_drawable, effect_crt2_10_name ) );
		list.add( new EffectList( effect_crt2_25_drawable, effect_crt2_25_name ) );
		list.add( new EffectList( effect_twisty_drawable, effect_twisty_name ) );
	}
	
	private EffectList( int pId, String pName )
	{
		this.id = pId;
		this.name = pName;
	}
	
	public ArrayList<EffectList> getList()
	{
		return this.list;
	}
	
	public CharSequence[] getCharSequenceList()
	{
		int size = this.list.size();
		CharSequence[] seq = new CharSequence[size];
		for(int i=0; i<size;i++)
		{
			seq[i] = this.list.get(i).getName();
		}
		return seq;
	}
	
	public EffectList getByName( String pEffectName )
	{
		for(int i=0; i<this.list.size();i++)
		{
			if( pEffectName.contentEquals( this.list.get(i).getName() ) )
				return this.list.get(i);
		}
		return this.list.get(0);
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getID()
	{
		return this.id;
	}
}
