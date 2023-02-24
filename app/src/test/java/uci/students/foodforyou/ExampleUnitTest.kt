package uci.students.foodforyou

import android.util.Log
import com.google.gson.Gson

import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*
import uci.students.foodforyou.Models.Recipe

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun createRecipeWithGson()
    {
        var gsonInstance=Gson()
        val jsonObject= JSONObject("""{"name": "Kale Rice Bowl", "ingredients": ["olive oil or clarified butter", "kale", "cooked brown rice", "capers", "a poached egg", "a dollop of salted greek yogurt", "a big drizzle of good extra-virgin olive oil", "lot's of za'atar", "toasted sesame seeds"], "url": "http://www.101cookbooks.com/archives/kale-rice-bowl-recipe.html", "image": "http://www.101cookbooks.com/mt-static/images/food/kale_rice_bowl_recipe.jpg", "ts": {"{$}date": 1365276029884}, "cookTime": "PT5M", "source": "101cookbooks", "recipeYield": "Serves 2-3.", "datePublished": "2013-02-11", "prepTime": "PT5M", "description": "A quick lunchtime brown rice bowl with kale, capers, salted yogurt, za'atar, toasted sesame seeds - and a poached egg for good measure.", "id": 0, "unparsed_ingredients": "olive oil or clarified butter\n1 bunch of kale, destemmed, chopped/shredded\n~3 cups cooked brown rice\nTo serve: \n- capers, rinsed, dried, and pan-fried until blistered in butter\n- a poached egg\n- a dollop of salted greek yogurt\n- a big drizzle of good extra-virgin olive oil\n- lot's of za'atar\n- toasted sesame seeds", "dietary_restrictions": ["fish-free", "shellfish-free", "nut-free", "peanut-free", "soy-free", "vegan", "vegetarian", "pork-free", "pescatarian"], "cuisine_prediction": "greek"}""")
        //var testRecipe=gsonInstance.fromJson("""{"name": "Kale Rice Bowl", "ingredients": ["olive oil or clarified butter", "kale", "cooked brown rice", "capers", "a poached egg", "a dollop of salted greek yogurt", "a big drizzle of good extra-virgin olive oil", "lot's of za'atar", "toasted sesame seeds"], "url": "http://www.101cookbooks.com/archives/kale-rice-bowl-recipe.html", "image": "http://www.101cookbooks.com/mt-static/images/food/kale_rice_bowl_recipe.jpg", "ts": {"{$}date": 1365276029884}, "cookTime": "PT5M", "source": "101cookbooks", "recipeYield": "Serves 2-3.", "datePublished": "2013-02-11", "prepTime": "PT5M", "description": "A quick lunchtime brown rice bowl with kale, capers, salted yogurt, za'atar, toasted sesame seeds - and a poached egg for good measure.", "id": 0, "unparsed_ingredients": "olive oil or clarified butter\n1 bunch of kale, destemmed, chopped/shredded\n~3 cups cooked brown rice\nTo serve: \n- capers, rinsed, dried, and pan-fried until blistered in butter\n- a poached egg\n- a dollop of salted greek yogurt\n- a big drizzle of good extra-virgin olive oil\n- lot's of za'atar\n- toasted sesame seeds", "dietary_restrictions": ["fish-free", "shellfish-free", "nut-free", "peanut-free", "soy-free", "vegan", "vegetarian", "pork-free", "pescatarian"], "cuisine_prediction": "greek"}""",Recipe::class.java)
        //assert
        val testRecipe=Recipe.fromJSONObject(jsonObject)
        assertEquals(testRecipe.name,"Kale Rice Bowl")
        assertEquals(testRecipe.ingredients, mutableListOf("olive oil or clarified butter", "kale", "cooked brown rice", "capers", "a poached egg", "a dollop of salted greek yogurt", "a big drizzle of good extra-virgin olive oil", "lot's of za'atar", "toasted sesame seeds"))

    }
}