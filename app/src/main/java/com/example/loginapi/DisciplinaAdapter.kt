package com.example.loginapi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DisciplinaAdapter(
    private val disciplinas: MutableList<Disciplina>,
    private val onDeleteClickListener: (Disciplina) -> Unit
) : RecyclerView.Adapter<DisciplinaAdapter.DisciplinaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisciplinaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_disciplinas, parent, false)
        return DisciplinaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DisciplinaViewHolder, position: Int) {
        holder.bind(disciplinas[position])
    }

    override fun getItemCount(): Int {
        return disciplinas.size
    }

    fun updateData(newDisciplinas: List<Disciplina>) {
        disciplinas.clear()
        disciplinas.addAll(newDisciplinas)
        notifyDataSetChanged()
    }

    inner class DisciplinaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nomeTextView: TextView = itemView.findViewById(R.id.text_disciplina_nome)
        private val cargaHorariaTextView: TextView = itemView.findViewById(R.id.text_carga_horaria)
        private val btnApagarDisciplina: ImageButton = itemView.findViewById(R.id.btn_apagar_disciplina)

        fun bind(disciplina: Disciplina) {
            nomeTextView.text = disciplina.nome
            cargaHorariaTextView.text = "${disciplina.cargaHoraria}h"

            btnApagarDisciplina.setOnClickListener {
                onDeleteClickListener.invoke(disciplina)
            }

            itemView.setOnClickListener {
                // Implement click action on item if necessary
            }
        }
    }
}
