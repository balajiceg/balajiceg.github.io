package com.gps.www.bike_pool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */
public class FoldingCellListAdapter extends ArrayAdapter<item> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private final HashSet<MapView> mMaps = new HashSet<MapView>();
    private  ArrayList<MapView> maps_array=new ArrayList<MapView>();
    Fragment frag;

    void openWhatsappContact(String number) {
        PackageManager pm=frag.getActivity().getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            Uri uri = Uri.parse("smsto:" + number);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            i.setPackage("com.whatsapp");
            frag.startActivity(Intent.createChooser(i, ""));
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public FoldingCellListAdapter(Fragment frag, List<item> objects) {
        super(frag.getContext(), 0, objects);
        this.frag=frag;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // get item for selected view
        ViewHolder viewHolder;


        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;

        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell, parent, false);
            // binding view parts to view holder
            viewHolder.points = (TextView) cell.findViewById(R.id.title_points);
            viewHolder.time = (TextView) cell.findViewById(R.id.title_time_label);
            viewHolder.date = (TextView) cell.findViewById(R.id.title_date_label);
            viewHolder.start_pt = (TextView) cell.findViewById(R.id.title_start_pt);
            viewHolder.dest_pt = (TextView) cell.findViewById(R.id.title_dest_point);
            viewHolder.name = (TextView) cell.findViewById(R.id.title_requests_count);
            viewHolder.mapView = (MapView) cell.findViewById(R.id.head_image);
            viewHolder.image_fold=(ImageView)cell.findViewById(R.id.image_menu_fold);

            viewHolder.content_points = (TextView) cell.findViewById(R.id.content_avatar_points);
            viewHolder.content_name = (TextView) cell.findViewById(R.id.content_name_view);
            viewHolder.content_start_pt = (TextView) cell.findViewById(R.id.content_from_address_1);
            viewHolder.content_dest_pt = (TextView) cell.findViewById(R.id.content_to_address_1);
            viewHolder.content_start_time = (TextView) cell.findViewById(R.id.content_start_time);
            viewHolder.content_end_time = (TextView) cell.findViewById(R.id.content_end_time);
            viewHolder.content_mobile = (TextView) cell.findViewById(R.id.content_avatar_phone);
            viewHolder.content_endtime_hide_pane = (RelativeLayout) cell.findViewById(R.id.content_end_time_pane);
            viewHolder.content_endtime_hide_pane.setVisibility(View.INVISIBLE);

            cell.setTag(viewHolder);

            viewHolder.initializeMapView();
            maps_array.add(viewHolder.mapView);




        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }


        final item item = getItem(position);
        viewHolder.mapView.setTag(item);
        // Keep track of MapView
        //mMaps.add(viewHolder.mapView);

        // bind data from selected element to view through view holder
        viewHolder.points.setText(item.getPoints()+"");
        viewHolder.time.setText(item.getStart_time_String());
        viewHolder.date.setText("Time");
        viewHolder.start_pt.setText(item.getStart_String());
        viewHolder.dest_pt.setText(item.getDest_String());
        viewHolder.name.setText(item.getName());

        viewHolder.content_mobile.setText(item.getMobile());
        viewHolder.content_points.setText(item.getPoints()+"");
        viewHolder.content_name.setText(item.getName());
        viewHolder.content_start_pt.setText(item.getStart_String());
        viewHolder.content_dest_pt.setText(item.getDest_String());
        viewHolder.content_start_time.setText(item.getStart_time_String());

        if(item.end_time!=null)
        {
            viewHolder.content_endtime_hide_pane.setVisibility(View.VISIBLE);
            viewHolder.content_end_time.setText(item.getEnd_time_string());
        }

        viewHolder.content_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsappContact(item.getMobile());
            }
        });


        final FoldingCellListAdapter fa=this;
        final FoldingCell finalCell = cell;
        viewHolder.image_fold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FoldingCell) finalCell).toggle(false);
                // register in adapter that state for selected cell is toggled
                fa.registerToggle(position);
            }
        });




        return cell;
    }

    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
        {
            registerFold(position);
            Log.v(this.getClass().getName(),"folding...");
        }
        else {
            registerUnfold(position);
            Log.v(this.getClass().getName(),"unfolding...");
        }
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
        zoom_map_path(position);
    }

    public void zoom_map_path(int position)
    {

        item i=getItem(position);
        GoogleMap map=maps_array.get(position).getMap();
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        if(i.end_time!=null){//then its from status offer ride
            b.include(i.getStart()).include(i.getDest());
        }
        else{
            for (LatLng m : i.path) b.include(m);
        }
        LatLngBounds bounds = b.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,0);
        map.moveCamera(cu);

    }

    // View lookup cache
    private class ViewHolder implements OnMapReadyCallback,GoogleMap.OnMapClickListener {
       /* TextView price;
        TextView pledgePrice;
        TextView fromAddress;
        TextView toAddress;
        TextView requestsCount;
        TextView date;
        TextView time;
        TextView temp;*/

        TextView points;
       TextView name;
        TextView start_pt;
        TextView dest_pt;
        TextView date;
        TextView time;
        MapView mapView;
        GoogleMap map;
        ImageView image_fold;

        TextView content_mobile;
        TextView content_points;
        TextView content_name;
        TextView content_start_pt;
        TextView content_dest_pt;
        TextView content_start_time;
        TextView content_end_time;
        RelativeLayout content_endtime_hide_pane;



        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(getContext());

            map = googleMap;
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.getUiSettings().setMapToolbarEnabled(false);

          final item user = (item) mapView.getTag();





            if (user != null) {
               //setting the line or points

                if(user.end_time!=null)//then its from status offer ride
                {
                    Log.v("Folding CellAdapter","into status offer");
                    Marker start=map.addMarker(new MarkerOptions().position(user.start));
                    Marker dest=map.addMarker(new MarkerOptions().position(user.dest));

                }
                else
                {

                    Log.v("Folding CellAdapter","into take offer");

                    Polyline line = map.addPolyline(new PolylineOptions()
                            .addAll(new ArrayList<LatLng>(Arrays.asList(user.path)))
                            .width(5f)
                            .color(Color.GREEN)
                            .geodesic(true));

                }
            }

            map.setOnMapClickListener(this);
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }
        }

        @Override
        public void onMapClick(LatLng latLng) {

            item i = (item) mapView.getTag();
            Show_User_Details f=new Show_User_Details();
            f.setUser(i);
            FragmentManager fragmentManager =frag.getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame,f);//.addToBackStack("status_list");
            fragmentTransaction.commit();
        }
    }

    /**
     * RecycleListener that completely clears the {@link com.google.android.gms.maps.GoogleMap}
     * attached to a row in the ListView.
     * Sets the map type to {@link com.google.android.gms.maps.GoogleMap#MAP_TYPE_NONE} and clears
     * the map.
     */

    private static void setMapLocation(GoogleMap map, LatLng data) {
        // Add a marker for this item and set the camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(data, 13f));
        map.addMarker(new MarkerOptions().position(data));

        // Set the map type back to normal.
        //map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }





}



