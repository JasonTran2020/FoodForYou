package uci.students.foodforyou.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import uci.students.foodforyou.Activities.fragments.HomeFragment
import uci.students.foodforyou.Activities.fragments.PantryFragment
import uci.students.foodforyou.Activities.fragments.ProfileFragment
import uci.students.foodforyou.Models.AppActivityViewModel
import uci.students.foodforyou.Models.Recipe
import uci.students.foodforyou.R

class AppActivity : AppCompatActivity() {
    val TAG="AppActivity"
    lateinit var breakfastRecipes: List<Recipe>
    lateinit var lunchRecipes: List<Recipe>
    lateinit var dinnerRecipes: List<Recipe>
    lateinit var recipesViewModel:AppActivityViewModel
    var missedRecipeCount=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        //Create and store the fragment instances as oppose to creating new ones each time
        val homeFragment=HomeFragment()
        val pantryFragment=PantryFragment()
        val profileFragment= ProfileFragment()
        val fragmentManager: FragmentManager = supportFragmentManager

        //Create the list of Recipes here, rather than in the fragment. Any of the fragments will be able to retrieve the lists from here
        breakfastRecipes=createRecipeListFromJSONFile("recipe_database/breakfast.json")
        Log.d(TAG,breakfastRecipes.toString())
        lunchRecipes=createRecipeListFromJSONFile("recipe_database/lunch.json")
        dinnerRecipes=createRecipeListFromJSONFile("recipe_database/dinner.json")
        Log.d(TAG,"When parsing the recipes from JSON, $missedRecipeCount were unabled to be parsed")

        recipesViewModel=ViewModelProvider(this).get(AppActivityViewModel::class.java)
        recipesViewModel.setRecipes(breakfastRecipes,lunchRecipes,dinnerRecipes)

        val ingredientsToStemmedIngredient = createMapFromJSONFile("ingredient_to_stemmed_ingredient.json")
        val breakfastDietaryRestrictions = createMapFromJSONFile("dietary_restrictions_data/breakfast.json")
        val lunchDietaryRestrictions = createMapFromJSONFile("dietary_restrictions_data/lunch.json")
        val dinnerDietaryRestrictions = createMapFromJSONFile("dietary_restrictions_data/dinner.json")

        //debugging REMOVE
        Log.d(TAG, "ingredients " + ingredientsToStemmedIngredient.toString())
        Log.d(TAG, "dinner recipes: " + dinnerDietaryRestrictions.toString())

        //Change fragments depending on which option is selected on the bottom navigation
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {
            item ->

            var fragmentToShow: Fragment? = null
            when(item.itemId) {
                R.id.action_home ->{
                    fragmentToShow = homeFragment
                }
                R.id.action_pantry ->{
                    fragmentToShow = pantryFragment
                }
                R.id.action_profile ->{
                    fragmentToShow = profileFragment
                }
            }

            if (fragmentToShow != null){
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentToShow).commit()
            }

            // Return true to say we've handles the user interaction for this item
            true
        }
        // Set default selection
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.action_home
    }

    /**
     * A method that takes in a path to the json file to be opened. It is expected that the json file adheres to the Recipe structure
     * Creates and returns a list of Recipe objects based on the content of the jsonFile
     */
    fun createRecipeListFromJSONFile(jsonFilePath:String): List<Recipe> {

        //By using the Asset Manager, we open the file to get an InputStream which we then call bufferedReader() on. We then immediately use to BufferedReader
        // to call readText to get entire jsonFile as a String
        val jsonFileAsString =this.assets.open(jsonFilePath).bufferedReader().readText()
        val jsonArrayOfRecipes=JSONArray(jsonFileAsString)
        val recipesList= mutableListOf<Recipe>()
        //Iterate through the jsonArrayOfRecipes (it's not iterable so we need to use indices), making each one into a Recipe object, and than adding to the list
        for (index in 0 until jsonArrayOfRecipes.length())
        {
            try {
                recipesList.add(Recipe.fromJSONObject(jsonArrayOfRecipes.getJSONObject(index)))
            }
            catch (e:JSONException)
            {
                Log.e(TAG,"Failed to add recipe called ${jsonArrayOfRecipes.getJSONObject(index).getString("name")} Continuing on to next recipe",e)
                missedRecipeCount+=1
            }


        }
        //This list should not be modified, and hence we return by calling toList() which makes the object we return immutable
        return recipesList.toList()

    }

    fun createMapFromJSONFile(jsonFilePath: String): Map<String, *> {
        val jsonFileAsString = this.assets.open(jsonFilePath).bufferedReader().readText()
        return JSONObject(jsonFileAsString).toMap()
    }

    fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
        when (val value = this[it])
        {
            is JSONArray ->
            {
                val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                JSONObject(map).toMap().values.toList()
            }
            is JSONObject -> value.toMap()
            JSONObject.NULL -> null
            else            -> value
        }
    }

}