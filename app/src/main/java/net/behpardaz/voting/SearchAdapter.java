package net.behpardaz.voting;

/**
 * Created by amin on 19/10/16.
 */

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import net.behpardaz.voting.activities.Voter;


public class SearchAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Voter> displayed_items;

    // private ArchiveFilter archiveFilter;

    private LayoutInflater mInflater;

    public SearchAdapter(Context context, ArrayList<Voter> items) {
        mInflater = LayoutInflater.from(context);
        this.displayed_items = items;
    }

    public int getCount() {
        return displayed_items.size();
    }

    public Object getItem(int position) {
        return displayed_items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SearchViewHolder holder;
        Voter thisEntity;
        thisEntity = displayed_items.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_row, parent,
                    false);
            holder = new SearchViewHolder();
            holder.name = (TextView) convertView
                    .findViewById(R.id.originID);
            holder.description = (TextView) convertView
                    .findViewById(R.id.pharmacyTitle);
//            holder.status = (ImageView) convertView
//                    .findViewById(R.id.imageView2);
            convertView.setTag(holder);
        } else {
            holder = (SearchViewHolder) convertView.getTag();
        }

        holder.name.setText(thisEntity.getOriginId());
//        holder.name.setTypeface(ShookaUIManager.defaultFont);
        holder.description.setText("Ext. "
                + thisEntity.getVoterTitle());
        // +"\n"+ALWrapper.getString(thisEntity.getTenant()))*/
//        holder.description.setTypeface(ShookaUIManager.defaultFont);

        //Log.w("Search Adapter","Member Status:"+thisEntity.getMemberStatus()+ "Room Status:"+thisEntity.getRoomStatus());
//        if (thisEntity.getEntityType().equals("Member")) {
//            if (thisEntity.getMemberStatus().contains("Offline")) {
//                holder.status.setImageResource(R.drawable.user_offline);
//            } else if (thisEntity.getMemberStatus().contains("Busy")) {
//                holder.status.setImageResource(R.drawable.user_busy);
//            } else {
//                holder.status.setImageResource(R.drawable.user_available);
//            }
//        } else{
//            boolean isLocked = thisEntity.getIsLocked().contains("true"), isPinned = thisEntity
//                    .hasPIN().contains("true"), isOccupied = thisEntity
//                    .getRoomStatus().contains("Occupied");
//            if (isLocked) {
//                if (isPinned)
//                    holder.status
//                            .setImageResource(R.drawable.locked_pinned_room);
//                else if (isOccupied)
//                    holder.status
//                            .setImageResource(R.drawable.occupied_locked_room);
//                else
//                    holder.status.setImageResource(R.drawable.locked_room);
//            } else if (isPinned) {
//                if (isOccupied)
//                    holder.status
//                            .setImageResource(R.drawable.occupied_pinned_room);
//                else
//                    holder.status.setImageResource(R.drawable.pinned_room);
//            } else if (isOccupied) {
//                holder.status.setImageResource(R.drawable.occupied_room);
//            } else {
//                holder.status.setImageResource(R.drawable.room_default_large);
//            }
//
//        }

        return convertView;
    }

    private class SearchViewHolder {
        TextView name;
        TextView description;
        ImageView status;

    }

    @Override
    public Filter getFilter() {
        // if (archiveFilter == null)
        // archiveFilter = new ArchiveFilter();
        // return archiveFilter;
        return null;
    }

    // private class ArchiveFilter extends Filter {
    //
    // @Override
    // protected FilterResults performFiltering(CharSequence constraint) {
    // FilterResults results = new FilterResults();
    // // We implement here the filter logic
    // if (constraint == null || constraint.length() == 0) {
    // // No filter implemented we return all the list
    // results.values = all_items;
    // results.count = all_items.size();
    // }
    // // We perform filtering operation
    // ArrayList<ArchiveItem> filteredItems = new ArrayList<ArchiveItem>();
    // constraint = constraint.toString().toLowerCase();
    // for (ArchiveItem item : all_items) {
    // if (item.getTitle().contains(constraint)) {
    // filteredItems.add(item);
    // }
    // }
    //
    // results.count = filteredItems.size();
    // results.values = filteredItems;
    // return results;
    // }
    //
    // @Override
    // protected void publishResults(CharSequence constraint,
    // FilterResults results) {
    // // Now we have to inform the adapter about the new list filtered
    // if (results.count == 0) {
    // notifyDataSetInvalidated();
    // Log.d("ArchiveAdapter", "No results");
    // } else {
    // Log.d("ArchiveAdapter", results.count + " results");
    // displayed_items = (ArrayList<ArchiveItem>) results.values;
    // notifyDataSetChanged();
    // }
    // }
    // }

}
