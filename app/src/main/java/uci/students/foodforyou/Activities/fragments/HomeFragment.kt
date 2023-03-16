package uci.students.foodforyou.Activities.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uci.students.foodforyou.Activities.AppActivity
import uci.students.foodforyou.Activities.DisplayActivity
import uci.students.foodforyou.Adapter.RecipeAdapter
import uci.students.foodforyou.Models.AppActivityViewModel
import uci.students.foodforyou.Models.Recipe
import uci.students.foodforyou.R
import java.util.*
import kotlin.math.log10
import kotlin.math.log2

class HomeFragment : Fragment(){
    val TAG="HomeFragment"
    lateinit var postRecyclerView: RecyclerView
    lateinit var recipesViewModel:AppActivityViewModel
    lateinit var recipesAdapter:RecipeAdapter
    lateinit var activityLauncher: ActivityResultLauncher<Intent>
    private lateinit var refBut:Button
    val listOfPantryIngredients= mutableListOf<String>()
    val recommendedRecipes= mutableListOf<Recipe>()

    var userDietaryRestrictions = mutableListOf<String>()

    val formatRestrictions = mapOf("Vegan" to "vegan", "Vegetarian" to "vegetarian", "Pescatarian" to "pescatarian", "Milk" to "milk-free", "Egg" to "egg-free", "Fish" to "fish-free", "Shellfish" to "shellfish-free", "Nuts" to "nut-free", "Peanuts" to "peanut-free", "Soy" to "soy-free", "Pork" to "pork-free")
    var userCuisinePreferences = mutableMapOf<String, Int>()

    var usersIngredients = mutableListOf<String>()

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

        postRecyclerView = view.findViewById(R.id.postRecyclerView)
        refBut = view.findViewById<Button>(R.id.refrbutton)

        recommendRecipes()

        recipesAdapter= context?.let { RecipeAdapter(it,recommendedRecipes,this) }!!
        postRecyclerView = view.findViewById(R.id.postRecyclerView)
        postRecyclerView.adapter=recipesAdapter
        postRecyclerView.layoutManager=LinearLayoutManager(context)

        Log.d(TAG, "set up ingredients")


        activityLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            Toast.makeText(context,"Returning from displayActivity",Toast.LENGTH_SHORT).show()
            val postDisplayDialogFragment=PostDisplayDialogFragment()
            val bundle=Bundle()
            bundle.putParcelable("recipe", it.data?.getParcelableExtra("recipe"))
            postDisplayDialogFragment.arguments=bundle
            postDisplayDialogFragment.show(childFragmentManager,"PostRecipeSurvey")
            recommendRecipes()
        }
        refBut.setOnClickListener{
            recommendRecipes()
        }
        // make function to get cuisine preferences, dietary restrictions
    }


    fun setupCurrentIngredients() {
        val database= Firebase.database.reference
        val auth= Firebase.auth
        val user= auth.currentUser ?: return
        database.child(getString(R.string.DatabaseIngredientsPantry)).child(user.uid).get().addOnCompleteListener {
            listOfPantryIngredients.clear()
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

    fun recommendRecipes() {
        //Added this in since recipes were duplicating if we switched between fragments
        recommendedRecipes.clear()
        getUsersDietaryRestrictions()
    }

    /**
     * Get User's dietary restrictions from database and store in list
     */
    fun getUsersDietaryRestrictions() {
        val database= Firebase.database.reference
        val auth= Firebase.auth
        val user= auth.currentUser ?: return
        database.child("user_survey_preference").child(user.uid).child("allergies").get().addOnCompleteListener {
            if (it.isSuccessful && it.result.value != null) {
                userDietaryRestrictions.clear()
                for (dietaryRestriction in it.result.value as List<String>) {
                    if (dietaryRestriction in formatRestrictions) {
                        val restriction = formatRestrictions[dietaryRestriction] as String
                        userDietaryRestrictions.add(restriction)
                    }
                }
            }
            getUsersCuisinePreferences()
        }
    }

    /**
     * Get mapping of User's cuisine preferences from database and store in Map
     */
    fun getUsersCuisinePreferences() {
        val database= Firebase.database.reference
        val auth= Firebase.auth
        val user= auth.currentUser ?: return
        database.child("user_personal_model").child(user.uid).get().addOnCompleteListener {
            if (it.isSuccessful && it.result.value != null) {
                userCuisinePreferences.clear()
                val preferences = it.result.value as Map<String, Int>
                for(entry in preferences) {
                    userCuisinePreferences[entry.key.lowercase()] = entry.value
                }
            }
            getUsersStemmedIngredients()
            Log.d(TAG, "testing123 $userCuisinePreferences")
            setupCurrentIngredients()
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

    /**
     * Determine the types of meals to recommend (breakfast, lunch, or dinner)
     */
    fun getMealType(): String {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val database= Firebase.database.reference
        val auth= Firebase.auth
        val user = Firebase.auth.currentUser
        var meals = mutableListOf<String>()
        var toReturn = ""
        user?.let {
            database.child("user_pantry").child(it.uid).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.value != null) {
                        val products = it.result.value as List<String>
                        if (products.isEmpty() == false) {
                            //Clear panIng first as it made already have content from switching off this fragment and back
                            meals.clear()
                            for (item in products) {
                                meals.add(item)
                            }
                        }
                    }
                }
            }
            if (meals.size == 1){
                toReturn = meals[0]
            }
            else{
                if (hour < 11 && meals.contains("breakfast")) {
                    toReturn = "breakfast"
                } else if (hour < 17 && meals.contains("lunch")) {
                    toReturn ="lunch"
                } else if (meals.contains("dinner")){
                    toReturn = "dinner"
                } else{
                    toReturn = "lunch"
                }
            }
        }
        return toReturn
    }

    /**
     * Returns the recipes of the correct meal type
     */
    fun getRecipes(): List<*> {
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

    /**
     * Gets the User's ingredients' stemmed mappings
     */
    fun getUsersStemmedIngredients() {
        val database= Firebase.database.reference
        val auth= Firebase.auth
        val user= auth.currentUser ?: return

        database.child("user_pantry").child(user.uid).get().addOnCompleteListener {
            usersIngredients.clear()
            if (it.isSuccessful && it.result.value != null) {
                Log.d(TAG, "debugga " + it.result.value.toString())
                Log.d(TAG, "debugga2" + recipesViewModel.ingredientsToStemmed)
                for (ingredient in it.result.value as List<String>) {
                    Log.d(TAG, ingredient.toString() + " testing")
                    if (ingredient.lowercase() in recipesViewModel.ingredientsToStemmed) {
                        Log.d(TAG, ingredient + " in dictionary")
                        usersIngredients.add(ingredient)
                        Log.d(TAG, usersIngredients.toString())
                    }
                }
            }
            recommendedRecipes.addAll(0,getRecommendRecipes())
            recipesAdapter.notifyDataSetChanged()
        }

    }

    /**
     * Recommends the top 10 recipes to the user from highest score to lowest score
     */
    fun getRecommendRecipes(): List<Recipe> {
        val recipes = getRecipes()
        val ingredientToStemmedIngredient = recipesViewModel.ingredientsToStemmed

        Log.d(TAG, "USERSINGREDIENTS " + usersIngredients)
        val recipeRatings = mutableMapOf<Recipe, Double>()
        for (i in recipes.indices) {
            val recipe = recipes[i] as Recipe

            // calculate percentage of ingredients in the recipe that the User has in their pantry
            val requiredIngredients = recipe.ingredients
            var ingredientsInPantry = 0
            for (ingredient in requiredIngredients) {
                val stemmedIngredient = ingredientToStemmedIngredient[ingredient]
                for (userIngredient in usersIngredients) {
                    //Updated to check if our pantry is a subsequence of the required ingredients since required
                    //sometimes has measurements as well
                    if (stemmedIngredient.toString().contains(userIngredient, true)) {
                        ingredientsInPantry += 1
                    }
                }
            }

            // calculate recipe rating using percentage of ingredients available and the User's
            // cuisine preferences
            var recipeRating = 0.0

            if (recipe.cuisine.lowercase() in userCuisinePreferences && userCuisinePreferences[recipe.cuisine.lowercase()]!! >= 1) {
                recipeRating = ((ingredientsInPantry * 1.0) / (requiredIngredients.size * 1.0)) + (.2 + log10((userCuisinePreferences[recipe.cuisine.lowercase()] as Number).toDouble()))
            }
            else{
                //If the recipe's cuisine ranking does not exist, or is less 1, then only consider the score with what ingredients they have.
                recipeRating=((ingredientsInPantry * 1.0) / (requiredIngredients.size * 1.0))
            }

            // check that the recipe adheres to dietary restrictions here else give it a value of 0
            if (userDietaryRestrictions.size > 0) {
                for (dietaryRestriction in userDietaryRestrictions) {
                    if (dietaryRestriction !in recipe.dietaryCompliances) {
                        recipeRatings[recipe] = -1.0
                    }
                }
            }
            recipeRatings[recipe] = recipeRating
        }

        // Sort recipes by order of highest score to lowest score
        val sorted = recipeRatings.toList().sortedBy { (_, value) -> value * -1}
        val topRecipes = mutableListOf<Recipe>()

        // Return the top 10 scoring recipes
        for (i in 0..30) {
            topRecipes.add(sorted[i].first)
            Log.d(TAG, "recipe and scoring " + sorted[i].first.toString() + sorted[i].second.toString())
        }
        return topRecipes
    }

    fun onClick(p0: View?,recipe:Recipe) {
        activityLauncher.launch(Intent(context, DisplayActivity::class.java).putExtra("ParcelableRecipe",recipe))
    }
}