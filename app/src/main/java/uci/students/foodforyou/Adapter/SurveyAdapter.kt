package uci.students.foodforyou.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import uci.students.foodforyou.R

class SurveyAdapter(private val context: Context, private val surveyItems:List<String>): RecyclerView.Adapter<SurveyAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_survey,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount()= surveyItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(surveyItems[position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        private val btnSurveyCheck=itemView.findViewById<Button>(R.id.btnSurveyCheck)

        fun bind(name:String){
            btnSurveyCheck.text = name
        }
    }


}