package uci.students.foodforyou

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val TAG="MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logoutBtn=findViewById<Button>(R.id.btnLogOutTemp)
        val auth= Firebase.auth
        logoutBtn.setOnClickListener {
            auth.signOut()
            Log.d(TAG,"Logging user out and return to login menu")
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}