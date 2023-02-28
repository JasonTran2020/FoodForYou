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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)
        val name:String = intent.getStringExtra("name").toString()
        val ings:List<String> = intent.getStringArrayListExtra("ingredients") as List<String>
        val instruct:String = intent.getStringExtra("url").toString()
        val image:String = intent.getStringExtra("image").toString()
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
        Glide.with(this).load(image).centerCrop().into(recImg)
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