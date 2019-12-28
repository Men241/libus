package com.dimitriongoua.libus.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.dimitriongoua.libus.R;
import com.dimitriongoua.libus.model.Libelle;

@SuppressWarnings("CanBeFinal")
public class LibelleListAdapter  extends RecyclerView.Adapter<LibelleListAdapter.LibelleViewHolder> {

    private List<Libelle> libelleList;

    public LibelleListAdapter(List<Libelle> libelleList) {
        this.libelleList = libelleList;
    }

    @NonNull
    @Override
    public LibelleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_libelle, parent, false);
        return new LibelleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LibelleViewHolder holder, int position) {
        final Libelle libelle = libelleList.get(position);
        holder.tv_libelle.setText(libelle.getLibelle());
    }

    @Override
    public int getItemCount() {
        return libelleList.size();
    }

    class LibelleViewHolder extends RecyclerView.ViewHolder {

        final TextView tv_libelle;

        LibelleViewHolder(View itemView) {
            super(itemView);
            tv_libelle = itemView.findViewById(R.id.tv_libelle);
        }
    }
}
