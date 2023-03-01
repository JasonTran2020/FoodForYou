package uci.students.foodforyou.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
@Parcelize
data class Recipe (val name:String,val ingredients:List<String>,val webpageUrl:String,val imageUrl:String, val dietaryCompliances:List<String>,val cuisine:String, val description:String, val missingIngredient: MutableList<String> = mutableListOf<String>()) :
    Parcelable {

    companion object Parser
    {
        fun fromJSONObject(jsonObject: JSONObject):Recipe
        {
            val ingredients= mutableListOf<String>()
            val dietaryCompliances=mutableListOf<String>()
            val jsonIngredient=jsonObject.getJSONArray("ingredients")
            for (ingredient_index in 0 until jsonIngredient.length())
            {
                ingredients.add(jsonIngredient.getString(ingredient_index))
            }

            val jsonDietary=jsonObject.getJSONArray("dietary_restrictions")
            for (dietary_index in 0 until jsonDietary.length())
            {
                dietaryCompliances.add(jsonDietary.getString(dietary_index))
            }
            return Recipe(jsonObject.getString("name"),ingredients,jsonObject.getString("url"),jsonObject.getString("image"),dietaryCompliances,jsonObject.getString("cuisine_prediction"),jsonObject.getString("description"))
        }
    }
}