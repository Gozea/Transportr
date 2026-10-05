/*
 *    Transportr
 *
 *    Copyright (c) 2013 - 2021 Torsten Grote
 *
 *    This program is Free Software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.grobox.transportr.trips.detail

import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.R as MaterialR
import com.mikepenz.materialdrawer.R as MaterialDrawerR
import de.grobox.transportr.R
import de.grobox.transportr.TransportrFragment
import de.grobox.transportr.trips.detail.TripDetailViewModel.SheetState
import de.grobox.transportr.trips.detail.TripDetailViewModel.SheetState.*
import de.grobox.transportr.trips.detail.TripUtils.getStandardFare
import de.grobox.transportr.trips.detail.TripUtils.hasFare
import de.grobox.transportr.trips.detail.TripUtils.intoCalendar
import de.grobox.transportr.trips.detail.TripUtils.share
import de.grobox.transportr.utils.DateUtils.formatDuration
import de.grobox.transportr.utils.DateUtils.formatTime
import de.grobox.transportr.utils.DateUtils.formatRelativeTime
import de.grobox.transportr.utils.TransportrUtils.getColorFromAttr
import de.schildbach.pte.dto.Trip
import de.grobox.transportr.databinding.FragmentTripDetailBinding
import javax.inject.Inject

class TripDetailFragment : TransportrFragment(), Toolbar.OnMenuItemClickListener {

    companion object {
        val TAG = TripDetailFragment::class.java.simpleName
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var binding: FragmentTripDetailBinding? = null

    private val views: FragmentTripDetailBinding
        get() = binding ?: error("TripDetailFragment views accessed outside of the view lifecycle")

    private lateinit var viewModel: TripDetailViewModel

    private val timeUpdater: CountDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000 * 30) {
        override fun onTick(millisUntilFinished: Long) {
            viewModel.getTrip().value?.let {
                formatRelativeTime(views.fromTimeRel.context, it.firstDepartureTime).let {
                    views.fromTimeRel.apply {
                        text = it.relativeTime
                        visibility = it.visibility
                    }
                }
            }
        }

        override fun onFinish() {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentTripDetailBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        component.inject(this)

        views.toolbar.setNavigationOnClickListener { _ -> onToolbarClose() }
        views.toolbar.setOnMenuItemClickListener(this)
        views.list.layoutManager = LinearLayoutManager(context)
        views.bottomBar.setOnClickListener { _ -> onBottomBarClick() }

        viewModel = ViewModelProvider(activity!!, viewModelFactory).get(TripDetailViewModel::class.java)
        viewModel.getTrip().observe(viewLifecycleOwner, Observer<Trip> { this.onTripChanged(it) })
        viewModel.sheetState.observe(viewLifecycleOwner, Observer<SheetState> { this.onSheetStateChanged(it) })
    }

    override fun onStart() {
        super.onStart()
        timeUpdater.start()
    }

    override fun onStop() {
        super.onStop()
        timeUpdater.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val toolbarMenu = views.toolbar.menu
        inflater.inflate(R.menu.trip_details, toolbarMenu)
        viewModel.tripReloadError.observe(this, Observer<String> { this.onTripReloadError(it) })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reload -> {
                item.setActionView(R.layout.actionbar_progress_actionview)
                viewModel.reloadTrip()
                return true
            }
            R.id.action_share -> {
                share(context, viewModel.getTrip().value)
                return true
            }
            R.id.action_calendar -> {
                intoCalendar(context, viewModel.getTrip().value)
                return true
            }
            else -> return false
        }
    }

    private fun onTripChanged(trip: Trip?) {
        if (trip == null) return

        val reloadMenuItem = views.toolbar.menu.findItem(R.id.action_reload)
        if (reloadMenuItem != null) reloadMenuItem.actionView = null

        val network = viewModel.transportNetwork.value
        val showLineName = network != null && network.hasGoodLineNames()
        val adapter = LegAdapter(trip.legs, viewModel, showLineName)
        views.list.adapter = adapter

        views.fromTime.text = formatTime(context, trip.firstDepartureTime)
        formatRelativeTime(views.fromTimeRel.context, trip.firstDepartureTime).let {
            views.fromTimeRel.apply {
                text = it.relativeTime
                visibility = it.visibility
            }
        }
        views.from.text = trip.from.uniqueShortName()
        views.toTime.text = formatTime(context, trip.lastArrivalTime)
        views.to.text = trip.to.uniqueShortName()
        views.duration.text = formatDuration(trip.duration)
        views.durationTop.text = getString(R.string.total_time, formatDuration(trip.duration))
        views.price.visibility = if (trip.hasFare()) VISIBLE else GONE
        views.price.text = trip.getStandardFare()
        views.priceTop.visibility = if (trip.hasFare()) VISIBLE else GONE
        views.priceTop.text = trip.getStandardFare()
    }

    private fun onToolbarClose() {
        viewModel.sheetState.value = BOTTOM
    }

    private fun onBottomBarClick() {
        viewModel.sheetState.value = MIDDLE
    }

    private fun onSheetStateChanged(sheetState: SheetState?) {
        when (sheetState) {
            null -> return
            BOTTOM -> {
                views.toolbar.visibility = GONE
                views.topBar.visibility = GONE
                views.bottomBar.visibility = VISIBLE
            }
            MIDDLE -> {
                views.toolbar.visibility = GONE
                views.topBar.visibility = VISIBLE
                views.topBar.setBackgroundColor(context.getColorFromAttr(MaterialR.attr.colorPrimary))
                views.fromTimeRel.setTextColor(getColor(context, MaterialDrawerR.color.md_white_1000))
                views.durationTop.setTextColor(getColor(context, MaterialDrawerR.color.md_white_1000))
                views.priceTop.setTextColor(getColor(context, MaterialDrawerR.color.md_white_1000))
                views.bottomBar.visibility = GONE
            }
            EXPANDED -> {
                views.toolbar.visibility = VISIBLE
                views.topBar.visibility = VISIBLE
                views.topBar.setBackgroundColor(context.getColorFromAttr(android.R.attr.colorBackground))
                views.fromTimeRel.setTextColor(context.getColorFromAttr(android.R.attr.textColorPrimary))
                views.durationTop.setTextColor(context.getColorFromAttr(android.R.attr.textColorSecondary))
                views.priceTop.setTextColor(context.getColorFromAttr(android.R.attr.textColorSecondary))
                views.bottomBar.visibility = GONE
            }
        }
    }

    private fun onTripReloadError(error: String?) {
        views.toolbar.menu.findItem(R.id.action_reload).actionView = null
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

}
