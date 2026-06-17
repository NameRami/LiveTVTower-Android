package com.namerami.livetvtower.adapter;

import android.content.Context;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.namerami.livetvtower.R;
import com.namerami.livetvtower.model.Channel;

import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {

    public interface OnChannelClickListener {
        void onChannelClick(Channel channel);
    }

    private final List<Channel> channels;
    private final OnChannelClickListener listener;

    public ChannelAdapter(List<Channel> channels, OnChannelClickListener listener) {
        this.channels = channels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_channel, parent, false);

        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        Channel channel = channels.get(position);

        // Set channel name
        holder.channelName.setText(channel.getName());

        // Set country with flag
        if (channel.getFlag() != null && !channel.getFlag().isEmpty()) {
            holder.countryFlag.setText(channel.getFlag());
        } else {
            holder.countryFlag.setText("🌍");
        }

        holder.countryName.setText(channel.getCountry() != null ? channel.getCountry() : "Unknown");

        // Set channel group
        if (channel.getGroup() != null && !channel.getGroup().isEmpty()) {
            holder.channelGroup.setText(channel.getGroup());
        } else {
            holder.channelGroup.setText("No Category");
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChannelClick(channel);
            }
        });

        // Add ripple effect on click (FIXED)
        holder.itemView.setForeground(createSelectableItemBackground(holder.itemView.getContext()));
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    /**
     * Creates a ripple drawable for touch feedback
     * FIXED: Returns a proper RippleDrawable instead of ColorStateList
     */
    private RippleDrawable createSelectableItemBackground(Context context) {
        // Create the ripple color (indigo with transparency)
        int rippleColor = Color.argb(76, 99, 102, 241); // 30% opacity indigo

        // Create the background color (transparent)
        ColorDrawable background = new ColorDrawable(Color.TRANSPARENT);

        // Create and return RippleDrawable
        return new RippleDrawable(
                android.content.res.ColorStateList.valueOf(rippleColor),
                background,
                null
        );
    }

    static class ChannelViewHolder extends RecyclerView.ViewHolder {
        TextView channelName;
        TextView channelGroup;
        TextView countryName;
        TextView countryFlag;
        ImageView channelLogo;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);

            channelName = itemView.findViewById(R.id.channelName);
            channelGroup = itemView.findViewById(R.id.channelGroup);
            countryName = itemView.findViewById(R.id.countryName);
            countryFlag = itemView.findViewById(R.id.countryFlag);
            channelLogo = itemView.findViewById(R.id.channelLogo);
        }
    }
}
