package com.example.noteapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteapp.adapter.NoteAdapter
import com.example.noteapp.databinding.FragmentMainBinding
import com.example.noteapp.uistate.NoteUiState
import com.example.noteapp.viewmodel.NotesListViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {
  private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    val viewmodel : NotesListViewmodel by viewModels()
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addNote.setOnClickListener {

            val action = MainFragmentDirections.actionMainFragmentToNoteFragment(null)
            findNavController().navigate(action)
        }

       binding.searchEditText.addTextChangedListener { text->
           val query = text.toString()
           viewmodel.searchNotes(query)

       }
        setUpRecyclerview()


        addObservers()
    }

    private fun addObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewmodel.uiState.collect { state->
                    when(state){
                        is NoteUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.textError.visibility = View.GONE
                        }
                        is NoteUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.textError.visibility = View.GONE
                            noteAdapter.differ.submitList(state.notes)}
                        is NoteUiState.Empty-> {
                            binding.progressBar.visibility = View.GONE
                            binding.textError.visibility = View.VISIBLE
                            binding.textError.text = "No notes available"
                        }
                        is NoteUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.textError.visibility = View.VISIBLE
                            binding.textError.text = state.message
                            noteAdapter.differ.submitList(emptyList())
                        }

                    }
                }
            }
        }
    }

    private fun setUpRecyclerview() {
        noteAdapter = NoteAdapter()
        noteAdapter.setOnItemClickListener { note ->
            val action = MainFragmentDirections.actionMainFragmentToNoteFragment(note)
            findNavController().navigate(action)
        }
        binding.noteList.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}