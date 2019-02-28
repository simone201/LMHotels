package it.simonerenzo.lmhotels.ui.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import it.simonerenzo.lmhotels.R
import kotterknife.bindView

class GalleryItem(val imageUri: String)
    : AbstractItem<GalleryItem, GalleryItem.ViewHolder>() {

    override fun getType(): Int = R.id.galleryItem

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_details_gallery

    class ViewHolder(view: View) : FastAdapter.ViewHolder<GalleryItem>(view) {

        private val image by bindView<AppCompatImageView>(R.id.hotelGalleryImage)

        override fun bindView(item: GalleryItem, payloads: List<Any>) {
            Glide.with(itemView)
                .load(item.imageUri)
                .thumbnail(0.5f)
                .placeholder(ColorDrawable(Color.DKGRAY))
                .into(image)
        }

        override fun unbindView(item: GalleryItem) {
            image.setImageDrawable(null)
        }

    }

}