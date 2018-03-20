package com.example.android.bakingrecipes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.android.bakingrecipes.RecipeObjects.Recipe;

import java.util.ArrayList;


/**
 * Created by Silky on 10/03/2018.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private ArrayList<Recipe> mArrayOfRecipes;
    private final OnItemClickListener listener;

    public RecipesAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Recipe recipeItem);
    }

    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.recipe_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        RecipesAdapter.ViewHolder viewHolder = new RecipesAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesAdapter.ViewHolder holder, int position) {

        holder.bind(mArrayOfRecipes.get(position), listener);
        holder.mRecipeItemTextView.setText(mArrayOfRecipes.get(position).getRecipeName());

    }

    @Override
    public int getItemCount() {

        if(mArrayOfRecipes==null) return 0;
        return mArrayOfRecipes.size();
    }

    public void setRecipeData(ArrayList<Recipe> recipes) {
        mArrayOfRecipes = recipes;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mRecipeItemTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mRecipeItemTextView = itemView.findViewById(R.id.recipe_item);
        }

        public void bind(final Recipe mRecipe, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.onItemClick(mRecipe);
                }
            });
        }
    }
}
