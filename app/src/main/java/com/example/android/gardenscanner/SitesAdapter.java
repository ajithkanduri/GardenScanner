package com.example.android.gardenscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SitesAdapter  extends RecyclerView.Adapter<SitesAdapter.ViewHolder>  {
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private ArrayList<String> sitesList;
    SitesAdapter(Context context, ArrayList<String> sitesList)
    {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.sitesList = sitesList;
    }
    @NonNull
    @Override
    public SitesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.sites_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SitesAdapter.ViewHolder holder, int position) {
        holder.myTextView.setText(sitesList.get(position));
    }

    @Override
    public int getItemCount() {
        return sitesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.sites_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    String getItem(int id) {

        return sitesList.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(SitesAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
