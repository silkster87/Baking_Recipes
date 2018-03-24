package com.example.android.bakingrecipes.UI;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingrecipes.R;
import com.example.android.bakingrecipes.RecipeObjects.Step;

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
        MasterListAdapter.ViewHolder viewHolder = new MasterListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MasterListAdapter.ViewHolder holder, int position) {
        holder.bind(listOfSteps.get(position), listener);
        holder.mRecipeItemTextView.setText(listOfSteps.get(position).getmShortDesc());

    }

    @Override
    public int getItemCount() {
        if(listOfSteps==null)return 0;
        return listOfSteps.size();
    }


    public interface OnItemClickListener {
        void onItemClick(Step recipeStep);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mRecipeItemTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mRecipeItemTextView = itemView.findViewById(R.id.recipe_item);
        }

        public void bind(final Step step, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.onItemClick(step);
                }
            });
        }
    }
}
