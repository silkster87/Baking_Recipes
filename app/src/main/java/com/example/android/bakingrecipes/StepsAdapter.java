package com.example.android.bakingrecipes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingrecipes.RecipeObjects.Step;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**This adapter will display the recycler view list of steps in the Recipe Detail Activity
 * Created by Silky on 17/03/2018.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {

   private ArrayList<Step> mArrayOfSteps;
   private final OnItemClickListener listener;
   private String TAG = StepsAdapter.class.getSimpleName();

   public StepsAdapter(OnItemClickListener listener, ArrayList<Step> mArrayOfSteps){
       this.listener=listener;
       this.mArrayOfSteps=mArrayOfSteps;
   }

   public interface OnItemClickListener {
       void onItemClick(Step stepItem, int position);
   }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.step_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new StepsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mArrayOfSteps.get(position), listener, position);

        if(position==0){
            holder.mStepItemTextView.setText(mArrayOfSteps.get(position).getmDescription());
        } else {
            String step = Integer.toString(mArrayOfSteps.get(position).getmID()) + ". " + mArrayOfSteps.get(position).getmShortDesc();
            holder.mStepItemTextView.setText(step);
        }

        try{
            String thumbNailURL = mArrayOfSteps.get(position).getmThumbNailURL();
            Context context = holder.mStepItemThumbNail.getContext();
            Picasso.with(context).load(thumbNailURL).into(holder.mStepItemThumbNail);
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "Unable to load Step thumbnail of " + mArrayOfSteps.get(position).getmShortDesc() + " step.");
            holder.mStepItemThumbNail.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if(mArrayOfSteps==null) return 0;
        return mArrayOfSteps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mStepItemTextView;
        public final ImageView mStepItemThumbNail;

        public ViewHolder(View itemView) {
            super(itemView);
            mStepItemTextView = itemView.findViewById(R.id.step_item_textView);
            mStepItemThumbNail = itemView.findViewById(R.id.step_item_thumbNail);
        }

        public void bind(final Step step, final OnItemClickListener listener, final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(step, position);
                }
            });
        }
    }
}
