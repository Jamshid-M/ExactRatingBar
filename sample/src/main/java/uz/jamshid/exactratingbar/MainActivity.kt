package uz.jamshid.exactratingbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import uz.jamshid.library.ExactRatingBar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rate.onRatingBarChanged = object : ExactRatingBar.OnRatingBarChanged{
            override fun newRate(rate: Float) {
                Toast.makeText(this@MainActivity, rate.toString(), Toast.LENGTH_LONG).show()
                Log.d("MYTAG", "interface$rate")
            }
        }

        rate.onRateChanged = {
            Log.d("MYTAG", "lambda$it")
        }
    }
}
