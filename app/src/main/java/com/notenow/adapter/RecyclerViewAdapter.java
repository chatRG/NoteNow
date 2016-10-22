package com.notenow.adapter;

/**
 * Created by Sayan Chatterjee on 10/21/16.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.notenow.R;
import com.notenow.model.Note;

import java.util.Collections;
import java.util.List;

import github.nisrulz.recyclerviewhelper.RVHAdapter;
import github.nisrulz.recyclerviewhelper.RVHViewHolder;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>
        implements RVHAdapter {

    //private Context context;
    private List<Note> notes;


    public RecyclerViewAdapter(Context context, List<Note> notes) {
        //this.context = context;
        this.notes = notes;
    }

    public void removeAllItem() {
        notes.clear();
        notifyDataSetChanged();
    }

    /*public void removeItem(int position) {
        notes.remove(position);
        notifyDataSetChanged();
    }*/

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_row, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.tvId.setText((notes.get(position).getId() + ""));
        holder.tvTitle.setText(notes.get(position).getTitle());
        holder.tvContent.setText(notes.get(position).getContent());
        holder.tvTime.setText(notes.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public Note getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(notes, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return false;
    }

    @Override
    public void onItemDismiss(int position, int direction) {
        notes.remove(position);
        notifyDataSetChanged();
        notifyItemRemoved(position);
    }

    public static class ViewHolder {
        public TextView tvId;
        public TextView tvTitle;
        public TextView tvContent;
        public TextView tvTime;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements RVHViewHolder {

        public TextView tvId;
        TextView tvTitle;
        TextView tvContent;
        TextView tvTime;
        Context ctx;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.ctx = itemView.getContext();
            tvId = (TextView) itemView.findViewById(R.id.note_id);
            tvTitle = (TextView) itemView.findViewById(R.id.note_title);
            tvContent = (TextView) itemView.findViewById(R.id.note_content);
            tvTime = (TextView) itemView.findViewById(R.id.note_time);
        }

        @Override
        public void onItemSelected(int actionstate) {
            /*String noteId = String.valueOf(notes.get(actionstate).getId());
            Intent intent = new Intent(ctx, EditNoteActivity.class);
            intent.putExtra("id", Integer.parseInt(noteId));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ctx.startActivity(intent);*/
        }

        @Override
        public void onItemClear() {
            //System.out.println("Item is unselected");
        }

    }
}
