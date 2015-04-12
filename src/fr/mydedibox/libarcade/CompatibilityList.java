package fr.mydedibox.libarcade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.mydedibox.libarcade.objects.RomInfo;

public class CompatibilityList 
{
	private ArrayList<RomInfo> list = new ArrayList<RomInfo>();
	
	private List<Integer> filter_years = new ArrayList<Integer>();
	private List<String> filter_systems = new ArrayList<String>();
	
	public CompatibilityList( ArrayList<RomInfo> pList )
	{
		this.list = pList;
		for( RomInfo rom : list )
		{
			if( !filter_years.contains( rom.GetYear() ) )
				filter_years.add( rom.GetYear() );
			
			if( !filter_systems.contains( rom.GetSystem() ) )
				filter_systems.add( rom.GetSystem() );
		}
		Collections.sort( filter_years );
		Collections.sort( filter_systems );
	}
	
	public void Add( RomInfo pRomInfo )
	{
		this.list.add( pRomInfo );
	}
	
	public ArrayList<RomInfo> getList()
	{
		return this.list;
	}
	
	public RomInfo GetRom( String pName )
	{
		if( pName == null || pName.length() <= 0 )
			return null;
		
		for( int i=0; i<this.list.size(); i++ )
			if( list.get(i).GetName().contentEquals( pName ) )
				return list.get(i); 
		
		return null;
	}
	
	public List<Integer> getFilterYears()
	{
		return this.filter_years;
	}
	public CharSequence[] getFilterYearsCharSeq()
	{
		List<String> list = new ArrayList<String>();
		for( int i : filter_years )
			list.add( new String( ""+i ) );
		
		return list.toArray(new CharSequence[list.size()]);
	}
	
	public List<String> getFilterSystems()
	{
		return this.filter_systems;
	}
	public CharSequence[] getFilterSystemsCharSeq()
	{
		return filter_systems.toArray(new CharSequence[filter_systems.size()]);
	}
}
