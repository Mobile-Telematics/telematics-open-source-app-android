package com.telematics.features.onboarding

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.telematics.core.common.extension.systemBarsAndDisplayCutout
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.features.onboarding.databinding.FragmentOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : BaseFragment(), View.OnClickListener {

    private val viewModel: OnboardingViewModel by viewModels()

    private lateinit var adapter: OnboardingPageAdapter
    private lateinit var binding: FragmentOnboardingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            applyInsets(insets)
        }
        initViews()
    }

    private fun initViews() {

        adapter = OnboardingPageAdapter()

        binding.onBoardingPager.adapter = adapter
        binding.onBoardingPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                showNextSignUpBtn()
            }
        })
        binding.onBoardingPagesIndicator.setViewPager(binding.onBoardingPager)

        binding.onboardingNext.setOnClickListener(this)
        binding.onboardingRegister.setOnClickListener(this)
    }

    private fun next() {
        if (!isLastPage()) {
            binding.onBoardingPager.currentItem++
        } else {
            nextScreen()
        }
    }


    private fun showNextBtn() {

        if (binding.onboardingNext.visibility == View.VISIBLE) return

        binding.onboardingNext.alpha = 0f
        binding.onboardingNext.translationX = 500f
        binding.onboardingNext.animate().setDuration(300).alpha(1f).translationX(0f)
            .setListener(null)
            .start()
        binding.onboardingNext.visibility = View.VISIBLE

        binding.onBoardingPagesIndicator.alpha = 0f
        binding.onBoardingPagesIndicator.translationX = -500f
        binding.onBoardingPagesIndicator.animate().setDuration(300).alpha(1f).translationX(0f)
            .setListener(null)
            .start()
        binding.onBoardingPagesIndicator.visibility = View.VISIBLE

        binding.onboardingRegister.visibility = View.INVISIBLE
    }

    private fun showSignUpBtn() {

        if (binding.onboardingRegister.visibility == View.VISIBLE) return

        binding.onboardingRegister.alpha = 0f
        binding.onboardingRegister.visibility = View.VISIBLE
        binding.onboardingRegister.animate().setDuration(300).alpha(1f).start()

        val onboardingNextView =
            view?.findViewById<View>(R.id.onboarding_next) ?: binding.onboardingNext
        onboardingNextView.alpha = 1f
        onboardingNextView.translationX = 0f
        onboardingNextView.animate().setDuration(100).alpha(0f).translationX(500f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    try {
                        onboardingNextView.visibility = View.INVISIBLE
                    } finally {
                    }
                }
            })
            .start()

        binding.onBoardingPagesIndicator.alpha = 1f
        binding.onBoardingPagesIndicator.translationX = 0f
        binding.onBoardingPagesIndicator.animate().setDuration(100).alpha(0f).translationX(-500f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    binding.onBoardingPagesIndicator.visibility = View.INVISIBLE
                }
            })
            .start()
    }

    private fun showNextSignUpBtn() {
        if (!isLastPage())
            showNextBtn()
        else
            showSignUpBtn()
    }

    private fun isLastPage(): Boolean = binding.onBoardingPager.currentItem >= adapter.itemCount - 1

    private fun nextScreen() {

        viewModel.onboardingScreenComplete()
        val uri = getString(R.string.deep_link_login_fragment).toUri()

        val navOptions = NavOptions.Builder()
            .setPopUpTo(
                findNavController().graph.id,
                true
            )
            .setLaunchSingleTop(true)
            .build()
        findNavController().navigate(uri, navOptions)
    }

    private fun applyInsets(windowInsets: WindowInsetsCompat): WindowInsetsCompat {
        val insetTypeMask = systemBarsAndDisplayCutout()

        val insets = windowInsets.getInsets(insetTypeMask)

        binding.root.updatePadding(bottom = insets.bottom)

        return WindowInsetsCompat.Builder()
            .setInsets(insetTypeMask, insets)
            .build()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.onboarding_next,
            R.id.onboarding_register -> next()
        }
    }
}