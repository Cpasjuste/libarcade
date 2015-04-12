package fr.mydedibox.libarcade.activity;

import fr.mydedibox.libarcade.R;
import fr.mydedibox.libarcade.fragments.romDetailFragment;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class romDetailActivity extends ActionBarActivity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rom_detail);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        
        if (savedInstanceState == null) 
        {
            Bundle arguments = new Bundle();
            arguments.putString(romDetailFragment.ARG_FILE_PATH,
            		getIntent().getStringExtra(romDetailFragment.ARG_FILE_PATH));
            romDetailFragment fragment = new romDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rom_detail_container, fragment)
                    .commit();
        }
    }

    /*TODO:
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        if ( item.getItemId() == android.R.id.home ) 
        {
            //NavUtils.navigateUpTo(this, new Intent(this, romListActivity.class));
        	this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
