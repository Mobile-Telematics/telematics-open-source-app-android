package com.telematics.features.dashboard

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
import com.telematics.features.dashboard.databinding.FragmentDashboardFeatureHostBinding

class DashboardFeatureHost : Fragment() {

    private lateinit var binding: FragmentDashboardFeatureHostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardFeatureHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dashboardNavHost.getFragment<NavHostFragment>().navController
            .addOnDestinationChangedListener { _, destination, _ ->
                binding.root.post {
                    try {
                        setFragmentResult(
                            TOP_BAR_VISIBILITY_KEY,
                            bundleOf(INT_RESULT_KEY to if (destination.id == R.id.dashboardFragment) 1 else 0)
                        )
                        /*setFragmentResult(
                            BOTTOM_BAR_VISIBILITY_KEY,
                            bundleOf(BOOLEAN_RESULT_KEY to (destination.id == R.id.dashboardFragment))
                        )*/
                    } catch (_: Exception) {

                    }
                }
            }
    }
}