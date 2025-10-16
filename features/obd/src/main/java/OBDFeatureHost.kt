import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateMargins
import androidx.navigation.fragment.findNavController
import com.telematics.core.common.extension.systemBarsInsets
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.features.obd.databinding.FragmentObdFeatureHostBinding


class OBDFeatureHost : BaseFragment() {

    private lateinit var binding: FragmentObdFeatureHostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObdFeatureHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInset()

        binding.obdBack.setOnClickListener {
            @Suppress("DEPRECATION")
            requireActivity().onBackPressed()
        }

        binding.obdExit.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setInset() {
        binding.toolbar.setOnApplyWindowInsetsListener { view, insets ->
            val systemBarsInsets = systemBarsInsets(insets)
            val params = view.layoutParams as ViewGroup.MarginLayoutParams

            params.updateMargins(
                top = systemBarsInsets.top
            )

            insets
        }
    }
}