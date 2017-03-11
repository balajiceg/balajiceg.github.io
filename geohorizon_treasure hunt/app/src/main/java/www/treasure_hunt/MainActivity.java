package www.treasure_hunt;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import 	android.support.design.widget.Snackbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

class clue{
Location loc;
    String ques,ans;


    public Location getLoc() {
        return loc;
    }

    public String getQues() {
        return ques;
    }

    public String getAns() {
        return ans;
    }

    public clue(Location loc, String ques, String ans) {
        this.loc = loc;
        this.ques = ques;
        this.ans = ans;
    }
}

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {




    private MapView map;

    private static final String TAG = "Treasure_Hunt---";
    private static final long INTERVAL = 500;
    private static final long FASTEST_INTERVAL = 500;
    private static final long SMALLEST_DISPLACEMENT =1;
    private static final float req_accuracy=15;

    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    boolean gps_enabled;
    boolean network_enabled;
    GoogleApiClient mGoogleApiClient;
    Context c;
    TextView tv;

    IMapController mapController;
    Location last_known;

    ArrayList<OverlayItem> overlayItemArray;
    ArrayList<clue> loc_list;
    int level;
    int last_lvl;
    Snackbar snack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c = this.getApplicationContext();//initialize the context to pass to members

        //intialize clues
        String[] clues=getResources().getStringArray(R.array.clues);
        String[] ans=getResources().getStringArray(R.array.answers);

        loc_list=new ArrayList<clue>();
        level=0;
        last_lvl=clues.length;

        for(int i=0;i<clues.length;i++)
        {
            String[] str=ans[i].split(",");
            Location loc=new Location("manual");
            loc.setLatitude(Double.parseDouble(str[0]));
            loc.setLongitude(Double.parseDouble(str[1]));
            loc_list.add(new clue(loc,clues[i],str[2]));
        }

        //Collections.shuffle(loc_list);
       /* for(clue c:loc_list)
        {
            Log.e(c.getQues(),c.getAns()+" "+c.getLat()+" "+c.getLng());
        }*/

        View parentLayout = findViewById(R.id.root);
        tv=(TextView)findViewById(R.id.tv);

        snack=Snackbar.make(parentLayout,"Please Wait..",Snackbar.LENGTH_INDEFINITE);
        snack.show();



        map = (MapView) findViewById(R.id.map);

        ////////////////////////////////////////////////////////////////////////////////////////////

        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(getApplicationContext(), "Please install google play services", Toast.LENGTH_SHORT).show();
            finish();
        }






        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        if (!check_gps_enabled())
            Toast.makeText(getApplicationContext(), "false", Toast.LENGTH_SHORT).show();
        else if (!mGoogleApiClient.isConnected()) {
            Log.d(TAG, "Location service started...");
            mGoogleApiClient.connect();
        }

        //map.setScrollableAreaLimit(new BoundingBoxE6(13.027304, 80.26062, 12.955045, 80.197449));
        map.setScrollableAreaLimit(new BoundingBoxE6(13.018816, 80.241308, 13.00646, 80.230064));//annauniv
        map.setMinZoomLevel(17);//16 for anna univ ,14for use
       map.setMaxZoomLevel(22);
        map.setUseDataConnection(false);

        addtiles(map);

        mapController = map.getController();
        mapController.setZoom(15);
        GeoPoint startPoint = new GeoPoint(13.011188, 80.235407);//anna univ
        //GeoPoint startPoint = new GeoPoint(12.964939, 80.213872);
        mapController.setCenter(startPoint);
       // map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
       // map.setTilesScaledToDpi(true);
        //--- Create Overlay
        overlayItemArray = new ArrayList<OverlayItem>();

        DefaultResourceProxyImpl defaultResourceProxyImpl
                = new DefaultResourceProxyImpl(this);
        MyItemizedIconOverlay myItemizedIconOverlay
                = new MyItemizedIconOverlay(
                overlayItemArray, null, defaultResourceProxyImpl);
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(map);
        map.getOverlays().add(myScaleBarOverlay);

        //---


        map.getOverlays().add(myItemizedIconOverlay);



        ////////////////////////////////////////////////////////////////////////////////////////////

