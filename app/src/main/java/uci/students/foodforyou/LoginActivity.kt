package uci.students.foodforyou

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var etUsername:EditText
    private lateinit var etPassword:EditText
    private lateinit var btnLogin: Button
    private val TAG="LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername=findViewById<EditText>(R.id.etUsername)
        etPassword=findViewById<EditText>(R.id.etPassword)
        btnLogin=findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener { loginUser() }
        auth= Firebase.auth

        currentUser=auth.currentUser
        if (currentUser!=null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }



    }
    fun loginUser(){
        val name=etUsername.text.toString()
        val password=etPassword.text.toString()

        auth.signInWithEmailAndPassword(name,password).addOnCompleteListener(this)
        {task ->
            if (task.isSuccessful) {
                // Log the user in if successful
                Log.d(TAG, "signInWithEmail:success")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                //TODO Change screen to the main menu
            } else {
                // Display an error message if failed
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(baseContext, "Failed to sign in",
                    Toast.LENGTH_SHORT).show()

            }

        }

    }
}