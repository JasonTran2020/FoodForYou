package uci.students.foodforyou.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import uci.students.foodforyou.Activities.fragments.HomeFragment
import uci.students.foodforyou.Activities.fragments.PantryFragment
import uci.students.foodforyou.Activities.fragments.ProfileFragment
import uci.students.foodforyou.R

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        val fragmentManager: FragmentManager = supportFragmentManager

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {
            item ->

            var fragmentToShow: Fragment? = null
            when(item.itemId) {
                R.id.action_home ->{
                    fragmentToShow = HomeFragment()
                }
                R.id.action_pantry ->{
                    fragmentToShow = PantryFragment()
                }
                R.id.action_profile ->{
                    fragmentToShow = ProfileFragment()
                }
            }

            if (fragmentToShow != null){
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentToShow).commit()
            }

            // Return true to say we've handles the user interaction for this item
            true
        }
        // Set default selection
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.action_home
    }

}