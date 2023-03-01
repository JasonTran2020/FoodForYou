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
    val recommendedRecipes= mutableListOf<Recipe>()
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
        // Set click listeners for each recipe. Liekly done in the adapter
        // Find a default image for food, because there are way too many dead images
    }
}