package com.example.android.bakingrecipes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingrecipes.RecipeObjects.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Silky on 10/03/2018.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private ArrayList<Recipe> mArrayOfRecipes;
    private final OnItemClickListener listener;

    private String TAG = RecipesAdapter.class.getSimpleName();

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

        return new RecipesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesAdapter.ViewHolder holder, int position) {

        holder.bind(mArrayOfRecipes.get(position), listener);

        String recipeName = mArrayOfRecipes.get(position).getRecipeName();
        holder.mRecipeItemTextView.setText(recipeName);

        String recipeImage = mArrayOfRecipes.get(position).getmImageURL();
        Context context = holder.mRecipeImageView.getContext();

        try {
            Picasso.with(context).load(recipeImage).into(holder.mRecipeImageView);
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "Unable to load Recipe image of " + recipeName + " recipe." );
            holder.mRecipeImageView.setVisibility(View.GONE);
        }
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
        public final ImageView mRecipeImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mRecipeItemTextView = itemView.findViewById(R.id.recipe_item);
            mRecipeImageView = itemView.findViewById(R.id.recipe_image_thumbnail);
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
