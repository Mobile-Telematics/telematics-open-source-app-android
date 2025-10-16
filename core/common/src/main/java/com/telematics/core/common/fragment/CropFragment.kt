package com.telematics.core.common.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import com.telematics.core.common.Constants
import com.telematics.core.common.databinding.FragmentCropImageBinding
import com.telematics.core.common.extension.getMemoryInfo
import com.telematics.core.common.extension.systemBarsAndDisplayCutout
import com.telematics.core.common.util.PhotoUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.math.ceil

@AndroidEntryPoint
class CropFragment : BaseFragment() {

    companion object {
        const val CROP_KEY = "crop_key"
        const val CROP_FILE_PATH_KEY = "crop_file_path_key"
        const val CROP_BOTTOM_PADDING_ENABLED_KEY = "bottom_padding_enabled_key"
        const val CROP_ASPECT_RATIO_X_KEY = "crop_aspect_ratio_x_key"
        const val CROP_ASPECT_RATIO_Y_KEY = "crop_aspect_ratio_y_key"

        const val CROP_INPUT_FILE_KEY = "input"
        const val CROP_RESULT_FILE_KEY = "result"
    }

    private lateinit var binding: FragmentCropImageBinding

    private var imageFile: String = ""
    private var resultFile: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCropImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageFile = arguments?.getString(CROP_INPUT_FILE_KEY) ?: ""
        resultFile = arguments?.getString(CROP_RESULT_FILE_KEY) ?: ""

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            applyInsets(insets)
        }
        setListeners()
        init()
    }

    private fun setListeners() {

        setBackPressedCallback()

        binding.toolbar.setNavigationOnClickListener { finish(null) }
        binding.cropReady.setOnClickListener {
            val bitmap = try {
                binding.cropImageView.getCroppedImage()
            } catch (e: Exception) {
                null
            }
            save(bitmap)
        }
        binding.cropRotateLeft.setOnClickListener { rotateLeft() }
        binding.cropRotateRight.setOnClickListener { rotateRight() }
    }

    private fun init() {
        arguments?.apply {
            val aspectRatioX = getInt(CROP_ASPECT_RATIO_X_KEY, 1)
            val aspectRatioY = getInt(CROP_ASPECT_RATIO_Y_KEY, 1)

            setAspectRatio(aspectRatioX, aspectRatioY)
        }

        val file = File(imageFile)
        val isImageExists = file.exists()
        if (!isImageExists) {
            finish(null)
            return
        }

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeFile(imageFile, options)

        val imageHeight: Int = options.outHeight
        val imageWidth: Int = options.outWidth

        val maxImageDimension = imageHeight.coerceAtLeast(imageWidth)
        val scaleByMaxTextureSize =
            ceil(maxImageDimension.toDouble() / Constants.maxTextureSize.toDouble()).toInt()
        val scaleByMaxImageSIze =
            ceil(maxImageDimension.toDouble() / Constants.IMAGE_MAX_BITMAP_DIMENSION.toDouble()).toInt()

        val totalMemory = Runtime.getRuntime().totalMemory()
        val threshold = getMemoryInfo(requireContext()).threshold

        val availableMemory = threshold - totalMemory

        val imageSize = imageHeight * imageWidth * 4

        val scaleByMemory = ceil(imageSize.toDouble() / availableMemory.toDouble()).toInt()

        val scale =
            scaleByMaxTextureSize.coerceAtLeast(scaleByMemory).coerceAtLeast(scaleByMaxImageSIze)

        options.apply {
            inJustDecodeBounds = false
            inSampleSize = scale
        }

        val scaledBitMap = BitmapFactory.decodeFile(imageFile, options)

        setImageBitmap(scaledBitMap)
    }

    private fun setAspectRatio(aspectRatioX: Int, aspectRatioY: Int) {
        binding.cropImageView.apply {
            setAspectRatio(aspectRatioX, aspectRatioY)
            setFixedAspectRatio(true)
        }
    }

    private fun setImageBitmap(bitmap: Bitmap) {
        binding.cropImageView.setImageBitmap(bitmap)
    }

    private fun rotateRight() {
        binding.cropImageView.rotateImage(90)
    }

    private fun rotateLeft() {
        binding.cropImageView.rotateImage(-90)
    }

    private fun save(bitmap: Bitmap?) {
        if (bitmap != null) {
            val file = File(resultFile)
            PhotoUtils.saveToFile(bitmap, file, Bitmap.CompressFormat.JPEG)
            finish(resultFile)
        }
    }

    private fun finish(filePath: String?) {

        val bundle = bundleOf(CROP_FILE_PATH_KEY to filePath)
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            CROP_KEY,
            bundle
        )
        findNavController().popBackStack()
    }

    private fun applyInsets(windowInsets: WindowInsetsCompat): WindowInsetsCompat {
        val insetTypeMask = systemBarsAndDisplayCutout()

        val insets = windowInsets.getInsets(insetTypeMask)

        val bottomPaddingEnabled = arguments?.getBoolean(CROP_BOTTOM_PADDING_ENABLED_KEY) ?: false

        binding.root.updatePadding(top = insets.top)

        if (bottomPaddingEnabled) {
            binding.root.updatePadding(bottom = insets.bottom)
        }


        return WindowInsetsCompat.Builder()
            .setInsets(insetTypeMask, insets)
            .build()
    }
}