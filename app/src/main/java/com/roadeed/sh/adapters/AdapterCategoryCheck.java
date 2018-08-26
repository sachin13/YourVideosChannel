package com.roadeed.sh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.roadeed.sh.Config;
import com.roadeed.sh.R;
import com.roadeed.sh.models.Cat;
import com.roadeed.sh.models.Category;
import com.roadeed.sh.models.JsonCatgory;
import com.roadeed.sh.models.JsonResult;
import com.roadeed.sh.models.VolleyRequests;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eng-wafaa on 29/04/18.
 */

public class AdapterCategoryCheck extends RecyclerView.Adapter<AdapterCategoryCheck.ViewHolder> {

    private List<Category> items = new ArrayList<>();
    public List<String>switches=new ArrayList<String>();
    //public String[] switches = new String[items.size()];
    public String[] dynamicswitches=new String[items.size()];
    private Context ctx;
    private AdapterCategory.OnItemClickListener mOnItemClickListener;
    public  static boolean catt1=true;
    public static String cat1 = "true";
    public static String cat2 = "true";
    public static String cat3 = "true";
    public static String cat4 = "true";
    public static String cat5 = "true";

    int i=0;

    String token = FirebaseInstanceId.getInstance().getToken();

    public interface OnItemClickListener {
        void onItemClick(View view, Category obj, int position);
    }

    public void setOnItemClickListener(final AdapterCategory.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterCategoryCheck(Context context, List<Category> items) {
        this.items = items;
        ctx = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView category_name;
        public TextView video_count;
        public ImageView category_image;
        public LinearLayout lyt_parent;
        public Switch check_notification;
        
        public ViewHolder(View v) {
            super(v);
            category_name = v.findViewById(R.id.category_name);
            video_count = v.findViewById(R.id.video_count);
            category_image = v.findViewById(R.id.category_image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            check_notification=v.findViewById(R.id.switch1);

        }
    }

    @Override
    public AdapterCategoryCheck.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_check, parent, false);
        AdapterCategoryCheck.ViewHolder vh = new AdapterCategoryCheck.ViewHolder(v);


        return vh;
    }


    @Override
    public void onBindViewHolder(final AdapterCategoryCheck.ViewHolder holder, final int position) {
        final Category c = items.get(position);
        holder.category_name.setText(c.category_name);

        holder.video_count.setText(c.video_count + " " + ctx.getResources().getString(R.string.video_count_text));
        if(c.cat==null){
            holder.check_notification.setChecked(true);
        }
        else { holder.check_notification.setChecked(false);}





/*
        new VolleyRequests<>().setIReceiveData(new VolleyRequests.IReceiveData() {
            @Override
            public void onDataReceived(Object posts) {

            // for(int j=0;j<items.size();j++) {
                  // i=j;

                  if (((JsonCatgory) posts).getCat().get(position).matches("true")) {
                    holder.check_notification.setChecked(true);
                 //  switches.set(position, "true");
                 switches.add(position,"true");
                     // Toast.makeText(ctx, "my result " + ((JsonCatgory) posts).getCat().get(position), Toast.LENGTH_SHORT).show();

                  } else {
                      holder.check_notification.setChecked(false);
                  switches.add(position,"false");
                  // switches.set(position, "false");
                 }
             // }

            }
        //}
        }).getSwitchvalue(token);*/

        //holder.check_notification.setChecked(true);

        Picasso.with(ctx).load(Config.ADMIN_PANEL_URL + "/upload/category/" + c.category_image)
                .placeholder(R.drawable.ic_thumbnail)
                .into(holder.category_image);
        holder.check_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {


                        new VolleyRequests<>().setIReceiveData(new VolleyRequests.IReceiveData() {
                            @Override
                            public void onDataReceived(Object posts) {

                                Toast.makeText(ctx, " " + ((JsonResult) posts).getResult(), Toast.LENGTH_SHORT).show();


                                // if(String.valueOf(c.cid).matches("true")){
                                 //   Toast.makeText(ctx, " " + ((JsonResult) posts).getResult(), Toast.LENGTH_SHORT).show();
                              //  }
                                }



                        }).getCatgoryNotification(token,"true",c.cid);


                      /*  new VolleyRequests<>().setIReceiveData(new VolleyRequests.IReceiveData() {
                            @Override
                            public void onDataReceived(Object posts) {
                      Toast.makeText(ctx, " " + ((JsonResult) posts).getResult(), Toast.LENGTH_SHORT).show();

                            }
                        }).getCatgoryNotification(token,"true",position+1);
                        //holder.check_notification.setChecked(true);*/



                    } else {

                        new VolleyRequests<>().setIReceiveData(new VolleyRequests.IReceiveData() {
                            @Override
                            public void onDataReceived(Object posts) {
                             Toast.makeText(ctx, " " + ((JsonResult) posts).getResult(), Toast.LENGTH_SHORT).show();



                            }
                        }).getCatgoryNotification(token,"false",c.cid);



                      /*  new VolleyRequests<>().setIReceiveData(new VolleyRequests.IReceiveData() {
                            @Override
                            public void onDataReceived(Object posts) {
                       Toast.makeText(ctx, "" + ((JsonResult)posts).getResult(), Toast.LENGTH_SHORT).show();

                            }
                        }).getCatgoryNotification(token,"false",position+1);*/
                        //holder.check_notification.setChecked(false);



                    }}
               // }
           //}
        });

    }






    public void setListData(List<Category> items){
        this.items = items;
        notifyDataSetChanged();
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }
    public List<String> getSwitchValue(){
        return this.switches;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {


        return items.size();
    }

}