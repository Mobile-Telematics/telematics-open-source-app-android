package com.telematics.features.settings.company_id

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import com.telematics.core.common.extension.hideKeyboard
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.extension.systemBarsInsets
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.features.settings.R
import com.telematics.features.settings.databinding.FragmentCompanyIdBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompanyIDFragment : BaseFragment() {

    private lateinit var binding: FragmentCompanyIdBinding

    private val companyIdViewModel: CompanyIdViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyIdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInset()
        setListeners()
    }

    private fun setListeners() {

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.profileCompanyIdSend.setOnClickListener {
            sendCompanyId(binding.profileCompanyIdInput.text.toString())
        }

        binding.profileCompanyIdInput.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                sendCompanyId(binding.profileCompanyIdInput.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun sendCompanyId(companyId: String) {

        hideKeyboard()

        companyIdViewModel.send(companyId).observe(viewLifecycleOwner) { result ->
            result.onSuccess { instanceName ->
                if (instanceName.isSuccess) {
                    val name = instanceName.name ?: ""
                    showSuccessMsg(name)
                } else
                    showError(R.string.company_screen_invalid_code)
            }
            result.onFailure {
                showError(R.string.company_screen_invalid_code)
            }
        }
    }

    private fun showError(res: Int) {
        showError(getString(res))
    }

    private fun showError(msg: String) {

        val oldText = binding.profileCompanyIdText.text

        binding.profileCompanyIdText.text = msg
        binding.profileCompanyIdText.setTextColor(Color.RED)

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                binding.profileCompanyIdText.text = oldText
                binding.profileCompanyIdText.setTextColor(Color.BLACK)
            } catch (_: Exception) {
            }
        }, 3000L)

    }

    private fun showSuccessMsg(msg: String) {

        val text = getString(R.string.dashboard_settings_company_id_success, " $msg")
        showMessage(text)
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