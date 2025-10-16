package com.telematics.features.reward.drivecoins.tab_safe_driving

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.telematics.core.common.extension.getSerializableCompat
import com.telematics.core.model.reward.DriveCoinsDetailedData
import com.telematics.features.reward.R
import com.telematics.features.reward.databinding.FragmentSafeDriveBinding
import com.telematics.features.reward.drivecoins.DriveCoinsViewPagerAdapter.Companion.DRIVE_COINS_DETAILED_KEY

class SafeDriveFragment : Fragment() {

    private lateinit var binding: FragmentSafeDriveBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSafeDriveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data: DriveCoinsDetailedData = arguments?.getSerializableCompat(
            DRIVE_COINS_DETAILED_KEY,
            DriveCoinsDetailedData::class.java
        ) ?: DriveCoinsDetailedData()

        binding.safeTotalCoins.apply {
            text = data.safeDrivingCoinsTotal.toString()
            setTextColor(getTextColor(data.safeDrivingCoinsTotal))
        }
    }

    private fun getTextColor(p: Int): Int {
        val rc = when {
            p == 0 -> R.color.colorPrimaryText
            p > 0 -> R.color.colorGreenText
            else -> R.color.colorRedText
        }
        return ContextCompat.getColor(requireContext(), rc)
    }
}