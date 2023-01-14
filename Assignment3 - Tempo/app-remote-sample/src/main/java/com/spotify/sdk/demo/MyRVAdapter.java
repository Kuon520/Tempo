//IMPORTANT CLASS THAT POPULATE THE RV WITH SONGS FROM THE ARRAYLISTARRAYLISTSTRING, TAKEN FROM SQLITE

package com.spotify.sdk.demo;

import static java.lang.Float.parseFloat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.spotify.android.appremote.demo.R;
import com.spotify.sdk.demo.activity.MainActivity;

import java.util.ArrayList;

public class MyRVAdapter extends RecyclerView.Adapter<MyRVAdapter.MyViewHolder> {

    ArrayList<ArrayList<String>> list;
    RecyclerViewClickInterface recyclerViewClickInterface;
    Context context;
    Bitmap tempBitMap;
    boolean isDoubleBeat;

//    public MyRVAdapter(ArrayList<ArrayList<String>> list, Context context, RecyclerViewClickInterface recyclerViewClickInterface){
//        this.list = list;
//        this.context = context;
//        this.recyclerViewClickInterface = recyclerViewClickInterface;
//    }

    public MyRVAdapter(ArrayList<ArrayList<String>> list, Context context, RecyclerViewClickInterface recyclerViewClickInterface, boolean isDoubleBeat){
        this.list = list;
        this.context = context;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
        this.isDoubleBeat = isDoubleBeat;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.track_line_layout,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v,recyclerViewClickInterface);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try{
            ArrayList<String> result = list.get(position);
            holder.trackNameTV.setText(result.get(2));
            holder.trackArtistTV.setText(result.get(3));
            holder.trackTempoTV.setText((int)(parseFloat(result.get(4)))+" BPM");
            holder.trackEnergyTV.setText(SongClassifier.getTrackEnergyClassify(parseFloat(result.get(5))));
//            holder.trackLoudnessTV.setText((result.get(7)));
            Glide.with(context)
                    .load(result.get(9))
                    .placeholder(R.drawable.ic__logo)
                    .into(holder.trackArtIV);
        }catch (Exception e){
            Log.e("Load image", e.toString());
        }
    }



    @Override
    public int getItemCount() { return list.size();    }


    ////////////////////////////////////////
    ////////////////////////////////////////
    ////////////////////////////////////////
    ////////////////////////////////////////
    ////////////////////////////////////////
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView trackNameTV;
        public TextView trackArtistTV;
        public TextView trackEnergyTV;
        public TextView trackTempoTV;
        public TextView trackLoudnessTV;
        public ImageView trackArtIV;

        public LinearLayout myLayout;

        Context context;

        public MyViewHolder(View itemView, RecyclerViewClickInterface recyclerViewClickInterface){
            super(itemView);
            myLayout= (LinearLayout)itemView;

            trackNameTV = (TextView) itemView.findViewById(R.id.trackNameTV);
            trackArtistTV = (TextView) itemView.findViewById(R.id.trackArtistTV);
            trackEnergyTV = (TextView) itemView.findViewById(R.id.trackEnergyTV);
            trackTempoTV = (TextView) itemView.findViewById(R.id.trackTempoTV);
//            trackLoudnessTV = (TextView) itemView.findViewById(R.id.trackLoudnessTV);
            trackArtIV=(ImageView)itemView.findViewById(R.id.trackArtIV);


            itemView.setOnClickListener(this);
            context = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewClickInterface.onRecyclerItemClick(getAdapterPosition());
                }
            });
        }


        @Override
        public void onClick(View view) {

        }
    }

}