//        if (!new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/treasurehunt/", "zoomed.sqlite").exists()) {
//            copyAssets();
//            Log.e("-----------------------", "newly created");
//        }
    }



    void addtiles(MapView mMapView) {
        //File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/treasurehunt/", "zoomed.sqlite");
        File f=null;
        try {
            f=File.createTempFile("zoomed",".sqlite");
            // write the inputStream to a FileOutputStream
            InputStream is = getAssets().open("zoomed.sqlite");
            FileOutputStream outputStream =
                    new FileOutputStream(f);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        if (f.exists()) {
            OfflineTileProvider tileProvider = null;
            try {
                tileProvider = new OfflineTileProvider(new SimpleRegisterReceiver(getApplicationContext()), new File[]{f});
            } catch (Exception e) {
                e.printStackTrace();
            }
            //tell osmdroid to use that provider instead of the default rig which is (asserts, cache, files/archives, online
            mMapView.setTileProvider(tileProvider);

            //this bit enables us to find out what tiles sources are available. note, that this action may take some time to run
            //and should be ran asynchronously. we've put it inline for simplicity

            String source = "";
            IArchiveFile[] archives = tileProvider.getArchives();
            if (archives.length > 0) {
                //cheating a bit here, get the first archive file and ask for the tile sources names it contains
                Set<String> tileSources = archives[0].getTileSources();
                //presumably, this would be a great place to tell your users which tiles sources are available
                if (!tileSources.isEmpty()) {
                    //ok good, we found at least one tile source, create a basic file based tile source using that name
                    //and set it. If we don't set it, osmdroid will attempt to use the default source, which is "MAPNIK",
                    //which probably won't match your offline tile source, unless it's MAPNIK
                    source = tileSources.iterator().next();
                    //Log.e("--------------",source);
                   // mMapView.setTileSource(FileBasedTileSource.getSource(source));
                    mMapView.setTileSource(new XYTileSource(
                            source,
                            14,
                            22,
                            256,
                            ".png",
                            new String[]{}
                    ));
                } else {
                    mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                }

            } else
                mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

            Toast.makeText(getApplicationContext(), "Using " + f.getAbsolutePath() + " " + source, Toast.LENGTH_LONG).show();
            mMapView.invalidate();
            return;
        } else {
            Toast.makeText(getApplicationContext(), f.getAbsolutePath() + " dir not found!", Toast.LENGTH_LONG).show();
        }

    }


    ////////////////////////////////////check gps enabled
    protected boolean check_gps_enabled() {


        chk_location_inner();


        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(myIntent);

                }
            });
            dialog.setNegativeButton(this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    chk_location_inner();
                    if (!gps_enabled && !network_enabled)
                        check_gps_enabled();

                }
            });
            dialog.show();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
            return false;
        } else if (gps_enabled)
            return true;

        return false;

    }

    void chk_location_inner() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;
        network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    ////////////////////////////////////.check gps enabled

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
/////////////////////////.check google play services

    /////////////////////////gps location updaters
    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");

        snack.setText(loc_list.get(level).getQues());
        if (null != mCurrentLocation) {
            last_known=mCurrentLocation;
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
            String acc=String.valueOf(mCurrentLocation.getAccuracy());


            Toast.makeText(c, "Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n", Toast.LENGTH_SHORT).show();


            GeoPoint locGeoPoint = new GeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mapController.setCenter(locGeoPoint);

            setOverlayLoc(mCurrentLocation);
            map.invalidate();

            if(mCurrentLocation.getAccuracy()<=req_accuracy&&level<last_lvl)
           validate_location(mCurrentLocation);



        } else {
            Log.e(TAG + "----", "location is null .........");

        }
    }

    private void validate_location(Location mCurrentLocation) {
        tv.setText(mCurrentLocation.distanceTo(loc_list.get(level).getLoc())+"");
        boolean next_step=false;
        if(mCurrentLocation.getAccuracy()<10)
        {
           next_step= mCurrentLocation.distanceTo(loc_list.get(level).getLoc())<10?true:false;
        }
        else if(mCurrentLocation.getAccuracy()<req_accuracy)
        {
           next_step= mCurrentLocation.distanceTo(loc_list.get(level).getLoc())<req_accuracy?true:false;
        }
       if(next_step)
        {
            if(level==last_lvl-1)
            {
                snack.setText("Completed");return;
            }
            level++;
            Toast.makeText(getApplicationContext(),"Stepping to nxt Clue...",Toast.LENGTH_SHORT).show();
            snack.setText(loc_list.get(level).getQues());
        }
    }

    private void setOverlayLoc(Location overlayloc){
        GeoPoint overlocGeoPoint = new GeoPoint(overlayloc);
        //---
        overlayItemArray.clear();

        OverlayItem newMyLocationItem = new OverlayItem(
                "My Location", "My Location", overlocGeoPoint);
        overlayItemArray.add(newMyLocationItem);
        //---
    }

    protected void startLocationUpdates() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.
                requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


        Log.d(TAG, "Location update started ..............: ");
    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }
