package baptista.tiago.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import baptista.tiago.popularmovies.R;
import baptista.tiago.popularmovies.interfaces.MovieSelectorInterface;
import baptista.tiago.popularmovies.models.Movie;
import baptista.tiago.popularmovies.settings.SettingsActivity;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieSelectorInterface {

    private static final String TAG = MainActivity.class.getName();
    //private MainActivityFragment fragmentData;
    private boolean mIsTablet;
    private static final String DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG";
    private static final String IS_TABLET = TAG + "_IS_TABLET";
    public static final String CURRENT_MOVIE = "CURRENT_MOVIE";

    @Bind(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mIsTablet = false;
        mProgressBar.setVisibility(View.VISIBLE);

        if (isNetworkAvailable()) {
            // Check if 2 pane by seeing if the detail fragment is being created
            if (findViewById(R.id.layout_detail_fragment) != null) {

                mIsTablet = true; // save this locally somewhere
                MovieDetailActivityFragment starterFragment = new MovieDetailActivityFragment();
                starterFragment.setTablet(true);
                //initiateMovieGridFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_detail_fragment, starterFragment, DETAIL_FRAGMENT_TAG)
                        .commit();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }
        else {
            popNetworkError();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.general_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            this.startActivity(settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie currentMovie) {

        if (mIsTablet) {
            MovieDetailActivityFragment detailFragment = new MovieDetailActivityFragment();
            detailFragment.setTablet(mIsTablet);
            // Again with dirty data, must change this to Parcelable
            detailFragment.setCurrentMovieDetails(currentMovie.getMovieArray());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_detail_fragment, detailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();

        } else {
            Log.d(TAG, "Starting activity as a phone...");
            startDetailActivity(currentMovie);
        }
    }

    private void startDetailActivity(Movie currentMovie) {
        // Try and use interface instead of starting activity, but only if tablet
        Log.d(TAG, "startDetailActivity(): " + currentMovie);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        //intent.putExtra(CURRENT_MOVIE, currentMovie);
        intent.putExtra(CURRENT_MOVIE, currentMovie.getMovieArray());
        this.startActivity(intent);
    }

   /* private void initiateMovieGridFragment() {
        Log.d(TAG, "onCreate(): Initializing fragment manager...");
        FragmentManager fm = getFragmentManager();
        fragmentData = (MainActivityFragment) fm.findFragmentByTag("MainActivityFragment");

        if (fragmentData == null) {
            Log.d(TAG, "onCreate(): Creating fragment for the first time");
            fragmentData = new MainActivityFragment();

            //Bundle args = new Bundle();
            //args.putSerializable(Fragment.CURRENT_MOVIE, result);

            fm.beginTransaction()
                    .add(fragmentData, "MainActivityFragment")
                    .commit();
        } else {
            Log.d(TAG, "onCreate(): Found existing fragment " + fragmentData);
        }
    }*/

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void popNetworkError() {
        //AlertDialogFragment dialog = new AlertDialogFragment();
        //dialog.show(getFragmentManager(), "error_dialog");
        Toast.makeText(this, getString(R.string.network_is_broken), Toast.LENGTH_SHORT).show();
    }
}


