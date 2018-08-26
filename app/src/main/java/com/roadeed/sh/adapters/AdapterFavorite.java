package com.roadeed.sh.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.roadeed.sh.Config;
import com.roadeed.sh.R;
import com.roadeed.sh.databases.DatabaseHandlerFavorite;
import com.roadeed.sh.models.Video;
import com.balysv.materialripple.MaterialRippleLayout;
import com.roadeed.sh.utils.Constant;
import com.roadeed.sh.utils.Tools;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterFavorite extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;

    private List<Video> items = new ArrayList<>();
    private List<Video> temp = new ArrayList<>();
    String searchString;
    int gridNumber = 1;////////////////////////////////////////////

    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private Video pos;
    private CharSequence charSequence = null;
    private DatabaseHandlerFavorite databaseHandler;

    public interface OnItemClickListener {
        void onItemClick(View view, Video obj, int position, List<Video> items);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterFavorite(Context context, RecyclerView view, List<Video> items,  int gridNumber) {////////////
        this.items = items;
        temp = items;
        this.context = context;
        this.gridNumber = gridNumber;///////////////////////////////////////////////////////////////////////////
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView category_name;
        public TextView video_title;
        public TextView video_duration;
        public TextView total_views;
        public TextView date_time;
        public TextView space;
        public ImageView video_thumbnail;
        public MaterialRippleLayout lyt_parent;
        public MaterialRippleLayout overflow;

        public OriginalViewHolder(View v) {
            super(v);
            category_name = v.findViewById(R.id.category_name);
            video_title = v.findViewById(R.id.video_title);
            video_duration = v.findViewById(R.id.video_duration);
            date_time = v.findViewById(R.id.date_time);
            total_views = v.findViewById(R.id.total_views);
            space = v.findViewById(R.id.space);
            video_thumbnail = v.findViewById(R.id.video_thumbnail);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            overflow = v.findViewById(R.id.ripple_overflow);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (!Config.DISPLAY_DATE_AS_TIME_AGO && !Config.ENABLE_VIEW_COUNT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_no_date_views, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            vh = new OriginalViewHolder(v);
            if(gridNumber == 2) {////////////////////////////////////////////////////////////////////
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video2, parent, false);
                vh = new OriginalViewHolder(v);
            }
            if(gridNumber >= 3) {///////////////////////////////////////////////////////////////////////
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video3, parent, false);
                vh = new OriginalViewHolder(v);
            }
        }


        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final Video p = items.get(position);
        final OriginalViewHolder vItem = (OriginalViewHolder) holder;

        vItem.category_name.setText(p.category_name);

        if (searchString != null && !searchString.equalsIgnoreCase("")) {
            // searchString="fi";
            if (p.video_title.contains(searchString)) {

                int startPos = p.video_title.indexOf(searchString);
                int endPos = startPos + searchString.length();

                Spannable spanString = Spannable.Factory.getInstance().newSpannable(p.video_title);
                spanString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                vItem.video_title.setText(spanString);
            }
        }else{
            vItem.video_title.setText(p.video_title);
        }
        vItem.video_duration.setText(p.video_duration);

        if (Config.ENABLE_VIEW_COUNT) {
            vItem.total_views.setText(Tools.withSuffix(p.total_views) + " " + context.getResources().getString(R.string.views_count));
        } else {
            vItem.total_views.setVisibility(View.GONE);
        }

        if (Config.ENABLE_DATE_DISPLAY && Config.DISPLAY_DATE_AS_TIME_AGO) {
            PrettyTime prettyTime = new PrettyTime();
            long timeAgo = Tools.timeStringtoMilis(p.date_time);
            vItem.date_time.setText(prettyTime.format(new Date(timeAgo)));
        } else if (Config.ENABLE_DATE_DISPLAY && !Config.DISPLAY_DATE_AS_TIME_AGO) {
            vItem.date_time.setText(Tools.getFormatedDateSimple(p.date_time));
        } else {
            vItem.date_time.setVisibility(View.GONE);
            vItem.space.setVisibility(View.GONE);
        }

        if (p.video_type != null && p.video_type.equals("youtube")) {
            Picasso.with(context)
                    .load(Constant.YOUTUBE_IMAGE_FRONT + p.video_id + Constant.YOUTUBE_IMAGE_BACK)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(vItem.video_thumbnail);
        } else {
            Picasso.with(context)
                    .load(Config.ADMIN_PANEL_URL + "/upload/" + p.video_thumbnail)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(vItem.video_thumbnail);
        }

        vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, p, position,items);
                }
            }
        });

        vItem.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = items.get(position);
                showPopupMenu(vItem.overflow);
            }
        });

       /* if(gridNumber >= 3) {
            vItem.video_title.setTextSize(14);
            vItem.category_name.setTextSize(12);
            vItem.total_views.setTextSize(12);
            vItem.date_time.setTextSize(12);
        }*/

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_ITEM;
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();

        databaseHandler = new DatabaseHandlerFavorite(context);
        List<Video> data = databaseHandler.getFavRow(pos.vid);
        if (data.size() == 0) {
            popup.getMenu().findItem(R.id.menu_context_favorite).setTitle(R.string.favorite_add);
            charSequence = popup.getMenu().findItem(R.id.menu_context_favorite).getTitle();
        } else {
            if (data.get(0).getVid().equals(pos.vid)) {
                popup.getMenu().findItem(R.id.menu_context_favorite).setTitle(R.string.favorite_remove);
                charSequence = popup.getMenu().findItem(R.id.menu_context_favorite).getTitle();
            }
        }
    }

    public class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private MyMenuItemClickListener() {

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.menu_context_favorite:
                    if (charSequence.equals(context.getString(R.string.favorite_add))) {
                        databaseHandler.AddtoFavorite(new Video(
                                pos.cat_id,
                                pos.category_name,
                                pos.vid,
                                pos.video_title,
                                pos.video_url,
                                pos.video_id,
                                pos.video_thumbnail,
                                pos.video_duration,
                                pos.video_description,
                                pos.video_type,
                                pos.total_views,
                                pos.date_time
                        ));
                        Toast.makeText(context, context.getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();

                    } else if (charSequence.equals(context.getString(R.string.favorite_remove))) {
                        databaseHandler.RemoveFav(new Video(pos.vid));
                        Toast.makeText(context, context.getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                    }
                    return true;

                case R.id.menu_context_share:

                    String share_title = android.text.Html.fromHtml(pos.video_title).toString();
                    String share_content = android.text.Html.fromHtml(context.getResources().getString(R.string.share_text)).toString();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, share_title + "\n\n" +pos.video_url+ share_content + "\n\n" + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);

                    return true;
                default:
            }
            return false;
        }
    }


    public List<Video> updateList(List<Video> newList, String query){

        searchString = query;

        for (Video user : temp){
            if(user.getVideo_title().contains(query)){
                newList.add(user);

            }
        }

        items = new ArrayList<>();
        items.addAll(newList);
        notifyDataSetChanged();
        return items;
    }

}