package edu.iit.arajago6hawk.searchlocal;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import org.w3c.dom.Text;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Home Page. Shows image slider and a panel with featured businesses.
     * TODO Should allow search using input text field. Nearby businesses button should 
     * query user's current location, get the city using Google's Reverse Geocoding API
     * filter records based on city, then find distance to each business in city 
     * and then give him businesses that are within a threshold, paginated. Phew. 
     * Sounds like fun tho.
     * BUG User logout does not remove profile picture. On initial triage, it looks like
     * Android is not updating drawers frequently to ensure UI smoothness. Oh snap. Have to resolve.
     */

    private ImageView userProfilePic;
    private NavigationView navigationView;
    private View headerView;
    private TextView userName, userEmail;
    private Bitmap profilePic;
    private Boolean exit = false;
    private Menu menu;
    private AutoScrollViewPager mViewPager;
    private CardView cButton, cFeatured;
    private RecyclerView recList;
    private Boolean isSearchActive = false;
    public static TextView salutation, entice;
    public static final double ERad = 6372.8; // Radius of earth in kilometers
    LocationManager mLocationManager;
    Location location = null;
    ArrayList<BusinessData> businessDataList = new ArrayList<BusinessData>();
    DbMain bDB;
    Resources res;


    @Override
    public void onResume(){
        super.onResume();
        if (LoginScreen.newAccessToken == null) {
            userName.setText("SearchLocal");
            userEmail.setText("ownlocal.com");
            userProfilePic.setImageBitmap(null);
            profilePic = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.circle_icon);
            userProfilePic.setImageBitmap(profilePic);
            headerView.refreshDrawableState();
            salutation.setText("Hello there...");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bDB = new DbMain(this);
        if(bDB.getBusinessCount()==0) {
	    // Add test businesses to DB on app installation
            bDB.insertBusiness("Bob's Bikes", "Automotive / Recreational", "The best bikes in the city!",
                    "W 47th street", "Austin", "3129374808", "www.ownlocal.com", "www.facebook.com/OwnLocal",
                    30.291859, -97.735252, "shop3", "HCNews");
            bDB.insertBusiness("Henna Chevy", "Auto Dealers", "We specialize in offering low down payments and affordable payment plans for all automobiles",
                    "8805 Interstate 35", "Austin", "3129374808", "www.ownlocal.com", "www.facebook.com/OwnLocal",
                    30.3510616, -97.7611326, "shop4", "HCNews");
            bDB.insertBusiness("Uchiko", "Food / Restaurant", " Sushi Bars, Gluten-Free, Japanese!",
                    "4200 N Lamar Blvd", "Austin", "3129374808", "www.ownlocal.com", "www.facebook.com/OwnLocal",
                    30.3108075, -97.810003, "shop5", "HCNews");
        }

	// Bind UI elements to variables
        salutation = (TextView) findViewById(R.id.salutation);
        entice = (TextView) findViewById(R.id.entice);
        EditText editText = (EditText) findViewById(R.id.editText2);
        int color = Color.parseColor("#27AD80");
        editText.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        res = getResources();

        //WebView browser = (WebView) findViewById(R.id.webview);
        //browser.setWebViewClient(new WebViewClient());
        //browser.loadUrl("https://www.facebook.com/Starbucks-107853072606849/reviews");

        cButton = (CardView) findViewById(R.id.card_button);
        cFeatured = (CardView) findViewById(R.id.card_featured);
        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

	// Navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        updateNavHeader(LoginScreen.userName,LoginScreen.userEmail);

	// Image slider
        mViewPager = (AutoScrollViewPager) findViewById(R.id.imgSlider);
        ImageSliderAdapter adapterView = new ImageSliderAdapter(MainActivity.this);
        mViewPager.setAdapter(adapterView);
        mViewPager.startAutoScroll(4000);
        mViewPager.setCycle(false);
        mViewPager.setInterval(4000);
        mViewPager.setOnPageChangeListener(adapterView);

	// Get user location
        location = getLastKnownLocation();

	// Button listener
        final Button gobtn = (Button) findViewById(R.id.go_button);
        gobtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (location != null) {
                    mViewPager.setVisibility(View.GONE);
                    cButton.setVisibility(View.GONE);
                    cFeatured.setVisibility(View.GONE);
                    isSearchActive=true;
                    businessDataList = bDB.getAllBusinesses();
                    BusinessAdapter ba = new BusinessAdapter(businessDataList);
                    recList.setAdapter(ba);
                    recList.setVisibility(View.VISIBLE);
                } else {
                    String cbMsg = "There was some issue in getting your current location!";
                    String htmlString = " <font color=\"#27AD80\"><b><i>LOCATION ERROR</font></i></b><br/>" + cbMsg;
                    Toast.makeText(getApplicationContext(), Html.fromHtml(htmlString), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Get facebook profile pic
    private class getProfilePic extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL imageURL = new URL(urls[0]);
                profilePic = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                return null;

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //Update UI
            userProfilePic.setImageBitmap(profilePic);
        }
    }

    // Update navigation drawer header on login and log out
    public void updateNavHeader(String name, String email){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        MenuItem nav_access = menu.findItem(R.id.nav_access);
        if (LoginScreen.newAccessToken == null){
            nav_access.setTitle("Log in");
            nav_access.setIcon(R.drawable.ic_menu_login);

        }

        headerView = navigationView.getHeaderView(0);
        userName = (TextView) headerView.findViewById(R.id.nav_name);
        userEmail = (TextView) headerView.findViewById(R.id.nav_email);
        userProfilePic = (ImageView) headerView.findViewById(R.id.nav_image);

        if (name != ""){
            userName.setText(name);
            userEmail.setText(email);
            new getProfilePic().execute("https://graph.facebook.com/"+ LoginScreen.userId +"/picture?type=normal");
            try{
                salutation.setText("Hello "+name.split("\\s+")[0]+"...");
            } catch(Exception e){
                salutation.setText("Hello "+name+"...");
            }
        }
    }

    public boolean finishLogout(){
        if (LoginScreen.newAccessToken != null) {
            LoginManager.getInstance().logOut();
            LoginScreen.newAccessToken = null;
            Intent intent = new Intent(getBaseContext(), LoginScreen.class);
            startActivity(intent);
            finish();
            return true;
        }
        else{
            Intent intent = new Intent(getBaseContext(), LoginScreen.class);
            startActivity(intent);
            return true;
        }
    }

    public void takeToBusinessPage(View view){
        Intent intent = new Intent(getBaseContext(), BusinessProfile.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (isSearchActive==true){
            recList.setVisibility(View.GONE);
            cFeatured.setVisibility(View.VISIBLE);
            cButton.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            isSearchActive=false;
        }
        else {
            try {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    if (exit) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        String cbMsg = "Press Back again to Exit.";
                        String htmlString = " <font color=\"#27AD80\"><b><i>LEAVING?!</font></i></b><br/>" + cbMsg;
                        Toast.makeText(getApplicationContext(), Html.fromHtml(htmlString), Toast.LENGTH_SHORT).show();
                        exit = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                exit = false;
                            }
                        }, 3 * 1000);

                    }
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        updateMenuTitles(menu);
        this.menu=menu;
        return true;
    }

    public static void updateMenuTitles(Menu menu) {
        MenuItem accessItem = menu.findItem(R.id.action_access);
        if (LoginScreen.newAccessToken != null) {
            accessItem.setTitle("Log out");
        } else {
            accessItem.setTitle("Log in");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_access) {
            finishLogout();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
        } else if (id == R.id.nav_business) {
            Intent intent = new Intent(getBaseContext(), BusinessActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_feed) {
            Intent intent = new Intent(getBaseContext(), AdfeedActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            String msg = "Hey, have you tried the new SearchLocal app?!\nTry today at play.google.com!";
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        else if (id == R.id.nav_access){
            finishLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Calculates distance between two lat longs.
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double havRes=0.0;
        try {
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);

            double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
            double c = 2 * Math.asin(Math.sqrt(a));
            havRes = ERad * c;
        }
        catch(Exception e){

        }
        return havRes;
    }

    // Routine to get user location.
    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            try {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            catch(SecurityException e){

            }
        }
        return bestLocation;
    }

    // Set business data pulled from local DB to the list of cards.
    public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder> {

        private ArrayList<BusinessData> businessDataList;
        private Double distance;
        private String resName;
        private int resId;
        private Drawable drawable;

        public BusinessAdapter(ArrayList<BusinessData> businessDataList) {
            this.businessDataList = businessDataList;
        }

        public double round(double value, int places) {
            if (places < 0) throw new IllegalArgumentException();

            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }

        @Override
        public int getItemCount() {
            return businessDataList.size();
        }

        @Override
        public void onBindViewHolder(BusinessViewHolder businessViewHolder, int i) {
            BusinessData bD = businessDataList.get(i);
            businessViewHolder.vName.setText(bD.getName());
            businessViewHolder.vAddress.setText(bD.getAddress());
            businessViewHolder.vCategory.setText(bD.getCategory());
            distance = round(haversine(bD.getLat(),bD.getLng(),location.getLatitude(),location.getLongitude()),2);
            businessViewHolder.vDistance.setText(Double.toString(distance)+" kms");
            resName = bD.getImgName();
            resId = res.getIdentifier(resName,"drawable",getPackageName());
            drawable = getDrawable(resId);
            businessViewHolder.vImage.setImageDrawable(drawable);
        }

        @Override
        public BusinessViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_item, viewGroup, false);

            return new BusinessViewHolder(itemView);
        }

        public class BusinessViewHolder extends RecyclerView.ViewHolder {
            protected TextView vName;
            protected TextView vAddress;
            protected TextView vCategory;
            protected TextView vDistance;
            protected ImageView vImage;

            public BusinessViewHolder(View v) {
                super(v);
                vName =  (TextView) v.findViewById(R.id.title);
                vAddress = (TextView)  v.findViewById(R.id.address);
                vCategory = (TextView) v.findViewById(R.id.category);
                vDistance = (TextView)  v.findViewById(R.id.distance);
                vImage = (ImageView)  v.findViewById(R.id.imageView);

            }
        }
    }
}
