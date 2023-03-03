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

        getUsersDietaryRestrictions()
        getUsersCuisinePreferences()
        //Temporarily, this will load all the lunch recipes, just to show that the recyclerview works, but this should be replaced with the recipes we recommend, in sorted order
        recommendedRecipes.addAll(0,recommendRecipes())
        recipesAdapter= context?.let { RecipeAdapter(it,recommendedRecipes) }!!
        postRecyclerView = view.findViewById(R.id.postRecyclerView)

        postRecyclerView.adapter=recipesAdapter
        postRecyclerView.layoutManager=LinearLayoutManager(context)
        //Next steps
        // Set click listeners for each recipe. Likely done in the adapter
        // Find a default image for food, because there are way too many dead images
        setupCurrentIngredients()
        Log.d(TAG, "set up ingredients")

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
        database.child("user_survey_preference").child(user.uid).child("allergies").get().addOnCompleteListener {
            if (it.isSuccessful && it.result.value != null) {
                userDietaryRestrictions.clear()
                Log.d(TAG, "aaaaa" + it.result)
                Log.d(TAG, "bbbbb" + (it.result.value as Any).javaClass.name)
                for (dietaryRestriction in it.result.value as List<String>) {
                    Log.d(TAG, "ccc " + dietaryRestriction)
                    Log.d(TAG, "ddd" + formatRestrictions[dietaryRestriction].toString())
                    if (dietaryRestriction in formatRestrictions) {
                        val restriction = formatRestrictions[dietaryRestriction] as String
                        userDietaryRestrictions.add(restriction)
                    }
                }
            }
        }
    }

    fun getUsersCuisinePreferences() {
        val database= Firebase.database.reference
        val auth= Firebase.auth
        val user= auth.currentUser ?: return
        database.child("user_personal_model").child(user.uid).get().addOnCompleteListener {
            if (it.isSuccessful && it.result.value != null) {
                userCuisinePreferences.clear()
                val preferences = it.result.value as Map<String, Int>
                for(entry in preferences) {
                    userCuisinePreferences[entry.key] = entry.value
                }
            }
            Log.d(TAG, "testing123 $userCuisinePreferences")
        }
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

    private fun getUsersStemmedIngredients(): MutableList<String> {
        val database= Firebase.database.reference
        val auth= Firebase.auth
        val user= auth.currentUser ?: return mutableListOf()
        var usersIngredients = mutableListOf<String>()

        database.child("user_pantry").child(user.uid).get().addOnCompleteListener {
            if (it.isSuccessful && it.result.value != null) {
                Log.d(TAG, "debugga " + it.result.value.toString())
                Log.d(TAG, "debugga2" + recipesViewModel.ingredientsToStemmed)
                for (ingredient in it.result.value as List<String>) {
                    Log.d(TAG, ingredient.toString() + " testing")
                    if (ingredient.lowercase() in recipesViewModel.ingredientsToStemmed) {
                        Log.d(TAG, ingredient.toString() + " in dictionary")
                        usersIngredients.add(ingredient)
                        Log.d(TAG, usersIngredients.toString())
                    }
                }
            }
        }
        return usersIngredients
    }

    fun recommendRecipes(): MutableList<Recipe> {
        val recipes = getRecipes()
        val ingredientToStemmedIngredient = recipesViewModel.ingredientsToStemmed
        val usersIngredients = getUsersStemmedIngredients()
        Log.d(TAG, "USERSINGREDIENTS " + usersIngredients)
        val recipeRatings = mutableMapOf<Recipe, Double>()
        for (i in recipes.indices) {
            val recipe = recipes[i] as Recipe
            // check adheres to dietary restrictions here
//            if (userDietaryRestrictions.size > 0) {
//                for (dietaryRestriction in userDietaryRestrictions) {
//                    if (dietaryRestriction !in recipe.dietaryCompliances) {
//                        recipeRatings[recipe] = 0.0
//                        continue
//                    }
//                }
//            }
            val requiredIngredients = recipe.ingredients
            var ingredientsInPantry = 0
            for (ingredient in requiredIngredients) {
                val stemmedIngredient = ingredientToStemmedIngredient[ingredient]
                if (stemmedIngredient in usersIngredients) {
                    ingredientsInPantry += 1
                }
            }
            var recipeRating = 0.0
            if (recipe.cuisine in userCuisinePreferences) {
                recipeRating = ((ingredientsInPantry * 1.0) / (requiredIngredients.size * 1.0)) * (1 + 0.05 * (userCuisinePreferences[recipe.cuisine] as Int))
            }
            recipeRatings[recipe] = recipeRating
        }

        val sorted = recipeRatings.toList().sortedBy { (_, value) -> value * -1}
        val topRecipes = mutableListOf<Recipe>()
        for (i in 0..10) {
            topRecipes.add(sorted[i].first)
            if (sorted[i].second > 0) {
                Log.d(TAG, "NOT ZERO" + sorted[i].first.toString() + " " + sorted[i].second.toString())
            }
            Log.d(TAG, "recipe and scoring " + sorted[i].first.toString() + sorted[i].second.toString())
        }
        return topRecipes
    }
}