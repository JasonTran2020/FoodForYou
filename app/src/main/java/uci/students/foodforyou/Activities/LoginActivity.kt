package uci.students.foodforyou.Activities

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
import uci.students.foodforyou.R


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var etUsername:EditText
    private lateinit var etPassword:EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private val TAG="LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername=findViewById<EditText>(R.id.etUsername)
        etPassword=findViewById<EditText>(R.id.etPassword)
        btnLogin=findViewById<Button>(R.id.btnLogin)
        btnSignUp=findViewById<Button>(R.id.btnSignUp)

        btnLogin.setOnClickListener { loginUser() }
        btnSignUp.setOnClickListener { goToSignUpScreen() }
        auth= Firebase.auth

        currentUser=auth.currentUser
        if (currentUser!=null){
            //Head to the next activity and call finish() so you can't hit the back button
            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }



    }
    fun loginUser(){
        val name=etUsername.text.toString()
        val password=etPassword.text.toString()
        if (name.isEmpty() or password.isEmpty())
        {
            Toast.makeText(baseContext,"Please please fill in your information",Toast.LENGTH_SHORT).show()
        }
        else
        {
            auth.signInWithEmailAndPassword(name,password).addOnCompleteListener(this)
            {task ->
                if (task.isSuccessful) {
                    // Log the user in if successful
                    Log.d(TAG, "signInWithEmail:success")
                    val intent = Intent(this, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    // Display an error message if failed
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Failed to sign in",
                        Toast.LENGTH_SHORT).show()

                }

            }
        }


    }
    fun goToSignUpScreen()
    {
        val intent=Intent(this,SignUpActivity::class.java)
        finish()
        startActivity(intent)
    }
}