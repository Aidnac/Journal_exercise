package inc.can_a.journalapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;

import inc.can_a.journalapp.R;
import inc.can_a.journalapp.Model.Entry;
import inc.can_a.journalapp.activity.DetailActivity;

public class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.MyViewHolder> {

    Context context;
	private ArrayList<Entry> entryArrayList;
    private EntryListAdapterListener listner;

    public class MyViewHolder extends RecyclerView.ViewHolder {
		public TextView title,note;
		public ImageButton edit_button,delete_button;

		public MyViewHolder(final View view) {
			super(view);

			title = view.findViewById(R.id.title);
            note =  view.findViewById(R.id.note);

            edit_button =  view.findViewById(R.id.edit_entry_button);
            edit_button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Entry entry = entryArrayList.get(getAdapterPosition());
                    listner.onEditPressed(entry,getAdapterPosition());

                  }
            });

            delete_button =  view.findViewById(R.id.delete_entry_button);
            delete_button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Entry entry = entryArrayList.get(getAdapterPosition());
                    listner.onDeletePressed(entry,getAdapterPosition());
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Entry entry = entryArrayList.get(getAdapterPosition());
                    listner.onViewPressed(entry);
                }
            });
		}

	}

	public EntryListAdapter(Context context, ArrayList<Entry> entryArrayList, EntryListAdapterListener listner) {
        this.context = context;
		this.entryArrayList = entryArrayList;
		this.listner = listner;

	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);

       	return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, final int position) {
		Entry entry = entryArrayList.get(position);
		holder.title.setText(entry.getTitle());
		holder.note.setText(entry.getNote());
    }


	@Override
	public int getItemCount() {
		return entryArrayList.size();
	}

    public interface EntryListAdapterListener {
        void onDeletePressed(Entry entry,int position);
        void onEditPressed(Entry entry,int position);
        void onViewPressed(Entry entry);
    }


}
