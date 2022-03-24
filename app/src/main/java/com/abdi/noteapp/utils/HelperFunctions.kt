package com.abdi.noteapp.utils

import android.content.Context
import android.view.View
import android.widget.AdapterView
import com.abdi.noteapp.R
import com.google.android.material.card.MaterialCardView

object HelperFunctions {

    fun spinnerListener(
        context: Context?,
        priorityIndicator: MaterialCardView
    ): AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                context?.let {
                    when (position) {
                        0 -> {
                            //Untuk menentukan warna
                            val pink = context.getColor(R.color.pink)
                            priorityIndicator.setCardBackgroundColor(pink)
                        }
                        1 -> {
                            val yellow = context.getColor(R.color.yellow)
                            priorityIndicator.setCardBackgroundColor(yellow)
                        }
                        2 -> {
                            val green = context.getColor(R.color.green)
                            priorityIndicator.setCardBackgroundColor(green)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
}