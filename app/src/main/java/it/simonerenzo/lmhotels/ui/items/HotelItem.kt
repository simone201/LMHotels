package it.simonerenzo.lmhotels.ui.items

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.view.RxView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.materialize.holder.StringHolder
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import hyogeun.github.com.colorratingbarlib.ColorRatingBar
import it.simonerenzo.lmhotels.R
import it.simonerenzo.lmhotels.net.models.Hotel
import kotterknife.bindView

const val EMPTY_IMAGE = "http://leeford.in/wp-content/uploads/2017/09/image-not-found.jpg"

class HotelItem(val hotelItem: Hotel)
    : AbstractItem<HotelItem, HotelItem.ViewHolder>() {

    override fun getType(): Int = R.id.hotelItem

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_hotel

    class ViewHolder(view: View) : FastAdapter.ViewHolder<HotelItem>(view) {

        private val image by bindView<AppCompatImageView>(R.id.hotelCardImage)
        private val name by bindView<AppCompatTextView>(R.id.hotelName)
        private val stars by bindView<ColorRatingBar>(R.id.hotelStars)
        private val address by bindView<AppCompatTextView>(R.id.hotelAddress)
        private val share by bindView<AppCompatImageButton>(R.id.hotelShareBtn)

        @SuppressLint("CheckResult")
        override fun bindView(item: HotelItem, payloads: List<Any>) {
            val hotel = item.hotelItem

            val transparent = itemView.resources.getDrawable(android.R.color.transparent, null)

            image.background = transparent
            name.background = transparent
            address.background = transparent

            Glide.with(itemView)
                .load(hotel.images?.get(0) ?: EMPTY_IMAGE)
                .into(image)

            StringHolder.applyToOrHide(StringHolder(hotel.name), name)
            StringHolder.applyToOrHide(StringHolder("${hotel.location.address} - ${hotel.location.city}"), address)

            stars.rating = hotel.stars.toFloat()

            RxView.clicks(share)
                .bindToLifecycle(itemView)
                .subscribe {
                    val shareTemplate = itemView.resources.getString(R.string.share_hotel_template)
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, String.format(shareTemplate,
                            hotel.name,
                            hotel.stars,
                            hotel.location.address,
                            hotel.location.city))
                        type = "text/plain"
                    }

                    itemView.context.startActivity(
                        Intent.createChooser(sendIntent,
                            itemView.resources.getText(R.string.action_send_to)))
                }
        }

        override fun unbindView(item: HotelItem) {
            image.setImageDrawable(null)
            name.text = null
            stars.rating = 0f
            address.text = null
            share.setOnClickListener(null)
        }

    }

}