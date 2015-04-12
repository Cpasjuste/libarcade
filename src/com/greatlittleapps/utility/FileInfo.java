package com.greatlittleapps.utility;

import java.io.File;
import java.io.Serializable;

import org.apache.commons.net.ftp.FTPFile;

public class FileInfo implements Serializable
{
	private static final long serialVersionUID = 6320058381229948008L;

	private Serializable data;
    
    String name = "";
    String path = "";
    public String http_path = "";
    String parent = "";
    long length = 0;
    boolean isDirectory = false;
    boolean isFile = false;
    int childs = 0;
    
    public FileInfo( String _name, String _path, String _parent, long _length, boolean _isDirectory )
    {
    	name = _name;
        path = _path;
        parent = _parent;
        length = _length;
        isDirectory = _isDirectory;
        isFile = !_isDirectory;
    }
    
    public FileInfo( String _path, FTPFile file )
    {
    	name = file.getName();
    	path = _path + name;
    	if( !path.equals( "/" ) )
    		parent = path;
    	length = file.getSize();
    	isDirectory = file.isDirectory();
    	isFile = file.isFile();
    }
    
    public FileInfo( File file )
    {
    	name = file.getName();
    	path = file.getAbsolutePath();
    	parent = file.getParent();
    	length = file.length();
    	childs = file.list() != null ? file.list().length : 0;
    	isDirectory = file.isDirectory();
    	isFile = file.isFile();
    }
    
    public FileInfo()
    {
    }
 
    public void setCustomData( Serializable _data )
    {
    	this.data = _data;
    }
    public Object getCustomData()
    {
    	return (Object)this.data;
    }
    public String getName()
    {
    	return name;
    }
    public String getPath()
    {
    	return path;
    }
    public String getParent()
    {
    	return parent;
    }
    public long getSize()
    {
    	return length;
    }
    public String getSizeFormated()
    {
    	return Utility.formatFileSize( length );
    }
    public int getChildCount()
    {
    	return childs;
    }
    public boolean isDirectory()
    {
    	return isDirectory;
    }
    public boolean isFile()
    {
    	return isFile;
    }
    
    /*
    public boolean Delete()
    {
        return delete( new File( this.GetPath() ) );
    }
 
    private static boolean delete( File dir ) 
    {
        if ( dir.isDirectory() ) 
        {
            String[] children = dir.list();
            for ( int i=0; i<children.length; i++ ) 
            {
                boolean success = delete( new File( dir, children[i] ) );
                if ( !success ) 
                    return false;
            }
        }
        return dir.delete();
    }
    */
}

