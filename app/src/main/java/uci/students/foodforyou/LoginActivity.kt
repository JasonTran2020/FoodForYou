package uci.students.foodforyou

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var etUsername:EditText
    private lateinit var etPassword:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername=findViewById<EditText>(R.id.etUsername)
        etPassword=findViewById<EditText>(R.id.etPassword)
        auth= Firebase.auth


    }
    fun loginUser(){
        var name=etUsername.text.toString()
        var password=etPassword.text.toString()

    }
}