package com.example.noteapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteapp.data.Note
import com.example.noteapp.databinding.FragmentNoteBinding
import com.example.noteapp.uistate.AddEditUiState
import com.example.noteapp.viewmodel.AddEditViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteFragment : Fragment() {
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: AddEditViewmodel by viewModels()
    val args: NoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val note = args.note

        if (note != null) {
            binding.addEditText.text = "Edit Note"
            binding.btnSubmit.text = "Update"
            binding.txtTitle.setText(note.title)
            binding.txtDescription.setText(note.description)
            binding.btnDelete.visibility = View.VISIBLE
        } else {
            binding.addEditText.text = "Add Note"
            binding.btnSubmit.text = "Submit"
            binding.btnDelete.visibility = View.GONE
        }

        binding.btnSubmit.setOnClickListener {
            val title = binding.txtTitle.text.toString()
            val desc = binding.txtDescription.text.toString()
            if (note !=null){
                viewmodel.saveNote(
                    note.copy(title = title, description = desc)
                )
            }else

            viewmodel.saveNote(
                Note(
                    id = 0,
                    title = binding.txtTitle.text.toString(),
                    description = binding.txtDescription.text.toString()
                )
            )
        }

        binding.btnDelete.setOnClickListener {
            viewmodel.delete(note!!)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        addObservers()
    }

    private fun addObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewmodel.uiState.collect { state ->
                    when(state) {
                        is AddEditUiState.Saved -> {
                            binding.txtTitle.text.clear()
                            binding.txtDescription.text.clear()
                            findNavController().popBackStack()
                        }
                        is AddEditUiState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is AddEditUiState.Loading ->{
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}