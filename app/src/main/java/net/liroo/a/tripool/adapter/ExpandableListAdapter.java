package net.liroo.a.tripool.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.liroo.a.tripool.R;

import java.util.ArrayList;
import java.util.List;

public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private ArrayList<Item> data;

    public static final int HEADER = 0;
    public static final int CHILD = 1;

    public ExpandableListAdapter(ArrayList<Item> data)
    {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type)
    {
        View view;
        Context context = parent.getContext();
        float dp = context.getResources().getDisplayMetrics().density;
        int subItemPaddingLeftToRight = (int) (30 * dp);

        switch ( type ) {
            case HEADER :
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.recyclerview_list, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;
            case CHILD :
                TextView itemTextView = new TextView(context);
                itemTextView.setPadding(subItemPaddingLeftToRight, 0, subItemPaddingLeftToRight, 0);
                itemTextView.setTextSize(12);
                itemTextView.setTextColor(0x88000000);
                itemTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new RecyclerView.ViewHolder(itemTextView) {
                };
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        final Item item = data.get(position);
        switch ( item.type ) {
            case HEADER :
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                itemController.refferalItem = item;
                itemController.header_title.setText(item.text);
                if ( item.invisibleChildren == null ) {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.shorten_arrow);
                }
                else {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.expand_arrow);
                }
                itemController.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        if ( item.invisibleChildren == null ) {
                            item.invisibleChildren = new ArrayList<>();
                            int count = 0;
                            int pos = data.indexOf(itemController.refferalItem);
                            while ( data.size() > pos + 1 && data.get(pos + 1).type == CHILD ) {
                                item.invisibleChildren.add(data.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemController.btn_expand_toggle.setImageResource(R.drawable.expand_arrow);
                        }
                        else {
                            int pos = data.indexOf(itemController.refferalItem);
                            int index = pos + 1;
                            for ( Item i : item.invisibleChildren ) {
                                data.add(index, i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
                            itemController.btn_expand_toggle.setImageResource(R.drawable.shorten_arrow);
                            item.invisibleChildren = null;
                        }
                    }
                });
                break;
            case CHILD :
                TextView itemTextView = (TextView) holder.itemView;
                itemTextView.setText(data.get(position).text);
                break;
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return data.get(position).type;
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    private static class ListHeaderViewHolder extends RecyclerView.ViewHolder
    {
        public TextView header_title;
        public ImageView btn_expand_toggle;
        public Item refferalItem;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            header_title = itemView.findViewById(R.id.header_title);
            btn_expand_toggle = itemView.findViewById(R.id.btn_expand_toggle);
        }
    }

    public static class Item
    {
        public int type;
        public String text;
        public List<Item> invisibleChildren;

        public Item(int type, String text)
        {
            this.type = type;
            this.text = text;
        }
    }
}
