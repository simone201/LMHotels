package it.simonerenzo.lmhotels.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.mikepenz.materialize.MaterializeBuilder
import it.simonerenzo.lmhotels.R
import it.simonerenzo.lmhotels.net.models.Hotel
import kotlinx.android.synthetic.main.activity_hotel_details.*

class HotelDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_details)
        setSupportActionBar(hotelDetailsToolbar)

        MaterializeBuilder().withActivity(this).build()

        val hotel = intent.extras?.get("item") as Hotel?

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = hotel?.name
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}