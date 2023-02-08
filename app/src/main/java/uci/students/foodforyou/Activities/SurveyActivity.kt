package uci.students.foodforyou.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uci.students.foodforyou.Adapter.SurveyAdapter
import uci.students.foodforyou.R

class SurveyActivity : AppCompatActivity() {

    private val surveyItems= mutableListOf<String>()
    private lateinit var rvSurvey: RecyclerView
    private lateinit var  myAdapter: SurveyAdapter
    private lateinit var btnNextSurvey:Button
    private val auth=Firebase.auth
    private  val database=Firebase.database.reference
    private val selectedMeals= mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)
        surveyItems.add("Breakfast")
        surveyItems.add("Lunch")
        surveyItems.add("Dinner")
        //Temp test for incorrect check marks due to poor recycling
        //for (x in "abcedfejkawgohfdg") surveyItems.add(x.toString())
        btnNextSurvey=findViewById<Button>(R.id.btnNextSurvey)
        btnNextSurvey.setOnClickListener { onPressNextSurveyListener() }

        rvSurvey=findViewById(R.id.rvSurvey)
        myAdapter=SurveyAdapter(this,surveyItems)

        //Attach the right objects to the recycler view
        rvSurvey.adapter=myAdapter
        rvSurvey.layoutManager=LinearLayoutManager(this)


        myAdapter.notifyDataSetChanged()

    }
    private fun onPressNextSurveyListener()
    {
        saveSurveyToDB()

    }
    private fun saveSurveyToDB()
    {
        val selectedItems=myAdapter.getSelectedItems()
        //auth.
        auth.currentUser?.let {
            database.child(getString(R.string.DatabaseUser)).child(it.uid).child("TempSurvey").setValue(selectedItems.toList()).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(baseContext,"Saved part of the survey check database",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(baseContext,"Failed to save survey",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


}