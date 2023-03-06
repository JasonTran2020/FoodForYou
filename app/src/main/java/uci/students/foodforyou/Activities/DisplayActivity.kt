package uci.students.foodforyou.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uci.students.foodforyou.R
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.ListView
import android.widget.ImageView
import com.bumptech.glide.Glide
import android.content.Intent
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.core.os.BuildCompat
import uci.students.foodforyou.Models.Recipe


class DisplayActivity : AppCompatActivity() {
    private var ingredients = mutableListOf<String>()
    private var left = mutableListOf<String>()
    private lateinit var cookButt: Button
    private lateinit var inDis: ListView
    private lateinit var recName: TextView
    private lateinit var recImg: ImageView
    private lateinit var ingAdapter: ArrayAdapter<String>
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var name:String
    private lateinit var ings:List<String>
    private lateinit var instruct:String
    private lateinit var image:String
    private lateinit var recipe: Recipe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        //This should be the primary way of getting data from the intent now, but the old code (else block) is still here to prevent crashes when
        //Launching this activity with the test button
        if (intent.hasExtra("ParcelableRecipe"))
        {
            recipe= intent.getParcelableExtra<Recipe>("ParcelableRecipe")!!
            name= recipe!!.name
            ings = recipe.ingredients
            instruct= recipe.webpageUrl
            image= recipe.imageUrl
        }
        else{
            name = intent.getStringExtra("name").toString()
            ings = intent.getStringArrayListExtra("ingredients") as List<String>
            instruct= intent.getStringExtra("url").toString()
            image= intent.getStringExtra("image").toString()
        }

        database=Firebase.database.reference
        auth= Firebase.auth
        cookButt = findViewById(R.id.cookBut)
        inDis = findViewById(R.id.misIng)
        recName = findViewById(R.id.rname)
        recImg = findViewById(R.id.rimage)
        recName.setText(name)
        for (ing in ings)
        {
            ingredients.add(ing.capitalize())
        }
        findMisIngs()
        Glide.with(this).load(image).centerCrop().error("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdrAYfsGt5sbUrmjHD_fLDuymf2GcjDji78ed2GOg&s").into(recImg)
        ingAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredients)
        inDis.adapter = ingAdapter
        cookButt=findViewById<Button>(R.id.cookBut)
        cookButt.setOnClickListener{
            intent =Intent(Intent.ACTION_VIEW, Uri.parse(instruct))
            startActivity(intent)
            consumeIng()
            ingredients.clear()
            for (ing in ings)
            {
                ingredients.add(ing.capitalize())
            }
            ingAdapter.notifyDataSetChanged()
        }

        //Code for saving the Recipe when hitting the back button. Yea we could have just saved the recipe when launching this activity, but this is more interesting
        onBackPressedDispatcher.addCallback(this ,object:OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed() {
                val intent=Intent()
                intent.putExtra("recipe",recipe)
                setResult(RESULT_OK,intent)
                finish()
            }

        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
    fun findMisIngs()
    {
        val user = Firebase.auth.currentUser
        user?.let {
            database.child("user_pantry").child(it.uid).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.value != null) {
                        val products = it.result.value as List<String>
                        if (products.isEmpty() == false) {
                            for (item in products) {
                                val ingIterator = ingredients.iterator()
                                while (ingIterator.hasNext())
                                {
                                    val item2 = ingIterator.next()
                                    if (item2.contains(item,true))
                                    {
                                        ingIterator.remove()
                                    }
                                }
                            }
                            ingAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
    fun consumeIng(){
        left.clear()
        val user = Firebase.auth.currentUser
        user?.let {
            database.child("user_pantry").child(it.uid).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.value != null) {
                        val products = it.result.value as List<String>
                        if (products.isEmpty() == false) {
                            for (item in products) {
                                left.add(item)
                            }
                            for (item in products){
                                val ingIterator = ingredients.iterator()
                                while (ingIterator.hasNext())
                                {
                                    val item2 = ingIterator.next()
                                    if (item2.contains(item,true))
                                    {
                                        left.remove(item)
                                    }
                                }
                            }
                        }
                        database.child("user_pantry").child(user.uid).setValue(left)
                    }
                }
            }
        }
    }
}