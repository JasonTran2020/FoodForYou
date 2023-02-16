package uci.students.foodforyou.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uci.students.foodforyou.Adapter.SurveyAdapter
import uci.students.foodforyou.R
import kotlin.properties.Delegates

class SurveyActivity : AppCompatActivity() {
    val TAG="SurveyActivity"
    var MAXPAGES by Delegates.notNull<Int>()

    private val surveyItems= mutableListOf<String>()
    private lateinit var rvSurvey: RecyclerView
    private lateinit var  myAdapter: SurveyAdapter
    private lateinit var btnNextSurvey:Button
    private val auth=Firebase.auth
    private  val database=Firebase.database.reference
    private lateinit var surveyPageContent: Map<Int,List<String>>
    private lateinit var surveyPageNames: Map<Int,String>
    private val selectedMeals= mutableSetOf<String>()
    private var currentSurveyPage = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)
        // Map out of the page content by splitting the string by commas and saving it as a list in the value. The key is an int corresponding to the page
        // Like python, sorted creates a new list, although in kotlin, sorted is a method of a list as oppose to a function where you pass an iterable into it
        surveyPageContent=mapOf<Int,List<String>>(0 to getString(R.string.MealTypes).split(","),1 to getString(R.string.AllergyTypes).split(",").sorted(),2 to getString(R.string.CuisineTypes).split(",").sorted())
        surveyPageNames=mapOf<Int,String>(0 to getString(R.string.DatabaseUserSurveyPreferredMeals),1 to getString(R.string.DatabaseUserSurveyAllergy),2 to getString(R.string.DatabaseUserSurveyCuisine))
        MAXPAGES=surveyPageContent.size


        //Initially load the first page of the survey in surveyItems. ? means that the let statement will only run if surveyPageContent[currentSurveyPage] is non-null
        //though this should never be the case
        surveyPageContent[currentSurveyPage]?.let { surveyItems.addAll(0, it) }


        //Temp test for incorrect check marks due to poor recycling
        //for (x in "abcedfejkawgohfdg") surveyItems.add(x.toString())
        btnNextSurvey=findViewById<Button>(R.id.btnNextSurvey)
        btnNextSurvey.setOnClickListener { onPressNextSurveyListener() }

        rvSurvey=findViewById(R.id.rvSurvey)
        myAdapter=SurveyAdapter(this,surveyItems)

        //Attach the right objects to the recycler view. Using a GridLayoutManager
        rvSurvey.adapter=myAdapter
        rvSurvey.layoutManager=GridLayoutManager(this,2)


        myAdapter.notifyDataSetChanged()

    }

    /**
     * Saves whatever is selected to Firebase, and the updates the items on the recyclerView to have
     * the next part of survey (from preferred meal times to preferred cuisine to dietary restrictions)
     */
    private fun onPressNextSurveyListener():Unit
    {
        // Save whatever is selected on the recyclerView to Firebase
        saveSurveyToDB(currentSurveyPage)
        //Increment the page and wipe out current content on in surveyItems.
        // Take not that modifying surveyItems without notifying the adapter can cause some undefined behavior, which is specifically why surveyItems.clear() is in loadPage
        currentSurveyPage+=1
        if (currentSurveyPage<MAXPAGES) {
            loadPage(currentSurveyPage)
        }
        else{
            //We do not start another activity which would go on the back stack, as this would mean the user would see the main acitvity twice when they hit the back button
            //Once they've completed the survey
            finish()

        }
    }
    private fun saveSurveyToDB(pageNum:Int): Unit
    {
        val selectedItems=myAdapter.getSelectedItems()
        //auth.
        val currentPageTitle=surveyPageNames[pageNum]
        //Null checking and implcitly casting to string
        if (currentPageTitle !is String)
        {
            Toast.makeText(baseContext,"Failed to save survey. Current Page Title is null",Toast.LENGTH_SHORT).show()
            return
        }
        auth.currentUser?.let {
            //If we don't save SOMETHING then that node in the Json tree won't exist. Whether if that's good or not, I don't know so for we're gonna save "Nothing" if selectedItems is empty
            if (selectedItems.isEmpty())
            {
                database.child(getString(R.string.DatabaseUserSurvey)).child(it.uid).child(currentPageTitle).setValue(listOf("Nothing")).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(baseContext,"Saved Nothing to database",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(baseContext,"Failed to save survey",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                database.child(getString(R.string.DatabaseUserSurvey)).child(it.uid).child(currentPageTitle).setValue(selectedItems.toList()).addOnCompleteListener {
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

    /**
     * Updates the recyclerView and adapter by adding new elements to surveyItems and notifying the dataset has changed
     */
    private fun loadPage(pageNum:Int):Unit
    {
        surveyItems.clear()
        surveyPageContent[pageNum]?.let { surveyItems.addAll(0, it) }
        myAdapter.clearSelectedItems()
        myAdapter.notifyDataSetChanged()
    }



}