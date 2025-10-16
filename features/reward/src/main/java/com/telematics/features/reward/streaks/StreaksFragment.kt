package com.telematics.features.reward.streaks

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.model.reward.Streak
import com.telematics.features.reward.databinding.FragmentStreaksBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StreaksFragment : BaseFragment() {

    private val streaksViewModel: StreaksViewModel by viewModels()

    private lateinit var binding: FragmentStreaksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStreaksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        init()
    }

    private fun setListeners() {

        val swipeRefreshLayout = binding.streaksSwipeRefresh
        swipeRefreshLayout.setOnRefreshListener {

            Handler(Looper.getMainLooper()).postDelayed({
                swipeRefreshLayout.isRefreshing = false
            }, 1000)

            init()
        }
    }

    private fun init() {

        binding.root.apply {
            alpha = 0f
            animate().setDuration(400).alpha(1f).start()
        }

        binding.streaksRecycler.layoutManager = LinearLayoutManager(requireContext())

        observeStreaksData()
    }

    private fun observeStreaksData() {

        streaksViewModel.getStreaksData().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                showData(it)
            }
        }
    }

    private fun showData(list: List<Streak>) {

        binding.streaksRecycler.adapter = StreaksRecyclerAdapter(list)
    }
}