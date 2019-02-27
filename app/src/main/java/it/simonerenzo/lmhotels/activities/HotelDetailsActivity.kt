package it.simonerenzo.lmhotels.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.mikepenz.materialize.MaterializeBuilder
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import it.simonerenzo.lmhotels.R
import it.simonerenzo.lmhotels.net.models.Hotel
import kotlinx.android.synthetic.main.activity_hotel_details.*
import kotlinx.android.synthetic.main.details_hotel.*

class HotelDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_details)
        setSupportActionBar(hotelDetailsToolbar)

        MaterializeBuilder().withActivity(this).build()

        val config = SlidrConfig.Builder()
            .position(SlidrPosition.LEFT)
            .build()

        Slidr.attach(this, config)

        val hotel = intent.extras?.get("item") as Hotel

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = hotel.name
        }

        setupLayout(hotel)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    private fun setupLayout(hotel: Hotel) {
        Glide.with(this)
            .load(hotel.images?.get(0))
            .into(hotelDetailsHeader)

        hotelDetailsStars.rating = hotel.stars.toFloat()
        hotelUserRating.rating = hotel.userRating

        val hoursTemplate = resources.getString(R.string.text_details_checkhours_template)
        hotelDetailsCheckIn.text = HtmlCompat.fromHtml(
            String.format(hoursTemplate, hotel.checkIn.from, hotel.checkIn.to),
            HtmlCompat.FROM_HTML_MODE_COMPACT)
        hotelDetailsCheckOut.text = HtmlCompat.fromHtml(
            String.format(hoursTemplate, hotel.checkOut.from, hotel.checkOut.to),
            HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

}