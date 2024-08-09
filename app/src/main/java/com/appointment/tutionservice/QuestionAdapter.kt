package com.appointment.tutionservice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.ItemQuestionsBinding

class QuestionAdapter(private val notification: List<QuestionAnswer>) : RecyclerView.Adapter<QuestionAdapter.CardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_questions, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = notification[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return notification.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemQuestionsBinding.bind(itemView)

        fun bind(notificationText: QuestionAnswer) {
            binding.questions.text = "Q. " + notificationText.question
            binding.answer.text = "A. " + notificationText.questionnaire_answer
        }
    }
}