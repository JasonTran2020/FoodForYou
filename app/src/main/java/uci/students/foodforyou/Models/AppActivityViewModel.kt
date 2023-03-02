package uci.students.foodforyou.Models

import androidx.lifecycle.ViewModel

class AppActivityViewModel: ViewModel() {

    lateinit var breakfastRecipes:List<Recipe>
    lateinit var lunchRecipes:List<Recipe>
    lateinit var dinnerRecipes:List<Recipe>
    lateinit var breakfastDietaryRestrictions:Map<String, *>
    lateinit var lunchDietaryRestrictions:Map<String, *>
    lateinit var dinnerDietaryRestrictions:Map<String, *>
    lateinit var ingredientsToStemmed:Map<String, *>

    fun setRecipes(bRecipes:List<Recipe>, lRecipes:List<Recipe>,dRecipes:List<Recipe>) {
        breakfastRecipes=bRecipes
        lunchRecipes=lRecipes
        dinnerRecipes=dRecipes
    }

    fun setIngredientsToStemmedIngredients(ingredientToStemmedIngredient: Map<String, *>) {
        ingredientsToStemmed = ingredientToStemmedIngredient
    }
    fun setDietaryRestrictions(bDietaryRestrictions:Map<String, *>, lDietaryRestrictions:Map<String, *>, dDietaryRestrictions:Map<String, *>) {
        breakfastDietaryRestrictions = bDietaryRestrictions
        lunchDietaryRestrictions = lDietaryRestrictions
        dinnerDietaryRestrictions = dDietaryRestrictions
    }
}