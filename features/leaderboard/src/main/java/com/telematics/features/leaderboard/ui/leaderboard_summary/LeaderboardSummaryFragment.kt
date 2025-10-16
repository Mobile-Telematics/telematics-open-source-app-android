package com.telematics.features.leaderboard.ui.leaderboard_summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.model.leaderboard.LeaderboardType
import com.telematics.features.leaderboard.R
import com.telematics.features.leaderboard.databinding.FragmentLeaderboardSummaryBinding
import com.telematics.features.leaderboard.ui.leaderboard_details.LeaderboardDetailsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LeaderboardSummaryFragment : BaseFragment() {
    private val leaderboardViewModel: LeaderboardSummaryViewModel by viewModels()

    private lateinit var adapter: LeaderboardSummaryAdapter

    private lateinit var binding: FragmentLeaderboardSummaryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderboardSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackPressedExitCallback()

        initViews()
    }

    private fun initViews() {
        initRecyclerView()
        setListeners()
        collectUiState()
        leaderboardViewModel.apply {
            refreshLeaderboardDataData()
        }
    }

    private fun initRecyclerView() {

        adapter = LeaderboardSummaryAdapter(object : LeaderboardSummaryAdapter.ClickListener {
            override fun onClick(type: LeaderboardType) {
                openLeaderboardDetails(type)
            }
        })

        val recyclerView = binding.leaderboardList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
    }

    private fun setListeners() {
        binding.leaderboardSwipeToRefresh.setOnRefreshListener {
            leaderboardViewModel.refreshLeaderboardDataData()
        }
    }

    private fun collectUiState() {

        viewLifecycleOwner.lifecycleScope.launch {
            leaderboardViewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { uiState ->
                    with(uiState) {
                        showRefresh(isLoading)
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            leaderboardViewModel.getLeaderboardDataFlow()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { data ->
                    data.apply {
                        adapter.submitList(this)
                        showNoDataView(this.isEmpty())
                    }
                }
        }
    }

    private fun showNoDataView(show: Boolean) {

        binding.leaderboarNoData.isVisible = show
        binding.leaderboardList.isVisible = !show
    }

    private fun showRefresh(show: Boolean) {

        binding.leaderboardSwipeToRefresh.isRefreshing = show
    }

    private fun openLeaderboardDetails(type: LeaderboardType) {

        val bundle = bundleOf(
            LeaderboardDetailsFragment.LEADERBOARD_DETAILS_TYPE_KEY to type.index
        )
        findNavController().navigate(
            R.id.action_leaderboardSummaryFragment_to_leaderboardDetailsFragment,
            bundle
        )
    }
}