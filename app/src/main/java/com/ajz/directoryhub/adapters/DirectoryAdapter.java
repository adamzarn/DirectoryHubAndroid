package com.ajz.directoryhub.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ajz.directoryhub.R;
import com.ajz.directoryhub.objects.Entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adamzarn on 10/21/17.
 */

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {

    private Boolean searching;

    private ArrayList<Entry> entries = new ArrayList<>();
    private ArrayList<Entry> filteredEntries = new ArrayList<>();

    private OnEntryClickListener clickListener;

    @Override
    public DirectoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.directory_entry_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Entry entry;

        if (searching) {
            entry = filteredEntries.get(position);
        } else {
            entry = entries.get(position);
        }

        holder.headerTextView.setText(entry.getHeader());

        if (!TextUtils.equals(entry.getPhone(), "")) {
            holder.phoneTextView.setVisibility(View.VISIBLE);
            holder.phoneTextView.setText(entry.getPhone());
        } else {
            holder.phoneTextView.setVisibility(View.GONE);
        }

        if (!TextUtils.equals(entry.getEmail(), "")) {
            holder.emailTextView.setVisibility(View.VISIBLE);
            holder.emailTextView.setText(entry.getEmail());
        } else {
            holder.emailTextView.setVisibility(View.GONE);
        }

        if (!TextUtils.equals(entry.getAddress().getLine2(), "")) {
            holder.line2TextView.setVisibility(View.VISIBLE);
            holder.line2TextView.setText(entry.getAddress().getLine2());
        } else {
            holder.line2TextView.setVisibility(View.GONE);
        }

        if (!TextUtils.equals(entry.getAddress().getLine3(), "")) {
            holder.line3TextView.setVisibility(View.VISIBLE);
            holder.line3TextView.setText(entry.getAddress().getLine3());
        } else {
            holder.line3TextView.setVisibility(View.GONE);
        }

        if (!TextUtils.equals(entry.getAddress().getStreet(), "")) {
            holder.streetTextView.setVisibility(View.VISIBLE);
            holder.streetTextView.setText(entry.getAddress().getStreet());
        } else {
            holder.streetTextView.setVisibility(View.GONE);
        }

        if (!TextUtils.equals(entry.getAddress().getCityStateZip(), "")) {
            holder.cityStateZipTextView.setVisibility(View.VISIBLE);
            holder.cityStateZipTextView.setText(entry.getAddress().getCityStateZip());
        } else {
            holder.cityStateZipTextView.setVisibility(View.GONE);
        }

        if (!TextUtils.equals(entry.getChildrenString(), "")) {
            holder.childrenTextView.setVisibility(View.VISIBLE);
            holder.childrenTextView.setText(entry.getChildrenString());
        } else {
            holder.childrenTextView.setVisibility(View.GONE);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.header_text_view)
        TextView headerTextView;

        @BindView(R.id.directory_phone_text_view)
        TextView phoneTextView;

        @BindView(R.id.directory_email_text_view)
        TextView emailTextView;

        @BindView(R.id.directory_line2_text_view)
        TextView line2TextView;

        @BindView(R.id.directory_line3_text_view)
        TextView line3TextView;

        @BindView(R.id.directory_street_text_view)
        TextView streetTextView;

        @BindView(R.id.directory_city_state_zip_text_view)
        TextView cityStateZipTextView;

        @BindView(R.id.directory_children_text_view)
        TextView childrenTextView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (searching) {
                clickListener.onEntryClick(filteredEntries.get(getAdapterPosition()));
            } else {
                clickListener.onEntryClick(entries.get(getAdapterPosition()));
            }
        }
    }

    public interface OnEntryClickListener {
        void onEntryClick(Entry selectedEntry);
    }

    public void setOnEntryClickListener(final OnEntryClickListener entryClickListener) {
        this.clickListener = entryClickListener;
    }

    public void setSearching(Boolean searching) {
        this.searching = searching;
    }

    public void setData(ArrayList<Entry> receivedEntries) {
        entries = receivedEntries;

        Collections.sort(entries, new Comparator<Entry>() {
            @Override public int compare(Entry e1, Entry e2) {
                return e1.getHeader().compareTo(e2.getHeader());
            }
        });

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (searching) {
            return filteredEntries.size();
        } else {
            return entries.size();
        }
    }

    public void filter(String text) {
        filteredEntries.clear();
        text = text.toLowerCase();
        for(Entry entry : entries) {
            if (entry.getHeader().toLowerCase().contains(text)) {
                filteredEntries.add(entry);
            }
        }
        notifyDataSetChanged();
    }

}

