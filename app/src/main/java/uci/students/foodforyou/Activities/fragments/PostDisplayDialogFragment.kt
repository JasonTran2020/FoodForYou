package uci.students.foodforyou.Activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import uci.students.foodforyou.Models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uci.students.foodforyou.R

class PostDisplayDialogFragment: DialogFragment() {
    private lateinit var btnYes:Button
    private lateinit var btnNoTime:Button
    private lateinit var btnNo:Button
    private lateinit var tvRecipeQuestion:TextView
    private lateinit var recipe:Recipe
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

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
        database=Firebase.database.reference
        auth= Firebase.auth
        recipe= requireArguments().getParcelable<Recipe>("recipe")!!
        btnYes=view.findViewById(R.id.btnYes)
        btnNoTime=view.findViewById(R.id.btnNoTime)
        btnNo=view.findViewById(R.id.btnNo)
        tvRecipeQuestion=view.findViewById(R.id.tvRecipeQuestion)
        val mealType = recipe.cuisine
        tvRecipeQuestion.text="Did you like ${recipe.name}}"

        btnYes.setOnClickListener {
            val user = Firebase.auth.currentUser
            user?.let {
                var score = database.child("user_personal_model").child(it.uid).child(mealType.capitalize())
                score.setValue(ServerValue.increment(1.0))
            }
            this.dismiss()
        }

        btnNoTime.setOnClickListener {
            //Do nothing with the point system
            this.dismiss()
        }

        btnNo.setOnClickListener {
            val user = Firebase.auth.currentUser
            user?.let {
                var score = database.child("user_personal_model").child(it.uid).child(mealType.capitalize())
                //Do not allow the score to dip down below 0
                score.get().addOnCompleteListener {
                    if (it.isSuccessful && it.result.value as Long  >= 1)
                    {
                        score.setValue(ServerValue.increment(-1.0))
                    }
                }

            }
            this.dismiss()
        }
    }
}