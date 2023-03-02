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
import java.util.*

class HomeFragment : Fragment() {
    val TAG="HomeFragment"
    lateinit var postRecyclerView: RecyclerView
    lateinit var recipesViewModel:AppActivityViewModel
    lateinit var recipesAdapter:RecipeAdapter
    val listOfPantryIngredients= mutableListOf<String>()
    val recommendedRecipes= mutableListOf<Recipe>()
    val missingIngredientsForEachRecipe= mutableListOf<List<String>>()
    val userDietaryRestrictions = mutableListOf<String>()
    val formatRestrictions = mapOf("Vegan" to "vegan", "Vegetarian" to "vegetarian", "Pescatarian" to "pescatarian", "Milk" to "milk-free", "Egg" to "egg-free", "Fish" to "fish-free", "Shellfish" to "shellfish-free", "Nuts" to "nut-free", "Peanuts" to "peanut-free", "Soy" to "soy-free", "Pork" to "pork-free")
    var userCuisinePreferences = mutableMapOf<String, Int>()
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
        Log.d(TAG, "set up ingredients")
        getUsersDietaryRestrictions()
        Log.d(TAG, "set up dietary restrictions " + userDietaryRestrictions.toString())
//        getUsersCuisinePreferences()
//        Log.d(TAG, "set up cuisine preferences " + userCuisinePreferences.toString())
        // make function to get cuisine preferences, dietary restrictions
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

    fun getUsersDietaryRestrictions() {
        val database= Firebase.database.reference
        val auth= Firebase.auth
        val user= auth.currentUser ?: return

        database.child(getString(R.string.DatabasePersonalModel)).child(user.uid).child("allergies").get().addOnCompleteListener {
            if (it.isSuccessful && it.result.value != null) {
                userDietaryRestrictions.clear()
                for (dietaryRestriction in it.result.value as List<String>) {
                    if ((dietaryRestriction) in formatRestrictions) {
                        formatRestrictions[dietaryRestriction]?.let { it1 ->
                            userDietaryRestrictions.add(
                                it1
                            )
                        }
                    }
                }
            }
        }
    }

    fun getUsersCuisinePreferences() {
        val database= Firebase.database.reference
        val auth= Firebase.auth
        val user= auth.currentUser ?: return

        userCuisinePreferences.putAll(database.child(getString(R.string.DatabasePersonalModel)).child(user.uid).get() as MutableMap<String, Int>)
    }

    /**
     * A function for determining what ingredients are missing given the recipe and the availible ingredients
     * Due to the possibility that the recipe's ingredients are overly specified(i.e., yolk of eggs), while a user puts a much more simple ingredients (i.e. eggs)
     * This functions considers an ingredient to be shared if an ingredient from the user is a sub-sequence of an ingredient from the recipe
     */
    private fun getListOfMissingIngredient(recipe:Recipe,userIngredients:List<String>): List<String> {
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

    private fun getMealType(): String {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)

        return if (hour < 11) {
            "breakfast"
        } else if (hour < 17) {
            "lunch"
        } else {
            "dinner"
        }
    }

    private fun getRecipes(): List<*> {
        return when (getMealType()) {
            "breakfast" -> {
                recipesViewModel.breakfastRecipes
            }
            "lunch" -> {
                recipesViewModel.lunchRecipes
            }
            else -> {
                recipesViewModel.dinnerRecipes
            }
        }
    }

    private fun getDietaryRestrictions(): Map<String, *> {
        return when (getMealType()) {
            "breakfast" -> {
                recipesViewModel.breakfastDietaryRestrictions
            }
            "lunch" -> {
                recipesViewModel.lunchDietaryRestrictions
            }
            else -> {
                recipesViewModel.dinnerDietaryRestrictions
            }
        }
    }

    private fun getUsersStemmedIngredients(): List<String> {
        val ingredientToStemmedIngredient = recipesViewModel.ingredientsToStemmed
        val stemmedIngredients = mutableListOf<String>()
        for (ingredient in listOfPantryIngredients) {
            if (ingredient.lowercase() in ingredientToStemmedIngredient) {
                stemmedIngredients.add(ingredientToStemmedIngredient[ingredient] as String)
            }
        }
        return stemmedIngredients
    }

    fun recommendRecipes(): List<Recipe> {
        val recipes = getRecipes()
        val ingredientToStemmedIngredient = recipesViewModel.ingredientsToStemmed
        val usersIngredients = getUsersStemmedIngredients()
        val recipeRatings = mutableMapOf<Recipe, Double>()
        for (i in recipes.indices) {
            val recipe = recipes[i] as Recipe
            // check adheres to dietary restrictions here
            if (userDietaryRestrictions.size > 0) {
                for (dietaryRestriction in userDietaryRestrictions) {
                    if (dietaryRestriction !in recipe.dietaryCompliances) {
                        recipeRatings[recipe] = 0.0
                        continue
                    }
                }
            }
            val requiredIngredients = recipe.ingredients
            var ingredientsInPantry = 0
            for (ingredient in requiredIngredients) {
                val stemmedIngredient = ingredientToStemmedIngredient[ingredient]
                if (stemmedIngredient in usersIngredients) {
                    ingredientsInPantry += 1
                }
            }
            val recipeRating = (ingredientsInPantry / requiredIngredients.size) * (1 + 0.05 * userCuisinePreferences[recipe.cuisine]!!)
            recipeRatings[recipe] = recipeRating
        }

        val sorted = recipeRatings.toList().sortedBy { (_, value) -> value * -1}
        val topRecipes = mutableListOf<Recipe>()
        for (i in 0..10) {
            topRecipes.add(sorted[i].first)
        }
        return topRecipes
    }
}