/////////////////////////.gps location updaters

    @Override
    /////////////////overriding location listeners
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        updateUI();
    }

    ////////////////////.locationlisteners

    //////////////////////////////////main activity methods
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected())
            stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
        if (gps_enabled == true && mGoogleApiClient.isConnected() == false) {
            mGoogleApiClient.connect();

            //startLocationUpdates();
        }

        check_gps_enabled();
    }

    /////////////////////////////////.main activity methods

    /////////////////////////////////////////resquesr permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    check_gps_enabled();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }



            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    /////////////////////////////////////////.resquesr permission

    ////////////////////////////////////////google api clients
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }
    ////////////////////////////////////////.google api clients


    private class MyItemizedIconOverlay extends ItemizedIconOverlay<OverlayItem> {

        protected final Paint mCirclePaint = new Paint();
        private final Point mMapCoordsProjected = new Point();
        private final Point mMapCoordsTranslated = new Point();

        public MyItemizedIconOverlay(
                List<OverlayItem> pList,
                org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener,
                ResourceProxy pResourceProxy) {


            super(pList, pOnItemGestureListener, pResourceProxy);
            mCirclePaint.setARGB(0, 255, 153, 221);
            mCirclePaint.setAntiAlias(true);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void draw(Canvas canvas, MapView mapview, boolean arg2) {
            // TODO Auto-generated method stub
            super.draw(canvas, mapview, arg2);

            if (!overlayItemArray.isEmpty()) {

                //overlayItemArray have only ONE element only, so I hard code to get(0)
                GeoPoint in = new GeoPoint(overlayItemArray.get(0).getPoint().getLatitude(),
                        overlayItemArray.get(0).getPoint().getLongitude());

                Point out = new Point();
                mapview.getProjection().toPixels(in, out);

                Bitmap bm = BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_menu_mylocation);
                canvas.drawBitmap(bm,
                        out.x - bm.getWidth() / 2,    //shift the bitmap center
                        out.y - bm.getHeight() / 2,    //shift the bitmap center
                        null);

                final float radius = last_known.getAccuracy()
                        / (float) TileSystem.GroundResolution(last_known.getLatitude(),
                        map.getZoomLevel());

                if(last_known.getAccuracy()>req_accuracy)mCirclePaint.setARGB(0,255,0,0);
                else mCirclePaint.setARGB(0,0,255,0);


                map.getProjection().toProjectedPixels((int) (last_known.getLatitude() * 1E6),
                        (int) (last_known.getLongitude() * 1E6), mMapCoordsProjected);
                final Projection pj = map.getProjection();
                pj.toPixelsFromProjected(mMapCoordsProjected, mMapCoordsTranslated);
                mCirclePaint.setAlpha(50);

                mCirclePaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mMapCoordsTranslated.x, mMapCoordsTranslated.y, radius, mCirclePaint);

                mCirclePaint.setAlpha(150);
                mCirclePaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(mMapCoordsTranslated.x, mMapCoordsTranslated.y, radius, mCirclePaint);
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event, MapView mapView) {
            // TODO Auto-generated method stub
            //return super.onSingleTapUp(event, mapView);
            return true;
        }
    }


/*    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
            String error = "";
            for (String a : files)
                error += a;
            Log.e("files in assests", error);
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }

        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.open("zoomed.sqlite");


            File outdir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "treasurehunt");
            if (!outdir.exists()) {
                outdir.mkdirs();
                Log.e("----------", "directory created");
            }

            String outf = Environment.getExternalStorageDirectory().getAbsolutePath() + "/treasurehunt/";

            File outFile = new File(outf, "zoomed.sqlite");
            if (!outFile.exists()) {
                outFile.createNewFile();
            }


            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Cant copy map to storage...enable storage",Toast.LENGTH_LONG).show();
        }

    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }*/





}
