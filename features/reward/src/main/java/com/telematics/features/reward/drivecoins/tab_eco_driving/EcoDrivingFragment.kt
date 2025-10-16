package com.telematics.features.reward.drivecoins.tab_eco_driving

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.telematics.core.common.extension.getColorByScore
import com.telematics.core.common.extension.getSerializableCompat
import com.telematics.core.model.reward.DriveCoinsDetailedData
import com.telematics.features.reward.R
import com.telematics.features.reward.databinding.FragmentEcoDriveBinding
import com.telematics.features.reward.drivecoins.DriveCoinsViewPagerAdapter.Companion.DRIVE_COINS_DETAILED_KEY

class EcoDrivingFragment : Fragment() {

    private lateinit var binding: FragmentEcoDriveBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEcoDriveBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // disabling seek_bar sliding
            ecoSeekBar.setOnTouchListener { _, _ -> true }
            brakedSeekBar.setOnTouchListener { _, _ -> true }
            fuelSeekBar.setOnTouchListener { _, _ -> true }
            tyresSeekBar.setOnTouchListener { _, _ -> true }
            costSeekBar.setOnTouchListener { _, _ -> true }

            val data = arguments?.getSerializableCompat(
                DRIVE_COINS_DETAILED_KEY,
                DriveCoinsDetailedData::class.java
            ) ?: DriveCoinsDetailedData()

            ecoSeekBar.apply {
                setProgressForSeekBar(this, data.ecoScore)
            }
            brakedSeekBar.apply {
                setProgressForSeekBar(this, data.ecoScoreBrakes)
            }
            fuelSeekBar.apply {
                setProgressForSeekBar(this, data.ecoScoreFuel)
            }
            tyresSeekBar.apply {
                setProgressForSeekBar(this, data.ecoScoreTyres)
            }
            costSeekBar.apply {
                setProgressForSeekBar(this, data.ecoScoreCostOfOwnership)
            }

            ecoScore.apply {
                setTextColor(getTextColor(data.ecoDrivingEcoScore))
                text = data.ecoDrivingEcoScore.toString()
            }
            brakeScore.apply {
                setTextColor(getTextColor(data.ecoDrivingBrakes))
                text = data.ecoDrivingBrakes.toString()
            }
            fuelScore.apply {
                setTextColor(getTextColor(data.ecoDrivingFuel))
                text = data.ecoDrivingFuel.toString()
            }
            tyresScore.apply {
                setTextColor(getTextColor(data.ecoDrivingTires))
                text = data.ecoDrivingTires.toString()
            }
            costScore.apply {
                setTextColor(getTextColor(data.ecoDrivingCostOfOwnership))
                text = data.ecoDrivingCostOfOwnership.toString()
            }
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

    private fun getProgressTextColor(p: Int): ColorStateList? {
        val res = p.getColorByScore()
        return ContextCompat.getColorStateList(requireContext(), res)
    }

    private fun setProgressForSeekBar(seekBar: SeekBar, p: Int) {

        val vl = ValueAnimator.ofInt(0, p)
        vl.duration = 1000
        vl.addUpdateListener {
            val v = it.animatedValue as Int
            seekBar.progress = v
        }
        vl.start()
        seekBar.progressTintList = getProgressTextColor(p)
        seekBar.thumbTintList = ContextCompat.getColorStateList(
            requireContext(),
            android.R.color.transparent
        )//getProgressTextColor(p)
        seekBar.splitTrack = false
    }
}