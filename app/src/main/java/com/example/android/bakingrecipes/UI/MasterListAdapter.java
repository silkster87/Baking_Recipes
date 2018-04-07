package com.example.android.bakingrecipes.UI;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingrecipes.R;
import com.example.android.bakingrecipes.RecipeObjects.Step;
import com.squareup.picasso.Picasso;

import java.util.List;


/**This Master List Adapter will be used in the horizontal tablet layout. It will be attached
 * to the recycler view displaying the list of the Recipe Step descriptions. When a user clicks
 * on an item the corresponding video and full description are displayed on the right hand side of the master
 * detail flow layout.
 * Created by Silky on 18/03/2018.
 */

public class MasterListAdapter extends RecyclerView.Adapter<MasterListAdapter.ViewHolder> {

    private final OnItemClickListener listener;
    private List<Step> listOfSteps;
    private int row_index = 0;
    private String TAG = MasterListAdapter.class.getSimpleName();

    public MasterListAdapter(List<Step> listOfSteps, OnItemClickListener listener){
        this.listOfSteps=listOfSteps;
        this.listener=listener;
    }

    @NonNull
    @Override
    public MasterListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.recipe_step_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MasterListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MasterListAdapter.ViewHolder holder, final int position) {
        holder.bind(listOfSteps.get(position), listener, position);

        String recipeStepThumbNail = listOfSteps.get(position).getmThumbNailURL();
        Context context = holder.mRecipeItemTextView.getContext();
        try{
            Picasso.with(context).load(recipeStepThumbNail).into(holder.mStepThumbNail);
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "Unable to load Recipe Step image of " + listOfSteps.get(position).getmShortDesc());
        }
        if(position!=0) {
            String recipeStep = Integer.toString(listOfSteps.get(position).getmID()) + ". " + listOfSteps.get(position).getmShortDesc();
            holder.mRecipeItemTextView.setText(recipeStep);
        } else {
            holder.mRecipeItemTextView.setText(listOfSteps.get(position).getmShortDesc());
        }
    }

    @Override
    public int getItemCount() {
        if(listOfSteps==null)return 0;
        return listOfSteps.size();
    }

    public void setStepToHighlight(int stepNumber) {
        this.row_index=stepNumber;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Step recipeStep);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mRecipeItemTextView;
        public final LinearLayout mLinearLayout;
        public final ImageView mStepThumbNail;

        public ViewHolder(View itemView) {
            super(itemView);
            mLinearLayout = itemView.findViewById(R.id.list_item_linear_layout);
            mRecipeItemTextView = itemView.findViewById(R.id.recipe_item);
            mStepThumbNail = itemView.findViewById(R.id.step_item_thumbNail);
        }

        public void bind(final Step step, final OnItemClickListener listener, final int position) {
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.onItemClick(step);
                    row_index = position;
                    notifyDataSetChanged();
                }
            });
            if(row_index==position){
                mLinearLayout.setBackgroundColor(Color.parseColor("#9FA8DA"));
            }
            else {
                mLinearLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }
    }

}
