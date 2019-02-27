package it.simonerenzo.lmhotels.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.OnClickListener
import com.mikepenz.itemanimators.AlphaInAnimator
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.simonerenzo.lmhotels.R
import it.simonerenzo.lmhotels.activities.HotelDetailsActivity
import it.simonerenzo.lmhotels.net.models.Hotel
import it.simonerenzo.lmhotels.net.services.HotelsService
import it.simonerenzo.lmhotels.ui.items.HotelItem
import kotterknife.bindView
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.startActivity
import retrofit2.HttpException

class HomeFragment : Fragment(),
    SwipeRefreshLayout.OnRefreshListener,
    OnClickListener<HotelItem> {

    private val hotelsClient by lazyOf(HotelsService.create())

    private val hotelSwipe by bindView<SwipeRefreshLayout>(R.id.hotelSwipe)
    private val hotelList by bindView<RecyclerView>(R.id.hotelList)
    private val hotelEmpty by bindView<LinearLayoutCompat>(R.id.hotelEmptyView)
    private val hotelEmptyAnim by bindView<LottieAnimationView>(R.id.hotelEmptyAnim)

    private val itemAdapter = ItemAdapter<HotelItem>()
    private val listAdapter = FastAdapter.with<HotelItem, ItemAdapter<HotelItem>>(itemAdapter)

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        hotelSwipe.setColorSchemeResources(
            R.color.md_green_500,
            R.color.md_cyan_500,
            R.color.md_red_500,
            R.color.md_yellow_500)
        hotelSwipe.setOnRefreshListener(this)

        listAdapter.withSelectable(true)
        listAdapter.withOnClickListener(this)

        hotelList.addItemDecoration(LayoutMarginDecoration(1, 32))
        hotelList.overScrollMode = View.OVER_SCROLL_NEVER
        hotelList.layoutManager = LinearLayoutManager(activity)
        hotelList.itemAnimator = AlphaInAnimator()
        hotelList.adapter = listAdapter
    }

    override fun onResume() {
        super.onResume()

        if (itemAdapter.itemList.isEmpty) {
            toggleEmpty(false)
            getData()
        }
    }

    override fun onClick(v: View?,
                         adapter: IAdapter<HotelItem>,
                         item: HotelItem,
                         position: Int): Boolean {
        startActivity<HotelDetailsActivity>("item" to item.hotelItem)
        return true
    }

    override fun onRefresh() {
        getData()
    }

    private fun toggleEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            hotelList.visibility = View.GONE
            hotelEmpty.visibility = View.VISIBLE
            hotelEmptyAnim.playAnimation()
        } else {
            hotelList.visibility = View.VISIBLE
            hotelEmpty.visibility = View.GONE
            hotelEmptyAnim.cancelAnimation()
        }
    }

    private fun getData() : Disposable {
        return hotelsClient.getHotels()
            .bindToLifecycle(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { hotelSwipe.isRefreshing = true }
            .doAfterTerminate { hotelSwipe.isRefreshing = false }
            .subscribe(
                { res -> fillList(res.hotels) },
                { e ->
                    if (e is HttpException) {
                        if (e.code() == 401) {
                            activity?.finish()
                        } else {
                            hotelSwipe.snackbar("A network error has occurred")
                        }
                    }

                    Log.e("HTTP ERROR", "An error has occurred", e)
                }
            )
    }

    private fun fillList(data: List<Hotel>) {
        itemAdapter.clear()
        itemAdapter.add(
            data.map { HotelItem(it) }
        )

        toggleEmpty(itemAdapter.itemList.isEmpty)
    }

}