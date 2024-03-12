package com.qooke.mapapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.qooke.mapapp.MapActivity;
import com.qooke.mapapp.R;
import com.qooke.mapapp.model.Place;
import com.qooke.mapapp.model.PlaceList;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder>{

    Context context;
    ArrayList<Place> placeArrayList;


    // 생성자 만들기
    public PlaceAdapter(Context context, ArrayList<Place> placeArrayList) {
        this.context = context;
        this.placeArrayList = placeArrayList;
    }


    @NonNull
    @Override
    public PlaceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.place_row, parent, false);
        return new PlaceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = placeArrayList.get(position);

        if (place.name == null) {
            holder.txtName.setText("상점명 없음");
        } else {
            holder.txtName.setText(place.name);
        }

        if (place.vicinity == null) {
            holder.txtVicinity.setText("주소 없음");
        } else {
            holder.txtVicinity.setText(place.vicinity);
        }
    }

    @Override
    public int getItemCount() {
        return placeArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtVicinity;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtVicinity = itemView.findViewById(R.id.txtVicinity);
            cardView = itemView.findViewById(R.id.cardView);


            // 카드뷰를 누르면 실행되는 함수
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    Place place = placeArrayList.get(index);

                    // place 정보를 맵 액티비티에 넘겨주면 된다.
                    Intent intent = new Intent(context, MapActivity.class);
                    intent.putExtra("place", place);
                    context.startActivity(intent);
                }
            });
        }
    }
}
