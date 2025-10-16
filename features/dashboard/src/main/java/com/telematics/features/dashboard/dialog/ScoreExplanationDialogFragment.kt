package com.telematics.features.dashboard.dialog

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.telematics.features.dashboard.R
import com.telematics.features.dashboard.databinding.FragmentDialogScoreExplanationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScoreExplanationDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentDialogScoreExplanationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        isCancelable = true

        binding = FragmentDialogScoreExplanationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.okBtn.setOnClickListener(this)
        initView()
    }

    private fun initView() = with(binding) {

        highlightText(
            highRiskDriving,
            getString(R.string.dashboard_high_risk_driving) + " " + getString(R.string.dashboard_high_risk_driving_explanation),
            getString(R.string.dashboard_high_risk_driving)
        )
        highlightText(
            aggressiveDriving,
            getString(R.string.dashboard_aggressive_driving) + " " + getString(R.string.dashboard_aggressive_driving_explanation),
            getString(R.string.dashboard_aggressive_driving)
        )
        highlightText(
            moderateDriving,
            getString(R.string.dashboard_moderate_driving) + " " + getString(R.string.dashboard_moderate_driving_explanation),
            getString(R.string.dashboard_moderate_driving)
        )
        highlightText(
            safeDriving,
            getString(R.string.dashboard_safe_driving) + " " + getString(R.string.dashboard_safe_driving_explanation),
            getString(R.string.dashboard_safe_driving)
        )
    }

    private fun highlightText(textView: TextView, text: String, highlightedText: String) {

        val spannableString = SpannableString(text)

        val start = text.indexOf(highlightedText)
        val end = start + highlightedText.length

        val styleSpanSemibold =
            ResourcesCompat.getFont(requireContext(), R.font.open_sans_bold_700)!!.style

        spannableString.setSpan(
            StyleSpan(styleSpanSemibold),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannableString
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.okBtn.id -> dismiss()
        }
    }

}