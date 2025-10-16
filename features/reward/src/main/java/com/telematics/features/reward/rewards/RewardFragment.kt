package com.telematics.features.reward.rewards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.features.reward.R
import com.telematics.features.reward.RewardFeatureHost
import com.telematics.features.reward.databinding.FragmentRewardBinding
import com.telematics.features.reward.drivecoins.DrivecoinsFragment
import com.telematics.features.reward.streaks.StreaksFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RewardFragment : BaseFragment() {
    private val rewardViewModel: RewardViewModel by viewModels()

    private var lastOpenFragment = 0

    private lateinit var binding: FragmentRewardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackPressedExitCallback()

        initViews()
    }

    private fun initViews() {

        var toStreaks: Boolean
        findNavController().graph.arguments.apply {
            toStreaks = this[RewardFeatureHost.NAV_TO_STREAKS]?.defaultValue as Boolean? ?: false
        }

        initTabs()
        initInviteScreen()

        openDriveCoinsFragment()

        if (toStreaks)
            binding.headerTabs.getTabAt(1)?.select()

        hideStreaks()
    }

    private fun initInviteScreen() {
        if (rewardViewModel.isNeedShowRewardsInvite) {
            binding.rewardsInvite.root.isVisible = true
            binding.rewards.isVisible = false
            binding.rewardsInvite.rewardsInviteStartBtn.setOnClickListener { closeInviteScreen() }
        } else {
            binding.rewardsInvite.root.isVisible = false
            binding.rewards.isVisible = true
        }
    }

    private fun closeInviteScreen() {
        binding.rewardsInvite.root.isVisible = false
        binding.rewards.isVisible = true
        rewardViewModel.inviteScreenClosed()
    }


    private fun initTabs() {

        val tabLayout = binding.headerTabs
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> openDriveCoinsFragment()
                    1 -> openStreaksFragment()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun openDriveCoinsFragment() {
        lastOpenFragment = 0
        openFragment(DrivecoinsFragment())
    }

    private fun openStreaksFragment() {
        lastOpenFragment = 1
        openFragment(StreaksFragment())
    }

    private fun openFragment(fragment: Fragment) {

        val container = R.id.more_drive_coins_container
        val manager: FragmentManager = parentFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(container, fragment)
        transaction.commit()
    }


    private fun hideStreaks() {

        val tabLayout = binding.headerTabs
        (tabLayout.getTabAt(0)?.view as LinearLayout).visibility = View.GONE
        (tabLayout.getTabAt(1)?.view as LinearLayout).visibility = View.GONE

        val params = tabLayout.layoutParams
        params.height = 1
        tabLayout.layoutParams = params

        binding.headerTabs.getTabAt(0)?.select()
    }
}