package it.simonerenzo.lmhotels.ui.items

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.materialize.holder.StringHolder
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
        }

        override fun unbindView(item: HotelItem) {
            image.setImageDrawable(null)
            name.text = null
            stars.rating = 0f
            address.text = null
        }

    }

}