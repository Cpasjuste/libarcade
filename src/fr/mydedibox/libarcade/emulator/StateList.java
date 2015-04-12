package fr.mydedibox.libarcade.emulator;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;


public class StateList 
{
	private ArrayList<StateInfo> states;
	
	public StateList( final String pSavePath, final String pRomName )
	{
		states = new ArrayList<StateInfo>();
		
		final FileFilter mFileFilter = new FileFilter() 
		{
			public boolean accept(File file)
			{
				final String name = file.getName();
				final int length = pRomName.length() + 8;
				
				if( name.length() == length && name.startsWith( pRomName ) && name.endsWith( ".bmp" ) )
					return true;
				
				return false;
			}
		};
					    
		final File[] filelist = new File( pSavePath ).listFiles( mFileFilter );
		if (filelist != null)
		{
			for (int i = 0; i < filelist.length; i++)
			{
				final String path = filelist[i].getAbsolutePath();
				states.add( new StateInfo(  path.substring( 0, path.lastIndexOf('.') ) ) );
			}
		}
	}
	
	public ArrayList<StateInfo> getStates()
	{
		return this.states;
	}
}
