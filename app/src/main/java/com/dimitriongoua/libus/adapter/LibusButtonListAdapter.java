package com.dimitriongoua.libus.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.dimitriongoua.libus.R;
import com.dimitriongoua.libus.model.LibusButton;

@SuppressWarnings("CanBeFinal")
public class LibusButtonListAdapter extends RecyclerView.Adapter<LibusButtonListAdapter.LibusButtonViewHolder> {

    private List<LibusButton> libusButtonList;

    public LibusButtonListAdapter(List<LibusButton> libusButtonList) {
        this.libusButtonList = libusButtonList;
    }

    @NonNull
    @Override
    public LibusButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_libus_button, parent, false);
        return new LibusButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LibusButtonViewHolder holder, int position) {
        final LibusButton libusButton = libusButtonList.get(position);
        holder.tv_libelle.setText(libusButton.getLibelle());
    }

    @Override
    public int getItemCount() {
        return libusButtonList.size();
    }

    class LibusButtonViewHolder extends RecyclerView.ViewHolder {

        final TextView tv_libelle;

        LibusButtonViewHolder(View itemView) {
            super(itemView);
            tv_libelle = itemView.findViewById(R.id.tv_libelle);
        }
    }
}
