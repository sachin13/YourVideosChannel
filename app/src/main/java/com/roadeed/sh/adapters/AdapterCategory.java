package com.roadeed.sh.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roadeed.sh.Config;
import com.roadeed.sh.R;
import com.roadeed.sh.models.Category;
import com.roadeed.sh.models.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ViewHolder> {

    private List<Category> items = new ArrayList<>();
    private List<Category> temp = new ArrayList<>();
    String searchString;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Category obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterCategory(Context context, List<Category> items) {
        this.items = items;
        temp = items;
        ctx = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView category_name;
        public TextView video_count;
        public ImageView category_image;
        public LinearLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            category_name = v.findViewById(R.id.category_name);
            video_count = v.findViewById(R.id.video_count);
            category_image = v.findViewById(R.id.category_image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Category c = items.get(position);

        if (searchString != null && !searchString.equalsIgnoreCase("")) {
            // searchString="fi";
            if (c.category_name.contains(searchString)) {

                int startPos = c.category_name.indexOf(searchString);
                int endPos = startPos + searchString.length();

                Spannable spanString = Spannable.Factory.getInstance().newSpannable(c.category_name);
                spanString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.category_name.setText(spanString);
            }
        }else {
            holder.category_name.setText(c.category_name);
        }
        holder.video_count.setText(c.video_count + " " + ctx.getResources().getString(R.string.video_count_text));

        Picasso.with(ctx)
                .load(Config.ADMIN_PANEL_URL + "/upload/category/" + c.category_image)
                .placeholder(R.drawable.ic_thumbnail)
                .into(holder.category_image);


       holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, c, position);
                }
            }
        });
    }

    public void setListData(List<Category> items){
        this.items = items;
        temp = items;
        notifyDataSetChanged();
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        temp = items;
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }


    public List<Category> updateList(List<Category> newList, String query){

        searchString = query;

        for (Category user : temp){
            if(user.getCategory_name().contains(query)){
                newList.add(user);
                //Toast.makeText(ctx, ""+(user.getCategory_name()), Toast.LENGTH_SHORT).show();
            }
        }

        items = new ArrayList<>();
        items.addAll(newList);
        notifyDataSetChanged();
        return items;
    }



}