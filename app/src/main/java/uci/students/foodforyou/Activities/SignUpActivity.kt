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
import com.google.firebase.database.ktx.database
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
        val firstName=etFirstName.text.toString()
        val lastName=etLastName.text.toString()
        val email=etEmail.text.toString()
        val password=etPassword.text.toString()
        val confirmPassword=etConfirmPassword.text.toString()

        if (email.isEmpty() or password.isEmpty() or confirmPassword.isEmpty() or firstName.isEmpty() or lastName.isEmpty())
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
                    this.createNewUserInDatabase(firstName,lastName,email)
                    this.createNewPersonalModelInDatabase()
                    Log.d(TAG,"Created new user")
                    val loginIntent= Intent(this,LoginActivity::class.java)
                    Toast.makeText(baseContext,"Sign up success! Please login",Toast.LENGTH_SHORT).show()
                    finish()
                    startActivity(loginIntent)
                }
                else
                {
                    //Log.d(TAG,it.result.toString())
                    Toast.makeText(baseContext,"Failed to sign up",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Firebase does not allow us to edit the authenticated user, so user related information is stored in the firebase databse
     * We also need to make a separate user inside of their, though there is no password
     */
    fun createNewUserInDatabase( firstName: String, lastName: String, email: String)
    {
        val database=Firebase.database.reference
        auth.currentUser?.let { user ->
            database.child(getString(R.string.DatabaseUser)).child(user.uid).child(getString(R.string.DatabaseFirstName)).setValue(firstName)
            database.child(getString(R.string.DatabaseUser)).child(user.uid).child(getString(R.string.DatabaseLastName)).setValue(lastName)
            database.child(getString(R.string.DatabaseUser)).child(user.uid).child(getString(R.string.DatabaseEmail)).setValue(email).addOnCompleteListener {
                if (it.isSuccessful)
                {
                    Log.d(TAG,"Created user in database as well")

                }
                else
                {
                    Log.e(TAG, "Failed to create user in database"+it.exception.toString())
                }
            }
        }
    }

    /**
     * Save a map representing the personal model about a user's likes and dislikes in terms of cuisine
     * Each cuisine is initially assigned a value of 0
     */
    fun createNewPersonalModelInDatabase()
    {
        //Initialize a map with values as zeros and keys as
        val cuisineMap=getString(R.string.CuisineTypes).split(",").associateBy({it},{0.0})

        val database=Firebase.database.reference
        auth.currentUser?.let { user->
            database.child(getString(R.string.DatabasePersonalModel)).child(user.uid).setValue(cuisineMap).addOnCompleteListener {
                if (it.isSuccessful)
                {
                    Log.d(TAG,"Created a personal model for the user")
                }
                else{
                    Log.e(TAG, "Failed to save initial personal model to database"+it.exception.toString())
                }
            }
        }
    }
}