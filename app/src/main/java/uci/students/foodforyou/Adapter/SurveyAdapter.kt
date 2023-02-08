package uci.students.foodforyou.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import uci.students.foodforyou.R

/**
*Custom adapter that extends from RecyclerView.Adapter and is parametized by our customer ViewHolder class
 */
class SurveyAdapter(private val context: Context, private val surveyItems:List<String>): RecyclerView.Adapter<SurveyAdapter.ViewHolder>() {

    private val selectedItems= mutableSetOf<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_survey,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount()= surveyItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(surveyItems[position])
    }
    fun getSelectedItems()=selectedItems

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        //Technically it is a CheckBox, but I'll leave the suffix as btn since it is still a type of button
        private val btnSurveyCheck=itemView.findViewById<CheckBox>(R.id.btnSurveyCheck)

        fun bind(name:String){
            btnSurveyCheck.text = name
            //Due to this being used in a recycler view, it is necessary to set the checkbox every time, as we may be using a recycled ViewHolder
            //That was previously checked or unchecked, but is now being used for a different item. Basically the view has leftover modifications from previous
            //usage and we need to reset its state, well at least the state that we care about
            setCheckMark(name)


            //We want to track when the user selects an item by pressing on the button
            btnSurveyCheck.setOnClickListener {
                //  Basically when clicked on, do the opposite action (remove if in the set, add if not in the set)
                when (name){
                    in selectedItems ->selectedItems.remove(name)
                    !in selectedItems -> selectedItems.add(name)
                }
//                if (box_name in selectedItems)
//                {
//                    selectedItems.remove(name)
//                }
//                else{
//                    selectedItems.add(name)
//                }
            }
        }

        /**
         * Sets whether the checkbox is checked or not depending on whether the name is in the selectedItems set
         */
        private fun setCheckMark(name:String){
            when (name){
                in selectedItems ->btnSurveyCheck.isChecked=true
                !in selectedItems -> btnSurveyCheck.isChecked=false
            }
        }
    }


}