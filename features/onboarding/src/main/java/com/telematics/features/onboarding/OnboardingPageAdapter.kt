package com.telematics.features.onboarding

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.telematics.features.onboarding.databinding.LayoutOnboardingCommonBinding

class OnboardingPageAdapter : RecyclerView.Adapter<OnboardingPageAdapter.ViewHolder>() {

    private lateinit var imagesData: List<Int>
    private lateinit var titlesData: List<String>
    private lateinit var subtitlesData: List<String>

    private fun initData(context: Context) {

        val isInitialized = listOf(
            this::imagesData.isInitialized,
            this::titlesData.isInitialized,
            this::subtitlesData.isInitialized
        ).all {
            it
        }

        if (isInitialized) return

        imagesData = listOf(
            R.drawable.onboarding_p1,
            R.drawable.onboarding_p2,
            R.drawable.onboarding_p3,
            R.drawable.onboarding_p4
        )
        titlesData = context.resources.getStringArray(R.array.onboarding_titles).toList()
        subtitlesData = context.resources.getStringArray(R.array.onboarding_subtitles).toList()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        initData(parent.context)

        return ViewHolder(
            LayoutOnboardingCommonBinding
                .inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 4

    inner class ViewHolder(val binding: LayoutOnboardingCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {

            onboardingPageImage.apply {
                setImageResource(imagesData[position])
                tag = "image_tag"
            }
            onboardingPageImageLogo.isVisible = position == 3
            onboardingPageLogo.isVisible = position == 0
            onboardingPageTitle.text = titlesData[position]
            if (position == 3) {
                onboardingPageSubtitle.apply {
                    setText(R.string.onboarding_p4_text)
                    movementMethod = LinkMovementMethod.getInstance()
                }
            } else {
                onboardingPageSubtitle.text = subtitlesData[position]
            }
        }
    }
}

