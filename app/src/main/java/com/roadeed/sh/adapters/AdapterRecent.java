package com.roadeed.sh.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.roadeed.sh.Config;
import com.roadeed.sh.R;
import com.roadeed.sh.databases.DatabaseHandlerFavorite;
import com.roadeed.sh.models.Video;
import com.roadeed.sh.utils.Constant;
import com.roadeed.sh.utils.Tools;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterRecent extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Video> items = new ArrayList<>();
    private List<Video> temp = new ArrayList<>();
    String searchString;

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private Video pos;
    private CharSequence charSequence = null;
    private DatabaseHandlerFavorite databaseHandler;

    int gridNumber = 1;////////////////////////////////////////////////////////////////////

    public interface OnItemClickListener {
        void onItemClick(View view, Video obj, int position, List<Video> list);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }
    public AdapterRecent(Context context, RecyclerView view, List<Video> items, int gridNumber) {/////////////////////////////
        this.items = items;
        temp = items;
        this.context = context;
        this.gridNumber = gridNumber;////////////////////////////////////////////////////
        lastItemViewDetector(view);
    }

    public AdapterRecent(Context context, RecyclerView view, List<Video> items, String searchString) {
        this.items = items;
        temp = items;
        this.searchString = searchString;
        this.context = context;
        lastItemViewDetector(view);
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

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.load_more);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            if (!Config.DISPLAY_DATE_AS_TIME_AGO && !Config.ENABLE_VIEW_COUNT) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_no_date_views, parent, false);
                vh = new OriginalViewHolder(v);
            } else if (Config.ENABLE_SINGLE_ROW_COLUMN) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_single_column, parent, false);
                vh = new OriginalViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
                vh = new OriginalViewHolder(v);

                if(gridNumber == 2) {//////////////////////////////////
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video2, parent, false);
                    vh = new OriginalViewHolder(v);
                }
                if(gridNumber >= 3) {////////////////////////////////
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video3, parent, false);
                    vh = new OriginalViewHolder(v);
                }
            }
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false);
            vh = new ProgressViewHolder(v);
        }



        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
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
            }else {
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
                        mOnItemClickListener.onItemClick(view, p, position, items);
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

          /*  if(gridNumber >= 3) {
                vItem.video_title.setTextSize(14);
                vItem.category_name.setTextSize(12);
                vItem.total_views.setTextSize(12);
                vItem.date_time.setTextSize(12);
            }*/

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROG;
        }
    }

    public void insertData(List<Video> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        if (onLoadMoreListener != null) {
                            int current_page = getItemCount() / Config.LOAD_MORE;
                            onLoadMoreListener.onLoadMore(current_page);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
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
                    String share_content = android.text.Html.fromHtml(context.getResources().getString(R.string.name)).toString();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, share_title + "\n\n" + pos.video_url + "\n\n" + share_content + "\n\n" + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
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

     /*   for(int i = 0; i<newList.size();i++) {
            CharSequence chars = highlightText(query, newList.get(i).getVideo_title());
            final StringBuilder sb = new StringBuilder(chars.length());
            sb.append(chars);
            String videoTitle = sb.toString();
            //String videoTitle =  String.valueOf();
            newList.set(i,new Video(newList.get(i).category_name,newList.get(i).vid,chars,
                    newList.get(i).video_url,newList.get(i).video_id,newList.get(i).video_thumbnail,
                    newList.get(i).video_duration,newList.get(i).video_description,
                    newList.get(i).video_type,newList.get(i).total_views,newList.get(i).date_time));
        }*/

        items = new ArrayList<>();
        items.addAll(newList);
        notifyDataSetChanged();
        return items;
    }

    public static CharSequence highlightText(String search, String originalText) {
        if (search != null && !search.equalsIgnoreCase("")) {
            String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
            int start = normalizedText.indexOf(search);
            if (start < 0) {
                return originalText;
            } else {
                Spannable highlighted = new SpannableString(originalText);
                while (start >= 0) {
                    int spanStart = Math.min(start, originalText.length());
                    int spanEnd = Math.min(start + search.length(), originalText.length());
                    highlighted.setSpan(new ForegroundColorSpan(Color.BLUE), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = normalizedText.indexOf(search, spanEnd);
                }
                return highlighted;
            }
        }
        return originalText;
    }
}