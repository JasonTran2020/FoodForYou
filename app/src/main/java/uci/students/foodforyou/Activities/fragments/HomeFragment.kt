package uci.students.foodforyou.Activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uci.students.foodforyou.R

class HomeFragment : Fragment() {

    lateinit var postRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set on click listeners

        postRecyclerView = view.findViewById(R.id.postRecyclerView)
        //Next steps
        // Done: Create layout for each row in list (item_recipe.xml)
        // Create data source for each row (make Recipe class)
        // Bridge data and layout
        // Set adapter and layout manager on recyclerview
    }
}