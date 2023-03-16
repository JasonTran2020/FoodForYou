package uci.students.foodforyou.Activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import uci.students.foodforyou.R


class PantryFragment : Fragment() {
    private lateinit var ingInput:EditText
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var listDisplay: ListView
    private lateinit var panAdapter: ArrayAdapter<String>
    private var panIng= mutableListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pantry, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set on click listeners and intial vars
        database=Firebase.database.reference
        auth= Firebase.auth
        listDisplay = view.findViewById<ListView>(R.id.pantryDisplay)
        initializeList()
        panAdapter = ArrayAdapter(view.context, android.R.layout.simple_list_item_1, panIng)
        listDisplay.adapter = panAdapter
        //Button to get the string ingredient to add
        view.findViewById<Button>(R.id.addItemButt).setOnClickListener{
            ingInput = view.findViewById(R.id.ingToAddDel)
            val ingredient = ingInput.getText().toString()
            if (ingredient.isEmpty()==false) {
                ingInput.getText().clear()
                panIng.add(ingredient)
                updateList()
            }
        }

        //Set up to remove an ingredient when holding down on the item
        listDisplay.setOnItemLongClickListener { adapterView, view, i, l ->
            val removedIngredient=panIng[i]
            panAdapter.remove(panIng[i])
            //panIng.removeAt(i)
            Toast.makeText(view.context,"$removedIngredient removed",Toast.LENGTH_SHORT).show()
            updateList()
            true
        }
    }

    fun initializeList()
    {
        val user = Firebase.auth.currentUser
        user?.let {
            database.child("user_pantry").child(it.uid).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    panIng.clear()
                    if (it.result.value != null) {
                        val products = it.result.value as List<String>
                        if (products.isEmpty() == false) {
                            //Clear panIng first as it made already have content from switching off this fragment and back
                            panIng.clear()
                            for (item in products) {
                                panIng.add(item)
                            }
                        }
                    }
                    panAdapter.notifyDataSetChanged()
                }
            }
        }
    }
    fun updateList()
    {
        val user = Firebase.auth.currentUser
        user?.let {
            database.child("user_pantry").child(it.uid).setValue(panIng)
        }
        panAdapter.notifyDataSetChanged()
    }

}