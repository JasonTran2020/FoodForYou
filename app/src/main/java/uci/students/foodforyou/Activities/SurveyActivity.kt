package uci.students.foodforyou.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uci.students.foodforyou.Adapter.SurveyAdapter
import uci.students.foodforyou.R

class SurveyActivity : AppCompatActivity() {

    private val surveyItems= mutableListOf<String>()
    private lateinit var rvSurvey: RecyclerView
    private lateinit var  myAdapter: SurveyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)
        surveyItems.add("Breakfast")
        surveyItems.add("Lunch")
        surveyItems.add("Dinner")
        //Temp test for incorrect check marks due to poor recycling
        //for (x in "abcedfejkawgohfdg") surveyItems.add(x.toString())

        rvSurvey=findViewById(R.id.rvSurvey)
        myAdapter=SurveyAdapter(this,surveyItems)

        //Attach the right objects to the recycler view
        rvSurvey.adapter=myAdapter
        rvSurvey.layoutManager=LinearLayoutManager(this)


        myAdapter.notifyDataSetChanged()

    }
}