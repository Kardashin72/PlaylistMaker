package com.practicum.playlistmaker.medialibrary.presentation.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.presentation.utils.dpToPx
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.medialibrary.presentation.viewmodel.CreatePlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment() {
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreatePlaylistViewModel by viewModel()
    private val args by navArgs<CreatePlaylistFragmentArgs>()

    private var pickedImageUri: Uri? = null
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            pickedImageUri = uri
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .transform(RoundedCorners(binding.playlistCoverImage.context.dpToPx(8)))
                .into(binding.playlistCoverImage)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupNameTextWatcher()
        setupSystemBackHandler()
        setupSystemBackHandler()
        observeViewModel()
        val editId = args.editPlaylistId
        if (editId > 0) {
            viewModel.loadForEdit(editId)
        }
    }

    private fun setupClickListeners() {
        binding.createPlaylistToolbar.setNavigationOnClickListener {
            handleBackPressed()
        }

        binding.playlistCoverImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createPlaylistButton.setOnClickListener {
            val name = binding.playlistName.text?.toString()?.trim().orEmpty()
            val description = binding.playlistDescription.text?.toString()?.trim().orEmpty()
            val storedPath = pickedImageUri?.let { saveImageToPrivateStorage(it) }

            val mode = viewModel.state.value?.mode
            if (mode == CreatePlaylistViewModel.Mode.EDIT) {
                viewModel.saveEdited(name, description, storedPath)
            } else {
                viewModel.createPlaylist(name, description, storedPath)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.event.observe(viewLifecycleOwner) { event ->
            event?.let {
                Toast.makeText(requireContext(), getString(it.messageResId, it.playlistName), Toast.LENGTH_SHORT).show()
                if (it.shouldClose) findNavController().popBackStack()
                viewModel.onEventHandled()
            }
        }
        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.mode == CreatePlaylistViewModel.Mode.EDIT && state.editing != null) {
                binding.createPlaylistToolbar.title = getString(R.string.edit_playlist)
                binding.createPlaylistButton.text = getString(R.string.save)

                val p = state.editing
                binding.playlistName.setText(p.name)
                binding.playlistDescription.setText(p.description)
                binding.createPlaylistButton.isEnabled = p.name.isNotBlank()

                val cover = p.coverImagePath
                if (!cover.isNullOrBlank()) {
                    Glide.with(this)
                        .load(cover)
                        .centerCrop()
                        .transform(RoundedCorners(binding.playlistCoverImage.context.dpToPx(8)))
                        .into(binding.playlistCoverImage)
                } else {
                    binding.playlistCoverImage.setImageResource(R.drawable.playlist_cover_placeholder)
                }
            } else {
                binding.createPlaylistToolbar.title = getString(R.string.new_playlist)
                binding.createPlaylistButton.text = getString(R.string.create_playlist)
            }
        }
    }

    private fun setupSystemBackHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }
            }
        )
    }

    private fun handleBackPressed() {
        val isEdit = viewModel.state.value?.mode == CreatePlaylistViewModel.Mode.EDIT
        if (isEdit) {
            findNavController().popBackStack()
            return
        }
        if (hasUnsavedChanges()) {
            showDiscardDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        val nameNotBlank = binding.playlistName.text?.toString()?.isNotBlank() == true
        val descriptionNotBlank = binding.playlistDescription.text?.toString()?.isNotBlank() == true
        val imagePicked = pickedImageUri != null
        return nameNotBlank || descriptionNotBlank || imagePicked
    }

    private fun showDiscardDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AppAlertDialogTheme)
            .setTitle(R.string.playlist_creation_exit_dialog_title)
            .setMessage(R.string.playlist_creation_exit_dialog_message)
            .setNegativeButton(R.string.playlist_creation_exit_dialog_cancel, null)
            .setPositiveButton(R.string.playlist_creation_exit_dialog_confirm) { _, _ ->
                findNavController().popBackStack()
            }
            .show()
    }

    private fun setupNameTextWatcher() {
        binding.playlistName.doOnTextChanged { text, _, _, _ ->
            binding.createPlaylistButton.isEnabled = text?.isNotBlank() == true
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String? {
        return try {
            val coversDir = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "covers")
            if (!coversDir.exists()) coversDir.mkdirs()

            val fileName = "cover_${System.currentTimeMillis()}.jpg"
            val outFile = File(coversDir, fileName)

            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val bitmap = inputStream.use { BitmapFactory.decodeStream(it) } ?: return null

            FileOutputStream(outFile).use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, output)
                output.flush()
            }
            bitmap.recycle()
            outFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}


