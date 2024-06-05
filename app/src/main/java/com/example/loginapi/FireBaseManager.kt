package com.example.loginapi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FireBaseManager {
    private val database = FirebaseDatabase.getInstance()
    private val disciplinasRef = database.reference.child("Disciplinas")
    private val auth = FirebaseAuth.getInstance()

    fun adicionarDisciplina(disciplina: Disciplina) {
        val disciplinaId = disciplinasRef.push().key
        disciplinaId?.let {
            disciplinasRef.child(it).setValue(disciplina)
        }
    }

    fun editarDisciplina(disciplinaId: String, disciplina: Disciplina) {
        disciplinasRef.child(disciplinaId).setValue(disciplina)
    }

    fun excluirDisciplina(disciplinaId: String) {
        disciplinasRef.child(disciplinaId).removeValue()
    }

    fun listarDisciplinas(listener: ValueEventListener) {
        disciplinasRef.addValueEventListener(listener)
    }

    fun getUsuarioAtualId(): String? {
        return auth.currentUser?.uid
    }
}
