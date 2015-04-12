package fr.mydedibox.libarcade.objects;

import java.io.Serializable;

public class ScreenInfo implements Serializable
{
	private static final long serialVersionUID = 3489844511903291507L;
	
	public final static int HORIZONTAL = 0;
	public final static int VERTICAL = 1;
	
	int width = 320;
	int height = 240;
	int orientation = HORIZONTAL;
	
	public ScreenInfo( int pScreenWidth, int pScreenHeight, int pOrientation )
	{
		this.width = pScreenWidth;
		this.height = pScreenHeight;
		this.orientation = pOrientation;
	}
	
	public int GetWidth()
	{
		return this.width;
	}
	public int GetHeight()
	{ 
		return this.height;
	}
	public int GetOrientation()
	{ 
		return this.orientation;
	}
	public boolean isVertical()
	{
		return this.orientation == VERTICAL ? true : false;
	}
}
