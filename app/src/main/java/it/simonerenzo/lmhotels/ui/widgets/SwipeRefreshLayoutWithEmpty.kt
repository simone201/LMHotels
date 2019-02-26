package it.simonerenzo.lmhotels.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class SwipeRefreshLayoutWithEmpty : SwipeRefreshLayout {

    private var container: ViewGroup? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun canChildScrollUp(): Boolean {
        // The swipe refresh layout has 2 children; the circle refresh indicator
        // and the view container. The container is needed here
        val container = getContainer() ?: return false

        // The container has 2 children; the empty view and the scrollable view.
        // Use whichever one is visible and test that it can scroll
        var view = container.getChildAt(0)

        if (view.visibility != View.VISIBLE) {
            view = container.getChildAt(1)
        }

        return view.canScrollVertically(-1)
    }

    private fun getContainer(): ViewGroup? {
        // Cache this view
        if (container != null) {
            return container
        }

        // The container may not be the first view. Need to iterate to find it
        for (i in 0 until childCount) {
            if (getChildAt(i) is ViewGroup) {
                container = getChildAt(i) as ViewGroup

                if (container!!.childCount != 2) {
                    throw RuntimeException("Container must have an empty view and content view")
                }

                break
            }
        }

        if (container == null) {
            throw RuntimeException("Container view not found")
        }

        return container
    }
}

