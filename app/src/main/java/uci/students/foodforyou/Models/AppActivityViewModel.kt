package uci.students.foodforyou.Models

import androidx.lifecycle.ViewModel

class AppActivityViewModel: ViewModel() {

    lateinit var breakfastRecipes:List<Recipe>
    lateinit var lunchRecipes:List<Recipe>
    lateinit var dinnerRecipes:List<Recipe>

    fun setRecipes(bRecipes:List<Recipe>, lRecipes:List<Recipe>,dRecipes:List<Recipe>) {
        breakfastRecipes=bRecipes
        lunchRecipes=lRecipes
        dinnerRecipes=dRecipes
    }
}