package fr.mydedibox.libarcade;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.greatlittleapps.utility.FileInfo;
import com.greatlittleapps.utility.Utility;

import fr.mydedibox.libarcade.objects.RomInfo;
import fr.mydedibox.libarcade.preferences.EmuPreferences;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ArcadeUtility 
{
	public static String getRomDescriptionOnline( String pRomName )
	{
		String descriptionFilePath = EmuPreferences.ROMINFO_PATH + "/" + pRomName + ".txt";
		String description = "No description available for this rom !";
		
		File descriptionFile = new File( descriptionFilePath );
		if( descriptionFile.exists() )
		{
			Utility.log( "Description found on sdcard" );
			return Utility.ReadFileAsString( descriptionFile.getAbsolutePath() );
		}

		Document doc = null;
		
		try 
		{
			try
			{
				doc = Jsoup.connect( "http://caesar.logiqx.com/php/history.php?id="+pRomName ).get();
			}
			catch (SocketException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return description;
			}
			Element element= doc.select( "table .news" ).first();
			if( element != null && element.hasText() && element.html() != null )
			{
				description =  element.html().replace( "<br /> <br /> <br />", "\n\n" )
						.replace( "<br /> <br />", "\n" )
						.replace( "&quot;", "\"" )
						.replace( "=&gt;", "Buttons:" )
						.replace( "= &gt;", "Buttons:" );
				description = description.substring( description.indexOf('\n')+1 );
				description = description.substring( description.indexOf('\n')+1 );
				description = description.substring( description.indexOf('\n')+1 );
				description = description.substring( description.indexOf('\n')+1 );
				description = description.replace( "</td>", "" );
				description = description.replace( "</tr>", "" );
				description = description.replace( "</tbody>", "" );
				
				// Save to file for offline view
				Utility.WriteStringToFile ( description, descriptionFilePath );
			}
			return description;
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return description;
		}
	}
	
	public static Bitmap GetIcon( Context ctx, RomInfo rom )
	{
		Bitmap bitmap = null;
		
		Resources r = ctx.getResources();
		
		String icon = rom.GetParent() == null ? rom.GetName() : rom.GetParent();
		if( Character.isDigit( icon.charAt(0) ) ) // fix resources cant start with numeric
			icon = "a"+icon;
		
		int id = r.getIdentifier( icon, "raw", ctx.getPackageName() );
		if( id > 0 )
			bitmap = BitmapFactory.decodeResource( r, id );

		return bitmap;
	}
	
	public static Bitmap[] getScreenshots( final FileInfo pFile )
	{
		Bitmap[] bitmaps = new Bitmap[2];
		unzip( pFile, bitmaps );
		return bitmaps;
	};
	
	private static boolean unzip( final FileInfo pFile, Bitmap[] bitmaps )
 	{ 
		final FileInfo file = pFile;
		final RomInfo rom = (RomInfo)file.getCustomData();
		
		File data = new File( EmuPreferences.DATA_PATH+"/"+"data.zip" );
		if( !data.exists() || rom == null )
			return false;
			
 		try  
 		{
 			String titleName = rom.GetParent() == null ? rom.GetName() : rom.GetParent();
 			titleName += ".png";
 			String titleZipPath = "titles/" + titleName;
 			
 			String previewName = rom.GetParent() == null ? rom.GetName() : rom.GetParent();
 			previewName += ".png";
 			String previewZipPath = "previews/" + previewName;

 			Utility.log( "get bitmaps for rom: " + rom.GetName() );

 			ZipFile zip = new ZipFile( data.getAbsolutePath() );
 			{
 				Utility.log( "searching for: " + titleZipPath );
	 			ZipEntry ze = zip.getEntry( titleZipPath );
	 			if( ze != null )
	 			{
	 				Utility.log( "found entry: " + titleZipPath );
	 				InputStream zin = zip.getInputStream( ze );
	 				bitmaps[0] = BitmapFactory.decodeStream( zin );
					zin.close(); 
	 			}
	 			
	 			Utility.log( "searching for: " + previewZipPath );
	 			ze = zip.getEntry( previewZipPath );
	 			if( ze != null )
	 			{
	 				Utility.log( "found entry: " + previewZipPath );
	 				InputStream zin = zip.getInputStream( ze );
	 				bitmaps[1] = BitmapFactory.decodeStream( zin );
					zin.close();
	 			}
 			}
 			zip.close();
 			return true;
 		}
 		catch( Exception e )
 		{ 
 			Utility.loge( "unzip: " + e.toString() );
		}
 		return false;
	}
}
