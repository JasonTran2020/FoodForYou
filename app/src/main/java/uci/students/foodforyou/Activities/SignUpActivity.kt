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
import com.google.firebase.ktx.Firebase
import uci.students.foodforyou.R

class SignUpActivity : AppCompatActivity() {
    val TAG="SignUpActivity"
    private lateinit var auth: FirebaseAuth

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

    private lateinit var btnCreateAccount:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        auth= Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etFirstName=findViewById(R.id.etFirstName)
        etLastName=findViewById(R.id.etLastName)

        etEmail=findViewById(R.id.etSignUpEmail)
        etPassword=findViewById(R.id.etSignUpPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)

        btnCreateAccount=findViewById(R.id.btnCreateAccount)
        btnCreateAccount.setOnClickListener { createNewUser() }
    }

    fun createNewUser()
    {
        val email=etEmail.text.toString()
        val password=etPassword.text.toString()
        val confirmPassword=etConfirmPassword.text.toString()

        if (email.isEmpty() or password.isEmpty() or confirmPassword.isEmpty())
        {
            Toast.makeText(baseContext,"Please fill in all fields",Toast.LENGTH_SHORT).show()
        }
        else if (password!=confirmPassword)
        {
            Toast.makeText(baseContext,"Passwords do not match",Toast.LENGTH_SHORT).show()
        }
        else{
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    Log.d(TAG,"Created new user")
                    val loginIntent= Intent(this,LoginActivity::class.java)
                    Toast.makeText(baseContext,"Sign up success! Please login",Toast.LENGTH_SHORT).show()
                    startActivity(loginIntent)
                }
                else
                {
                    Toast.makeText(baseContext,"Failed to sign up",Toast.LENGTH_SHORT).show()
                }


            }
        }
    }
}