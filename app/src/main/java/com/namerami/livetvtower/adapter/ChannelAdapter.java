package com.namerami.livetvtower.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        holder.channelName.setText(channel.getFlag() + " " + channel.getName());

        String country = channel.getCountry() == null ? "" : channel.getCountry();
        String group = channel.getGroup() == null ? "TV" : channel.getGroup();
        String streamCount = channel.getUrls() == null ? "0" : String.valueOf(channel.getUrls().size());

        holder.channelGroup.setText(country + " · " + group + " · " + streamCount + " stream(s)");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChannelClick(channel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    static class ChannelViewHolder extends RecyclerView.ViewHolder {
        TextView channelName;
        TextView channelGroup;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            channelName = itemView.findViewById(R.id.channelName);
            channelGroup = itemView.findViewById(R.id.channelGroup);
        }
    }
}
