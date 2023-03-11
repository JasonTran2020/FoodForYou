package uci.students.foodforyou.Activities.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uci.students.foodforyou.Activities.LoginActivity
import uci.students.foodforyou.Activities.SurveyActivity

import uci.students.foodforyou.R

class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set on click listeners
        auth = Firebase.auth // sign into Firebase
        database = Firebase.database.reference // get database

        updateInfo() // populate setting with user data

        val algyBtn = view.findViewById<Button>(R.id.allergyBtn)

        algyBtn.setOnClickListener{
            redirectSurvey()
        }
        val logoutBtn=view.findViewById<Button>(R.id.logOutBtn)
        auth= Firebase.auth
        logoutBtn.setOnClickListener {
            auth.signOut()
            activity?.let{
                val intent = Intent(it, LoginActivity::class.java)
                it.startActivity(intent)
            }
        }
    }

    private fun updateInfo(){
    // function updates setting text with user data
        val user = Firebase.auth.currentUser    // check if user is signed in
        // took this from PantryFragment
        user?.let{ db ->
            database.child( "food_users")
                .child(db.uid)
                .get().addOnCompleteListener{
                // if there is data, parse
                if(it.isSuccessful && it.result.value != null) {
                    // initialize to kotlin map
                    val userInfo = it.result.value as Map<*, *>
                    if(userInfo.isNotEmpty()){
                        updateText(userInfo)
                    }
                }
                // TODO? : implement onCancelled and onDataChange if needed?
            }
        }
    }

    private fun updateText(info: Map<*,*>){
    // updates: first_name, last_name, and email
        for(key in info.keys) { // iterate through each key
            when(key){ // switch case to match First/Last name and email
                // then replace static text with user info
                getString(R.string.DatabaseFirstName) -> {
                    view?.findViewById<TextView>(R.id.profFirstName)
                        ?.text = info[key].toString()
                }
                getString(R.string.DatabaseLastName) -> {
                    view?.findViewById<TextView>(R.id.profLastName)
                        ?.text = info[key].toString()
                }
                getString(R.string.DatabaseEmail) -> {
                    view?.findViewById<TextView>(R.id.profEmail)
                        ?.text = info[key].toString()
                }
            }
        }
    }
    private fun redirectSurvey(){
        // https://stackoverflow.com/questions/53355786/kotlin-open-new-activity-inside-of-a-fragment

        activity?.let{
            val intent = Intent (it, SurveyActivity::class.java)
            intent.putExtra("editAlgy", true)
            it.startActivity(intent)
        }
    }
}