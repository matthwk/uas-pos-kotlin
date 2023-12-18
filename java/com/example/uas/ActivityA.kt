package com.example.uas
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.uas.databinding.ActivityABinding

class ActivityA : AppCompatActivity() {

    private lateinit var binding: ActivityABinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityABinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            // Initially load the FragmentLogin
            loadFragment(FragmentLogin())
        }
    }

    // Function to load the specified fragment into the fragment container
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Function to navigate to the Create Account fragment
    fun navigateToCreateAccount() {
        loadFragment(FragmentCreateLogin())
    }

    // Function to navigate back to the Login fragment
    fun navigateToLogin() {
        loadFragment(FragmentLogin())
    }
}
