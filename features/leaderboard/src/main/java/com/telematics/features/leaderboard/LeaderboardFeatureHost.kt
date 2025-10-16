package com.telematics.features.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.NavHostFragment
import com.telematics.core.common.Constants.INT_RESULT_KEY
import com.telematics.core.common.Constants.TOP_BAR_VISIBILITY_KEY
import com.telematics.features.leaderboard.databinding.FragmentLeaderboardFeatureHostBinding

class LeaderboardFeatureHost : Fragment() {

    lateinit var binding: FragmentLeaderboardFeatureHostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderboardFeatureHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.leaderboardNavHost.getFragment<NavHostFragment>().navController
            .addOnDestinationChangedListener { _, destination, _ ->

                binding.root.post {
                    try {
                        setFragmentResult(
                            TOP_BAR_VISIBILITY_KEY,
                            bundleOf(INT_RESULT_KEY to if (destination.id == R.id.leaderboardSummaryFragment) 1 else 0)
                        )
                        /*setFragmentResult(
                        BOTTOM_BAR_VISIBILITY_KEY,
                        bundleOf(BOOLEAN_RESULT_KEY to (destination.id == R.id.leaderboardSummaryFragment))
                    )*/
                    } catch (_: Exception) {

                    }
                }
            }
    }
}