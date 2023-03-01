package uci.students.foodforyou.Activities.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uci.students.foodforyou.Activities.AppActivity
import uci.students.foodforyou.Adapter.RecipeAdapter
import uci.students.foodforyou.Models.AppActivityViewModel
import uci.students.foodforyou.Models.Recipe
import uci.students.foodforyou.R

class HomeFragment : Fragment() {
    val TAG="HomeFragment"
    lateinit var postRecyclerView: RecyclerView
    lateinit var recipesViewModel:AppActivityViewModel
    lateinit var recipesAdapter:RecipeAdapter
    val listOfPantryIngredients= mutableListOf<String>()
    val recommendedRecipes= mutableListOf<Recipe>()
    val missingIngredientsForEachRecipe= mutableListOf<List<String>>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Unlike in the AppActivity, we do not pass "this" in as the owner, as that would imply the HomeFragment is the owner. The owner is actually AppActivity
        recipesViewModel= ViewModelProvider(activity as AppActivity).get(AppActivityViewModel::class.java)
        Log.d(TAG,"In the Homefragment we have the recipes. Here are some for example ${recipesViewModel.breakfastRecipes}")

        //Temporarily, this will load all the lunch recipes, just to show that the recyclerview works, but this should be replaced with the recipes we recommend, in sorted order
        recommendedRecipes.addAll(0,recipesViewModel.lunchRecipes)
        recipesAdapter= context?.let { RecipeAdapter(it,recommendedRecipes) }!!
        postRecyclerView = view.findViewById(R.id.postRecyclerView)

        postRecyclerView.adapter=recipesAdapter
        postRecyclerView.layoutManager=LinearLayoutManager(context)
        //Next steps
        // Set click listeners for each recipe. Likely done in the adapter
        // Find a default image for food, because there are way too many dead images
        setupCurrentIngredients()
    }

    fun setupCurrentIngredients() {
        val database= Firebase.database.reference
        val auth= Firebase.auth
        val user= auth.currentUser ?: return

        database.child(getString(R.string.DatabaseIngredientsPantry)).child(user.uid).get().addOnCompleteListener {
            if (it.isSuccessful && it.result.value!=null)
            {
                listOfPantryIngredients.clear()
                listOfPantryIngredients.addAll(it.result.value as List<String>)

            }
            //For each recipe, add the missing ingredients to the object
            for (recipe in recommendedRecipes)
            {
                recipe.missingIngredient.clear()
                recipe.missingIngredient.addAll(getListOfMissingIngredient(recipe,listOfPantryIngredients))
            }
            recipesAdapter.notifyDataSetChanged()


        }
    }

    /**
     * A function for determining what ingredients are missing given the recipe and the availible ingredients
     * Due to the possibility that the recipe's ingredients are overly specified(i.e., yolk of eggs), while a user puts a much more simple ingredients (i.e. eggs)
     * This functions considers an ingredient to be shared if an ingredient from the user is a sub-sequence of an ingredient from the recipe
     */
    fun getListOfMissingIngredient(recipe:Recipe,userIngredients:List<String>): List<String> {
        //missingRecipeIngredients needs to be a copy of recipe.ingredients as we will be modifying recipeIngredients to contain only the missing ingredients
        val missingRecipeIngredients=recipe.ingredients.toMutableList()

        for (item in userIngredients)
        {
            val ingIterator = missingRecipeIngredients.iterator()
            while (ingIterator.hasNext())
            {
                val item2 = ingIterator.next()
                if (item2.contains(item,true))
                {
                    ingIterator.remove()
                }
            }
        }
        Log.i(TAG,missingRecipeIngredients.toString())
        return missingRecipeIngredients.toList()
    }
}