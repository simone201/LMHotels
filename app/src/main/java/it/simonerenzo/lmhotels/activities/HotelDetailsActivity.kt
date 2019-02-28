package it.simonerenzo.lmhotels.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.OnClickListener
import com.mikepenz.itemanimators.AlphaInAnimator
import com.mikepenz.materialize.MaterializeBuilder
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import it.simonerenzo.lmhotels.R
import it.simonerenzo.lmhotels.net.models.Hotel
import it.simonerenzo.lmhotels.ui.items.GalleryItem
import kotlinx.android.synthetic.main.activity_hotel_details.*
import kotlinx.android.synthetic.main.card_details_gallery.*
import kotlinx.android.synthetic.main.card_details_general.*
import org.jetbrains.anko.sdk27.coroutines.onClick


class HotelDetailsActivity : AppCompatActivity(), OnClickListener<GalleryItem> {

    private val itemAdapter = ItemAdapter<GalleryItem>()
    private val listAdapter = FastAdapter.with<GalleryItem, ItemAdapter<GalleryItem>>(itemAdapter)

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
        setupGallery(hotel.images ?: emptyList())
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    override fun onClick(v: View?,
                         adapter: IAdapter<GalleryItem>,
                         item: GalleryItem,
                         position: Int): Boolean {
        val popup = Dialog(this, android.R.style.Theme_Translucent)
        popup.requestWindowFeature(Window.FEATURE_NO_TITLE)
        popup.setContentView(R.layout.dialog_gallery_open)
        popup.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        popup.setCancelable(true)
        popup.create()

        val image = popup.findViewById<AppCompatImageView>(R.id.galleryZoomImage)
        Glide.with(this)
            .load(item.imageUri)
            .placeholder(ColorDrawable(Color.DKGRAY))
            .into(image)

        popup.findViewById<LinearLayoutCompat>(R.id.galleryZoomItem).onClick {
            popup.dismiss()
        }

        popup.show()
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

    private fun setupGallery(images: List<String>) {
        if (images.isNullOrEmpty() || images.size == 1) {
            hotelDetailsGalleryCard.visibility = View.GONE
            return
        }

        listAdapter.withOnClickListener(this)

        hotelGallery.addItemDecoration(LayoutMarginDecoration(1, 32))
        hotelGallery.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        hotelGallery.itemAnimator = AlphaInAnimator()
        hotelGallery.adapter = listAdapter

        itemAdapter.clear()
        itemAdapter.add(
            images.map { GalleryItem(it) }
        )

        if (itemAdapter.itemList.isEmpty) {
            hotelGallery.visibility = View.GONE
        }
    }

}