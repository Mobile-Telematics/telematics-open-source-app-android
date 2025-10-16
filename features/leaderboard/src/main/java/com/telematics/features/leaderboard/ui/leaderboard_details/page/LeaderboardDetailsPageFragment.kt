package com.telematics.features.leaderboard.ui.leaderboard_details.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.telematics.core.common.extension.getSerializableCompat
import com.telematics.core.model.leaderboard.LeaderboardType
import com.telematics.features.leaderboard.databinding.FragmentLeaderboardDetailsPageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaderboardDetailsPageFragment : Fragment() {

    companion object {
        const val LEADERBOARD_DETAILS_PAGE_TYPE = "LEADERBOARD_DETAILS_PAGE_TYPE"
    }

    private val leaderboardDetailsPageViewModel: LeaderboardDetailsPageViewModel by viewModels()

    private lateinit var adapter: LeaderboardDetailsPageAdapter

    private lateinit var binding: FragmentLeaderboardDetailsPageBinding

    private var leaderboardType = LeaderboardType.Rate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderboardDetailsPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getSerializableCompat(
            LEADERBOARD_DETAILS_PAGE_TYPE,
            LeaderboardType::class.java
        )?.also {
            leaderboardType = it
        }

        initViews()
        observeLeaderboardUserList(leaderboardType)
    }

    private fun initViews() {

        initLeaderboardUserList()
        setListeners()
    }

    private fun setListeners() {

        binding.leaderboardSwipeToRefresh.setOnRefreshListener {
            observeLeaderboardUserList(leaderboardType)
        }
    }

    private fun initLeaderboardUserList() {

        val recyclerView = binding.leaderboardRecyclerView
        adapter = LeaderboardDetailsPageAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
    }

    private fun observeLeaderboardUserList(type: LeaderboardType) {

        val mappedType = when (type) {
            LeaderboardType.Rate -> 6
            LeaderboardType.Acceleration -> 1
            LeaderboardType.Deceleration -> 2
            LeaderboardType.Speeding -> 4
            LeaderboardType.Distraction -> 3
            LeaderboardType.Turn -> 5
            LeaderboardType.Trips -> 8
            LeaderboardType.Distance -> 7
            LeaderboardType.Duration -> 9
        }

        showRefresh(true)
        leaderboardDetailsPageViewModel.getUserListByType(mappedType)
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess { data ->
                    adapter.setData(data)
                }
                result.onFailure {

                }
                showRefresh(false)
            }
    }

    private fun showRefresh(show: Boolean) {

        binding.leaderboardSwipeToRefresh.isRefreshing = show
    }
}