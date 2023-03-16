package uci.students.foodforyou.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uci.students.foodforyou.Activities.DisplayActivity
import uci.students.foodforyou.Activities.fragments.HomeFragment
import uci.students.foodforyou.Models.Recipe
import uci.students.foodforyou.R

class RecipeAdapter(val context: Context, val recommendedRecipes:List<Recipe>, val homeFragment: HomeFragment) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recipe,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recommendedRecipes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recommendedRecipes[position])
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        val btnCook=itemView.findViewById<Button>(R.id.btnCook)
        val ivRecipeImage=itemView.findViewById<ImageView>(R.id.ivRecipeImage)
        val tvRecipeName=itemView.findViewById<TextView>(R.id.tvName)
        val tvMissingIngr=itemView.findViewById<TextView>(R.id.tvMissingIngr)
        val tvCuisine=itemView.findViewById<TextView>(R.id.tvCuisine)
        fun bind(recipe:Recipe)
        {
            tvRecipeName.text=recipe.name
            tvCuisine.text="Cuisine: ${recipe.cuisine.capitalize()}"
            //Load the image into the ImageView
            Glide.with(itemView).load(recipe.imageUrl).error("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdrAYfsGt5sbUrmjHD_fLDuymf2GcjDji78ed2GOg&s").into(ivRecipeImage)

            tvMissingIngr.text=recipe.missingIngredient.joinToString(", ")

            btnCook.setOnClickListener {
                homeFragment.onClick(itemView,recipe)
            }
        }
    }
}