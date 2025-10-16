package com.telematics.features.settings.logbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.telematics.core.common.NetworkException
import com.telematics.core.common.dialog.EnterDateDropdownDialog
import com.telematics.core.common.dialog.EnterDateDropdownDialog.enterDate
import com.telematics.core.common.dialog.FailureDialogFragment
import com.telematics.core.common.dialog.SuccessDialogFragment
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.extension.systemBarsInsets
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.model.logbook.LogbookDateFormat
import com.telematics.core.model.logbook.LogbookTypeOfReport
import com.telematics.core.model.logbook.LogbookUnits
import com.telematics.core.model.measures.DistanceMeasure
import com.telematics.features.settings.R
import com.telematics.features.settings.databinding.FragmentDriverLogbookBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DriverLogbookFragment : BaseFragment(), View.OnClickListener {

    companion object {

        private const val ERROR_REQUEST_KEY = "error_request_key"
        private const val SUCCESS_REQUEST_KEY = "success_request_key"
        private const val DIALOG_RESULT_KEY = "dialog_result_key"
    }

    private val viewModel: DriverLogbookViewModel by viewModels()

    private lateinit var binding: FragmentDriverLogbookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        childFragmentManager.setFragmentResultListener(
            SUCCESS_REQUEST_KEY,
            this
        ) { _, bundle ->
            if (bundle.getBoolean(DIALOG_RESULT_KEY, true)) {
                findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDriverLogbookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setInset()
        collectUiState()
        setListeners()
    }

    private fun initView() {
        binding.units.apply {
            setText(
                getString(
                    if (viewModel.getDistanceMeasure() == DistanceMeasure.KM) {
                        LogbookUnits.METRIC.stringId
                    } else {
                        LogbookUnits.IMPERIAL.stringId
                    }
                )
            )
            tag = if (viewModel.getDistanceMeasure() == DistanceMeasure.KM) {
                LogbookUnits.METRIC.value
            } else {
                LogbookUnits.IMPERIAL.value
            }
        }

        binding.dateFormat.apply {
            setText(
                getString(
                    LogbookDateFormat.STANDARD.stringId
                )
            )
            tag = LogbookDateFormat.STANDARD.value
        }

        binding.reportType.apply {
            setText(
                getString(
                    LogbookTypeOfReport.DAILY_SUMMARY.stringId
                )
            )
            tag = LogbookTypeOfReport.DAILY_SUMMARY.value
        }
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        enterDate(requireContext(), binding.fromDate)
        enterDate(requireContext(), binding.toDate)

        binding.units.setOnClickListener(this)
        binding.dateFormat.setOnClickListener(this)
        binding.reportType.setOnClickListener(this)
        binding.submit.setOnClickListener(this)
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getUserProfileFlow()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { userProfile ->
                    userProfile?.apply {
                        binding.email.setText(email)
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect {
                    if (it.isNotBlank()) {
                        FailureDialogFragment.getNewInstance(
                            description = it,
                            requestKey = ERROR_REQUEST_KEY,
                            resultKey = DIALOG_RESULT_KEY
                        ).show(childFragmentManager, this@DriverLogbookFragment::class.java.name)
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { uiState ->

                    with(uiState) {
                        showProgressBar(isLoading)

                        error?.let { throwable ->
                            handleFailure(throwable)
                            viewModel.onErrorHandled()
                        }

                        if (logbookRequested) {
                            SuccessDialogFragment.getNewInstance(
                                description = getString(R.string.logbook_success),
                                button = getString(R.string.dialog_button_ok),
                                requestKey = SUCCESS_REQUEST_KEY,
                                resultKey = DIALOG_RESULT_KEY
                            ).show(
                                childFragmentManager,
                                this@DriverLogbookFragment::class.java.name
                            )
                        }
                    }
                }
        }
    }

    private fun showProgressBar(visible: Boolean) {
        binding.progressBar.isVisible = visible
    }

    private fun showDropdownMenu(anchor: EditText, items: List<String>, values: List<String>) {
        val popup = PopupMenu(anchor.context, anchor)

        items.forEachIndexed { index, item ->
            popup.menu.add(0, index, 0, item)
        }

        popup.setOnMenuItemClickListener { menuItem ->
            val selectedItem = items[menuItem.itemId]
            anchor.setText(selectedItem)
            anchor.tag = values[menuItem.itemId]
            true
        }

        popup.show()
    }

    private fun handleFailure(throwable: Throwable? = null) {

        when (throwable) {

            is NetworkException.NoNetwork -> {
                showMessage(R.string.auth_error_network)
            }

            else -> {
                showMessage(R.string.something_went_wrong)
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

    override fun onClick(v: View?) {
        when (v?.id) {

            binding.units.id -> {
                showDropdownMenu(
                    binding.units,
                    items = LogbookUnits.entries.map { getString(it.stringId) },
                    values = LogbookUnits.entries.map { it.value }
                )
            }

            binding.dateFormat.id -> {
                showDropdownMenu(
                    binding.dateFormat,
                    items = LogbookDateFormat.entries.map { getString(it.stringId) },
                    values = LogbookDateFormat.entries.map { it.value }
                )
            }

            binding.reportType.id -> {
                showDropdownMenu(
                    binding.reportType,
                    items = LogbookTypeOfReport.entries.map { getString(it.stringId) },
                    values = LogbookTypeOfReport.entries.map { it.value }
                )
            }

            binding.submit.id -> {
                viewModel.sendRequest(
                    email = binding.email.text.toString(),
                    startDate = EnterDateDropdownDialog.getServerDate(binding.fromDate),
                    endDate = EnterDateDropdownDialog.getServerDate(binding.toDate),
                    units = binding.units.tag as String,
                    dateFormat = binding.dateFormat.tag as String,
                    reportType = binding.reportType.tag as String
                )
            }
        }
    }
}