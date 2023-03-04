package uci.students.foodforyou.Activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import uci.students.foodforyou.Models.Recipe
import uci.students.foodforyou.R

class PostDisplayDialogFragment: DialogFragment() {
    private lateinit var btnYes:Button
    private lateinit var btnNoTime:Button
    private lateinit var btnNo:Button
    private lateinit var tvRecipeQuestion:TextView
    private lateinit var recipe:Recipe

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Create the dialogFragment with the custom layout
        return inflater.inflate(R.layout.dialog_fragment_postdisplay,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //The recipe is stored inside arguments (getArguments), so we need to make sure it is not null, otherwise theres a problem

        recipe= requireArguments().getParcelable<Recipe>("recipe")!!
        btnYes=view.findViewById(R.id.btnYes)
        btnNoTime=view.findViewById(R.id.btnNoTime)
        btnNo=view.findViewById(R.id.btnNo)
        tvRecipeQuestion=view.findViewById(R.id.tvRecipeQuestion)

        tvRecipeQuestion.text="Did you like ${recipe.name}}"

        btnYes.setOnClickListener {
            //TODO Adjust points correctly to the corresponding cuisine (increase points)
            this.dismiss()
        }

        btnNoTime.setOnClickListener {
            //Do nothing with the point system
            this.dismiss()
        }

        btnNo.setOnClickListener {
            //TODO Adjust points correctly to the corresponding cuisine (Decrease points)
            this.dismiss()
        }
    }
}