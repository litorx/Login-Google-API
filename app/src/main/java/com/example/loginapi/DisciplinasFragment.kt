package com.example.loginapi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginapi.databinding.FragmentDisciplinasBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DisciplinasFragment : Fragment() {
    private var _binding: FragmentDisciplinasBinding? = null
    private val binding get() = _binding!!
    private val disciplinaAdapter by lazy { DisciplinaAdapter(mutableListOf()) { disciplina -> confirmarExcluirDisciplina(disciplina) } }
    private val database by lazy { FirebaseDatabase.getInstance().getReference("Disciplinas") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisciplinasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadDisciplinas()

        binding.btnAddDisciplina.setOnClickListener {
            showEditDisciplinaDialog(null) // Pass null to indicate a new discipline
        }

        binding.btnSignOut.setOnClickListener {
            findNavController().navigate(R.id.action_disciplinasFragment_to_mainFragment)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewDisciplinas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = disciplinaAdapter
        }
    }

    private fun loadDisciplinas() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val disciplinasList = mutableListOf<Disciplina>()
                for (data in snapshot.children) {
                    val nome = data.child("nome").getValue(String::class.java)
                    val cargaHoraria = data.child("cargaHoraria").getValue(Int::class.java)
                    if (nome != null && cargaHoraria != null) {
                        val disciplina = Disciplina(nome, cargaHoraria)
                        disciplinasList.add(disciplina)
                    }
                }
                disciplinaAdapter.updateData(disciplinasList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DisciplinasFragment", "Error loading disciplines from Firebase: ${error.message}")
            }
        })
    }

    private fun confirmarExcluirDisciplina(disciplina: Disciplina) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Discipline")
            .setMessage("Are you sure you want to delete the discipline ${disciplina.nome}?")
            .setPositiveButton("Yes") { _, _ ->
                apagarDisciplina(disciplina)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun apagarDisciplina(disciplina: Disciplina) {
        val disciplinaRef = database.orderByChild("nome").equalTo(disciplina.nome)
        disciplinaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    childSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DisciplinasFragment", "Error deleting discipline from database: ${error.message}")
            }
        })
    }

    private fun showEditDisciplinaDialog(disciplina: Disciplina?) {
        val dialogView = layoutInflater.inflate(R.layout.disciplina_edit, null)
        val nomeEditText = dialogView.findViewById<TextInputEditText>(R.id.nomeEditText)
        val cargaHorariaEditText = dialogView.findViewById<TextInputEditText>(R.id.cargaHorariaEditText)

        if (disciplina != null) {
            nomeEditText.setText(disciplina.nome)
            cargaHorariaEditText.setText(disciplina.cargaHoraria.toString())
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (disciplina == null) "Add Discipline" else "Edit Discipline")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val nome = nomeEditText.text.toString()
                val cargaHoraria = cargaHorariaEditText.text.toString().toIntOrNull() ?: 0
                if (disciplina == null) {
                    // Add new discipline
                    adicionarDisciplina(Disciplina(nome, cargaHoraria))
                } else {
                    // Update existing discipline
                    updateDisciplina(disciplina, Disciplina(nome, cargaHoraria))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun adicionarDisciplina(disciplina: Disciplina) {
        database.push().setValue(disciplina)
    }

    private fun updateDisciplina(oldDisciplina: Disciplina, newDisciplina: Disciplina) {
        val disciplinaRef = database.orderByChild("nome").equalTo(oldDisciplina.nome)
        disciplinaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    // Update discipline values with new values
                    childSnapshot.ref.child("nome").setValue(newDisciplina.nome)
                    childSnapshot.ref.child("cargaHoraria").setValue(newDisciplina.cargaHoraria)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DisciplinasFragment", "Error updating discipline in database: ${error.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
