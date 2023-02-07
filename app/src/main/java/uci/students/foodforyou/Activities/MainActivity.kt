package uci.students.foodforyou.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uci.students.foodforyou.R

class MainActivity : AppCompatActivity() {
    private val TAG="MainActivity"
    private lateinit var saveBtn:Button
    private lateinit var textToSave:EditText
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        saveBtn=findViewById(R.id.btnTestSave)
        textToSave=findViewById(R.id.editTextTextMultiLine)
        database=Firebase.database.reference

        saveBtn.setOnClickListener { saveData() }



        val logoutBtn=findViewById<Button>(R.id.btnLogOutTemp)
        auth= Firebase.auth
        logoutBtn.setOnClickListener {
            auth.signOut()
            Log.d(TAG,"Logging user out and return to login menu")
            intent = Intent(this, LoginActivity::class.java)
            finish()
            startActivity(intent)
        }

        val btnTestSurvey=findViewById<Button>(R.id.btnTestSurvey)
        btnTestSurvey.setOnClickListener {
            intent =Intent(this,SurveyActivity::class.java)
            startActivity(intent)
        }
    }

    fun saveData()
    {
        val input=textToSave.text.toString()
        if (input.isEmpty()==false)
        {
            auth.currentUser?.let {
                database.child("food_users").child(it.uid).child("random").setValue(input).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(baseContext,"Saved apparently. Check the database",Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext,"Failed to save",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}