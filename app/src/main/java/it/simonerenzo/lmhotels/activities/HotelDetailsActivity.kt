package it.simonerenzo.lmhotels.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import cn.nekocode.badge.BadgeDrawable
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.view.RxView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.OnClickListener
import com.mikepenz.itemanimators.AlphaInAnimator
import com.mikepenz.materialize.MaterializeBuilder
import com.mikepenz.materialize.color.Material
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import it.simonerenzo.lmhotels.R
import it.simonerenzo.lmhotels.net.models.Contact
import it.simonerenzo.lmhotels.net.models.Hotel
import it.simonerenzo.lmhotels.net.models.Location
import it.simonerenzo.lmhotels.ui.items.GalleryItem
import kotlinx.android.synthetic.main.activity_hotel_details.*
import kotlinx.android.synthetic.main.card_details_contact.*
import kotlinx.android.synthetic.main.card_details_gallery.*
import kotlinx.android.synthetic.main.card_details_general.*
import kotlinx.android.synthetic.main.card_details_location.*
import org.jetbrains.anko.design.snackbar
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

        setupLocation(hotel.location)
        setupGeneralDetails(hotel)
        setupGallery(hotel.images ?: emptyList())
        setupContact(hotel.contact)
        setupEvents(hotel)
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

    /**
     * Setup Location Card
     *
     * @param location Location Object
     */
    private fun setupLocation(location: Location) {
        hotelDetailsAddress.text = location.address
        hotelDetailsCity.text = location.city
    }

    /**
     * Setup General Details Card
     *
     * @param hotel Hotel Object
     */
    private fun setupGeneralDetails(hotel: Hotel) {
        Glide.with(this)
            .load(hotel.images?.get(0))
            .into(hotelDetailsHeader)

        hotelDetailsStars.rating = hotel.stars.toFloat()

        hotelUserRating.text = BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
            .badgeColor(Material.Amber._500.asColor)
            .text1(hotel.userRating.toString())
            .text2("10")
            .strokeWidth(2)
            .build()
            .toSpannable()

        val hoursTemplate = resources.getString(R.string.text_details_checkhours_template)
        hotelDetailsCheckIn.text = HtmlCompat.fromHtml(
            String.format(hoursTemplate, hotel.checkIn.from, hotel.checkIn.to),
            HtmlCompat.FROM_HTML_MODE_COMPACT)
        hotelDetailsCheckOut.text = HtmlCompat.fromHtml(
            String.format(hoursTemplate, hotel.checkOut.from, hotel.checkOut.to),
            HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    /**
     * Setup Hotel Gallery Card
     *
     * @param images List of Hotel Images (URIs)
     */
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

    /**
     * Setup Contacts Card
     *
     * @param contact Contact Object
     */
    private fun setupContact(contact: Contact) {
        hotelPhone.text = contact.phoneNumber
        hotelEmail.text = contact.email
    }

    /**
     * Setup UI Events handlers
     *
     * @param hotel Hotel Object
     */
    @SuppressLint("CheckResult")
    private fun setupEvents(hotel: Hotel) {
        RxView.clicks(detailsGoto)
            .bindToLifecycle(this)
            .subscribe {
                val location = hotel.location
                val gmmIntentUri = Uri.parse("google.navigation:q=${location.latitude},${location.longitude}")

                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")

                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    hotelDetailsContent.snackbar(R.string.error_details_maps)
                }
            }
    }

}