package fr.mydedibox.libarcade.objects;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.greatlittleapps.utility.Utility;

public class RomInfo implements Serializable
{
	private static final long serialVersionUID = -3312923520972496936L;
	
	public final static int WORKING = 0;
	public final static int NOT_WORKING = 1;
	public final static int PARTIAL = 2;
	
	private String title;
	private String name;
	private String parent = null;
	private String filename;
	private String comment;
	private String manufacturer;
	private String system;
	private String year;
	private int status;
	private int buttons;
	private ScreenInfo screen;
	
	private String googleurl;
	//private int resid = 0;
	
	public RomInfo( String ptitle, String pname, String pparent, String pcomment,
					String pmanufacturer, String psystem, String pyear, int pstatus, int pbuttons,
					int pwidth, int pheight, int porientation )
	{
		this.title = ptitle;
		this.name = pname;
		this.parent = pparent;
		this.filename = pname + ".zip";
		this.comment = pcomment;
		this.manufacturer = pmanufacturer;
		this.system = psystem;
		this.year = pyear;
		this.status = pstatus;
		this.buttons = pbuttons;
		this.screen = new ScreenInfo( pwidth, pheight, porientation );
		
		try 
		{
			this.googleurl = "http://www.google.com/search?q=" + URLEncoder.encode(  this.title + "+" + this.filename, "UTF-8" );
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}
	
	public String GetTitle()
	{
		return this.title;
	}
	public String GetName()
	{
		return this.name;
	}
	public String GetParent()
	{
		return this.parent;
	}
	public String GetFilename()
	{
		return this.filename;
	}
	public String GetComment()
	{
		return this.comment;
	}
	public String GetManufacturer()
	{
		return this.manufacturer;
	}
	public String GetSystem()
	{
		return this.system;
	}
	public int GetYear()
	{	
		return Utility.parseInt( this.year );
	}
	public int GetStatus()
	{
		return this.status;
	}
	public int GetButtonCount()
	{
		return this.buttons;
	}
	public ScreenInfo GetScreenResolution()
	{
		return this.screen;
	}
	public String GetGoogleLink()
	{
		return this.googleurl;
	}
}
