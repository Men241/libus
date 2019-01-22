package co.dimitriongoua.libus.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.dimitriongoua.libus.R;
import co.dimitriongoua.libus.model.Libelle;

public class LibelleListAdapter  extends RecyclerView.Adapter<LibelleListAdapter.LibelleViewHolder> {

    private List<Libelle> libelleList;

    public LibelleListAdapter(List<Libelle> libelleList) {
        this.libelleList = libelleList;
    }

    @Override
    public LibelleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_libelle, parent, false);
        return new LibelleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LibelleViewHolder holder, int position) {
        final Libelle libelle = libelleList.get(position);
        holder.tv_libelle.setText(libelle.getLibelle());
    }

    @Override
    public int getItemCount() {
        return libelleList.size();
    }

    class LibelleViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_libelle;

        public LibelleViewHolder(View itemView) {
            super(itemView);
            tv_libelle = itemView.findViewById(R.id.tv_libelle);
        }
    }
}
