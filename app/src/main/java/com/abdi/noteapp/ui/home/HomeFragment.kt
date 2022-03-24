package com.abdi.noteapp.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.abdi.noteapp.R
import com.abdi.noteapp.data.entity.Notes
import com.abdi.noteapp.databinding.FragmentHomeBinding
import com.abdi.noteapp.ui.NotesViewModel
import com.abdi.noteapp.utils.Extension.setActionBar

class HomeFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding as FragmentHomeBinding

    private val homeViewModel by viewModels<NotesViewModel>()

    private val homeAdapter by lazy { HomeAdapter() }

    private var _currentData: List<Notes>? = null
    private val currentData get() = _currentData as List<Notes>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.apply {
            toolbarHome.setActionBar(requireActivity())

            fabAdd.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_addFragment)
            }

            btnTest.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_detailFragment)
            }
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvHome.apply {
            homeViewModel.getAllData().observe(viewLifecycleOwner) {
                checkIsDataEmpty(it)
                homeAdapter.setData(it)
                _currentData = it
            }
            adapter = homeAdapter
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

            swipeToDelete(this)
        }
    }

    private fun checkIsDataEmpty(data: List<Notes>?) {
        binding.apply {
            if (data?.isEmpty() == true) {
                imgNoNotes.visibility = View.VISIBLE
                rvHome.visibility = View.INVISIBLE
            } else {
                imgNoNotes.visibility = View.INVISIBLE
                rvHome.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchAction = search.actionView as? SearchView

        searchAction?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_priohigh -> homeViewModel.sortByHighPriority().observe(this) {
                homeAdapter.setData(it)
            }
            R.id.menu_priolow -> homeViewModel.sortByLowPriority().observe(this) {
                homeAdapter.setData(it)
            }

            R.id.menu_delete_all -> confrimDeleteAll()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confrimDeleteAll() {
        if (currentData.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Hmm...")
                .setMessage("No Notes Here!!")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete all notes")
                .setMessage("Are you sure want to delete all notes?")
                .setPositiveButton("yes") { _, _ ->
                    homeViewModel.deleteAllData()
                    Toast.makeText(context, "Succes delete all notes!!", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("no") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        val querySearch = "%$query%"

        query?.let {
            homeViewModel.searchByQuery(querySearch).observe(this) {
                homeAdapter.setData(it)
            }
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val querySearch = "%$newText%"
        newText?.let {
            homeViewModel.searchByQuery(querySearch).observe(this) {
                homeAdapter.setData(it)
            }
        }
        return true
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDelete = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = homeAdapter.listNotes[viewHolder.adapterPosition]
                homeViewModel.deleteNote(deletedItem)

                Toast.makeText(context,"Note was deleted..", Toast.LENGTH_SHORT).show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDelete)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
