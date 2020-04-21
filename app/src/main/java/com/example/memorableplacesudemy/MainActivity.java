package com.example.memorableplacesudemy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    static ArrayList<String> listOfPlaces;
    static ArrayList<LatLng> locations;
    static ArrayAdapter arrayAdapter;

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.memorableplacesudemy",Context.MODE_PRIVATE);
        ArrayList<String> latitude=new ArrayList<>();
        ArrayList<String> longitude=new ArrayList<>();
        listView=(ListView)findViewById(R.id.listview);
      listOfPlaces=new ArrayList<String>();
        locations=new ArrayList<LatLng>();
       listOfPlaces.clear();
        latitude.clear();
        longitude.clear();
        locations.clear();
        try{
            listOfPlaces=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize((new ArrayList<String>()))));
            latitude=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize((new ArrayList<String>()))));
            longitude=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("longs",ObjectSerializer.serialize((new ArrayList<String>()))));
        }catch (Exception r) {
            r.printStackTrace();
        }
            if(listOfPlaces.size()>0 && latitude.size()>0 && longitude.size()>0){
                if(listOfPlaces.size()==latitude.size()&& listOfPlaces.size()==longitude.size()){
                    for(int i=0;i<latitude.size();i++){
                        locations.add(new LatLng(Double.parseDouble(latitude.get(i)),Double.parseDouble(longitude.get(i))));
                    }
                }
            }else{
                listOfPlaces.add("Add a new place");
                locations.add(new LatLng(0,0));
            }
       arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listOfPlaces);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("placenumber",position);
                startActivity(intent);
            }
        });
    }
}
