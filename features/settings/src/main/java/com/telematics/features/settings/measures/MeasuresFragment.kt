package com.telematics.features.settings.measures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import com.telematics.core.common.extension.systemBarsInsets
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.model.measures.DateMeasure
import com.telematics.core.model.measures.DistanceMeasure
import com.telematics.core.model.measures.MapStyle
import com.telematics.core.model.measures.TimeMeasure
import com.telematics.features.settings.databinding.FragmentMeasuresBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MeasuresFragment : BaseFragment() {

    private val measuresViewModel: MeasuresViewModel by viewModels()

    private lateinit var binding: FragmentMeasuresBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMeasuresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInset()
        observeMeasures()
        setListeners()
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.kmBtn.setOnClickListener { measuresViewModel.setDistanceMeasure(DistanceMeasure.KM) }
        binding.miBtn.setOnClickListener { measuresViewModel.setDistanceMeasure(DistanceMeasure.MI) }

        binding.ddMmButton.setOnClickListener { measuresViewModel.setDateMeasure(DateMeasure.DD_MM) }
        binding.mmDdButton.setOnClickListener { measuresViewModel.setDateMeasure(DateMeasure.MM_DD) }

        binding.btn24.setOnClickListener { measuresViewModel.setTimeMeasure(TimeMeasure.H24) }
        binding.btn12.setOnClickListener { measuresViewModel.setTimeMeasure(TimeMeasure.H12) }

        binding.darkSchemeBtn.setOnClickListener { measuresViewModel.setMapStyle(MapStyle.DARK) }
        binding.lightSchemeBtn.setOnClickListener { measuresViewModel.setMapStyle(MapStyle.LIGHT) }
    }

    private fun observeMeasures() {

        showDate(measuresViewModel.getDateMeasure())
        showDistance(measuresViewModel.getDistanceMeasure())
        showTime(measuresViewModel.getTimeMeasure())
        showMapStyle(measuresViewModel.getMapStyle())
    }

    private fun showDate(dateMeasure: DateMeasure) {

        when (dateMeasure) {
            DateMeasure.DD_MM -> {
                binding.ddMmButton.isChecked = true
            }

            DateMeasure.MM_DD -> {
                binding.mmDdButton.isChecked = true
            }
        }
    }

    private fun showDistance(distanceMeasure: DistanceMeasure) {

        when (distanceMeasure) {
            DistanceMeasure.KM -> {
                binding.kmBtn.isChecked = true
            }

            DistanceMeasure.MI -> {
                binding.miBtn.isChecked = true
            }
        }
    }

    private fun showTime(timeMeasure: TimeMeasure) {

        when (timeMeasure) {
            TimeMeasure.H24 -> {
                binding.btn24.isChecked = true
            }

            TimeMeasure.H12 -> {
                binding.btn12.isChecked = true
            }
        }
    }

    private fun showMapStyle(mapStyle: MapStyle) {
        when (mapStyle) {
            MapStyle.DARK -> {
                binding.darkSchemeBtn.isChecked = true
            }

            MapStyle.LIGHT -> {
                binding.lightSchemeBtn.isChecked = true
            }
        }
    }

    private fun setInset() {
        binding.toolbar.setOnApplyWindowInsetsListener { view, insets ->
            val systemBarsInsets = systemBarsInsets(insets)
            view.updatePadding(
                top = systemBarsInsets.top
            )

            insets
        }
    }
}